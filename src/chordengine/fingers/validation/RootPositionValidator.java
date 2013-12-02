package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class RootPositionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Root position";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 0;
	}
}
