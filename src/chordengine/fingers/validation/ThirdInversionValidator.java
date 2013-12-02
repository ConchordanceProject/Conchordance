package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class ThirdInversionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Third inversion";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 3;
	}
}
