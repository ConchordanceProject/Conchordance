package com.conchordance.instrument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.conchordance.music.Note;
import static com.conchordance.music.NoteName.*;

public class InstrumentBank {

	/**
	 * Default set of instruments available.
	 */
	public static final InstrumentBank DEFAULT_BANK;
	
	static {
		DEFAULT_BANK = new InstrumentBank();
		DEFAULT_BANK.addInstrument(Instrument.GUITAR);
        DEFAULT_BANK.addInstrument(new Instrument("Guitar - DADGAD", 14,
                new Note[] {new Note(D, 0, 5), new Note(A, 0, 4), new Note(G, 0, 4), new Note(D, 0, 4), new Note(A, 0, 3), new Note(D, 0, 3)}));
        DEFAULT_BANK.addInstrument(new Instrument("Guitar - DADF#AD", 14,
                new Note[] {new Note(D, 0, 5), new Note(A, 0, 4), new Note(F, 1, 4), new Note(D, 0, 4), new Note(A, 0, 3), new Note(D, 0, 3)}));
		DEFAULT_BANK.addInstrument(new Instrument("Guitar - Drop D", 14,
				new Note[] {new Note(E, 0, 5), new Note(B, 0, 4), new Note(G, 0, 4), new Note(D, 0, 4), new Note(A, 0, 3), new Note(D, 0, 3)}));
		DEFAULT_BANK.addInstrument(new Instrument("Irish Bouzouki", 12,
				new Note[] {new Note(D, 0, 5), new Note(A, 0, 4), new Note(D, 0, 4), new Note(G, 0, 3)}));
		DEFAULT_BANK.addInstrument(new Instrument("Mandolin", 12,
				new Note[] {new Note(E, 0, 5), new Note(A, 0, 4), new Note(D, 0, 4), new Note(G, 0, 3)}));
		DEFAULT_BANK.addInstrument(Instrument.BANJO);
	}

	public void addInstrument(Instrument i) {
		instrumentNames.put(i.name, i);
		instruments.add(i);
	}
	
	public Instrument getInstrument(String name) {
		return instrumentNames.get(name);
	}
	
	public List<Instrument> getInstruments() {
		return instruments;
	}
	
	public InstrumentBank() {
		instrumentNames = new HashMap<>();
		instruments = new LinkedList<>();
	}
	
	private HashMap<String, Instrument> instrumentNames;
	private LinkedList<Instrument> instruments;
}
