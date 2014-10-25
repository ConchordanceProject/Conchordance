package com.conchordance.fingers.validation;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;

public class ConjunctiveValidator implements ChordFingeringValidator {
	public boolean validate(ChordFingering candidate, Chord compareTo) {
		for (ChordFingeringValidator v : validators) {
			if (!v.validate(candidate, compareTo))
				return false;
		}
		return true;
	}
	
	public ConjunctiveValidator(ChordFingeringValidator... validators) {
		this.validators = validators;
	}
	
	private ChordFingeringValidator[] validators;
}
