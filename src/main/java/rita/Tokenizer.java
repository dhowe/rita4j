package rita;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NOTE: Based on the Penn Treebank tokenization standard, with the following
 * difference: In Penn, double quotes (") are changed to doubled forward and backward single
 * quotes (`` and '').
 */
public class Tokenizer {

	public static String[] tokenize(String words) {
		return tokenize(words, null);
	}

	public static String untokenize(String[] arr) {
		return untokenize(arr, " ");
	}

	public static String[] tokens(String text) {
		return tokens(text, null);
	}
	
	public static String[] tokens(String text, String regex) {
    String[] words = tokenize(text, regex);
    Set<String> tokens = new HashSet<String>();
    for (int i = 0; i < words.length; i++) {
			if (RE.test(ALPHA_RE, words[i])) {
				tokens.add(words[i].toLowerCase());
			}
		}
    List<String> toSort = new ArrayList<String>(tokens);
    Collections.sort(toSort);
    return (String[]) toSort.toArray(new String[toSort.size()]);
  }
  
	public static String[] sentences(String text, Pattern pattern) {

		if (text == null || text.length() == 0) {
			return new String[] { text };
		}

		String clean = LINEBREAKS.matcher(text).replaceAll(" ");

		if (pattern == null) pattern = SPLITTER;

		Matcher m = pattern.matcher(escapeAbbrevs(clean));
		List<String> allMatches = new ArrayList<String>();
		while (m.find()) {
			allMatches.add(m.group());
		}

		String[] arr = allMatches.toArray(new String[0]);
		return arr.length == 0
				? new String[] { text }
				: unescapeAbbrevs(arr);
	}

	public static String untokenize(String[] arr, String delim) {

		boolean thisNBPunct, lastNBPunct, lastNAPunct, thisQuote;
		boolean lastQuote, thisComma, isLast, lastComma, lastEndWithS;
		boolean nextIsS, thisLBracket, thisRBracket, lastLBracket;
		boolean lastRBracket, lastIsWWW, thisDomin, withinQuote = false;
		boolean afterQuote = false, midSentence = false, nextNoSpace = false;

		if (arr == null || arr.length == 0) return "";

		if (arr.length > 0) withinQuote = QUOTES.matcher(arr[0]).matches();

		String result = arr[0]; // start with first token

		for (int i = 1; i < arr.length; i++) {

			if (arr[i] == null) continue;

			thisComma = arr[i].equals(",");
			thisNBPunct = NB_PUNCT.matcher(arr[i]).matches();
			//thisNAPunct = NA_PUNCT.matcher(arr[i]).matches();
			thisQuote = QUOTES.matcher(arr[i]).matches();
			thisLBracket = LBRACKS.matcher(arr[i]).matches();
			thisRBracket = RBRACKS.matcher(arr[i]).matches();
			thisDomin = DOMIN.matcher(arr[i]).matches();
			lastComma = arr[i - 1].equals(",");
			lastNBPunct = NB_PUNCT.matcher(arr[i - 1]).matches();
			lastNAPunct = NA_PUNCT.matcher(arr[i - 1]).matches();
			lastQuote = QUOTES.matcher(arr[i - 1]).matches();
			lastLBracket = LBRACKS.matcher(arr[i - 1]).matches();
			lastRBracket = RBRACKS.matcher(arr[i - 1]).matches();
			lastEndWithS = arr[i - 1].charAt(arr[i - 1].length() - 1) == 's'
					&& !arr[i - 1].equals("is") && !arr[i - 1].equals("Is") && !arr[i - 1].equals("IS");
			lastIsWWW = WWW.matcher(arr[i - 1]).matches();
			nextIsS = i == arr.length - 1 ? false : (arr[i + 1].equals("s") || arr[i + 1].equals("S"));
			isLast = (i == arr.length - 1);

			if ((arr[i - 1].equals(".") && thisDomin) || nextNoSpace) {
				nextNoSpace = false;
				result += arr[i];
				continue;
			}
			else if (arr[i].equals(".") && lastIsWWW) {
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
				else if (!((APOS.matcher(arr[i]).matches() && lastEndWithS)
						|| (APOS.matcher(arr[i]).matches() && nextIsS))) {
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

			if (thisNBPunct && !lastNBPunct && !withinQuote
					&& SQUOTES.matcher(arr[i]).matches() && lastEndWithS) {
				result += delim; // fix to #477
			}
		}

		for (int i = 0; i < UNTOKENIZE_HTMLTAG_RE.length; i++) {
			result = untokenizeTags(result, i);
		}

		return result.trim();
	}

	public static String[] tokenize(String words, String regex) {

		if (words == null) return new String[0];
		if (words.length() == 0) return new String[] { "" };

		// handle a regex argument
		if (regex != null) return words.split(regex);

		words = words.trim();

		//handle html tags ---- save tags
		ArrayList<String> htmlTags = (ArrayList<String>) pushTags(words).get(0);
		words = (String) pushTags(words).get(1);
		for (int i = 0; i < TOKPAT1.length; i++) {
			words = TOKPAT1[i].matcher(words)
					.replaceAll(TOKREP1[i]);
		}

		if (RiTa.SPLIT_CONTRACTIONS) {
			for (int i = 0; i < TOKPAT2.length; i++) {
				words = TOKPAT2[i].matcher(words)
						.replaceAll(TOKREP2[i]);
			}
		}

		for (int i = 0; i < TOKPAT3.length; i++) {
			words = TOKPAT3[i].matcher(words)
					.replaceAll(TOKREP3[i]);
		}

		words = words.trim();
		String[] result = words.split("\\s+");
		ArrayList<String> toReturn = popTags(result, htmlTags);
		return toReturn.toArray(new String[] {});
	}
	
	private static List<Object> pushTags(String words) {
		ArrayList<String> htmlTags = new ArrayList<String>();
		int indexOfTags = 0;
		Matcher currentMatcher = HTML_TAGS_RE.matcher(words);
		while (currentMatcher.find()) {
			htmlTags.add(currentMatcher.group());
			words = words.replace(htmlTags.get(indexOfTags), " _HTMLTAG" + indexOfTags + "_ ");
			indexOfTags++;
		}
		List<Object> toReturn = Arrays.asList(htmlTags, words);
		return toReturn;
	}

	private static ArrayList<String> popTags(String[] result, ArrayList<String> htmlTags) {
		ArrayList<String> toReturn = new ArrayList<String>(); //strings are immutable
		for (int i = 0; i < result.length; i++) {
			String token = result[i];
			//pop html tags
			if (token.contains("_HTMLTAG")) {
				toReturn.add(htmlTags.get(0));
				htmlTags.remove(0);
				continue;
			}

			if (token.contains("_")) {
				toReturn.add(UNDERSCORE.matcher(token).replaceAll("$1 $2"));
				continue;
			}
			toReturn.add(token);
		}
		return toReturn;
	}

	private static String[] unescapeAbbrevs(String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].replaceAll(DELIM, ".");
		}
		return arr;
	}

	private static String escapeAbbrevs(String text) {

		String[] abbrevs = RiTa.ABRV;
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

	private static String untokenizeTags(String input, int i) {
		Matcher currentMatcher = UNTOKENIZE_HTMLTAG_RE[i].matcher(input);
		switch (i) {
		default:
			break;
		case 0:
			while (currentMatcher.find()) {
				String trimedP1 = currentMatcher.group(1).trim();
				String toReplace = currentMatcher.group();
				input = input.replace(toReplace, "<" + trimedP1 + "/>");
			}
			break;
		case 1:
			while (currentMatcher.find()) {
				String trimedP1 = currentMatcher.group(1).trim();
				String toReplace = currentMatcher.group();
				input = input.replace(toReplace, "<" + trimedP1 + ">");
			}
			break;
		case 2:
			while (currentMatcher.find()) {
				String trimedP1 = currentMatcher.group(1).trim();
				String toReplace = currentMatcher.group();
				input = input.replace(toReplace, "</" + trimedP1 + ">");
			}
			break;
		case 3:
			while (currentMatcher.find()) {
				String p1 = currentMatcher.group(1).replaceAll(" ", "");
				String trimedP2 = currentMatcher.group(2).trim();
				String toReplace = currentMatcher.group();
				input = input.replace(toReplace, "<" + p1 + " " + trimedP2 + ">");
			}
			break;
		case 4:
			while (currentMatcher.find()) {
				String trimedP1 = currentMatcher.group(1).trim();
				String toReplace = currentMatcher.group();
				input = input.replace(toReplace, "<!--" + trimedP1 + "-->");
			}
			break;
		}
		return input;
	}

	private static final Pattern UNDERSCORE = Pattern.compile("([a-zA-Z]|[\\\\,\\\\.])_([a-zA-Z])");
	private static final Pattern SPLITTER = Pattern.compile("(\\S.+?[.!?][\"”\u201D]?)(?=\\s+|$)");
	private static final Pattern LBRACKS = Pattern.compile("^[\\[\\(\\{⟨]+$");
	private static final Pattern RBRACKS = Pattern.compile("^[\\)\\]\\}⟩]+$");

	// no space before the punctuation
	private static final Pattern NB_PUNCT = Pattern.compile("^[,\\.;:\\?!)\"\"“”\u2019‘`'%…\u2103\\^\\*°/⁄\\-@]+$");

	// no space after the punctuation
	private static final Pattern NA_PUNCT = Pattern.compile("^[\\^\\*\\$/⁄#\\-@°]+$");
	private static final Pattern QUOTES = Pattern.compile("^[(\"\"“”\u2019‘`''«»‘’]+$");
	private static final Pattern SQUOTES = Pattern.compile("^[\u2019‘`']+$");
	private static final Pattern APOS = Pattern.compile("^[\u2019'’]+$");
	private static final Pattern WWW = Pattern.compile("^(www[0-9]?|WWW[0-9]?)$");
	private static final Pattern DOMIN = Pattern.compile("^(com|org|edu|net|xyz|gov|int|eu|hk|tw|cn|de|ch|fr)$");
	private static final Pattern ALPHA_RE = Pattern.compile("^[A-Za-z]+$");

	private static final Pattern[] TOKPAT1 = new Pattern[] {
			//save abbreviation ------------------------------------
			Pattern.compile("([Ee])[.]([Gg])[.]"),//e.g
			Pattern.compile("([Ii])[.]([Ee])[.]"),//i.e
			Pattern.compile("([Aa])[\\.]([Mm])[\\.]"),//a.m.
			Pattern.compile("([Pp])[\\.]([Mm])[\\.]"),//p.m
			Pattern.compile("(Cap)[\\.]"),//Cap.
			Pattern.compile("([Cc])[\\.]"),//c.
			Pattern.compile("([Ee][Tt])[\\s]([Aa][Ll])[\\.]"),//et al.
			Pattern.compile("(ect|ECT)[\\.]"),//ect.
			Pattern.compile("([Pp])[\\.]([Ss])[\\.]"),//p.s.
			Pattern.compile("([Pp])[\\.]([Ss])"),//p.s
			Pattern.compile("([Pp])([Hh])[\\.]([Dd])"),//Ph.D
			Pattern.compile("([Rr])[\\.]([Ii])[\\.]([Pp])"),//R.I.P
			Pattern.compile("([Vv])([Ss]?)[\\.]"),//v.s and v.
			Pattern.compile("([Mm])([Rr]|[Ss]|[Xx])[\\.]"),//Mr. Ms. and Mx.
			Pattern.compile("([Dd])([Rr])[\\.]"),//Dr.
			Pattern.compile("([Pp])([Ff])[\\.]"),//Pf.
			Pattern.compile("([Ii])([Nn])([Dd]|[Cc])[\\.]"),// Inc. and Ind.
			Pattern.compile("([Cc])([Oo])[\\.][\\,][\\s]([Ll])([Tt])([Dd])[\\.]"),//co., ltd.
			Pattern.compile("([Cc])([Oo])[\\.][\\s]([Ll])([Tt])([Dd])[\\.]"),//co. ltd.
			Pattern.compile("([Cc])([Oo])[\\.][\\,]([Ll])([Tt])([Dd])[\\.]"),//co.,ltd.
			Pattern.compile("([Cc])([Oo])([Rr]?)([Pp]?)[\\.]"),//co. and Corp.
			Pattern.compile("([Ll])([Tt])([Dd])[\\.]"), //ltd.
			Pattern.compile("(Prof|PROF|prof)[\\.]"), //Prof.
			//---------------------------------------------------------------
			Pattern.compile("\\.{3}"),
			Pattern.compile("([\\?\\!\\\"\\u201C\\\\.,;:@#$%&])"),
			Pattern.compile("\\s+"),
			Pattern.compile(",([^0-9])"),
			Pattern.compile("([^.])([.])([\\])}>\\\"'’]*)\\s*$"),
			Pattern.compile("([\\[\\](){}<>⟨⟩])"),
			Pattern.compile("--"),
			Pattern.compile("$"),
			Pattern.compile("^"),
			Pattern.compile("([^'])' | '"),
			Pattern.compile(" \u2018"),
			Pattern.compile("'([SMD]) "),
	};

	private static final Pattern[] TOKPAT2 = new Pattern[] {
			Pattern.compile("([Cc])an['’]t"),
			Pattern.compile("([Dd])idn['’]t"),
			Pattern.compile("([CcWw])ouldn['’]t"),
			Pattern.compile("([Ss])houldn['’]t"),
			Pattern.compile("([Ii])t['’]s"),
			Pattern.compile("n['’]t "),
			Pattern.compile("['’]ve "),
			Pattern.compile("['’]re "),
	};

	private static final Pattern[] TOKPAT3 = new Pattern[] {
			Pattern.compile(" ([A-Z]) \\."),
			Pattern.compile("\\s+"),
			Pattern.compile("^\\s+"),
			Pattern.compile("\\^"),
			Pattern.compile("°"),
			Pattern.compile("…"),
			Pattern.compile("([\\w])([’'])\\s"),
			Pattern.compile("_elipsisDDD_"),

			// pop abbreviations----------------------------------
			Pattern.compile("_([Ee])([Gg])_"),//e.g.
			Pattern.compile("_([Ii])([Ee])_"),//i.e.
			Pattern.compile("_([Aa])([Mm])_"),//a.m.
			Pattern.compile("_([Pp])([Mm])_"),//p.m.
			Pattern.compile("_(Cap)_"),//Cap.
			Pattern.compile("_([Cc])_"),//c.
			Pattern.compile("_([Ee][Tt])zzz([Aa][Ll])_"),//et al.
			Pattern.compile("_(ect|ECT)_"),//ect.
			Pattern.compile("_([Pp])([Ss])dot_"),//p.s.
			Pattern.compile("_([Pp])([Ss])_"),//p.s
			Pattern.compile("_([Pp])([Hh])([Dd])_"),//Ph.D
			Pattern.compile("_([Rr])([Ii])([Pp])_"),//R.I.P
			Pattern.compile("_([Vv])([Ss]?)_"),//vs. and v.
			Pattern.compile("_([Mm])([Rr]|[Ss]|[Xx])_"),//Mr. Ms. and Mx.
			Pattern.compile("_([Dd])([Rr])_"),//Dr.
			Pattern.compile("_([Pp])([Ff])_"),//Pf.
			Pattern.compile("_([Ii])([Nn])([Dd]|[Cc])_"),//Ind. and Inc.
			Pattern.compile("_([Cc])([Oo])dcs([Ll])([Tt])([Dd])_"),//co., ltd.
			Pattern.compile("_([Cc])([Oo])ds([Ll])([Tt])([Dd])_"),//co. ltd.
			Pattern.compile("_([Cc])([Oo])dc([Ll])([Tt])([Dd])_"),//co.,ltd.
			Pattern.compile("_([Cc])([Oo])([Rr]?)([Pp]?)_"),//Co. and Corp.
			Pattern.compile("_([Ll])([Tt])([Dd])_"), // ltd.
			Pattern.compile("_(Prof|PROF|prof)_"),//Prof.
	};

	private static final String DELIM = "___";
	private static final String[] TOKREP1 = new String[] {
			"_$1$2_",
			"_$1$2_",
			"_$1$2_",
			"_$1$2_",
			"_Cap_",
			"_$1_",
			"_$1zzz$2_",
			"_$1_",
			"_$1$2dot_",
			"_$1$2_",
			"_$1$2$3_",
			"_$1$2$3_",
			"_$1$2_",
			"_$1$2_",
			"_$1$2_",
			"_$1$2_",
			"_$1$2$3_",
			"_$1$2dcs$3$4$5_",
			"_$1$2ds$3$4$5_",
			"_$1$2dc$3$4$5_",
			"_$1$2$3$4_",
			"_$1$2$3_",
			"_$1_",
			"_elipsisDDD_",
			" $1 ",
			" ",
			" , $1",
			"$1 $2$3 ",
			" $1 ",
			" -- ",
			" ",
			" ",
			"$1 \' ",
			" \u2018 ",
			" \'$1 ",
	};

	private static final String[] TOKREP2 = new String[] {
			"$1an not",
			"$1id not",
			"$1ould not",
			"$1hould not",
			" $1t is",
			" not ",
			" have ",
			" are ",
	};

	private static final String[] TOKREP3 = new String[] {
			" $1. ",
			" ",
			"",
			" ^ ",
			" ° ",
			" … ",
			"$1 $2 ",
			" ... ",
			// restore abbreviations--------------------------
			"$1.$2.",// E.G.
			"$1.$2.",// I.E.
			"$1.$2.",// a.m.
			"$1.$2.",// p.m.
			"Cap.", // Cap.
			"$1.",// c.
			"$1_$2.",// et al.
			"$1.",// ect.
			"$1.$2.",// p.s.
			"$1.$2",// p.s
			"$1$2.$3",// Ph.D
			"$1.$2.$3",// R.I.P
			"$1$2.",// vs. and v.
			"$1$2.",// Mr. Ms. and Mx.
			"$1$2.",// Dr.
			"$1$2.",// Pf.
			"$1$2$3.",// Ind. and Inc.
			"$1$2.,_$3$4$5.", // co., ltd.
			"$1$2._$3$4$5.",// co. ltd.
			"$1$2.,$3$4$5.", // co.,ltd.
			"$1$2$3$4.",// Co. and Corp.
			"$1$2$3.",// ltd.
			"$1.", // Prof.
	};

	private static final Pattern LINEBREAKS = Pattern.compile("(\r?\n)+");

	private static final Pattern HTML_TAGS_RE = Pattern.compile(
			"(<\\/?[a-z][a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]*\\/?>|<!DOCTYPE[^>]*>|<!--[^>-]*-->)", Pattern.CASE_INSENSITIVE);

	private static final Pattern[] UNTOKENIZE_HTMLTAG_RE = new Pattern[] {
			Pattern.compile(" <([a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]+)\\/> ", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<([a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]+)> ", Pattern.CASE_INSENSITIVE),
			Pattern.compile(" <\\/([a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]+)>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("< *(! *DOCTYPE)([^>]*)>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<! *--([^->]*)-->", Pattern.CASE_INSENSITIVE),
	};

	static {
		if (TOKPAT1.length != TOKREP1.length
				|| TOKPAT2.length != TOKREP2.length
				|| TOKPAT3.length != TOKREP3.length) {
			throw new RiTaException("Invalid Tokenizer");
		}
	}
}
