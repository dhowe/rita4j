package rita.test;

import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RiTaTests {

	@Test
	public void accessStaticConstantAndFunction() {
		assertEquals("2", RiTa.VERSION);
		assertTrue(RiTa.hasWord("dog"));
	}

	@Test
	public void callStem() {
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
			assertEquals(tests[i + 1], RiTa.stem(tests[i]));
		}

	}

	@Test
	public void callRandomOrdering() {
		//int
		int[] result = new int[] { 0 };
		assertArrayEquals(result, RiTa.randomOrdering(1));
		int[] result2 = new int[] { 0, 1 };
		int[] ro = RiTa.randomOrdering(2);
		Arrays.sort(ro);
		assertArrayEquals(result2, ro);
		// expect(RiTa.randomOrdering(['a'])).	['a']);
		// expect(RiTa.randomOrdering(['a', 'b'])).to.have.members(['a', 'b']);
		// not in Java yet

		//List <Float>
		List<Float> resLF = new ArrayList<Float>();
		resLF.add(Float.valueOf(0));
		List<Float> outputLF = RandGen.randomOrdering(resLF);
		assertTrue(listEq(resLF, outputLF));

		resLF.add(Float.valueOf((float) 0.6));
		resLF.add(Float.valueOf(1));
		outputLF = RandGen.randomOrdering(resLF);
		Collections.sort(resLF);
		Collections.sort(outputLF);
		assertTrue(listEq(resLF, outputLF));

		//List <Int>
		List<Integer> resLI = new ArrayList<Integer>();
		resLI.add(Integer.valueOf(0));
		List<Integer> outputLI = RandGen.randomOrdering(resLI);
		assertTrue(listEq(resLI, outputLI));

		resLI.add(Integer.valueOf(1));
		resLI.add(Integer.valueOf(2));
		outputLI = RandGen.randomOrdering(resLI);
		Collections.sort(resLI);
		Collections.sort(outputLI);
		assertTrue(listEq(resLI, outputLI));

		//List <Boolean>
		List<Boolean> resLB = new ArrayList<Boolean>();
		resLB.add(Boolean.valueOf(false));
		List<Boolean> outputLB = RandGen.randomOrdering(resLB);
		assertTrue(listEq(resLB, outputLB));

		resLB.add(Boolean.valueOf(false));
		resLB.add(Boolean.valueOf(true));
		outputLB = RandGen.randomOrdering(resLB);
		Collections.sort(resLB);
		Collections.sort(outputLB);
		assertTrue(listEq(resLB, outputLB));

		//List <Double>
		List<Double> resLD = new ArrayList<Double>();
		resLD.add(Double.valueOf(0.0));
		List<Double> outputLD = RandGen.randomOrdering(resLD);
		assertTrue(listEq(resLD, outputLD));

		resLD.add(Double.valueOf(1));
		resLD.add(Double.valueOf(2));
		outputLD = RandGen.randomOrdering(resLD);
		Collections.sort(resLD);
		Collections.sort(outputLD);
		assertTrue(listEq(resLD, outputLD));

		//List <T> T is other kind of finalized object
		List<String> resLO = new ArrayList<String>();
		resLO.add("a");
		List<String> outputLO = RandGen.randomOrdering(resLO);
		assertTrue(listEq(resLO, outputLO));

		resLO.add("b");
		resLO.add("c");
		outputLO = RandGen.randomOrdering(resLO);
		Collections.sort(resLO);
		Collections.sort(outputLO);
		assertTrue(listEq(resLO, outputLO));

		//int[]
		int[] resI = new int[] { 0 };
		int[] outputI = RandGen.randomOrdering(resI);
		assertArrayEquals(resI, outputI);
		resI = new int[] { 0, 1, 2, 3, 4 };
		outputI = RandGen.randomOrdering(resI);
		Arrays.sort(outputI);
		assertArrayEquals(resI, outputI);

		//float[]
		float[] resF = new float[] { 0 };
		float[] outputF = RandGen.randomOrdering(resF);
		assertArrayEquals(resF, outputF);
		resF = new float[] { 0, 1, 2, 3, (float) 4.554 };
		outputF = RandGen.randomOrdering(resF);
		Arrays.sort(outputF);
		assertArrayEquals(resF, outputF);

		//double[] 
		double[] resD = new double[] { 0 };
		double[] outputD = RandGen.randomOrdering(resD);
		assertArrayEquals(resD, outputD);
		resD = new double[] { 0, 1, 2, 3, 4 };
		outputD = RandGen.randomOrdering(resD);
		Arrays.sort(outputD);
		assertArrayEquals(resD, outputD);

		//boolean[]
		boolean[] resB = new boolean[] { false };
		boolean[] outputB = RandGen.randomOrdering(resB);
		assertArrayEquals(resB, outputB);
		resB = new boolean[] { false, false, false, false };
		outputB = RandGen.randomOrdering(resB);
		assertArrayEquals(resB, outputB);

		//T[] T is final object
		String[] resO = new String[] { "a" };
		String[] outputO = RandGen.randomOrdering(resO);
		assertArrayEquals(resO, outputO);
		resO = new String[] { "a", "b", "c" };
		outputO = RandGen.randomOrdering(resO);
		Arrays.sort(outputO);
		assertArrayEquals(resO, outputO);
	}

	@Test
	public void callIsQuestion() {
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
	public void callArticlize() {
		String[] data = {
				"a dog", "dog",
				"an ant", "ant",
				"an honor", "honor",
				"an eagle", "eagle",
				"an ermintrout", "ermintrout"
		};
		for (int i = 0; i < data.length; i += 2) {
			assertEquals(data[i], RiTa.articlize(data[i + 1]));
		}
	}

	@Test
	public void callIsAbbreviation() { // TODO add second parameter tests

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
	public void callIsPunctuation() {

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
	public void callTokenize() {
		String[] expect = { "" };
		assertArrayEquals(expect, RiTa.tokenize(""));
		expect = new String[] { "The", "dog" };
		assertArrayEquals(expect, RiTa.tokenize("The dog"));

		String[] input = {
				"The student said 'learning is fun'",
				"\"Oh God,\" he thought.",
				"The boy, dressed in red, ate an apple.",
				"why? Me?huh?!",
				"123 123 1 2 3 1,1 1.1 23.45.67 22/05/2012 12th May,2012",
				"The boy screamed, \"Where is my apple?\"",
				"The boy screamed, \u201CWhere is my apple?\u201D",
				"The boy screamed, 'Where is my apple?'",
				"The boy screamed, \u2018Where is my apple?\u2019",
				"dog, e.g. the cat.",
				"dog, i.e. the cat.",
				"What does e.g. mean? E.g. is used to introduce a few examples, not a complete list.",
				"What does i.e. mean? I.e. means in other words.",
				"it cost $30",
				"calculate 2^3",
				"30% of the students",
				"A simple sentence.",
				"that's why this is our place).",
				"most, punctuation; is. split: from! adjoining words?",
				"double quotes \"OK\"",
				"face-to-face class",
				"\"it is strange\", said John, \"Katherine does not drink alchol.\"",
				"\"What?!\", John yelled.",
				"more abbreviations: a.m. p.m. Cap. c. et al. etc. P.S. Ph.D R.I.P vs. v. Mr. Ms. Dr. Pf. Mx. Ind. Inc. Corp. Co.,Ltd. Co., Ltd. Co. Ltd. Ltd. Prof.",
				"elipsis dots... another elipsis dots…",
				"(testing) [brackets] {all} ⟨kinds⟩",
		};

		String[][] output = {
				new String[] { "The", "student", "said", "'", "learning", "is", "fun", "'" },
				new String[] { "\"", "Oh", "God", ",", "\"", "he", "thought", "." },
				new String[] { "The", "boy", ",", "dressed", "in", "red", ",", "ate", "an", "apple", "." },
				new String[] { "why", "?", "Me", "?", "huh", "?", "!" },
				new String[] { "123", "123", "1", "2", "3", "1", ",", "1", "1", ".", "1", "23", ".", "45", ".", "67", "22/05/2012", "12th", "May",
						",", "2012" },
				new String[] { "The", "boy", "screamed", ",", "\"", "Where", "is", "my", "apple", "?", "\"" },
				new String[] { "The", "boy", "screamed", ",", "\u201C", "Where", "is", "my", "apple", "?", "\u201D" },
				new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" },
				new String[] { "The", "boy", "screamed", ",", "\u2018", "Where", "is", "my", "apple", "?", "\u2019" },
				new String[] { "dog", ",", "e.g.", "the", "cat", "." },
				new String[] { "dog", ",", "i.e.", "the", "cat", "." },
				new String[] { "What", "does", "e.g.", "mean", "?", "E.g.", "is", "used", "to", "introduce", "a", "few", "examples", ",", "not", "a",
						"complete", "list", "." },
				new String[] { "What", "does", "i.e.", "mean", "?", "I.e.", "means", "in", "other", "words", "." },
				new String[] { "it", "cost", "$", "30" },
				new String[] { "calculate", "2", "^", "3" },
				new String[] { "30", "%", "of", "the", "students" },
				new String[] { "A", "simple", "sentence", "." },
				new String[] { "that's", "why", "this", "is", "our", "place", ")", "." },
				new String[] { "most", ",", "punctuation", ";", "is", ".", "split", ":", "from", "!", "adjoining", "words", "?" },
				new String[] { "double", "quotes", "\"", "OK", "\"" },
				new String[] { "face-to-face", "class" },
				new String[] { "\"", "it", "is", "strange", "\"", ",", "said", "John", ",", "\"", "Katherine", "does", "not", "drink", "alchol", ".",
						"\"" },
				new String[] { "\"", "What", "?", "!", "\"", ",", "John", "yelled", "." },
				new String[] { "more", "abbreviations", ":", "a.m.", "p.m.", "Cap.", "c.", "et al.", "etc.", "P.S.", "Ph.D", "R.I.P", "vs.", "v.",
						"Mr.", "Ms.", "Dr.", "Pf.", "Mx.", "Ind.", "Inc.", "Corp.", "Co.,Ltd.", "Co., Ltd.", "Co. Ltd.", "Ltd.", "Prof." },
				new String[] { "elipsis", "dots", "...", "another", "elipsis", "dots", "…" },
				new String[] { "(", "testing", ")", "[", "brackets", "]", "{", "all", "}", "⟨", "kinds", "⟩" }
		};
		assertTrue(input.length == output.length);
		for (int i = 0; i < input.length; i++) {
			assertArrayEquals(output[i], RiTa.tokenize(input[i]));
		}

		//contractions----------------------------
		String[] cIn = {
				"Dr. Chan is talking slowly with Mr. Cheng, and they're friends.",
				"He can't didn't couldn't shouldn't wouldn't eat.",
				"Shouldn't he eat?",
				"It's not that I can't.",
				"We've found the cat.",
				"We didn't find the cat.",
				"it's 30°C outside"
		};
		String[][] cOut1 = {
				new String[] { "Dr.", "Chan", "is", "talking", "slowly", "with", "Mr.", "Cheng", ",", "and", "they", "are", "friends", "." },
				new String[] { "He", "can", "not", "did", "not", "could", "not", "should", "not", "would", "not", "eat", "." },
				new String[] { "Should", "not", "he", "eat", "?" },
				new String[] { "It", "is", "not", "that", "I", "can", "not", "." },
				new String[] { "We", "have", "found", "the", "cat", "." },
				new String[] { "We", "did", "not", "find", "the", "cat", "." },
				new String[] { "it", "is", "30", "°", "C", "outside" }
		};
		RiTa.SPLIT_CONTRACTIONS = true;
		assertTrue(cIn.length == cOut1.length);
		for (int i = 0; i < cIn.length; i++) {
			assertArrayEquals(cOut1[i], RiTa.tokenize(cIn[i]));
		}
		String[][] cOut2 = {
				new String[] { "Dr.", "Chan", "is", "talking", "slowly", "with", "Mr.", "Cheng", ",", "and", "they're", "friends", "." },
				new String[] { "He", "can't", "didn't", "couldn't", "shouldn't", "wouldn't", "eat", "." },
				new String[] { "Shouldn't", "he", "eat", "?" },
				new String[] { "It's", "not", "that", "I", "can't", "." },
				new String[] { "We've", "found", "the", "cat", "." },
				new String[] { "We", "didn't", "find", "the", "cat", "." },
				new String[] { "it's", "30", "°", "C", "outside" }
		};
		assertTrue(cIn.length == cOut2.length);
		RiTa.SPLIT_CONTRACTIONS = false;
		for (int i = 0; i < cIn.length; i++) {
			assertArrayEquals(cOut2[i], RiTa.tokenize(cIn[i]));
		}
	}

	@Test
	public void callUntokenize() {
		assertEquals("", RiTa.untokenize(new String[] { "" }));
		String[][] input = {
				new String[] { "We", "should", "consider", "the", "students", "'", "learning" },
				new String[] { "The", "boy", ",", "dressed", "in", "red", ",", "ate", "an", "apple", "." },
				new String[] { "We", "should", "consider", "the", "students", "\u2019", "learning" },
				new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" },
				new String[] { "Dr", ".", "Chan", "is", "talking", "slowly", "with", "Mr", ".", "Cheng", ",", "and", "they're", "friends", "." },
				new String[] { "why", "?", "Me", "?", "huh", "?", "!" },
				new String[] { "123", "123", "1", "2", "3", "1", ",", "1", "1", ".", "1", "23", ".", "45", ".", "67", "22/05/2012", "12th", "May",
						",", "2012" },
				new String[] { "\"", "Oh", "God", ",", "\"", "he", "thought", "." },
				new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" },
				new String[] { "She", "screamed", ",", "\"", "Oh", "God", "!", "\"" },
				new String[] { "\"", "Oh", ",", "God", "\"", ",", "he", "thought", ",", "\"", "not", "rain", "!", "\"" },
				new String[] { "The", "student", "said", "'", "learning", "is", "fun", "'" },
				new String[] { "dog", ",", "e.g.", "the", "cat", "." },
				new String[] { "dog", ",", "i.e.", "the", "cat", "." },
				new String[] { "What", "does", "e.g.", "mean", "?", "E.g.", "is", "used", "to", "introduce", "a", "few", "examples", ",", "not", "a",
						"complete", "list", "." },
				new String[] { "What", "does", "i.e.", "mean", "?", "I.e.", "means", "in", "other", "words", "." },
				new String[] { "A", "simple", "sentence", "." },
				new String[] { "that's", "why", "this", "is", "our", "place", ")", "." },
				new String[] { "this", "is", "for", "semicolon", ";", "that", "is", "for", "else" },
				new String[] { "this", "is", "for", "2", "^", "3", "2", "*", "3" },
				new String[] { "this", "is", "for", "$", "30", "and", "#", "30" },
				new String[] { "this", "is", "for", "30", "°", "C", "or", "30", "\u2103" },
				new String[] { "this", "is", "for", "a", "/", "b", "a", "⁄", "b" },
				new String[] { "this", "is", "for", "«", "guillemets", "»" },
				new String[] { "this", "...", "is", "…", "for", "ellipsis" },
				new String[] { "this", "line", "is", "'", "for", "'", "single", "‘", "quotation", "’", "mark" },
				new String[] { "Katherine", "’", "s", "cat", "and", "John", "'", "s", "cat" },
				new String[] { "this", "line", "is", "for", "(", "all", ")", "[", "kind", "]", "{", "of", "}", "⟨", "brackets", "⟩", "done" },
				new String[] { "this", "line", "is", "for", "the", "-", "dash" },
				new String[] { "30", "%", "of", "the", "student", "love", "day", "-", "dreaming", "." },
				new String[] { "\"", "that", "test", "line", "\"" },
				new String[] { "my", "email", "address", "is", "name", "@", "domin", ".", "com" },
				new String[] { "it", "is", "www", ".", "google", ".", "com" },
				new String[] { "that", "is", "www6", ".", "cityu", ".", "edu", ".", "hk" },
		};
		String[] output = {
				"We should consider the students' learning",
				"The boy, dressed in red, ate an apple.",
				"We should consider the students\u2019 learning",
				"The boy screamed, 'Where is my apple?'",
				"Dr. Chan is talking slowly with Mr. Cheng, and they're friends.",
				"why? Me? huh?!",
				"123 123 1 2 3 1, 1 1. 1 23. 45. 67 22/05/2012 12th May, 2012",
				"\"Oh God,\" he thought.",
				"The boy screamed, 'Where is my apple?'",
				"She screamed, \"Oh God!\"",
				"\"Oh, God\", he thought, \"not rain!\"",
				"The student said 'learning is fun'",
				"dog, e.g. the cat.",
				"dog, i.e. the cat.",
				"What does e.g. mean? E.g. is used to introduce a few examples, not a complete list.",
				"What does i.e. mean? I.e. means in other words.",
				"A simple sentence.",
				"that's why this is our place).",
				"this is for semicolon; that is for else",
				"this is for 2^3 2*3",
				"this is for $30 and #30",
				"this is for 30°C or 30\u2103",
				"this is for a/b a⁄b",
				"this is for «guillemets»",
				"this... is… for ellipsis",
				"this line is 'for' single ‘quotation’ mark",
				"Katherine’s cat and John's cat",
				"this line is for (all) [kind] {of} ⟨brackets⟩ done",
				"this line is for the-dash",
				"30% of the student love day-dreaming.",
				"\"that test line\"",
				"my email address is name@domin.com",
				"it is www.google.com",
				"that is www6.cityu.edu.hk"
		};
		assertTrue(input.length == output.length);
		for (int i = 0; i < input.length; i++) {
			assertEquals(output[i], RiTa.untokenize(input[i]));
		}
	}

	@Test
	public void callConcordance() {
		Map<String, String> data = RiTa.concordance("The dog ate the cat");
		Set keys = data.keySet();
		assertEquals(5, keys.size());
		assertEquals("1", data.get("the"));
		assertEquals("1", data.get("The"));
		assertEquals("1", data.get("dog"));

		data = RiTa.concordance("The dog ate the cat", "dog");
		keys = data.keySet();
		assertEquals(1, keys.size());
		assertEquals("dog", (String) keys.toArray()[0]);
		assertEquals("1", data.get("dog"));

		data = RiTa.concordance("The dog ate the cat", "pig");
		keys = data.keySet();
		assertEquals(1, keys.size());
		assertEquals("pig", (String) keys.toArray()[0]);
		assertEquals("0", data.get("pig"));

		Map<String, Object> defaultOpts = new HashMap<String, Object>();
		defaultOpts.put("ignoreCase", false);
		defaultOpts.put("ignoreStopWords", false);
		defaultOpts.put("ignorePunctuation", false);
		data = RiTa.concordance("The dog ate the cat", defaultOpts);
		keys = data.keySet();
		assertEquals(5, keys.size());
		assertEquals("1", data.get("the"));
		assertEquals("1", data.get("The"));
		assertEquals("1", data.get("dog"));

		data = RiTa.concordance("The dog ate the cat", "dog", defaultOpts);
		keys = data.keySet();
		assertEquals(1, keys.size());
		assertEquals("dog", (String) keys.toArray()[0]);
		assertEquals("1", data.get("dog"));

		Map<String, Object> ignoreCase = new HashMap<String, Object>();
		ignoreCase.put("ignoreCase", true);
		data = RiTa.concordance("The dog ate the cat", ignoreCase);
		keys = data.keySet();
		assertEquals(4, keys.size());
		assertEquals("2", data.get("the"));
		assertEquals(null, data.get("The"));

		data = RiTa.concordance("The dog ate the cat");
		keys = data.keySet();
		assertEquals(5, keys.size());
		assertEquals("1", data.get("the"));
		assertEquals("1", data.get("The"));

		data = RiTa.concordance("The dog ate the cat", "The", ignoreCase);
		keys = data.keySet();
		assertEquals(1, keys.size());
		assertEquals("The", keys.toArray()[0]);
		assertEquals("0", data.get("The"));

		data = RiTa.concordance("The dog ate the cat", "the", ignoreCase);
		keys = data.keySet();
		assertEquals(1, keys.size());
		assertEquals("the", keys.toArray()[0]);
		assertEquals("2", data.get("the"));

		Map<String, Object> ignorePunc = new HashMap<String, Object>();
		ignorePunc.put("ignorePunctuation", true);
		data = RiTa.concordance("'What a wonderful world;!:,?.'\"", ignorePunc);
		keys = data.keySet();
		assertEquals(4, keys.size());
		assertEquals(null, data.get("!"));

		Map<String, Object> ignoreStopW = new HashMap<String, Object>();
		ignoreStopW.put("ignoreStopWords", true);
		data = RiTa.concordance("The dog ate the cat", ignoreStopW);
		keys = data.keySet();
		assertEquals(4, keys.size());
		assertEquals(null, data.get("the"));
		ignoreStopW.clear();
		ignoreStopW.put("wordsToIgnore", new String[] { "dog", "cat" });
		data = RiTa.concordance("The dog ate the cat", ignoreStopW);
		keys = data.keySet();
		assertEquals(2, keys.size());
		assertEquals(null, data.get("dog"));
		assertEquals(null, data.get("cat"));
		assertEquals("1", data.get("ate"));
		ignoreStopW.clear();
		ignoreStopW.put("ignoreStopWords", true);
		ignoreStopW.put("ignoreCase", true);
		data = RiTa.concordance("The dog ate the cat", ignoreStopW);
		keys = data.keySet();
		assertEquals(3, keys.size());
		assertEquals(null, data.get("The"));

		Map<String, Object> all = new HashMap<String, Object>();
		all.put("ignoreCase", true);
		all.put("ignoreStopWords", true);
		all.put("ignorePunctuation", true);
		all.put("wordsToIgnore", new String[] { "fish" });
		data = RiTa.concordance("The Fresh fried fish, Fish fresh fried.", all);
		keys = data.keySet();
		assertEquals(2, keys.size());
		assertEquals("2", data.get("fresh"));
	}

	@Test
	public void callSentences() {
		assertArrayEquals(new String[] { "" }, RiTa.sentences(""));
		String[] input = {
				"Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications. The slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels. If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs.",
				"\"The boy went fishing.\", he said. Then he went away.",
				"The dog",
				"I guess the dog ate the baby.",
				"Oh my god, the dog ate the baby!",
				"Which dog ate the baby?",
				"'Yes, it was a dog that ate the baby', he said.",
				"The baby belonged to Mr. and Mrs. Stevens. They will be very sad.",
				"\"The baby belonged to Mr. and Mrs. Stevens. They will be very sad.\"",
				"\u201CThe baby belonged to Mr. and Mrs. Stevens. They will be very sad.\u201D",
				"\"My dear Mr. Bennet. Netherfield Park is let at last.\"",
				"\u201CMy dear Mr. Bennet. Netherfield Park is let at last.\u201D",
				"She wrote: \"I don't paint anymore. For a while I thought it was just a phase that I'd get over.\"",
				" I had a visit from my \"friend\" the tax man."
		};
		String[][] output = {
				new String[] { "Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications.",
						"The slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels.",
						"If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs." },
				new String[] { "\"The boy went fishing.\", he said.", "Then he went away." },
				new String[] { "The dog" },
				new String[] { "I guess the dog ate the baby." },
				new String[] { "Oh my god, the dog ate the baby!" },
				new String[] { "Which dog ate the baby?" },
				new String[] { "\'Yes, it was a dog that ate the baby\', he said." },
				new String[] { "The baby belonged to Mr. and Mrs. Stevens.", "They will be very sad." },
				new String[] { "\"The baby belonged to Mr. and Mrs. Stevens.", "They will be very sad.\"" },
				new String[] { "\u201CThe baby belonged to Mr. and Mrs. Stevens.", "They will be very sad.\u201D" },
				new String[] { "\"My dear Mr. Bennet.", "Netherfield Park is let at last.\"" },
				new String[] { "\u201CMy dear Mr. Bennet.", "Netherfield Park is let at last.\u201D" },
				new String[] { "She wrote: \"I don't paint anymore.", "For a while I thought it was just a phase that I'd get over.\"" },
				new String[] { "I had a visit from my \"friend\" the tax man." }
		};
		assertTrue(input.length == output.length);
		for (int i = 0; i < input.length; i++) {
			assertArrayEquals(output[i], RiTa.sentences(input[i]));
		}
	}

	//--------------------------------helper----------------------
	private static <T extends Comparable<T>> boolean listEq(List<T> a, List<T> b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.size() != b.size()) {
			return false;
		}
		else {
			for (int i = 0; i < a.size(); i++) {
				if (!a.get(i).equals(b.get(i))) {
					return false;
				}
			}
			return true;
		}
	}

}
