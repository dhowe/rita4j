package rita;

import java.util.HashMap;
import java.util.Map;

public class Analyzer {

	public static final String SLASH = "/";
	public static final String DELIM = "-";

	public Map<String, String> analyze(String map) {
		return this.analyze(map, null);
	}

	public Map<String, String> analyze(String text, Map<String, Object> opts) {
		String[] words = Tokenizer.tokenize(text);
		String[] tags = Tagger.tag(text, opts); // don't fail if no lexicon

		String phones = "", stresses = "", syllables = "";
		for (int i = 0; i < words.length; i++) {
			String[] data = this.analyzeWord(words[i], opts);
			//{ phones, stresses, syllables }
			phones += data[0];
			stresses += data[1];
			syllables += data[2];
		}

		Map<String, String> features = new HashMap<>();
		features.put("phones", phones.trim());
		features.put("stresses", stresses.trim());
		features.put("syllables", syllables.trim());
		features.put("pos", String.join(" ", tags));
		features.put("tokens", String.join(" ", words));

		return features;
	}

	private String[] analyzeWord(String word, Map<String, Object> opts) {

		boolean useRaw = false;
		String rawPhones = RiTa._lexicon()._rawPhones(word, true);

		if (rawPhones.length() == 0) {

			String[] ltsPhones = RiTa.lts.computePhones(word);
			if (ltsPhones != null && ltsPhones.length > 0) {

				if (!RiTa.SILENT && !RiTa.SILENCE_LTS && word.matches("[a-zA-Z]+")) {
					System.out.println("[RiTa] Used LTS-rules for '" + word + "'");
				}
				rawPhones = Util.syllablesFromPhones(ltsPhones);
			}
			else {
				// phones = word;
				rawPhones = word;
				useRaw = true;
			}
		}

		String sp = rawPhones.replaceAll("[0-2]", "").replaceAll(" ", DELIM) + " ";
		String phones = sp.equals("dh ") ? "dh-ah " : sp; // special case
		String ss = rawPhones.replaceAll(" +", SLASH).replaceAll("1", "") + " ";
		String syllables = ss.equals("dh ") ? "dh-ah " : ss;
		String stresses = "";

		if (!useRaw) {
			String[] stressyls = rawPhones.split(" ");
			for (int j = 0; j < stressyls.length; j++) {
				if (/*stressyls[j] == null ||*/stressyls[j].length() == 0) continue;
				stresses += (stressyls[j].indexOf(RiTa.STRESSED) > -1)
						? RiTa.STRESSED
						: RiTa.UNSTRESSED;
				if (j < stressyls.length - 1) stresses += SLASH;
			}
		}
		else {

			stresses += word;
		}

		if (!stresses.endsWith(" ")) stresses += " ";

		return new String[] { phones, stresses, syllables };
	}

	public Map<String, String> analyzeX(String text) {

		Map<String, String> features = new HashMap<String, String>();
		String phones = "", syllables = "", stresses = "";

		String[] words = RiTa.tokenize(text), tags = RiTa.pos(text);

		features.put("tokens", String.join(" ", words));
		features.put("pos", String.join(" ", tags));

		for (int i = 0, l = words.length; i < l; i++) {

			boolean useRaw = false;
			String rawPhones = RiTa._lexicon()._rawPhones(words[i], true);

			if (rawPhones.length() == 0) {

				String[] ltsPhones = RiTa.lts.computePhones(words[i]);
				if (ltsPhones != null && ltsPhones.length > 0) {

					if (!RiTa.SILENT && !RiTa.SILENCE_LTS && words[i].matches("/[a-zA-Z]+/)")) {
						System.out.println("[RiTa] Used LTS-rules for '" + words[i] + "'");
					}

					rawPhones = Util.syllablesFromPhones(ltsPhones);

				}
				else {
					// phones = words[i];
					rawPhones = words[i];
					useRaw = true;
				}
			}

			phones += rawPhones.replaceAll("[0-2]", "").replaceAll(" ", DELIM) + " ";
			syllables += rawPhones.replaceAll(" +", SLASH).replaceAll("1", "") + " ";

			if (!useRaw) {
				String[] stressyls = rawPhones.split(" ");
				for (int j = 0; j < stressyls.length; j++) {

					if (stressyls[j].length() == 0 || stressyls[j] == null) continue;

					stresses += (stressyls[j].indexOf(RiTa.STRESSED) > -1) ? RiTa.STRESSED : RiTa.UNSTRESSED;

					if (j < stressyls.length - 1) stresses += SLASH;
				}
			}
			else {

				stresses += words[i];
			}

			if (!stresses.endsWith(" ")) stresses += " ";
		}

		features.put("phones", phones.trim());
		features.put("stresses", stresses.trim());
		features.put("syllables", syllables.trim());

		// System.out.print("analysis features: "+features);
		return features;
	}
	
	public static void main(String[] args) {
		System.out.println(Tagger.tag("dog"));
	}

}
