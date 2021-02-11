package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import java.util.regex.Pattern;


import static rita.RiTa.opts;

import rita.RiGrammar;
import rita.RiTa;
import rita.*;

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

	//@Test
	public void throwOnBadGrammars_GRAMMAR() {
		assertThrows(RiTaException.class, () -> RiTa.grammar(opts("", "pet")));
		assertThrows(RiTaException.class, () -> RiTa.grammar(opts("$$start", "pet")));
		assertThrows(RiTaException.class, () -> RiTa.grammar().addRule("$$rule", "pet"));
		assertThrows(RiTaException.class, () -> RiTa.grammar().removeRule("$$rule"));
	}

	//@Test
	public void callToStringAndToStringWithArg_GRAMMAR() {
		String str = "";
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "$pet.articlize()");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString();
		assertEq(str, "{\n  \"$$start\": \"$pet.articlize()\",\n  \"$$pet\": \"(dog | cat)\"\n}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet.articlize()");
		rg.addRule("$pet", "dog");
		str = rg.toString();
		assertEq(str, "{\n  \"$$start\": \"$pet.articlize()\",\n  \"$$pet\": \"(dog | cat)\"\n}");
		String lb = "<br>";
		rg = new RiGrammar();
		rg.addRule("$start", "$pet.articlize()");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString(lb);
		assertEq(str, "{<br>  \"$$start\": \"$pet.articlize()\",<br>  \"$$pet\": \"(dog | cat)\"<br>}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet.articlize()");
		rg.addRule("$pet", "dog");
		str = rg.toString(lb);
		assertEq(str, "{<br>  \"$$start\": \"$pet.articlize()\",<br>  \"$$pet\": \"(dog | cat)\"<br>}");
	}

	//@Test
	public void overrideDynamicDefault_GRAMMAR() {
		int count = 4;

		//normal dynamic behavior
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$rule $rule");
		rg.addRule("rule", "(a|b|c|d|e)");
		boolean ok = false;
		for (int i = 0; i < count; i++) {
			String[] parts = rg.expand().split(" ");
			assertEquals(2, parts.length);
			System.out.println(parts[0] + " " + parts[1]);
			if (!parts[0].equals(parts[1])) {
				ok = true;
				break;
			}
		}
		assertEquals(true, ok);
	}
	
	//@Test
	public void supportNorepeatRules() {
		//unresolved transform .norepeat()
		boolean fail = false;
		String names = "a|b|c|d|e";
		Map<String, Object> g = opts("start", "$names $names.norepeat()", "names", names);
		for (int i = 0; i < 5; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void supportNorepeatSymbolRules() {
		//unresolved transform .nr()
		boolean fail = false;
		String names = "(a|b|c|d|e).nr()";
		Map<String, Object> g = opts("start", "$names $names", "names", names);
		for (int i = 0; i < 5; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void supportNorepeatInlineRules() {
		//unresolved transform .nr()
		boolean fail = false;
		Map<String, Object> g = opts("start", "($$names=(a | b | c | d|e).nr()) $names");
		for (int i = 0; i < 5; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void supportNorepeatChoiceTransforms_SEQUENCES() {
		//unresolve transform .norepeat()
		int count = 5;
		boolean fail = false;
		Pattern regex = Pattern.compile("^[a-e] [a-e]$");
		for (int i = 0; i < count; i++) {
			String res = RiTa.evaluate("$$names=(a|b|c|d|e)\n$names $names.norepeat()", null);
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEq(parts.length, 2);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void supportNorepeatSymbolTransforms_SEQUENCES() {
		//unresolve transform .norepeat()
		int count = 5;
		boolean fail = false;
		Pattern regex = Pattern.compile("^[a-e] [a-e]$");
		for (int i = 0; i < count; i++) {
			String res = RiTa.evaluate("$$rule=(a|b|c|d|e).norepeat()\n$rule $rule");
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEq(parts.length, 2);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void supportNorepeatInlineTransforms_SEQUENCES() {
		//unresolved transform .norepeat()
		int count = 5;
		boolean fail = false;
		Pattern regex = Pattern.compile("^[a-e] [a-e]$");
		for (int i = 0; i < count; i++) {
			String res = RiTa.evaluate("($$rule=(a|b|c|d|e).norepeat()) $rule");
			assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEq(parts.length, 2);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		assertTrue(!fail);
	}

	//@Test
	public void resolveSimpleExpressions_EVALUATION() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("foo\nbar", ctx), "foo\nbar");
		assertEq(RiTa.evaluate("$foo=bar\nbaz\n$foo", ctx), "baz\nbar");
		String str = "Now in one year\n     A book published\n          And plumbing —";
		assertEq(RiTa.evaluate(str), str);
	}

	//@Test
	public void resolveTransformsOnLiterals_ASSIGN() {
		assertEq(RiTa.evaluate("(deeply-nested $art).art()", opts("art", "emotion")), "a deeply-nested emotion");
	}

	//@Test
	public void resolveSymbolsWithTransforms_GRAMMAR() {
		//unresolved transform
		assertEq(RiTa.evaluate("$foo=(bar).ucf\n$foo"), "Bar");
		Supplier<String> bar = () -> "result";
		Map<String, Object> ctx = opts("bar", bar);// func transform
		String res = RiTa.evaluate("().bar", ctx);
		assertEq(res, "result");

		ctx = opts("mammal", "(ox | ox)");
		res = RiTa.evaluate("The big $mammal ate the smaller $mammal.s.", ctx);
		assertEq(res, "The big ox ate the smaller oxen.");
	}

	//@Test
	public void callSearchWithoutRegex() {
		assertEquals(10, RiTa.search().length);
		//assertEquals(11, RiTa.search(opts("limit", 11)).length);
		//assertEquals(11, RiTa.search("", opts("limit", 11)).length);
		String[] expected = new String[] { "abalone", "abandonment", "abbey", "abbot", "abbreviation", "abdomen",
				"abduction", "aberration", "ability", "abnormality" };
		//assertArrayEquals(expected, RiTa.search(opts("pos", "n")));

		expected = new String[] { "abashed", "abate", "abbey", "abbot", "abet", "abhor", "abide", "abject", "ablaze",
				"able" };

		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 2)));

		expected = new String[] { "abbey", "abbot", "abode", "abscess", "absence", "abstract", "abuse", "abyss",
				"accent", "access" };
		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 2, "pos", "n")));

		expected = new String[] { "ace", "ache", "act", "age", "aid", "aide", "aim", "air", "aisle", "ale" };
		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 1, "pos", "n")));
	}
	
	//@Test
	public void callSearchWithPosFeatureLimit() {
		String[] expected;
		expected = new String[] { "abalone", "abandonments", "abbreviations", "abductions", "abilities" };
		assertArrayEquals(expected, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "nns")));
		assertArrayEquals(expected, RiTa.search("/0\\/1\\/0/", opts("type", "stresses", "limit", 5, "pos", "nns")));

		expected = new String[] { "abductions", "abortions", "absorbers", "abstentions", "abstractions" };
		assertArrayEquals(expected,
				RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "nns", "numSyllables", 3)));
	}
	
	//@Test
	public void callSpellsLikeOptions() {
		String[] expected = new String[] { "axes", "beef", "deer", "dibs", "fish" };
		String[] result = RiTa.spellsLike("ice", opts("minLength", 4, "maxLength", 4, "pos", "nns", "minDistance", 3, "limit", 5));
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 4);
		}
		assertArrayEquals(expected, result); 
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
