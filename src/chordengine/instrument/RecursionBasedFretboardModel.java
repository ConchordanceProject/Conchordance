package chordengine.instrument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import chordengine.fingers.ChordFingering;
import chordengine.fingers.list.ChordListModel;
import chordengine.fingers.validation.BaseValidator;
import chordengine.fingers.validation.ChordFingeringValidator;
import chordengine.music.Chord;
import chordengine.music.IntervalicNote;
import chordengine.music.Note;

public class RecursionBasedFretboardModel extends FretboardModel {
	public ChordListModel getChordList() {
		return chords;
	}

	public void setChord(Chord c) {
		chord = c;
		
		// Changing the chord means the included notes change
		updateInChord();
		
		// Find all fingerings for the new chord
		calculateChords();
	}

	public void setMinFret(int f) {
		minFret = f;
		calculateChords();
	}
	
	public int getMinFret() {
		return minFret;
	}

	public void setMaxFret(int f) {
		maxFret = f;
		calculateChords();
	}

	public int getMaxFret() {
		return maxFret;
	}

	public void addCapo(Capo capo) {
		capos.add(capo);
		evaluateCapos();
		calculateChords();
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
		calculateChords();
	}
	
	public void removeCapo(Capo c) {
		capos.remove(c);
		evaluateCapos();
		calculateChords();
	}

	public List<Capo> getCapos() {
		return capos;
	}

	public void setInstrument(Instrument i) {
		instrument = i;
		
		// Reset the capo and min/max settings since the instrument changed.
		capos = new LinkedList<Capo>();
		evaluateCapos();
		maxFret = i.frets;
		minFret = 1;

		// Temporarily clear out any notes on the fretboard, so that dependants can receive instrument change before chords are caluclated.
		inChord = new IntervalicNote[instrument.strings][instrument.frets+1];
		
		updateInChord();
		calculateChords();
	}

