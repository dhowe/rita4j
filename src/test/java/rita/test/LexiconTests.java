package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.regex.Pattern;

import org.antlr.v4.parse.ANTLRParser.throwsSpec_return;
import org.junit.jupiter.api.Test;

import rita.*;
import static rita.RiTa.*;

public class LexiconTests {

	// TODO: use opts() instead of creating Maps
	LexiconTests() {
		RiTa.SILENCE_LTS = true;
	}
	
	@Test
	public void callHasWord() {
		assertTrue(RiTa.hasWord("random"));
		assertTrue(RiTa.hasWord("dog"));
		assertTrue(RiTa.hasWord("dogs"));
		assertTrue(RiTa.hasWord("men"));
		assertTrue(RiTa.hasWord("happily"));
		assertTrue(RiTa.hasWord("play"));
		assertTrue(RiTa.hasWord("plays"));
		assertTrue(RiTa.hasWord("played"));
		assertTrue(RiTa.hasWord("written"));
		assertTrue(RiTa.hasWord("oxen"));
		assertTrue(RiTa.hasWord("mice"));

		// strict mode
		Map<String, Object> opts = opts("noDerivations", true);

		assertTrue(!RiTa.hasWord("dogs", opts));
		assertTrue(!RiTa.hasWord("played", opts));
		assertTrue(!RiTa.hasWord("cats", opts));

		// https://github.com/dhowe/rita/issues/139 
		assertTrue(!RiTa.hasWord("bunning"));
		assertTrue(!RiTa.hasWord("coyes"));
		assertTrue(!RiTa.hasWord("soes"));
		assertTrue(!RiTa.hasWord("knews"));
		assertTrue(!RiTa.hasWord("fastering"));
		assertTrue(!RiTa.hasWord("loosering"));
		assertTrue(!RiTa.hasWord("knews")); 
	
		assertTrue(!RiTa.hasWord("barkness")); 
		assertTrue(!RiTa.hasWord("horne")); 
		assertTrue(!RiTa.hasWord("haye")); 
	}

	@Test
	public void callRandomWord() {
		String result;
		assertThrows(RiTaException.class, () -> RiTa.randomWord(opts("pos", "xxx")));

		result = RiTa.randomWord();
		assertTrue(result.length() > 0);
		assertTrue(! result.equals(RiTa.randomWord()));

		result = RiTa.randomWord(opts("numSyllables", 3));
		assertTrue(result.length() > 0);

		result = RiTa.randomWord(opts("numSyllables", 5));
		assertTrue(result.length() > 0);
	}

	@Test
	public void callRandomWordWithRegex() {
		// regex as first parameter
		String result = RiTa.randomWord("^a");
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^a[a-zA-Z]+", result));

		result = RiTa.randomWord("^apple$");
		assertEquals("apple", result);

		result = RiTa.randomWord("le");
		assertTrue(Pattern.matches("[a-zA-Z]*le[a-zA-Z]*", result));

		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			results.add(RiTa.randomWord("^a"));
		}
		assertTrue(results.size() == 10);
		int idx = 0;
		while (idx < results.size() - 1) {
			if (results.get(idx).equals(results.get(idx + 1))) {
				results.remove(idx);
			}
			else {
				idx++;
			}
		}
		assertTrue(results.size() > 1);

		result = RiTa.randomWord(Pattern.compile("^a"));
		assertTrue(Pattern.matches("^a[a-zA-Z]*", result));

		result = RiTa.randomWord(Pattern.compile("^apple$"));
		assertEquals("apple", result);

		result = RiTa.randomWord(Pattern.compile("le"));
		assertTrue(Pattern.matches("[a-zA-Z]*le[a-zA-Z]*", result));

