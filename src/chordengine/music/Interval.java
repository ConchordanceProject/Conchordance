package chordengine.music;

/**
 * Models a musical interval.
 * Expresses an interval as some modification (a number of flats or sharps) of a major interval, and as a number of half-steps from the root.
 *
 */
public class Interval {
	private static final int[] intervalsToHalfSteps = {0, 2, 4, 5, 7, 9, 11};

	public static final Interval UNISON = new Interval(0, 0);
	public static final Interval MINOR_THIRD = new Interval(2, -1);
	public static final Interval MAJOR_THIRD = new Interval(2, 0);
	public static final Interval DIMINISHED_FIFTH = new Interval(4, -1);
	public static final Interval PERFECT_FIFTH = new Interval(4, 0);
	public static final Interval MINOR_SEVENTH = new Interval(6, -1);
	public static final Interval MAJOR_SEVENTH = new Interval(6, 0);
	public static final Interval[] MAJOR_INTERVALS = {UNISON, MAJOR_THIRD, PERFECT_FIFTH};
	
	/**
	 * The major interval this interval is based on, 0 for unison.
	 */
	public final int major;
	
	/**
	 * The sharp or flat value modifying the major interval. Positive values are sharp, and the magnitude of this value is the number of modifiers.
	 */
	public final int modifier;
	
	/**
	 * The number of half-steps from the root this interval expresses.
	 */
	public final int halfSteps;

	public boolean strictlyEquals(Interval other) {
		return major == other.major && modifier == other.modifier;
	}
	
	public Interval(int major, int modifier) {
		this.major = major;
		this.modifier = modifier;
		halfSteps = intervalsToHalfSteps[major]+modifier;
	}
}
