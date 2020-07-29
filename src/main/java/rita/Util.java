package rita;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Util {
	public static final String[] MODALS = { "asbestos", "barracks", "bathos", "breeches", "beef", "britches", "chaos",
			"cognoscenti", "clippers", "corps", "cosmos", "crossroads", "diabetes", "ethos", "gallows",
			/* "gas", */ "graffiti", "herpes", "innings", "lens", "means", "measles", "mews", "mumps", "news", "pathos",
			"pincers", "pliers", "proceedings", "rabies", "rhinoceros", "sassafras", "scissors", "series", "shears",
			"species", "tuna", "acoustics", "aesthetics", "aquatics", "basics", "ceramics", "classics", "cosmetics",
			"dialectics", "deer", "dynamics", "ethics", "harmonics", /* "happiness", "hardness", "shortness", */ "heroics",
			"mechanics", "metrics", "ooze", "optics", /* "people", */ "physics", "polemics", "pyrotechnics", "quadratics",
			/* "quarters", */ "salespeople", "statistics", "tactics", "tropics", "bengalese", "bengali", "bonsai", "booze",
			"cellulose", "mess", "moose", "burmese", "chinese", "colossus", "congolese", "discus", "electrolysis", "emphasis",
			"expertise", "flu", "fructose", "gauze", "glucose", "grease", "guyanese", "haze", "incense", "japanese",
			"lebanese", "malaise", "mayonnaise", "maltese", "music", "money", "menopause", "merchandise", "olympics",
			"overuse", "paradise", "poise", "polymerase", "portuguese", "prose", "recompense", "remorse", "repose",
			"senegalese", "siamese", "singhalese", "innings", "sleaze", "sioux", "sudanese", "suspense", "swiss", "taiwanese",
			"vietnamese", "unease", "aircraft", "anise", "antifreeze", "applause", "archdiocese", "apparatus", "asparagus",
			"barracks", "bellows", "bison", "bluefish", "bourgeois", "bream", "brill", "butterfingers", "cargo", "carp",
			"catfish", "chassis", "clothes", "chub", "cod", "codfish", "coley", "contretemps", "corps", "crawfish",
			"crayfish", "crossroads", "cuttlefish", "deer", "dice", "dogfish", "doings", "dory", "downstairs", "eldest",
			"earnings", "economics", "electronics", "firstborn", "fish", "flatfish", "flounder", "fowl", "fry", "fries",
			"works", "goldfish", "golf", "grand", "grief", "haddock", "hake", "halibut", "headquarters", "herring", "hertz",
			"horsepower", "goods", "hovercraft", "ironworks", "kilohertz", "ling", "shrimp", "swine", "lungfish", "mackerel",
			"macaroni", "means", "megahertz", "moorfowl", "moorgame", "mullet", "nepalese", "offspring", "pants", "patois",
			"pekinese", "perch", "pickerel", "pike", "potpourri", "precis", "quid", "rand", "rendezvous", "roach", "salmon",
			"samurai", "series", "seychelles", "shad", "sheep", "shellfish", "smelt", "spaghetti", "spacecraft", "species",
			"starfish", "stockfish", "sunfish", "superficies", "sweepstakes", "smallpox", "swordfish", "tennis", "tobacco",
			"triceps", "trout", "tuna", "tunafish", "turbot", "trousers", "turf", "dibs", "undersigned", "waterfowl",
			"waterworks", "waxworks", "wildfowl", "woodworm", "yen", "aries", "pisces", "forceps", "jeans", "mathematics",
			"news", "odds", "politics", "remains", "goods", "aids", "wildlife", "shall", "would", "may", "might", "ought",
			"should", "wildlife"
	};

	public static boolean isNode() {
		return false;
	}

	//public Object/*String*/ invokeStatic(Class target, Method method, Object[] args) throws Throwable {

	public static Object/*String*/ invokeStatic(Method method, Object... args) {
		return invoke(null, method, args);
	}

	public static Object/*String*/ invoke(Object target, Method method, Object... args) {
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

	public static Field getProperty(Object o, String prop) {
		Field[] fields = o.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(prop)) {
				return fields[i];
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Method getStatic(Class c, String name, Class... params) {
		try {
			return c.getDeclaredMethod(name, params);
		} catch (Exception e) {
			return null;
		}
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

	public static int minEditDist(String[] source, String[] target) {

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
				matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + cost);
			}
		}

		// Step 6 ----------------------------------------------
		return matrix[source.length][target.length];
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

}
