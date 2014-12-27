
package com.conchordance.fretted.fingering;

import com.conchordance.fretted.FretboardModel;

import java.util.List;

public interface ChordFingeringGenerator {

	/**
	 * Finds all valid ChordFingerings in the context of the given Fretboard.
	 */
	public abstract List<ChordFingering> getChordFingerings(FretboardModel fretboard);

	/**
	 * Finds all valid ChordFingerings whose fret shapes exactly match the fret shape given,
	 * in the context of the given Fretboard.
	 */
	public abstract List<ChordFingering> getChordFingerings(FretboardModel fretboard, int[] frets);

	public abstract List<ChordFingering> getAllChordFingerings(FretboardModel fretboard);

}
