package com.conchordance.fingers;

import java.util.Arrays;

import com.conchordance.music.Chord;
import com.conchordance.music.IntervalicNote;


/**
 * Class to model a fingering of a chord.
 * Maintains the particular placement of fingers on the fretboard.
 * Maintains the resulting notes of the fingering, by order of string.
 * Maintains the resulting notes of the fingering, musically ordered.
 *
 */
public class ChordFingering implements Cloneable {
	public static ChordFingering trivialChordFingering(Chord chord, int numStrings) {
		int[] unfretted = new int[numStrings];
		Arrays.fill(unfretted, -1);
		return new ChordFingering(chord, unfretted, unfretted, new int[numStrings], new IntervalicNote[numStrings], false);
	}
	
	/**
	 * Index by string for what finger is placed on that string; -1 indicates no finger is placed on a string.
	 */
	public final int[] fingers;
	
	/**
	 * Index by string for what fret that string has a finger or capo.
	 * -1 indicates that the string is not played.
	 * 0 indicates that the string is played totally open; no finger or capo.
	 */
	public final int[] absoluteFrets;

	/**
	 * Index by string for what fret that string is fingered at.
	 * -1 indicates that the string is not played.
	 * 0 indicates that the string is open to a capo or the nut.
	 */
	public final int[] capoRelativeFrets;
	
	/**
	 * The notes resulting from this fingering, indexed by string.
	 */
	public final IntervalicNote[] notes;
	
	/**
	 * The notes resulting from this fingering, sorted by increasing pitch.
	 */
	public final IntervalicNote[] sortedNotes;

    /**
     * The musical inversion of the chord
     * 0 indicates root position, 1 indicates first inversion, etc.
     */
	public final int inversion;
	
	public final boolean isBarred;
	
	/**
	 * The lowest fret on which a finger is placed, or 0 if the chord is entirely open
	 */
	public final int position;
	
	/**
	 * The highest fret on which a finger is placed, or 0 if the chord is entirely open
	 */
	public final int maxFret;

	/**
	 * The number of fingers used in this fingering.
	 */
	public final int numFingersUsed;

	public final Chord chord;
	
	/**
	 * Returns a deep clone of the ChordFingering. All arrays are new references with the same values.
	 * @return a deep clone of the ChordFingering.
	 */
	public ChordFingering clone() {
		int[] fingersCopy = Arrays.copyOf(fingers, fingers.length);
		int[] absoluteFretsCopy = Arrays.copyOf(absoluteFrets, absoluteFrets.length);
		int[] capoRelativeFretsCopy = Arrays.copyOf(absoluteFrets, absoluteFrets.length);
		IntervalicNote[] notesCopy = Arrays.copyOf(notes, notes.length);
		IntervalicNote[] sortedNotesCopy = Arrays.copyOf(sortedNotes, sortedNotes.length);
		return new ChordFingering(fingersCopy, absoluteFretsCopy, capoRelativeFretsCopy, notesCopy, sortedNotesCopy, inversion, isBarred, position, maxFret, numFingersUsed);
	}

    /**
     * Clones this chord fingering with the addition of one new finger placement,
     * adding a single note the the cloned chord
     */
	public ChordFingering clone(int string, int absoluteFret, int capoRelativeFret, int finger, IntervalicNote note) {
		int[] fingersCopy = Arrays.copyOf(fingers, fingers.length);
		int[] absoluteFretsCopy = Arrays.copyOf(absoluteFrets, absoluteFrets.length);
		int[] capoRelativeFretsCopy = Arrays.copyOf(absoluteFrets, absoluteFrets.length);
		IntervalicNote[] notesCopy = Arrays.copyOf(notes, notes.length);
		
		// Place the new finger
		fingersCopy[string] = finger;
		absoluteFretsCopy[string] = absoluteFret;
		capoRelativeFretsCopy[string] = capoRelativeFret;
		notesCopy[string] = note;
		
		return new ChordFingering(chord, absoluteFretsCopy, capoRelativeFretsCopy, fingersCopy, notesCopy, isBarred);
	}
	
	/**
	 * A String uniquely representing the shape of this chord on the fretboard.
	 * Does not represent which fingers are used to form the shape, so different ways to finger the same shape will produce equal-value strings.
	 * @return
	 */
	public String chordShapeHash() {
		String hash = "";
		for (int f : absoluteFrets)
			hash += f+".";
		return hash;
	}
	
	public String toString() {
		return chord.toString() + "{" + Arrays.toString(absoluteFrets) + "}";		
	}
	
	/**
	 * Constructor.
	 * Initializes the chord fingering. Given the placement of the fingers and the resulting notes, will calculate more relevant information about the fingering and sort the notes. 
	 * @param absoluteFrets the fret number of each string, zeroed at the nut
	 * @param capoRelativeFrets the fret number of each string, zeroed at the nut and capos
	 * @param fingers which finger is placed at each string
	 * @param notes the notes of the fingering, by string
	 */
	public ChordFingering(Chord chord, int[] absoluteFrets, int[] capoRelativeFrets, int[] fingers, IntervalicNote[] notes, boolean isBarred) {
		this.fingers = fingers;
		this.absoluteFrets = absoluteFrets;
		this.capoRelativeFrets = capoRelativeFrets;
		this.notes = notes;
		this.isBarred = isBarred;
		this.chord = chord;
		
		int numNotes = 0;
		int minimumFret = Integer.MAX_VALUE;
		int tempMaxFret = -1;
		int tempNumFingers = 0;

		for (int string = 0; string < absoluteFrets.length; ++string) {
			if (notes[string] != null)
				++numNotes;
			if (capoRelativeFrets[string]>0){
				++tempNumFingers;
				minimumFret = Math.min(minimumFret, capoRelativeFrets[string]);
				tempMaxFret = Math.max(tempMaxFret, capoRelativeFrets[string]);
			}
		}

        numFingersUsed = tempNumFingers;

        // If no fingers are used the chord is in the open position
        if (numFingersUsed == 0) {
            position = 0;
            maxFret = 0;
        } else {
            position = minimumFret;
            maxFret = tempMaxFret;
        }

		// Populate and sort sortedNotes
		sortedNotes = new IntervalicNote[numNotes];
		int i = 0;
		for (IntervalicNote n : notes) {
			if (n != null)
				sortedNotes[i++] = n;
		}
		Arrays.sort(sortedNotes);
		
		// Find inversion
		int tempInversion = -1;
		if (numNotes > 0) {
			for (int inv = 0; inv < chord.intervals.length; ++inv) {
				if (sortedNotes[0].interval.strictlyEquals(chord.intervals[inv]))
					tempInversion = inv;
			}
		}
		inversion = tempInversion;
	}

	private ChordFingering(int[] fingers, int[] absoluteFrets, int[] capoRelativeFrets, IntervalicNote[] notes, IntervalicNote[] sortedNotes, int inversion, boolean isBarred, int position, int maxFret, int numFingers) {
		this.fingers = fingers;
		this.absoluteFrets = absoluteFrets;
		this.capoRelativeFrets = capoRelativeFrets;
		this.notes = notes;
		this.sortedNotes = sortedNotes;
		this.inversion = inversion;
		this.isBarred = isBarred;
		this.position = position;
		this.maxFret = maxFret;
		this.numFingersUsed = numFingers;
		this.chord = null;
	}
}
