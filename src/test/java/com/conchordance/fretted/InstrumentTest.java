package com.conchordance.fretted;

import static com.conchordance.music.NoteName.E;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.conchordance.fretted.fingering.RecursiveChordFingeringGenerator;
import com.conchordance.fretted.fingering.list.ChordListModel;
import com.conchordance.fretted.fingering.validation.ExactShapeValidator;
import com.conchordance.music.*;
import org.junit.Test;

import com.conchordance.fretted.fingering.ChordFingering;

import static com.conchordance.music.NoteName.*;


public class InstrumentTest {

	@Test
	public void testAlternateFingerings() {
		System.out.println("Alternate fingerings:");
		FretboardModel fretboard = new FretboardModel(Instrument.GUITAR, Chord.A_MAJOR);

		int[] aMajorFrets = new int[]{0, 2, 2, 2, 0, 0};

		List<ChordFingering> fingerings = new RecursiveChordFingeringGenerator().getChordFingerings(fretboard, aMajorFrets);
		ChordListModel chords = new ChordListModel();
		chords.setFilterDuplicateShapes(false);
		chords.setValidator(new ExactShapeValidator(aMajorFrets));
		chords.setChords(fingerings.toArray(new ChordFingering[fingerings.size()]));

		LinkedList<int[]> uniqueFingerings = new LinkedList<>();
		for (ChordFingering f : chords.toArray()) {
			for (int[] otherFingers : uniqueFingerings)
				assertFalse("No duplicate fingerings", Arrays.equals(otherFingers, f.fingers));
			uniqueFingerings.add(f.fingers);
		}
	}

    /**
     *
     */
    @Test
    public void testChordNoteLayout() {
        FretboardModel fretboard = new FretboardModel();

        Chord gMajor = new Chord(new Note(NoteName.G, 0), ChordType.MAJOR);
        Chord cMajor = new Chord(new Note(NoteName.C, 0), ChordType.MAJOR);

        // guitar
        fretboard.setInstrument(Instrument.GUITAR);
		fretboard.setChord(Chord.A_MAJOR);
        assertEquals("Guitar 1st string open", new Note(NoteName.E, 0, 5), fretboard.getChordNoteAt(0, 0).note);
        assertEquals("Guitar 1st string octave", new Note(NoteName.E, 0, 6), fretboard.getChordNoteAt(0, 12).note);
        assertEquals("Guitar 5th string octave", new Note(NoteName.A, 0, 3), fretboard.getChordNoteAt(4, 0).note);
        assertEquals("Guitar 6th string open", new Note(NoteName.E, 0, 3), fretboard.getChordNoteAt(5, 0).note);
        assertEquals("Guitar 6th string octave", new Note(NoteName.E, 0, 4), fretboard.getChordNoteAt(5, 12).note);

        // C's on guitar (do the octaves roll over correctly?)
        fretboard.setChord(cMajor);
        assertEquals("Guitar 5th 3rd fret", new Note(NoteName.C, 0, 4), fretboard.getChordNoteAt(4, 3).note);

        // 5-string banjo (irregular tuning pegs)
        fretboard.setInstrument(Instrument.BANJO);
        fretboard.setChord(Chord.A_MAJOR);
        assertEquals("Banjo 5th string 7th fret", new Note(NoteName.A, 0, 5), fretboard.getChordNoteAt(4, 7).note);

        fretboard.setChord(gMajor);
        assertEquals("Banjo 5th string open (5th fret)", new Note(NoteName.G, 0, 5), fretboard.getChordNoteAt(4, 5).note);

        // guitar, half-capo

        // guitar, multiple capos

        // Banjo with capo
    }

	/**
	 * Tests that obvious fully open chords are produced by the fretboard
	 */
	@Test
	public void openChord() {
		Instrument openA = new Instrument("Open A", 1, Clef.TREBLE, Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[0], Chord.A_MAJOR.notes[1], Chord.A_MAJOR.notes[2]);
		FretboardModel fretboard = new FretboardModel(openA, Chord.A_MAJOR);
		
		List<ChordFingering> chords = new RecursiveChordFingeringGenerator().getChordFingerings(fretboard);
		assertEquals("Only one chord found", 1, chords.size());
	}
	
	/**
	 * This tests that the fretboard produces all the classic "campfire" chords.
	 * 
	 * If all the most common fingerings for chords aren't produced the generator isn't very useful.
	 */
	@Test
	public void campfireChords() {
		FretboardModel fretboard = new FretboardModel(Instrument.GUITAR, Chord.A_MAJOR);

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
	
	private void testFingering(String name, int[] frets, Chord chord, FretboardModel fretboard) {
		fretboard.setChord(chord);
		boolean found = false;
		
		List<ChordFingering> chords = new RecursiveChordFingeringGenerator().getChordFingerings(fretboard);
		for (ChordFingering fingering : chords) {
			if (Arrays.equals(frets, fingering.absoluteFrets)) {
				found = true;
				break;
			}
		}
		
		assertTrue(name + " not found", found);
	}
}
