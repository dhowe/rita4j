package rita.test;


import static org.junit.Assert.*;
//import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import rita.RiTa;

public class RiTaTests {

	@Test
	public void testStem() {
		String[] tests = {
				"boy", "boy",
				"boys", "boy",
				"biophysics", "biophysics",
				"automata", "automaton",
				"genus", "genus",
				"emus", "emu",
				"cakes", "cake",
				"run", "run",
				"runs", "run",
				"running", "running",
				"take", "take",
				"takes", "take",
				"taking", "taking",
				"hide", "hide",
				"hides", "hide",
				"hiding", "hiding",
				"become", "become",
				"becomes", "become",
				"becoming", "becoming",
				"gases", "gas",
				"buses", "bus",
				"happiness", "happiness",
				"terrible", "terrible"
		};
		for (int i = 0; i < tests.length; i += 2) {
			// System.out.println("p: " + RiTa.singularize(tests[i]) + " s: " + tests[i +
			// 1]);
			assertEquals(RiTa.stem(tests[i]), tests[i + 1]);
		}

	}

	@Test
	public void testRandomOrdering() {
		int[] result = new int[]{0};
		assertArrayEquals(RiTa.randomOrdering(1), result);
		int[] result2 = new int[]{0,1};
		int[] ro = RiTa.randomOrdering(2);
		Arrays.sort(ro);
		assertArrayEquals(ro, result2);
	  // expect(RiTa.randomOrdering(['a'])).eql(['a']);
	  // expect(RiTa.randomOrdering(['a', 'b'])).to.have.members(['a', 'b']);
	}

	@Test
	public void testIsQuestion() {
		assertTrue(RiTa.isQuestion("What"));
		assertTrue(RiTa.isQuestion("what"));
		assertTrue(RiTa.isQuestion("what is this"));
		assertTrue(RiTa.isQuestion("what is this?"));
		assertTrue(RiTa.isQuestion("Does it?"));
		assertTrue(RiTa.isQuestion("Would you believe it?"));
		assertTrue(RiTa.isQuestion("Have you been?"));
		assertTrue(RiTa.isQuestion("Is this yours?"));
		assertTrue(RiTa.isQuestion("Are you done?"));
		assertTrue(RiTa.isQuestion("what is this? , where is that?"));
		assertTrue(!RiTa.isQuestion("That is not a toy This is an apple"));
		assertTrue(!RiTa.isQuestion("string"));
		assertTrue(!RiTa.isQuestion("?"));
		assertTrue(!RiTa.isQuestion(""));
	}

	@Test
	public void testIsAbbreviation() { // TODO add second parameter tests

		assertTrue(RiTa.isAbbreviation("Dr."));
		assertTrue(!RiTa.isAbbreviation("dr."));
		// T in java

		assertTrue(!RiTa.isAbbreviation("DR."));
		// F in Processing.lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("Dr. "));
		// space
		assertTrue(!RiTa.isAbbreviation(" Dr."));
		// space
		assertTrue(!RiTa.isAbbreviation("  Dr."));
		// double space
		assertTrue(!RiTa.isAbbreviation("Dr.  "));
		// double space
		assertTrue(!RiTa.isAbbreviation("   Dr."));
		// tab space
		assertTrue(!RiTa.isAbbreviation("Dr.    "));
		// tab space
		assertTrue(!RiTa.isAbbreviation("Dr"));
		assertTrue(!RiTa.isAbbreviation("Doctor"));
		assertTrue(!RiTa.isAbbreviation("Doctor."));

		assertTrue(RiTa.isAbbreviation("Prof."));
		assertTrue(!RiTa.isAbbreviation("prof."));
		// T in java
		assertTrue(!RiTa.isAbbreviation("PRFO."));
		// F in Processing. lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("PrFo."));
		// F in Processing. lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("Professor"));
		assertTrue(!RiTa.isAbbreviation("professor"));
		assertTrue(!RiTa.isAbbreviation("PROFESSOR"));
		assertTrue(!RiTa.isAbbreviation("Professor."));

		assertTrue(!RiTa.isAbbreviation("@#$%^&*()"));

		assertTrue(!RiTa.isAbbreviation(""));
		assertTrue(!RiTa.isAbbreviation(null));
		// assertTrue(!RiTa.isAbbreviation(undefined)); //no undefined in JAVA
		// assertTrue(!RiTa.isAbbreviation(1)); //no wrong datatype in JAVA
	}

	@Test
	public void testIsPunctuation() {

		assertTrue(!RiTa.isPunctuation("What the"));
		assertTrue(!RiTa.isPunctuation("What ! the"));
		assertTrue(!RiTa.isPunctuation(".#\"\\!@i$%&}<>"));

		assertTrue(RiTa.isPunctuation("!"));
		assertTrue(RiTa.isPunctuation("?"));
		assertTrue(RiTa.isPunctuation("?!"));
		assertTrue(RiTa.isPunctuation("."));
		assertTrue(RiTa.isPunctuation(".."));
		assertTrue(RiTa.isPunctuation("..."));
		assertTrue(RiTa.isPunctuation("...."));
		assertTrue(RiTa.isPunctuation("%..."));

		assertTrue(!RiTa.isPunctuation("! "));
		// space
		assertTrue(!RiTa.isPunctuation(" !"));
		// space
		assertTrue(!RiTa.isPunctuation("!  "));
		// double space
		assertTrue(!RiTa.isPunctuation("  !"));
		// double space
		assertTrue(!RiTa.isPunctuation("!  "));
		// tab space
		assertTrue(!RiTa.isPunctuation("   !"));

		String punct;

		punct = "$%&^,";
		String[] punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		punct = ",;:!?)([].#\"\\!@$%&}<>|+=-_\\/*{^";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		// TODO: also test multiple characters strings here ****
		punct = "\"��������`'";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		punct = "\"��������`',;:!?)([].#\"\\!@$%&}<>|+=-_\\/*{^";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		// TODO: and here...
		String nopunct = "Helloasdfnals  FgG   \t kjdhfakjsdhf askjdfh aaf98762348576";
		punctArr = nopunct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(!RiTa.isPunctuation(punctArr[i]));
		}

		assertTrue(!RiTa.isPunctuation(""));

	}

	@Test
	public void testConcordance() {
		// TODO
	}

}
