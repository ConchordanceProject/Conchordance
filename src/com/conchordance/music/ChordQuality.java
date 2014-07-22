package com.conchordance.music;

import java.util.HashMap;


/**
 * Models a musical chord quality (major, minor, etc).
 * Couples the name of a chord quality with the intervals that comprise it.
 *
 */
public class ChordQuality {
	
	public static final ChordQuality MAJOR = new ChordQuality("M", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH});
	
	/**
	 * Instances of ChordQuality. Represents a list of chord qualities, usable for chord creation.
	 */
	public static HashMap<String, ChordQuality> instances;
	static {
		instances = new HashMap<String, ChordQuality>();

		instances.put("M", MAJOR);
		instances.put("m", new ChordQuality("m", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH}));
		instances.put("7", new ChordQuality("7", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH, Interval.MINOR_SEVENTH}));
		instances.put("M7", new ChordQuality("M7", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH, Interval.MAJOR_SEVENTH}));
		instances.put("m7", new ChordQuality("m7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH, Interval.MINOR_SEVENTH}));
		instances.put("m-M7", new ChordQuality("m-M7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH, Interval.MAJOR_SEVENTH}));
		instances.put("dim7", new ChordQuality("dim7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.DIMINISHED_FIFTH, new Interval(6, -2)}));
	}

	/**
	 * The name of the chord quality (i.e. maj, min, etc)
	 */
	public final String name;
	
	/**
	 * The intervals that comprise this chord quality, including the root, ordered by increasing distance from root.
	 */
	public final Interval[] intervals;
	
	/**
	 * The name of the chord quality, so as to be appended to a root to form a chord name.
	 */
	public String toString() {
		return name;
	}

	public ChordQuality(String name, Interval[] intervals) {
		this.name = name;
		this.intervals= intervals;
	}
}
