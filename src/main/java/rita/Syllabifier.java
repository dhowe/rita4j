package rita;

import java.util.*;

public class Syllabifier {

	// Takes a syllabification and turns it into a string of phonemes,
	// delimited with dashes, with spaces between syllables
	static String toPhones(String[][] syllables) {

		ArrayList<String> ret = new ArrayList<String>();

		for (int i = 0; i < syllables.length; i++) {

			String[] syl = syllables[i];
			char stress = syl[0].charAt(0); // TODO
			String onset = syl[1];
			String nucleus = syl[2];
			String coda = syl[3];

			if (stress != ' ' && nucleus.length() > 0) {// dch
				nucleus = stress + nucleus;
			}

			ArrayList<String> data = new ArrayList<String>();

			for (int j = 0; j < onset.length(); j++) {
				data.add(Character.toString(onset.charAt(j)));
			}
			for (int j = 0; j < nucleus.length(); j++) {
				data.add(Character.toString(nucleus.charAt(j)));
			}
			for (int j = 0; j < coda.length(); j++) {
				data.add(Character.toString(coda.charAt(j)));
			}

			ret.add(String.join("-", data));
		}

		return String.join(" ", ret);
	}

	static String fromPhones(String[] ltsPhones) { // TODO:
		
		throw new RuntimeException("TODO: PORT ME");
	}

	private static Map<String, String[]> Phones;
	
	static {
		Phones = new HashMap<String, String[]>();
		Phones.put("consonants", new String[] { "b", "ch", "d", "dh", "f", "g", "hh", "jh", "k", "l", "m",
				"n", "ng", "p", "r", "s", "sh", "t", "th", "v", "w", "y", "z", "zh" });
		Phones.put("vowels", new String[] { "aa", "ae", "ah", "ao", "aw", "ax", "ay", "eh", "er", "ey", "ih",
				"iy", "ow", "oy", "uh", "uw" });
		Phones.put("onsets", new String[] { "p", "t", "k", "b", "d", "g", "f", "v", "th", "dh", "s", "z",
				"sh", "ch", "jh", "m", "n", "r", "l", "hh", "w", "y", "p r", "t r",
				"k r", "b r", "d r", "g r", "f r", "th r", "sh r", "p l", "k l", "b l",
				"g l", "f l", "s l", "t w", "k w", "d w", "s w", "s p", "s t", "s k",
				"s f", "s m", "s n", "g w", "sh w", "s p r", "s p l", "s t r", "s k r",
				"s k w", "s k l", "th w", "zh", "p y", "k y", "b y", "f y", "hh y",
				"v y", "th y", "m y", "s p y", "s k y", "g y", "hh w", "" });
		Phones.put("digits", new String[] { "z-ih-r-ow", "w-ah-n", "t-uw", "th-r-iy", "f-ao-r", "f-ay-v",
				"s-ih-k-s", "s-eh1-v-ax-n", "ey-t", "n-ih-n" });
	}
	
	public static void main(String[] args) {
		System.out.println(Syllabifier.fromPhones(new String[] {}));
	}

}
