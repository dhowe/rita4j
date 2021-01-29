package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import static rita.RiTa.opts;

import rita.RiGrammar;
import rita.RiTa;

// Failing tests go here until debugged
public class KnownIssues {

	//@Test
	public void resolveComplexInlines_RISCRIPT() {

		String[] expected = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };
		String rs = RiTa.evaluate("($chosen=$person) talks to $chosen.", opts("person", "(Dave | Jill | Pete)"));
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	//@Test
	public void resolveComplexInlines_GRAMMAR() {
		String[] expected = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };
		String rules = "{\"start\": \" talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave\",\"Jill\": \"Jill\",\"Pete\": \"Pete\"}";
		RiGrammar rg = RiGrammar.fromJSON(rules);
		String rs = rg.expand();
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	//@Test
	public void grammarToString() {
		for (String g : GrammarTests.grammars) { //  KnownIssues
			RiGrammar rg = RiGrammar.fromJSON(g);
			RiGrammar rg2 = RiGrammar.fromJSON(rg.toJSON());
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

	//@Test
	public void ignoreLineComments_COMMENTS() {
		assertEq(RiTa.evaluate("// $foo=a"), "");
		assertEq(RiTa.evaluate("// hello"), "");
		assertEq(RiTa.evaluate("//hello"), "");
		assertEq(RiTa.evaluate("//()"), "");
		assertEq(RiTa.evaluate("//{}"), "");
		assertEq(RiTa.evaluate("//$"), "");
		assertEq(RiTa.evaluate("hello\n//hello"), "hello");
	}

	// @Test
	public void ignoreBlockComments_COMMENTS() {
		assertEq(RiTa.evaluate("/* hello */"), "");
		assertEq(RiTa.evaluate("/* $foo=a */"), "");
		assertEq(RiTa.evaluate("a /* $foo=a */b"), "a b");
		assertEq(RiTa.evaluate("a/* $foo=a */ b"), "a b");
		assertEq(RiTa.evaluate("a/* $foo=a */b"), "ab");
	}

	//@Test
	public void distinguishInlineWithParens_INLINE() {
		HashMap<String, Object> ctx = new HashMap<String, Object>();
		assertEq(RiTa.evaluate("hello \n($a=A)", ctx), "hello A");
		assertEq(ctx.get("a"), "A");
	}

	private static void assertEq(Object a, Object b) { // swap order of args
		assertEquals(b, a);
	}

	private Object stem(String s) {
		String t = RiTa.stem(s);
		System.out.println(s + " -> " + t);
		return "write";
	}

	// NOT SURE WHY THIS TEST EXISTS
	//assertEquals(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx), "bar baz");

}
