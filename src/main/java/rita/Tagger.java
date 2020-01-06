package rita;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tagger
{

	public static final String[] ADJ = {"jj", "jjr", "jjs"};
	public static final String[] ADV = {"rb", "rbr", "rbs", "rp"};
	public static final String[] NOUNS = {"nn", "nns", "nnp", "nnps"};
	public static final String[] VERBS = {"vb", "vbd", "vbg", "vbn", "vbp", "vbz"};
	
	static String[] MODALS = Util.MODALS;


	public static boolean isAdjective(String word)
	{
		return checkType(word, ADJ);
	}

	public static boolean isAdverb(String word)
	{

		return checkType(word, ADV);
	}

	public static boolean isNoun(String word)
	{
	    boolean result = checkType(word, NOUNS);
	    if (!result) {
	      String singular = RiTa.singularize(word);
	      if (singular != word) {
	        result = checkType(singular, NOUNS);
	      }
	    }
	    return result;
	}

	public static boolean isVerb(String word)
	{
		return checkType(word, VERBS);
	}

	public static boolean isVerbTag(String tag)
	{
		return Arrays.asList(VERBS).contains(tag);
	}
	public static boolean isNounTag(String tag) {
		return Arrays.asList(NOUNS).contains(tag);
	}

	public static boolean isAdverbTag(String tag) {
		return Arrays.asList(ADV).contains(tag);
	}

	public static boolean isAdjTag(String tag) {
		return Arrays.asList(ADJ).contains(tag);
	}

	public static String tagInline(String words, boolean useSimpleTags)
	{
		/*
		  if (words == null || words.length() == 0 ) return "";

		    if (words.length() != tags.length) throw new RiTaException("Tagger: invalid state");

		    delimiter = delimiter || '/';

		    String sb = "";
		    for (int i = 0; i < words.length(); i++) {

		      sb += words[i];
		      if (!RiTa.isPunctuation(words[i])) {
		        sb += delimiter + tags[i];
		      }
		      sb += ' ';
		    }

		    return sb.trim();
		    
		    */
		
		return null;
	}

	public static String[] tag(String words, boolean useSimpleTags)
	{

	    if (words == null || words.length() == 0) return new String[] {};

	    Lexicon lexicon = RiTa._lexicon();
	    ArrayList<String> result = new ArrayList<String>();
	    ArrayList<String> choices2d = new ArrayList<String>();;

	    
	    if (words == "") return new String[] {};
	    String[] wordsArr = Tokenizer.tokenize(words);


	    for (int i = 0; i < wordsArr.length; i++) {

	      if (wordsArr[i].length() < 1) {

	        result.add("");
	        continue;
	      }

	      if (wordsArr[i].length() == 1) {

	        result.add(_handleSingleLetter(wordsArr[i]));
	        continue;
	      }

	      String[] data = lexicon._posArr(wordsArr[i]); // fail if no lexicon
	      
		  System.out.println("data : " + Arrays.toString(data));
		  
	      if (data.length == 0) {

	        // use stemmer categories if no lexicon

	        //choices2d.add("");
	        String tag = "nn";
	        if (wordsArr[i].endsWith("s")) {
	          tag = "nns";
	        }

	        if (!RiTa.SILENT) { // warn // TODO 
	          if (RiTa.LEX_WARN) { // lex.size() <= 1000 lex is never defined
	        	  Logger logger = Logger.getLogger( Tagger.class.getName()); 
					logger.warning(Boolean.toString(RiTa.LEX_WARN));
					RiTa.LEX_WARN = false; // only once
	          }
	          /*//TODO
	          if (RiTa.LTS_WARN && LetterToSound == "undefined") {
	        	  Logger logger = Logger.getLogger( Tagger.class.getName()); 
					logger.warning(Boolean.toString(RiTa.LTS_WARN));
					RiTa.LTS_WARN = false; // only once
	          }
	          */
	        }

	        if (wordsArr[i].endsWith("s")) {
	          String sub2 = "";
	          String sub = wordsArr[i].substring(0, wordsArr[i].length() - 1);

	          if (wordsArr[i].endsWith("es"))
	            sub2 = wordsArr[i].substring(0, wordsArr[i].length() - 2);

	          if (_lexHas("n", sub) || (sub2.length() > 0 && _lexHas("n", sub2))) {
	            choices2d.add("nns");
	          } else {
	            String sing = RiTa.singularize(wordsArr[i]);
	            if (_lexHas("n", sing)) choices2d.add("nns");
	          }

	        } else {

	          String sing = RiTa.singularize(wordsArr[i]);

	          if (_lexHas("n", sing)) {
	            choices2d.add("nns");
	            tag = "nns";
	          } else if (Stemmer._checkPluralNoLex(wordsArr[i])) {
	            tag = "nns";
	            //common plurals
	          }
	        }

	        result.add(tag);

	      } else {

	        result.add(data[0]);
	        choices2d.addAll(Arrays.asList(data));
	      }
	    }

	    // Adjust pos according to transformation rules
	    String[] tags = _applyContext(words, result.toArray(new String[result.size()]), choices2d.toArray(new String[choices2d.size()]));
	    System.out.println("choices2d : " + choices2d);
	    if (useSimpleTags) {
	      for (int i = 0; i < tags.length; i++) {
	        if (Arrays.asList(NOUNS).contains(tags[i])) tags[i] = "n";
	        else if (Arrays.asList(VERBS).contains(tags[i])) tags[i] = "v";
	        else if (Arrays.asList(ADJ).contains(tags[i])) tags[i] = "a";
	        else if (Arrays.asList(ADV).contains(tags[i])) tags[i] = "r";
	        else tags[i] = "-"; // default: other
	      }
	    }	  
	    System.out.println("Tags : " + Arrays.toString(tags));
	    return ((tags == null) ? new String[] {} : tags);
	}

	private static String[] _applyContext(String words, String[] result, String[] choices2d) {
/*
		//console.log("ac(" + words + "," + result + "," + choices2d + ")");

	    // Apply transformations
	    for (int i = 0, l = words.length(); i < l; i++) {

	      String word = Character.toString(words.charAt(i));
	      String tag = result[i];
	      String[] resultSA = result;
	      // transform 1a: DT, {VBD | VBP | VB} --> DT, NN
	      if (i > 0 && (resultSA[i - 1] == "dt")) {

	        if (tag.startsWith("vb")) {
	          tag = "nn";

	          // transform 7: if a word has been categorized as a
	          // common noun and it ends with "s", then set its type to plural common noun (NNS)


	          if (word.matches("^.*[^s]s$")) {
	            if (!Arrays.asList(MODALS).contains(word)) {
	              tag = "nns";
	            }
	          }

	          _logCustom("1a", word, tag);
	        }

	        // transform 1b: DT, {RB | RBR | RBS} --> DT, {JJ |
	        // JJR | JJS}
	        else if (tag.startsWith("rb")) {

	          tag = (tag.length() > 2) ? "jj" + tag.charAt(2) : "jj";
	          _logCustom("1b", word, tag);
	        }
	      }

	      // transform 2: convert a noun to a number (cd) if it is
	      // all digits and/or a decimal "."
	      if (tag.startsWith("n") && choices2d[i].length() != 0 ) {
	        if (isNumeric(word)) {
	          tag = "cd";
	        } // mods: dch (add choice check above) <---- ? >
	      }

	      // transform 3: convert a noun to a past participle if
	      // word ends with "ed" and (following any nn or prp?)
	      if (i > 0 && tag.startsWith("n") && word.endsWith("ed") && !word.endsWith("eed") && resultSA[i - 1].matches("^(nn|prp)$")) {
	        tag = "vbn";
	      }

	      // transform 4: convert any type to adverb if it ends in "ly";
	      if (word.endsWith("ly")) {
	        tag = "rb";
	      }

	      // transform 5: convert a common noun (NN or NNS) to a
	      // adjective if it ends with "al", special-case for mammal
	      if (tag.startsWith("nn") && word.endsWith("al") && word != "mammal") {
	        tag = "jj";
	      }

	      // transform 6: convert a noun to a verb if the
	      // preceeding word is modal
	      if (i > 0 && tag.startsWith("nn") && resultSA[i - 1].startsWith("md")) {
	        tag = "vb";
	      }

	      // transform 8: convert a common noun to a present
	      // participle verb (i.e., a gerund)
	      if (tag.startsWith("nn") && word.endsWith("ing")) {

	        // DH: fixed here -- add check on choices2d for any verb: eg. // "morning"
	        if (hasTag(choices2d[i], "vb")) {
	          tag = "vbg";
	          _logCustom(8, word, tag);
	        }
	      }

	      // transform 9(dch): convert plural nouns (which are also 3sg-verbs) to
	      // 3sg-verbs when following a singular noun (the dog dances, Dave dances, he dances)
	      if (i > 0 && tag == "nns" && hasTag(choices2d[i], "vbz") && resultSA[i - 1].match(/^(nn|prp|nnp)$/)) {
	        tag = "vbz";
	        _logCustom(9, word, tag);
	      }

	      // transform 10(dch): convert common nouns to proper
	      // nouns when they start w' a capital
	      if (tag.startsWith("nn") && (word.charAt(0) == word.charAt(0).toUpperCase())) {
	        //if it is not at the start of a sentence or it is the only word
	        // or when it is at the start of a sentence but can't be found in the dictionary
	        if (i != 0 || words.length == 1 || (i == 0 && !_lexHas("nn", RiTa.singularize(word).toLowerCase()))) {
	          tag = tag.endsWith("s") ? "nnps" : "nnp";
	          _logCustom(10, word, tag);
	        }
	      }

	      // transform 11(dch): convert plural nouns (which are
	      // also 3sg-verbs) to 3sg-verbs when followed by adverb
	      if (i < result.length - 1 && tag == "nns" && resultSA[i + 1].startsWith("rb") &&
	        hasTag(choices2d[i], "vbz")) {
	        tag = "vbz";
	        _logCustom(11, word, tag);
	      }

	      // transform 12(dch): convert plural nouns which have an entry for their base form to vbz
	      if (tag == "nns") {

	        // is preceded by one of the following
	        if (i > 0 && ["nn", "prp", "cc", "nnp"].indexOf(resultSA[i - 1]) > -1) {
	          // if word is ends with s or es and is "nns" and has a vb
	          if (_lexHas("vb", RiTa.singularize(word))) {
	            tag = "vbz";
	            _logCustom(12, word, tag);
	          }
	        } // if only word and not in lexicon
	        else if (words.length == 1 && !choices2d[i].length) {
	          // if the stem of a single word could be both nn and vb, return nns
	          // only return vbz when the stem is vb but not nn
	          if (!_lexHas("nn", RiTa.singularize(word)) && _lexHas("vb", RiTa.singularize(word))) {
	            tag = "vbz";
	            _logCustom(12, word, tag);
	          }

	        }
	      }

	      //transform 13(cqx): convert a vb/ potential vb to vbp when following nns (Elephants dance, they dance)
	      if (tag == "vb" || (tag == "nn" && hasTag(choices2d[i], "vb"))) {
	        if (i > 0 && resultSA[i - 1].match("^(nns|nnps|prp)$")) {
	          tag = "vbp";
	          _logCustom(13, word, tag);
	        }
	      }

	      resultSA[i] = tag;
	    }

	    return result;
		*/
		
		return null;
	}

	private static void _logCustom(String i, String frm, String to) {
		// TODO Auto-generated method stub
		
	}
	

	private static boolean hasTag(String[] choices, String tag) {
	  //  if (!Array.isArray(choices)) return false;
	    String choiceStr = String.join("", choices);
	    return (choiceStr.indexOf(tag) > -1);
	  }


	private static boolean _lexHas(String string, String sub) {
		// TODO Auto-generated method stub
		return false;
	}

	private static String _handleSingleLetter(String c) {
	    String result = c;

	    if (c == "a" || c == "A")
	      result = "dt";
	    else if (c == "I")
	      result = "prp";
	    else if (isNumeric(c))
	      result = "cd";

	    return result;
	}
	
	private static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	private static boolean checkType(String word, String[] tagArray) {

		if (word == null || word.length() == 0) return false;


		if (word.indexOf(" ") < 0) {

			List<String> psa = Arrays.asList(RiTa._lexicon()._posArr(word));
			
			
			if(psa.size() == 0) {
				if (RiTa.LEX_WARN) { // TODO what is size() <= 1000 ??
					Logger logger = Logger.getLogger( Tagger.class.getName()); 
					logger.warning(Boolean.toString(RiTa.LEX_WARN));
					RiTa.LEX_WARN = false; // only once
				}
				List<String> posT = Arrays.asList(RiTa.posTags(word));

				if(posT.size() > 0 ) psa.addAll(posT);
			}
			List<String> finalType = new ArrayList<String>();
			for (String item : psa) {
			    if (Arrays.asList(tagArray).contains(item)) {
			    	finalType.add(item);
			    }
			}
			
		//	psa.forEach(p -> Arrays.asList(tagArray).stream().filter(p1 -> p.indexOf(p1) > 0).forEach(list::add));
			 return finalType.size() > 0;
		}

		throw new RiTaException("checkType() expects single word, found: '" + word + "'");


	}
}
