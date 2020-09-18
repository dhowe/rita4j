package rita.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.Markov;

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
				"bambinoes", "bambino",
				"universes", "universe",
				"toothbrushes", "toothbrush",
				"clashes", "clash",
				"credoes", "credo",
				"verves", "verve",
				"sudses", "sudse",
				"addresses", "address",
				"brownies", "browny",
				"consensuses", "consensus",
				"stuccoes", "stucco",
				"flashes", "flash",
				"obverses", "obverse",
				"morasses", "morass",
				"conclaves", "conclave",
				"desperadoes", "desperado",
				"pesoes", "peso",
				"promises", "promise",
				"tangoes", "tango",
				"spouses", "spouse",
				"acumens", "acumen",
				"undresses", "undress",
				"branches", "branch",
				"lapses", "lapse",
				"quizes", "quiz",
				"spyglasses", "spyglass",
				"overpasses", "overpass",
				"hashes", "hash",
				"cloneses", "clones",
				"potashes", "potash",
				"vetoes", "veto",
				"biggies", "biggie",
				"sleeves", "sleeve",
				"microwaves", "microwave",
				"hypotheses", "hypothesis",
				"pretenses", "pretense",
				"latches", "latch",
				"espressoes", "espresso",
				"pooches", "pooch",
				"fetuses", "fetus",
				"alumni", "alumnus",
				"lighthouses", "lighthouse",
				"weirdoes", "weirdo",
				"onyxes", "onyx",
				"genuses", "genus",
				"zombies", "zombie",
				"hearses", "hearse",
				"trenches", "trench",
				"paradoxes", "paradox",
				"hippies", "hippie",
				"tempoes", "tempo",
				"yuppies", "yuppie",
				"purses", "purse",
				"hatches", "hatch",
				"witches", "witch",
				"latexes", "latex",
				"sinuses", "sinus",
				"ostinatoes", "ostinato",
				"phrases", "phrase",
				"gustoes", "gusto",
				"gauchoes", "gaucho",
				"arches", "arch",
				"bitches", "bitch",
				"duplexes", "duplex",
				"hairdoes", "hairdo",
				"missives", "missive",
				"madhouses", "madhouse",
				"winoes", "wino",
				"washes", "wash",
				"pauses", "pause",
				"heroes", "hero",
				"sketches", "sketch",
				"conclaves", "conclave",
				"meshes", "mesh",
				"microeconomicses", "microeconomics",
				"cornstarches", "cornstarch",
				"amicuses", "amicus",
				"brasses", "brass",
				"marshes", "marsh",
				"masses", "mass",
				"esophaguses", "esophagus",
				"overpasses", "overpass",
				"impulses", "impulse",
				"pelvises", "pelvis",
				"electrodynamicses", "electrodynamics",
				"fetishes", "fetish",
				"manganeses", "manganese",
				"abysses", "abyss",
				"lighthouses", "lighthouse",
				"gashes", "gash",
				"pachinkoes", "pachinko",
				"calculuses", "calculus",
				"moxies", "moxie",
				"thatches", "thatch",
				"dynamoes", "dynamo",
				"lurches", "lurch",
				"vortexes", "vortex",
				"crunches", "crunch",
				"directives", "directive",
				"calories", "calorie",
				"kimonoes", "kimono",
				"witches", "witch",
				"moves", "move",
				"expanses", "expanse",
				"chaises", "chaise",
				"metroes", "metro",
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
	public void markovToStringBug() {
		//not testable
		//if do not visit (e.g, print it out to console) the value of markov.root.chlidren 
		//and the last word's chlidren, those value kind of gone, causing problems
		//see Markov.toString() (now is Markov.java line 43)
	}

	public static void main(String[] args) {
		new KnownIssues().singularizeBugs();
	}
}
