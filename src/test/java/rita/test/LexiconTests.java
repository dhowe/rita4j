package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

import rita.*;
import static rita.Util.*;

public class LexiconTests {

	// TODO: use opts() instead of creating Maps
	LexiconTests() {
		RiTa.SILENCE_LTS = true;
	}

	@Test
	public void callHasWord() {
		assertTrue(RiTa.hasWord("random"));
	}

	@Test
	public void callRandomWord() {
		String result;
		Map<String, Object> hm = opts("pos", "xxx");
		assertThrows(RiTaException.class, () -> RiTa.randomWord(hm));

		hm.clear();
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.put("pos", "nn");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.clear();
		hm.put("pos", "nns");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.clear();
		hm.put("pos", "n");
		result = RiTa.randomWord(hm);
		assertTrue(result.length() > 0);

		hm.clear();
		hm.put("pos", "v");
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
	public void callRandomWordNNS() {
		Map<String, Object> hm = opts("pos", "nns");
		//Map<String, Object> bad = new HashMap<String, Object>();

		for (int i = 0; i < 1000; i++) {
			String result = RiTa.randomWord(hm);
			if (!Inflector.isPlural(result)) {
				// For now, just warn here as there are too many edge cases (see #521)
				System.err.println("Pluralize/Singularize problem: randomWord(nns) was "
						+ result + " (" + "isPlural=" + Inflector.isPlural(result) +
						"), singularized is " + RiTa.singularize(result) + ")");
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
	}

	@Test
	public void callRandomWordPos() {
		String[] pos = { "nn", "jj", "jjr", "wp" };
		Map<String, Object> hm = new HashMap<String, Object>();
		for (int j = 0; j < pos.length; j++) {
			for (int i = 0; i < 5; i++) {
				hm.clear();
				hm.put("pos", pos[j]);
				String result = RiTa.randomWord(hm);
				String best = Tagger.allTags(result)[0];
				if (!best.equals(pos[j])) {
					System.out.println(result + ": " + pos[j] + " ?= "
							+ best + "/" + Tagger.allTags(result)[0]);
				}
				assertEquals(pos[j], best);
			}
		}

	}

	@Test
	public void callRandomWordSyls() {
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
		assertArrayEquals(results, RiTa.search("/phant/"));
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

		String[] res2 = RiTa.search("/f-a[eh]-n-t/", opts("type", "phones", "limit", 10));
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
	}

	@Test
	public void callSearchWithPos() {
		String[] res;
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("type", "stresses");
		hm.put("limit", 5);
		hm.put("pos", "n");
		assertArrayEquals(RiTa.search("010", hm),
				new String[] { "abalone", "abandonment", "abatement", "abbreviation", "abdomen" });

		hm.put("numSyllables", 3);
		assertArrayEquals(RiTa.search("010", hm),
				new String[] { "abatement", "abdomen", "abduction", "abeyance", "abortion" });

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
		assertArrayEquals(RiTa.search("/f-a[eh]-n-t/", hm), new String[] { "fantasize" });

		hm.clear();
		hm.put("type", "stresses");
		hm.put("limit", 5);
		hm.put("pos", "nns");
		res = RiTa.search("010", hm);
		assertArrayEquals(res,
				new String[] { "abalone", "abandonments", "abatements", "abbreviations", "abdomens" });

		hm.put("numSyllables", 3);
		assertArrayEquals(RiTa.search("010", hm),
				new String[] { "abatements", "abdomens", "abductions", "abeyances", "abortions" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 3);
		hm.put("pos", "nns");
		assertArrayEquals(RiTa.search("f-ah-n-t", hm), new String[] { "elephants", "infants", "infantries" });

		hm.clear();
		hm.put("type", "phones");
		hm.put("limit", 5);
		hm.put("pos", "vb");
		assertArrayEquals(RiTa.search("/f-a[eh]-n-t/", hm), new String[] { "fantasize" });
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
				"malfunctionings",
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
		assertTrue(Arrays.asList(RiTa.rhymes("shore",opts("limit", 100))).contains("more"));
		assertTrue(Arrays.asList(RiTa.rhymes("weight",opts("limit", 100))).contains("eight"));
		assertTrue(Arrays.asList(RiTa.rhymes("eight",opts("limit", 100))).contains("weight"));
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
		assertArrayEquals(result, new String[] { "dice", "iced",  "lice", "nice", "rice", "vice" });

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
		assertArrayEquals(result, new String[] { "dice", "rice"  });
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

		/*
		result = RiTa.soundsLike("cat");
		answer = new String[] { "bat", "cab", "cache", "calf", "calve", "can",
				"can't", "cap", "capped", "cash", "cashed", "cast", "caste", "catch",
				"catty", "caught", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit",
				"kite", "mat", "matt", "matte", "pat", "rat", "sat", "tat", "that", "vat" };
		eql(result, answer);*/

		result = RiTa.soundsLike("cat", opts("limit", 5));
		answer = new String[] { "bat", "cab", "cache", "calf", "calve" };
		eql(result, answer);

		result = RiTa.soundsLike("cat", opts("minLength", 2, "maxLength", 4, "limit", 50));
		answer = new String[] { "bat", "cab", "calf", "can", "cant", "cap", "cash", "cast", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit",
				"kite", "mat", "matt", "pat", "rat", "sat", "tat", "that", "vat" };
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
