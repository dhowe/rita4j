package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.regex.Pattern;

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
		assertTrue(!RiTa.hasWord("knews"));  // SYNC:
	
		assertTrue(!RiTa.hasWord("barkness")); 
		assertTrue(!RiTa.hasWord("horne")); 
	}

	@Test
	public void callRandomWord() {
		String result;
		Map<String, Object> hm = opts("pos", "xxx");
		assertThrows(RiTaException.class, () -> RiTa.randomWord(hm));

		hm.clear();
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.clear();
		hm.put("numSyllables", 3);
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.clear();
		hm.put("numSyllables", 5);
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		//no opts
		result = RiTa.randomWord();
		assertTrue(result != null);
		assertTrue(result.length() >= 4);

		//string opts
		result = RiTa.randomWord(opts("pos", "v"));
		assertTrue(result.length() > 0);
		assertTrue(RiTa.isVerb(result));

		result = RiTa.randomWord(opts("numSyllables", 5));
		assertTrue(result.length() > 0);

		result = RiTa.randomWord(opts("pos", "v", "numSyllables", 1));
		assertTrue(result.length() > 0);
		assertTrue(RiTa.isVerb(result));

		//randomWord should be random
		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			String w = RiTa.randomWord(opts("pos", "nns"));
			results.add(w);
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

		Map<String, Object> hm = opts("type", "stresses");
		result = RiTa.randomWord("0/1/0", hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord("^0/1/0$", hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		result = RiTa.randomWord("010", hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord("^010$", hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		result = RiTa.randomWord(Pattern.compile("0/1/0"), hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		result = RiTa.randomWord(Pattern.compile("^0/1/0$"), hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		hm = opts("type", "phones");
		result = RiTa.randomWord("^th", hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord("v$", hm);
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord("^b-ih-l-iy-v$", hm);
		assertEquals("believe", result);

		result = RiTa.randomWord("ae", hm);
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("^th"), hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("v$"), hm);
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		result = RiTa.randomWord(Pattern.compile("^b-ih-l-iy-v$"), hm);
		assertEquals("believe", result);

		result = RiTa.randomWord(Pattern.compile("ae"), hm);
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));

		//regex in options
		hm = opts("regex", "^a");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^a[a-zA-Z]+", result));

		hm = opts("regex", "^apple$");
		result = RiTa.randomWord(hm);
		assertEquals("apple", result);

		hm = opts("regex", "le");
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-zA-Z]*le[a-zA-Z]*", result));

		hm = opts("regex", Pattern.compile("^a"));
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("^a[a-zA-Z]*", result));

		hm.put("regex", Pattern.compile("^apple$"));
		result = RiTa.randomWord(hm);
		assertEquals("apple", result);

		hm.put("regex", Pattern.compile("le"));
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-zA-Z]*le[a-zA-Z]*", result));

		hm = opts("type", "stresses");
		hm.put("regex", "0/1/0");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		hm.put("regex", "^0/1/0$");
		result = RiTa.randomWord(hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		hm.put("regex", "010");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		hm.put("regex", "^010$");
		result = RiTa.randomWord(hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		hm.put("regex", Pattern.compile("0/1/0"));
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("[01/]*0/1/0[01/]*", RiTa.analyze(result).get("stresses")));

		hm.put("regex", Pattern.compile("^0/1/0$"));
		result = RiTa.randomWord(hm);
		assertEquals("0/1/0", RiTa.analyze(result).get("stresses"));

		hm = opts("type", "phones");
		hm.put("regex", "^th");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		hm.put("regex", "v$");
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		hm.put("regex", "^b-ih-l-iy-v$");
		result = RiTa.randomWord(hm);
		assertEquals("believe", result);

		hm.put("regex", "ae");
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));

		hm.put("regex", Pattern.compile("^th"));
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 3);
		assertTrue(Pattern.matches("^th[a-z\\-]*", RiTa.analyze(result).get("phones")));

		hm.put("regex", Pattern.compile("v$"));
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-z\\-]*v$", RiTa.analyze(result).get("phones")));

		hm.put("regex", Pattern.compile("^b-ih-l-iy-v$"));
		result = RiTa.randomWord(hm);
		assertEquals("believe", result);

		hm.put("regex", Pattern.compile("ae"));
		result = RiTa.randomWord(hm);
		assertTrue(Pattern.matches("[a-z\\-]*ae[a-z\\-]*", RiTa.analyze(result).get("phones")));
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

		List<String> knownBad = Arrays.asList(new String[] { // TODO:
				"fracases", "magpies", "arthritis", "bronchitis", "hepatitis", "encephalitis" 
		});
		
		// TODO: Why is "strives" returned as a plural?   

		for (int i = 0; i < 100; i++) {
			String result = RiTa.randomWord(hm);
			if (!knownBad.contains(result) && !Inflector.isPlural(result)) {
				//if (!Inflector.isPlural(result)) {

				// For now, just warn here as there are too many edge cases (see #521)
				System.err.println("Pluralize/singularize problem: randomWord(nns) was " + result + " (" + "isPlural="
						+ Inflector.isPlural(result) + "), singularized is " + RiTa.singularize(result) + ")");
			}

			assertFalse(result.endsWith("ness"));
			assertFalse(result.endsWith("isms"));
			// TODO: occasional problem here, examples: beaux

			// No vbg, No -ness, -ism
			String pos = RiTa.lexicon().posData(result);
			//if (pos == null) System.out.println("FAIL:" + plural + "/" + sing + ": " + pos);
			//if (pos == null) bad.put(plural, sing);
			assertTrue(pos == null || pos.indexOf("vbg") < 0, "fail at " + result);
		}
		//		for (Iterator<Entry<String,Object>> it = bad.entrySet().iterator(); it.hasNext();) {
		//			Entry<String, Object> e = it.next();
		//			System.out.println("\""+e.getKey()+"\", \""+e.getValue()+"\",");
		//		}

		String[] pos = { "nn", "jj", "jjr", "wp" };
		String result = "";
		hm = new HashMap<String, Object>();
		for (int j = 0; j < pos.length; j++) {
			for (int i = 0; i < 5; i++) {
				hm.clear();
				hm.put("pos", pos[j]);
				result = RiTa.randomWord(hm);
				String best = RiTa.tagger.allTags(result)[0];
				if (!best.equals(pos[j])) {
					System.out.println(result + ": " + pos[j] + " ?= " + best + "/" + RiTa.tagger.allTags(result)[0]);
				}
				assertEquals(pos[j], best);
			}
		}

		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			results.add(RiTa.randomWord(opts("pos", "nns")));
		}
		assertTrue(results.size() == 10);

		int i = 0;
		while (i < results.size() - 1) {
			if (results.get(i).equals(results.get(i + 1))) {
				results.remove(i);
			}
			else {
				i++;
			}
		}
		assertTrue(results.size() > 1);

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

		result = RiTa.randomWord(opts("pos", "v"));
		assertTrue(result.length() > 0, "randomWord v=" + result);

	}

	@Test
	public void callRandomWordWithSyls() {
		int i = 0;
		String result = "";
		String syllables = "";
		int num = 0;
		Map<String, Object> hm = new HashMap<String, Object>();

		hm.put("numSyllables", 3);
		for (i = 0; i < 10; i++) {
			result = RiTa.randomWord(hm);
			syllables = RiTa.syllables(result);
			num = syllables.split(RiTa.SYLLABLE_BOUNDARY).length;
			assertTrue(result.length() > 0);
			assertTrue(num == 3); // "3 syllables: "
		}

		hm.clear();
		hm.put("numSyllables", 5);
		for (i = 0; i < 10; i++) {
			result = RiTa.randomWord(hm);
			syllables = RiTa.syllables(result);
			num = syllables.split(RiTa.SYLLABLE_BOUNDARY).length;
			assertTrue(result.length() > 0);
			assertTrue(num == 5); // "5 syllables: "
		}

	}

	@Test
	public void callSearchWithoutOpts() {
		assertTrue(RiTa.search().length > 20000);
	}

	@Test
	public void callSearchWithPrecompliedRegex() {
		Pattern regex = Pattern.compile("^a");
		String[] result = RiTa.search(regex);
		for (int i = 0; i < Math.min(100, result.length); i++) {
			assertTrue(result[i].charAt(0) == 'a', " " + result[i]);
		}
	}

	@Test
	public void callSearchWithoutRegex() {
		//assertEquals(10, RiTa.search().length); -> fail, move to knownIssue
		//assertEquals(11, RiTa.search(opts("limit", 11)).length); -> to knownIssue
		assertEquals(11, RiTa.search("", opts("limit", 11)).length);
		String[] expected = new String[] {
				"abalone", "abandonment",
				"abbey", "abbot",
				"abbreviation", "abdomen",
				"abduction", "aberration",
				"ability", "abnormality"
		};
		//assertArrayEquals(expected, RiTa.search(opts("pos", "n")));
		assertArrayEquals(expected, RiTa.search("", opts("pos", "n", "limit", 10)));

		expected = new String[] {
				"abashed", "abate",
				"abbey", "abbot",
				"abet", "abhor",
				"abide", "abject",
				"ablaze", "able"
		};

		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 2)));
		assertArrayEquals(expected, RiTa.search("", opts("numSyllables", 2, "limit", 10)));

		expected = new String[] {
				"abbey", "abbot",
				"abode", "abscess",
				"absence", "abstract",
				"abuse", "abyss",
				"accent", "access"
		};
		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 2, "pos", "n")));
		assertArrayEquals(expected, RiTa.search("", opts("numSyllables", 2, "pos", "n", "limit", 10)));

		expected = new String[] {
				"ace", "ache",
				"act", "age",
				"aid", "aide",
				"aim", "air",
				"aisle", "ale"
		};
		//assertArrayEquals(expected, RiTa.search(opts("numSyllables", 1, "pos", "n")));
		assertArrayEquals(expected, RiTa.search("", opts("numSyllables", 1, "pos", "n", "limit", 10)));
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
		assertArrayEquals(results, RiTa.search("phant"));
		//regex in options
		Map<String, Object> hm = opts("regex", "phant");
		assertArrayEquals(results, RiTa.search(hm));
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

		//regex in options
		Map<String, Object> hm = opts("type", "phones", "limit", 5);
		hm.put("regex", "f-ah-n-t");
		res1 = RiTa.search(hm);
		assertArrayEquals(res1, new String[] {
				"elephant",
				"infant",
				"infantile",
				"infantry",
				"oftentimes"
		});

		hm = opts("type", "phones", "limit", 10);
		hm.put("regex", "f-a[eh]-n-t");
		res2 = RiTa.search(hm);
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
	public void callSearchWithPos() { // TODO: use opts()

		String[] res;
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("type", "stresses");
		hm.put("limit", 5);
		hm.put("pos", "n");
		assertArrayEquals(RiTa.search("010", hm), new String[] { "abalone", "abandonment", "abbreviation", "abdomen", "abduction" });

		hm.put("numSyllables", 3);
		assertArrayEquals(RiTa.search("010", hm), new String[] { "abdomen", "abduction", "abortion", "abruptness", "absorber" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 3);
		hm.put("pos", "n");
		assertArrayEquals(RiTa.search("f-ah-n-t", hm), new String[] { "elephant", "infant", "infantry" });

		hm.put("numSyllables", 2);
		assertArrayEquals(RiTa.search("f-ah-n-t", hm), new String[] { "infant" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 5);
		hm.put("pos", "v");
		assertArrayEquals(RiTa.search("f-a[eh]-n-t", hm), new String[] { "fantasize" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 5);
		hm.put("pos", "vb");
		assertArrayEquals(RiTa.search("f-a[eh]-n-t", hm), new String[] { "fantasize" });

		hm.clear();
		hm.put("type", "stresses");
		hm.put("limit", 5);
		hm.put("pos", "nns");
		res = RiTa.search("010", hm);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(res,
				new String[] { "abalone", "abandonments", "abbreviations", "abductions", "abilities" });

		hm.put("numSyllables", 3);
		assertArrayEquals(RiTa.search("010", hm),
				new String[] { "abductions", "abortions", "absorbers", "abstentions", "abstractions" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 3);
		hm.put("pos", "nns");
		assertArrayEquals(RiTa.search("f-ah-n-t", hm), new String[] { "elephants", "infants", "infantries" });
	}

	@Test
	public void callSearchWithStress() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("type", "stresses");
		hm.put("limit", 5);

		assertArrayEquals(RiTa.search("0/1/0/0/0/0", hm), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });

		assertArrayEquals(RiTa.search("010000", hm), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });

		hm.put("regex", "0/1/0/0/0/0");
		assertArrayEquals(RiTa.search(hm), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });

		hm.put("regex", "010000");
		assertArrayEquals(RiTa.search(hm), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"colonialism" });

		hm.put("maxLength", 11);

		assertArrayEquals(RiTa.search("010000", hm), new String[] {
				"colonialism",
				"imperialism",
				"materialism" });

		hm.clear();
		hm.put("type", "stresses");
		hm.put("limit", 5);
		hm.put("minLength", 12);

		assertArrayEquals(RiTa.search("010000", hm), new String[] {
				"accountability",
				"anticipatory",
				"appreciatively",
				"authoritarianism",
				"conciliatory" });

	}

	@Test
	public void callRandomWordPosSyls() {
		// function fail(result, epos) {
		// let test = result.endsWith('es') ? result.substring(-2) : result;
		// let ent = RiTa.lexicon()[test];
		// return ('(' + epos + ') Fail: ' + result + ': expected ' + epos + ', got ' +
		// (ent ? ent[1] : 'null'));
		// }

		Map<String, Object> hm = new HashMap<String, Object>();
		String result, syllables;
		RiTa.SILENCE_LTS = true;

		for (int j = 0; j < 1; j++) {

			for (int i = 0; i < 5; i++) {
				hm.put("pos", "vbz");
				hm.put("numSyllables", 3);
				result = RiTa.randomWord(hm);
				assertTrue(result.length() > 0);
				syllables = RiTa.syllables(result);
				assertEquals(3, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
				assertTrue(RiTa.isVerb(result));

				hm.clear();
				hm.put("pos", "n");
				hm.put("numSyllables", 1);
				result = RiTa.randomWord(hm);
				assertTrue(result.length() > 0);
				syllables = RiTa.syllables(result);
				assertEquals(1, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
				assertTrue(RiTa.isNoun(result));

				hm.clear();
				hm.put("pos", "nns");
				hm.put("numSyllables", 1);
				result = RiTa.randomWord(hm);
				assertTrue(result.length() > 0);
				syllables = RiTa.syllables(result);
				assertEquals(1, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
				assertTrue(RiTa.isNoun(result));

				hm.clear();
				hm.put("pos", "nns");
				hm.put("numSyllables", 5);
				result = RiTa.randomWord(hm);
				assertTrue(result.length() > 0);
				syllables = RiTa.syllables(result);
				assertEquals(5, syllables.split(RiTa.SYLLABLE_BOUNDARY).length);
				assertTrue(RiTa.isNoun(result));

			}
		}

		RiTa.SILENCE_LTS = false;

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
		assertArrayEquals(expected, RiTa.search("0\\/1\\/0\\/0\\/0\\/0", opts("type", "stresses", "limit", 5)));
	}

	@Test
	public void callSearchWithPosFeatureLimit() {
		String[] expected = new String[] { "abalone", "abandonment", "abbreviation", "abdomen", "abduction" };
		assertArrayEquals(expected, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "n")));

		expected = new String[] { "abdomen", "abduction", "abortion", "abruptness", "absorber" };
		assertArrayEquals(expected,
				RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "n", "numSyllables", 3)));

		expected = new String[] { "elephant", "infant", "infantry" };
		assertArrayEquals(expected, RiTa.search("f-ah-n-t", opts("type", "phones", "limit", 3, "pos", "n")));

		expected = new String[] { "infant" };
		assertArrayEquals(expected,
				RiTa.search("f-ah-n-t", opts("type", "phones", "limit", 3, "pos", "n", "numSyllables", 2)));

		expected = new String[] { "fantasize" };
		assertArrayEquals(expected, RiTa.search("f-a[eh]-n-t", opts("type", "phones", "pos", "v", "limit", 5)));

		expected = new String[] { "fantasize" };
		assertArrayEquals(expected, RiTa.search("f-a[eh]-n-t", opts("type", "phones", "pos", "vb", "limit", 5)));

		expected = new String[] {
				"abalone",
				"abandonments",
				"abbreviations",
				"abductions",
				"abilities"
		};
		//assertArrayEquals(expected, RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "nns")));
		//assertArrayEquals(expected, RiTa.search("/0\\/1\\/0/", opts("type", "stresses", "limit", 5, "pos", "nns")));

		expected = new String[] {
				"abductions",
				"abortions",
				"absorbers",
				"abstentions",
				"abstractions"
		};
		//assertArrayEquals(expected,
		//RiTa.search("010", opts("type", "stresses", "limit", 5, "pos", "nns", "numSyllables", 3)));

		expected = new String[] { "elephants", "infants", "infantries" };
		assertArrayEquals(expected, RiTa.search("f-ah-n-t", opts("type", "phones", "pos", "nns", "limit", 3)));
	}

	@Test
	public void callToPhoneArray() {

		String[] result = RiTa.lexicon().toPhoneArray(RiTa.lexicon().rawPhones("tornado", false));
		String[] ans = { "t", "ao", "r", "n", "ey", "d", "ow" };
		assertArrayEquals(result, ans);
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
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("minLength", 1);
		hm.put("numSyllables", 7);
		hm.put("pos", "n");

		String[] result = RiTa.alliterations("cat", hm);

		assertArrayEquals(result, new String[] { "electrocardiogram", "telecommunications" });

		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

		hm.clear();
		hm.put("minLength", 14);
		hm.put("pos", "v");

		assertArrayEquals(RiTa.alliterations("dog", hm), new String[] { "disenfranchise" });

		hm.clear();
		hm.put("minLength", 13);
		hm.put("pos", "rb");
		hm.put("limit", 100);

		assertArrayEquals(RiTa.alliterations("dog", hm), new String[] {
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

		hm.clear();
		hm.put("minLength", 14);
		hm.put("pos", "nns");

		assertArrayEquals(RiTa.alliterations("freedom", hm), new String[] {
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
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

		result = RiTa.alliterations("dog", opts("limit", 100));
		assertTrue(result.length == 100);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog"));
		}

		Map<String, Object> hm = new HashMap<String, Object>();

		hm.put("minLength", 15);
		result = RiTa.alliterations("dog", hm);
		assertTrue(result.length > 0 && result.length < 5, "got length=" + result.length);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog")); // , "FAIL1: " + result[i]
		}

		hm.clear();
		hm.put("minLength", 16);

		result = RiTa.alliterations("cat", hm);
		assertTrue(result.length > 0 && result.length < 15);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));// , "FAIL2: " + result[i]
		}

		result = RiTa.alliterations("khatt", hm);
		assertTrue(result.length > 0 && result.length < 15);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));// , "FAIL2: " + result[i]
		}

		//for single letter words return []
		assertArrayEquals(new String[] { }, RiTa.alliterations("a"));
		assertArrayEquals(new String[] { }, RiTa.alliterations("I"));
		assertArrayEquals(new String[] { }, RiTa.alliterations("K"));
	}

	public static boolean contains(String[] arr, String item) {
		for (String n : arr) {
			if (item == n) {
				return true;
			}
		}
		return false;
	}

	@Test
	public void callRhymes() {

		assertTrue(Arrays.asList(RiTa.rhymes("cat")).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("yellow")).contains("mellow"));
		assertTrue(Arrays.asList(RiTa.rhymes("toy")).contains("boy"));
		assertTrue(Arrays.asList(RiTa.rhymes("sieve")).contains("give"));

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

		//for single letter words return []
		assertArrayEquals(RiTa.rhymes("a"), new String[] { });
		assertArrayEquals(RiTa.rhymes("I"), new String[] { });
		assertArrayEquals(RiTa.rhymes("K"), new String[] { });
		assertArrayEquals(RiTa.rhymes("Z"), new String[] { });
	}

	@Test
	public void callRhymesPos() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("pos", "v");

		assertFalse(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("tense", hm)).contains("sense"));
		assertFalse(Arrays.asList(RiTa.rhymes("shore", hm)).contains("more"));
		assertFalse(Arrays.asList(RiTa.rhymes("apple")).contains("polo"));
		assertFalse(Arrays.asList(RiTa.rhymes("this")).contains("these"));
		assertFalse(Arrays.asList(RiTa.rhymes("hose")).contains("house"));
		assertFalse(Arrays.asList(RiTa.rhymes("sieve")).contains("mellow"));
		assertFalse(Arrays.asList(RiTa.rhymes("swag")).contains("grab"));

		hm.clear();
		hm.put("pos", "a");
		assertTrue(Arrays.asList(RiTa.rhymes("yellow", hm)).contains("mellow"));

		hm.clear();
		hm.put("pos", "n");
		assertTrue(Arrays.asList(RiTa.rhymes("toy", hm)).contains("boy"));
		assertFalse(Arrays.asList(RiTa.rhymes("sieve", hm)).contains("give"));
		assertFalse(Arrays.asList(RiTa.rhymes("crab", hm)).contains("drab"));

		hm.clear();
		hm.put("pos", "nn");
		hm.put("limit", 100);
		assertTrue(Arrays.asList(RiTa.rhymes("mouse", hm)).contains("house"));
		assertTrue(Arrays.asList(RiTa.rhymes("eight", hm)).contains("weight"));

		String[] rhymes = RiTa.rhymes("weight", opts("pos", "vb", "limit", 100));
		assertFalse(Arrays.asList(rhymes).contains("eight"));
		assertTrue(Arrays.asList(rhymes).contains("hate"));

		rhymes = RiTa.rhymes("abated", opts("pos", "vbd", "limit", 100));
		assertTrue(Arrays.asList(rhymes).contains("annihilated"));
		assertTrue(Arrays.asList(rhymes).contains("allocated"));
		assertFalse(Arrays.asList(rhymes).contains("condensed"));
	}

	@Test
	public void callRhymesNumSyllables() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("numSyllables", 1);
		assertTrue(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));

		hm.clear();
		hm.put("numSyllables", 2);
		assertFalse(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("yellow", hm)).contains("mellow"));

		hm.clear();
		hm.put("numSyllables", 3);
		assertFalse(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));
		assertFalse(Arrays.asList(RiTa.rhymes("yellow", hm)).contains("mellow"));

		// special case, where word is not in dictionary
		String[] rhymes = RiTa.rhymes("abated", opts("numSyllables", 3));
		assertTrue(Arrays.asList(rhymes).contains("elated"));
		assertFalse(Arrays.asList(rhymes).contains("abated"));
		assertFalse(Arrays.asList(rhymes).contains("allocated"));
		assertFalse(Arrays.asList(rhymes).contains("condensed"));
	}

	@Test
	public void callRhymesWordLength() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("minLength", 4);
		assertFalse(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));

		hm.clear();
		hm.put("maxLength", 2);
		assertFalse(Arrays.asList(RiTa.rhymes("cat", hm)).contains("hat"));
	}

	@Test
	public void callSpellsLike() {
		String[] result;

		// TODO: use opts()

		Map<String, Object> hm = new HashMap<String, Object>();

		result = RiTa.spellsLike("");
		assertArrayEquals(result, new String[] { });

		result = RiTa.spellsLike("banana", hm);
		assertArrayEquals(result, new String[] { "banal", "bonanza", "cabana", "manna" });

		hm.put("minLength", 6);
		hm.put("maxLength", 6);
		result = RiTa.spellsLike("banana", hm);
		Arrays.asList(result).forEach(r -> assertEquals(6, r.length()));

		assertArrayEquals(result, new String[] { "cabana" });

		hm.clear();
		hm.put("minDistance", 1);
		result = RiTa.spellsLike("banana", hm);
		assertArrayEquals(result, new String[] { "banal", "bonanza", "cabana", "manna" });

		result = RiTa.spellsLike("tornado");
		assertArrayEquals(result, new String[] { "torpedo" });

		result = RiTa.spellsLike("ice");
		assertArrayEquals(result, new String[] { "ace", "dice", "iced", "icy", "ire", "lice", "nice", "rice", "vice" });

		hm.clear();
		hm.put("minDistance", 1);
		hm.put("minLength", 4);
		result = RiTa.spellsLike("ice", hm);
		assertArrayEquals(result, new String[] { "dice", "iced", "lice", "nice", "rice", "vice" });

		hm.clear();
		hm.put("minDistance", 2);
		hm.put("minLength", 3);
		hm.put("maxLength", 3);
		hm.put("limit", 20);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(3, r.length()));

		assertTrue(result.length > 10);

		hm.clear();
		hm.put("minLength", 3);
		hm.put("maxLength", 3);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(3, r.length()));

		assertArrayEquals(result, new String[] { "ace", "icy", "ire" });

		hm.clear();
		hm.put("minLength", 3);
		hm.put("maxLength", 3);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(3, r.length()));

		assertArrayEquals(result, new String[] { "ace", "icy", "ire" });

		hm.clear();
		hm.put("pos", "n");
		hm.put("minLength", 3);
		hm.put("maxLength", 3);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(3, r.length()));

		assertArrayEquals(result, new String[] { "ace", "ire" });

		hm.clear();
		hm.put("minLength", 4);
		hm.put("maxLength", 4);
		hm.put("pos", "v");
		hm.put("limit", 5);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(4, r.length()));
		assertArrayEquals(result, new String[] { "ache", "bide", "bite", "cite", "dine" });

		hm.clear();
		hm.put("minLength", 4);
		hm.put("maxLength", 4);
		hm.put("pos", "nns");
		hm.put("limit", 5);
		result = RiTa.spellsLike("ice", hm);
		Arrays.asList(result).forEach(r -> assertEquals(4, r.length()));
		assertArrayEquals(result, new String[] { "dice", "rice" });
	}

	@Test
	public void callSpellsLikeOptions() {
		String[] result;
		String[] expected;

		expected = new String[] { "cabana" };
		result = RiTa.spellsLike("banana", opts("minLength", 6, "maxLength", 6));
		assertArrayEquals(expected, result);

		expected = new String[] { "banal", "bonanza", "cabana", "manna" };
		result = RiTa.spellsLike("banana", opts("minDistance", 1));
		assertArrayEquals(expected, result);

		expected = new String[] { "ace", "icy", "ire" };
		result = RiTa.spellsLike("ice", opts("maxLength", 3));
		assertArrayEquals(expected, result);

		result = RiTa.spellsLike("ice", opts("minDistance", 2, "minLength", 3, "maxLength", 3, "limit", 1000));
		assertTrue(result.length > 10);
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 3);
		}

		expected = new String[] { "ace", "icy", "ire" };
		result = RiTa.spellsLike("ice", opts("minDistance", 0, "minLength", 3, "maxLength", 3));
		assertArrayEquals(expected, result);

		result = RiTa.spellsLike("ice", opts("minLength", 3, "maxLength", 3));
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 3);
		}
		assertArrayEquals(expected, result);

		expected = new String[] { "ace", "ire" };
		result = RiTa.spellsLike("ice", opts("minLength", 3, "maxLength", 3, "pos", "n"));
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 3);
		}
		assertArrayEquals(expected, result);

		expected = new String[] { "ache", "bide", "bite", "cite", "dine" };
		result = RiTa.spellsLike("ice", opts(
				"minLength", 4, "maxLength", 4, "pos", "v", "limit", 5));
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 4);
		}
		assertArrayEquals(expected, result);

		expected = new String[] { "dice", "rice" };
		result = RiTa.spellsLike("ice", opts( // dice, rice ??
				"minLength", 4, "maxLength", 4, "pos", "nns", "limit", 5));
		for (int i = 0; i < result.length; i++) {
			assertTrue(result[i].length() == 4);
		}
		assertArrayEquals(expected, result);

		expected = new String[] { "axes", "beef", "deer", "dibs", "fish" };
		result = RiTa.spellsLike("ice", opts("minLength", 4, "maxLength", 4, "pos", "nns", "minDistance", 3, "limit", 5));
		for (int i = 0; i < result.length; i++) {
			//assertTrue(result[i].length() == 4);
		}
		//assertArrayEquals(expected, result); -> to knownIssue

		result = RiTa.spellsLike("abated", opts("pos", "vbd"));
		assertTrue(Arrays.asList(result).contains("abetted"));
		assertTrue(Arrays.asList(result).contains("aborted"));
		assertTrue(!Arrays.asList(result).contains("condensed"));
	}

	@Test
	public void callSoundsLike() {
		String[] result, answer;

		result = RiTa.soundsLike("tornado");
		assertArrayEquals(result, new String[] { "torpedo" });

		result = RiTa.soundsLike("try", opts("limit", 50));  // why?
		answer = new String[] { "cry", "dry", "fry", "pry", /*"rye",*/
				"tie", "tray", "tree", "tribe", "tried", "tripe", "trite", "true", "wry" };
		eql(result, answer);

		result = RiTa.soundsLike("try", opts("minDistance", 2, "limit", 50));
		//console.log(result);
		assertTrue(result.length > answer.length); // more

		result = RiTa.soundsLike("happy");
		answer = new String[] { "happier", "hippie" };
		assertArrayEquals(result, answer);

		result = RiTa.soundsLike("happy", opts("minDistance", 2));
		assertTrue(result.length > answer.length); // more

		/*  ????  SYNC
		result = RiTa.soundsLike("cat");
		answer = new String[] { "bat", "cab", "cache", "calf", "calve", "can",
				"can't", "cap", "capped", "cash", "cashed", "cast", "caste", "catch",
				"catty", "caught", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit",
				"kite", "mat", "matt", "matte", "pat", "rat", "sat", "tat", "that", "vat" };
		eql(result, answer);
		*/

		result = RiTa.soundsLike("cat", opts("limit", 5));
		answer = new String[] { "bat", "cab", "cache", "calf", "calve" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts("minLength", 2, "maxLength", 4, "limit", 50));
		answer = new String[] { "bat", "cab", "calf", "can", "cap", "cash", "cast", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit",
				"kite", "mat", "matt", "pat", "rat", "sat", "that", "vat" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts(
				"minLength", 4,
				"maxLength", 5,
				"pos", "jj",
				"limit", 8));
		answer = new String[] { "catty", "curt" };
		eql(result, answer);
	}

	@Test
	public void callSoundsLikeMatchSpelling() { // TODO: use opts()
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

		assertTrue(RiTa.isRhyme("sieve", "give"));

		assertTrue(RiTa.isRhyme("solo", "yoyo"));
		assertTrue(RiTa.isRhyme("yoyo", "jojo"));

		//noLTS
		assertTrue(!RiTa.isRhyme("solo", "jojo", true));
		assertTrue(!RiTa.isRhyme("jojo", "yoyo", true));
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

		assertTrue(RiTa.isAlliteration("this", "these"));
		assertTrue(RiTa.isAlliteration("psychology", "cholera"));
		assertTrue(RiTa.isAlliteration("consult", "sultan"));
		assertTrue(RiTa.isAlliteration("monsoon", "super"));

		//no lts
		assertTrue(!RiTa.isAlliteration("omen", "apple", true));
		assertTrue(!RiTa.isAlliteration("omen", "adobe", true));
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

}
