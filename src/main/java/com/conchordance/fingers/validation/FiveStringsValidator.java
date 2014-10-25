package com.conchordance.fingers.validation;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;

public class FiveStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "Has five strings";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.sortedNotes.length >= 5;
	}
}
