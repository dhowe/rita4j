package rita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
	private static Pattern splitter = Pattern.compile("(\\S.+?[.!?][\"”\u201D]?)(?=\\s+|$)");
	private static String delim = "___";

	public static String[] sentences(String text, Pattern pattern) {
		if (text == null || text.length() == 0) return new String[] { text };
		if (pattern == null) pattern = splitter;

		String clean = text.replaceAll("(\r?\n)+", " ");
		List<String> allMatches = new ArrayList<String>();

		Matcher m = pattern.matcher(escapeAbbrevs(clean));
		while (m.find()) {
			allMatches.add(m.group());
		}
		String[] arr = allMatches.toArray(new String[0]);
		if (arr == null || arr.length == 0)
			return new String[] { text };
		else
			return unescapeAbbrevs(arr);

	}

	private static String[] unescapeAbbrevs(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].replaceAll(delim, ".");
		}
		return arr;
	}

	private static String escapeAbbrevs(String text) {

		String[] abbrevs = RiTa.ABBREVIATIONS;
		for (int i = 0; i < abbrevs.length; i++) {
			String abv = abbrevs[i];
			int idx = text.indexOf(abv);
			while (idx > -1) {
				System.out.print(abv);
				text = text.replace(abv, abv.replace(".", delim));
				idx = text.indexOf(abv);
			}
		}
		return text;
	}

	public static String untokenize(String[] arr) {
		return untokenize(arr, " ");
	}

	public static String untokenize(String[] arr, String delim) {
		int dbug = 0;

		boolean thisNBPunct, thisNAPunct, lastNBPunct, lastNAPunct, thisQuote, lastQuote, thisComma, isLast,
				lastComma, lastEndWithS, nextIsS, thisLBracket, thisRBracket, lastLBracket, lastRBracket, lastIsWWW, thisDomin, withinQuote = false;

		String  nbPunct = "^[,\\.;:\\?!)\"\"“”\u2019‘`'%…\u2103\\^\\*°/⁄\\-@]+$",//no space before the punctuation
				naPunct = "^[\\^\\*\\$/⁄#\\-@°]+$",//no space after the punctuation
				leftBrackets = "^[\\[\\(\\{⟨]+$",
				rightBrackets = "^[\\)\\]\\}⟩]+$",
				quotes = "^[(\"\"“”\u2019‘`''«»‘’]+$",
				squotes = "^[\u2019‘`']+$",
				apostrophes = "^[\u2019'’]+$",
				www = "^(www[0-9]?|WWW[0-9]?)$",
				domin = "^(com|org|edu|net|xyz|gov|int|eu|hk|tw|cn|de|ch|fr)$";

		if (arr.length > 0) {
			withinQuote = Pattern.matches(quotes, arr[0]);
		}
		System.out.println(withinQuote);

		String result = arr.length > 0 ? arr[0] : "";

		boolean afterQuote = false, midSentence = false, nextNoSpace = false;

		for (int i = 1; i < arr.length; i++) {

			if (arr[i] == null) continue;

			thisComma = arr[i] == ",";
			thisNBPunct = Pattern.matches(nbPunct, arr[i]);
			thisNAPunct = Pattern.matches(naPunct, arr[i]);
			thisQuote = Pattern.matches(quotes, arr[i]);
			thisLBracket = Pattern.matches(leftBrackets, arr[i]);
			thisRBracket = Pattern.matches(rightBrackets, arr[i]);
			thisDomin = Pattern.matches(domin, arr[i]);
			lastComma = arr[i - 1] == ",";
			lastNBPunct = Pattern.matches(nbPunct, arr[i - 1]);
			lastNAPunct = Pattern.matches(naPunct, arr[i - 1]);
			lastQuote = Pattern.matches(quotes, arr[i - 1]);
			lastLBracket = Pattern.matches(leftBrackets, arr[i - 1]);
			lastRBracket = Pattern.matches(rightBrackets, arr[i - 1]);
			lastEndWithS = arr[i - 1].charAt(arr[i - 1].length() - 1) == 's' && arr[i - 1]!= "is" && arr[i - 1] != "Is" && arr[i - 1] != "IS";
			lastIsWWW = Pattern.matches(www, arr[i - 1]);
			nextIsS = i == arr.length - 1 ? false : (arr[i + 1] == "s" || arr[i + 1] == "S");
			isLast = (i == arr.length - 1);

			if ((arr[i - 1] == "." && thisDomin) || nextNoSpace){
				nextNoSpace = false;
				result += arr[i];
				continue;
			} else if (arr[i] == "." && lastIsWWW){
				nextNoSpace = true;
			} else if (thisLBracket) {
				result += delim;
			} else if (lastRBracket){
				if (!thisNBPunct && !thisLBracket){
				  result += delim;
				}
			} else if (thisQuote) {

				if (withinQuote) {
					// no-delim, mark quotation done
					afterQuote = true;
					withinQuote = false;

				} else if (!((Pattern.matches(apostrophes, arr[i]) && lastEndWithS) || (Pattern.matches(apostrophes, arr[i]) && nextIsS))) {
					withinQuote = true;
					afterQuote = false;
					result += delim;

				}

			} else if (afterQuote && !thisNBPunct) {

				result += delim;
				afterQuote = false;

			} else if (lastQuote && thisComma) {

				midSentence = true;

			} else if (midSentence && lastComma) {

				result += delim;
				midSentence = false;

			} else if ((!thisNBPunct && !lastQuote && !lastNAPunct && !lastLBracket && !thisRBracket) || (!isLast && thisNBPunct && lastNBPunct && !lastNAPunct && !lastQuote && !lastLBracket && !thisRBracket)) {

				result += delim;
			}

			result += arr[i]; // add to result

			if (thisNBPunct && !lastNBPunct && !withinQuote && Pattern.matches(squotes, arr[i]) && lastEndWithS) {

				result += delim; // fix to #477
			}
		}

		return result.trim();
	}

	public static String[] tokenize(String words) {
		if (words == null || words.length() == 0) return new String[] { "" };

		// if (regex) return words.split(regex); //TODO check this param

		// Javascript to java regex converted from https://regex101.com/
		words = words.trim(); // ???
		words = words.replaceAll("([Ee])[.]([Gg])[.]", "_$1$2_"); // E.©G.
		words = words.replaceAll("([Ii])[.]([Ee])[.]", "_$1$2_"); // I.E.

		words = words.replaceAll("([\\\\?!\\\"\\u201C\\\\.,;:@#$%&])", " $1 ");
		words = words.replaceAll("\\\\.\\\\.\\\\.", " ... ");
		words = words.replaceAll("\\\\s+", " ");
		words = words.replaceAll(",([^0-9])", " , $1");
		words = words.replaceAll("([^.])([.])([\\])}>\\\"'’]*)\\\\s*$", "$1 $2$3 ");
		words = words.replaceAll("([\\[\\](){}<>])", " $1 ");
		words = words.replaceAll("--", " -- ");
		words = words.replaceAll("$", " ");
		words = words.replaceAll("^", " ");
		words = words.replaceAll("([^'])' | '", "$1 \' ");
		words = words.replaceAll(" \u2018", " \u2018 ");
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
		words = words.replaceAll(" ([A-Z]) \\\\.", " $1. ");
		words = words.replaceAll("\\\\s+", " ");
		words = words.replaceAll("^\\\\s+", "");

		words = words.replaceAll("_([Ee])([Gg])_", "$1.$2."); // E.G.
		words = words.replaceAll("_([Ii])([Ee])_", "$1.$2."); // I.E.

		words = words.trim();

		return words.split("\\s+");

	}

}