	public void capoMoved() {
		evaluateCapos();
		calculateChords();
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public IntervalicNote getNoteAt(int string, int fret) {
		return inChord[string][fret];
	}

	public boolean hasNoteAt(int string, int fret) {
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

	public RecursionBasedFretboardModel(Chord chord) {
		super();
		chords = new ChordListModel();
		instrument = Instrument.GUITAR;
		this.chord = chord;
		primaryValidator = new BaseValidator();
		minFret = 1;
		maxFret = instrument.frets;
		capos = new LinkedList<Capo>();
		evaluateCapos();
		updateInChord();
		calculateChords();
	}
	
	private void updateInChord() {
		inChord = new IntervalicNote[instrument.strings][instrument.frets+1];
		for (int s = 0; s<instrument.strings; ++s) {
			for (int f = instrument.fretNutPositions[s]; f<=instrument.frets; ++f) {
				for (int i = 0; i<chord.notes.length; ++i) {
					Note n = chord.notes[i];
					if (note(s, f)%12 == n.halfSteps%12)
						inChord[s][f] = new IntervalicNote(chord.intervals[i], new Note(n.noteName, n.modifier, (instrument.tuning[s].halfSteps+f-n.modifier)/12));
				}
			}
		}
	}

	/**
	 * The int-value of the note at a position on the fretboard
	 */
	private int note(int string, int fret) {
		return fret + instrument.tuning[string].halfSteps;
	}

	private void calculateChords() {
		// Find all points on the neck that *could* be included in a chord
		ArrayList<StringFret> fingerOptions = new ArrayList<StringFret>();
		for (int s = 0; s<instrument.strings; ++s) {
			for (int f = 0; f<=instrument.frets; ++f) {
				if (hasNoteAt(s, f) && isInRange(s, f) && f > highestCapoedFrets[s])
					fingerOptions.add(new StringFret(s, f));
			}
		}

		ArrayList<ChordFingering> chordFingerings = new ArrayList<ChordFingering>();
		ChordFingering base = ChordFingering.trivialChordFingering(instrument.strings);

		// Find the open strings which could be included in a chord.
		ArrayList<Integer> openStringList = new ArrayList<Integer>();
		for (int s = 0; s<instrument.tuning.length; ++s) {
			if (hasNoteAt(s, highestCapoedFrets[s]))
				openStringList.add(s);
		}
		openStringPatterns = openStringCombinations(instrument.strings, openStringList);

		// Try to make wholly-open chords
		for (boolean[] pattern : openStringPatterns) {
			int[] openAbsoluteFrets = Arrays.copyOf(base.absoluteFrets, pattern.length);
			int[] openCapoRelativeFrets = Arrays.copyOf(base.capoRelativeFrets, pattern.length);
			int[] openFingers = new int[pattern.length];
			IntervalicNote[] openNotes = new IntervalicNote[pattern.length];
			for (int s = 0; s < pattern.length; ++s) {
				if (pattern[s]) {
					openAbsoluteFrets[s] = highestCapoedFrets[s];
					openCapoRelativeFrets[s] = 0;
					openNotes[s] = inChord[s][highestCapoedFrets[s]];
				}
			}
			ChordFingering open = new ChordFingering(chord, openAbsoluteFrets, openCapoRelativeFrets, openFingers, openNotes, false);
			if (primaryValidator.validate(open, chord))
				chordFingerings.add(open);
		}
		
		ArrayList<ChordFingering> rejects = new ArrayList<ChordFingering>();

		// For each legal position, assign finger one to that note and derive new chords from that shape.
		for (StringFret fingerPos : fingerOptions) {
			int finger = 1;
			int[] fingers = new int[instrument.strings];
			fingers[fingerPos.string] = finger;
			IntervalicNote[] notes = new IntervalicNote[instrument.strings];
			notes[fingerPos.string] = inChord[fingerPos.string][fingerPos.fret];
			int[] absoluteFrets = base.absoluteFrets.clone();
			absoluteFrets[fingerPos.string] = fingerPos.fret;
			int[] capoRelativeFrets = base.capoRelativeFrets.clone();
			capoRelativeFrets[fingerPos.string] = fingerPos.fret;
			ArrayList<StringFret> nextFingerOptions = removeIllegal(fingerOptions, fingerPos, finger);
			ArrayList<ChordFingering> fingerOneChords = new ArrayList<ChordFingering>();
			ArrayList<ChordFingering> fingerOneRejects = new ArrayList<ChordFingering>();
			rCalcFingerings(new ChordFingering(chord, absoluteFrets, capoRelativeFrets, fingers, notes, false), finger+1, nextFingerOptions, fingerOneChords, fingerOneRejects);
			chordFingerings.addAll(fingerOneChords);
			
			// Try to create barred chords
			fingerOneChords.addAll(fingerOneRejects);
			int barreFret = fingerPos.fret;
			toBarre: for (ChordFingering toBarre : fingerOneChords) {
				boolean canBarre = false;
				for (int s = fingerPos.string-1; s >=0; --s) {
					if (toBarre.absoluteFrets[s] == barreFret || toBarre.capoRelativeFrets[s] == 0) {
						canBarre = false;
						continue toBarre;
					}
					if (toBarre.absoluteFrets[s] == -1 && hasNoteAt(s, barreFret))
						canBarre = true;
				}
				if (canBarre) {
					ChordFingering tempBarre = toBarre.clone();
					for (int s = fingerPos.string-1; s >=0; --s) {
						if (toBarre.absoluteFrets[s] == -1 && hasNoteAt(s, barreFret)) {
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
				fingers = new int[instrument.strings];
				fingers[fingerPos.string] = finger;
				notes = new IntervalicNote[instrument.strings];
				notes[fingerPos.string] = inChord[fingerPos.string][fingerPos.fret];
				absoluteFrets = base.absoluteFrets.clone();
				absoluteFrets[fingerPos.string] = fingerPos.fret;
				capoRelativeFrets = base.capoRelativeFrets.clone();
				capoRelativeFrets[fingerPos.string] = fingerPos.fret;
				nextFingerOptions = removeIllegal(fingerOptions, fingerPos, finger);
				rCalcFingerings(new ChordFingering(chord, absoluteFrets, capoRelativeFrets, fingers, notes, false), finger+1, nextFingerOptions, chordFingerings, rejects);
			}
		}

		chords.setChords(chordFingerings.toArray(new ChordFingering[chordFingerings.size()]));
	}
	
	/**
	 * Removes all StringFret positions that cannot physically be fingered
	 * 
	 * With the addition of latestFinger in a chord shape, the legal positions
	 * can be narrowed down based on how the hand can stretch.
	 */
	private ArrayList<StringFret> removeIllegal(ArrayList<StringFret> options, StringFret latestFinger, int finger) {
		ArrayList<StringFret> toreturn = new ArrayList<StringFret>();
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

	private void rCalcFingerings(ChordFingering current, int firstAvailableFinger, ArrayList<StringFret> options, ArrayList<ChordFingering> chordFingerings, ArrayList<ChordFingering> rejects) {
		if (primaryValidator.validate(current, chord))
			chordFingerings.add(current);
		else
			rejects.add(current);

		// Derive possible open chords.
		patterns: for (boolean[] pattern : openStringPatterns) {
			int[] openAbsoluteFrets = current.absoluteFrets.clone();
			int[] openCapoRelativeFrets = current.capoRelativeFrets.clone();
			int[] openFingers = current.fingers.clone();
			IntervalicNote[] openNotes = current.notes.clone();
			for (int s = 0; s < pattern.length; ++s) {
				if (pattern[s]) {
					if (openNotes[s] != null)
						continue patterns;
					openAbsoluteFrets[s] = highestCapoedFrets[s];
					openCapoRelativeFrets[s] = 0;
					openNotes[s] = inChord[s][highestCapoedFrets[s]];
				}
			}
			ChordFingering open = new ChordFingering(chord, openAbsoluteFrets, openCapoRelativeFrets, openFingers, openNotes, false);
			if (primaryValidator.validate(open, chord))
				chordFingerings.add(open);
			else
				rejects.add(open);
		}

		// Derive new chord fingerings by placing new fingers
		if (firstAvailableFinger <= 4) {
			for(StringFret newPlacement : options) {
				for (int finger = firstAvailableFinger; finger<5; ++finger) {
					int[] newAbsoluteFrets = current.absoluteFrets.clone();
					int[] newCapoRelativeFrets = current.capoRelativeFrets.clone();
					int[] newFingers = current.fingers.clone();
					IntervalicNote[] newNotes = current.notes.clone();

					newAbsoluteFrets[newPlacement.string] = newPlacement.fret;
					newCapoRelativeFrets[newPlacement.string] = newPlacement.fret;
					newFingers[newPlacement.string] = finger;
					newNotes[newPlacement.string] = getNoteAt(newPlacement.string, newPlacement.fret);
					ChordFingering newChord = new ChordFingering(chord, newAbsoluteFrets, newCapoRelativeFrets, newFingers, newNotes, false);
					rCalcFingerings(newChord, finger+1, removeIllegal(options, newPlacement, finger), chordFingerings, rejects);
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

	private ChordListModel chords;
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
