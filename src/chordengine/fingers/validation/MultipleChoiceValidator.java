package chordengine.fingers.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class MultipleChoiceValidator implements ChordFingeringValidator{
	public boolean validate(ChordFingering candidate, Chord compareTo) {
		return activeValidator.validate(candidate, compareTo);
	}
	
	public boolean isActive(ChordFingeringValidator v) {
		return v == activeValidator;
	}

	public Iterable<ChordFingeringValidator> getValidators() {
		return new Iterable<ChordFingeringValidator>() {
			public Iterator<ChordFingeringValidator> iterator() {
				return validators.iterator();
			}
		};
	}
	
	public void setActiveValidator(ChordFingeringValidator v) {
		activeValidator = v;
	}
	
	public String toString() {
		return groupName;
	}
	
	public MultipleChoiceValidator(String groupName, Collection<ChordFingeringValidator> validators, ChordFingeringValidator activeValidator) {
		this.activeValidator = activeValidator;
		this.groupName = groupName;
		this.validators = new ArrayList<ChordFingeringValidator>();
		this.validators.addAll(validators);
	}
	
	private String groupName;
	private ChordFingeringValidator activeValidator;
	private ArrayList<ChordFingeringValidator> validators;
}
