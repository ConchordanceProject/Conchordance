
package com.conchordance.fretted.fingering;

import com.conchordance.fretted.FretboardModel;
import com.conchordance.fretted.Instrument;
import com.conchordance.fretted.fingering.validation.BaseValidator;
import com.conchordance.fretted.fingering.validation.ChordFingeringValidator;
import com.conchordance.fretted.fingering.validation.TrivialValidator;
import com.conchordance.music.Chord;
import com.conchordance.music.IntervalicNote;

import java.util.ArrayList;
import java.util.List;

public class RecursiveChordFingeringGenerator implements ChordFingeringGenerator {

	public List<ChordFingering> getChordFingerings(FretboardModel fretboard) {
		this.fretboard = fretboard;
		this.instrument = fretboard.getInstrument();
		this.chord = fretboard.getChord();

		// Find all points on the neck that *could* be included in a chord
		ArrayList<StringFret> fingerOptions = new ArrayList<>();
		for (int s = 0; s<instrument.strings; ++s) {
			for (int f = 1; f<=instrument.frets; ++f) {
				if (fretboard.hasChordNoteAt(s, f) && fretboard.isInRange(s, f))
					fingerOptions.add(new StringFret(s, f));
			}
		}

		return calculateFingerings(fingerOptions);
	}

	public List<ChordFingering> getChordFingerings(FretboardModel fretboard, int[] frets) {
		this.fretboard = fretboard;
		this.instrument = fretboard.getInstrument();
		this.chord = fretboard.getChord();

		ArrayList<StringFret> fingerOptions = new ArrayList<>();
		for (int string = 0; string<frets.length; ++string) {
			if (frets[string] > 0)
				fingerOptions.add(new StringFret(string, frets[string]));
		}

		return calculateFingerings(fingerOptions);
	}

	public List<ChordFingering> getAllChordFingerings(FretboardModel fretboard) {
		this.fretboard = fretboard;
		this.instrument = fretboard.getInstrument();
		this.chord = fretboard.getChord();

		// all positions below the 6th fret are available
		ArrayList<StringFret> fingerOptions = new ArrayList<>();
		for (int s = 0; s<instrument.strings; ++s) {
			for (int f = 1; f<=5; ++f) {
				if (fretboard.isInRange(s, f))
					fingerOptions.add(new StringFret(s, f));
			}
		}

		ChordFingeringValidator oldValidator = validator;
		validator = new ChordFingeringValidator() {
			@Override
			public boolean validate(ChordFingering candidate, Chord compareTo) {
				return candidate.notes.length >= 4;
			}
		};

		List<ChordFingering> fingerings = calculateFingerings(fingerOptions);
		validator = oldValidator;
		return fingerings;
	}

	public RecursiveChordFingeringGenerator() {
		validator = new BaseValidator();
	}

	private List<ChordFingering> calculateFingerings(ArrayList<StringFret> fingerOptions) {
		ArrayList<ChordFingering> chordFingerings = new ArrayList<>();
		ChordFingering base = ChordFingering.trivialChordFingering(chord, instrument.strings);

		// Find all open chords (only open strings are played)
		openStringPatterns = fretboard.openStringCombinations();
		for (boolean[] pattern : openStringPatterns) {
			int[] absoluteFrets = new int[instrument.strings];
			int[] relativeFrets = new int[instrument.strings];
			int[] fingers = new int[instrument.strings];
			IntervalicNote[] notes = new IntervalicNote[pattern.length];
			for (int string = 0; string < instrument.strings; ++string) {
				if (pattern[string]) {
					// Include the open string in this chord
					absoluteFrets[string] = fretboard.getOpenPosition(string);
					relativeFrets[string] = 0;
					notes[string] = fretboard.getOpenNote(string);
				} else {
					// The string is not used in this chord
					absoluteFrets[string] = -1;
					relativeFrets[string] = -1;
				}
			}
			ChordFingering open = new ChordFingering(chord, absoluteFrets, relativeFrets, fingers, notes, false);
			if (validator.validate(open, chord))
				chordFingerings.add(open);
		}

		// For each legal position, assign finger one to that note and derive new chords from that shape.
		for (StringFret fingerPos : fingerOptions) {
			int finger = 1;
			IntervalicNote note = fretboard.getChordNoteAt(fingerPos.string, fingerPos.fret);
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
					if (toBarre.absoluteFrets[s] == -1 && fretboard.hasChordNoteAt(s, barreFret))
						canBarre = true;
				}
				if (canBarre) {
					ChordFingering tempBarre = toBarre.clone();
					for (int s = fingerPos.string-1; s >=0; --s) {
						if (toBarre.absoluteFrets[s] == -1 && fretboard.hasChordNoteAt(s, barreFret)) {
							tempBarre.absoluteFrets[s] = barreFret;
							tempBarre.capoRelativeFrets[s] = barreFret;
							tempBarre.fingers[s] = 1;
							tempBarre.notes[s] = fretboard.getChordNoteAt(s, barreFret);
						}
					}
					ChordFingering barred = new ChordFingering(chord, tempBarre.absoluteFrets, tempBarre.capoRelativeFrets, tempBarre.fingers, tempBarre.notes, true);
					if (validator.validate(barred, chord))
						chordFingerings.add(barred);
				}
			}

			for (finger = 2; finger<5; ++finger) {
				IntervalicNote newNote = fretboard.getChordNoteAt(fingerPos.string, fingerPos.fret);
				ChordFingering newChord = base.clone(fingerPos.string, fingerPos.fret, fingerPos.fret, finger, newNote);

				nextFingerOptions = removeIllegal(fingerOptions, fingerPos, finger);
				rCalcFingerings(newChord, finger+1, nextFingerOptions, chordFingerings);
			}
		}

		return chordFingerings;
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
		if (validator.validate(current, chord))
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
			int[] relativeFrets = current.capoRelativeFrets.clone();
			int[] fingers = current.fingers.clone();
			IntervalicNote[] notes = current.notes.clone();
			for (int string = 0; string < instrument.strings; ++string) {
				if (pattern[string]) {
					absoluteFrets[string] = fretboard.getOpenPosition(string);
					relativeFrets[string] = 0;
					notes[string] = fretboard.getOpenNote(string);
				}
			}
			ChordFingering open = new ChordFingering(chord, absoluteFrets, relativeFrets, fingers, notes, false);
			if (validator.validate(open, chord))
				chordFingerings.add(open);
		}

		// Derive new chord fingerings by placing new fingers
		if (firstAvailableFinger <= 4) {
			// Explore each available position with each available finger
			for (StringFret newPlacement : options) {
				for (int finger = firstAvailableFinger; finger<5; ++finger) {
					IntervalicNote newNote = fretboard.getChordNoteAt(newPlacement.string, newPlacement.fret);
					ChordFingering newChord = current.clone(newPlacement.string, newPlacement.fret, newPlacement.fret, finger, newNote);

					rCalcFingerings(newChord, finger+1, removeIllegal(options, newPlacement, finger), chordFingerings);
				}
			}
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

	private FretboardModel fretboard;
	private Instrument instrument;
	private Chord chord;
	private ChordFingeringValidator validator;

	private boolean[][] openStringPatterns;
}
