package com.conchordance.fingers.validation;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;


public class AllStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "All strings played";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.sortedNotes.length == candidate.notes.length;
	}
}
