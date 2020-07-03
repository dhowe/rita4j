package rita;

import java.util.HashMap;
import java.util.Map;

public class Analyzer
{
	public Map<String,String> analyze(String text)
	{
		String[] stressyls;
		String[] ltsPhones;
		String phones;
		boolean useRaw;
		String phonemes = " ",
				syllables = " ",
				stresses = " ",
				slash = "/",
				delim = "-";

		Map<String, String> features = new HashMap<String, String>();

		String[] words = RiTa.tokenize(text);
		String[] tags = RiTa.pos(text);

		features.put("tokens", String.join(" " , words));
		features.put("pos", String.join(" " , tags));

		for (int i = 0 , l = words.length; i < l; i++) {

			useRaw = false;
			phones = RiTa._lexicon()._rawPhones(words[i], false);

			if (phones.length() == 0) {

				ltsPhones = RiTa.lts.getPhones(words[i]);
				if (ltsPhones != null && ltsPhones.length > 0) {

					if (!RiTa.SILENT && !RiTa.SILENCE_LTS && words[i].matches("/[a-zA-Z]+/)")) {
						System.out.println("[RiTa] Used LTS-rules for '" + words[i] + "'");
					}

					phones = Syllabifier.fromPhones(ltsPhones);

				} else {
					//phones = words[i];
					phones = words[i];
					useRaw = true;
				}
			}

			phonemes += (phones.replace("/[0-2]/g", "")).replace("/ /g", delim) + " ";
			syllables += (phones.replace("/ /g", slash)).replace("/1/g", "") + " ";

			if (!useRaw) {
				stressyls = phones.split(" ");
				for (int j = 0; j < stressyls.length; j++) {

					if (stressyls[j].length() ==0 || stressyls[j] == null) continue;

					stresses += (stressyls[j].indexOf(RiTa.STRESSED) > -1) ?
							RiTa.STRESSED : RiTa.UNSTRESSED;

					if (j < stressyls.length - 1) stresses += slash;
				}
			} else {

				stresses += words[i];
			}

			if (!stresses.endsWith(" ")) stresses += " ";
		}

		features.put("stresses", stresses.trim());
		features.put("phonemes", phonemes.trim());//.replace(/\\s+/, ' '); // needed?
		features.put("syllables", syllables.trim());//.replace(/\\s+/, ' '); // needed?

		System.out.print("analysis features: ");
		System.out.print(features);
		return features;
	}

}
