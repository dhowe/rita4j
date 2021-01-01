package rita;

import java.util.*;

public class Concorder {

	protected String[] words, wordsToIgnore;
	protected Map<String, Map<String, Object>> model;
	protected boolean ignoreCase, ignoreStopWords, ignorePunctuation;

	public Concorder() {
		model = new HashMap<String, Map<String, Object>>();
	}

	public Map<String, Integer> concordance(String text, Map<String, Object> opts) {
		return this.concordance(RiTa.tokenize(text), opts);
	}

	public Map<String, Integer> concordance(String[] words, Map<String, Object> opts) {

		this.words = words;
		this.ignoreCase = Util.boolOpt("ignoreCase", opts);
		this.ignoreStopWords = Util.boolOpt("ignoreStopWords", opts);
		this.ignorePunctuation = Util.boolOpt("ignorePunctuation", opts);
		this.wordsToIgnore = Util.strsOpt("wordsToIgnore", opts);

		buildModel();

		Map<String, Integer> result = new HashMap<String, Integer>();
		for (Map.Entry<String, Map<String, Object>> entry : model.entrySet()) {
			Map<String, Object> data = (Map<String, Object>) model.get(entry.getKey());
			result.put(entry.getKey(), ((int[]) data.get("indexes")).length);
		}

		return result; // TODO: sort by value here?
	}

	public String[] kwic(String word) {
		return this.kwic(word, 6);
	}

	public String[] kwic(String word, Map<String, Object> opts) {

		String text = Util.strOpt("text", opts);
		if (text != null) concordance(text, opts);
		String[] words = Util.strsOpt("words", opts);
		if (words != null) concordance(words, opts);
		return this.kwic(word, Util.intOpt("numWords", opts, -1));
	}

	public String[] kwic(String word, int numWords) {

		if (this.model.size() == 0) {
			throw new RiTaException("Call concordance() first");
		}

		if (numWords == -1) {
			numWords = 6;
		}

		List<String> result = new ArrayList<String>();
		Map<String, Object> data = this.lookup(word);
		if (data != null) {
			int[] idxs = (int[]) data.get("indexes");
			for (int i = 0; i < idxs.length; i++) {
				String[] sub = slice(this.words, Math.max(0, idxs[i] - numWords),
						Math.min(this.words.length, idxs[i] + numWords + 1));
				if (i < 1 || (idxs[i] - idxs[i - 1]) > numWords) {
					result.add(RiTa.untokenize(sub));
				}
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	///////////////////////////////////////////////////////////////////////////

	private void buildModel() {

		if (words == null) throw new RiTaException("No text in model");

		model = new HashMap<String, Map<String, Object>>();
		for (int j = 0; j < words.length; j++) {

			if (!isIgnorable(words[j])) {

				Map<String, Object> data = lookup(words[j]);

				if (data == null) {

					String key = compareKey(words[j]);
					model.put(key, RiTa.opts("key", key, "word", words[j], "indexes", new int[] { j }));
				}
				else {
					data.put("indexes", arrayExtend((int[]) data.get("indexes"), j));
					model.put((String) data.get("key"), data);
				}
			}
		}
	}

	private boolean isIgnorable(String key) {
		
		if (this.ignorePunctuation && RiTa.isPunctuation(key)) {
			return true;
		}
		
		if (this.ignoreStopWords && RiTa.isStopWord(key)) {
			return true;
		}
		
		if (this.wordsToIgnore != null) {
			for (int i = 0; i < this.wordsToIgnore.length; i++) {
				String word = this.wordsToIgnore[i];
				if (key.equals(word) || (this.ignoreCase && key.equalsIgnoreCase(word))) {
					return true;
				}
			}
		}
		
		return false;
	}

	private String compareKey(String word) {
		return ignoreCase ? word.toLowerCase() : word;
	}

	private Map<String, Object> lookup(String word) {
		return (Map<String, Object>) model.get(compareKey(word));
	}

	// helpers ////////////////////////////////////////////////////////////////

	private static String[] slice(String[] arr, int start, int end) {
		String[] slice = new String[end - start];
		for (int i = 0; i < slice.length; i++) {
			slice[i] = arr[start + i];
		}
		return slice;
	}

	private static int[] arrayExtend(int[] in, int toAdd) {
		int[] out = new int[in.length + 1];
		for (int i = 0; i < in.length; i++) {
			out[i] = in[i];
		}
		out[in.length] = toAdd;
		return out;
	}

	private static String[] arrayConcat(String[] a, String[] b) { // not used
		String[] out = new String[a.length + b.length];
		System.arraycopy(a, 0, out, 0, a.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;
	}

}
