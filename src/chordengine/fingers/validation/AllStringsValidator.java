package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class AllStringsValidator implements ChordFingeringValidator {
	public String toString() {
		return "All strings played";
	}

	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return candidate.numNotes == candidate.numStrings;
	}
}
