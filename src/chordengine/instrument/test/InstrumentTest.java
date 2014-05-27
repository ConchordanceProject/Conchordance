package chordengine.instrument.test;

import static org.junit.Assert.*;
import org.junit.Test;

import chordengine.fingers.list.ChordListModel;
import chordengine.instrument.FretboardModel;
import chordengine.instrument.Instrument;
import chordengine.instrument.RecursionBasedFretboardModel;
import chordengine.music.Chord;

public class InstrumentTest {

	@Test
	public void openChord() {
		Instrument openA = new Instrument("Open A", 1, Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[1], Chord.A_MAJOR.notes[2]);		
		FretboardModel model = new RecursionBasedFretboardModel(Chord.A_MAJOR);
		model.setInstrument(openA);
		
		ChordListModel list = model.getChordList();
		assertEquals("Only one chord found", 1, list.getSize());
	}
}
