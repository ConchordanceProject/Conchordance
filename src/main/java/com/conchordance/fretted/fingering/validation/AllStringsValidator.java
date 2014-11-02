package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;


public class AllStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "All strings played";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.sortedNotes.length == candidate.notes.length;
	}
}
