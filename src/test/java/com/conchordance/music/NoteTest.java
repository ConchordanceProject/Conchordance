package com.conchordance.music;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class NoteTest {

	@Test
	public void test() {
		Note cFlat = new Note('C', -1, 4);
		Note b = new Note('B', 0, 3);
		assertTrue("Cb enharmonically equals B", cFlat.enharmonicallyEquals(b));

		Note c = new Note('C', 0, 4);
		Note bSharp = new Note('B', 1, 3);
		assertTrue("C enharmonically equals B#", c.enharmonicallyEquals(bSharp));

		Note cSharp = new Note('C', 1, 4);
		Note bDouble = new Note('B', 2, 3);
		assertTrue("C# enharmonically equals Bx", cSharp.enharmonicallyEquals(bDouble));
		
		Note cDouble = new Note('C', 2, 4);
		Note d = new Note('D', 0, 4);
		assertTrue("Cx enharmonically equals D", cDouble.enharmonicallyEquals(d));

		Note c0 = new Note('C', 0, 0);
		Note c1 = new Note('C', 0, 1);
		assertTrue("C0 lower than C1", c0.compareTo(c1) == -1);

		assertTrue("C0 and C1 same pitch class", c.samePitchClass(c1));
		
		assertFalse("C and C# different pitch class", c.samePitchClass(cSharp));
		
		Note cPlusHalfStep = c.modify(1);
		assertTrue("C plus a half step equals C#", cSharp.equals(cPlusHalfStep));
	}

}
