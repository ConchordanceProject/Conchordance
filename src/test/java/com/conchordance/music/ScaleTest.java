package com.conchordance.music;

import org.junit.Test;
import java.text.MessageFormat;
import static org.junit.Assert.assertEquals;
import static com.conchordance.music.NoteName.*;


public class ScaleTest {

	@Test
	public void testGetMajorScale() {
		String cScaleString = Scale.getMajorScale(new Note(C, 0)).toString();
		assertEquals("C Scale", "C0, D0, E0, F0, G0, A0, B0", cScaleString);

		String bScaleString = Scale.getMajorScale(new Note(B, 0)).toString();
		String bScaleManual = MessageFormat.format("B0, C{0}1, D{0}1, E1, F{0}1, G{0}1, A{0}1", Note.SHARP);
		assertEquals("B Scale", bScaleManual, bScaleString);
	}

	@Test
	public void testApplyIntervals() {
		Scale cScale = Scale.getMajorScale(new Note(C, 0));
		Note[] cMajor = cScale.applyIntervals(Interval.UNISON, Interval.MAJOR_THIRD, Interval.PERFECT_FIFTH);
		assertEquals("Major chord intervals applied to C scale", new Note(C, 0), cMajor[0]);
		assertEquals("Major chord intervals applied to C scale", new Note(E, 0), cMajor[1]);
		assertEquals("Major chord intervals applied to C scale", new Note(G, 0), cMajor[2]);
	}

	@Test
	public void testOctaves() {
		Scale aScale = Scale.getMajorScale(new Note(A, 0));
		Note c = aScale.getNote(3);
		assertEquals("Octave rolls over at C", new Note(C, 1, 1), c);
	}

	@Test
	public void testApplyInterval() {
		Scale cScale = Scale.getMajorScale(new Note(C, 0));

		Note cFifth = cScale.applyInterval(Interval.PERFECT_FIFTH);
		assertEquals("Fifth degree of C major scale is G", new Note(G, 0), cFifth);
		
		Note cFlatFlatSeven = cScale.applyInterval(new Interval(6, -2));
		assertEquals("Double-flat-Seventh degree of C major scale is Bbb", new Note(B, -2), cFlatFlatSeven);
	}
}
