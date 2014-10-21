package com.conchordance.instrument;

import static com.conchordance.music.NoteName.E;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import com.conchordance.music.NoteName;
import org.junit.Test;

import com.conchordance.fingers.ChordFingering;
import com.conchordance.fingers.list.ChordListModel;
import com.conchordance.music.Chord;
import com.conchordance.music.ChordType;
import com.conchordance.music.Note;
import static com.conchordance.music.NoteName.*;


public class InstrumentTest {

    /**
     *
     */
    @Test
    public void testChordNoteLayout() {
        FretboardModel model = new RecursionBasedFretboardModel(Chord.A_MAJOR);

        Chord gMajor = new Chord(new Note(NoteName.G, 0), ChordType.MAJOR);
        Chord cMajor = new Chord(new Note(NoteName.C, 0), ChordType.MAJOR);

        // guitar
        model.setInstrument(Instrument.GUITAR);
        assertEquals("Guitar 1st string open", new Note(NoteName.E, 0, 5), model.getChordNoteAt(0, 0).note);
        assertEquals("Guitar 1st string octave", new Note(NoteName.E, 0, 6), model.getChordNoteAt(0, 12).note);
        assertEquals("Guitar 5th string octave", new Note(NoteName.A, 0, 3), model.getChordNoteAt(4, 0).note);
        assertEquals("Guitar 6th string open", new Note(NoteName.E, 0, 3), model.getChordNoteAt(5, 0).note);
        assertEquals("Guitar 6th string octave", new Note(NoteName.E, 0, 4), model.getChordNoteAt(5, 12).note);

        // C's on guitar (do the octaves roll over correctly?)
        model.setChord(cMajor);
        assertEquals("Guitar 5th 3rd fret", new Note(NoteName.C, 0, 4), model.getChordNoteAt(4, 3).note);

        // 5-string banjo (irregular tuning pegs)
        model.setInstrument(Instrument.BANJO);
        model.setChord(Chord.A_MAJOR);
        assertEquals("Banjo 5th string 7th fret", new Note(NoteName.A, 0, 5), model.getChordNoteAt(4, 7).note);

        model.setChord(gMajor);
        assertEquals("Banjo 5th string open (5th fret)", new Note(NoteName.G, 0, 5), model.getChordNoteAt(4, 5).note);

        // guitar, half-capo

        // guitar, multiple capos

        // Banjo with capo
    }

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
		testFingering("A-form A major", new int[] {0, 2, 2, 2, 0, -1}, new Chord(new Note(A, 0), ChordType.MAJOR), fretboard);
		testFingering("A-form B major", new int[] {2, 4, 4, 4, 2, -1}, new Chord(new Note(B, 0), ChordType.MAJOR), fretboard);
		
		// Movable E chords
		testFingering("E-form E major", new int[] {0, 0, 1, 2, 2, 0}, new Chord(new Note(E, 0), ChordType.MAJOR), fretboard);
		testFingering("E-form E major / octave", new int[] {12, 12, 13, 14, 14, 12}, new Chord(new Note(E, 0), ChordType.MAJOR), fretboard);
		testFingering("F-form E major", new int[] {1, 1, 2, 3, 3, 1}, new Chord(new Note(F, 0), ChordType.MAJOR), fretboard);
		testFingering("G-form E major", new int[] {3, 3, 4, 5, 5, 3}, new Chord(new Note(G, 0), ChordType.MAJOR), fretboard);
		
		// Open position
		testFingering("Open C major", new int[] {0, 1, 0, 2, 3, -1}, new Chord(new Note(C, 0), ChordType.MAJOR), fretboard);
		testFingering("Open D major", new int[] {2, 3, 2, 0, -1, -1}, new Chord(new Note(D, 0), ChordType.MAJOR), fretboard);
		testFingering("Open G major", new int[] {3, 0, 0, 0, 2, 3}, new Chord(new Note(G, 0), ChordType.MAJOR), fretboard);
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
