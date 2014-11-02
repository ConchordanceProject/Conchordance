package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;


public class ThirdInversionValidator implements ChordFingeringValidator {
	public String toString() {
		return "Third inversion";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.inversion == 3;
	}
}
