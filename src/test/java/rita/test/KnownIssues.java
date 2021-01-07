package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rita.*;

// Failing tests go here until debugged
public class KnownIssues {
	//@Trst
	public void showingLiteralDollarSign() {
		assertEquals("This is $100", RiTa.evaluate("This is &#36100"));
		assertEquals("This is $100", RiTa.evaluate("This is &#x00024100"));
	}

	//@Test
	public void grammarToString() {
		for (String g : GrammarTests.grammars) { //  KnownIssues
			Grammar rg = Grammar.fromJSON(g);
			Grammar rg2 = Grammar.fromJSON(rg.toJSON());
			assertTrue(rg2.toString().equals(rg.toString()));
			assertTrue(rg.equals(rg2));
		}
	}

	//@Test 
	public void stemmerProblem() {

		System.out.println(RiTa.stem("write writes writing writings."));
		

		assertEquals("write", stem("write"));
		assertEquals("write", stem("writes"));
		assertEquals("write", stem("writing"));
		assertEquals("write", stem("writings"));
		System.out.println();
		assertEquals("write", stem("writer"));
		assertEquals("write", stem("wrote"));
		assertEquals("write", stem("written"));
	}

	private Object stem(String s) {
		String t = RiTa.stem(s);
		System.out.println(s+" -> "+t);
		return "write";
	}

	// NOT SURE WHY THIS TEST EXISTS
	//assertEquals(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx), "bar baz");

}
