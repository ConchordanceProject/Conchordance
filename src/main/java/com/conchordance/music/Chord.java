package com.conchordance.music;


/**
 * Model of a musical chord as an ordered sequence of notes.
 *
 */
public class Chord {	
	public static final Chord A_MAJOR = 
			new Chord(new Note('A', 0, 0), new Note[]{new Note('A', 0, 0), new Note('C', 1, 0), new Note('E', 0, 0)}, Interval.MAJOR_INTERVALS);
	
	/**
	 * The musical root of the chord.
	 */
	public final Note root;
	
	/**
	 * The notes of the chord.
	 * Where ordering is significant notes should be ordered in the array lowest-to-highest.
	 */
	public final Note[] notes;

	public final Interval[] intervals;
	
	public String toString() {
		return root.toString();
	}
	
	public Chord(Note root, ChordType type) {
		this.root = root;
		this.intervals = type.intervals;
		Scale scale = Scale.getMajorScale(root);
		this.notes = scale.applyIntervals(intervals);
	}
	
	/**
	 * Constructor.
	 * Initializes the chord with its root and notes.
	 * @param root the root of the chord
	 * @param notes the notes in the chord
	 */
	public Chord(Note root, Note[] notes, Interval[] intervals) {
		this.root = root;
		this.notes = notes;
		this.intervals = intervals;
	}
}
