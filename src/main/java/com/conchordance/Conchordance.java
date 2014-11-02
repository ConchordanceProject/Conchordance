package com.conchordance;

import com.conchordance.fretted.fingering.ChordFingering;
import com.conchordance.fretted.fingering.list.ChordListModel;
import com.conchordance.fretted.FretboardModel;
import com.conchordance.fretted.Instrument;
import com.conchordance.fretted.InstrumentBank;
import com.conchordance.fretted.RecursionBasedFretboardModel;
import com.conchordance.fretted.fingering.validation.ExactShapeValidator;
import com.conchordance.music.*;

import java.util.List;

public class Conchordance {

    public ChordFingering getChordFingering(String rootName, String chordTypeName, String frets, String instrumentName) throws MusicException {
        Chord chord = getChord(rootName, chordTypeName);

        Instrument instrument = instrumentBank.getInstrument(instrumentName);
        model.setInstrument(instrument);

        int[] fretPositions = parseFretsParameter(frets, instrument.strings);

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
        List<ChordFingering> chordList = model.calculateFingerings();

        ChordListModel chords = new ChordListModel();
        chords.setChords(chordList.toArray(new ChordFingering[chordList.size()]));

		return chords.toArray();
	}

    public ChordFingering[] getChords(String instrumentName, String rootName, String chordTypeName, String frets) throws MusicException {
        Instrument instrument = instrumentBank.getInstrument(instrumentName);
        model.setInstrument(instrument);

        Note root = Note.parse(rootName);
        ChordType type = chordTypeBank.getChordType(chordTypeName);
        Chord chord = new Chord(root, type);

        int[] fretPositions = parseFretsParameter(frets, instrument.strings);
        model.setChord(chord);
        List<ChordFingering> chordList = model.calculateFingerings(fretPositions);

        ChordListModel chords = new ChordListModel();
        chords.setFilterDuplicateShapes(false);
        chords.setValidator(new ExactShapeValidator(fretPositions));

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

    private int[] parseFretsParameter(String fretsParameter, int strings) {
        String[] parsedFrets = fretsParameter.split("-");
        int[] fretPositions = new int[strings];
        for (int string = 0; string<fretPositions.length; ++string) {
            String fretStr = parsedFrets[strings-string-1];
            if (fretStr.equals("x"))
                fretPositions[string] = -1;
            else
                fretPositions[string] = Integer.parseInt(fretStr);
        }
        return fretPositions;
    }
	
	private FretboardModel model;
	
	private InstrumentBank instrumentBank;
	
	private ChordTypeBank chordTypeBank;
}
