package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;


/**
 * Acts as a filter for a ChordFingering, checking it against a predicate and returning whether it matches.
 *
 */
public interface ChordFingeringValidator {
	/**
	 * Checks if a ChordFingering meets chosen qualifications.
	 * @param candidate the ChordFingering to test
	 * @param compareTo the Chord to compare the candidate to
	 * @return true if the chord fingering satisfies the qualifications relative to the given chord
	 */
	public boolean validate(ChordFingering candidate, Chord compareTo);
}
