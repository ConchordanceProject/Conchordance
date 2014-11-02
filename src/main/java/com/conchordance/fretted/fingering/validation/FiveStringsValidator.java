package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;

public class FiveStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "Has five strings";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.sortedNotes.length >= 5;
	}
}
