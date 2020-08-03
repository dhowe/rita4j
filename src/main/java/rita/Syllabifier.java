package rita;

import java.util.*;

public class Syllabifier {

	// Takes a syllabification and turns it into a string of phonemes,
	// delimited with dashes, with spaces between syllables
	static String toPhones(List<List<List<String>>> syllables) {

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

	static String toPhones1(String[][][] syllables) {

		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < syllables.length; i++) {
			String[][] syl = syllables[i];
			String stress = syl[0][0];
			String[] onset = syl[1];
			String[] nucleus = syl[2];
			String[] coda = syl[3];
			if (stress != null && nucleus.length > 0) {
				nucleus[0] += stress;
			}
			ArrayList<String> data = new ArrayList<String>();
			data.addAll(Arrays.asList(onset));
			data.addAll(Arrays.asList(nucleus));
			data.addAll(Arrays.asList(coda));
			ret.add(String.join("-", data.toArray(new String[0])));
		}
		return String.join(" ", ret.toArray(new String[0]));
	}

	static String toPhones2(String[][] syllables) {

		ArrayList<String> ret = new ArrayList<String>();

		for (int i = 0; i < syllables.length; i++) {

			String[] syl = syllables[i];

			char stress = syl[0].charAt(0); // TODO?

			String onset = syl[1];
			String nucleus = syl[2];
			String coda = syl[3];

			if (stress != ' ' && nucleus.length() > 0) {
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

	static String fromPhones(String[] input) { // TODO:

		boolean dbug = true;

		// returned data structure
		//String[][] syllables = new String[input.length][2];
		List<List<List<String>>> syllables = new ArrayList<List<List<String>>>();
		//String[] internuclei = new String[0];
		List<String> internuclei = new ArrayList<String>();
		String[] sylls = input;

		if (sylls == null || sylls.length == 0) {
			return "";
		}

		for (int i = 0; i < sylls.length; i++) {

			String phoneme = sylls[i].trim(), stress = "";
			if (phoneme.length() == 0) continue;

			String last = phoneme.substring(phoneme.length() - 1);
			if (isNum(last)) {
				stress = last;
				phoneme = phoneme.substring(0, phoneme.length() - 1);
			}

			if (dbug) System.out.println(i + ")" + phoneme + " stress="
					+ stress + " inter=" + String.join(":", internuclei));

			if (Syllabifier.isVowel(phoneme)) {

				// Split the consonants seen since the last nucleus into coda and onset.
				String[] coda = null, onset = null;

				// Make the largest onset we can. The 'split' variable marks the break point.
				for (int split = 0; split < internuclei.size() + 1; split++) {

					coda = Util.slice(internuclei, 0, split).toArray(new String[0]);
					onset = Util.slice(internuclei, split, internuclei.size()).toArray(new String[0]);

					if (dbug) System.out.println("  " + split + ") onset=" + String.join(":", onset) +
							"  coda=" + String.join(":", coda) + "  inter=" + String.join(":", internuclei));

					// If we are looking at a valid onset, or if we're at the start of the word
					// (in which case an invalid onset is better than a coda that doesn't follow
					// a nucleus), or if we've gone through all of the onsets and we didn't find
					// any that are valid, then split the nonvowels we've seen at this location.
					boolean isOnset = Syllabifier.isOnset(String.join(" ", onset));
					if (isOnset || syllables.size() == 0 || onset.length == 0) {
						if (dbug) System.out.println("  break " + phoneme);
						break;
					}
				}

				// Tack the coda onto the coda of the last syllable.
				// Can't do it if this is the first syllable.
				if (syllables.size() > 0) {
					syllables.get(syllables.size() - 1).get(3).addAll(Arrays.asList(coda));
					if (dbug) {
						List<String> l = syllables.get(syllables.size() - 1).get(3);
						System.out.println("  tack: " + coda + " -> len=" + l.size() + " [" + l.get(3) + "]");
					}
				}

				// Make a new syllable out of the onset and nucleus.
				List<List<String>> toPush = new ArrayList<List<String>>();
				toPush.get(0).add(stress);
				toPush.get(1).addAll(Arrays.asList(onset));
				toPush.get(2).add(phoneme);

				syllables.add(toPush);

				// At this point we"ve processed the internuclei list.
				internuclei = new ArrayList<String>();
			}
			else if (!(Syllabifier.isConsonant(phoneme)) && !phoneme.equals(" ")) {
				throw new RuntimeException("Invalid phoneme: " + phoneme);
			}
			else { // a consonant
				internuclei.add(phoneme);
			}
		}

		// Done looping through phonemes. We may have consonants left at the end.
		// We may have even not found a nucleus.
		if (internuclei.size() > 0) {
			if (syllables.size() == 0) {
				List<List<String>> toPush = new ArrayList<List<String>>();
				toPush.get(0).add(null);
				toPush.get(1).addAll(internuclei);
			}
			else {
				syllables.get(syllables.size() - 1).get(3).addAll(internuclei);
			}
		}

		return toPhones(syllables);
	}

	private static boolean isVowel(String phone) {
		String[] vowels = Phones.get("vowels");
		return Arrays.asList(vowels).contains(phone);
	}

	private static boolean isConsonant(String phone) {
		String[] cons = Phones.get("consonants");
		return Arrays.asList(cons).contains(phone);
	}

	private static boolean isOnset(String phone) {
		String[] onsets = Phones.get("onsets");
		return Arrays.asList(onsets).contains(phone);
	}

	private static boolean isNum(String strNum) {
		if (strNum == null) return false;
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
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
		System.out.println(Syllabifier.fromPhones(new String[] { }));
	}

}
