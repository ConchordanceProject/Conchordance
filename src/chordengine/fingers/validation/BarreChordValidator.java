package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class BarreChordValidator implements ChordFingeringValidator {
	public String toString() {
		return "Barre chords only";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.isBarred;
	}
}
