package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;


public class RootPositionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Root position";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 0;
	}
}
