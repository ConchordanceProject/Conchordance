package com.conchordance;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.instrument.FretboardModel;
import com.conchordance.instrument.Instrument;
import com.conchordance.instrument.InstrumentBank;
import com.conchordance.instrument.RecursionBasedFretboardModel;
import com.conchordance.music.Chord;
import com.conchordance.music.ChordType;
import com.conchordance.music.ChordTypeBank;
import com.conchordance.music.Note;

public class Conchordance {

    public ChordFingering getChordFingering(String rootName, String chordTypeName, String frets, String instrumentName) {
        Chord chord = getChord(rootName, chordTypeName);

        Instrument instrument = instrumentBank.getInstrument(instrumentName);
        model.setInstrument(instrument);

        int[] fretPositions = new int[instrument.strings];
        for (int i = 0; i<fretPositions.length; ++i) {
            char fretChar = frets.charAt(i);
            if (fretChar == 'x')
                fretPositions[instrument.strings-i-1] = -1;
            else
                fretPositions[instrument.strings-i-1] = fretChar - '0';
        }

        return model.getChordFingering(chord, fretPositions);
    }
	
	public Chord getChord(String rootName, String chordTypeName) {
		Note root = Note.parse(rootName);
		ChordType type = chordTypeBank.getChordType(chordTypeName);
		return new Chord(root, type);
	}

	public ChordFingering[] getChords(String instrumentName, String rootName, String chordTypeName) {
		Instrument instrument = instrumentBank.getInstrument(instrumentName);
		model.setInstrument(instrument);
		
		Note root = Note.parse(rootName);
		ChordType type = chordTypeBank.getChordType(chordTypeName);
		Chord chord = new Chord(root, type);
		
		model.setChord(chord);
		
		ChordFingering[] chords = new ChordFingering[model.getChordList().getSize()];
		for (int i = 0; i<chords.length; ++i)
			chords[i] = model.getChordList().getElementAt(i);
		
		return chords;
	}

	public FretboardModel getFretboard(String instrumentName, String rootName, String chordTypeName) {
		Instrument instrument = instrumentBank.getInstrument(instrumentName);
		model.setInstrument(instrument);
		
		Note root = Note.parse(rootName);
		ChordType type = chordTypeBank.getChordType(chordTypeName);
		Chord chord = new Chord(root, type);
		
		model.setChord(chord);
		
		return model;
	}
	
	public Conchordance() {
		model = new RecursionBasedFretboardModel(Chord.A_MAJOR);
		instrumentBank = InstrumentBank.DEFAULT_BANK;
		chordTypeBank = ChordTypeBank.DEFAULT_BANK;
	}
	
	private FretboardModel model;
	
	private InstrumentBank instrumentBank;
	
	private ChordTypeBank chordTypeBank;
}
