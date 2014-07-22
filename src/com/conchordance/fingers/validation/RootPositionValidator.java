package com.conchordance.fingers.validation;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;


public class RootPositionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Root position";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 0;
	}
}
