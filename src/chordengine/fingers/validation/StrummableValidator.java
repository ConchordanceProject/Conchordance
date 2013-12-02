package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class StrummableValidator implements ChordFingeringValidator {
	public String toString() {
		return "All strings are together";
	}
	public boolean validate(ChordFingering candidate, Chord compareTo) {
		int lowestStringUsed = 0;
		while (candidate.notes[lowestStringUsed] == null)
			++lowestStringUsed;
		
		int highestStringUsed = candidate.numStrings-1;
		while (candidate.notes[highestStringUsed] == null)
			--highestStringUsed;
			
		for (int s = lowestStringUsed+1; s<highestStringUsed; ++s) {
			if (candidate.notes[s] == null)
				return false;
		}

		return true;
	}
}
