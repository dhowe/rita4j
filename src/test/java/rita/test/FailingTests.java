package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import rita.RiTa;

public class FailingTests {
	
	@Test
	public void singularizeFails() { // also in js
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
			//eq(RiTa.singularize(tests[i]), tests[i + 1]);
		}
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}

	public static void main(String[] args) {
		new FailingTests().singularizeFails();
	}
}
