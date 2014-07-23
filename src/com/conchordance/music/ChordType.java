package com.conchordance.music;


/**
 * A type of chord, as in Major, Minor, Diminished, etc.
 * 
 * These are represented as an ordered series of intervals.
 *
 */
public class ChordType {
	
	public static final ChordType MAJOR = new ChordType("M", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH});

	/**
	 * The name of the chord type (i.e. maj, min, etc)
	 */
	public final String name;
	
	/**
	 * The intervals that comprise this chord type, relative to the root
	 */
	public final Interval[] intervals;
	
	public String toString() {
		return name;
	}

	public ChordType(String name, Interval[] intervals) {
		this.name = name;
		this.intervals= intervals;
	}
}
