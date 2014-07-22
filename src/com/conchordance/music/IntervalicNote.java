package com.conchordance.music;

public class IntervalicNote implements Comparable<IntervalicNote> {
	public final Interval interval;
	public final Note note;

	public int compareTo(IntervalicNote other) {
		return note.compareTo(other.note);
	}
	
	public IntervalicNote(Interval i, Note n) {
		interval = i;
		note = n;
	}
}
