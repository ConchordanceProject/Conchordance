package com.conchordance.fretted;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.fretted.fingering.validation.BaseValidator;
import com.conchordance.fretted.fingering.validation.ChordFingeringValidator;
import com.conchordance.music.Chord;
import com.conchordance.music.Interval;
import com.conchordance.music.IntervalicNote;
import com.conchordance.music.Note;


public class RecursionBasedFretboardModel extends FretboardModel {

	public void setChord(Chord c) {
		chord = c;
		
		// Changing the chord means the included notes change
		updateInChord();
	}

	public void setMinFret(int f) {
		minFret = f;
	}
	
	public int getMinFret() {
		return minFret;
	}

	public void setMaxFret(int f) {
		maxFret = f;
		calculateFingerings();
	}

	public int getMaxFret() {
		return maxFret;
	}

	public void addCapo(Capo capo) {
		capos.add(capo);
		evaluateCapos();
	}
	
	public void addCapo() {
		// Find the lowest fret that does not have a capo already
		frets: for (int fret = 1; fret < instrument.frets; ++fret) {
			for (Capo otherCapo : capos) {
				if (otherCapo.getFret() == fret)
					continue frets;
			}
			capos.add(new Capo(fret, 0, instrument.strings-1));
			break;
		}
		evaluateCapos();
	}
	
	public void removeCapo(Capo c) {
		capos.remove(c);
		evaluateCapos();
	}

	public List<Capo> getCapos() {
		return capos;
	}

	public void setInstrument(Instrument i) {
		instrument = i;
		
		// Reset the capo and min/max settings since the instrument changed.
		capos = new LinkedList<>();
		evaluateCapos();
		maxFret = i.frets;
		minFret = 1;

		updateInChord();
	}

