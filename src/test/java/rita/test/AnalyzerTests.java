package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import rita.*;

import org.junit.jupiter.api.Test;

public class AnalyzerTests { 
	
	@Test
	public void handleSingularPluralPairs() {

		String[] testPairs = {
				"turf", "turf",
				"macaroni", "macaroni",
				"spaghetti", "spaghetti",
				"potpourri", "potpourri",
				"electrolysis", "electrolysis",
				"media", "medium",
				"millennia", "millennium",
				"consortia", "consortium",
				"concerti", "concerto",
				"septa", "septum",
				"termini", "terminus",
				"stimuli", "stimulus",
				"larvae", "larva",
				"vertebrae", "vertebra",
				"memorabilia", "memorabilium",
				"sheaves", "sheaf",
				"spoofs", "spoof",
				"proofs", "proof",
				"roofs", "roof",
				"disbeliefs", "disbelief",
				"indices", "index",
				"accomplices", "accomplice",
				"hooves", "hoof",
				"thieves", "thief",
				"rabbis", "rabbi",
				"flu", "flu",
				"safaris", "safari",
				"uses", "use",
				"pinches", "pinch",
				"catharses", "catharsis",
				"hankies", "hanky",
				"selves", "self",
				"bookshelves", "bookshelf",
				"wheezes", "wheeze",
				"diagnoses", "diagnosis",
				"blondes", "blonde",
				"eyes", "eye",
				"swine", "swine",
				"cognoscenti", "cognoscenti",
				"bonsai", "bonsai",
				"taxis", "taxi",
				"chiefs", "chief",
				"monarchs", "monarch",
				"lochs", "loch",
				"stomachs", "stomach",
				"Chinese", "Chinese",
				"people", "person",
				"money", "money",
				"humans", "human",
				"germans", "german",
				"romans", "roman",
				"memoranda", "memorandum",
				"data", "datum",
				"appendices", "appendix",
				"theses", "thesis",
				"alumni", "alumnus",
				"solos", "solo",
				"music", "music",
				"oxen", "ox",
				"beef", "beef",
				"tobacco", "tobacco",
				"cargo", "cargo",
				"golf", "golf",
				"grief", "grief",
				"cakes", "cake",
				"dogs", "dog",
				"feet", "foot",
				"teeth", "tooth",
				"kisses", "kiss",
				"children", "child",
				// "randomwords", "randomword", // should be seen as two words: "random word"
				"deer", "deer",
				"sheep", "sheep",
				"shrimp", "shrimp",
				"tomatoes", "tomato",
				"photos", "photo",
				"toes", "toe",
				"series", "series",
				"men", "man",
	      "mice", "mouse",
	      "lice", "louse",
//	    "dice", "die", consider 'dice' as singular
	      "rice", "rice",
	      "women", "woman",
	      "clothes", "clothes",
				"gases", "gas",
				"buses", "bus",
				"happiness", "happiness",
				"crises", "crisis",
				"apotheoses", "apotheosis",
				"stimuli", "stimulus",
				"corpora", "corpus",
				"women", "woman",
				"congressmen", "congressman",
				"aldermen", "alderman",
				"freshmen", "freshman",
				"firemen", "fireman",
				"grandchildren", "grandchild",
				"menus", "menu",
				"gurus", "guru",
				"hardness", "hardness",
				"shortness", "shortness",
				"dreariness", "dreariness",
				"unwillingness", "unwillingness",
				"fish", "fish",
				"ooze", "ooze",
				"enterprises", "enterprise",
				"treatises", "treatise",
				"houses", "house",
				"chemises", "chemise",
				"aquatics", "aquatics",
				"mechanics", "mechanics",
				"quarters", "quarter",
				"dazes", "daze",
				"hives", "hive",
				"dives", "dive",
				"octopuses", "octopus",
				"abalone", "abalone",
				"wildlife", "wildlife",
				"beliefs", "belief",
				"prognoses", "prognosis",
				"whizzes", "whiz",
				"geese", "goose",
				"femurs", "femur",
				"smallpox", "smallpox",
				"motifs", "motif",
				"moose", "moose",
				"lives", "life",
				"additives", "additive",
				"epochs", "epoch",
				"ranchs", "ranch",
				"alcoves", "alcove",
				"goddesses", "goddess",
				"tresses", "tress",
				"murderesses", "murderess",
				"memories", "memory",
				
				// TODO: remove dups
				
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
				"clones", "clone",
				//according to Cambridge dictionary clone is n. [c]
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
				"impulses", "impulse",
				"pelvises", "pelvis",
				"fetishes", "fetish",
				"abysses", "abyss",
				"gashes", "gash",
				"directives", "directive",
				"calories", "calorie",
				"moves", "move",
				"expanses", "expanse",
				"briefcases", "briefcase",
				"beaux", "beau",
				"milieux", "milieu",
				//"clones", "clone",
		};

		String res1, res2;
		boolean res3, dbug = false;
		
		//testPairs = new String[] {"stimuli", "stimulus"}; // test one

		for (int i = 0; i < testPairs.length; i += 2) {

			if (dbug) console.log(testPairs[i] + "/" + testPairs[i + 1]);

			res1 = Inflector.singularize(testPairs[i], RiTa.opts("dbug", dbug));
			res2 = Inflector.pluralize(testPairs[i + 1], RiTa.opts("dbug", dbug));
			res3 = Inflector.isPlural(testPairs[i], RiTa.opts("dbug", dbug));

			// singularize
			assertEquals(testPairs[i + 1], res1, "FAIL: singularize(" + testPairs[i]
					+ ") was " + res1 + ", but expected " + testPairs[i + 1] + "\n        "
					+ "pluralize(" + testPairs[i + 1] + ") was " + res2 + "\n\n");

			// pluralize
			assertEquals(testPairs[i], res2, "FAIL: pluralize(" + testPairs[i + 1]
					+ ") was " + res2 + ", but expected " + testPairs[i] + "\n        "
					+ "singularize(" + testPairs[i] + ") was " + res1 + "\n\n");

			// pluralize plural word should return input
			assertEquals(testPairs[i], Inflector.pluralize(testPairs[i]));

			// isPlural
			assertTrue(res3, "FAIL: isPlural(" + testPairs[i] + ") was false\n\n");
		}
	}

