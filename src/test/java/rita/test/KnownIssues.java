package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import rita.RiTa;

public class KnownIssues {

	// Failing tests we need to work on go here
	@Test
	public void simplestPossibleCode() {
		String[] sentences = new String[] {
				"it is www.google.com",
				"it is 'hell'"
		};
		System.out.println(RiTa.untokenize(RiTa.tokenize(sentences[0])));
		// => expect output: "it is www.google.com"
		System.out.println(RiTa.untokenize(RiTa.tokenize(sentences[1])));
		// => expect output: "it is 'hell'"
		assertEquals(RiTa.untokenize(RiTa.tokenize(sentences[0])), sentences[0]);
		assertEquals(RiTa.untokenize(RiTa.tokenize(sentences[1])), sentences[1]);
	}

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

}
