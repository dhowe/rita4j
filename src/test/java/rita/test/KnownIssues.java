package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rita.Util.opts;

import java.util.*;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.*;

// Failing tests go here until debugged
public class KnownIssues {

	@Test
	public void symbolsInMultiwordTransforms() {
		String res = RiTa.evaluate("($a dog).pluralize()\n$a=the", null, opts("trace", true));
		assertEquals("the dogs", res); 
	}
	
	@Test
	public void singularizeBugs() { // also in js
		String[] tests = {
				"grooves", "groove",
				"universes", "universe",
				"toothbrushes", "toothbrush",
				"clashes", "clash",
				"addresses", "address",
				"flashes", "flash",
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
				"directives", "directive",
				"calories", "calorie",
				"moves", "move",
				"expanses", "expanse",
				"briefcases", "briefcase",
		};
		for (int i = 0; i < tests.length; i += 2) {
			System.out.println(tests[i] + " -> " + RiTa.singularize(tests[i])
					+ ", but expecting " + tests[i + 1]);
			assertEquals(RiTa.singularize(tests[i]), tests[i + 1]);
		}
	}

	@Test
	public void choiceProblem() {
		Grammar rg;
		String res;
		Map<String, Object> TT = opts("trace", true);
		Function<String, String> pluralise = (s) -> {
			s = s.trim();
			if (s.contains(" ")) {
				String[] words = RiTa.tokenize(s);
				int lastIdx = words.length - 1;
				String last = words[lastIdx];
				words[lastIdx] = RiTa.pluralize(last);
				return RiTa.untokenize(words);
			}
			return RiTa.pluralize(s);
		};
		Map<String, Object> ctx = opts("pluralise", pluralise);
		rg = new Grammar(opts("start", "($state feeling).pluralise()", "state", "bad"), ctx);
		res = rg.expand(TT);
		assertEquals("bad feelings", res); //pass
		rg = new Grammar(opts("start", "($state feeling).pluralise()", "state", "(bad | bad)"), ctx);
		res = rg.expand(TT);
		assertEquals("bad feelings", res); //fail
		//seems that the choice problem still exist in some cases
		//script: (script (expr (choice ( (wexpr (expr (chars bad)))  |  (wexpr (expr (chars bad))) )) (chars   feeling) (symbol (transform .pluralise()))) <EOF>)
		//should: (script (expr (choice ( (wexpr (expr (chars bad)))  |  (wexpr (expr (chars bad))) ) (chars   feeling) (symbol (transform .pluralise())))) <EOF>) ?

		//-------------using riscipt to recreate---------------
		res = RiTa.evaluate("($a b).pluralize()\n$a=(a | a)", opts(), TT);
		assertEquals("a bs", res); //fail
		//script: (script (expr (symbol $a) (chars   b) (symbol (transform .pluralize()))) <EOF>)
		//should: (script (expr (((symbol $a) (chars   b)) (symbol (transform .pluralize())))) <EOF>) ?
	}

	@Test
	public void assignmentWithSymbolInContext() {
		//from Garmmar, testResolveInlines()
		//-----------------------------simplest recreation---------------
		for (int i = 0; i < 10; i++) {
			String[] expec = {"a a", "b b", "c c"};
			Map<String,Object> ctx = opts("symbol","(a | b | c)");
			String res = RiTa.evaluate("[$chosen=$symbol] $chosen", ctx, opts("trace", true));
			assertTrue(Arrays.asList(expec).contains(res));
		}

		//-----------------------------detail----------------------------
		Map<String, Object> TT = opts("trace", true);
		for (int i = 0; i < 10; i++) {
			String[] expec = { "Dave talks to Dave.", "John talks to John.", "Katherine talks to Katherine." };
			String res = RiTa.evaluate("[$chosen=$person] talks to $chosen.\n$person=(Dave | John | Katherine)");
			assertTrue(Arrays.asList(expec).contains(res));
		}
		//this pass

		String[] expected = { "Dave talks to Dave.", "John talks to John.", "Katherine talks to Katherine." };
		Grammar rg;
		String rules, rs;

		for (int i = 0; i < 10; i++) {
			rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"Dave | John | Katherine\"}";
			rg = Grammar.fromJSON(rules);
			rs = rg.expand();
			assertTrue(Arrays.asList(expected).contains(rs));
		}
		//but this fail

		//ok it calls evaluate like this
		for (int i = 0; i < 10; i++) {
			String[] expec = { "Dave talks to Dave.", "John talks to John.", "Katherine talks to Katherine." };
			Map<String, Object> ctx = opts("start", "[$chosen=$person] talks to $chosen.", "person", "(Dave | John | Katherine)");
			String res = RiTa.evaluate("[$chosen=$person] talks to $chosen.", ctx, opts("trace", true));
			assertTrue(Arrays.asList(expec).contains(res));
		}
		//and it fail
		//script: (script (expr (choice ( (wexpr (expr (chars Dave)))  |  (wexpr (expr (chars John)))  |  (wexpr (expr (chars Katherine))) )) (chars   talks   to  ) (choice ( (wexpr (expr (chars Dave)))  |  (wexpr (expr (chars John)))  |  (wexpr (expr (chars Katherine))) )) (chars .)) <EOF>)
		//so $chosen is parsed as choice(...), but not a char(...)

		for (int i = 0; i < 10; i++) {
			String[] expec = { "Dave talks to Dave.", "John talks to John.", "Katherine talks to Katherine." };
			Map<String, Object> ctx = opts("start", "$chosen talks to $chosen.", "person", "(Dave | John | Katherine)", "chosen", "$person");
			String res = RiTa.evaluate("$chosen talks to $chosen.", ctx, opts("trace", true));
			assertTrue(Arrays.asList(expec).contains(res));
		}
		//same as above

	}

	public static void main(String[] args) {
		new KnownIssues().singularizeBugs();
	}

}
