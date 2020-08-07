package rita;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class Lexicon {

	private static int MAP_SIZE = 30000;
	private static String EA[] = { }, E = "";
	private static String DELIM = ":";

	public Map<String, String[]> dict; // data

	public Lexicon(String filePath) {
		List<String> lines = loadJSON(filePath);
		if (lines == null || lines.size() < 2) {
			throw new RiTaException("Problem parsing lexicon files");
		}
		populateDict(lines);
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

	public String[] alliterations(String theWord, Map<String, Object> opts) {

		if (theWord == null || theWord.length() == 0) return EA;

		opts = this.parseArgs(opts);

		int limit = Util.intOpt("limit", opts);
		boolean silent = Util.boolOpt("silent", opts);
		String targetPos = Util.strOpt("targetPos", opts);

		// only allow consonant inputs
		if (Util.contains(RiTa.VOWELS, theWord.charAt(0))) {
			if (!silent && !RiTa.SILENT) {
				console.warn("Expects a word starting with a consonant, got: " + theWord);
			}
			return EA;
		}

		String fss = this._firstStressedSyl(theWord, false);
		if (fss == null) return EA;

		String phone = this._firstPhone(fss);
		ArrayList<String> result = new ArrayList<>();

		// make sure we parsed first phoneme
		if (phone == null) {
			if (!silent && !RiTa.SILENT) {
				console.warn("Failed parsing first phone in '" + theWord + "'");
			}
			return EA;
		}

		String[] words = dict.keySet().toArray(EA);
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String[] rdata = dict.get(word);
			// check word length and syllables 
			if (word.equals(theWord) || !this.checkCriteria(word, rdata, opts)) {
				continue;
			}
			if (targetPos.length() > 0) {
				word = this.matchPos(word, rdata, opts);
				if (word == null) continue;
			}
			String c2 = this._firstPhone(this._firstStressedSyl(word));
			if (phone == c2) result.add(word);
			if (result.size() >= limit) break;
		}

		return result.toArray(EA);
	}

	public boolean hasWord(String word) {
		return (word != null && word.length() > 0)
				? Inflector.isPlural(word.toLowerCase())
				: false;
	}

	public boolean isAlliteration(String word1, String word2, boolean noLts) {
		if (word1 == null || word2 == null) return false;
		if (word1.length() == 0 || word2.length() == 0) return false;

		String c1 = _firstPhone(_firstStressedSyl(word1, noLts));
		String c2 = _firstPhone(_firstStressedSyl(word2, noLts));

		return c1.length() > 0 && c2.length() > 0
				&& !_isVowel(c1.charAt(0)) && c1.equals(c2);
	}

	public boolean isRhyme(String word1, String word2, boolean noLts) {

		if (word1 == null || word2 == null) return false;
		if (word1.toUpperCase().equals(word2.toUpperCase())) return false;

		String phones1 = _rawPhones(word1, noLts);
		String phones2 = _rawPhones(word2, noLts);
		if (phones2 == phones1) return false;

		String p1 = _lastStressedVowelPhonemeToEnd(word1, noLts);
		String p2 = _lastStressedVowelPhonemeToEnd(word2, noLts);

		return p1.length() > 0 && p2.length() > 0 && p1.equals(p2);
	}

	private String matchPos(String word, String[] rdata, Map<String, Object> opts) {
		return this.matchPos(word, rdata, opts, false);
	}

	private String matchPos(String word, String[] rdata,
			Map<String, Object> opts, boolean strict) {

		String pos = (String) opts.get("pos");
		String targetPos = (String) opts.get("targetPos");
		int numSyllables = (int) opts.get("numSyllables");
		boolean pluralize = (boolean) opts.get("pluralize");
		boolean conjugate = (boolean) opts.get("conjugate");

		if (strict) {
			if (!targetPos.equals(rdata[1].split(" ")[0])) {
				return null;
			}
		}
		else {
			if (!Arrays.asList(rdata[1].split(" ")).contains(targetPos)) {
				return null;
			}
		}

		// we've matched our pos, pluralize or inflect if needed
		String result = word;
		if (pluralize) {
			if (isMassNoun(word, rdata[1])) return null;
			result = RiTa.pluralize(word);
		}
		if (conjugate) { // inflect
			result = reconjugate(word, pos);
		}

		// verify we haven't changed syllable count
		if (result != word && numSyllables > 0) {
			boolean tmp = RiTa.SILENCE_LTS;
			RiTa.SILENCE_LTS = true;
			int num = RiTa.syllables(result).split(RiTa.SYLLABLE_BOUNDARY).length;
			RiTa.SILENCE_LTS = tmp;
			// reject if syllable count has changed
			if (num != numSyllables) return null;
		}

		return result;
	}

	private String reconjugate(String word, String pos) {
		switch (pos) {
		/*  VBD 	Verb, past tense
		    VBG 	Verb, gerund or present participle
		    VBN 	Verb, past participle
		    VBP 	Verb, non-3rd person singular present
		    VBZ 	Verb, 3rd person singular present */
		case "vbd":
			return RiTa.conjugate(word, Util.opts(
					"number", RiTa.SINGULAR,
					"person", RiTa.FIRST_PERSON,
					"tense", RiTa.PAST_TENSE));
		case "vbg":
			return RiTa.presentParticiple(word);
		case "vbn":
			return RiTa.pastParticiple(word);
		case "vbp":
			return word;// RiTa.conjugate(word); // no args
		case "vbz":
			return RiTa.conjugate(word, Util.opts(
					"number", RiTa.SINGULAR,
					"person", RiTa.THIRD_PERSON,
					"tense", RiTa.PRESENT_TENSE));
		default:
			throw new RiTaException("Unexpected pos: " + pos);
		}
	}

	private boolean isMassNoun(String w, String pos) {
		return w.endsWith("ness")
				|| w.endsWith("ism")
				|| pos.indexOf("vbg") > 0
				|| Util.contains(Util.MASS_NOUNS, w);
	}

	private boolean checkCriteria(String word, String[] rdata, Map<String, Object> opts) {

		int minLength = (int) opts.get("minLength");
		int maxLength = (int) opts.get("maxLength");
		int numSyllables = (int) opts.get("numSyllables");

		// check word length
		if (word.length() > maxLength) return false;
		if (word.length() < minLength) return false;

		// match numSyllables if supplied
		if (numSyllables > 0) {
			int syls = rdata[0].split(" ").length;
			if (numSyllables != syls) return false;
		}
		return true;
	}

	private Map<String, Object> parseArgs(Map<String, Object> opts) {
		String tpos = Util.strOpt("pos", opts, E);
		boolean pluralize = false;
		boolean conjugate = false;
		if (tpos.length() > 0) {
			pluralize = (tpos.equals("nns"));
			conjugate = (tpos.charAt(0) == 'v' && tpos.length() > 2);
			if (tpos.charAt(0) == 'n') tpos = "nn";
			else if (tpos.charAt(0) == 'v') tpos = "vb";
			else if (tpos.equals("r")) tpos = "rb";
			else if (tpos.equals("a")) tpos = "jj";
		}
		if (opts == null) opts = new HashMap<String, Object>();
		opts.put("minDistance", Util.intOpt("minDistance", opts, 1));
		opts.put("numSyllables", Util.intOpt("numSyllables", opts, 0));
		opts.put("minLength", Util.intOpt("minLength", opts, 3));
		opts.put("maxLength", Util.intOpt("maxLength", opts, Integer.MAX_VALUE));
		opts.put("limit", Util.intOpt("limit", opts, Integer.MAX_VALUE));
		opts.put("pluralize", pluralize);
		opts.put("conjugate", conjugate);
		opts.put("targetPos", tpos);
		return opts;
	}

	public String[] search(String regex) {
		return this.search(regex, null);
	}

	public String[] search(String regex, Map<String, Object> opts) {

		String[] words = dict.keySet().toArray(EA);
		if (regex == null) return words;

		String type = Util.strOpt("type", opts, E);
		int limit = Util.intOpt("limit", opts);

		if (type.equals("stresses")) {
			// if we have a stress string without slashes
			// add them to the regex pattern
			if (RE.test("^[01]+$", regex)) {
				regex = String.join("/", regex.split(E));
			}
		}
		if (RE.test("^/.*/$", regex)) {
			regex = regex.substring(1, regex.length() - 1);
		}

		Pattern re = Pattern.compile(regex);
		opts = this.parseArgs(opts);
		//console.log(opts);

		ArrayList<String> result = new ArrayList<String>();
		boolean tmp = RiTa.SILENCE_LTS;
		RiTa.SILENCE_LTS = true;

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String[] rdata = dict.get(word);
			if (!this.checkCriteria(word, rdata, opts)) continue;
			if (((String) opts.get("targetPos")).length() > 0) {
				word = matchPos(word, rdata, opts, false);
				if (word == null) continue;
			}
			if (type.equals("stresses")) {
				String stresses = RiTa.stresses(word);
				if (RE.test(re, stresses)) result.add(word);
			}
			if (type.equals("phones")) {
				if (RE.test(re, RiTa.phones(word))) result.add(word);
			}
			else {
				if (RE.test(re, word)) result.add(word);
			}
			if (limit > 0 && result.size() >= limit) break;
		}

		RiTa.SILENCE_LTS = tmp;
		return result.toArray(EA);
	}

	public String[] soundsLike(String word) {
		return this.soundsLike(word, null);
	}

	public String[] spellsLike(String word) {
		return this.spellsLike(word, null);
	}

	public String[] soundsLike(String word, Map<String, Object> opts) {
		String[] result = EA;
		if (word != null && word.length() > 0) {
			if (opts == null) opts = new HashMap<>();
			opts.put("type", "sound");
			result = Util.boolOpt("matchSpelling", opts)
					? this.similarBySoundAndLetter(word, opts)
					: this.similarByType(word, opts);
		}
		return result;
	}

	public String[] spellsLike(String word, Map<String, Object> opts) {
		String[] result = EA;
		if (word != null && word.length() > 0) {
			if (opts == null) opts = new HashMap<>();
			opts.put("type", "letter");
			result = this.similarByType(word, opts);
		}
		return result;
	}
	//
	//	public String[] similarBy(String word, Map<String, Object> opts) // TODO
	//	{
	//		if (word == null || word.length() == 0) return EA;
	//		if (opts == null) return EA;
	//		if (opts.get("type") == null || opts.get("type") == E) {
	//			opts.put("type", "letter");
	//		}
	//		return (opts.get("type") == "soundAndLetter")
	//				? similarBySoundAndLetter(word, opts)
	//				: similarByType(word, opts);
	//	}

	public String[] similarBySoundAndLetter(String word, Map<String, Object> opts) {

		opts.put("type", "letter");
		// opts.get("type") = "letter";
		String[] simLetter = similarByType(word, opts);
		if (simLetter.length < 1) return EA;

		opts.put("type", "sound");
		String[] simSound = similarByType(word, opts);
		if (simSound.length < 1) return EA;

		return _intersect(simSound, simLetter);
	}

	public String randomWord(Map<String, Object> opts) {

		int minLength = Util.intOpt("minLength", opts, 4);
		opts = this.parseArgs(opts);
		opts.put("minLength", minLength); // default to 4, not 3

		String tpos = (String) opts.get("targetPos");
		String[] words = dict.keySet().toArray(EA);
		int ran = (int) Math.floor(RandGen.random(words.length));
		for (int k = 0; k < words.length; k++) {
			int j = (ran + k) % words.length;
			String word = words[j];
			String[] rdata = dict.get(word);
			if (!this.checkCriteria(word, rdata, opts)) continue;
			if (tpos.length() == 0) return words[j]; // done if no pos
			String result = this.matchPos(word, rdata, opts, true);
			if (result != null) return result;
		}
		throw new RiTaException("No random word with options: " + opts);
	}

	public String[] rhymes(String theWord) {
		return this.rhymes(theWord, null);
	}

	public String[] rhymes(String theWord, Map<String, Object> opts) {

		opts = this.parseArgs(opts);

		int limit = Util.intOpt("limit", opts);
		String tpos = Util.strOpt("targetPos", opts);

		if (theWord == null || theWord.length() == 0) return EA;

		String phone = this._lastStressedPhoneToEnd(theWord);
		if (phone == null) return EA;

		ArrayList<String> result = new ArrayList<String>();
		String[] words = dict.keySet().toArray(EA);
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			String[] rdata = dict.get(word);

			// check word length and syllables 
			if (word.equals(theWord) || !this.checkCriteria(word, rdata, opts)) {
				continue;
			}

			if (tpos.length() > 0) {
				word = this.matchPos(word, rdata, opts);
				if (word == null) continue;
			}

			// check for the rhyme
			if (rdata[0].endsWith(phone)) result.add(word);
			if (result.size() >= limit) break;
		}

		return result.toArray(EA);
	}

	public String[] similarByType(String theWord, Map<String, Object> opts) {
		opts = this.parseArgs(opts);

    String type = Util.strOpt("type", opts);
    boolean sound = type.equals("sound");
		int limit = Util.intOpt("limit", opts);
		int minDist = Util.intOpt("minDistance", opts);
		String tpos = Util.strOpt("targetPos", opts);
		
		
		
    String input = theWord.toLowerCase();
    String variations = input+"||"+input + "s||"+ input + "es";
    String[] phonesA = null, words = dict.keySet().toArray(EA);
    
    if (sound) {
    	phonesA = this.toPhoneArray(this._rawPhones(input));
    	if (phonesA == null) return EA;
    }

		ArrayList<String> result = new ArrayList<String>();
		int minVal = Integer.MAX_VALUE;
    for (int i = 0; i < words.length; i++) {
      String word = words[i];
			String[] rdata = dict.get(word);
      if (!this.checkCriteria(word, rdata, opts)) continue;
      if (variations.contains(word)) continue;

      if (tpos.length() > 0) {
        word = this.matchPos(word, rdata, opts);
        if (word == null) continue;
      }

      int med = -1;
      String[] phonesB;
      if (sound) {
      	phonesB = rdata[0].replaceAll("1", "")
      			.replaceAll(" ", "-").split("-");
      	med = minEditDist(phonesA, phonesB);
      }
      else {
      	med = minEditDist(input, word);
      }

      // found something even closer
      if (med >= minDist && med < minVal) {
        minVal = med;
        result = new ArrayList<String>();
        result.add(word);
      }
      // another best to add
      else if (med == minVal) {
        result.add(word);
      }
      if (result.size() == limit) break;
    }
    
    return result.toArray(EA);
	}

	/* TODO check result against js (compareA, compare B)
	public String[] similarByTypeX(String word, Map<String, Object> opts) {
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

		boolean noLts = false; // TODO _rawPhones second param has to be removed?
		String[] compareA = (opts.get("type") == "sound" ? toPhoneArray(_rawPhones(input, noLts))
				: new String[] { input });

		for (int i = 0; i < words.size(); i++) {

			String entry = words.get(i);

			if ((entry.length() < minLen) || preserveLength > 0 && (entry.length() != input.length())
					|| variations.contains(entry)) {
				continue;
			}

			String[] compareB = toPhoneArray(dict.get(entry)[0].replaceAll("'", E).replaceAll("\\[", E));

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
		String[] s = (String[]) result.toArray(EA);
		return s;
	}*/

	public String[] toPhoneArray(String raw) {
		ArrayList<String> result = new ArrayList<String>();
		String sofar = E;
		for (int i = 0; i < raw.length(); i++) {
			if (raw.charAt(i) == ' ' || raw.charAt(i) == '-') {
				result.add(sofar);
				sofar = E;
			}
			else if (raw.charAt(i) != '1' && raw.charAt(i) != '0') {
				sofar += raw.charAt(i);
			}
		}
		result.add(sofar);

		String[] s = result.toArray(EA);
		return s;
	}

	public String[] words() {
		return dict.keySet().toArray(EA);
	}

	public String[] words(Pattern regex) {
		return dict.keySet().stream()
				.filter(word -> regex.matcher(word).matches())
				.toArray(String[]::new);
	}

	//////////////////////////////////////////////////////////////////////

	public static String[] _intersect(String[] a, String[] b) {
		// https://stackoverflow.com/questions/17863319/java-find-intersection-of-two-arrays
		Set<String> s1 = new HashSet<String>(Arrays.asList(a));
		Set<String> s2 = new HashSet<String>(Arrays.asList(b));
		s1.retainAll(s2);
		return s1.toArray(EA);
	}

	public String _posData(String word) {

		String[] rdata = _lookupRaw(word);
		return (rdata != null && rdata.length == 2)
				? rdata[1].replaceAll("'", E).replaceAll("\\]", E)
				: E;
	}

	public String _bestPos(String word) {

		String[] pl = _posArr(word);
		return (pl.length > 0) ? pl[0] : E;
	}

	public String _rawPhones(String word) {
		return this._rawPhones(word, false);
	}

	public String _rawPhones(String word, boolean noLts) {

		String[] rdata = _lookupRaw(word);
		if (rdata != null && rdata.length != 0) {
			return rdata[0];
		}

		if (!noLts) { // check lts otherwise
			if (RiTa.lts == null) throw new RiTaException("Null LTS");

			String[] phones = RiTa.lts.computePhones(word);
			if (phones != null && phones.length > 0) {
				return Util.syllablesFromPhones(phones);
				//.replaceAll("\\[", E).replaceAll("'", E);
			}
		}
		return E;
	}

	String[] _posArr(String word) {

		String pl = _posData(word);
		return (pl == null || pl.length() == 0)
				? EA
				: pl.split(" ");
	}

	private String[] _lookupRaw(String word) {
		String[] result = null;
		if (word != null && word.length() > 0) {
			word = word.toLowerCase();
			if (dict != null) {
				result = dict.get(word);
			}
		}
		return result != null ? result : EA;
	}

	private void populateDict(List<String> lines) {

		dict = new LinkedHashMap<String, String[]>(MAP_SIZE);

		for (int i = 1; i < lines.size() - 1; i++) // ignore JS prefix/suffix
		{
			String line = lines.get(i).replaceAll("[\"'\\[\\]]", E);
			String[] parts = line.split(DELIM);
			if (parts == null || parts.length != 2) {
				throw new RiTaException("Illegal entry: " + line);
			}

			dict.put(parts[0].trim(), parts[1].split(","));
			//if (i < 10) System.out.println(parts[0].trim()
			// +": "+parts[1].split(",")[0]);
		}
	}

	private boolean _isVowel(String c) {
		return c != null && RiTa.VOWELS.contains(c);
	}

	private boolean _isVowel(char c) {
		return Util.contains(RiTa.VOWELS, c);
	}

	private boolean _isConsonant(String p) {
		return (p.length() == 1 && RiTa.VOWELS.indexOf(p) < 0
				&& "^[a-z\u00C0-\u00ff]+$".matches(p)); // TODO: precompile
	}

	private String _lastStressedPhoneToEnd(String word) {
		return _lastStressedPhoneToEnd(word, false);
	}

	private String _lastStressedPhoneToEnd(String word, boolean noLts) {

		if (word == null || word.length() == 0) return E; // return null?

		String raw = _rawPhones(word, noLts);
		if (raw == null || raw.length() == 0) return E; // return null?
		int idx = raw.lastIndexOf(RiTa.STRESSED);
		if (idx < 0) return E; // return null?
		char c = raw.charAt(--idx);
		while (c != '-' && c != ' ') {
			if (--idx < 0) {
				return raw; // single-stressed syllable
			}
			c = raw.charAt(idx);
		}
		return raw.substring(idx + 1);
	}

	private String _lastStressedVowelPhonemeToEnd(String word, boolean noLts) {

		if (word == null || word.length() == 0) return E; // return null?

		String raw = _lastStressedPhoneToEnd(word, noLts);
		if (raw == null || raw.length() == 0) return E; // return null?

		String[] syllables = raw.split(" ");
		String lastSyllable = syllables[syllables.length - 1];
		lastSyllable = lastSyllable.replace("[^a-z-1 ]", E);

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

	private String _firstStressedSyl(String word) {
		return _firstStressedSyl(word, false);
	}

	private String _firstStressedSyl(String word, boolean noLts) {

		String raw = _rawPhones(word, noLts);
		if (raw == E || raw == null) return E; // return null?
		int idx = raw.indexOf(RiTa.STRESSED);
		if (idx < 0) return E; // no stresses... return null?
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

	private String _firstPhone(String rawPhones) {
		if (rawPhones != null && rawPhones.length() > 0) {
			String[] phones = rawPhones.split(RiTa.PHONEME_BOUNDARY);
			if (phones != null) return phones[0];
		}
		return E; // return null?
	}

	private static int minEditDist(String source, String target) {
		return minEditDist(source.split(""), target.split(""));
	}
	
	private static int minEditDist(String[] source, String[] target) {

		int i, j;

		int cost; // cost
		String sI; // ith character of s
		String tJ; // jth character of t
		int[][] matrix = new int[source.length + 1][target.length + 1];
		// Step 1 ----------------------------------------------

		for (i = 0; i <= source.length; i++) {
			// System.out.println(i);
			matrix[i][0] = i;
		}

		for (j = 0; j <= target.length; j++) {
			matrix[0][j] = j;
		}

		// Step 2 ----------------------------------------------

		for (i = 1; i <= source.length; i++) {
			sI = source[i - 1];

			// Step 3 --------------------------------------------

			for (j = 1; j <= target.length; j++) {
				tJ = target[j - 1];

				// Step 4 ------------------------------------------

				cost = (sI == tJ) ? 0 : 1;

				// Step 5 ------------------------------------------
				matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1,
						matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + cost);
			}
		}

		// Step 6 ----------------------------------------------
		return matrix[source.length][target.length];
	}

	public static void main(String[] args) throws Exception {
		Lexicon lex = new Lexicon(RiTa.DICT_PATH);
		//		System.out.println(lex.dict.get("dog")[0]);
		//		System.out.println(lex.dict.get("dog")[1]);
		//		System.out.println(lex._rawPhones("dog"));
		//System.out.println(lex._rawPhones("absolot"));
		//		Pattern re = Pattern.compile("phant");
		//		System.out.println(RE.test(re, "elephantine"));
	}

}
