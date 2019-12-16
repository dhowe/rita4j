package rita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import java.util.logging.Logger;

public class Tagger
{

	public static final String[] ADJ = {"jj", "jjr", "jjs"};
	public static final String[] ADV = {"rb", "rbr", "rbs", "rp"};
	public static final String[] NOUNS = {"nn", "nns", "nnp", "nnps"};
	public static final String[] VERBS = {"vb", "vbd", "vbg", "vbn", "vbp", "vbz"};


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

	      String[] data = lexicon._posArr(wordsArr[i]);
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
	          String sub2 ="";
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
	    String[] tags = _applyContext(words, result, choices2d);

	    if (useSimpleTags) {
	      for (int i = 0; i < tags.length; i++) {
	        if (Arrays.asList(NOUNS).contains(tags[i])) tags[i] = "n";
	        else if (Arrays.asList(VERBS).contains(tags[i])) tags[i] = "v";
	        else if (Arrays.asList(ADJ).contains(tags[i])) tags[i] = "a";
	        else if (Arrays.asList(ADV).contains(tags[i])) tags[i] = "r";
	        else tags[i] = "-"; // default: other
	      }
	    }	    
	    return ((tags == null) ? new String[] {} : tags);
	}

	private static String[] _applyContext(String words, List<String> result, List<String> choices2d) {
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean _lexHas(String string, String sub) {
		// TODO Auto-generated method stub
		return false;
	}

	private static String _handleSingleLetter(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private static boolean checkType(String word, String[] tagArray) {

		if (word == null || word.length() == 0) return false;


		if (word.indexOf(" ") < 0) {

			List<String> psa = Arrays.asList(RiTa._lexicon()._posArr(word));
			
			System.out.println("psa before : " + psa );
			
			if(psa.size() == 0) {
				if (RiTa.LEX_WARN) { // TODO what is this.size() <= 1000 ??
					Logger logger = Logger.getLogger( Tagger.class.getName()); 
					logger.warning(Boolean.toString(RiTa.LEX_WARN));
					RiTa.LEX_WARN = false; // only once
				}
				List<String> posT = Arrays.asList(RiTa.posTags(word));
				//System.out.println("posT.length " + posT.length );
				if(posT.size() > 0 ) psa.addAll(posT);
			}
			List<String> finalType = new ArrayList<String>();
			for (String item : psa) {
			    if (Arrays.asList(tagArray).contains(item)) {
			    	finalType.add(item);
			    }
			}
			
		//	psa.forEach(p -> Arrays.asList(tagArray).stream().filter(p1 -> p.indexOf(p1) > 0).forEach(list::add));
			 System.out.println("finalType " + finalType );
			 return finalType.size() > 0;
		}

		throw new RiTaException("checkType() expects single word, found: '" + word + "'");


	}
}
