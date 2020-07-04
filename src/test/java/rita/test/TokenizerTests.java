package rita.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rita.RiTa;

public class TokenizerTests {

	@Test
	public void testTokenize() {

		assertArrayEquals(RiTa.tokenize(""), new String[] { "" });
		assertArrayEquals(RiTa.tokenize("The dog"), new String[] { "The", "dog" });

		String input;
		String[] expected, output;

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

		// TODO: check Penn-Treebank tokenizer rules & add some more edge cases
		String[] inputs = new String[] { "A simple sentence.", "that's why this is our place).", };
		String[][] outputs = new String[][] {
				new String[] { "A", "simple", "sentence", "." },
				new String[] { "that's", "why", "this", "is", "our", "place", ")", "." }
		};

		assertEquals(inputs.length, outputs.length);
		for (int i = 0; i < inputs.length; i++) {
			assertArrayEquals(RiTa.tokenize(inputs[i]), outputs[i]);
		}

		// contractions -------------------------

		String txt1 = "Dr. Chan is talking slowly with Mr. Cheng, and they're friends."; // strange but same as RiTa-java
		String txt2 = "He can't didn't couldn't shouldn't wouldn't eat.";
		String txt3 = "Shouldn't he eat?";
		String txt4 = "It's not that I can't.";
		String txt5 = "We've found the cat.";
		String txt6 = "We didn't find the cat.";

		RiTa.SPLIT_CONTRACTIONS = true;
		assertArrayEquals(RiTa.tokenize(txt1), new String[] { "Dr", ".", "Chan", "is", "talking", "slowly", "with", "Mr",
				".", "Cheng", ",", "and", "they", "are", "friends", "." });
		assertArrayEquals(RiTa.tokenize(txt2),
				new String[] { "He", "can", "not", "did", "not", "could", "not", "should", "not", "would", "not", "eat", "." });
		assertArrayEquals(RiTa.tokenize(txt3), new String[] { "Should", "not", "he", "eat", "?" });
		assertArrayEquals(RiTa.tokenize(txt4), new String[] { "It", "is", "not", "that", "I", "can", "not", "." });
		assertArrayEquals(RiTa.tokenize(txt5), new String[] { "We", "have", "found", "the", "cat", "." });
		assertArrayEquals(RiTa.tokenize(txt6), new String[] { "We", "did", "not", "find", "the", "cat", "." });

		RiTa.SPLIT_CONTRACTIONS = false;
		assertArrayEquals(RiTa.tokenize(txt1), new String[] { "Dr", ".", "Chan", "is", "talking", "slowly", "with", "Mr",
				".", "Cheng", ",", "and", "they're", "friends", "." });
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
		};

		String[][] inputs = new String[][] {
				new String[] { "A", "simple", "sentence", "." },
				new String[] { "that's", "why", "this", "is", "our", "place", ")", "." }
		};

		assertEquals(inputs.length, outputs.length);
		for (int i = 0; i < inputs.length; i++) {
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
