package chordengine.instrument.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import chordengine.fingers.ChordFingering;
import chordengine.fingers.list.ChordListModel;
import chordengine.instrument.FretboardModel;
import chordengine.instrument.Instrument;
import chordengine.instrument.RecursionBasedFretboardModel;
import chordengine.music.Chord;
import chordengine.music.ChordQuality;
import chordengine.music.Note;

public class InstrumentTest {

	/**
	 * Tests that obvious fully open chords are produced by the fretboard
	 */
	@Test
	public void openChord() {
		Instrument openA = new Instrument("Open A", 1, Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[1], Chord.A_MAJOR.notes[2]);		
		FretboardModel model = new RecursionBasedFretboardModel(Chord.A_MAJOR);
		model.setInstrument(openA);
		
		ChordListModel list = model.getChordList();
		assertEquals("Only one chord found", 1, list.getSize());
	}
	
	/**
	 * This tests that the fretboard produces all the classic "campfire" chords.
	 * 
	 * If all the most common fingerings for chords aren't produced the generator isn't very useful.
	 */
	@Test
	public void campfireChords() {
		FretboardModel fretboard = new RecursionBasedFretboardModel(Chord.A_MAJOR);

		// Movable A chords
		testFingering("A-form A major", new int[] {0, 2, 2, 2, 0, -1}, new Chord(new Note('A', 0), ChordQuality.MAJOR), fretboard);
		testFingering("A-form B major", new int[] {2, 4, 4, 4, 2, -1}, new Chord(new Note('B', 0), ChordQuality.MAJOR), fretboard);
		
		// Movable E chords
		testFingering("E-form E major", new int[] {0, 0, 1, 2, 2, 0}, new Chord(new Note('E', 0), ChordQuality.MAJOR), fretboard);
		testFingering("E-form E major / octave", new int[] {12, 12, 13, 14, 14, 12}, new Chord(new Note('E', 0), ChordQuality.MAJOR), fretboard);
		testFingering("F-form E major", new int[] {1, 1, 2, 3, 3, 1}, new Chord(new Note('F', 0), ChordQuality.MAJOR), fretboard);
		testFingering("G-form E major", new int[] {3, 3, 4, 5, 5, 3}, new Chord(new Note('G', 0), ChordQuality.MAJOR), fretboard);
		
		// Open position
		testFingering("Open C major", new int[] {0, 1, 0, 2, 3, -1}, new Chord(new Note('C', 0), ChordQuality.MAJOR), fretboard);
		testFingering("Open D major", new int[] {2, 3, 2, 0, -1, -1}, new Chord(new Note('D', 0), ChordQuality.MAJOR), fretboard);
		testFingering("Open G major", new int[] {3, 0, 0, 0, 2, 3}, new Chord(new Note('G', 0), ChordQuality.MAJOR), fretboard);
	}
	
	private void testFingering(String name, int[] frets, Chord chord, FretboardModel model) {
		model.setChord(chord);
		boolean found = false;
		
		ChordListModel chords = model.getChordList();
		for (int i = 0; i<chords.getSize(); ++i) {
			ChordFingering fingering = chords.getElementAt(i);
			if (Arrays.equals(frets, fingering.absoluteFrets)) {
				found = true;
				break;
			}
		}
		
		assertTrue(name + " not found", found);
	}
}
