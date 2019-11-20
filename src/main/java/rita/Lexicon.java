package rita;

import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Lexicon // KW: Wait on this class please
{
  private static String LEXICON_DELIM = ":";
  private static int MAP_SIZE = 30000;

  protected Map<String, String[]> dict; // data

  public Lexicon(String filePath) throws Exception
  {
    List<String> lines = loadJSON(filePath);

    if (lines == null || lines.size() < 2) {
      throw new Exception("Problem parsing RiLexicon data files");
    }

    dict = new LinkedHashMap<String, String[]>(MAP_SIZE);

    for (int i = 1; i < lines.size() - 1; i++) // ignore JS prefix/suffix
    {
      String line = lines.get(i);
      String[] parts = line.split(LEXICON_DELIM);
      if (parts == null || parts.length != 2) {
        throw new Exception("Illegal entry: " + line);
      }
      dict.put(parts[0], parts[1].split(","));
    }
  }

  public static List<String> loadJSON(String file) throws Exception
  {
    if (file == null) {
      throw new Exception("No dictionary path specified!");
    }

    URL resource = RiTa.class.getResource(file);
    if (resource == null) {
      throw new Exception("Unable to load lexicon from: " + file);
    }
      
    final Path path = Paths.get(resource.toURI());
    final List<String> lines = Files.readAllLines(path);

    // clean out the JSON formatting (TODO: optimize)
    // String clean = data.replaceAll("['\\[\\]]", E).replaceAll(",", "|");

    return lines;
  }


  
  public String[] alliterations(String word, int minWordLength)
  {

	    word = word.contains(" ") ? word.substring(0, word.indexOf(" ")) : word;

	    if (RiTa.VOWELS.contains(String.valueOf(word.charAt(0)))) return new String[]{};

	   //  int matchMinLength = minWordLength || 4;
	   //  boolean useLTS = opts && opts.useLTS || false;
	    
	    boolean useLTS = false;

	    ArrayList<String> resultsArrayList;
	    String[] results = {};
	    String[] words = (String[]) dict.keySet().toArray();
	    String fss = _firstStressedSyl(word, useLTS);
	    String c1 = _firstPhone(fss);

	    if (c1 != null || c1.length() ==0 ) return new String[] {};

	    for (int i = 0; i < words.length; i++) {

	      if (words[i].length() < minWordLength) continue;

	      String c2 = _firstPhone(_firstStressedSyl(words[i], useLTS));

	      if (RiTa.VOWELS.contains(Character.toString(word.charAt(0)))) return new String[] {}; // ????

	      if (c1 == c2) 
	      {
	    	  resultsArrayList.add(words[i]);
	    	  
	      } 

	    }
	    results = (String[]) resultsArrayList.toArray();
	    return Util.shuffle(results, RiTa); //TODO
  }
  

  public boolean hasWord(String word)
  {
	  
	    word = word.length() > 0 ? word.toLowerCase() : "";
	    return dict.hasOwnProperty(word) || RiTa.pluralizer.isPlural(word);
  }

  public boolean isAlliteration(String word1, String word2)
  {
	  if ( word1 != null || word2 != null || word1.length() == 0 || word2.length() == 0) {
	      return false;
	    }

	    if (word1.indexOf(" ") > -1 || word2.indexOf(" ") > -1) {
	      throw new IllegalArgumentException("isAlliteration expects single words only");
	    }

	    String c1 = _firstPhone(this._firstStressedSyl(word1, useLTS));
	    String c2 = _firstPhone(this._firstStressedSyl(word2, useLTS));

	    if (_isVowel(Character.toString(c1.charAt(0))) || _isVowel(Character.toString(c2.charAt(0)))) {
	      return false;
	    }

	    return c1.length() > 0 && c2.length() > 0 && c1 == c2;
  }

  public boolean isRhyme(String word1, String word2)
  {

	  if (word1 == null || word2 == null || word1.toUpperCase() == word2.toUpperCase()) {
	      return false;
	    }

	    String phones1 = _rawPhones(word1, useLTS);
	    String phones2 = _rawPhones(word2, useLTS);

	    if (phones2 == phones1) return false;

	    String p1 = _lastStressedVowelPhonemeToEnd(word1, useLTS);
	    String  p2 = _lastStressedVowelPhonemeToEnd(word2, useLTS);

	    return p1.length() > 0 && p2.length() > 0 && p1 == p2;
  }

  public String randomWord(String pos, int numSyllabes)  //TODO one argument only in rita-script
  {
	  boolean pluralize = false;
	    String words = Object.keys(dict);
	    float ran = Math.floor(RiTa.random(words.length()));
	    let targetPos = opts && opts.pos;
	    int targetSyls = opts && opts.syllableCount || 0;

	    let isNNWithoutNNS = (w, pos) => (w.endsWith("ness") ||
	      w.endsWith("ism") || pos.indexOf("vbg") > 0);

	    if (targetPos && targetPos.length) {
	      targetPos = targetPos.trim().toLowerCase();
	      pluralize = (targetPos === "nns");
	      if (targetPos[0] === "n") targetPos = "nn";
	      else if (targetPos === "v") targetPos = "vb";
	      else if (targetPos === "r") targetPos = "rb";
	      else if (targetPos === "a") targetPos = "jj";
	    }

	    for (let i = 0; i < words.length; i++) {
	      let j = (ran + i) % words.length;
	      let rdata = this.dict[words[j]];

	      // match the syls if supplied
	      if (targetSyls && targetSyls !== rdata[0].split(' ').length) {
	        continue;
	      }

	      if (targetPos) { // match the pos if supplied
	        if (targetPos === rdata[1].split(' ')[0]) {

	          // match any pos but plural noun
	          if (!pluralize) return words[j];

	          // match plural noun
	          if (!isNNWithoutNNS(words[j], rdata[1])) {
	            return RiTa.pluralize(words[j]);
	          }
	        }
	      }
	      else {
	        return words[j]; // no pos to match
	      }
	    }

	    return []; // TODO: failed, should throw here
  }

  public String[] rhymes(String theWord) //TODO
  {
	  if (theWord == null || theWord.length() == 0) return new String[] {};

	    String word = theWord.toLowerCase();

	    String[] results;
	    String words = Object.keys(this.dict);
	    String p = _lastStressedPhoneToEnd(word);

	    for (int i = 0; i < words.length(); i++) {

	      if (words[i] == word) continue;

	      if (dict[words[i]][0].endsWith(p)) results.push(words[i]);
	    }

	    return results;
  }

  public String[] similarBy(String word, Map<String, Object> opts)  //TODO
  {
	    if (word != null || word.length() == 0 ) return new String[]{};

	    if(opts.size() == null) return new String[]{};
	    
	    if(opts.get("type") == null || opts.get("type") == "") {
	    	"letter";
	    }
	    opts.type = opts.type || "letter";

	    return (opts.type == "soundAndLetter") ?
	      this.similarBySoundAndLetter(word, opts)
	      : this.similarByType(word, opts);
  }

  public String[] words(Pattern regex)
  {
    return regex != null ? this.dict.keySet().stream().filter
        (word -> regex.matcher(word).matches()).toArray(String[]::new) :
        dict.keySet().toArray(new String[0]);
  }
  
  public static void main(String[] args) throws Exception
  {
    System.out.println(new Lexicon(RiTa.DICT_PATH).words(null).length);
  }
  
  //////////////////////////////////////////////////////////////////////

  private boolean _isVowel(String c) {

    return c != null && c.length() >0  && RiTa.VOWELS.contains(c);
  }

  private boolean _isConsonant(String p) {

    return (typeof p == S && p.length == 1 && RiTa.VOWELS.indexOf(p) < 0 && /^[a-z\u00C0-\u00ff]+$/.test(p)); // precompile
  }

  private String _firstPhone(String rawPhones) {

    if (rawPhones != null || rawPhones.length() == 0) return "";
    String[] phones = rawPhones.split(RiTa.PHONEME_BOUNDARY);
    if (phones != null) return phones[0];
    return ""; //return null?
    
  }
  

  private String _intersect() { // https://gist.github.com/lovasoa/3361645 //TODO
    String all, n, len;
    String[] ret;
    String[] obj = {},
    int shortest = 0,
    int nOthers = arguments.length - 1,
    int nShortest = arguments[0].length;
    for (int i = 0; i <= nOthers; i++) {
      n = arguments[i].length;
      if (n < nShortest) {
        shortest = i;
        nShortest = n;
      }
    }
    for (int i = 0; i <= nOthers; i++) {
      n = (i === shortest) ? 0 : (i || shortest);
      len = arguments[n].length;
      for (let j = 0; j < len; j++) {
        let elem = arguments[n][j];
        if (obj[elem] === i - 1) {
          if (i === nOthers) {
            ret.push(elem);
            obj[elem] = 0;
          } else {
            obj[elem] = i;
          }
        } else if (i === 0) {
          obj[elem] = 0;
        }
      }
    }
    return ret;
  }

  
  private String _lastStressedPhoneToEnd(String word, boolean useLTS) {

    if (word != null || word.length() == 0) return ""; // return null?

    int idx; 
    char c;
    String result;
    String raw = _rawPhones(word, useLTS);

    if (raw != null || raw.length() == 0) return ""; // return null?

    idx = raw.lastIndexOf(RiTa.STRESSED);

    if (idx < 0) return ""; // return null?

    c = raw.charAt(--idx);
    while (c != '-' && c != ' ') {
      if (--idx < 0) {
        return raw; // single-stressed syllable
      }
      c = raw.charAt(idx);
    }
    result = raw.substring(idx + 1);

    return result;
  }


  private String _lastStressedVowelPhonemeToEnd(String word, boolean useLTS) {

    if (word != null || word.length() == 0) return ""; // return null?

    String raw = _lastStressedPhoneToEnd(word, useLTS);
    if (raw != null || raw.length() == 0) return ""; // return null?

    String[] syllables = raw.split(" ");
    String lastSyllable = syllables[syllables.length - 1];
    lastSyllable = lastSyllable.replace("[^a-z-1 ]", "");

    int idx = -1;
    for (int i = 0; i < lastSyllable.length(); i++) {
      char c = lastSyllable.charAt(i);
      if (RiTa.VOWELS.contains(Character.toString(c))) {
        idx = i;
        break;
      }
    }

    return lastSyllable.substring(idx);
  }

  private String _firstStressedSyl(String word, boolean useLTS) {

	 String raw = _rawPhones(word, useLTS);

    if (raw == ""|| raw == null) return ""; // return null?

    int idx = raw.indexOf(RiTa.STRESSED);

    if (idx < 0) return ""; // no stresses... return null?

    char c = raw.charAt(--idx);

    while (c != ' ') {
      if (--idx < 0) {
        // single-stressed syllable
        idx = 0;
        break;
      }
      c = raw.charAt(idx);
    }

    String firstToEnd = idx == 0 ? raw : raw.substring(idx).trim();
    idx = firstToEnd.indexOf(' ');

    return idx < 0 ? firstToEnd : firstToEnd.substring(0, idx);
  }

  private String _posData(String word) {

    String[] rdata = _lookupRaw(word);
    return (rdata != null && rdata.length == 2) ? rdata[1] : "";
  }

  private String[] _posArr(String word) {

    String pl = _posData(word);
    if (pl != null || pl.length() > 0) return new String[] {};
    return pl.split(" ");
  }

  private String _bestPos(String word) {

    String[] pl = _posArr(word);
    return (pl.length > 0) ? pl[0] : "";
  }


  
   private String[] _lookupRaw(String word) {
	   //word = word && word.toLowerCase();
	   String[] a = null;
	    word = word.toLowerCase();
	    
	    if (dict != null) {
	    	return dict.get(word);
	    }else {
	    	return a; //TODO is it correct to return null?
	    }
  }
  
   String _rawPhones(String word, boolean b) {//, forceLTS) {

	    // TODO: remove all useLTS vars ?

	    String[] phones = null; 
	    String result = ""; 
	    String[] rdata = _lookupRaw(word);
	    //useLTS = useLTS || false;

	    if (rdata != null) result = rdata.length == 2 ? rdata[0] : "";

	    if (rdata == null) { //|| forceLTS) { // ??
	    	if(RiTa.lts != null) {
	  	      phones = RiTa.lts.getPhones(word);
	    	}
	      if (phones != null && phones.length > 0) {
	        result = RiTa.syllabifier.fromPhones(phones);
	      }
	    }

	    return result;
  }



}
