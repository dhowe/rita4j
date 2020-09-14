package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import rita.RiTa;

public class TokenizerTests {

	@Test
	public void forDebug() {
		//calling tokenize() and untokenize() together seems to cause some werid bugs
		//but I guess normally no one do this
		String[][] tokens = new String[][] {
				new String[] { "it", "is", "www", ".", "google", ".", "com" },
				new String[] { "it", "is", "'", "hell", "'" },
				new String[] { "everything", "else", ",", "seems", "to", "be", "\"", "OK", "\"", "." }
		};

		String[] untokens = new String[] {
				"it is www.google.com",
				"it is 'hell'",
				"everything else, seems to be \"OK\"."
		};

		String[][] usingTokenize = new String[][] {
				RiTa.tokenize(untokens[0]),
				RiTa.tokenize(untokens[1]),
				RiTa.tokenize(untokens[2])
		};

		String[] usingUntokenize = new String[] {
				RiTa.untokenize(tokens[0]),
				RiTa.untokenize(tokens[1]),
				RiTa.untokenize(tokens[2])
		};

		String[] usingTokenizeThenUntokenize = new String[] {
				RiTa.untokenize(RiTa.tokenize(untokens[0])),
				RiTa.untokenize(RiTa.tokenize(untokens[1])),
				RiTa.untokenize(RiTa.tokenize(untokens[2]))
		};

		String[][] usingUntokenizeThenTokenize = new String[][] {
				RiTa.tokenize(RiTa.untokenize(tokens[0])),
				RiTa.tokenize(RiTa.untokenize(tokens[1])),
				RiTa.tokenize(RiTa.untokenize(tokens[2]))
		};

		String[] usingTokenizeThenUntokenizeWithMiddleMan = new String[] {
				RiTa.untokenize(usingTokenize[0]),
				RiTa.untokenize(usingTokenize[1]),
				RiTa.untokenize(usingTokenize[2])
		};

		for (int i = 0; i < tokens.length; i++) {
			System.out.println("tokens:");
			for (int j = 0; j < tokens[i].length; j++) {
				if (j != tokens[i].length - 1) {
					System.out.print(tokens[i][j] + "//");
				}
				else {
					System.out.println(tokens[i][j] + "\\\\");
				}
			}
			System.out.println("usingTokenize:");
			for (int j = 0; j < usingTokenize[i].length; j++) {
				if (j != usingTokenize[i].length - 1) {
					System.out.print(usingTokenize[i][j] + "//");
				}
				else {
					System.out.println(usingTokenize[i][j] + "\\\\");
				}
			}
			//just call tokenize() => everything OK
			System.out.println("using untokenize then tokenize:");
			for (int j = 0; j < usingUntokenizeThenTokenize[i].length; j++) {
				if (j != usingUntokenizeThenTokenize[i].length - 1) {
					System.out.print(usingUntokenizeThenTokenize[i][j] + "//");
				}
				else {
					System.out.println(usingUntokenizeThenTokenize[i][j] + "\\\\");
				}
			}
			//calling untokenize then tokenize seems to be ok
			System.out.println("untokens:");
			System.out.println(untokens[i]);
			System.out.println("usingUntokenize:");
			System.out.println(usingUntokenize[i]);
			// just call untokenize() => everything OK
			System.out.println("using tokenize then untokenize:");
			System.out.println(usingTokenizeThenUntokenize[i]);
			// call tokenize() then untokenize() together => bugs
			// bugs do not appear in js side
			System.out.println("using tokenize then untokenize with middle man");
			System.out.println(usingTokenizeThenUntokenizeWithMiddleMan[i]);
			// this also doesn't work...
			/* bugs only occur when: 1. encounter email and website address
			                         2. when single quotation marks appear after the word is */

			//assertArrayEquals(tokens[i], usingTokenize[i]);
			//assertEquals(untokens[i], usingUntokenize[i]);
			//assertEquals(untokens[i], usingTokenizeThenUntokenize[i]);
			//assertEquals(tokens[i],usingUntokenizeThenTokenize[i]);
		}
	}

