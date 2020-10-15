package rita;

import java.lang.reflect.*;
import java.util.*;

public class Util {

	// Takes a syllabification and turns it into a string of phonemes,
	// delimited with dashes, with spaces between syllables
	// see js and/or https://github.com/dhowe/RiTa/blob/master/java/rita/support/LetterToSound.java#L450 ?
	static String syllablesToPhones(List<List<List<String>>> syllables) {

		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < syllables.size(); i++) {
			List<List<String>> syl = syllables.get(i);
			String stress = syl.get(0).get(0);
			List<String> onset = syl.get(1);
			List<String> nucleus = syl.get(2);
			List<String> coda = syl.get(3);
			if (stress != null && nucleus.size() > 0) {
				nucleus.set(0, nucleus.get(0) + stress);
			}
			ArrayList<String> data = new ArrayList<String>();
			data.addAll(onset);
			data.addAll(nucleus);
			data.addAll(coda);
			ret.add(String.join("-", data.toArray(new String[0])));
		}
		return String.join(" ", ret.toArray(new String[0]));
	}

	// see js and/or https://github.com/dhowe/RiTa/blob/master/java/rita/support/LetterToSound.java#L171
	static String syllablesFromPhones(String[] input) {

		boolean dbug = false;

		// returned data structure
		List<List<List<String>>> syllables = new ArrayList<List<List<String>>>();
		//String[][] syllables = new String[input.length][2];

		List<String> internuclei = new ArrayList<String>();
		//String[] internuclei = new String[0];

		String[] sylls = input;

		if (sylls == null || sylls.length == 0) {
			return "";
		}

		for (int i = 0; i < sylls.length; i++) {

			String stress = "";
			String phoneme = sylls[i].trim();
			if (phoneme.length() == 0) continue;

			String last = phoneme.substring(phoneme.length() - 1);
			if (Util.isNum(last)) {
				stress = last;
				phoneme = phoneme.substring(0, phoneme.length() - 1);
			}

			if (dbug) System.out.println(i + ")" + phoneme + " stress="
					+ stress + " inter=" + String.join(":", internuclei));

			if (isVowel(phoneme)) {

				// Split the consonants seen since the last nucleus into coda and onset.
				String[] coda = null, onset = null;

				// Make the largest onset we can. The "split" variable marks the break point.
				for (int split = 0; split < internuclei.size() + 1; split++) {

					coda = Util.slice(internuclei, 0, split).toArray(new String[0]);
					onset = Util.slice(internuclei, split, internuclei.size()).toArray(new String[0]);

					if (dbug) System.out.println("  " + split + ") onset=" + String.join(":", onset) +
							"  coda=" + String.join(":", coda) + "  inter=" + String.join(":", internuclei));

					// If we are looking at a valid onset, or if we"re at the start of the word
					// (in which case an invalid onset is better than a coda that doesn"t follow
					// a nucleus), or if we"ve gone through all of the onsets and we didn"t find
					// any that are valid, then split the nonvowels we"ve seen at this location.
					boolean isOnset = isOnset(String.join(" ", onset));
					if (isOnset || syllables.size() == 0 || onset.length == 0) {
						if (dbug) System.out.println("  break " + phoneme);
						break;
					}
				}

				// Tack the coda onto the coda of the last syllable.
				// Can"t do it if this is the first syllable.
				if (syllables.size() > 0) {
					syllables.get(syllables.size() - 1).get(3).addAll(Arrays.asList(coda));
					if (dbug) {
						List<String> l = syllables.get(syllables.size() - 1).get(3);
						System.out.println("  tack: " + coda + " -> len=" + l.size() + " [" + l.get(3) + "]");
					}
				}

				// Make a new syllable out of the onset and nucleus.
				List<List<String>> toPush = new ArrayList<List<String>>();
				for (int j = 0; j < 4; j++) {
					toPush.add(new ArrayList<String>()); // ?
				}
				toPush.get(0).add(stress);
				toPush.get(1).addAll(Arrays.asList(onset));
				toPush.get(2).add(phoneme);

				syllables.add(toPush);

				// At this point we"ve processed the internuclei list.
				internuclei = new ArrayList<String>();
			}
			else if (!(isConsonant(phoneme)) && !phoneme.equals(" ")) {
				throw new RuntimeException("Invalid phoneme: " + phoneme);
			}
			else { // a consonant
				internuclei.add(phoneme);
			}
		}

		// Done looping through phonemes. 
		// We may have consonants left at the end.
		// We may have even not found a nucleus.
		if (internuclei.size() > 0) {
			if (syllables.size() == 0) {
				// JS: syllables.push([[undefined], internuclei, [], []]);
				List<List<String>> toPush = new ArrayList<List<String>>();
				for (int j = 0; j < 4; j++) {
					toPush.add(new ArrayList<String>()); // ?
				}
				toPush.get(0).add(null);
				toPush.get(1).addAll(internuclei);
			}
			else {
				//System.out.println(syllables.get(syllables.size() - 1));
				syllables.get(syllables.size() - 1).get(3).addAll(internuclei);
			}
		}

		return syllablesToPhones(syllables);
	}

	public static boolean isNum(String strNum) {
		if (strNum == null) return false;
		try {
			Double.parseDouble(strNum);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public static boolean isNode() {
		return false;
	}

	public static Object invokeStatic(Method method, Object... args) {
		return invoke(null, method, args);
	}

	public static Object invoke(Object target, Method method, Object... args) {
		try {
			return method.invoke(target, args);//.toString();
		} catch (Exception e) {
			String msg = "Invoke error";
			if (e instanceof java.lang.IllegalAccessException)
				msg = "Make sure the class you are trying to "
						+ "(dynamically) cast is publicly defined.";
			throw new RiTaException(msg, e);
		}
	}

	public static boolean hasProperty(Object o, String prop) {
		return getProperty(o, prop) != null;
	}

	public static boolean hasMethod(Object o, String prop) {
		return getMethods(o, prop).length > 0;
	}

	public static Object getProperty(Object o, String prop) {
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(prop)) {
				fields[i].setAccessible(true);
				try {
					return fields[i].get(o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.err.println("[WARN] " + e.getMessage());
				}
			}
		}
		// we've failed - check for a map
		return (o instanceof Map) ? ((Map)o).get(prop) : null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T getProperty(Object o, String prop, T defaultVal) {

		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(prop)) {
				fields[i].setAccessible(true);
				try {
					return (T) fields[i].get(o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					System.err.println("[WARN] " + e.getMessage());
				}
			}
		}
		return defaultVal;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Method getStatic(Class c, String name, Class... params) {
		try {
			return c.getDeclaredMethod(name, params);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method getMethod(Object o, String name) {
		return getMethod(o, name, new Class[0]);
	}

	@SuppressWarnings("rawtypes")
	public static Method getMethod(Object o, String name, Class... params) {
		try {
			return o.getClass().getDeclaredMethod(name, params);
		} catch (Exception e) {
			return null;
		}
	}

	public static Method[] getMethods(Object o, String meth) {
		List<Method> result = new ArrayList<Method>();
		Method[] methods = o.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(meth)) {
				result.add(methods[i]);
			}
		}
		return result.toArray(new Method[0]);
	}

	public static Map<String, Object> deepMerge(Map<String, Object> m1, Map<String, Object> m2) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (m1 != null) result.putAll(m1);
		if (m2 != null) result.putAll(m2);
		return result;
	}

	public static boolean boolOpt(String key, Map<String, Object> opts) {
		return boolOpt(key, opts, false);
	}

	public static boolean boolOpt(String key, Map<String, Object> opts, boolean def) {
		return (opts != null) ? (boolean) opts.getOrDefault(key, def) : def;
	}

	public static int intOpt(String key, Map<String, Object> opts) {
		return intOpt(key, opts, -1);
	}

	public static int intOpt(String key, Map<String, Object> opts, int def) {
		return (opts != null) ? (int) opts.getOrDefault(key, def) : def;
	}

	public static float floatOpt(String key, Map<String, Object> opts) {
		return floatOpt(key, opts, -1);
	}

	public static float floatOpt(String key, Map<String, Object> opts, float def) {
		return (opts != null) ? (float) opts.getOrDefault(key, def) : def;
	}

	public static String strOpt(String key, Map<String, Object> opts) {
		return strOpt(key, opts, null);
	}

	public static String strOpt(String key, Map<String, Object> opts, String def) {
		return (opts != null) ? (String) opts.getOrDefault(key, def) : def;
	}

	public static Map<String, Object> mapOpt(String key, Map<String, Object> opts) {
		return mapOpt(key, opts, null);// new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> mapOpt(String key, Map<String, Object> opts, Map<String, Object> def) {
		return opts != null ? (Map<String, Object>) opts.getOrDefault(key, def) : def;
		// if (opts == null) return def;
		//Object val = opts.get(key);
		//return (val == null || !(val instanceof Map)) ? def : (Map<String, Object>) val;
	}

	public static String[] shuffle(String[] arr) { // shuffle array //TODO what is the type of second arg
		String[] newArray = arr;

		Random rand = new Random();

		for (int i = 0; i < newArray.length; i++) {
			int randomIndexToSwap = rand.nextInt(newArray.length);
			String temp = newArray[randomIndexToSwap];
			newArray[randomIndexToSwap] = newArray[i];
			newArray[i] = temp;
		}
		/*
		 *
		 * // int len = arr.length; // int i = len; // Random rand = new Random();
		 * 
		 * while (i>0) { int p = parseInt(((Object) randomable).random() * len); String
		 * t = newArray[i]; newArray[i] = newArray[p]; newArray[p] = t; i--;
		 * 
		 * }
		 */
		return newArray;
	}

	public static Map<String, Object> opts() {
		return new HashMap<String, Object>();
	}

	public static Map<String, Object> opts(String key, Object val) {
		return opts(new String[] { key }, new Object[] { val });
	}

	public static Map<String, Object> opts(String key1, Object val1, String key2, Object val2) {
		return opts(new String[] { key1, key2 }, new Object[] { val1, val2 });
	}

	public static Map<String, Object> opts(String key1, Object val1, String key2, Object val2, String key3, Object val3) {
		return opts(new String[] { key1, key2, key3 }, new Object[] { val1, val2, val3 });
	}

	public static Map<String, Object> opts(String key1, Object val1,
			String key2, Object val2, String key3, Object val3, String key4, Object val4) {
		return opts(new String[] { key1, key2, key3, key4 }, new Object[] { val1, val2, val3, val4 });
	}

	public static Map<String, Object> opts(String[] keys, Object[] vals) {
		if (keys.length != vals.length) throw new RuntimeException("Bad Args");
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			data.put(keys[i], vals[i]);
		}
		return data;
	}

	public static List<?> slice(List<?> arr, int from) {
		return slice(arr, from, arr.size());
	}

	public static List<?> slice(List<?> arr, int from, int to) {
		return arr.subList(from, to);
	}

	public static String[] slice(String[] arr, int from) {
		return slice(arr, from, arr.length);
	}

	public static String[] slice(String[] arr, int from, int to) {
		return Arrays.copyOfRange(arr, from, to);
	}

	private static boolean isVowel(String phone) {
		return Arrays.asList(Vowels).contains(phone);
	}

	private static boolean isConsonant(String phone) {
		return Arrays.asList(Consonants).contains(phone);
	}

	private static boolean isOnset(String phone) {
		return Arrays.asList(Onsets).contains(phone);
	}

	////////////////////////////////////////////////////////////////////////

	public static final String[] MASS_NOUNS = { "abalone", "asbestos", "barracks", "bathos", "breeches", "beef", "britches", "chaos", "cognoscenti",
			"clippers", "corps", "cosmos", "crossroads", "diabetes", "ethos", "gallows", "graffiti", "herpes", "innings", "lens", "means", "measles",
			"mews", "mumps", "news", "pathos", "pincers", "pliers", "proceedings", "rabies", "rhinoceros", "sassafras", "scissors", "series", "shears",
			"species", "tuna", "acoustics", "aesthetics", "aquatics", "basics", "ceramics", "classics", "cosmetics", "dialectics", "deer", "dynamics",
			"ethics", "harmonics", "heroics", "mechanics", "metrics", "ooze", "optics", "physics", "polemics", "pyrotechnics", "statistics", "tactics",
			"tropics", "bengalese", "bengali", "bonsai", "booze", "cellulose", "mess", "moose", "burmese", "chinese", "colossus", "congolese", "discus",
			"electrolysis", "emphasis", "expertise", "flu", "fructose", "gauze", "glucose", "grease", "guyanese", "haze", "incense", "japanese", "lebanese",
			"malaise", "mayonnaise", "maltese", "music", "money", "menopause", "merchandise", "olympics", "overuse", "paradise", "poise", "potash",
			"portuguese", "prose", "recompense", "remorse", "repose", "senegalese", "siamese", "singhalese", "innings", "sleaze", "sioux", "sudanese",
			"suspense", "swiss", "taiwanese", "vietnamese", "unease", "aircraft", "anise", "antifreeze", "applause", "archdiocese", "apparatus",
			"asparagus", "barracks", "bellows", "bison", "bluefish", "bourgeois", "bream", "brill", "butterfingers", "cargo", "carp", "catfish", "chassis",
			"clothes", "chub", "cod", "codfish", "coley", "contretemps", "corps", "crawfish", "crayfish", "crossroads", "cuttlefish", "deer", "dice",
			"dogfish", "doings", "dory", "downstairs", "eldest", "earnings", "economics", "electronics", "firstborn", "fish", "flatfish", "flounder",
			"fowl", "fry", "fries", "works", "goldfish", "golf", "grand", "grief", "haddock", "hake", "halibut", "headquarters", "herring", "hertz",
			"horsepower", "goods", "hovercraft", "ironworks", "kilohertz", "ling", "shrimp", "swine", "lungfish", "mackerel", "macaroni", "means",
			"megahertz", "moorfowl", "moorgame", "mullet", "nepalese", "offspring", "pants", "patois", "pekinese", "perch", "pickerel", "pike", "potpourri",
			"precis", "quid", "rand", "rendezvous", "roach", "salmon", "samurai", "series", "seychelles", "shad", "sheep", "shellfish", "smelt",
			"spaghetti", "spacecraft", "species", "starfish", "stockfish", "sunfish", "superficies", "sweepstakes", "smallpox", "swordfish", "tennis",
			"tobacco", "triceps", "trout", "tuna", "tunafish", "turbot", "trousers", "turf", "dibs", "undersigned", "waterfowl", "waterworks", "waxworks",
			"wildfowl", "woodworm", "yen", "aries", "pisces", "forceps", "jeans", "mathematics", "news", "odds", "politics", "remains", "goods", "aids",
			"wildlife", "shall", "would", "may", "might", "ought", "should", "wildlife", "acne", "admiration", "advice", "air", "anger", "anticipation",
			"assistance", "awareness", "bacon", "baggage", "blood", "bravery", "chess", "clay", "clothing", "coal", "compliance", "comprehension",
			"confusion", "consciousness", "cream", "darkness", "diligence", "dust", "education", "electrolysis", "empathy", "enthusiasm", "envy",
			"equality", "equipment", "evidence", "feedback", "fitness", "flattery", "foliage", "fun", "furniture", "garbage", "gold", "gossip", "grammar",
			"gratitude", "gravel", "guilt", "happiness", "hardware", "hate", "hay", "health", "heat", "help", "hesitation", "homework", "honesty", "honor",
			"honour", "hospitality", "hostility", "humanity", "humility", "ice", "immortality", "independence", "information", "integrity", "intimidation",
			"jargon", "jealousy", "jewelry", "justice", "knowledge", "literacy", "logic", "luck", "lumber", "luggage", "mail", "management", "merchandise",
			"milk", "morale", "mud", "music", "nonsense", "oppression", "optimism", "oxygen", "participation", "pay", "peace", "perseverance", "pessimism",
			"pneumonia", "poetry", "police", "pride", "privacy", "propaganda", "public", "punctuation", "recovery", "rice", "rust", "satisfaction", "shame",
			"sheep", "slang", "software", "spaghetti", "stamina", "starvation", "steam", "steel", "stuff", "support", "sweat", "thunder", "timber", "toil",
			"traffic", "training", "trash", "valor", "vehemence", "violence", "warmth", "waste", "weather", "wheat", "wisdom", "work" };

	private static final String[] Consonants = { "b", "ch", "d", "dh", "f", "g", "hh", "jh", "k", "l", "m",
			"n", "ng", "p", "r", "s", "sh", "t", "th", "v", "w", "y", "z", "zh" };

	private static final String[] Vowels = { "aa", "ae", "ah", "ao", "aw", "ax", "ay", "eh", "er", "ey", "ih",
			"iy", "ow", "oy", "uh", "uw" };

	private static final String[] Onsets = { "p", "t", "k", "b", "d", "g", "f", "v", "th", "dh", "s", "z",
			"sh", "ch", "jh", "m", "n", "r", "l", "hh", "w", "y", "p r", "t r",
			"k r", "b r", "d r", "g r", "f r", "th r", "sh r", "p l", "k l", "b l",
			"g l", "f l", "s l", "t w", "k w", "d w", "s w", "s p", "s t", "s k",
			"s f", "s m", "s n", "g w", "sh w", "s p r", "s p l", "s t r", "s k r",
			"s k w", "s k l", "th w", "zh", "p y", "k y", "b y", "f y", "hh y",
			"v y", "th y", "m y", "s p y", "s k y", "g y", "hh w", "" };

	private static final String[] Digits = { "z-ih-r-ow", "w-ah-n", "t-uw", "th-r-iy", "f-ao-r", "f-ay-v",
			"s-ih-k-s", "s-eh1-v-ax-n", "ey-t", "n-ih-n" }; // ?

	public static boolean contains(Object[] s, String t) {
		return Arrays.asList(s).contains(t);
	}

	public static boolean contains(Object[] s, char c) {
		return contains(s, Character.toString(c));
	}

	public static boolean contains(String s, String t) {
		return s.contains(t);
	}

	public static boolean contains(String s, char c) {
		return contains(s, Character.toString(c));
	}

	public static void main(String[] args) {
		System.out.println(syllablesFromPhones(new String[] { }));
	}
}
