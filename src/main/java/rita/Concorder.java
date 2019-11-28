package rita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Concorder
{
	private boolean ignoreCase;
	private boolean ignoreStopWords;
	private boolean ignorePunctuation;
	private String[] wordsToIgnore;

	String[] words;
	Map<String, String> model = new HashMap<String, String>();
	Map<String, Object> _lookup = new HashMap<String, Object>();
	  
  public Map<String, String> concordance(String text, String word, Map<String, Object> opts)
  {
	  return null;

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

  public String[] kwic(String text, String word)
  {
	  //return null;
	  
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
	    
  }
  
  
  ///////////////////////////////////////////////////////////////////////////
  
  private void _parseOptions(Map<String, Object> options) { //TODO
    if (options.size() > 0) {
    	if(options.containsKey("ignoreCase")) ignoreCase = true;
    	if(options.containsKey("ignoreStopWords")) ignoreStopWords = true;
    	if(options.containsKey("ignorePunctuation")) ignorePunctuation = true;
    	if(options.containsKey("wordsToIgnore")) wordsToIgnore = (String[]) options.get("wordsToIgnore");
      }

      if (ignoreStopWords) {
        wordsToIgnore = wordsToIgnore.concat(RiTa.STOP_WORDS);
      }
  }
  
  private void _build() {
	  
	    if (words == null) throw new RiTaException("No text in model"); //TODO is it correct?

	    model = new HashMap<String,String>();
	    for (int j = 0; j < words.length; j++) {

	      String word = words[j];
	      if (_isIgnorable(word)) continue;
	      _lookup = _lookup(word);

	      // The typeof check below fixes a strange bug in Firefox: #XYZ
	      // where the string 'watch' comes back from _lookup as a function
	      // TODO: resolve in a better way
	    //  if (!_lookup || typeof _lookup !== 'object') {

	      Map<String,Object> lookupMap = new HashMap<String,Object>();
	      lookupMap.put("word",word);
	      lookupMap.put("key",_compareKey(word));
	      lookupMap.put("indexes", new String[] {});
	        _lookup = lookupMap;
	        
	        
	        model[lookupMap.get(key)] = _lookup; //TODO
	    //  }
	      _lookup.indexes.push(j);
	    }
	  }

	  private boolean _isIgnorable(String key) {

	    if (ignorePunctuation && RiTa.isPunctuation(key)) return true;

	    for (int i = 0; i < wordsToIgnore.length; i++) {
	      String word = wordsToIgnore[i];
	      if ((ignoreCase && key.toUpperCase() == word.toUpperCase()) || key == word)
	        return true;
	    }
	    return false;
	  }

	  public String _compareKey(String word) {
	    return ignoreCase ? word.toLowerCase() : word;
	  }

	  public String _lookup(String word) {
	    String key = _compareKey(word);
	    return model.get(key);
	  }

}
