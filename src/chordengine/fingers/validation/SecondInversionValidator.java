package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class SecondInversionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Second inversion";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 2;
	}
}
