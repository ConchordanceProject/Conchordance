package chordengine.fingers.validation;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class TrivialValidator implements ChordFingeringValidator {
	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return true;
	}
	
	public String toString() {
		return name;
	}
	
	public TrivialValidator(String name) {
		this.name = name;
	}
	
	public TrivialValidator() {
		this("Any Chord");
	}
	
	private String name;
}
