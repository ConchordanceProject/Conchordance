package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;


public class BarreChordValidator implements ChordFingeringValidator {
	public String toString() {
		return "Barre chords only";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.isBarred;
	}
}
