package chordengine.instrument;

/**
 * Models the string configuration of a fretted instrument.
 *
 */
public class Instrument {

	/**
	 * Guitar in standard tuning.
	 */
	public static final Instrument GUITAR = new Instrument("Guitar", new int[] {7+5*12, 2+5*12, 10+4*12, 5+4*12, 0+4*12, 7+3*12}, new int[6], 14); 
	
	/**
	 * Default set of instruments available.
	 */
	public static final Instrument[] DEFAULT_INSTRUMENTS = {
		GUITAR,
		new Instrument("Guitar - DADGAD", new int[] {5+5*12, 0+5*12, 10+4*12, 5+4*12, 0+4*12, 5+3*12}, new int[6], 14),
		new Instrument("Guitar - Drop D", new int[] {7+5*12, 2+5*12, 10+4*12, 5+4*12, 0+4*12, 5+3*12}, new int[6], 14),
		new Instrument("Irish Bouzouki", new int[] {5+5*12, 0+5*12, 5+4*12, 10+3*12}, new int[4], 12),
		new Instrument("Mandolin", new int[] {10+3*12, 5+4*12, 0+5*12, 7+5*12}, new int[4], 12),
		new Instrument("5-String Banjo", new int[] {5+4*12, 2+4*12, 10+3*12, 3+3*12, 5+4*12}, new int[] {0, 0, 0, 0, 5}, 12),
	};
	
	/**
	 * The name of the instrument
	 */
	public final String name;
	
	/**
	 * The tuning of the instrument, from lowest-numbered string to highest-numbered string.
	 * The tuning is considered to be the note conceptually at the 0th fret. If a string is tuned to a G at the fifth fret (as in the five-string banjo) that string conceptually tunes to a D at the 0th fret.
	 */
	public final int[] tuning;

	/**
	 * The number of frets up the neck where each string begins.
	 * For most instruments, every string extends the full extent of the neck, and so the nut is at 0. The fifth string of a five-string banjo starts at the fifth fret, and so should have the value 5 in this array.
	 * The tuning of an instrument should compensate for any non-zero fret nut position. For example if a string is tuned to G and has its nut at the fifth fret, the tuning should compensate that string to a D. This ensures that the instrument has a G on the fifth fret for that string.
	 */
	public final int[] fretNutPositions;
	
	/**
	 * The number of frets to depict for the instrument
	 */
	public final int frets;
	
	/**
	 * The number of strings the instrument has
	 */
	public final int strings;
	
	/**
	 * The name of the instrument
	 */
	public String toString() {
		return name;
	}

	public Instrument(String name, int[] tuning, int[] fretNutPositions, int frets){
		this.name = name;
		this.tuning = tuning;
		this.fretNutPositions = fretNutPositions;
		this.frets = frets;
		strings = tuning.length;
	}
}
