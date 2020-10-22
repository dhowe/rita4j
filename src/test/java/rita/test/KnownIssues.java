package rita.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.Markov;
import rita.RandGen;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static rita.Util.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import rita.*;


// Failing tests go here until debugged
public class KnownIssues {

	@Test
	public void tokenizeUrl() {

		String test = "it is www.google.com";
		String[] actual = RiTa.tokenize(test);
		assertArrayEquals(new String[] { "it", "is", "www", ".", "google", ".", "com" }, actual);
	}

	@Test
	public void untokenizeUrl() {

		String[] test = { "it", "is", "www", ".", "google", ".", "com" };
		String actual = RiTa.untokenize(test);
		assertEquals("it is www.google.com", actual);
	}

	@Test
	public void untokenizeAndBackUrl() { // See https://github.com/dhowe/rita2/issues/46
		String test = "it is www.google.com";
		String[] tokens = RiTa.tokenize(test);
		assertArrayEquals(new String[] { "it", "is", "www", ".", "google", ".", "com" }, tokens);
		String actual = RiTa.untokenize(tokens);
		assertEquals(test, actual);
	}

	@Test
	public void tokenizeSingleQuotes() {
		String test = "it is 'hell'";
		String[] actual = RiTa.tokenize(test);
		assertArrayEquals(new String[] { "it", "is", "'", "hell", "'" }, actual);
	}

	@Test
	public void untokenizeSingleQuotes() {
		String[] test = { "it", "is", "'", "hell", "'" };
		String actual = RiTa.untokenize(test);
		assertEquals("it is 'hell'", actual);
	}

	@Test
	public void untokenizeAndBackSingleQuotes() {
		// See https://github.com/dhowe/rita2/issues/46
		String test = "it is 'hell'";
		String[] tokens = RiTa.tokenize(test);
		assertArrayEquals(new String[] { "it", "is", "'", "hell", "'" }, tokens);
		String actual = RiTa.untokenize(tokens);
		assertEquals(test, actual);
	}

	@Test
	public void singularizeBugs() { // also in js
		String[] tests = {
				"grooves", "groove",
				"universes", "universe",
				"toothbrushes", "toothbrush",
				"clashes", "clash",
				"verves", "verve",
				"addresses", "address",
				"flashes", "flash",
				"morasses", "morass",
				"conclaves", "conclave",
				"promises", "promise",
				"spouses", "spouse",
				"branches", "branch",
				"lapses", "lapse",
				"quizes", "quiz",
				"spyglasses", "spyglass",
				"overpasses", "overpass",
				"clones", "clones",
				"microwaves", "microwave",
				"hypotheses", "hypothesis",
				"pretenses", "pretense",
				"latches", "latch",
				"fetuses", "fetus",
				"alumni", "alumnus",
				"lighthouses", "lighthouse",
				"onyxes", "onyx",
				"genuses", "genus",
				"zombies", "zombie",
				"hearses", "hearse",
				"trenches", "trench",
				"paradoxes", "paradox",
				"hippies", "hippie",
				"yuppies", "yuppie",
				"purses", "purse",
				"hatches", "hatch",
				"witches", "witch",
				"sinuses", "sinus",
				"phrases", "phrase",
				"arches", "arch",
				"duplexes", "duplex",
				"missives", "missive",
				"madhouses", "madhouse",
				"washes", "wash",
				"pauses", "pause",
				"heroes", "hero",
				"sketches", "sketch",
				"meshes", "mesh",
				"brasses", "brass",
				"marshes", "marsh",
				"masses", "mass",
				"overpasses", "overpass",
				"impulses", "impulse",
				"pelvises", "pelvis",
				"fetishes", "fetish",
				"abysses", "abyss",
				"lighthouses", "lighthouse",
				"gashes", "gash",
				"dynamoes", "dynamo",
				"lurches", "lurch",
				"directives", "directive",
				"calories", "calorie",
				"moves", "move",
				"expanses", "expanse",
				"chaises", "chaise",
				"briefcases", "briefcase",
		};
		for (int i = 0; i < tests.length; i += 2) {
			System.out.println(tests[i] + " -> " + RiTa.singularize(tests[i])
					+ ", but expecting " + tests[i + 1]);
			assertEquals(RiTa.singularize(tests[i]), tests[i + 1]);
		}
	}

	@Test
	public void markovGenerateDebug() {
		int n = 2;
		//different bugs appear when n=1 and n=2,3,4...
		Markov rm = new Markov(n);
		rm.addText("a simple sentence here.");
		//System.out.println(rm.input.toString());
		System.out.println(rm.generate());
	}

	@Test
	public void transformsProblem1(){
		//1. ($a).toUpperCase() doesn't work in java, but $a.toUpperCase() does
		Map<String, Object> ctx = opts();
		Map<String, Object> TT = opts("trace", true);
		assertEquals(RiTa.evaluate("$a=a\n$a.toUpperCase()", ctx,TT), "A");
		//ok
		assertEquals(RiTa.evaluate("$a=a\n($a).toUpperCase()", ctx,TT), "A");
		//fail
		//2. fail when chose a rule in choice with transform, fail with same reason
		assertEquals(RiTa.evaluate("$a=a\n(a | a).toUpperCase()",ctx,TT), "A");
		//pass
		assertEquals(RiTa.evaluate("$a=a\n($a | a).toUpperCase()",ctx,TT), "A");
		//fail when choose $a
		assertEquals(RiTa.evaluate("$a=a\n$b=a\n($a | $b).toUpperCase()",ctx,TT), "A");
		assertEquals(RiTa.evaluate("($a | $a).toUpperCase()\n$a=a", ctx, TT), "A");
		//fail

		//problem at visitChildren -> choice 
		/*
		Pass case: 													fail case:
		script:														script:
		(expr (symbol $a (transform .toUpperCase())))				(expr (choice ( (wexpr (expr (symbol $a))) ) (transform .toUpperCase())))
		log:														log:
		visitExpr: '$a.toUpperCase()' tfs=							visitExpr: '($a).toUpperCase()' tfs=
																	visitChoice: ($a).toUpperCase() ['$a'] tfs=.toUpperCase()
																	  select: '$a' tfs=.toUpperCase()
	****															visitExpr: '$a.toUpperCase()' tfs=.toUpperCase() 
		visitSymbol: 'a' tfs=.toUpperCase() -> $a.toUpperCase()		visitSymbol: 'a' tfs= -> $a
		applyTransform: 'a' tf=toUpperCase()						visitSymbol: 'a' tfs= -> a
		resolveTransform: 'a' -> 'A'								[ERROR] visitTransform: '.toUpperCase()'
		visitSymbol: 'a' tfs=.toUpperCase() -> A
		*/
		//seems that with choice, $a and .toUpperCase is separated
		//in **** line tfs should equal to null?
		//issue#53
	}

	@Test
	public void randomDoubleRangeProblem() {
		for (int i = 0; i < 1000; i++) {
			double res = RandGen.randomDouble();
			if (res > 0.5) {
				System.out.println(">0.5");
			}
		}
	}
	
	public static void main(String[] args) {
		new KnownIssues().singularizeBugs();
	}

}
