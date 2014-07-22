package com.conchordance.music;


/**
 * Models a musical note. Expresses the name of the note (C, D, etc), number of
 * flats or sharps, octave, and integral representation.
 * 
 */
public class Note implements Comparable<Note> {
	public static final String SHARP = "\u266F";
	public static final String DOUBLE_SHARP = "x";
	public static final String FLAT = "\u266D";
	
	private static final int[] noteNamesToHalfSteps = {0, 2, 3, 5, 7, 8, 10};

	public static final Note[] SHARP_NOTES = new Note[] {
		new Note('A', 0),
		new Note('A', 1),
		new Note('B', 0),
		new Note('C', 0),
		new Note('C', 1),
		new Note('D', 0),
		new Note('D', 1),
		new Note('E', 0),
		new Note('F', 0),
		new Note('F', 1),
		new Note('G', 0),
		new Note('G', 1),
	};

	public static final Note[] FLAT_NOTES = new Note[] {
		new Note('A', 0),
		new Note('B', -1),
		new Note('B', 0),
		new Note('C', 0),
		new Note('D', -1),
		new Note('D', 0),
		new Note('E', -1),
		new Note('E', 0),
		new Note('F', 0),
		new Note('G', -1),
		new Note('G', 0),
		new Note('A', -1),
	};
	
	/**
	 * The note name, only the letter, which determines the position on the staff
	 */
	public final char noteName;

	/**
	 * The modifier of the note, positive values for sharps
	 */
	public final int modifier;

	/**
	 * The octave the note resides in
	 */
	public final int octave;

	/**
	 * Number of half steps from A-0 (the "absolute zero" note apparently)
	 */
	public final int halfSteps;

	/**
	 * Represents the string as a qualified note name (ie "C", "A#, "Ebb", etc)
	 */
	public String toString() {
		String name = this.noteName + "";
		if (modifier == 1)
			name += SHARP;
		else if (modifier == 2)
			name += DOUBLE_SHARP;
		for (int i = 0; i > modifier; --i)
			name += FLAT;
		return name;
	}

	/**
	 * Compares the note with another note, by ascending order of pitch
	 */
	public int compareTo(Note other) {
		return new Integer(halfSteps).compareTo(other.halfSteps);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Note))
			return false;

		Note otherNote = (Note) other;
		return noteName == otherNote.noteName
			&& modifier == otherNote.modifier
			&& octave == otherNote.octave;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public boolean enharmonicallyEquals(Note other) {
		return halfSteps == other.halfSteps;
	}

	public boolean samePitchClass(Note other) {
		return halfSteps%12 == other.halfSteps%12;
	}
	
	public Note modify(int modifier) {
		return new Note(noteName, this.modifier+modifier, octave);
	}

	public Note(char name, int modifier, int octave) {
		this.noteName = name;
		this.modifier = modifier;
		this.octave = octave;
		
		int tempNote = 12 * octave + noteNamesToHalfSteps[name - 'A'] + modifier;
		if (tempNote < 0)
			tempNote += 12;
		halfSteps = tempNote;
	}
	
	public Note(char name, int modifier) {
		this(name, modifier, 0);
	}
}
