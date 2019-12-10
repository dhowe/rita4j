package rita;

import java.util.regex.Pattern;

public class Tokenizer
{

  public static String[] sentences(String text, Pattern regex)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public static String untokenize(String[] words)
  {
	  /*

	    delim = delim || ' ';

	    let thisPunct, lastPunct, thisQuote, lastQuote, thisComma, isLast,
	      lastComma, lastEndWithS, punct = /^[,\.\;\:\?\!\)""“”\u2019‘`']+$/,
	      dbug = 0,
	      quotes = /^[\(""“”\u2019‘`']+$/,
	      squotes = /^[\u2019‘`']+$/,
	      apostrophes = /^[\u2019']+$/,
	      afterQuote = false,
	      withinQuote = arr.length && quotes.test(arr[0]),
	      result = arr[0] || '',
	      midSentence = false;

	    for (let i = 1; i < arr.length; i++) {

	      if (!arr[i]) continue;

	      thisComma = arr[i] === ',';
	      thisPunct = punct.test(arr[i]);
	      thisQuote = quotes.test(arr[i]);
	      lastComma = arr[i - 1] === ',';
	      lastPunct = punct.test(arr[i - 1]);
	      lastQuote = quotes.test(arr[i - 1]);
	      lastEndWithS = arr[i - 1].charAt(arr[i - 1].length - 1) === 's';
	      isLast = (i == arr.length - 1);

	      if (thisQuote) {

	        if (withinQuote) {

	          // no-delim, mark quotation done
	          afterQuote = true;
	          withinQuote = false;
	        } else if (!(apostrophes.test(arr[i]) && lastEndWithS)) {
	          withinQuote = true;
	          afterQuote = false;
	          result += delim;
	        }

	      } else if (afterQuote && !thisPunct) {

	        result += delim;
	        afterQuote = false;

	      } else if (lastQuote && thisComma) {

	        midSentence = true;

	      } else if (midSentence && lastComma) {

	        result += delim;
	        midSentence = false;

	      } else if ((!thisPunct && !lastQuote) || (!isLast && thisPunct && lastPunct)) {

	        result += delim;
	      }

	      result += arr[i]; // add to result

	      if (thisPunct && !lastPunct && !withinQuote && squotes.test(arr[i])) {

	        result += delim; // fix to #477
	      }
	    }

	    return Util.trim(result);
	 
	  }
	  */
	  return null;
  }

  public static String[] tokenize(String words)
  {
	  if (words == null || words.length() == 0) return new String[] {""};

	    //if (regex) return words.split(regex); //TODO check this param

	  //Javascript to java regex converted from https://regex101.com/
	    words = words.trim(); // ???
	    words = words.replaceAll("([Ee])[.]([Gg])[.]", "_$1$2_"); // E.©G.
	    words = words.replaceAll("([Ii])[.]([Ee])[.]", "_$1$2_"); // I.E.

	    words = words.replaceAll("([\\\\\\\\?!\\\\\\\"\\\\u201C\\\\\\\\.,;:@#$%&])", " $1 ");
	    words = words.replaceAll("\\\\\\\\.\\\\\\\\.\\\\\\\\.", " ... ");
	    words = words.replaceAll("\\\\\\\\s+", " ");
	    words = words.replaceAll(",([^0-9])", " , $1");
	    words = words.replaceAll("(([^.])([.])([\\\\])\\\\}>\\\\\\\"'’]*)\\\\\\\\s*$", "$1 $2$3 ");
	    words = words.replaceAll("([\\\\[\\\\]()\\\\{}<>])", " $1 ");
	    words = words.replaceAll("--", " -- ");
	    words = words.replaceAll("$", " ");
	    words = words.replaceAll("^", " ");
	    words = words.replaceAll("([^'])' | '", "$1 \' ");
	    words = words.replaceAll(" \\\\u2018", " \u2018 ");
	    words = words.replaceAll("'([SMD]) ", " \'$1 ");

	    if (RiTa.SPLIT_CONTRACTIONS) {

	      words = words.replaceAll("([Cc])an['’]t", "$1an not");
	      words = words.replaceAll("([Dd])idn['’]t", "$1id not");
	      words = words.replaceAll("([CcWw])ouldn['’]t", "$1ould not");
	      words = words.replaceAll("([Ss])houldn['’]t", "$1hould not");
	      words = words.replaceAll(" ([Ii])t['’]s", " $1t is");
	      words = words.replaceAll("n['’]t ", " not ");
	      words = words.replaceAll("['’]ve ", " have ");
	      words = words.replaceAll("['’]re ", " are ");
	    }

	    // "Nicole I. Kidman" gets tokenized as "Nicole I . Kidman"
	    words = words.replaceAll(" ([A-Z]) \\\\\\\\.", " $1. ");
	    words = words.replaceAll("\\\\\\\\s+", " ");
	    words = words.replaceAll("^\\\\\\\\s+", "");

	    words = words.replaceAll("_([Ee])([Gg])_", "$1.$2."); // E.G.
	    words = words.replaceAll("_([Ii])([Ee])_", "$1.$2."); // I.E.
	    
	    words = words.trim();

	    return words.split("\\s+");
	    
  }

}
