package rita;

import java.util.HashMap;
import java.util.Map;

public class Concorder {
	private boolean ignoreCase;
	private boolean ignoreStopWords;
	private boolean ignorePunctuation;
	private String[] wordsToIgnore;

	String[] words;
	Map<String, Object> model = new HashMap<String, Object>();
	//	Map<String, Object> _lookup = new HashMap<String, Object>();

	public Map<String, String> concordance(String text, String word, Map<String, Object> opts) {
		_parseOptions(opts);
		words = RiTa.tokenize(text);
		_build();
		Map<String, String> result = new HashMap<String, String>();
		if (word != null && word instanceof String) {
			Map<String, Object> data = (Map) model.get(word);
			if (data == null) {
				result.put(word, "0");
			}
			else {
				int[] indexes = (int[]) data.get("indexes");
				result.put(word, String.valueOf(indexes.length));
			}
			return result;
		}
		else {
			for (Map.Entry<String, Object> entry : model.entrySet()) {
				Map<String, Object> data = (Map) model.get(entry.getKey());
				int[] indexes = (int[]) data.get("indexes");
				result.put(entry.getKey(), String.valueOf(indexes.length));
			}
			return result;
		}
		/*
		_parseOptions(options);
		
		words = Array.isArray(text) ? text : RiTa.tokenize(text);
		_build();
		
		String[] result;
		for (let name in this.model) {
		  result[name] = this.model[name].indexes.length;
		}
		
		// TODO: need to sort by value here
		return result;
		 */
	}

	public String[] kwic(String text, String word) {
		return null;
		/*
		if (model.size() == 0) throw new RiTaException("Call concordance() first");
		String value = _lookup(text);
		ArrayList<String> result = new ArrayList<String>();
		if (value != null) {
		  int[] idxs = value.indexes; //what is .indexes
		  for (int i = 0; i < idxs.length; i++) {
			  String[] sub = Arrays.copyOfRange(words, Math.max(0, idxs[i] - word), Math.min(words.length, idxs[i] + numWords + 1));
		
		    if (i < 1 || (idxs[i] - idxs[i - 1]) > word) {
		      result.add(RiTa.untokenize(sub));
		    }
		  }
		}
		
		return (String[]) result.toArray();
		 */
	}

	///////////////////////////////////////////////////////////////////////////

	private void _parseOptions(Map<String, Object> options) { 
		if (options != null && options.size() > 0) {
			if (options.containsKey("ignoreCase")) {
				ignoreCase = (boolean) options.get("ignoreCase");
			} else {
				ignoreCase = false;
			}
			if (options.containsKey("ignoreStopWords")) {
				ignoreStopWords = (boolean) options.get("ignoreStopWords");
			} else {
				ignoreStopWords = false;
			}
			if (options.containsKey("ignorePunctuation")) {
				ignorePunctuation = (boolean) options.get("ignorePunctuation");
			} else {
				ignorePunctuation = false;
			}
			if (options.containsKey("wordsToIgnore")) {
				String[] got = (String[]) options.get("wordsToIgnore");
				wordsToIgnore = _concatStringArray(got, RiTa.STOP_WORDS);
			} else {
				wordsToIgnore = null;
			}
			if (ignoreStopWords && wordsToIgnore == null) {
				wordsToIgnore = RiTa.STOP_WORDS;
			}
		}

		if (options == null) {
			ignoreCase = false;
			ignoreStopWords = false;
			ignorePunctuation = false;
			wordsToIgnore = null;
		}
	}

	private void _build() {

		if (words == null) throw new RiTaException("No text in model"); //TODO is it correct?

		model = new HashMap<String, Object>();
		for (int j = 0; j < words.length; j++) {

			String word = words[j];
			if (_isIgnorable(word)) continue;
			Object _lookup = _lookup(word);

			// The typeof check below fixes a strange bug in Firefox: #XYZ
			// where the string 'watch' comes back from _lookup as a function
			// TODO: resolve in a better way
			if (_lookup == null || !(_lookup instanceof Object)) {

				Map<String, Object> lookupMap = new HashMap<String, Object>();
				lookupMap.put("word", word);
				lookupMap.put("key", _compareKey(word));
				lookupMap.put("indexes", new int[] { j });// put first index

				model.put((String) lookupMap.get("key"), lookupMap);

				//	      _lookup.push(j); // TODO
			}
			else {
				Map<String, Object> copyOfLookup = (Map) _lookup;
				int[] newIndexes = _pushIntArray((int[]) copyOfLookup.get("indexes"), j);
				copyOfLookup.put("indexes", newIndexes);
				model.put((String) copyOfLookup.get("key"), copyOfLookup);
			}
		}
	}

	private boolean _isIgnorable(String key) {

		if (ignorePunctuation && RiTa.isPunctuation(key)) return true;

		if (wordsToIgnore != null) {
			for (int i = 0; i < wordsToIgnore.length; i++) {
				String word = wordsToIgnore[i];
				if ((ignoreCase && key.toUpperCase().equals(word.toUpperCase())) || key.equals(word))
					return true;
			}
		}
		return false;
	}

	public String _compareKey(String word) {
		return ignoreCase ? word.toLowerCase() : word;
	}

	public Object _lookup(String word) {
		String key = _compareKey(word);
		return (Object) model.get(key);
	}

	public int[] _pushIntArray(int[] in, int toAdd) {
		int[] out = new int[in.length + 1];
		for (int i = 0; i < in.length; i++) {
			out[i] = in[i];
		}
		out[in.length] = toAdd;
		return out;
	}

	public String[] _concatStringArray(String[] a, String[] b) {
		String[] out = new String[a.length + b.length];
		System.arraycopy(a, 0, out, 0, a.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;
	}

}
