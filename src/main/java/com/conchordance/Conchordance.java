package com.conchordance;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.fingers.list.ChordListModel;
import com.conchordance.instrument.FretboardModel;
import com.conchordance.instrument.Instrument;
import com.conchordance.instrument.InstrumentBank;
import com.conchordance.instrument.RecursionBasedFretboardModel;
import com.conchordance.music.*;

import java.util.List;

public class Conchordance {

    public ChordFingering getChordFingering(String rootName, String chordTypeName, String frets, String instrumentName) throws MusicException {
        Chord chord = getChord(rootName, chordTypeName);

        Instrument instrument = instrumentBank.getInstrument(instrumentName);
        model.setInstrument(instrument);

        String[] parsedFrets = frets.split("-");
        int[] fretPositions = new int[instrument.strings];
        for (int s = 0; s<fretPositions.length; ++s) {
            String fretStr = parsedFrets[instrument.strings-s-1];
            if (fretStr.equals("x"))
                fretPositions[s] = -1;
            else
                fretPositions[s] = Integer.parseInt(fretStr);
        }

        return model.getChordFingering(chord, fretPositions);
    }
	
	public Chord getChord(String rootName, String chordTypeName) throws MusicException {
		Note root = Note.parse(rootName);
		ChordType type = chordTypeBank.getChordType(chordTypeName);
		return new Chord(root, type);
	}

	public ChordFingering[] getChords(String instrumentName, String rootName, String chordTypeName) throws MusicException {
		Instrument instrument = instrumentBank.getInstrument(instrumentName);
		model.setInstrument(instrument);
		
		Note root = Note.parse(rootName);
		ChordType type = chordTypeBank.getChordType(chordTypeName);
		Chord chord = new Chord(root, type);
		
		model.setChord(chord);
        List<ChordFingering> chordList = model.calculateChords();

        ChordListModel chords = new ChordListModel();
        chords.setChords(chordList.toArray(new ChordFingering[chordList.size()]));

		return chords.toArray();
	}

	public FretboardModel getFretboard(String instrumentName, String rootName, String chordTypeName) throws MusicException {
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
