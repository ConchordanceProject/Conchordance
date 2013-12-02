package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class FirstInversionValidator implements ChordFingeringValidator {
	public String toString() {
		return "First inversion";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 1;
	}
}
