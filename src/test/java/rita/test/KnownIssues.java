package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rita.*;

// Failing tests go here until debugged
public class KnownIssues {

	//@Test
	public void singularPlural() {
		assertEquals("clones", RiTa.pluralize("clone"));
		assertEquals("clone", RiTa.singularize("clones"));
		assertEquals(true, Inflector.isPlural("clones"));
		assertEquals(false, Inflector.isPlural("clone"));
	}
	
	//@Test
	public void isRhymeProblem() {
		assertTrue(!RiTa.isRhyme("solo", "yoyo")); // should not be rhymes
		assertTrue(!RiTa.isRhyme("yoyo", "jojo")); // should not be rhymes
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

	// NOT SURE WHY THIS TEST EXISTS
	//assertEquals(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx), "bar baz");

}