	@Test
	public void callAnalyzeLts() {
		// failing bc of testComputePhones (above)
		Map<String, String> feats = RiTa.analyze("cloze");
		eq(feats.get("pos"), "nn");
		eq(feats.get("tokens"), "cloze");
		eq(feats.get("syllables"), "k-l-ow-z");
	}

	@Test
	public void callAnalyze() {

		Map<String, String> feats;

		// analyze()
		Map<String, String> hm = new HashMap<String, String>();
		hm.put("pos", "");
		hm.put("phones", "");
		hm.put("tokens", "");
		hm.put("stresses", "");
		hm.put("syllables", "");

		assertEquals(RiTa.analyze(""), hm);

		feats = RiTa.analyze("clothes");
		eq(feats.get("pos"), "nns");
		eq(feats.get("tokens"), "clothes");
		eq(feats.get("syllables"), "k-l-ow-dh-z");

		feats = RiTa.analyze("the clothes");
		eq(feats.get("pos"), "dt nns");
		eq(feats.get("tokens"), "the clothes");
		eq(feats.get("syllables"), "dh-ah k-l-ow-dh-z");

		feats = RiTa.analyze("chevrolet");
		eq(feats.get("tokens"), "chevrolet");
		eq(feats.get("syllables"), "sh-eh-v/r-ow/l-ey");
		
		
		feats = RiTa.analyze("abandon");
//		System.out.println(feats);
		eq(feats.get("pos"), "vb");
		eq(feats.get("phones"), "ah-b-ae-n-d-ah-n");
		eq(feats.get("tokens"), "abandon");
		eq(feats.get("stresses"), "0/1/0");
		eq(feats.get("syllables"), "ah/b-ae-n/d-ah-n");
	}

