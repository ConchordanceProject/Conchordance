package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class FiveStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "Has five strings";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.numNotes >= 5;
	}
}
