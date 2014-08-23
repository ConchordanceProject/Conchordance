package com.conchordance.fingers.validation;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.music.Chord;


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
