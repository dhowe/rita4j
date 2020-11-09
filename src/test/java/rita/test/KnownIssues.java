package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import rita.*;

// Failing tests go here until debugged
public class KnownIssues {
	//@Test
	public void pluralizeProblem(){
		assertEquals("pleae", RiTa.pluralize("pleae"));
		//from js knownIssues
		//can't find this word in Cambridge dictionary tho
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