	//@Test
	//sync from js analyzer-tests.js line 57
	// TODO: (then SYNC:) See https://github.com/dhowe/rita/issues/65
	public void todo() {
		Map<String, String> feats = RiTa.analyze("off-site");
		assertEquals("jj", feats.get("pos"));
		assertEquals("ah-b-ae-n-d-ah-n", feats.get("phones"));
		assertEquals("abandon", feats.get("tokens"));
		assertEquals("0/1/0", "stresses");
		assertEquals("ah/b-ae-n/d-ah-n", feats.get("syllables"));

		feats = RiTa.analyze("oft-cited");
		assertEquals("jj", feats.get("pos"));
		assertEquals("ah-b-ae-n-d-ah-n", feats.get("phones"));
		assertEquals("abandon", feats.get("tokens"));
		assertEquals("0/1/0", "stresses");
		assertEquals("ah/b-ae-n/d-ah-n", feats.get("syllables"));
	}

	@Test
	public void callAnalyzeWord() {

		String[] data;
		data = RiTa.analyzer.analyzeWord("abandon");
		//System.out.println(Arrays.asList(data));
		String phones = data[0];
		String stresses = data[1];
		String syllables = data[2];
		eq(phones, "ah-b-ae-n-d-ah-n ");
		eq(stresses,"0/1/0 ");
		eq(syllables, "ah/b-ae-n/d-ah-n ");
	}
		
	@Test
	public void callStresses() {

		String result, answer;

		result = RiTa.stresses("");
		answer = "";
		eq(result, answer);

		result = RiTa.stresses("The emperor had no clothes on");
		answer = "0 1/0/0 1 1 1 1";
		eq(result, answer);

		result = RiTa.stresses("The emperor had no clothes on.");
		answer = "0 1/0/0 1 1 1 1 .";
		eq(result, answer);

		result = RiTa.stresses("The emperor had no clothes on. The King is fat.");
		answer = "0 1/0/0 1 1 1 1 . 0 1 1 1 .";
		eq(result, answer);

		result = RiTa.stresses("to preSENT, to exPORT, to deCIDE, to beGIN");
		answer = "1 1/0 , 1 1/0 , 1 0/1 , 1 0/1";
		eq(result, answer);

		result = RiTa.stresses("to present, to export, to decide, to begin");
		answer = "1 1/0 , 1 1/0 , 1 0/1 , 1 0/1";
		eq(result, answer);

		result = RiTa.stresses("The dog ran faster than the other dog.  But the other dog was prettier.");
		answer = "0 1 1 1/0 1 0 1/0 1 . 1 0 1/0 1 1 1/0/0 .";
		eq(result, answer);

		eq(RiTa.stresses("chevrolet"), "0/0/1");
		eq(RiTa.stresses("women"), "1/0");
		eq(RiTa.stresses("genuine"), "1/0/0");
	}

