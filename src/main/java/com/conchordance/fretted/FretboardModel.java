package com.conchordance.fretted;

import java.util.LinkedList;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;
import com.conchordance.music.Interval;
import com.conchordance.music.IntervalicNote;
import com.conchordance.music.Note;

public class FretboardModel {

	public Chord getChord() {
		return chord;
	}

	public void setChord(Chord chord) {
		this.chord = chord;
		updateInChord();
	}
	
	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
		updateInChord();
	}

	public boolean hasChordNoteAt(int string, int fret) {
		return fret >= instrument.fretNutPositions[string] && inChord[string][fret] != null;
	}

	/**
	 * Gets the chord note that is at a position on the neck.
	 *
	 * This is the note within the Fretboard's current chord, so is not defined for a string/fret that doesn't belong
	 * in the current chord.
	 */
	public IntervalicNote getChordNoteAt(int string, int fret) {
		return inChord[string][fret];
	}

	public boolean isInRange(int string, int fret) {
		return isAtFretNut(string, fret) || fret >= instrument.fretNutPositions[string];
	}

	public boolean isAtFretNut(int string, int fret) {
		return fret == instrument.fretNutPositions[string];
	}

	/**
	 * The fret number equivalent to "open" for this string.
	 *
	 * This takes into account both the position of the fret nut and any capos on that string.
	 */
	public int getOpenPosition(int string) {
		return instrument.fretNutPositions[string];
	}

	/**
	 * Gets the note played when the string is open.
	 *
	 * The note may be at the nut or at a capo.
	 */
	public IntervalicNote getOpenNote(int string) {
		return getChordNoteAt(string, getOpenPosition(string));
	}

	/**
	 * Gets the ChordFingering with the given fret shape on this fretboard.
	 */
	public ChordFingering getChordFingering(Chord chord, int[] relativeFrets) {
		int[] absoluteFrets = new int[relativeFrets.length];
		IntervalicNote[] notes = new IntervalicNote[relativeFrets.length];

		for (int s = 0; s<relativeFrets.length; ++s) {
			int relativeFret = relativeFrets[s];
			if (relativeFret != -1) {
				int absoluteFret = relativeFret == 0 ? getInstrument().fretNutPositions[s] : relativeFret;
				absoluteFrets[s] = absoluteFret;
				IntervalicNote note = getChordNoteAt(s, absoluteFret);
				notes[s] = note;
			} else {
				absoluteFrets[s] = -1;
			}
		}

		// No finger information is given, so this is blank
		int[] fingers = new int[relativeFrets.length];

		return new ChordFingering(chord, absoluteFrets, relativeFrets, fingers, notes, false);
	}

	/**
	 * All combinations of open strings that can be included in the current chord.
	 * Each combination is represented by a boolean array that is indexed by string number
	 */
	public boolean[][] openStringCombinations() {
		LinkedList<Integer> validStrings = new LinkedList<>();
		for (int string = 0; string<instrument.strings; ++string) {
			if (hasChordNoteAt(string, getOpenPosition(string)))
				validStrings.add(string);
		}

		// combinations = 2^(possible open strings)-1
		int combos = (int)Math.pow(2, validStrings.size())-1; // no combination corresponding to zero strings (all zeroes in binary)

		boolean[][] bitPatterns = new boolean[combos][getInstrument().strings];

		for (int i = 0; i<combos; ++i) {
			boolean[] asBinary = bitPatterns[i];
			int icopy = i+1;
			for (int j : validStrings) {
				asBinary[j] = icopy%2 == 1;
				icopy/=2;
			}
			bitPatterns[i] = asBinary;
		}
		return bitPatterns;
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
	 * The int-value of the note at a position on the fretboard, based on an absolute fret
	 */
	private int note(int string, int fret) {
		// Subtracting the fret nut position does nothing in the normal case, but for banjo-like instruments
		// it moves the open note value up to the correct fret (fifth fret for example)
		return fret + instrument.tuning[string].halfSteps - instrument.fretNutPositions[string];
	}

	public FretboardModel(Instrument instrument, Chord chord) {
		this.instrument = instrument;
		this.chord = chord;

		updateInChord();
	}

	public FretboardModel() {
		this(Instrument.GUITAR, Chord.A_MAJOR);
	}

	private Instrument instrument;
	private Chord chord;
	private IntervalicNote[][] inChord;
}
