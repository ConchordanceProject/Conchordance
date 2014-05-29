package chordengine.fingers;

import java.util.Arrays;

import chordengine.music.Chord;
import chordengine.music.IntervalicNote;

/**
 * Class to model a fingering of a chord.
 * Maintains the particular placement of fingers on the fretboard.
 * Maintains the resulting notes of the fingering, by order of string.
 * Maintains the resulting notes of the fingering, musically ordered.
 *
 */
public class ChordFingering implements Cloneable {
	/**
	 * Factory method to produce a trivial chord fingering, one in which no fingers have been placed.
	 * @param numStrings number of strings the chord fingering has
	 * @return the trivial chord fingering
	 */
	public static ChordFingering trivialChordFingering(int numStrings) {
		int[] unfretted = new int[numStrings];
		Arrays.fill(unfretted, -1);
		return new ChordFingering(null, unfretted, unfretted, new int[numStrings], new IntervalicNote[numStrings], false);
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
	
	public final int inversion;
	
	public final boolean isBarred;
	
	/**
	 * The lowest fret on which a finger is placed.
	 */
	public final int minFret;
	
	/**
	 * The highest fret on which a finger is placed.
	 */
	public final int maxFret;
	
	/**
	 * The highest-numbered finger that is placed in this finger.
	 */
	public final int maxFinger;
	
	/**
	 * The number of notes sounded in this fingering.
	 */
	public final int numNotes;
	
	/**
	 * The number of fingers used in this fingering.
	 */
	public final int numFingers;
	
	/**
	 * The number of strings of the instrument.
	 */
	public final int numStrings;
	
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
		return new ChordFingering(fingersCopy, absoluteFretsCopy, capoRelativeFretsCopy, notesCopy, sortedNotesCopy, inversion, isBarred, minFret, maxFret, maxFinger, numNotes, numFingers, numStrings);
	}
	
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
		this.numStrings = fingers.length;
		this.fingers = fingers;
		this.absoluteFrets = absoluteFrets;
		this.capoRelativeFrets = capoRelativeFrets;
		this.notes = notes;
		this.isBarred = isBarred;
		this.chord = chord;
		
		int tempNumNotes = 0;
		int tempMinFret = Integer.MAX_VALUE;
		int tempMaxFret = -1;
		int tempMaxFinger = 0;
		int tempNumFingers = 0;
		for (int s = 0; s < numStrings; ++s) {
			tempMaxFinger = Math.max(tempMaxFinger, fingers[s]);
			if (notes[s] != null)
				++tempNumNotes;
			if (capoRelativeFrets[s]>0){
				++tempNumFingers;
				tempMinFret = Math.min(tempMinFret, capoRelativeFrets[s]);
				tempMaxFret = Math.max(tempMaxFret, capoRelativeFrets[s]);
			}
		}
		minFret = tempMinFret;
		maxFret = tempMaxFret;
		maxFinger = tempMaxFinger;
		numFingers = tempNumFingers;
		numNotes = tempNumNotes;

		//populate and sort sortedNotes
		sortedNotes = new IntervalicNote[tempNumNotes];
		int i = 0;
		for (IntervalicNote n : notes) {
			if (n != null)
				sortedNotes[i++] = n;
		}
		Arrays.sort(sortedNotes);
		
		int tempInversion = -1;
		if (chord != null) {
			for (int inv = 0; inv<chord.intervals.length; ++inv) {
				if (sortedNotes[0].interval.strictlyEquals(chord.intervals[inv]))
					tempInversion = inv;
			}
		}
		inversion = tempInversion;
	}

	private ChordFingering(int[] fingers, int[] absoluteFrets, int[] capoRelativeFrets, IntervalicNote[] notes, IntervalicNote[] sortedNotes, int inversion, boolean isBarred, int minFret, int maxFret, int maxFinger, int numNotes, int numFingers, int numStrings) {
		this.fingers = fingers;
		this.absoluteFrets = absoluteFrets;
		this.capoRelativeFrets = capoRelativeFrets;
		this.notes = notes;
		this.sortedNotes = sortedNotes;
		this.inversion = inversion;
		this.isBarred = isBarred;
		this.minFret = minFret;
		this.maxFret = maxFret;
		this.maxFinger = maxFinger;
		this.numNotes = numNotes;
		this.numFingers = numFingers;
		this.numStrings = numStrings;
		this.chord = null;
	}
}