	@Test
	public void callPhonemes() {

		String result, answer;

		result = RiTa.phones("");
		answer = "";
		eq(result, answer);

		result = RiTa.phones("b");
		answer = "b";
		eq(result, answer);

		result = RiTa.phones("B");
		answer = "b";
		eq(result, answer);

		result = RiTa.phones("The");
		answer = "dh-ah";
		eq(result, answer);

		result = RiTa.phones("said");
		answer = "s-eh-d";
		eq(result, answer);

		result = RiTa.phones("The.");
		answer = "dh-ah .";
		eq(result, answer);

		result = RiTa.phones("The boy jumped over the wild dog.");
		answer = "dh-ah b-oy jh-ah-m-p-t ow-v-er dh-ah w-ay-l-d d-ao-g .";
		eq(result, answer);

		result = RiTa.phones("The boy ran to the store.");
		answer = "dh-ah b-oy r-ae-n t-uw dh-ah s-t-ao-r .";
		eq(result, answer);

		result = RiTa.phones("The dog ran faster than the other dog.  But the other dog was prettier.");
		answer = "dh-ah d-ao-g r-ae-n f-ae-s-t-er dh-ae-n dh-ah ah-dh-er d-ao-g . b-ah-t dh-ah ah-dh-er d-ao-g w-aa-z p-r-ih-t-iy-er .";
		eq(result, answer);

		result = RiTa.phones("flowers");
		answer = "f-l-aw-er-z";
		eq(result, answer);

		result = RiTa.phones("quiche");
		answer = "k-iy-sh";
		eq(result, answer);

		result = RiTa.phones("mice");
		answer = "m-ay-s";
		eq(result, answer);

		eq(RiTa.phones("chevrolet"), "sh-eh-v-r-ow-l-ey");
		eq(RiTa.phones("women"), "w-ih-m-eh-n");
		eq(RiTa.phones("genuine"), "jh-eh-n-y-uw-w-ah-n");

	}

	@Test
	public void callSyllableLts() {
		String result = RiTa.syllables("The Laggin");
		eq(result, "dh-ah l-ae/g-ih-n", "got '" + result + "'");
	}

	@Test
	public void callSyllables() {
		// syllables()

		eq(RiTa.syllables("clothes"), "k-l-ow-dh-z");

		eq(RiTa.syllables(""), "");
		eq(RiTa.syllables("chevrolet"), "sh-eh-v/r-ow/l-ey");

		eq(RiTa.syllables("women"), "w-ih/m-eh-n");
		eq(RiTa.syllables("genuine"), "jh-eh-n/y-uw/w-ah-n");

		String input, expected;

		input = "The emperor had no clothes on.";
		expected = "dh-ah eh-m/p-er/er hh-ae-d n-ow k-l-ow-dh-z aa-n .";
		eq(RiTa.syllables(input), expected);

		input = "The dog ran faster than the other dog. But the other dog was prettier.";
		expected = "dh-ah d-ao-g r-ae-n f-ae/s-t-er dh-ae-n dh-ah ah/dh-er d-ao-g . b-ah-t dh-ah ah/dh-er d-ao-g w-aa-z p-r-ih/t-iy/er .";
		eq(RiTa.syllables(input), expected);

		input = "The dog ran faster than the other dog. But the other dog was prettier.";
		expected = "dh-ah d-ao-g r-ae-n f-ae/s-t-er dh-ae-n dh-ah ah/dh-er d-ao-g . b-ah-t dh-ah ah/dh-er d-ao-g w-aa-z p-r-ih/t-iy/er .";
		eq(RiTa.syllables(input), expected);

		input = "The emperor had no clothes on.";
		expected = "dh-ah eh-m/p-er/er hh-ae-d n-ow k-l-ow-dh-z aa-n .";
		eq(RiTa.syllables(input), expected);

		RiTa.SILENCE_LTS = true;
		//System.out.println(RiTa.syllables("The Laggin Dragon"));
		eq(RiTa.syllables("The Laggin Dragon"), "dh-ah l-ae/g-ih-n d-r-ae/g-ah-n");
		RiTa.SILENCE_LTS = false;
	}

	@Test
	public void pluralizePhrases() {
		String input;
		String expected;

		input = "set of choice";
		expected = "set of choices";
		assertEquals(expected, RiTa.pluralize(input));

		input = "bag of chocolate";
		expected = "bag of chocolates";
		assertEquals(expected, RiTa.pluralize(input));

		input = "gaggle of goose";
		expected = "gaggle of geese";
		assertEquals(expected, RiTa.pluralize(input));
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}
}
