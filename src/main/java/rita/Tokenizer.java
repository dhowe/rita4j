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
		return tokenize(words, RiTa.opts());
	}

	public static String untokenize(String[] arr) {
		return untokenize(arr, " ");
	}

	public static String[] tokens(String text) {
		return tokens(text, null);
	}
	
	public static String[] tokens(String text, String regex) {
    // opts: {includePunct, caseSensitive, sort, ignoreStopWords} ?
    String[] words = tokenize(text, regex);
    Set<String> tokens = new HashSet<String>();
    for (int i = 0; i < words.length; i++) {
			if (RE.test(ALPHA_RE, words[i])) {
				tokens.add(words[i].toLowerCase());
			}
		}
    // TODO: includePunct, caseSensitive, ignoreStopWords
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
		arr = preProcessTags(arr);

		boolean thisNBPunct, lastNBPunct, lastNAPunct, thisQuote;
		boolean lastQuote, thisComma, isLast, lastComma, lastEndWithS;
		boolean nextIsS, thisLBracket, thisRBracket, lastLBracket;
		boolean thisLineBreak;
		boolean lastRBracket, lastIsWWW, thisDomin, withinQuote = false;
		boolean afterQuote = false, midSentence = false, nextNoSpace = false;

		if (arr == null || arr.length == 0) return "";

		if (arr.length > 0) withinQuote = QUOTES.matcher(arr[0]).matches();

		String result = arr[0]; // start with first token

		for (int i = 1; i < arr.length; i++) {

			if (arr[i] == null) continue;

			thisComma = arr[i].equals(",");
			thisNBPunct = NB_PUNCT.matcher(arr[i]).matches() || UNTOKENIZE_HTMLTAG_RE[2].matcher(arr[i]).matches() || LINEBREAKS_RE.matcher(arr[i]).matches();
			//thisNAPunct = NA_PUNCT.matcher(arr[i]).matches();
			thisQuote = QUOTES.matcher(arr[i]).matches();
			thisLBracket = LBRACKS.matcher(arr[i]).matches();
			thisRBracket = RBRACKS.matcher(arr[i]).matches();
			thisDomin = DOMIN.matcher(arr[i]).matches();
			lastComma = arr[i - 1].equals(",");
			lastNBPunct = NB_PUNCT.matcher(arr[i - 1]).matches() || LINEBREAKS_RE.matcher(arr[i - 1]).matches();
			lastNAPunct = NA_PUNCT.matcher(arr[i - 1]).matches() || UNTOKENIZE_HTMLTAG_RE[1].matcher(arr[i - 1]).matches() || LINEBREAKS_RE.matcher(arr[i - 1]).matches();
			lastQuote = QUOTES.matcher(arr[i - 1]).matches();
			lastLBracket = LBRACKS.matcher(arr[i - 1]).matches();
			lastRBracket = RBRACKS.matcher(arr[i - 1]).matches();
			lastEndWithS = arr[i - 1].charAt(arr[i - 1].length() - 1) == 's'
					&& !arr[i - 1].equals("is") && !arr[i - 1].equals("Is") && !arr[i - 1].equals("IS");
			lastIsWWW = WWW.matcher(arr[i - 1]).matches();
			nextIsS = i == arr.length - 1 ? false : (arr[i + 1].equals("s") || arr[i + 1].equals("S"));
			isLast = (i == arr.length - 1);
			thisLineBreak = LINEBREAKS_RE.matcher(arr[i]).matches();

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
					|| (!isLast && thisNBPunct && lastNBPunct && !lastNAPunct && !lastQuote && !lastLBracket && !thisRBracket && !thisLineBreak)) {
				result += delim;
			}

			result += arr[i]; // add to result

			if (thisNBPunct && !lastNBPunct && !withinQuote
					&& SQUOTES.matcher(arr[i]).matches() && lastEndWithS) {
				result += delim; // fix to #477
			}
		}

		return result.trim();
	}

	public static String[] tokenize(String words, String regex) {
		return tokenize(words, RiTa.opts("regex", regex));
	}

	public static String[] tokenize(String words, Map<String,Object> opts) {

		if (words == null) return new String[0];
		if (words.length() == 0) return new String[] { "" };

		String regex = Util.strOpt("regex", opts, null);
		// handle a regex argument
		if (regex != null) return words.split(regex);

		boolean spl_bc = RiTa.SPLIT_CONTRACTIONS;
		if (Util.boolOpt("splitContractions", opts, false)) RiTa.SPLIT_CONTRACTIONS = true;

		words = words.trim();

		@SuppressWarnings("unchecked")
		ArrayList<String> htmlTags = (ArrayList<String>) pushTags(words).get(0);
		words = (String) pushTags(words).get(1);

		for (int i = 0; i < TOKPAT1.length; i++) {
			words = TOKPAT1[i].matcher(words)
					.replaceAll(TOKREP1[i]);
		}

		if (!Util.boolOpt("keepHyphen", opts,false)) {
			words = HYPHEN_RE.matcher(words).replaceAll("$1 - ");
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
		RiTa.SPLIT_CONTRACTIONS = spl_bc;
		words = words.trim();
		String[] result = words.split(" +");
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

	private static String[] preProcessTags(String[] array) {
		ArrayList<String> result = new ArrayList<String>();
		int currentIdx = 0;
		while (currentIdx < array.length) {
			String currentToken = array[currentIdx];
			if (!LT_RE.matcher(currentToken).matches()) {
				result.add(currentToken);
				currentIdx++;
				continue;
			}
			ArrayList<String> subArray = new ArrayList<String>();
			subArray.add(array[currentIdx]);
			int inspectIdx = currentIdx + 1;
			while (inspectIdx < array.length) {
				subArray.add(array[inspectIdx]);
				if (LT_RE.matcher(array[inspectIdx]).matches())
					break;
				if (GT_RE.matcher(array[inspectIdx]).matches())
					break;
				inspectIdx++;
			}
			if (LT_RE.matcher(subArray.get(subArray.size() - 1)).matches()) {
				subArray.remove(subArray.size() - 1);
				result.addAll(subArray);
				currentIdx = inspectIdx;
				continue;
			}
			if (!GT_RE.matcher(subArray.get(subArray.size() - 1)).matches()) {
				result.addAll(subArray);
				currentIdx = inspectIdx + 1;
				continue;
			}
			if (!HTML_TAGS_RE.matcher(String.join("", subArray)).matches()) {
				result.addAll(subArray);
				currentIdx = inspectIdx + 1;
				continue;
			}
			String tag = tagSubarrayToString(subArray.toArray(new String[] {}));
			result.add(tag);
			currentIdx = inspectIdx + 1;
		}
		return result.toArray(new String[] {});
	}

	private static String tagSubarrayToString(String[] array) {
		String start = "";
		String end = "";
		start += array[0].trim();
		end = array[array.length - 1].trim() + end;
		int inspectIdx = 1;
		while (inspectIdx < array.length - 1 && TAGSTART_RE.matcher(array[inspectIdx]).matches()) {
			start += array[inspectIdx].trim();
			inspectIdx++;
		}
		int contentStartIdx = inspectIdx;
		inspectIdx = array.length - 2;
		while (inspectIdx > contentStartIdx && TAGEND_RE.matcher(array[inspectIdx]).matches()) {
			end = array[inspectIdx].trim() + end;
			inspectIdx--;
		}
		int contentEndIdx = inspectIdx;
		String[] contentArray = Arrays.copyOfRange(array, contentStartIdx, contentEndIdx + 1);
		return start + untokenize(contentArray) + end;
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
	private static final Pattern LINEBREAKS_RE = Pattern.compile("([\r\n\u001e]|\r\n|\n\r)");

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
			Pattern.compile("([\\-]?[0-9]+)\\.([0-9]+)"), //(-)27.3
			Pattern.compile("([\\-]?[0-9]+)\\.([0-9]+)e([\\-]?[0-9]+)"), //(-)1.2e10
			Pattern.compile("([0-9]{3}),([0-9]{3})"), // large numbers like 200,200
			Pattern.compile("\r\n"), // CR LF
			Pattern.compile("\n\r"), // LF CR
			Pattern.compile("\n"), // LF
			Pattern.compile("\r"), // CR
			Pattern.compile("\u001e"), // RS
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
			Pattern.compile("([tT]hat)['’]s"),
			Pattern.compile("(she|he|you|they|i)['’]d", Pattern.CASE_INSENSITIVE),
			Pattern.compile("(she|he|you|they|i)['’]ll", Pattern.CASE_INSENSITIVE),
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
			Pattern.compile("([\\-]?[0-9]+)DECIMALDOT([0-9]+)_"), //(-)27.3
			Pattern.compile("_([\\-][0-9]+)DECIMALDOT([0-9]+)POWERE([\\-]?[0-9]+)_"), //(-)1.2e10
			Pattern.compile("_DECIMALCOMMA_"), // large numbers like 200,000
			Pattern.compile("_LINEFEED_"), // LF
			Pattern.compile("_CARRIAGERETURN_"), // CR
			Pattern.compile("_CARRIAGERETURNLINEFEED_"), // CR LF
			Pattern.compile("_LINEFEEDCARRIAGERETURN_"), // LF CR
			Pattern.compile("_RECORDSEPARATOR_"), // RS
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
			"$1DECIMALDOT$2_",
			"_$1DECIMALDOT$2POWERE$3_",
			"$1_DECIMALCOMMA_$2",
			" _CARRIAGERETURNLINEFEED_ ",
			" _LINEFEEDCARRIAGERETURN_ ",
			" _LINEFEED_ ",
			" _CARRIAGERETURN_ ",
			" _RECORDSEPARATOR_ ",
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
			"$1 is",
			"$1 would",
			"$1 will",
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
			"$1.$2", // (-)27.3
			"$1.$2e$3", // (-)1.2e10
			",", // 200,000
			"\n",
			"\r",
			"\r\n",
			"\n\r",
			"\u001e",
	};

	private static final Pattern LINEBREAKS = Pattern.compile("(\r?\n)+");

	private static final Pattern HTML_TAGS_RE = Pattern.compile(
			"(<\\/?[a-z][a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]*\\/?>|<!DOCTYPE[^>]*>|<!--[^>-]*-->)", Pattern.CASE_INSENSITIVE);

	private static final Pattern[] UNTOKENIZE_HTMLTAG_RE = new Pattern[] {
			Pattern.compile("^ *<[a-z][a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]*\\/> *$>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("^ *<([a-z][a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]*[a-z0-9='\"#;:&\\s\\-\\+\\.\\?]|[a-z])> *$", Pattern.CASE_INSENSITIVE),
			Pattern.compile("^ *<\\/[a-z][a-z0-9='\"#;:&\\s\\-\\+\\/\\.\\?]*> *$", Pattern.CASE_INSENSITIVE),
			Pattern.compile("^ *<!DOCTYPE[^>]*> *$", Pattern.CASE_INSENSITIVE),
			Pattern.compile("^ *<!--[^->]*--> *$", Pattern.CASE_INSENSITIVE),
	};

	private static final Pattern LT_RE = Pattern.compile("^ *< *$");
	private static final Pattern GT_RE = Pattern.compile("^ *> *$");
	private static final Pattern TAGSTART_RE = Pattern.compile("^ *[!\\-\\/] *$");
	private static final Pattern TAGEND_RE = Pattern.compile("^ *[\\-\\/] *$");
	private static final Pattern HYPHEN_RE = Pattern.compile("(\\w+)-(?=(\\w+))");

	static {
		if (TOKPAT1.length != TOKREP1.length
				|| TOKPAT2.length != TOKREP2.length
				|| TOKPAT3.length != TOKREP3.length) {
			throw new RiTaException("Invalid Tokenizer");
		}
	}
}
