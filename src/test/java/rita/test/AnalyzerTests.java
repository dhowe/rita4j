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

		Map<String, String> data;
		data = RiTa.analyzer.analyzeWord("abandon");
		//System.out.println(Arrays.asList(data));
		String phones = data.get("phones");
		String stresses = data.get("stresses");
		String syllables = data.get("syllables");
		eq(phones, "ah-b-ae-n-d-ah-n");
		eq(stresses,"0/1/0");
		eq(syllables, "ah/b-ae-n/d-ah-n");
		
	    data = RiTa.analyzer.analyzeWord("z");
	    phones = data.get("phones");
	    stresses = data.get("stresses");
	    syllables = data.get("syllables");
	    eq(phones, "z");
	    eq(stresses, "0");
	    eq(syllables, "z");
	    
	    data = RiTa.analyzer.analyzeWord("cloze");
	    phones = data.get("phones");
	    stresses = data.get("stresses");
	    syllables = data.get("syllables");
	    eq(phones, "k-l-ow-z");
	    eq(stresses, "1");
	    eq(syllables, "k-l-ow-z");
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
				"nns , jj nns , cc jj nnps \u2014 dt dt jj nn \u2014 md vb to dt jj nn in vbn in vbg .");
		eq(feats.get("tokens"),
				"Phones , hand-held computers , and built-in TVs — each a possible distraction — can lead to a dangerous situation if used while driving .");
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
	public void handleHyphenatedWords() {
		String[] pool1 = new String[] { "mother-in-law", "father-in-law", "sister-in-law", "brother-in-law", "off-site", "up-to-date", "state-of-the-art", "self-esteem", "merry-go-round", "man-eating", "twenty-one", "twenty-first", "thirty-second", "happy-go-lucky", "editor-in-chief", "over-the-counter", "long-term", "high-speed", "in-depth", "full-length", "part-time", "sun-dried", "well-off", "well-known", "gift-wrap", "follow-up", "well-being", "good-looking", "knee-length", "runner-up", "tip-off", "blush-on", "sugar-free", "ice-cold", "far-flung", "high-rise", "life-size", "king-size", "next-door", "full-time", "forty-acre", "on-campus", "family-run", "low-grade", "round-trip" };
		String[][] feats1 = new String[][] {
			{  "nn",  "mother-in-law",  "m-ah-dh-er-ih-n-l-ao",  "1/0-0-1",  "m-ah/dh-er/ih-n/l-ao" },
			{  "nn",  "father-in-law",  "f-aa-dh-er-ih-n-l-ao",  "1/0-0-1",  "f-aa/dh-er/ih-n/l-ao" },
			{  "nn",  "sister-in-law",  "s-ih-s-t-er-ih-n-l-ao",  "1/0-0-1",  "s-ih/s-t-er/ih-n/l-ao" },
			{  "nn",  "brother-in-law",  "b-r-ah-dh-er-ih-n-l-ao",  "1/0-0-1",  "b-r-ah/dh-er/ih-n/l-ao" },
			{  "jj",  "off-site",  "ao-f-s-ay-t",  "1-1",  "ao-f/s-ay-t" },
			{  "jj",  "up-to-date",  "ah-p-t-uw-d-ey-t",  "1-1-1",  "ah-p/t-uw/d-ey-t" },
			{  "nn",  "state-of-the-art",  "s-t-ey-t-ah-v-dh-ah-aa-r-t",  "1-1-0-1",  "s-t-ey-t/ah-v/dh-ah/aa-r-t" },
			{  "nn",  "self-esteem",  "s-eh-l-f-ah-s-t-iy-m",  "1-0/1",  "s-eh-l-f/ah/s-t-iy-m" },
			{  "nn",  "merry-go-round",  "m-eh-r-iy-g-ow-r-aw-n-d",  "1/0-1-1",  "m-eh/r-iy/g-ow/r-aw-n-d" },
			{  "jj",  "man-eating",  "m-ae-n-iy-t-ih-ng",  "1-1/0",  "m-ae-n/iy/t-ih-ng" },
			{  "cd",  "twenty-one",  "t-w-eh-n-t-iy-w-ah-n",  "1/0-1",  "t-w-eh-n/t-iy/w-ah-n" },
			{  "jj",  "twenty-first",  "t-w-eh-n-t-iy-f-er-s-t",  "1/0-1",  "t-w-eh-n/t-iy/f-er-s-t" },
			{  "jj",  "thirty-second",  "th-er-t-iy-s-eh-k-ah-n-d",  "1/0-1/0",  "th-er/t-iy/s-eh/k-ah-n-d" },
			{  "jj",  "happy-go-lucky",  "hh-ae-p-iy-g-ow-l-ah-k-iy",  "1/0-1-1/0",  "hh-ae/p-iy/g-ow/l-ah/k-iy" },
			{  "nn",  "editor-in-chief",  "eh-d-ah-t-er-ih-n-ch-iy-f",  "1/0/0-0-1",  "eh/d-ah/t-er/ih-n/ch-iy-f" },
			{  "jj",  "over-the-counter",  "ow-v-er-dh-ah-k-aw-n-t-er",  "1/0-0-1/0",  "ow/v-er/dh-ah/k-aw-n/t-er" },
			{  "jj",  "long-term",  "l-ao-ng-t-er-m",  "1-1",  "l-ao-ng/t-er-m" },
			{  "jj",  "high-speed",  "hh-ay-s-p-iy-d",  "1-1",  "hh-ay/s-p-iy-d" },
			{  "jj",  "in-depth",  "ih-n-d-eh-p-th",  "0-1",  "ih-n/d-eh-p-th" },
			{  "jj",  "full-length",  "f-uh-l-l-eh-ng-k-th",  "1-1",  "f-uh-l/l-eh-ng-k-th" },
			{  "jj",  "part-time",  "p-aa-r-t-t-ay-m",  "1-1",  "p-aa-r-t/t-ay-m" },
			{  "jj",  "sun-dried",  "s-ah-n-d-r-ay-d",  "1-1",  "s-ah-n/d-r-ay-d" },
			{  "jj",  "well-off",  "w-eh-l-ao-f",  "1-1",  "w-eh-l/ao-f" },
			{  "jj",  "well-known",  "w-eh-l-n-ow-n",  "1-1",  "w-eh-l/n-ow-n" },
			{  "nn",  "gift-wrap",  "g-ih-f-t-r-ae-p",  "1-1",  "g-ih-f-t/r-ae-p" },
			{  "nn",  "follow-up",  "f-aa-l-ow-ah-p",  "1/0-1",  "f-aa/l-ow/ah-p" },
			{  "nn",  "well-being",  "w-eh-l-b-iy-ih-ng",  "1-1/0",  "w-eh-l/b-iy/ih-ng" },
			{  "jj",  "good-looking",  "g-uh-d-l-uh-k-ih-ng",  "1-1/0",  "g-uh-d/l-uh/k-ih-ng" },
			{  "jj",  "knee-length",  "n-iy-l-eh-ng-k-th",  "1-1",  "n-iy/l-eh-ng-k-th" },
			{  "nn",  "runner-up",  "r-ah-n-er-ah-p",  "1/0-1",  "r-ah/n-er/ah-p" },
			{  "nn",  "tip-off",  "t-ih-p-ao-f",  "1-1",  "t-ih-p/ao-f" },
			{  "nn",  "blush-on",  "b-l-ah-sh-aa-n",  "1-1",  "b-l-ah-sh/aa-n" },
			{  "jj",  "sugar-free",  "sh-uh-g-er-f-r-iy",  "1/0-1",  "sh-uh/g-er/f-r-iy" },
			{  "jj",  "ice-cold",  "ay-s-k-ow-l-d",  "1-1",  "ay-s/k-ow-l-d" },
			{  "jj",  "far-flung",  "f-aa-r-f-l-ah-ng",  "1-1",  "f-aa-r/f-l-ah-ng" },
			{  "nn",  "high-rise",  "hh-ay-r-ay-z",  "1-1",  "hh-ay/r-ay-z" },
			{  "jj",  "life-size",  "l-ay-f-s-ay-z",  "1-1",  "l-ay-f/s-ay-z" },
			{  "jj",  "king-size",  "k-ih-ng-s-ay-z",  "1-1",  "k-ih-ng/s-ay-z" },
			{  "jj",  "next-door",  "n-eh-k-s-t-d-ao-r",  "1-1",  "n-eh-k-s-t/d-ao-r" },
			{  "jj",  "full-time",  "f-uh-l-t-ay-m",  "1-1",  "f-uh-l/t-ay-m" },
			{  "jj",  "forty-acre",  "f-ao-r-t-iy-ey-k-er",  "1/0-1/0",  "f-ao-r/t-iy/ey/k-er" },
			{  "jj",  "on-campus",  "aa-n-k-ae-m-p-ah-s",  "1-1/0",  "aa-n/k-ae-m/p-ah-s" },
			{  "jj",  "family-run",  "f-ae-m-ah-l-iy-r-ah-n",  "1/0/0-1",  "f-ae/m-ah/l-iy/r-ah-n" },
			{  "jj",  "low-grade",  "l-ow-g-r-ey-d",  "1-1",  "l-ow/g-r-ey-d" },
			{  "jj",  "round-trip",  "r-aw-n-d-t-r-ih-p",  "1-1",  "r-aw-n-d/t-r-ih-p"}
		};
		for (int i = 0; i < feats1.length; i++) {
			String w = pool1[i];
			Map<String, String> feats = RiTa.analyze(w, RiTa.opts("dbug", true));
			assertEquals(feats1[i][0], feats.get("pos"), "fail at: " + pool1[i]);
			assertEquals(feats1[i][1], feats.get("tokens"));
			assertEquals(feats1[i][2], feats.get("phones"));
			assertEquals(feats1[i][3], feats.get("stresses"));
			assertEquals(feats1[i][4], feats.get("syllables"));
			assertArrayEquals(RiTa.tokenize(pool1[i]), feats.get("tokens").split(" "));
		}

		String[] pool2A = new String[] {"oft-cited", "deeply-nested", "empty-handed", "sergeant-at-arms", "left-handed", "long-haired", "breath-taking", "self-centered", "single-minded", "short-tempered", "one-sided", "warm-blooded", "cold-blooded", "bell-bottoms", "corn-fed", "able-bodied"};
		String[][] feats2A = new String[][] {
			{  "jj",  "oft-cited",  "ao-f-t-s-ih-t-ah-d",  "1-1/0",  "ao-f-t/s-ih/t-ah-d" },
			{  "jj",  "deeply-nested",  "d-iy-p-l-iy-n-eh-s-t-ah-d",  "1/0-1/0",  "d-iy-p/l-iy/n-eh/s-t-ah-d" },
			{  "jj",  "empty-handed",  "eh-m-p-t-iy-hh-ae-n-d-ah-d",  "1/0-1/0",  "eh-m-p/t-iy/hh-ae-n/d-ah-d" },
			{  "nn",  "sergeant-at-arms",  "s-aa-r-jh-ah-n-t-ae-t-aa-r-m-z",  "1/0-1-1",  "s-aa-r/jh-ah-n-t/ae-t/aa-r-m-z" },
			{  "jj",  "left-handed",  "l-eh-f-t-hh-ae-n-d-ah-d",  "1-1/0",  "l-eh-f-t/hh-ae-n/d-ah-d" },
			{  "jj",  "long-haired",  "l-ao-ng-hh-eh-r-d",  "1-1",  "l-ao-ng/hh-eh-r-d" },
			{  "jj",  "breath-taking",  "b-r-eh-th-t-ey-k-ih-ng",  "1-1/0",  "b-r-eh-th/t-ey/k-ih-ng" },
			{  "jj",  "self-centered",  "s-eh-l-f-s-eh-n-t-er-d",  "1-1/0",  "s-eh-l-f/s-eh-n/t-er-d" },
			{  "jj",  "single-minded",  "s-ih-ng-g-ah-l-m-ay-n-d-ah-d",  "1/0-1/0",  "s-ih-ng/g-ah-l/m-ay-n/d-ah-d" },
			{  "jj",  "short-tempered",  "sh-ao-r-t-t-eh-m-p-er-d",  "1-1/0",  "sh-ao-r-t/t-eh-m/p-er-d" },
			{  "jj",  "one-sided",  "w-ah-n-s-ay-d-ah-d",  "1-1/0",  "w-ah-n/s-ay/d-ah-d" },
			{  "jj",  "warm-blooded",  "w-ao-r-m-b-l-ah-d-ah-d",  "1-1/0",  "w-ao-r-m/b-l-ah/d-ah-d" },
			{  "jj",  "cold-blooded",  "k-ow-l-d-b-l-ah-d-ah-d",  "1-1/0",  "k-ow-l-d/b-l-ah/d-ah-d" },
			{  "nn",  "bell-bottoms",  "b-eh-l-b-aa-t-ah-m-z",  "1-1/0",  "b-eh-l/b-aa/t-ah-m-z" },
			{  "jj",  "corn-fed",  "k-ao-r-n-f-eh-d",  "1-1",  "k-ao-r-n/f-eh-d" },
			{  "jj",  "able-bodied",  "ey-b-ah-l-b-aa-d-iy-d",  "1/0-1/0",  "ey/b-ah-l/b-aa/d-iy-d"}
		};

		for (int i = 0; i < feats2A.length; i++) {
			Map<String, String> feats = RiTa.analyze(pool2A[i]);
			assertEquals(feats2A[i][0], feats.get("pos"));
			assertEquals(feats2A[i][1], feats.get("tokens"));
			assertEquals(feats2A[i][2], feats.get("phones"));
			assertEquals(feats2A[i][3], feats.get("stresses"));
			assertEquals(feats2A[i][4], feats.get("syllables"));
			assertArrayEquals(RiTa.tokenize(pool2A[i]), feats.get("tokens").split(" "));
		}

		String[] pool2B = new String[] {"de-emphasize", "re-apply", "ho-hum", "co-manage", "co-manager", "neo-liberalism", "u-turn", "x-ray", "a-frame", "high-tech", "nitty-gritty"};
		String[][] feats2B = new String[][] {
			{  "vb",  "de-emphasize",  "d-ih-eh-m-f-ah-s-ay-z",  "0-1/0/0",  "d-ih/eh-m/f-ah/s-ay-z" },
			{  "vb",  "re-apply",  "r-iy-ah-p-l-ay1",  "0-0/1",  "r-iy/ah/p-l-ay1" },
			{  "uh",  "ho-hum",  "hh-ow-hh-ah-m",  "0-1",  "hh-ow/hh-ah-m" },
			{  "vb",  "co-manage",  "k-ow-m-ae-n-ah-jh",  "0-1/0",  "k-ow/m-ae/n-ah-jh" },
			{  "nn",  "co-manager",  "k-ow-m-ae-n-ah-jh-er",  "0-1/0/0",  "k-ow/m-ae/n-ah/jh-er" },
			{  "nn",  "neo-liberalism",  "n-iy-ow-l-ih-b-er-ah-l-ih-z-ah-m",  "0/0-1/0/0/0/0",  "n-iy/ow/l-ih/b-er/ah/l-ih/z-ah-m" },
			{  "nn",  "u-turn",  "y-uw-t-er-n",  "1-1",  "y-uw/t-er-n" },
			{  "nn",  "x-ray",  "eh-k-z-r-ey",  "1-1",  "eh-k-z/r-ey" },
			{  "nn",  "a-frame",  "ey-f-r-ey-m",  "1-1",  "ey/f-r-ey-m" },
			{  "jj",  "high-tech",  "hh-ay-t-eh-k",  "1-1",  "hh-ay/t-eh-k" },
			{  "nn",  "nitty-gritty",  "n-ih-t-iy-g-r-ih-t-iy",  "1/0-1/0",  "n-ih/t-iy/g-r-ih/t-iy"}
		};

		for (int i = 0; i < feats2B.length; i++) {
			Map<String, String> feats = RiTa.analyze(pool2B[i]);
			assertEquals(feats2B[i][0], feats.get("pos"));
			assertEquals(feats2B[i][1], feats.get("tokens"));
			if (Arrays.asList(new String[]{"de-emphasize", "re-apply", "u-turn", "x-ray"}).contains(pool2B[i])) continue;
			assertEquals(feats2B[i][2], feats.get("phones"));
			assertEquals(feats2B[i][3], feats.get("stresses"));
			assertEquals(feats2B[i][4], feats.get("syllables"));
			assertArrayEquals(RiTa.tokenize(pool2B[i]), feats.get("tokens").split(" "));
		}

		String[] pool3 = new String[] { "co-op", "roly-poly", "topsy-turvy" };
		String[][] feats3 = new String[][] {
			{  "nn",  "co-op",  "k-ow-ah-p",  "0-0",  "k-ow/ah-p" },
			{  "jj",  "roly-poly",  "r-ow-l-iy-p-aa-l-iy",  "1/0-1/0",  "r-ow/l-iy/p-aa/l-iy" },
			{  "jj",  "topsy-turvy",  "t-aa-p-s-iy-t-er-v-iy",  "1/0-1/0",  "t-aa-p/s-iy/t-er/v-iy"}
		};

		for (int i = 0; i < feats3.length; i++) {
			Map<String, String> feats = RiTa.analyze(pool3[i]);
			assertEquals(feats3[i][0], feats.get("pos"));
			assertEquals(feats3[i][1], feats.get("tokens"));
			assertEquals(feats3[i][2], feats.get("phones"));
			assertEquals(feats3[i][3], feats.get("stresses"));
			assertEquals(feats3[i][4], feats.get("syllables"));
			assertArrayEquals(RiTa.tokenize(pool3[i]), feats.get("tokens").split(" "));
		}
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}
}
