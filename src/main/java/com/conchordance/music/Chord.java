package com.conchordance.music;


/**
 * Model of a musical chord as an ordered sequence of notes.
 *
 */
public class Chord {	
	public static final Chord A_MAJOR = 
			new Chord(new Note('A', 0, 0), ChordType.MAJOR);
	
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

    public final String typeName;
	
	public String toString() {
		return root.toString();
	}
	
	public Chord(Note root, ChordType type) {
		this.root = root;
		this.intervals = type.intervals;
        this.typeName = type.name;
		Scale scale = Scale.getMajorScale(root);
		this.notes = scale.applyIntervals(intervals);
	}
}
