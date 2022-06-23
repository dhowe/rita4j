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
				"knives", "knife"
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
		
		feats = RiTa.analyze("yoyo");
		eq(feats.get("pos"), "nn");
		eq(feats.get("tokens"), "yoyo");
		eq(feats.get("syllables"), "y-ow/y-ow");
		
	    feats = RiTa.analyze("1903");
		eq(feats.get("pos"), "cd");
		eq(feats.get("phones"), "w-ah-n-n-ih-n-z-ih-r-ow-th-r-iy");
		eq(feats.get("tokens"), "1903");
		eq(feats.get("stresses"), "0/0/0/0/0");
		eq(feats.get("syllables"), "w-ah-n/n-ih-n/z-ih/r-ow/th-r-iy");

		feats = RiTa.analyze("bit");
		eq(feats.get("pos"), "vbd");

		// 'bit': as a vbd
		feats = RiTa.analyze("It bit me.");
		eq(feats.get("pos"), "prp vbd prp .");
		// 'bit': as an nn
		feats = RiTa.analyze("Give the duck a bit of bread.");
		eq(feats.get("pos"), "vb dt nn dt nn in nn .");
		
		feats = RiTa.analyze("broke");
		eq(feats.get("pos"), "vbd");
		feats = RiTa.analyze("I broke my leg.");
		eq(feats.get("pos"), "prp vbd prp$ nn .");

		feats = RiTa.analyze("The show has ended.");
		eq(feats.get("pos"), "dt nn vbz vbn .");

		feats = RiTa.analyze("She oversaw it.");
		eq(feats.get("pos"), "prp vbd prp .");

		feats = RiTa.analyze("She remade this video.");
		eq(feats.get("pos"), "prp vbd dt nn .");
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
		
	    data = RiTa.analyzer.analyzeWord("z");
	    phones = data[0];
	    stresses = data[1];
	    syllables = data[2];
	    eq(phones, "z ");
	    eq(stresses, "0 ");
	    eq(syllables, "z ");
	    
	    data = RiTa.analyzer.analyzeWord("cloze");
	    phones = data[0];
	    stresses = data[1];
	    syllables = data[2];
	    eq(phones, "k-l-ow-z ");
	    eq(stresses, "1 ");
	    eq(syllables, "k-l-ow-z ");
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

	@Test
	public void doNothingWhenInputToPluralizeIsPlural() {
		String[] tests = new String[] { "tidings", "schnapps", "canvases", "censuses", "bonuses", "isthmuses",
				"thermoses", "circuses", "tongs", "emeriti" };
		for (int i = 0; i < tests.length; i++) {
			assertEquals(tests[i], RiTa.pluralize(tests[i]));
		}
	}

	@Test
	public void returnTrueForPluralNouns() {
		String[] tests = new String[] { "tidings", "schnapps", "canvases", "censuses", "bonuses", "isthmuses",
				"thermoses", "circuses", "tongs", "emeriti" };
		for (int i = 0; i < tests.length; i++) {
			assertTrue(Inflector.isPlural(tests[i]));
		}
	}

	@Test
	public void handleSinglarNounsInTaggerAllTags() {
		String[] tests = new String[] { "tiding", "census", "bonus", "thermos", "circus" };
		for (int i = 0; i < tests.length; i++) {
			String[] tags = RiTa.tagger.allTags(tests[i]);
			assertTrue(Arrays.asList(tags).contains("nn"));
		}
	}
	
	@Test
	public void handleUncountables() {
		String[] tests = new String[] { "accommodation", "advertising", "aid", "art", "bread", "business", "butter",
				"calm", "cash", "cheese", "childhood", "clothing ", "coffee", "content", "corruption", "courage",
				"currency", "damage", "danger", "determination", "electricity", "employment", "energy",
				"entertainment", "failure", "fire", "flour", "food", "freedom", "friendship", "fuel",
				"genetics", "hair", "harm", "hospitality ", "housework", "humour", "imagination", "importance",
				"innocence", "intelligence", "juice", "kindness", "labour", "lack", "laughter", "leisure", "literature",
				"litter", "love", "magic", "metal", "motherhood", "motivation", "nature", "nutrition", "obesity", "oil",
				"old age", "paper", "patience", "permission", "pollution", "poverty", "power", "production", "progress",
				"pronunciation", "publicity", "quality", "quantity", "racism", "rain", "relaxation", "research",
				"respect", "room", "rubbish", "safety", "salt", "sand", "seafood", "shopping", "silence",
				"smoke", "snow", "soup", "speed", "spelling", "stress ", "sugar", "sunshine", "tea", "time",
				"tolerance", "trade", "transportation", "travel", "trust", "understanding", "unemployment", "usage",
				"vision", "water", "wealth", "weight", "welfare", "width", "wood", "yoga", "youth", "homecare",
				"childcare", "fanfare", "healthcare", "medicare", "honey", "pasta"};
		for (int i = 0; i < tests.length; i++) {
			assertEquals(tests[i], RiTa.pluralize(tests[i]));
			assertTrue(Inflector.isPlural(tests[i]));
		}
	}
	
	@Test
	public void handleDashes() {
		Map<String, String> feats;
		String sentence;

		// U+2012
		sentence = "Teaching‒the profession has always appealed to me.";
		feats = RiTa.analyze(sentence);
		eq(feats.get("pos"), "vbg ‒ dt nn vbz rb vbd to prp .");
		eq(feats.get("tokens"), "Teaching \u2012 the profession has always appealed to me .");
		assertEquals(sentence, RiTa.untokenize(RiTa.tokenize(sentence)));
		assertArrayEquals(feats.get("tokens").split(" "), RiTa.tokenize(sentence));

		// U+2013
		sentence = "The teacher assigned pages 101–181 for tonight's reading material.";
		feats = RiTa.analyze(sentence);
		eq(feats.get("pos"), "dt nn vbn nns cd \u2013 cd in nns vbg jj .");
		eq(feats.get("tokens"), "The teacher assigned pages 101 – 181 for tonight's reading material .");
		assertEquals(sentence, RiTa.untokenize(RiTa.tokenize(sentence)));
		assertArrayEquals(feats.get("tokens").split(" "), RiTa.tokenize(sentence));

		// U+2014
		sentence = "Type two hyphens—without a space before, after, or between them.";
		feats = RiTa.analyze(sentence);
		eq(feats.get("pos"), "nn cd nns \u2014 in dt nn in , in , cc in prp .");
		eq(feats.get("tokens"), "Type two hyphens — without a space before , after , or between them .");
		assertEquals(sentence, RiTa.untokenize(RiTa.tokenize(sentence)));
		assertArrayEquals(feats.get("tokens").split(" "), RiTa.tokenize(sentence));

		// U+2014
		sentence = "Phones, hand-held computers, and built-in TVs—each a possible distraction—can lead to a dangerous situation if used while driving.";
		feats = RiTa.analyze(sentence);
		eq(feats.get("pos"),
				"nns , nn - vbn nns , cc vbn - in nnps \u2014 dt dt jj nn \u2014 md vb to dt jj nn in vbn in vbg .");
		eq(feats.get("tokens"),
				"Phones , hand - held computers , and built - in TVs — each a possible distraction — can lead to a dangerous situation if used while driving .");
		assertEquals(sentence, RiTa.untokenize(RiTa.tokenize(sentence)));
		assertArrayEquals(feats.get("tokens").split(" "), RiTa.tokenize(sentence));

		// "--"
		sentence = "He is afraid of two things--spiders and senior prom.";
		feats = RiTa.analyze(sentence);
		eq(feats.get("pos"), "prp vbz jj in cd nns -- nns cc jj nn .");
		eq(feats.get("tokens"), "He is afraid of two things -- spiders and senior prom .");
		assertEquals(sentence, RiTa.untokenize(RiTa.tokenize(sentence)));
		assertArrayEquals(feats.get("tokens").split(" "), RiTa.tokenize(sentence));
	}

	@Test
	// https://github.com/dhowe/rita/issues/65
	public void handleHyphenatedWords(){
		Map<String,String> feats = RiTa.analyze("off-site");
		assertEquals("in - nn", feats.get("pos"));
		assertEquals("ao-f - s-ay-t", feats.get("phones"));
		assertEquals("off - site", feats.get("tokens"));
		assertEquals("1 - 1", feats.get("stresses"));
		assertEquals("ao-f - s-ay-t", feats.get("syllables"));
		assertEquals("off-site", RiTa.untokenize(RiTa.tokenize("off-site")));
		assertArrayEquals(new String[]{"off", "-", "site"}, RiTa.tokenize("off-site"));
		
		feats = RiTa.analyze("oft-cited");
		assertEquals("rb - vbd", feats.get("pos"));
		assertEquals("ao-f-t - s-ih-t-ah-d", feats.get("phones"));
		assertEquals("1 - 1/0", feats.get("stresses"));
		assertEquals("ao-f-t - s-ih/t-ah-d", feats.get("syllables"));
		assertArrayEquals(new String[]{"oft", "-", "cited"}, RiTa.tokenize("oft-cited"));
		
		feats = RiTa.analyze("deeply-nested");
		assertEquals("rb - vbd", feats.get("pos"));
		assertEquals("d-iy-p-l-iy - n-eh-s-t-ah-d", feats.get("phones"));
		assertEquals("1/0 - 1/0", feats.get("stresses"));
		assertEquals("d-iy-p/l-iy - n-eh/s-t-ah-d", feats.get("syllables"));
		assertArrayEquals(new String[]{"deeply", "-", "nested"}, RiTa.tokenize("deeply-nested"));
		
		feats = RiTa.analyze("father-in-law");
		assertEquals("nn - in - nn", feats.get("pos"));
		assertEquals("f-aa-dh-er - ih-n - l-ao", feats.get("phones"));
		assertEquals("father - in - law", feats.get("tokens"));
		assertEquals("1/0 - 0 - 1", feats.get("stresses"));
		assertEquals("f-aa/dh-er - ih-n - l-ao", feats.get("syllables"));
		assertEquals("father-in-law", RiTa.untokenize(RiTa.tokenize("father-in-law")));
		assertArrayEquals(new String[]{"father", "-", "in", "-", "law"}, RiTa.tokenize("father-in-law"));
		
		feats = RiTa.analyze("up-to-date");
		assertEquals("in - to - nn", feats.get("pos"));
		assertEquals("ah-p - t-uw - d-ey-t", feats.get("phones"));
		assertEquals("up - to - date", feats.get("tokens"));
		assertEquals("1 - 1 - 1", feats.get("stresses"));
		assertEquals("ah-p - t-uw - d-ey-t", feats.get("syllables"));
		assertEquals("up-to-date", RiTa.untokenize(RiTa.tokenize("up-to-date")));
		assertArrayEquals(new String[]{"up", "-", "to", "-", "date"}, RiTa.tokenize("up-to-date"));
		
		feats = RiTa.analyze("state-of-the-art");
		assertEquals("jj - in - dt - nn", feats.get("pos"));
		assertEquals("s-t-ey-t - ah-v - dh-ah - aa-r-t", feats.get("phones"));
		assertEquals("state - of - the - art", feats.get("tokens"));
		assertEquals("1 - 1 - 0 - 1", feats.get("stresses"));
		assertEquals("s-t-ey-t - ah-v - dh-ah - aa-r-t", feats.get("syllables"));
		assertEquals("state-of-the-art", RiTa.untokenize(RiTa.tokenize("state-of-the-art")));
		assertArrayEquals(new String[]{"state", "-", "of", "-", "the", "-", "art"}, RiTa.tokenize("state-of-the-art"));
		
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}
}
