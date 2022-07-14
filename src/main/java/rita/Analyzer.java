package rita;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Analyzer {

	public static final String SLASH = "/";
	public static final String DELIM = "-";
	public static final String SP = " ";
	public static final String E = "";
	private Map<String, Map<String, String>> cache = new HashMap<String, Map<String, String>>();

	protected LetterToSound lts;

	public Map<String, String> analyze(String text) {
		return this.analyze(text, null);
	}

	public Map<String, String> analyze(String text, Map<String, Object> opts) {
		String[] words = Tokenizer.tokenize(text);
		String[] tags = RiTa.tagger.tag(text, opts); // don't fail if no lexicon
		Map<String, String> features = new HashMap<String, String>() {
			{
				put("phones", E);
				put("stresses", E);
				put("syllables", E);
				put("pos", String.join(SP, tags));
				put("tokens", String.join(SP, words));
			}
		};

		String phones = "", stresses = "", syllables = "";
		for (int i = 0; i < words.length; i++) {
			Map<String, String> data = this.analyzeWord(words[i], opts);
			//{ phones, stresses, syllables }
			phones += SP + data.get("phones");
			stresses += SP + data.get("stresses");
			syllables += SP + data.get("syllables");
		}

		
		features.put("phones", phones.trim());
		features.put("stresses", stresses.trim());
		features.put("syllables", syllables.trim());

		return features;
	}

	public Map<String, String> analyzeWord(String word) {
		return this.analyzeWord(word, null);
	}

	public Map<String, String> analyzeWord(String word, Map<String, Object> opts) {

		Map<String, String> result = null;
		if (RiTa.CACHING && this.cache.containsKey(word)) result = this.cache.get(word);
		if (result != null) return result;

		Lexicon lex = RiTa.lexicon();
		String slash = "/";
		String delim = "-";
		String phones = word;
		String syllables = word;
		String stresses = word;
		String rawPhones = RiTa.lexicon().rawPhones(word, true);

		String[] rawPhonesArr = new String[] {};
		if (rawPhones == null || rawPhones.length() < 1) rawPhonesArr = this._computeRawPhones(word, lex, opts);
		if (rawPhonesArr.length == 1) {
			rawPhones = rawPhonesArr[0];
		}

		if (rawPhones != null && rawPhones.length() > 0) {
			String sp = rawPhones.replaceAll("1", E).replaceAll(" ", delim) + SP;
			phones = sp.equals("dh ") ? "dh-ah " : sp;
			String ss = rawPhones.replaceAll(" ", slash).replaceAll("1", E) + SP;
			syllables = ss.equals("dh ") ? "dh-ah " : ss;
			stresses = this.phonesToStress(rawPhones);
		} else if (rawPhonesArr != null && rawPhonesArr.length > 0) {
			List<String> ps = new ArrayList<String>();
			List<String> syls = new ArrayList<String>();
			List<String> strs = new ArrayList<String>();
			for (String p : rawPhonesArr) {
				if (p == null) {
					ps.add(word);
					syls.add(word);
					strs.add(word);
					continue;
				};
				String sp = p.replaceAll("1", E).replaceAll(" ", delim);
				ps.add(sp.equals("dh ") ? "dh-ah " : sp);
				String ss = p.replaceAll(" ", slash).replaceAll("1", E);
				syls.add(ss.equals("dh ") ? "dh-ah " : ss);
				strs.add(this.phonesToStress(p));
			}
			phones = String.join("-", ps);
			syllables = String.join("/", syls);
			stresses = String.join("-", strs);
		}

		final String tp = phones.trim();
		final String tsyl = syllables.trim();
		final String tstr = stresses.trim();

		result = new HashMap<String, String>() {
			{
				put("phones", tp);
				put("syllables", tsyl);
				put("stresses", tstr);
			}
		};
		if (RiTa.CACHING) this.cache.put(word, result);
		
		return result;
	}

	public String[] computePhones(String word) {
		if (lts == null) lts = new LetterToSound();
		return lts.buildPhones(word);
	}
	
	private String[] _computeRawPhones(String word, Lexicon lex, Map<String, Object> opts) {
		return word.contains("-") ? this._computePhonesHyph(word, lex, opts)
				: new String[] { this._computePhonesWord(word, lex, opts) };
	}

	private String[] _computePhonesHyph(String word, Lexicon lex, Map<String, Object> opts) {
		List<String> rawPhones = new ArrayList<String>();
		String[] words = word.split("-");
		for (String w : words) {
			String part = this._computePhonesWord(w, lex, opts, true);
			if (part != null && part.length() > 0) rawPhones.add(part);
		}
		return rawPhones.toArray(new String[] {});
	}
	
	private String _computePhonesWord(String word, Lexicon lex, Map<String, Object> opts) {
		return this._computePhonesWord(word, lex, opts, false);
	}

	private String _computePhonesWord(String word, Lexicon lex, Map<String, Object> opts, boolean isPart) {
		String rawPhones = null;
		if (isPart) rawPhones = lex.rawPhones(word, true);
		if ((rawPhones == null || rawPhones.length() < 1) && word.endsWith("s")) {
			String sing = RiTa.singularize(word);
			rawPhones = lex.rawPhones(sing, true);
			if (rawPhones != null && rawPhones.length() > 0) rawPhones += "-z";
		}
		boolean silent = RiTa.SILENT || RiTa.SILENCE_LTS || Util.boolOpt("slient", opts);
		if (rawPhones == null || rawPhones.length() < 1) {
			String[] ltsPhones = this.computePhones(word);
			if (ltsPhones != null && ltsPhones.length > 0) {
				if (!silent && lex.size() > 0) {
					System.out.println("[RiTa] Used LTS-rules for '" + word + "'");
				}
				rawPhones = Util.syllabifyPhones(ltsPhones);
			}
		}
		return rawPhones;
	}

	public String phonesToStress(String phones) {
		String stresses = "", syls[] = phones.split(" ");
		for (int j = 0; j < syls.length; j++) {
			if (syls[j].length() == 0) continue;
			stresses += (syls[j].indexOf(RiTa.STRESS) > -1)
					? RiTa.STRESS
					: RiTa.NOSTRESS;
			if (j < syls.length - 1) stresses += SLASH;
		}
		return stresses;
	}

}
