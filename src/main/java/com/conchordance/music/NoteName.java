package com.conchordance.music;

public enum NoteName {
    C(0),
    D(2),
    E(4),
    F(5),
    G(7),
    A(9),
    B(11);

    static NoteName fromChar(char c) {
        switch(c) {
            case 'A': return A;
            case 'B': return B;
            case 'C': return C;
            case 'D': return D;
            case 'E': return E;
            case 'F': return F;
            case 'G': return G;
            default: return null;
        }
    }

    public final int halfStepsFromC;

    public NoteName offset(int offset) {
        return values()[(ordinal() + offset) % values().length];
    }

    NoteName(int stepsFromC) {
        halfStepsFromC = stepsFromC;
    }
}
