package com.conchordance.music.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.conchordance.music.Note;


public class NoteTest {

	@Test
	public void test() {
		Note cFlat = new Note('C', -1, 0);
		Note b = new Note('B', 0, 0);

		assertTrue("Cb enharmonically equals B", cFlat.enharmonicallyEquals(b));

		Note c = new Note('C', 0, 0);
		Note bSharp = new Note('B', 1, 0);
		assertTrue("C enharmonically equals B#", c.enharmonicallyEquals(bSharp));

		Note cSharp = new Note('C', 1, 0);
		Note bDouble = new Note('B', 2, 0);
		assertTrue("C# enharmonically equals Bx", cSharp.enharmonicallyEquals(bDouble));
		
		Note cDouble = new Note('C', 2, 0);
		Note d = new Note('D', 0, 0);
		assertTrue("Cx enharmonically equals D", cDouble.enharmonicallyEquals(d));

		Note c1 = new Note('C', 0, 1);
		assertTrue("C0 lower than C1", c.compareTo(c1) == -1);

		assertTrue("C0 and C1 same pitch class", c.samePitchClass(c1));
		
		assertFalse("C and C# different pitch class", c.samePitchClass(cSharp));
		
		Note cPlusHalfStep = c.modify(1);
		assertTrue("C plus a half step equals C#", cSharp.equals(cPlusHalfStep));
	}

}