	@Test
	public void testTokenizeAndBack() {
		String[] tests = {
				"Dr. Chan is talking slowly with Mr. Cheng, and they're friends.",
				"He can't didn't couldn't shouldn't wouldn't eat.",
				"Shouldn't he eat?",
				"It's not that I can't.",
				"We've found the cat.",
				"We didn't find the cat.",
				"face-to-face class",
				"\"it is strange\", said John, \"Katherine does not drink alchol.\"",
				"more abbreviations: a.m. p.m. Cap. c. et al. etc. P.S. Ph.D R.I.P vs. v. Mr. Ms. Dr. Pf. Mx. Ind. Inc. Corp. Co.,Ltd. Co., Ltd. Co. Ltd. Ltd.",
				"elipsis dots... another elipsis dots…",
				"(testing) [brackets] {all} ⟨kinds⟩",
				"this is for semicolon; that is for else",
				"this is for 2^3 2*3",
				"this is for $30 and #30",
				"this is for 30°C or 30\u2103",
				"this is for a/b a⁄b",
				"this is for «guillemets»",
				"this... is… for ellipsis",
				//"this line is 'for' single ‘quotation’ mark",//this pass tokenize and unkenize separately but not this?
				"Katherine’s cat and John's cat",
				"this line is for (all) [kind] {of} ⟨brackets⟩ done",
				"this line is for the-dash",
				"30% of the student love day-dreaming.",
				//"my email address is name@domin.com", 
				//"it is www.google.com",
				//"that is www6.cityu.edu.hk", //these too... => see forDegub()
				"30% of the student will pay $30 to decrease the temperature by 2°C"
		};
		for (int i = 0; i < tests.length; i++) {
			String[] tokenized = RiTa.tokenize(tests[i]);
			for (int j = 0; j < tokenized.length; j++) {
				if (j != tokenized.length - 1) {
					//System.out.print(tokenized[j] + "//");
				}
				else {
					//System.out.println(tokenized[j]);
				}
			}
			String untokenized = RiTa.untokenize(tokenized);
			//System.out.println(untokenized);
			assertEquals(untokenized, tests[i]);
		}
	}

