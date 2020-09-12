package rita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  NOTE: Based on the Penn Treebank tokenization standard, with the following differences:
 *  In Penn, double quotes (") are changed to doubled forward and backward single quotes (`` and '')
 */
public class Tokenizer {
	
	private static final Pattern SPLITTER = Pattern.compile("(\\S.+?[.!?][\"”\u201D]?)(?=\\s+|$)");
	private static final String DELIM = "___";

	public static String[] sentences(String text, Pattern pattern) {
		if (text == null || text.length() == 0) return new String[] { text };
		if (pattern == null) pattern = SPLITTER;

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

	public static String untokenize(String[] arr) {
		return untokenize(arr, " ");
	}

	public static String untokenize(String[] arr, String delim) {
		int dbug = 0;

		boolean thisNBPunct, lastNBPunct, lastNAPunct, thisQuote, lastQuote, thisComma, isLast,
				lastComma, lastEndWithS, nextIsS, thisLBracket, thisRBracket, lastLBracket, lastRBracket, lastIsWWW, thisDomin, withinQuote = false;


		String leftBrackets = "^[\\[\\(\\{⟨]+$",
				rightBrackets = "^[\\)\\]\\}⟩]+$",
		    // no space before the punctuation
		    nbPunct = "^[,\\.;:\\?!)\"\"“”\u2019‘`'%…\u2103\\^\\*°/⁄\\-@]+$",
		    // no space after the punctuation
				naPunct = "^[\\^\\*\\$/⁄#\\-@°]+$",
				quotes = "^[(\"\"“”\u2019‘`''«»‘’]+$",
				squotes = "^[\u2019‘`']+$",
				apostrophes = "^[\u2019'’]+$",
				www = "^(www[0-9]?|WWW[0-9]?)$",
				domin = "^(com|org|edu|net|xyz|gov|int|eu|hk|tw|cn|de|ch|fr)$";

		if (arr.length > 0) {
			withinQuote = Pattern.matches(quotes, arr[0]);
		}

		String result = arr.length > 0 ? arr[0] : "";

		boolean afterQuote = false, midSentence = false, nextNoSpace = false;

		for (int i = 1; i < arr.length; i++) {

			if (arr[i] == null) continue;

			thisComma = arr[i] == ",";
			thisNBPunct = Pattern.matches(nbPunct, arr[i]);
			//thisNAPunct = Pattern.matches(naPunct, arr[i]);
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
			lastEndWithS = arr[i - 1].charAt(arr[i - 1].length() - 1) == 's'
					&& arr[i - 1] != "is" && arr[i - 1] != "Is" && arr[i - 1] != "IS";
			lastIsWWW = Pattern.matches(www, arr[i - 1]);
			nextIsS = i == arr.length - 1 ? false : (arr[i + 1] == "s" || arr[i + 1] == "S");
			isLast = (i == arr.length - 1);

			if ((arr[i - 1] == "." && thisDomin) || nextNoSpace) {
				nextNoSpace = false;
				result += arr[i];
				continue;
			}
			else if (arr[i] == "." && lastIsWWW) {
				nextNoSpace = true;
			}
			else if (thisLBracket) {
				result += delim;
			}
			else if (lastRBracket) {
				if (!thisNBPunct && !thisLBracket) {
					result += delim;
				}
			}
			else if (thisQuote) {

				if (withinQuote) {
					// no-delim, mark quotation done
					afterQuote = true;
					withinQuote = false;
				}
				else if (!((Pattern.matches(apostrophes, arr[i]) && lastEndWithS)
						|| (Pattern.matches(apostrophes, arr[i]) && nextIsS))) {
					withinQuote = true;
					afterQuote = false;
					result += delim;
				}
			}
			else if (afterQuote && !thisNBPunct) {

				result += delim;
				afterQuote = false;

			}
			else if (lastQuote && thisComma) {

				midSentence = true;

			}
			else if (midSentence && lastComma) {

				result += delim;
				midSentence = false;

			}
			else if ((!thisNBPunct && !lastQuote && !lastNAPunct && !lastLBracket && !thisRBracket)
					|| (!isLast && thisNBPunct && lastNBPunct && !lastNAPunct && !lastQuote && !lastLBracket && !thisRBracket)) {

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

		words = words.trim();

		// save abbreviations------------
		words = words.replaceAll("([Ee])[.]([Gg])[.]", "_$1$2_"); // E.©G.
		words = words.replaceAll("([Ii])[.]([Ee])[.]", "_$1$2_"); // I.E.
		words = words.replaceAll("([Aa])[\\.]([Mm])[\\.]", "_$1$2_"); // a.m.
		words = words.replaceAll("([Pp])[\\.]([Mm])[\\.]", "_$1$2_"); // p.m.
		words = words.replaceAll("(Cap)[\\.]", "_Cap_"); // Cap.
		words = words.replaceAll("([Cc])[\\.]", "_$1_"); // c.
		words = words.replaceAll("([Ee][Tt])[\\s]([Aa][Ll])[\\.]", "_$1zzz$2_"); // et al.
		words = words.replaceAll("(ect|ECT)[\\.]", "_$1_"); // ect.
		words = words.replaceAll("([Pp])[\\.]([Ss])[\\.]", "_$1$2dot_"); // p.s.
		words = words.replaceAll("([Pp])[\\.]([Ss])", "_$1$2_"); // p.s
		words = words.replaceAll("([Pp])([Hh])[\\.]([Dd])", "_$1$2$3_");// Ph.D
		words = words.replaceAll("([Rr])[\\.]([Ii])[\\.]([Pp])", "_$1$2$3_"); // R.I.P
		words = words.replaceAll("([Vv])([Ss]?)[\\.]", "_$1$2_"); // vs. and v.
		words = words.replaceAll("([Mm])([Rr]|[Ss]|[Xx])[\\.]", "_$1$2_"); // Mr. Ms. and Mx.
		words = words.replaceAll("([Dd])([Rr])[\\.]", "_$1$2_"); // Dr.
		words = words.replaceAll("([Pp])([Ff])[\\.]", "_$1$2_"); // Pf.
		words = words.replaceAll("([Ii])([Nn])([Dd]|[Cc])[\\.]", "_$1$2$3_"); // Ind. and Inc.
		words = words.replaceAll("([Cc])([Oo])[\\.][\\,][\\s]([Ll])([Tt])([Dd])[\\.]", "_$1$2dcs$3$4$5_"); // co., ltd.
		words = words.replaceAll("([Cc])([Oo])[\\.][\\s]([Ll])([Tt])([Dd])[\\.]", "_$1$2ds$3$4$5_"); // co. ltd.
		words = words.replaceAll("([Cc])([Oo])[\\.][\\,]([Ll])([Tt])([Dd])[\\.]", "_$1$2dc$3$4$5_"); // co.,ltd.
		words = words.replaceAll("([Cc])([Oo])([Rr]?)([Pp]?)[\\.]", "_$1$2$3$4_"); // Co. and Corp.
		words = words.replaceAll("([Ll])([Tt])([Dd])[\\.]", "_$1$2$3_"); // ltd.

		words = words.replaceAll("\\.{3}", "_elipsisDDD_");
		words = words.replaceAll("([\\?\\!\\\"\\u201C\\\\.,;:@#$%&])", " $1 ");
		words = words.replaceAll("\\s+", " ");
		words = words.replaceAll(",([^0-9])", " , $1");
		words = words.replaceAll("([^.])([.])([\\])}>\\\"'’]*)\\s*$", "$1 $2$3 ");
		words = words.replaceAll("([\\[\\](){}<>⟨⟩])", " $1 ");
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
			words = words.replaceAll("([Ii])t['’]s", " $1t is");
			words = words.replaceAll("n['’]t ", " not ");
			words = words.replaceAll("['’]ve ", " have ");
			words = words.replaceAll("['’]re ", " are ");
		}

		// "Nicole I. Kidman" gets tokenized as "Nicole I . Kidman"
		words = words.replaceAll(" ([A-Z]) \\\\.", " $1. ");
		words = words.replaceAll("\\\\s+", " ");
		words = words.replaceAll("^\\\\s+", "");
		words = words.replaceAll("\\^", " ^ ");
		words = words.replaceAll("°", " ° ");
		words = words.replaceAll("…", " … ");
		words = words.replaceAll("_elipsisDDD_", " ... ");

		// restore abbreviations--------------------------
		words = words.replaceAll("_([Ee])([Gg])_", "$1.$2."); // E.G.
		words = words.replaceAll("_([Ii])([Ee])_", "$1.$2."); // I.E.
		words = words.replaceAll("_([Aa])([Mm])_", "$1.$2."); // a.m.
		words = words.replaceAll("_([Pp])([Mm])_", "$1.$2."); // p.m.
		words = words.replaceAll("_(Cap)_", "Cap."); // Cap.
		words = words.replaceAll("_([Cc])_", "$1."); // c.
		words = words.replaceAll("_([Ee][Tt])zzz([Aa][Ll])_", "$1_$2."); // et al.
		words = words.replaceAll("_(ect|ECT)_", "$1."); // ect.
		words = words.replaceAll("_([Pp])([Ss])dot_", "$1.$2."); // p.s.
		words = words.replaceAll("_([Pp])([Ss])_", "$1.$2"); // p.s
		words = words.replaceAll("_([Pp])([Hh])([Dd])_", "$1$2.$3");// Ph.D
		words = words.replaceAll("_([Rr])([Ii])([Pp])_", "$1.$2.$3"); // R.I.P
		words = words.replaceAll("_([Vv])([Ss]?)_", "$1$2."); // vs. and v.
		words = words.replaceAll("_([Mm])([Rr]|[Ss]|[Xx])_", "$1$2."); // Mr. Ms. and Mx.
		words = words.replaceAll("_([Dd])([Rr])_", "$1$2."); // Dr.
		words = words.replaceAll("_([Pp])([Ff])_", "$1$2."); // Pf.
		words = words.replaceAll("_([Ii])([Nn])([Dd]|[Cc])_", "$1$2$3."); // Ind. and Inc.
		words = words.replaceAll("_([Cc])([Oo])dcs([Ll])([Tt])([Dd])_", "$1$2.,_$3$4$5."); // co., ltd.
		words = words.replaceAll("_([Cc])([Oo])ds([Ll])([Tt])([Dd])_", "$1$2._$3$4$5."); // co. ltd.
		words = words.replaceAll("_([Cc])([Oo])dc([Ll])([Tt])([Dd])_", "$1$2.,$3$4$5."); // co.,ltd.
		words = words.replaceAll("_([Cc])([Oo])([Rr]?)([Pp]?)_", "$1$2$3$4."); // Co. and Corp.
		words = words.replaceAll("_([Ll])([Tt])([Dd])_", "$1$2$3."); // ltd.

		words = words.trim();

		String[] result = words.split("\\s+");
		for (int i = 0; i < result.length; i++) {
			String token = result[i];
			if (token.contains("_")) {
				result[i] = token.replaceAll("([a-zA-Z]|[\\,\\.])_([a-zA-Z])", "$1 $2");
			}
		}
		return result;
	}

	//////////////////////////////////////////////////////////////////

	private static String[] unescapeAbbrevs(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].replaceAll(DELIM, ".");
		}
		return arr;
	}

	private static String escapeAbbrevs(String text) {

		String[] abbrevs = RiTa.ABBREVIATIONS;
		for (int i = 0; i < abbrevs.length; i++) {
			String abv = abbrevs[i];
			int idx = text.indexOf(abv);
			while (idx > -1) {
				text = text.replace(abv, abv.replace(".", DELIM));
				idx = text.indexOf(abv);
			}
		}
		return text;
	}

}
