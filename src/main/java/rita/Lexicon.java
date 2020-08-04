package rita;

import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class Lexicon {
	
	private static String LEXICON_DELIM = ":", E = "";
	private static int MAP_SIZE = 30000;

	public Map<String, String[]> dict; // data

	public Lexicon(String filePath) {
		List<String> lines = loadJSON(filePath);
		if (lines == null || lines.size() < 2) {
			throw new RiTaException("Problem parsing lexicon files");
		}
		populateDict(lines);
	}

	private void populateDict(List<String> lines) {

		dict = new LinkedHashMap<String, String[]>(MAP_SIZE);

		for (int i = 1; i < lines.size() - 1; i++) // ignore JS prefix/suffix
		{
			String line = lines.get(i).replaceAll("['\\[\\]]", E);
			String[] parts = line.split(LEXICON_DELIM);
			if (parts == null || parts.length != 2) {
				throw new RiTaException("Illegal entry: " + line);
			}
			dict.put(parts[0].trim(), parts[1].split(","));
		}
	}

	public static List<String> loadJSON(String file) {

		if (file == null) {
			throw new RiTaException("No dictionary path specified!");
		}

		URL resource = RiTa.class.getResource(file);
		if (resource == null) {
			throw new RiTaException("Unable to load lexicon from: " + file);
		}

		try {
			return Files.readAllLines(Paths.get(resource.toURI()));
		} catch (Exception e) {
			throw new RiTaException("Unable to read lexicon from: " + file);
		}
	}

	public String[] alliterations(String word, int minWordLength) {

		if (word == null || word.length() == 0) return new String[0];

		word = word.contains(" ") ? word.substring(0, word.indexOf(" ")) : word;

		if (RiTa.VOWELS.contains(Character.toString(word.charAt(0)))) {
			return new String[0];
		}

		// int matchMinLength = minWordLength || 4;
		// boolean useLTS = opts && opts.useLTS || false;

		boolean useLTS = false; // TODO: default is true or false?

		ArrayList<String> results = new ArrayList<String>();
		String[] words = (String[]) dict.keySet().toArray(new String[dict.size()]);
		String fss = _firstStressedSyl(word, useLTS);
		String c1 = _firstPhone(fss);

		if (c1 == null || c1.length() == 0) return new String[0];

		for (int i = 0; i < words.length; i++) {
			if (words[i].length() < minWordLength) continue;
			String c2 = _firstPhone(_firstStressedSyl(words[i], useLTS));
			if (c1.equals(c2)) results.add(words[i]);
		}

		return Util.shuffle(results.toArray(new String[0])); // TODO
		// return null;
	}

	public boolean hasWord(String word) {
		if (word == null) {
			return false;
		}
		word = word.length() > 0 ? word.toLowerCase() : "";
		return Pluralizer.isPlural(word);
	}

	public boolean isAlliteration(String word1, String word2, boolean useLTS) {
		if (word1 == null || word2 == null || word1.length() == 0 || word2.length() == 0) {
			return false;
		}

		if (word1.indexOf(" ") > -1 || word2.indexOf(" ") > -1) {
			throw new IllegalArgumentException("isAlliteration expects single words only");
		}

		String c1 = _firstPhone(_firstStressedSyl(word1, useLTS));
		String c2 = _firstPhone(_firstStressedSyl(word2, useLTS));

		if (c1.length() > 0 && c2.length() > 0) {
			if (_isVowel(Character.toString(c1.charAt(0))) || _isVowel(Character.toString(c2.charAt(0)))) {
				return false;
			}
		}

		return c1.length() > 0 && c2.length() > 0 && c1 == c2;
	}

	public boolean isRhyme(String word1, String word2, boolean useLTS) {

		if (word1 == null || word2 == null || word1.toUpperCase() == word2.toUpperCase()) {
			return false;
		}

		String phones1 = _rawPhones(word1, useLTS);
		String phones2 = _rawPhones(word2, useLTS);

		if (phones2 == phones1) return false;

		String p1 = _lastStressedVowelPhonemeToEnd(word1, useLTS);
		String p2 = _lastStressedVowelPhonemeToEnd(word2, useLTS);

		return p1.length() > 0 && p2.length() > 0 && p1.equals(p2);
	}

	public String randomWord(String pos, int numSyllabes) // TODO:
	{
		throw new RuntimeException("Implement me");
	}

	public String[] search(String theWord) // TODO
	{
		return this.search(theWord, null);
	}

	public String[] search(String theWord, Map<String, Object> opts) {
		throw new RuntimeException("Implement me");
	}

	public String[] soundsLike(String word) {
		return this.soundsLike(word, null);
	}

	public String[] spellsLike(String word) {
		return this.spellsLike(word, null);
	}

	public String[] soundsLike(String word, Map<String, Object> opts) {
		throw new RuntimeException("Implement me");
	}

	public String[] spellsLike(String word, Map<String, Object> opts) {
		throw new RuntimeException("Implement me");
	}

	public String[] rhymes(String theWord) // TODO
	{
		return this.rhymes(theWord, null);
	}

	public String[] rhymes(String theWord, Map<String, Object> opts) // TODO
	{
		if (theWord == null || theWord.length() == 0) return new String[0];

		String word = theWord.toLowerCase();

		ArrayList<String> results = new ArrayList<String>();

		Set<String> wordSet = dict.keySet();

		String[] words = new String[wordSet.size()];
		wordSet.toArray(words);

		String p = _lastStressedPhoneToEnd(word);

		for (int i = 0; i < words.length; i++) {

			if (words[i] == word) continue;

			String w = dict.get(words[i])[0];
			w = w.replaceAll("'", "").replaceAll("\\[", "");

			if (w.endsWith(p)) results.add((words[i]));

			// if (dict[words[i]][0].endsWith(p))
		}

		String[] s = results.toArray(new String[0]);
		return s;
	}

	public String[] similarBy(String word, Map<String, Object> opts) // TODO
	{

		if (word == null || word.length() == 0) return new String[0];

		if (opts == null) return new String[0];

		if (opts.get("type") == null || opts.get("type") == "") {
			opts.put("type", "letter");
		}

		return (opts.get("type") == "soundAndLetter") ? similarBySoundAndLetter(word, opts) : similarByType(word, opts);
	}

	public String[] similarBySoundAndLetter(String word, Map<String, Object> opts) {

		opts.put("type", "letter");
		// opts.get("type") = "letter";
		String[] simLetter = similarByType(word, opts);
		if (simLetter.length < 1) return new String[0];

		opts.put("type", "sound");
		String[] simSound = similarByType(word, opts);
		if (simSound.length < 1) return new String[0];

		return _intersect(simSound, simLetter);
	}

	// TODO check result against js (compareA, compare B)
	public String[] similarByType(String word, Map<String, Object> opts) {
		int minLen = 2;
		int preserveLength = 0;
		int minAllowedDist = 1;

		if (!opts.isEmpty()) {
			minLen = (int) ((opts.containsValue("minimumWordLen")) ? opts.get("minimumWordLen") : 2);
			preserveLength = (int) ((opts.containsValue("preserveLength")) ? opts.get("preserveLength") : 0);
			minAllowedDist = (int) ((opts.containsValue("minAllowedDistance")) ? opts.get("minAllowedDistance") : 1);
		}

		ArrayList<String> result = new ArrayList<String>();
		int minVal = Integer.MAX_VALUE;
		String input = word.toLowerCase();

		ArrayList<String> words = new ArrayList<String>(dict.keySet());
		ArrayList<String> variations = new ArrayList<String>();
		variations.add(input);
		variations.add(input + "s");
		variations.add(input + "es");

		boolean useLTS = false; // TODO _rawPhones second param has to be removed?
		String[] compareA = (opts.get("type") == "sound" ? toPhoneArray(_rawPhones(input, useLTS))
				: new String[] { input });

		for (int i = 0; i < words.size(); i++) {

			String entry = words.get(i);

			if ((entry.length() < minLen) || preserveLength > 0 && (entry.length() != input.length())
					|| variations.contains(entry)) {
				continue;
			}

			String[] compareB = toPhoneArray(dict.get(entry)[0].replaceAll("'", "").replaceAll("\\[", ""));

			for (int j = 0; j < compareA.length; j++) {
				// System.out.print("Compare A " + compareA[j]);
			}
			// System.out.print(" compareA[j] ");
			for (int j = 0; j < compareB.length; j++) {
				// System.out.println(" ,Compare B " + compareB[j]);
			}

			int med = Util.minEditDist(compareA, compareB);

			// found something even closer
			if (med >= minAllowedDist && med < minVal) {
				minVal = med;
				result.add(entry);
				// console.log("BEST(" + med + ")" + entry + " -> " + phonesArr);
			}

			// another best to add
			else if (med == minVal) {
				// console.log("TIED(" + med + ")" + entry + " -> " + phonesArr);
				result.add(entry);
			}
		}
		String[] s = (String[]) result.toArray(new String[0]);
		return s;
	}

	public String[] toPhoneArray(String raw) {
		ArrayList<String> result = new ArrayList<String>();
		String sofar = "";
		for (int i = 0; i < raw.length(); i++) {
			if (raw.charAt(i) == ' ' || raw.charAt(i) == '-') {
				result.add(sofar);
				sofar = "";
			}
			else if (raw.charAt(i) != '1' && raw.charAt(i) != '0') {
				sofar += raw.charAt(i);
			}
		}
		result.add(sofar);

		String[] s = result.toArray(new String[0]);
		return s;
	}

	public String[] words() {
		return dict.keySet().toArray(new String[0]);
	}

	public String[] words(Pattern regex) {
		return dict.keySet().stream()
				.filter(word -> regex.matcher(word).matches())
				.toArray(String[]::new);
	}

	//////////////////////////////////////////////////////////////////////

	private boolean _isVowel(String c) {

		return c != null && c.length() > 0 && RiTa.VOWELS.contains(c);
	}

	private boolean _isConsonant(String p) {

		return (p.length() == 1 && RiTa.VOWELS.indexOf(p) < 0
				&& "^[a-z\u00C0-\u00ff]+$".matches(p)); // TODO: precompile
	}

	private String _firstPhone(String rawPhones) {
		if (rawPhones == null || rawPhones.length() == 0) return "";
		String[] phones = rawPhones.split(RiTa.PHONEME_BOUNDARY);
		if (phones != null) return phones[0];
		return ""; // return null?
	}

	public static String[] _intersect(String[] a, String[] b) {
		// https://stackoverflow.com/questions/17863319/java-find-intersection-of-two-arrays
		Set<String> s1 = new HashSet<String>(Arrays.asList(a));
		Set<String> s2 = new HashSet<String>(Arrays.asList(b));
		s1.retainAll(s2);
		return s1.toArray(new String[0]);
	}

	private String _lastStressedPhoneToEnd(String word) {
		return _lastStressedPhoneToEnd(word, false);
	}

	private String _lastStressedPhoneToEnd(String word, boolean useLTS) {

		if (word == null || word.length() == 0) return ""; // return null?

		int idx;
		char c;
		String result, raw = _rawPhones(word, useLTS);
		if (raw == null || raw.length() == 0) return ""; // return null?

		idx = raw.lastIndexOf(RiTa.STRESSED);
		if (idx < 0) return ""; // return null?

		c = raw.charAt(--idx);
		while (c != '-' && c != ' ') {
			if (--idx < 0) {
				return raw; // single-stressed syllable
			}
			c = raw.charAt(idx);
		}
		result = raw.substring(idx + 1);

		return result;
	}

	private String _lastStressedVowelPhonemeToEnd(String word, boolean useLTS) {

		if (word == null || word.length() == 0) return ""; // return null?

		String raw = _lastStressedPhoneToEnd(word, useLTS);
		if (raw == null || raw.length() == 0) return ""; // return null?

		String[] syllables = raw.split(" ");
		String lastSyllable = syllables[syllables.length - 1];
		lastSyllable = lastSyllable.replace("[^a-z-1 ]", "");

		int idx = -1;
		for (int i = 0; i < lastSyllable.length(); i++) {
			char c = lastSyllable.charAt(i);
			if (RiTa.VOWELS.contains(Character.toString(c))) {
				idx = i;
				break;
			}
		}

		return lastSyllable.substring(idx);
	}

	private String _firstStressedSyl(String word, boolean useLTS) {

		String raw = _rawPhones(word, useLTS);

		if (raw == "" || raw == null) return ""; // return null?

		int idx = raw.indexOf(RiTa.STRESSED);

		if (idx < 0) return ""; // no stresses... return null?

		char c = raw.charAt(--idx);

		while (c != ' ') {
			if (--idx < 0) {
				// single-stressed syllable
				idx = 0;
				break;
			}
			c = raw.charAt(idx);
		}

		String firstToEnd = idx == 0 ? raw : raw.substring(idx).trim();
		idx = firstToEnd.indexOf(" ");

		return idx < 0 ? firstToEnd : firstToEnd.substring(0, idx);
	}

	public String _posData(String word) {

		String[] rdata = _lookupRaw(word);
		return (rdata != null && rdata.length == 2)
				? rdata[1].replaceAll("'", "").replaceAll("\\]", "")
				: "";
	}

	String[] _posArr(String word) {

		String pl = _posData(word);
		return (pl == null || pl.length() == 0)
				? new String[0]
				: pl.split(" ");
	}

	public String _bestPos(String word) {

		String[] pl = _posArr(word);
		return (pl.length > 0) ? pl[0] : "";
	}

	private String[] _lookupRaw(String word) {
		String[] result = null;
		if (word != null && word.length() > 0) {
			word = word.toLowerCase();
			if (dict != null) {
				result = dict.get(word);
			}
		}
		return result != null ? result : new String[0];
	}

	public String _rawPhones(String word) {
		return this._rawPhones(word, false);
	}

	public String _rawPhones(String word, boolean noLts) {

		String[] rdata = _lookupRaw(word);
		if (rdata != null && rdata.length != 0) return rdata[0];

		if (!noLts) {
			if (RiTa.lts == null) throw new RiTaException("Null LTS");

			String[] phones = RiTa.lts.computePhones(word);
			if (phones != null && phones.length > 0) {
				return Util.syllablesFromPhones(phones);
				//.replaceAll("\\[", "").replaceAll("'", "");
			}
		}
		return "";
	}

	public static void main(String[] args) throws Exception {
		Lexicon lex = new Lexicon(RiTa.DICT_PATH);
		//		System.out.println(lex.dict.get("dog")[0]);
		//		System.out.println(lex.dict.get("dog")[1]);
		//		System.out.println(lex._rawPhones("dog"));
		System.out.println(lex._rawPhones("absolot"));
	}

}
