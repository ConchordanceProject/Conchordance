package com.conchordance.music;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ChordTypeBank {
	
	public static final ChordTypeBank DEFAULT_BANK;
	
	static {
		DEFAULT_BANK = new ChordTypeBank();

		DEFAULT_BANK.addChordType(ChordType.MAJOR);
		DEFAULT_BANK.addChordType(new ChordType("m", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH}));
		DEFAULT_BANK.addChordType(new ChordType("7", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH, Interval.MINOR_SEVENTH}));
		DEFAULT_BANK.addChordType(new ChordType("M7", new Interval[]{Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH, Interval.MAJOR_SEVENTH}));
		DEFAULT_BANK.addChordType(new ChordType("m7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH, Interval.MINOR_SEVENTH}));
		DEFAULT_BANK.addChordType(new ChordType("m-M7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.PERFECT_FIFTH, Interval.MAJOR_SEVENTH}));
		DEFAULT_BANK.addChordType(new ChordType("dim7", new Interval[]{Interval.UNISON, Interval.MINOR_THIRD, Interval.DIMINISHED_FIFTH, new Interval(6, -2)}));
	}
	
	public ChordType getChordType(String name) {
		return chordTypeNames.get(name);
	}
	
	public void addChordType(ChordType chordType) {
		chordTypes.add(chordType);
		chordTypeNames.put(chordType.name, chordType);
	}
	
	public List<ChordType> getChordTypes() {
		return chordTypes;
	}
	
	public ChordTypeBank() {
		chordTypes = new LinkedList<>();
		chordTypeNames = new HashMap<>();
	}
	
	private LinkedList<ChordType> chordTypes;
	
	private HashMap<String, ChordType> chordTypeNames;
}