	public void capoMoved() {
		evaluateCapos();
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public IntervalicNote getChordNoteAt(int string, int fret) {
		return inChord[string][fret];
	}

	public boolean hasChordNoteAt(int string, int fret) {
		return fret >= instrument.fretNutPositions[string] && inChord[string][fret] != null;
	}

	public boolean isInRange(int string, int fret) {
		return isAtFretNut(string, fret) || fret >= instrument.fretNutPositions[string] && fret <= maxFret && fret >= minFret;
	}

	public boolean isOnCapo(int string, int fret) {
		for (Capo capo : capos) {
			if (fret == capo.getFret() && string >= capo.getLowestString() && string <= capo.getHighestString())
				return true;
		}
	
		return false;
	}
	
	public boolean isAtFretNut(int string, int fret) {
		return fret == instrument.fretNutPositions[string];
	}

    public List<ChordFingering> calculateFingerings() {
        // Find all points on the neck that *could* be included in a chord
        ArrayList<StringFret> fingerOptions = new ArrayList<>();
        for (int s = 0; s<instrument.strings; ++s) {
            for (int f = 1; f<=instrument.frets; ++f) {
                if (hasChordNoteAt(s, f) && isInRange(s, f) && f > highestCapoedFrets[s])
                    fingerOptions.add(new StringFret(s, f));
            }
        }

        return calculateFingerings(fingerOptions);
    }

    public List<ChordFingering> calculateFingerings(int[] frets) {
        ArrayList<StringFret> fingerOptions = new ArrayList<>();
        for (int string = 0; string<frets.length; ++string) {
            if (frets[string] > 0)
                fingerOptions.add(new StringFret(string, frets[string]));
        }

        return calculateFingerings(fingerOptions);
    }

    public RecursionBasedFretboardModel() {
        this(Chord.A_MAJOR);
    }

    public RecursionBasedFretboardModel(Chord chord) {
		super();
		this.chord = chord;

		instrument = Instrument.GUITAR;
		primaryValidator = new BaseValidator();
		minFret = 1;
		maxFret = instrument.frets;
		capos = new LinkedList<>();
		
		evaluateCapos();
		updateInChord();
	}

    private List<ChordFingering> calculateFingerings(ArrayList<StringFret> fingerOptions) {
        ArrayList<ChordFingering> chordFingerings = new ArrayList<>();
        ChordFingering base = ChordFingering.trivialChordFingering(chord, instrument.strings);

        // Find the open strings which could be included in a chord.
        ArrayList<Integer> openStringList = new ArrayList<>();
        for (int s = 0; s<instrument.strings; ++s) {
            if (hasChordNoteAt(s, highestCapoedFrets[s]))
                openStringList.add(s);
        }

        // Find all open chords (only open strings are played)
        openStringPatterns = openStringCombinations(openStringList);
        for (boolean[] pattern : openStringPatterns) {
            int[] absoluteFrets = new int[instrument.strings];
            int[] capoRelativeFrets = new int[instrument.strings];
            int[] fingers = new int[instrument.strings];
            IntervalicNote[] notes = new IntervalicNote[pattern.length];
            for (int string = 0; string < instrument.strings; ++string) {
                if (pattern[string]) {
                    // Include the open string in this chord
                    absoluteFrets[string] = highestCapoedFrets[string];
                    capoRelativeFrets[string] = 0;
                    notes[string] = inChord[string][highestCapoedFrets[string]];
                } else {
                    // The string is not used in this chord
                    absoluteFrets[string] = -1;
                    capoRelativeFrets[string] = -1;
                }
            }
            ChordFingering open = new ChordFingering(chord, absoluteFrets, capoRelativeFrets, fingers, notes, false);
            if (primaryValidator.validate(open, chord))
                chordFingerings.add(open);
        }

        // For each legal position, assign finger one to that note and derive new chords from that shape.
        for (StringFret fingerPos : fingerOptions) {
            int finger = 1;
            IntervalicNote note = getChordNoteAt(fingerPos.string, fingerPos.fret);
            ChordFingering fingering = base.clone(fingerPos.string, fingerPos.fret, fingerPos.fret, finger, note);
            ArrayList<StringFret> nextFingerOptions = removeIllegal(fingerOptions, fingerPos, finger);
            ArrayList<ChordFingering> fingerOneChords = new ArrayList<>();
            rCalcFingerings(fingering, finger+1, nextFingerOptions, fingerOneChords);
            chordFingerings.addAll(fingerOneChords);

            // Try to create barred chords
            int barreFret = fingerPos.fret;
            toBarre: for (ChordFingering toBarre : fingerOneChords) {
                boolean canBarre = false;
                for (int s = fingerPos.string-1; s >=0; --s) {
                    if (toBarre.absoluteFrets[s] == barreFret || toBarre.capoRelativeFrets[s] == 0) {
                        canBarre = false;
                        continue toBarre;
                    }
                    if (toBarre.absoluteFrets[s] == -1 && hasChordNoteAt(s, barreFret))
                        canBarre = true;
                }
                if (canBarre) {
                    ChordFingering tempBarre = toBarre.clone();
                    for (int s = fingerPos.string-1; s >=0; --s) {
                        if (toBarre.absoluteFrets[s] == -1 && hasChordNoteAt(s, barreFret)) {
                            tempBarre.absoluteFrets[s] = barreFret;
                            tempBarre.capoRelativeFrets[s] = barreFret;
                            tempBarre.fingers[s] = 1;
                            tempBarre.notes[s] = inChord[s][barreFret];
                        }
                    }
                    ChordFingering barred = new ChordFingering(chord, tempBarre.absoluteFrets, tempBarre.capoRelativeFrets, tempBarre.fingers, tempBarre.notes, true);
                    if (primaryValidator.validate(barred, chord))
                        chordFingerings.add(barred);
                }
            }

            for (finger = 2; finger<5; ++finger) {
                IntervalicNote newNote = getChordNoteAt(fingerPos.string, fingerPos.fret);
                ChordFingering newChord = base.clone(fingerPos.string, fingerPos.fret, fingerPos.fret, finger, newNote);

                nextFingerOptions = removeIllegal(fingerOptions, fingerPos, finger);
                rCalcFingerings(newChord, finger+1, nextFingerOptions, chordFingerings);
            }
        }

        return chordFingerings;
    }

    private void updateInChord() {
		inChord = new IntervalicNote[instrument.strings][instrument.frets+1];
		for (int string = 0; string<instrument.strings; ++string) {
            // Iterate up this string starting at the nut (for this string)
			for (int fret = instrument.fretNutPositions[string]; fret<=instrument.frets; ++fret) {
                // Search the notes of the chord for a matching pitch
				for (int i = 0; i<chord.notes.length; ++i) {
					Note chordNote = chord.notes[i];
                    if (chordNote.halfSteps % 12 == note(string,fret) % 12) {
                        // TODO in rare cases a B# would appear to be a C, or a Cb would appear to be a B# and thus
                        // the octave is incorrect. The note name should be taken into account to get the correct octave
                        int octave = note(string,fret) / 12;
                        Interval interval = chord.intervals[i];
						inChord[string][fret] = new IntervalicNote(interval, new Note(chordNote.noteName, chordNote.modifier, octave));
                    }
				}
			}
		}
	}

	/**
	 * The int-value of the note at a position on the fretboard
	 */
	private int note(int string, int fret) {
        // Subtracting the fret nut position does nothing in the normal case, but for banjo-like instruments
        // it moves the open note value up to the correct fret (fifth fret for example)
		return fret + instrument.tuning[string].halfSteps - instrument.fretNutPositions[string];
	}
	
	/**
	 * Removes all StringFret positions that cannot physically be fingered
	 * 
	 * With the addition of latestFinger in a chord shape, the legal positions
	 * can be narrowed down based on how the hand can stretch.
	 */
	private ArrayList<StringFret> removeIllegal(ArrayList<StringFret> options, StringFret latestFinger, int finger) {
		ArrayList<StringFret> toreturn = new ArrayList<>();
		for (StringFret current : options) {
			if (current.fret < latestFinger.fret)
				continue;
			
			// Cannot stretch more than two frets
			if (current.fret-latestFinger.fret > 2)
				continue;
			
			// Cannot put a finger on the same string twice
			if (current.string == latestFinger.string)
				continue;
			
			// Only finger 4 can reach the same fret on a higher-numbered string
			if (finger < 4 && current.string > latestFinger.string && current.fret == latestFinger.fret)
				continue;
			toreturn.add(current);
		}
		return toreturn;
	}

	private void rCalcFingerings(ChordFingering current, int firstAvailableFinger, ArrayList<StringFret> options, ArrayList<ChordFingering> chordFingerings) {
		if (primaryValidator.validate(current, chord))
			chordFingerings.add(current);

		// Derive chords that include open strings
		for (boolean[] pattern : openStringPatterns) {
			boolean perfectMatch = true;
			for (int string = 0; string<instrument.strings; ++string) {
				if (pattern[string] && current.capoRelativeFrets[string] != -1)
					perfectMatch = false;
			}

			// These open strings should be "added" to form a new chord only if
			// none of the strings are being used
			if (!perfectMatch)
				continue;

			int[] absoluteFrets = current.absoluteFrets.clone();
			int[] capoRelativeFrets = current.capoRelativeFrets.clone();
			int[] fingers = current.fingers.clone();
			IntervalicNote[] notes = current.notes.clone();
			for (int string = 0; string < instrument.strings; ++string) {
				if (pattern[string]) {
					absoluteFrets[string] = highestCapoedFrets[string];
					capoRelativeFrets[string] = 0;
					notes[string] = inChord[string][highestCapoedFrets[string]];
				}
			}
			ChordFingering open = new ChordFingering(chord, absoluteFrets, capoRelativeFrets, fingers, notes, false);
			if (primaryValidator.validate(open, chord))
				chordFingerings.add(open);
		}

		// Derive new chord fingerings by placing new fingers
		if (firstAvailableFinger <= 4) {
			// Explore each available position with each available finger
			for (StringFret newPlacement : options) {
				for (int finger = firstAvailableFinger; finger<5; ++finger) {
					IntervalicNote newNote = getChordNoteAt(newPlacement.string, newPlacement.fret);
					ChordFingering newChord = current.clone(newPlacement.string, newPlacement.fret, newPlacement.fret, finger, newNote);
					
					rCalcFingerings(newChord, finger+1, removeIllegal(options, newPlacement, finger), chordFingerings);
				}
			}
		}
	}

	private void evaluateCapos() {
		highestCapoedFrets = instrument.fretNutPositions.clone();
		for (Capo capo : capos) {
			int fret = capo.getFret();
			for (int s = capo.getLowestString(); s <= capo.getHighestString(); ++s)
				highestCapoedFrets[s] = Math.max(fret, highestCapoedFrets[s]);
		}
	}

	private class StringFret {
		public final int string;
		public final int fret;
		public StringFret(int string, int fret) {
			this.string = string;
			this.fret = fret;
		}
	}

	private Chord chord;
	private Instrument instrument;
	private boolean[][] openStringPatterns;
	private ChordFingeringValidator primaryValidator;
	private int minFret;
	private int maxFret;
	private IntervalicNote[][] inChord;
	private int[] highestCapoedFrets;
	private LinkedList<Capo> capos;
}
