package rita.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rita.RiTa;

// Failing tests go here until debugged
public class KnownIssues {


	//@Test
	public void isRhymeProblem() { 
		assertTrue(!RiTa.isRhyme("solo", "yoyo")); // should not be rhymes
		assertTrue(!RiTa.isRhyme("yoyo", "jojo")); // should not be rhymes
	}
	
	// NOT SURE WHY THIS TEST EXISTS
	//assertEquals(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx), "bar baz");

}
