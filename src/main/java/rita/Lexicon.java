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

	    word = word.includes(" ") ? word.substring(0, word.indexOf(" ")) : word;

	    if (RiTa.VOWELS.includes(word.charAt(0))) return [];

	    // let matchMinLength = opts && opts.matchMinLength || 4;
	    // let useLTS = opts && opts.useLTS || false;

	    let results = [];
	    let words = Object.keys(this.dict);
	    let fss = this._firstStressedSyl(word, useLTS);
	    let c1 = this._firstPhone(fss);

	    if (!c1 || !c1.length) return [];

	    for (let i = 0; i < words.length; i++) {

	      if (words[i].length < matchMinLength) continue;

	      let c2 = this._firstPhone(this._firstStressedSyl(words[i], useLTS));

	      if (RiTa.VOWELS.includes(word.charAt(0))) return []; // ????

	      if (c1 === c2) results.push(words[i]);
	    }

	    return Util.shuffle(results, RiTa);
  }

  public boolean hasWord(String word)
  {
    return false;
  }

  public boolean isAlliteration(String word1, String word2)
  {

    return false;
  }

  public boolean isRhyme(String word1, String word2)
  {

    return false;
  }

  public String randomWord(String pos, int numSyllabes)
  {
    return null;
  }

  public String[] rhymes(String word)
  {
    return null;
  }

  public String[] similarBy(String word, Map<String, Object> opts)
  {
    return null;
  }

  public String[] words(Pattern regex)
  {
    return regex != null ? this.dict.keySet().stream().filter
        (word -> regex.matcher(word).matches()).toArray(String[]::new) :
        this.dict.keySet().toArray(new String[0]);
  }
  
  public static void main(String[] args) throws Exception
  {
    System.out.println(new Lexicon(RiTa.DICT_PATH).words(null).length);
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
