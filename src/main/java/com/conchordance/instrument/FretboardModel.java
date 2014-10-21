package com.conchordance.instrument;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.fingers.list.ChordListModel;
import com.conchordance.music.Chord;
import com.conchordance.music.IntervalicNote;

/**
 * Class to model the fretboard of an instrument. The Fretboard does the brunt of the work of generating chords.
 * Given a chord, it finds all the positions on the neck that are part of the chord, and generates ChordFingerings out of fingerable combinations of those positions.
 * The Fretboard maintains the configuration of capos on the neck, and calculates the chords based on capos.
 *
 */
public abstract class FretboardModel {
	public abstract ChordListModel getChordList();

	public abstract void setChord(Chord c);
	
	public abstract Instrument getInstrument();
	
	public abstract void setInstrument(Instrument instrument);

	/**
	 * Determines whether a position on the neck is a note that is part of the chord.
	 * This includes notes that are not accessible because of a capo, or are out of the fret range.
	 */
	public abstract boolean hasChordNoteAt(int string, int fret);
	
	/**
	 * Whether a finger can be placed on a position on the fretboard.
	 * A position is out of range if it is outside of the range specified by the min/max frets, or if it is nut-side of a capo.
	 */
	public abstract boolean isInRange(int string, int fret);

    /**
     * Gets the chord note that is at a position on the neck.
     *
     * This is the note within the Fretboard's current chord, so is not defined for a string/fret that doesn't belong
     * in the current chord
     */
    public abstract IntervalicNote getChordNoteAt(int string, int fret);

    /**
	 * Whether a position is directly covered by a capo.
	 */
	public abstract boolean isOnCapo(int string, int fret);
	
	/**
	 * Whether a fret is on a fret nut of a given string.
	 * Note that for most strings the fret nut is at fret 0, but in some cases (like five string banjo) the fret nut occurs on a higher fret.
	 */
	public abstract boolean isAtFretNut(int string, int fret);
	
	/**
	 * Set the minimum fret that can be used when building chords.
	 * Open notes, whether open to the nut or a capo, are used even if below the minimum fret.
	 */
	public abstract void setMinFret(int f);
	
	/**
	 * Gets the minimum fret that can be used when building chords.
	 */
	public abstract int getMinFret();
	
	/**
	 * Set the maximum fret that can be used when building chords.
	 * Capoed notes are used even if above the maximum fret.
	 */
	public abstract void setMaxFret(int f);

	/**
	 * Gets the maximum fret that can be used when building chords.
	 */
	public abstract int getMaxFret();

	/**
	 * Gets the set of capos that are on the fretboard.
	 */
	public abstract List<Capo> getCapos();

	/**
	 * Adds a capo model to the neck of the instrument, at the location specified by the capo.
	 */
	public abstract void addCapo(Capo capo);
	
	/**
	 * Creates a new capo for the first available fret and adds it to the instrument.
	 * If there is no room for a capo, no capo is created or placed.
	 */
	public abstract void addCapo();
	
	/**
	 * Invoked when a capo is moved from one place on the fretboard to another.
	 */
	public abstract void capoMoved();
	
	/**
	 * Remove a capo from the fretboard.
	 */
	public abstract void removeCapo(Capo capo);

    /**
     *
     */
    public ChordFingering getChordFingering(Chord chord, int[] relativeFrets) {
        int[] absoluteFrets = new int[relativeFrets.length];
        IntervalicNote[] notes = new IntervalicNote[relativeFrets.length];

        for (int s = 0; s<relativeFrets.length; ++s) {
            int relativeFret = relativeFrets[s];
            if (relativeFret != -1) {
                int absoluteFret = relativeFret == 0 ? getInstrument().fretNutPositions[s] : relativeFret;
                absoluteFrets[s] = absoluteFret;
                IntervalicNote note = getChordNoteAt(s, absoluteFret);
                notes[s] = note;
            } else {
                absoluteFrets[s] = -1;
            }
        }

        // No finger information is given, so this is blank
        int[] fingers = new int[relativeFrets.length];

        return new ChordFingering(chord, absoluteFrets, relativeFrets, fingers, notes, false);
    }

	/**
	 * Produce an array representing each possible combination of strings that could be played open.
	 * Each combination is represented as a boolean array. Index the array by string number for whether that string is used as part of the combination.
	 * @param validStrings the strings that can be played open
	 */
	public boolean[][] openStringCombinations(ArrayList<Integer> validStrings) {
		// combinations = 2^(possible open strings)-1
		int combos = (int)Math.pow(2, validStrings.size())-1; // no combination corresponding to zero strings (all zeroes in binary)

		boolean[][] bitPatterns = new boolean[combos][getInstrument().strings];

		for (int i = 0; i<combos; ++i) {
			boolean[] asBinary = bitPatterns[i];
			int icopy = i+1;
			for (int j : validStrings) {
				asBinary[j] = icopy%2 == 1;
				icopy/=2;
			}
			bitPatterns[i] = asBinary;
		}
		return bitPatterns;
	}
	
	public void addFretboardListener(FretboardListener listener) {
		listeners.add(listener);
	}

	public FretboardModel() {
		listeners = new LinkedList<>();
	}
	
	protected void notifyFretboardChanged() {
		for (FretboardListener l : listeners)
			l.fretboardChanged();
	}
	
	private LinkedList<FretboardListener> listeners;
}
