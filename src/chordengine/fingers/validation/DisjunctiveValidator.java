package chordengine.fingers.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import chordengine.fingers.ChordFingering;
import chordengine.music.Chord;

public class DisjunctiveValidator implements ChordFingeringValidator {
	public boolean validate(ChordFingering candidate, Chord compareTo) {
		for (ChordFingeringValidator v : activeValidators) {
			if (v.validate(candidate, compareTo))
				return true;
		}
		return false;
	}
	
	public boolean isActive(ChordFingeringValidator v) {
		return activeValidators.contains(v);
	}
	
	public Iterable<ChordFingeringValidator> getValidators() {
		return new Iterable<ChordFingeringValidator>() {
			public Iterator<ChordFingeringValidator> iterator() {
				return validators.iterator();
			}
		};
	}

	public void toggleValidator(ChordFingeringValidator v, boolean active) {
		if (active)
			activeValidators.add(v);
		else
			activeValidators.remove(v);
	}
	
	public String toString() {
		return groupName;
	}
	
	public DisjunctiveValidator(String groupName, Collection<ChordFingeringValidator> validators) {
		this.groupName = groupName;
		activeValidators = new HashSet<ChordFingeringValidator>();
		this.validators = new ArrayList<ChordFingeringValidator>();
		this.validators.addAll(validators);
	}
	
	private String groupName;
	private HashSet<ChordFingeringValidator> activeValidators;
	private ArrayList<ChordFingeringValidator> validators;
}