		while (results.size() > 0) {
			results.remove(0);
		}
		for (int i = 0; i < 10; i++) {
			results.add(RiTa.randomWord(Pattern.compile("^a")));
		}
		assertTrue(results.size() == 10);
		idx = 0;
		while (idx < results.size() - 1) {
			if (results.get(idx).equals(results.get(idx + 1))) {
				results.remove(idx);
			}
			else {
				idx++;
			}
		}
		assertTrue(results.size() > 1);
	}
	@Test
	public void callRandomWordWithStressRegex(){
		String result = RiTa.randomWord("0/1/0", opts("type", "stresses"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord("^0/1/0$", opts("type", "stresses"));
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		result = RiTa.randomWord("010", opts("type", "stresses"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord("^010$", opts("type", "stresses"));
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		result = RiTa.randomWord(Pattern.compile("0/1/0"), opts("type", "stresses"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord(Pattern.compile("^0/1/0/0$"), opts("type", "stresses"));
		assertEquals("0/1/0/0", RiTa.analyze(result).get("stresses"));
	}

	@Test
	public void callRandomWordWithPhonesRegex(){
		String result = RiTa.randomWord("^th", opts("type", "phones"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord("v$", opts("type", "phones"));
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord("^b-ih-l-iy-v$", opts("type", "phones"));
		assertEquals("believe", result);

		result = RiTa.randomWord("ae", opts("type", "phones"));
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("^th"), opts("type", "phones"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("v$"), opts("type", "phones"));
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("^b-ih-l-iy-v$"), opts("type", "phones"));
		assertEquals("believe", result);

		result = RiTa.randomWord(Pattern.compile("ae"), opts("type", "phones"));
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));	
	}

	@Test
	public void callRandomWordWithOptsRegex() {
		String result = RiTa.randomWord(opts("regex", "^a"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^a[a-zA-Z]+", result));

		result = RiTa.randomWord(opts("regex", Pattern.compile("^a")));
		assertTrue(Pattern.matches("^a[a-zA-Z]*", result));
		
		result = RiTa.randomWord(opts("type", "stresses", "regex", "0/1/0"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord(opts("type", "stresses", "regex", Pattern.compile("0/1/0")));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord(opts("type", "phones", "regex", "^th"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(opts("type", "phones", "regex", "^th"));
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));
	}

	@Test
	public void handleAnAugmentedLexicon() {
		Lexicon lexicon = RiTa.lexicon();
		lexicon.dict.put("deg", new String[] { "d-eh1-g", "nn" });
		lexicon.dict.put("wadly", new String[] { "w-ae1-d l-iy", "rb" });

		assertTrue(RiTa.hasWord("run"));
		assertTrue(RiTa.hasWord("walk"));
		assertTrue(RiTa.hasWord("deg"));
		assertTrue(RiTa.hasWord("wadly"));
		assertTrue(RiTa.isAlliteration("wadly", "welcome"));

		// remove two added entries
		lexicon.dict.remove("deg");
		lexicon.dict.remove("wadly");
	}

	@Test
	public void hanldeACustomLexicon() {
		Lexicon lexicon = RiTa.lexicon();
		Map<String, String[]> orig = lexicon.dict;
		Map<String, String[]> data = new HashMap<String, String[]>();
		data.put("dog", new String[] { "d-ao1-g", "nn" });
		data.put("cat", new String[] { "k-ae1-t", "nn" });
		data.put("happily", new String[] { "hh-ae1 p-ah l-iy", "rb" });
		data.put("walk", new String[] { "w-ao1-k", "vb vbp nn" });
		data.put("welcome", new String[] { "w-eh1-l k-ah-m", "jj nn vb vbp" });
		data.put("sadly", new String[] { "s-ae1-d l-iy", "rb" });

		for (Map.Entry<String, String[]> entry : data.entrySet())
			lexicon.dict.put(entry.getKey(), entry.getValue());

		assertTrue(lexicon.hasWord("run"));
		assertTrue(lexicon.hasWord("walk"));
		assertTrue(lexicon.isAlliteration("walk", "welcome", false));
		lexicon.dict = orig;
	}

	@Test
	public void callRandomWordWithPOS() {
		Map<String, Object> hm = opts("pos", "nns");
		//Map<String, Object> bad = new HashMap<String, Object>();

		assertThrows(RiTaException.class, () -> {
			String res = RiTa.randomWord(opts("pos", "xxx"));
		});

		String[] pos = { "nn", "jj", "jjr", "wp" };
		String result = "";
		hm = new HashMap<String, Object>();
		for (int j = 0; j < pos.length; j++) {
			result = RiTa.randomWord(opts("pos", pos[j]));
			String best = RiTa.tagger.allTags(result)[0];
			assertEquals(pos[j], best);
		}

		for (int i = 0; i < 5; i++) {
			result = RiTa.randomWord(opts("pos", "nns"));
			if (!Inflector.isPlural(result)) {
				System.err.println("Pluralize/Singularize problem: randomWord(nns) was '" + result + "' (" +
				"isPlural=" + Inflector.isPlural(result) + "), singularized is '" + RiTa.singularize(result) + "'");
			}
			String poss = RiTa.lexicon().posData(result);
			assertTrue(poss == null || poss.length() == 0 || !poss.contains("vbg"));
			assertTrue(!result.endsWith("ness"));
			assertTrue(!result.endsWith("isms"));
		}

		//////////////////////////////////////////////////////////////
		result = RiTa.randomWord(opts("pos", "v"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

		result = RiTa.randomWord(opts("pos", "n"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

		result = RiTa.randomWord(opts("pos", "nn"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

		result = RiTa.randomWord(opts("pos", "nns"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

		result = RiTa.randomWord(opts("pos", "v"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

		result = RiTa.randomWord(opts("pos", "rp"));
		assertTrue(result.length() > 0, "randomWord rp=" + result);

		List<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			results.add(RiTa.randomWord(opts("pos", "nns")));
		}
		assertEquals(10, results.size());

		int i = 0;
		while (i < results.size() - 1) {
			if (results.get(i).equals(results.get(i + 1))) {
				results.remove(i);
			} else {
				i ++;
			}
		}
		assertTrue(results.size() > 1);
	}

	@Test
	public void callRandomWordWithSyllabes() {
		int i = 0;
		String result = "";
		String syllables = "";
		int num = 0;

		for (i = 0; i < 10; i++) {
			result = RiTa.randomWord(opts("numSyllables", 3));
			syllables = RiTa.syllables(result);
			num = syllables.split(RiTa.SYLLABLE_BOUNDARY).length;
			assertTrue(result.length() > 0);
			assertTrue(num == 3); // "3 syllables: "
		}

		for (i = 0; i < 10; i++) {
			result = RiTa.randomWord(opts("numSyllables", 5));
			syllables = RiTa.syllables(result);
			num = syllables.split(RiTa.SYLLABLE_BOUNDARY).length;
			assertTrue(result.length() > 0);
			assertTrue(num == 5); // "5 syllables: "
		}

	}

	@Test
	public void callSearchWithouRegex() {
		// assertTrue(RiTa.search().length > 20000);
		assertEquals(11, RiTa.search(opts("limit", 11)).length);
		assertArrayEquals(new String[] {
			"abalone", "abandonment",
			"abbey", "abbot",
			"abbreviation", "abdomen",
			"abduction", "aberration",
			"ability", "abnormality"
		}, RiTa.search(opts("pos", "n")));
		assertArrayEquals(new String[] {
			"abashed", "abate",
      		"abbey", "abbot",
      		"abet", "abhor",
      		"abide", "abject",
      		"ablaze", "able"
		}, RiTa.search(opts("numSyllables", 2)));
		assertArrayEquals(new String[] {
			"abbey", "abbot",
      		"abode", "abscess",
      		"absence", "abstract",
     		"abuse", "abyss",
      		"accent", "access"
		}, RiTa.search(opts("numSyllables",2,"pos", "n")));
		assertArrayEquals(new String[] {
			"ace", "ache",
			"act", "age",
			"aid", "aide",
			"aim", "air",
			"aisle", "ale"
		}, RiTa.search(opts("numSyllables", 1, "pos","n")));
	}

	@Test
	public void callSearchWithLetters() {
		String[] results = {
				"elephant",
				"elephantine",
				"phantom",
				"sycophantic",
				"triumphant",
				"triumphantly"
		};
		assertArrayEquals(results, RiTa.search("phant"));
		assertArrayEquals(results, RiTa.search(Pattern.compile("phant")));
		//regex in options
		assertArrayEquals(results, RiTa.search(opts("regex", "phant")));
		assertArrayEquals(results, RiTa.search(opts("regex", Pattern.compile("phant"))));
	}

	@Test
	public void callSearchWithPhones() {

		String[] res1 = RiTa.search("f-ah-n-t", opts("type", "phones", "limit", 5));
		//System.out.println(Arrays.asList(res1));
		assertArrayEquals(res1, new String[] {
				"elephant",
				"infant",
				"infantile",
				"infantry",
				"oftentimes"
		});

		String[] res2 = RiTa.search("f-a[eh]-n-t", opts("type", "phones", "limit", 10));
		//System.out.println(Arrays.asList(res2));
		assertArrayEquals(res2, new String[] {
				"elephant",
				"elephantine",
				"fantasia",
				"fantasize",
				"fantastic",
				"fantastically",
				"fantasy",
				"infant",
				"infantile",
				"infantry"
		});

		res1 = RiTa.search(opts("type", "phones", "limit", 5, "regex", "f-ah-n-t"));
		assertArrayEquals(res1, new String[] {
				"elephant",
				"infant",
				"infantile",
				"infantry",
				"oftentimes"
		});

		res2 = RiTa.search(opts("type", "phones", "limit", 10, "regex", Pattern.compile("f-a[eh]-n-t")));
		assertArrayEquals(res2, new String[] {
				"elephant",
				"elephantine",
				"fantasia",
				"fantasize",
				"fantastic",
				"fantastically",
				"fantasy",
				"infant",
				"infantile",
				"infantry"
		});

	}

	@Test
	public void callSearchWithPhonesNoLimitShuffle(){
		String[] result = RiTa.search(opts("regex", "f-ah-n-t", "type", "phones", "limit", -1));
		assertArrayEquals(new String[] {
			"elephant",
			"infant",
			"infantile",
			"infantry",
			"oftentimes",
			"triumphant",
			"triumphantly"
		}, result);


		String[] res2 = RiTa.search(opts("regex", "f-ah-n-t", "type", "phones", "limit", -1, "shuffle", true));
		Arrays.sort(res2);
		assertArrayEquals(result, res2);
	}

	@Test
	public void callSearchWithPosPhonesSyllsLimit() { // TODO: use opts()
		assertArrayEquals(new String[] {"infant"}, RiTa.search("f-ah-n-t",opts("type", "phones", "pos", "n", "limit", 3, "numSyllables", 2)));
	}

	@Test
	public void callSearchWithPosPhonesLimit(){
		String[] actual = RiTa.search("f-ah-n-t", opts("type", "phone", "pos", "n", "limit", 3));
		assertArrayEquals(new String[]{"elephant", "infant", "infantry"}, actual);
		actual = RiTa.search(Pattern.compile("f-a[eh]-n-t"), opts("type", "phone", "pos", "v", "limit", 5));
		assertArrayEquals(new String[] {"fantasize"}, actual);
	}

	@Test
	public void callSearchWithSimplePosPhonesLimit(){
		assertArrayEquals(new String[]{"elephants", "infants", "infantries"}, RiTa.search("f-ah-n-t", opts("type", "phone", "pos", "nns", "limit", 3)));
		assertArrayEquals(new String[] {"fantasize"}, RiTa.search(Pattern.compile("f-a[eh]-n-t"), opts("type", "phone", "pos", "vb", "limit", 5)));
	}

	@Test
	public void callSearchWithPosStressLimit(){
		assertArrayEquals(new String[] {
			"abalone", "abandonment", "abbreviation", "abdomen", "abduction"
		}, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "n")));

		assertArrayEquals(new String[] {
			"abdomen", "abduction", "abortion", "abruptness", "absorber"
		}, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "n", "numSyllables", 3)));

		assertArrayEquals(new String[] {
			"abalone",
        "abandonments",
        "abbreviations",
        "abductions",
        "abilities"
		}, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "nns")));

		assertArrayEquals(new String[] {
			"abalone",
        "abandonments",
        "abbreviations",
        "abductions",
        "abilities"
		}, RiTa.search(Pattern.compile("010"), opts("type", "stresses", "limit", 5, "pos", "nns")));

		assertArrayEquals(new String[] {
			"abductions",
        "abortions",
        "absorbers",
        "abstentions",
        "abstractions"
		}, RiTa.search("010", opts("type", "stress","limit", 5, "pos", "nns", "numSyllables", 3)));
	}

	@Test
	public void callSearchWithStressLimit() {
		String[] expected;

		expected = new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism"
		};
		assertArrayEquals(expected, RiTa.search("010000", opts("type", "stresses", "limit", 5)));

		expected = new String[] {
				"colonialism",
				"imperialism",
				"materialism"
		};
		assertArrayEquals(expected, RiTa.search("010000", opts("type", "stresses", "limit", 5, "maxLength", 11)));

		expected = new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"conciliatory"
		};
		assertArrayEquals(expected, RiTa.search("010000", opts("type", "stresses", "limit", 5, "minLength", 12)));

		expected = new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism"
		};
		assertArrayEquals(expected, RiTa.search("0/1/0/0/0/0", opts("type", "stresses", "limit", 5)));
		assertArrayEquals(expected, RiTa.search(opts("regex", "010000","type", "stresses", "limit", 5)));

		expected = new String[] {
			"colonialism",
      		"imperialism",
      		"materialism"
		};

		assertArrayEquals(expected, RiTa.search(opts("regex", "010000", "type", "stresses", "limit", 5, "maxLength", 11 )));


		expected = new String[] {
			"accountability",
      		"anticipatory",
      		"appreciatively",
      		"authoritarianism",
      		"conciliatory"
		};

		assertArrayEquals(expected, RiTa.search(opts( "regex", "010000", "type", "stresses", "limit", 5, "minLength", 12)));

		expected = new String[] {
			"accountability",
      		"anticipatory",
      		"appreciatively",
      		"authoritarianism",
      		"colonialism"
		};

		assertArrayEquals(expected, RiTa.search(opts("regex", "0/1/0/0/0/0", "type", "stresses", "limit", 5 )));
	}

	@Test
	public void callSearchWithStressRegexLimit() {

		assertArrayEquals(RiTa.search(Pattern.compile("0/1/0/0/0/0"), opts("type", "stresses", "limit", 5)), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });

		assertArrayEquals(RiTa.search(opts("regex", Pattern.compile("0/1/0/0/0/0"),"type", "stresses", "limit", 5)), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });
	}

	@Test
	public void callRandomWordPosSyls() {
		// function fail(result, epos) {
		// let test = result.endsWith('es') ? result.substring(-2) : result;
		// let ent = RiTa.lexicon()[test];
		// return ('(' + epos + ') Fail: ' + result + ': expected ' + epos + ', got ' +
		// (ent ? ent[1] : 'null'));
		// }

		String result, syllables;
		RiTa.SILENCE_LTS = true;
		result = RiTa.randomWord(opts( "numSyllables", 3, "pos", "vbz"));
		assertTrue(result.length() > 0);
		syllables = RiTa.syllables(result);
		assertEquals(3, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
		assertTrue(RiTa.isVerb(result));

		result = RiTa.randomWord(opts( "numSyllables", 1, "pos", "n" ));
		assertTrue(result.length() > 0);
		syllables = RiTa.syllables(result);
		assertEquals(1, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
		assertTrue(RiTa.isNoun(result));

		result = RiTa.randomWord(opts("numSyllables", 1, "pos", "nns"));
		assertTrue(result.length() > 0);
		syllables = RiTa.syllables(result);
		assertEquals(1, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
		assertTrue(RiTa.isNoun(result));


		result = RiTa.randomWord(opts("numSyllables", 5, "pos", "nns"));
		assertTrue(result.length() > 0);
		syllables = RiTa.syllables(result);
		assertEquals(5, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
		assertTrue(RiTa.isNoun(result));

		RiTa.SILENCE_LTS = false;

	}

	@Test
	public void callAlliterationsNumSyllables() {

		String[] result = RiTa.alliterations("cat",
				opts("minLength", 1, "numSyllables", 7));

		assertArrayEquals(result, new String[] {
				"electrocardiogram", "electromechanical", "telecommunications"
		});

		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

	}

	@Test
	public void callAlliterationsPos() {

		String[] result = RiTa.alliterations("cat", opts("numSyllables", 7, "pos", "n"));

		assertArrayEquals(result, new String[] { "electrocardiogram", "telecommunications" });

		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

		result = RiTa.alliterations("dog", opts("minLength", 14, "pos", "v"));
		for (String string : result) {
			assertTrue(string.length() >= 14);
		}
		assertArrayEquals(result, new String[] { "disenfranchise" });

		result = RiTa.alliterations("dog", opts("minLength", 13, "pos", "rb", "limit", 11));
		for (String string : result) {
			assertTrue(string.length() >= 13);
		}
		assertArrayEquals(result, new String[] {
				"coincidentally",
				"conditionally",
				"confidentially",
				"contradictorily",
				"devastatingly",
				"expeditiously",
				"paradoxically",
				"predominantly",
				"traditionally",
				"unconditionally",
				"unpredictably" });
		
		result = RiTa.alliterations("freedom", opts("minLength", 14, "pos", "nns"));
		for (String string : result) {
			assertTrue(string.length() >= 14);
		}
		assertArrayEquals(result, new String[] {
				"featherbeddings",
				"fundamentalists",
				"pharmaceuticals",
				"photosyntheses",
				"reconfigurations",
				"sophistications" });

	}

	@Test
	public void callAlliterations() {

		String[] result;

		// TODO: make sure we have LTS cases in here

		result = RiTa.alliterations("");
		assertTrue(result.length < 1);

		result = RiTa.alliterations("#$%^&*");
		assertTrue(result.length < 1);

		result = RiTa.alliterations("umbrella", opts("silent", true));
		assertTrue(result.length < 1);

		result = RiTa.alliterations("cat", opts("limit", 100));
		assertTrue(result.length == 100);
		assertTrue(!Arrays.asList(result).contains("cat"));
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

		result = RiTa.alliterations("dog", opts("limit", 100));
		assertTrue(result.length == 100);
		assertTrue(!Arrays.asList(result).contains("dog"));
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog"));
		}

		result = RiTa.alliterations("dog", opts("minLength", 15));
		assertTrue(result.length > 0 && result.length < 5, "got length=" + result.length);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog")); // , "FAIL1: " + result[i]
		}

		result = RiTa.alliterations("cat", opts("minLength", 16));
		assertTrue(result.length > 0 && result.length < 15);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));// , "FAIL2: " + result[i]
		}

		result = RiTa.alliterations("khatt", opts("minLength", 16));
		assertTrue(result.length > 0 && result.length < 15);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));// , "FAIL2: " + result[i]
		}

		//for single letter words return []
		assertArrayEquals(new String[] { }, RiTa.alliterations("a"));
		assertArrayEquals(new String[] { }, RiTa.alliterations("I"));
		assertArrayEquals(new String[] { }, RiTa.alliterations("K"));
	}

	@Test
	public void callRhymes() {
		assertEquals(10, RiTa.rhymes("cat").length);
		assertTrue(Arrays.asList(RiTa.rhymes("cat")).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("yellow")).contains("mellow"));
		assertTrue(Arrays.asList(RiTa.rhymes("toy")).contains("boy"));
		assertTrue(Arrays.asList(RiTa.rhymes("crab")).contains("drab"));

		assertTrue(Arrays.asList(RiTa.rhymes("mouse")).contains("house"));
		assertFalse(Arrays.asList(RiTa.rhymes("apple")).contains("polo"));
		assertFalse(Arrays.asList(RiTa.rhymes("this")).contains("these"));

		assertFalse(Arrays.asList(RiTa.rhymes("hose")).contains("house"));
		assertFalse(Arrays.asList(RiTa.rhymes("sieve")).contains("mellow"));
		assertFalse(Arrays.asList(RiTa.rhymes("swag")).contains("grab"));

		assertTrue(Arrays.asList(RiTa.rhymes("tense", opts("limit", 100))).contains("sense"));
		assertTrue(Arrays.asList(RiTa.rhymes("shore", opts("limit", 100))).contains("more"));
		assertTrue(Arrays.asList(RiTa.rhymes("weight", opts("limit", 100))).contains("eight"));
		assertTrue(Arrays.asList(RiTa.rhymes("eight", opts("limit", 100))).contains("weight"));

		assertTrue(Arrays.asList(RiTa.rhymes("bog")).contains("fog"));
		assertTrue(Arrays.asList(RiTa.rhymes("dog")).contains("log"));

		//for single letter words return []
		assertArrayEquals(RiTa.rhymes("a"), new String[] { });
		assertArrayEquals(RiTa.rhymes("I"), new String[] { });
		assertArrayEquals(RiTa.rhymes("K"), new String[] { });
		assertArrayEquals(RiTa.rhymes("Z"), new String[] { });
		assertArrayEquals(RiTa.rhymes("B"), new String[] { });

	}

	
	@Test
	public void callRhymesPos() {

		assertFalse(Arrays.asList(RiTa.rhymes("cat", opts("pos", "v"))).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("yellow", opts("pos", "a"))).contains("mellow"));
		assertTrue(Arrays.asList(RiTa.rhymes("toy", opts("pos", "n"))).contains("boy"));
		assertFalse(Arrays.asList(RiTa.rhymes("sieve", opts("pos", "n"))).contains("give"));

		assertFalse(Arrays.asList(RiTa.rhymes("tense", opts("pos", "v"))).contains("condense"));
		assertFalse(Arrays.asList(RiTa.rhymes("crab", opts("pos", "n"))).contains("drab"));
		assertFalse(Arrays.asList(RiTa.rhymes("shore", opts("pos", "v"))).contains("more"));

		assertFalse(Arrays.asList(RiTa.rhymes("mouse", opts("pos", "nn"))).contains("house"));

		assertFalse(Arrays.asList(RiTa.rhymes("weight", opts("pos", "vb"))).contains("eight"));
		assertFalse(Arrays.asList(RiTa.rhymes("eight", opts("pos", "nn", "limit", 1000))).contains("weight"));

		assertFalse(Arrays.asList(RiTa.rhymes("apple", opts("pos", "v"))).contains("polo"));
		assertFalse(Arrays.asList(RiTa.rhymes("this", opts("pos", "v"))).contains("these"));

		assertFalse(Arrays.asList(RiTa.rhymes("hose", opts("pos", "v"))).contains("house"));
		assertFalse(Arrays.asList(RiTa.rhymes("sieve", opts("pos", "v"))).contains("mellow"));
		assertFalse(Arrays.asList(RiTa.rhymes("swag", opts("pos", "v"))).contains("grab"));
	}

	@Test
	public void callRhymesPosNid(){
		String[] rhymes = RiTa.rhymes("abated", opts("pos", "vbd", "limit", 1000));
		assertTrue(Arrays.asList(rhymes).contains("allocated"));
		assertTrue(Arrays.asList(rhymes).contains("annihilated"));
		assertTrue(Arrays.asList(rhymes).contains("condensed"));
	}

	@Test
	public void callRhymesNumSyllables() {
		assertTrue(Arrays.asList(RiTa.rhymes("cat", opts("numSyllables", 1))).contains("hat"));
		assertFalse(Arrays.asList(RiTa.rhymes("cat", opts("numSyllables", 2))).contains("hat"));
		assertFalse(Arrays.asList(RiTa.rhymes("cat", opts("numSyllables", 3))).contains("hat"));

		assertTrue(Arrays.asList(RiTa.rhymes("yellow", opts("numSyllables", 2))).contains("mellow"));
		assertFalse(Arrays.asList(RiTa.rhymes("yellow", opts("numSyllables", 3))).contains("mellow"));

		// special case, where word is not in dictionary
		String[] rhymes = RiTa.rhymes("abated", opts("numSyllables", 3));
		assertTrue(Arrays.asList(rhymes).contains("elated"));
		assertFalse(Arrays.asList(rhymes).contains("abated"));
		assertFalse(Arrays.asList(rhymes).contains("allocated"));
		assertFalse(Arrays.asList(rhymes).contains("condensed"));
	}

	@Test
	public void callRhymesWordLength() {
		assertFalse(Arrays.asList(RiTa.rhymes("cat", opts("minLength", 4))).contains("hat"));
		assertFalse(Arrays.asList(RiTa.rhymes("cat", opts("maxLength", 2))).contains("hat"));

		String[] rhymes = RiTa.rhymes("abated", opts("pos", "vbd", "maxLength", 9));
		assertTrue(Arrays.asList(rhymes).contains("allocated"));
		assertFalse(Arrays.asList(rhymes).contains("annihilated"));
		assertFalse(Arrays.asList(rhymes).contains("condensed"));
	}

	@Test
	public void callSpellsLike() {
		String[] result;

		result = RiTa.spellsLike("");
		assertArrayEquals(result, new String[] { });

		result = RiTa.spellsLike("banana");
		assertArrayEquals(result, new String[] { "banal", "bonanza", "cabana", "manna" });

		result = RiTa.spellsLike("tornado");
		assertArrayEquals(new String[]{"torpedo"}, result);

		result = RiTa.spellsLike("ice");
		assertArrayEquals(new String[]{
			"ace", "dice",
      		"iced", "icy",
      		"ire", "lice",
      		"nice", "rice",
      		"vice"
		}, result);
	}

	@Test
	public void callSpellsLikeOptions(){
		String[] result;

		result = RiTa.spellsLike("banana", opts("minLength", 6, "maxLength", 6 ));
    	assertArrayEquals(result, new String[]{"cabana"});

    	result = RiTa.spellsLike("banana", opts("minLength", 6, "maxLength", 6 ));
    	assertArrayEquals(result, new String[]{"cabana"});

    	result = RiTa.spellsLike("banana", opts( "minDistance", 1 ));
    	assertArrayEquals(result, new String[]{"banal", "bonanza", "cabana", "manna"});

    	result = RiTa.spellsLike("ice", opts( "maxLength", 3 ));
   	 	assertArrayEquals(result, new String[]{"ace", "icy", "ire"});

    	result = RiTa.spellsLike("ice", opts( "minDistance", 2, "minLength", 3, "maxLength", 3, "limit", 1000));
		for (String string : result) {
			assertTrue(string.length() == 3);
		}
    	assertTrue(result.length > 10);

    	result = RiTa.spellsLike("ice", opts( "minDistance", 0, "minLength", 3, "maxLength", 3 ));
    	assertArrayEquals(result, new String[]{"ace", "icy", "ire"});

    	result = RiTa.spellsLike("ice", opts( "minLength", 3, "maxLength", 3 ));
    	for (String string : result) {
			assertTrue(string.length() == 3);
		}
    	assertArrayEquals(result, new String[]{"ace", "icy", "ire"});

    	result = RiTa.spellsLike("ice", opts( "minLength", 3, "maxLength", 3, "pos", "n" ));
		for (String string : result) {
			assertTrue(string.length() == 3);
		}
    	assertArrayEquals(result, new String[]{"ace", "ire"});


    	result = RiTa.spellsLike("ice", opts( "minLength", 4, "maxLength", 4, "pos", "v", "limit", 5 ));
    	for (String string : result) {
			assertTrue(string.length() == 4);
		}
    	assertArrayEquals(result, new String[]{"ache", "bide", "bite", "cite", "dine"});

    	result = RiTa.spellsLike("ice", opts( "minLength", 4, "maxLength", 4, "pos", "nns", "limit", 5));
		for (String string : result) {
			assertTrue(string.length() == 4);
		}
    	assertArrayEquals(result, new String[]{"dice", "rice"});

    	result = RiTa.spellsLike("ice", opts("minLength", 4, "maxLength", 4, "pos", "nns", "minDistance", 3, "limit", 5));
		for (String string : result) {
			assertTrue(string.length() == 4);
		}
    	assertArrayEquals(result, new String[] {"axes", "beef", "deer", "dibs", "fame"});

    	// special case, where word is not in dictionary
   	 	result = RiTa.spellsLike("abated", opts( "pos", "vbd" ));
		assertTrue(Arrays.asList(result).contains("abetted"));
		assertTrue(Arrays.asList(result).contains("aborted"));
		assertFalse(Arrays.asList(result).contains("condensed"));
	}

	@Test
	public void callSoundsLike() {
		String[] result, answer;

		result = RiTa.soundsLike("tornado", opts("type", "sound"));
		assertArrayEquals(result, new String[] { "torpedo" });

		result = RiTa.soundsLike("try", opts("limit", 20));  // why?
		answer = new String[] { "cry", "dry", "fry", "pry", "rye", "tie", "tray", "tree", "tribe", "tried", "tripe", "trite", "true", "wry"};
		eql(result, answer);

		result = RiTa.soundsLike("try", opts("minDistance", 2, "limit", 20));
		//console.log(result);
		assertTrue(result.length > answer.length); // more

		result = RiTa.soundsLike("happy");
		answer = new String[] { "happier", "hippie" };
		assertArrayEquals(result, answer);

		result = RiTa.soundsLike("happy", opts("minDistance", 2));
		assertTrue(result.length > answer.length); // more

		result = RiTa.soundsLike("cat", opts("type", "sound"));
		assertArrayEquals(new String[]{
			"bat", "cab",
			"cache", "calf",
			"calve", "can",
			"can't", "cap",
			"cash", "cast"
		}, result);

		result = RiTa.soundsLike("cat", opts("limit", 5));
		answer = new String[] { "bat", "cab", "cache", "calf", "calve" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts("minLength", 2, "maxLength", 4, "limit", 1000, "type", "sound"));
		answer = new String[] { "at", "bat", "cab", "calf", "can", "cap", "cash", "cast", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit", "kite", "mat", "matt", "pat", "rat", "sat", "that", "vat" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts(
				"minLength", 4,
				"maxLength", 5,
				"pos", "jj"));
		answer = new String[] { "catty", "curt" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts("minDistance", 2));
		assertTrue(result.length > answer.length);

		result = RiTa.soundsLike("abated", opts("pos", "vbd"));
		assertTrue(Arrays.asList(result).contains("abetted"));
		assertTrue(Arrays.asList(result).contains("debated"));
		assertFalse(Arrays.asList(result).contains("condensed"));
	}

	@Test
	public void callSoundsLikeMatchSpelling() { 
		String[] result, answer;

		result = RiTa.soundsLike("try", opts("matchSpelling", true));
		answer = new String[] { "cry", "dry", "fry", "pry", "tray" };
		eql(result, answer);

		result = RiTa.soundsLike("try", opts("matchSpelling", true, "maxLength", 3));
		answer = new String[] { "cry", "dry", "fry", "pry", "wry" };
		eql(result, answer);

		result = RiTa.soundsLike("try", opts("matchSpelling", true, "minLength", 4));
		answer = new String[] { "tray" };
		eql(result, answer);

		result = RiTa.soundsLike("try", opts("matchSpelling", true, "limit", 3));
		answer = new String[] { "cry", "dry", "fry" };
		eql(result, answer);

		result = RiTa.soundsLike("daddy", opts("matchSpelling", true));
		answer = new String[] { "dandy", "paddy" };
		eql(result, answer);

		result = RiTa.soundsLike("banana", opts("matchSpelling", true));
		answer = new String[] { "bonanza" };
		eql(result, answer);

		result = RiTa.soundsLike("abated", opts("pos", "vbd", "matchSpelling", true));
		assertEquals(2, result.length);
		assertTrue(Arrays.asList(result).contains("abetted"));
		assertTrue(Arrays.asList(result).contains("awaited"));
	}

	@Test
	public void callIsRhyme() {

		assertFalse(RiTa.isRhyme("", ""));
		assertFalse(RiTa.isRhyme("apple", "polo"));
		assertFalse(RiTa.isRhyme("this", "these"));

		assertTrue(RiTa.isRhyme("cat", "hat"));
		assertTrue(RiTa.isRhyme("yellow", "mellow"));
		assertTrue(RiTa.isRhyme("toy", "boy"));

		assertTrue(RiTa.isRhyme("solo", "tomorrow"));
		assertTrue(RiTa.isRhyme("tense", "sense"));
		assertTrue(RiTa.isRhyme("crab", "drab"));
		assertTrue(RiTa.isRhyme("shore", "more"));
		assertFalse(RiTa.isRhyme("hose", "house"));
		assertFalse(RiTa.isRhyme("sieve", "mellow"));

		assertTrue(RiTa.isRhyme("mouse", "house"));

		assertTrue(RiTa.isRhyme("yo", "bro"));
		assertFalse(RiTa.isRhyme("swag", "grab"));

		assertTrue(RiTa.isRhyme("weight", "eight"));
		assertTrue(RiTa.isRhyme("eight", "weight"));
		assertTrue(RiTa.isRhyme("abated", "debated"));

		assertTrue(RiTa.isRhyme("sieve", "give"));
	}

	@Test
	public void callIsAlliteration() {

		assertTrue(RiTa.isAlliteration("knife", "gnat")); // gnat=lts
		assertTrue(RiTa.isAlliteration("knife", "naughty"));

		assertTrue(RiTa.isAlliteration("sally", "silly"));
		assertTrue(RiTa.isAlliteration("sea", "seven"));
		assertTrue(RiTa.isAlliteration("silly", "seven"));
		assertTrue(RiTa.isAlliteration("sea", "sally"));

		assertTrue(RiTa.isAlliteration("big", "bad"));
		assertTrue(RiTa.isAlliteration("bad", "big")); // swap position

		assertTrue(RiTa.isAlliteration("BIG", "bad")); // CAPITAL LETTERS
		assertTrue(RiTa.isAlliteration("big", "BAD")); // CAPITAL LETTERS
		assertTrue(RiTa.isAlliteration("BIG", "BAD")); // CAPITAL LETTERS

		// False
		assertFalse(RiTa.isAlliteration("", ""));
		assertFalse(RiTa.isAlliteration("wind", "withdraw"));
		assertFalse(RiTa.isAlliteration("solo", "tomorrow"));
		assertFalse(RiTa.isAlliteration("solo", "yoyo"));
		assertFalse(RiTa.isAlliteration("yoyo", "jojo"));
		assertFalse(RiTa.isAlliteration("cat", "access"));

		assertTrue(RiTa.isAlliteration("unsung", "sine"));
		assertTrue(RiTa.isAlliteration("job", "gene"));
		assertTrue(RiTa.isAlliteration("jeans", "gentle"));

		assertTrue(RiTa.isAlliteration("abet", "better"));
		assertTrue(RiTa.isAlliteration("never", "knight"));
		assertTrue(RiTa.isAlliteration("knight", "navel"));
		assertTrue(RiTa.isAlliteration("cat", "kitchen"));

		// not counting assonance
		assertFalse(RiTa.isAlliteration("octopus", "oblong"));
		assertFalse(RiTa.isAlliteration("omen", "open"));
		assertFalse(RiTa.isAlliteration("amicable", "atmosphere"));

		assertTrue(RiTa.isAlliteration("abated", "abetted"));

		assertTrue(RiTa.isAlliteration("this", "these"));
		assertTrue(RiTa.isAlliteration("psychology", "cholera"));
		assertTrue(RiTa.isAlliteration("consult", "sultan"));
		assertTrue(RiTa.isAlliteration("monsoon", "super"));
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}

	static void eql(String[] a, String[] b) {
		eql(a, b, "");
	}

	static void eql(String[] a, String[] b, String msg) {
		Arrays.sort(a);
		Arrays.sort(b);// hack
		String s = "";
		boolean ok = a.length == b.length;
		int len = Math.max(a.length, b.length);
		for (int i = 0; i < len; i++) {
			s += i + ") " + (i < a.length ? a[i] : "NA") + " " + (i < b.length ? b[i] : "NA") + "\n";
			if (ok && !a[i].equals(b[i])) ok = false;
		}
		if (!ok) System.err.println(s);
		assertEquals(Arrays.asList(b), Arrays.asList(a), msg);
	}

	public static boolean contains(String[] arr, String item) {
		for (String n : arr) {
			if (item == n) {
				return true;
			}
		}
		return false;
	}

}