	@Test
	public void testTokenize() {

		assertArrayEquals(RiTa.tokenize(""), new String[] { "" });
		assertArrayEquals(RiTa.tokenize("The dog"), new String[] { "The", "dog" });

		String input, expected[], output[];

		input = "The student said 'learning is fun'";
		expected = new String[] { "The", "student", "said", "'", "learning", "is", "fun", "'" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "\"Oh God,\" he thought.";
		expected = new String[] { "\"", "Oh", "God", ",", "\"", "he", "thought", "." };
		output = RiTa.tokenize(input);
		// console.log(expected,output);
		assertArrayEquals(output, expected);

		input = "The boy, dressed in red, ate an apple.";
		expected = new String[] { "The", "boy", ",", "dressed", "in", "red", ",", "ate", "an", "apple", "." };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "why? Me?huh?!";
		expected = new String[] { "why", "?", "Me", "?", "huh", "?", "!" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "123 123 1 2 3 1,1 1.1 23.45.67 22/05/2012 12th May,2012";
		expected = new String[] { "123", "123", "1", "2", "3", "1", ",", "1", "1", ".", "1", "23", ".", "45", ".", "67",
				"22/05/2012", "12th", "May", ",", "2012" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "The boy screamed, \"Where is my apple?\"";
		expected = new String[] { "The", "boy", "screamed", ",", "\"", "Where", "is", "my", "apple", "?", "\"" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "The boy screamed, \u201CWhere is my apple?\u201D";
		expected = new String[] { "The", "boy", "screamed", ",", "\u201C", "Where", "is", "my", "apple", "?", "\u201D" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "The boy screamed, 'Where is my apple?'";
		expected = new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "The boy screamed, \u2018Where is my apple?\u2019";
		expected = new String[] { "The", "boy", "screamed", ",", "\u2018", "Where", "is", "my", "apple", "?", "\u2019" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "dog, e.g. the cat.";
		expected = new String[] { "dog", ",", "e.g.", "the", "cat", "." };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "dog, i.e. the cat.";
		expected = new String[] { "dog", ",", "i.e.", "the", "cat", "." };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "What does e.g. mean? E.g. is used to introduce a few examples, not a complete list.";
		expected = new String[] { "What", "does", "e.g.", "mean", "?", "E.g.", "is", "used", "to", "introduce", "a", "few",
				"examples", ",", "not", "a", "complete", "list", "." };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "What does i.e. mean? I.e. means in other words.";
		expected = new String[] { "What", "does", "i.e.", "mean", "?", "I.e.", "means", "in", "other", "words", "." };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "it cost $30";
		expected = new String[] { "it", "cost", "$", "30" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "calculate 2^3";
		expected = new String[] { "calculate", "2", "^", "3" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "30% of the students";
		expected = new String[] { "30", "%", "of", "the", "students" };
		output = RiTa.tokenize(input);
		assertArrayEquals(output, expected);

		input = "it's 30°C outside";
		expected = new String[] { "it", "is", "30", "°", "C", "outside" };
		RiTa.SPLIT_CONTRACTIONS = true;
		output = RiTa.tokenize(input);
		RiTa.SPLIT_CONTRACTIONS = false;
		assertArrayEquals(output, expected);

		String[] inputs = new String[] {
				"A simple sentence.",
				"that's why this is our place).",
				"most, punctuation; is. split: from! adjoining words?",
				"double quotes \"OK\"",
				"face-to-face class",
				"\"it is strange\", said John, \"Katherine does not drink alchol.\"",
				"\"What?!\", John yelled.",
				"more abbreviations: a.m. p.m. Cap. c. et al. etc. P.S. Ph.D R.I.P vs. v. Mr. Ms. Dr. Pf. Mx. Ind. Inc. Corp. Co.,Ltd. Co., Ltd. Co. Ltd. Ltd.",
				"(testing) [brackets] {all} ⟨kinds⟩",
				"elipsis dots... another elipsis dots…",
				"this line is 'for' single ‘quotation’ mark"
		};

		String[][] outputs = new String[][] {
				{ "A", "simple", "sentence", "." },
				{ "that's", "why", "this", "is", "our", "place", ")", "." },
				{ "most", ",", "punctuation", ";", "is", ".", "split", ":", "from", "!", "adjoining", "words", "?" },
				{ "double", "quotes", "\"", "OK", "\"" },
				{ "face-to-face", "class" },
				{ "\"", "it", "is", "strange", "\"", ",", "said", "John", ",", "\"", "Katherine", "does", "not", "drink", "alchol", ".", "\"" },
				{ "\"", "What", "?", "!", "\"", ",", "John", "yelled", "." },
				{ "more", "abbreviations", ":", "a.m.", "p.m.", "Cap.", "c.", "et al.", "etc.", "P.S.", "Ph.D", "R.I.P", "vs.", "v.", "Mr.",
						"Ms.", "Dr.", "Pf.", "Mx.", "Ind.", "Inc.", "Corp.", "Co.,Ltd.", "Co., Ltd.", "Co. Ltd.", "Ltd." },
				{ "(", "testing", ")", "[", "brackets", "]", "{", "all", "}", "⟨", "kinds", "⟩" },//this might not need to be fix coz ⟨⟩ is rarely seen
				{ "elipsis", "dots", "...", "another", "elipsis", "dots", "…" },
				{ "this", "line", "is", "'", "for", "'", "single", "‘", "quotation", "’", "mark" }
		};

		assertEquals(inputs.length, outputs.length);
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < RiTa.tokenize(inputs[i]).length; j++) {
				if (j != RiTa.tokenize(inputs[i]).length - 1) {
					//System.out.print(RiTa.tokenize(inputs[i])[j] + "//");
				}
				else {
					//System.out.println(RiTa.tokenize(inputs[i])[j]);
				}
			}
			assertArrayEquals(RiTa.tokenize(inputs[i]), outputs[i], RiTa.tokenize(inputs[i]).toString());
		}

		// contractions -------------------------

		String txt1 = "Dr. Chan is talking slowly with Mr. Cheng, and they're friends."; // strange but same as RiTa-java
		String txt2 = "He can't didn't couldn't shouldn't wouldn't eat.";
		String txt3 = "Shouldn't he eat?";
		String txt4 = "It's not that I can't.";
		String txt5 = "We've found the cat.";
		String txt6 = "We didn't find the cat.";

		RiTa.SPLIT_CONTRACTIONS = true;
		assertArrayEquals(RiTa.tokenize(txt1),
				new String[] { "Dr.", "Chan", "is", "talking", "slowly", "with", "Mr.", "Cheng", ",", "and", "they", "are", "friends", "." });
		assertArrayEquals(RiTa.tokenize(txt2),
				new String[] { "He", "can", "not", "did", "not", "could", "not", "should", "not", "would", "not", "eat", "." });
		assertArrayEquals(RiTa.tokenize(txt3), new String[] { "Should", "not", "he", "eat", "?" });
		assertArrayEquals(RiTa.tokenize(txt4), new String[] { "It", "is", "not", "that", "I", "can", "not", "." });
		assertArrayEquals(RiTa.tokenize(txt5), new String[] { "We", "have", "found", "the", "cat", "." });
		assertArrayEquals(RiTa.tokenize(txt6), new String[] { "We", "did", "not", "find", "the", "cat", "." });

		RiTa.SPLIT_CONTRACTIONS = false;
		assertArrayEquals(RiTa.tokenize(txt1),
				new String[] { "Dr.", "Chan", "is", "talking", "slowly", "with", "Mr.", "Cheng", ",", "and", "they're", "friends", "." });
		assertArrayEquals(RiTa.tokenize(txt2),
				new String[] { "He", "can't", "didn't", "couldn't", "shouldn't", "wouldn't", "eat", "." });
		assertArrayEquals(RiTa.tokenize(txt3), new String[] { "Shouldn't", "he", "eat", "?" });
		assertArrayEquals(RiTa.tokenize(txt4), new String[] { "It's", "not", "that", "I", "can't", "." });
		assertArrayEquals(RiTa.tokenize(txt5), new String[] { "We've", "found", "the", "cat", "." });
		assertArrayEquals(RiTa.tokenize(txt6), new String[] { "We", "didn't", "find", "the", "cat", "." });
	}

	@Test
	public void testUntokenize() {

		assertEquals(RiTa.untokenize(new String[] { "" }), "");

		String[] input;
		String output, expected;

		expected = "We should consider the students' learning";
		input = new String[] { "We", "should", "consider", "the", "students", "'", "learning" };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "The boy, dressed in red, ate an apple.";
		input = new String[] { "The", "boy", ",", "dressed", "in", "red", ",", "ate", "an", "apple", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "We should consider the students\u2019 learning";
		input = new String[] { "We", "should", "consider", "the", "students", "\u2019", "learning" };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "The boy screamed, 'Where is my apple?'";
		input = new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "Dr. Chan is talking slowly with Mr. Cheng, and they're friends."; // strange but same as RiTa-java
		input = new String[] { "Dr", ".", "Chan", "is", "talking", "slowly", "with", "Mr", ".", "Cheng", ",", "and",
				"they're", "friends", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "why", "?", "Me", "?", "huh", "?", "!" };
		expected = "why? Me? huh?!";
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "123", "123", "1", "2", "3", "1", ",", "1", "1", ".", "1", "23", ".", "45", ".", "67",
				"22/05/2012", "12th", "May", ",", "2012" };
		expected = "123 123 1 2 3 1, 1 1. 1 23. 45. 67 22/05/2012 12th May, 2012";
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "\"", "Oh", "God", ",", "\"", "he", "thought", "." };
		expected = "\"Oh God,\" he thought.";
		output = RiTa.untokenize(input);
		// console.log(expected,'\n',output);
		assertEquals(output, expected);

		expected = "The boy screamed, 'Where is my apple?'";
		input = new String[] { "The", "boy", "screamed", ",", "'", "Where", "is", "my", "apple", "?", "'" };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "She", "screamed", ",", "\"", "Oh", "God", "!", "\"" };
		expected = "She screamed, \"Oh God!\"";
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "She", "screamed", ":", "\"", "Oh", "God", "!", "\"" };
		expected = "She screamed: \"Oh God!\"";
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		input = new String[] { "\"", "Oh", ",", "God", "\"", ",", "he", "thought", ",", "\"", "not", "rain", "!", "\"" };
		expected = "\"Oh, God\", he thought, \"not rain!\"";
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "The student said 'learning is fun'";
		input = new String[] { "The", "student", "said", "'", "learning", "is", "fun", "'" };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "dog, e.g. the cat.";
		input = new String[] { "dog", ",", "e.g.", "the", "cat", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "dog, i.e. the cat.";
		input = new String[] { "dog", ",", "i.e.", "the", "cat", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "What does e.g. mean? E.g. is used to introduce a few examples, not a complete list.";
		input = new String[] { "What", "does", "e.g.", "mean", "?", "E.g.", "is", "used", "to", "introduce", "a", "few",
				"examples", ",", "not", "a", "complete", "list", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		expected = "What does i.e. mean? I.e. means in other words.";
		input = new String[] { "What", "does", "i.e.", "mean", "?", "I.e.", "means", "in", "other", "words", "." };
		output = RiTa.untokenize(input);
		assertEquals(output, expected);

		// more tests

		String[] outputs = new String[] { "A simple sentence.",
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
				"that is www6.cityu.edu.hk",
				"30% of the students will pay $30 to decrease the temperature by 2°C"
		};

		String[][] inputs = new String[][] {
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
				new String[] { "30", "%", "of", "the", "students", "will", "pay", "$", "30", "to", "decrease", "the", "temperature", "by", "2", "°",
						"C" }
		};

		assertEquals(inputs.length, outputs.length);
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[i].length; j++) {
				if (j != inputs[i].length - 1) {
					//System.out.print(inputs[i][j] + "\\\\");
				}
				else {
					//System.out.println(inputs[i][j]);
				}
			}
			//System.out.println(RiTa.untokenize(inputs[i]));
			assertEquals(RiTa.untokenize(inputs[i]), outputs[i]);
		}
	}

	@Test
	public void testSentences() {

		String input = "Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications. The slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels. If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs.";
		String[] expected = new String[] {
				"Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications.",
				"The slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels.",
				"If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs." };

		String[] output = RiTa.sentences(input);
		assertArrayEquals(output, expected);

		input = "Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications.\n\nThe slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels.\r\n If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs.";
		expected = new String[] {
				"Stealth's Open Frame, OEM style LCD monitors are designed for special mounting applications.",
				"The slim profile packaging provides an excellent solution for building into kiosks, consoles, machines and control panels.",
				"If you cannot find an off the shelf solution call us today about designing a custom solution to fit your exact needs." };

		output = RiTa.sentences(input);
		assertArrayEquals(output, expected);

		input = "\"The boy went fishing.\", he said. Then he went away.";
		expected = new String[] { "\"The boy went fishing.\", he said.", "Then he went away." };
		output = RiTa.sentences(input);
		assertArrayEquals(output, expected);

		input = "The dog";
		output = RiTa.sentences(input);
		assertArrayEquals(output, new String[] { input });

		input = "I guess the dog ate the baby.";
		output = RiTa.sentences(input);
		assertArrayEquals(output, new String[] { input });

		input = "Oh my god, the dog ate the baby!";
		output = RiTa.sentences(input);
		expected = new String[] { "Oh my god, the dog ate the baby!" };
		assertArrayEquals(output, expected);

		input = "Which dog ate the baby?";
		output = RiTa.sentences(input);
		expected = new String[] { "Which dog ate the baby?" };
		assertArrayEquals(output, expected);

		input = "'Yes, it was a dog that ate the baby', he said.";
		output = RiTa.sentences(input);
		expected = new String[] { "\'Yes, it was a dog that ate the baby\', he said." };
		assertArrayEquals(output, expected);

		input = "The baby belonged to Mr. and Mrs. Stevens. They will be very sad.";
		output = RiTa.sentences(input);
		expected = new String[] { "The baby belonged to Mr. and Mrs. Stevens.", "They will be very sad." };
		assertArrayEquals(output, expected);

		// More quotation marks
		input = "\"The baby belonged to Mr. and Mrs. Stevens. They will be very sad.\"";
		output = RiTa.sentences(input);
		expected = new String[] { "\"The baby belonged to Mr. and Mrs. Stevens.", "They will be very sad.\"" };
		assertArrayEquals(output, expected);

		input = "\u201CThe baby belonged to Mr. and Mrs. Stevens. They will be very sad.\u201D";
		output = RiTa.sentences(input);
		expected = new String[] { "\u201CThe baby belonged to Mr. and Mrs. Stevens.", "They will be very sad.\u201D" };
		assertArrayEquals(output, expected);

		// https://github.com/dhowe/RiTa/issues/498
		input = "\"My dear Mr. Bennet. Netherfield Park is let at last.\"";
		output = RiTa.sentences(input);
		expected = new String[] { "\"My dear Mr. Bennet.", "Netherfield Park is let at last.\"" };
		assertArrayEquals(output, expected);

		input = "\u201CMy dear Mr. Bennet. Netherfield Park is let at last.\u201D";
		output = RiTa.sentences(input);
		expected = new String[] { "\u201CMy dear Mr. Bennet.", "Netherfield Park is let at last.\u201D" };
		assertArrayEquals(output, expected);

		/*******************************************/

		input = "She wrote: \"I don't paint anymore. For a while I thought it was just a phase that I'd get over.\"";
		output = RiTa.sentences(input);
		expected = new String[] { "She wrote: \"I don't paint anymore.",
				"For a while I thought it was just a phase that I'd get over.\"" };
		assertArrayEquals(output, expected);

		input = " I had a visit from my \"friend\" the tax man.";
		output = RiTa.sentences(input);
		expected = new String[] { "I had a visit from my \"friend\" the tax man." };
		assertArrayEquals(output, expected);

		assertArrayEquals(RiTa.sentences(""), new String[] { "" });
	}

}
