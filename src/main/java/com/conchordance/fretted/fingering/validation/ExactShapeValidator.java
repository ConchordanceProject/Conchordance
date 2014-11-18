package com.conchordance.fretted.fingering.validation;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.music.Chord;

import java.util.Arrays;

public class ExactShapeValidator implements ChordFingeringValidator {

    public boolean validate(ChordFingering candidate, Chord compareTo) {
        return Arrays.equals(candidate.capoRelativeFrets, relativeFrets);
    }

    public ExactShapeValidator(int[] relativeFrets) {
        this.relativeFrets = relativeFrets;
    }

    private int[] relativeFrets;
}
