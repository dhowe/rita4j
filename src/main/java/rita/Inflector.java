package rita;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class Inflector {

	private static final String[] MASS_NOUNS = Util.MASS_NOUNS;
	private static final int SINGULARIZE = 1, PLURALIZE = 2;
	private static final Pattern DEFAULT_IS_PLURAL = Pattern.compile("(ae|ia|s)$");
	private static final RE DEFAULT_SINGULAR_RULE = new RE("^.*s$", 1);
	private static final RE DEFAULT_PLURAL_RULE = new RE("^((\\w+)(-\\w+)*)(\\s((\\w+)(-\\w+)*))*$", 0, "s");
	private static final RE[] SINGULAR_RULES = {
			new RE("^(apices|cortices)$", 4, "ex"),
			new RE("^(meninges|phalanges)$", 3, "x"), // x -> ges
			new RE("^(octopus|pinch)es$", 2),
			new RE("^(whizzes)$", 3),
			new RE("^(tomatoes|kisses)$", 2),
			new RE("^(to|wheez|ooz|us|enterpris|alcov|hous|hors|cas|daz|hiv|div|additiv)es$", 1), //End with: es -> e
			new RE("(l|w)ives$", 3, "fe"),
			new RE("(men|women)$", 2, "an"),
			new RE("ves$", 3, "f"),
			new RE("^(appendices|matrices)$", 3, "x"),
			new RE("^(indices|apices|cortices)$", 4, "ex"),
			new RE("^(gas|bus)es$", 2),
			new RE("([a-z]+osis|[a-z]+itis|[a-z]+ness)$", 0),
			new RE("^(stimul|alumn|termin)i$", 1, "us"),
			new RE("^(media|millennia|consortia|septa|memorabilia|data)$", 1, "um"),
			new RE("^(memoranda|bacteria|curricula|minima|maxima|referenda|spectra|phenomena|criteria)$", 1, "um"), // Latin stems
			new RE("ora$", 3, "us"),
			new RE("^[lm]ice$", 3, "ouse"),
			new RE("[bcdfghjklmnpqrstvwxyz]ies$", 3, "y"),
			new RE("(ces)$", 1), // accomplices
			new RE("^feet$", 3, "oot"),
			new RE("^teeth$", 4, "ooth"),
			new RE("children$", 3),
			new RE("geese", 4, "oose"),
			new RE("^concerti$", 1, "o"),
			new RE("people$", 4, "rson"),
			new RE("^(vertebr|larv|minuti)ae$", 1),
			new RE("^oxen", 2),
			new RE("esses$", 2),
			new RE("(treatises|chemises)$", 1),
			new RE("(ses)$", 2, "is"), // catharses, prognoses
			//  new RE("([a-z]+osis|[a-z]+itis|[a-z]+ness)$", 0),
			DEFAULT_SINGULAR_RULE
	};

	private static final RE[] PLURAL_RULES = {
			new RE("(human|german|roman)$", 0, "s"),
			new RE("^(monarch|loch|stomach|epoch|ranch)$", 0, "s"),
			new RE("^(piano|photo|solo|ego|tobacco|cargo|taxi)$", 0, "s"),
			new RE("(chief|proof|ref|relief|roof|belief|spoof|golf|grief)$", 0, "s"),
			new RE("^(appendix|index|matrix|apex|cortex)$", 2, "ices"), 
			new RE("^concerto$", 1, "i"), 
			new RE("^prognosis", 2, "es"),
			new RE("[bcdfghjklmnpqrstvwxyz]o$", 0, "es"), 
			new RE("[bcdfghjklmnpqrstvwxyz]y$", 1, "ies"), 
			new RE("^ox$", 0, "en"),
			new RE("^(stimul|alumn|termin)us$", 2, "i"), 
			new RE("^corpus$", 2, "ora"), 
			new RE("(xis|sis)$", 2, "es"),
			//new RE("(ness)$", 0, "es"),
			new RE("whiz$", 0, "zes"), 
			new RE("motif$", 0, "s"), 
			new RE("[lraeiou]fe$", 2, "ves"), 
			new RE("[lraeiou]f$", 1, "ves"),
			new RE("(eu|eau)$", 0, "x"), 
			new RE("(man|woman)$", 2, "en"), 
			new RE("person$", 4, "ople"), 
			new RE("^meninx|phalanx$", 1, "ges"),
			new RE("schema$", 0, "ta"), 
			new RE("^(bus|gas)$", 0, "es"), 
			new RE("child$", 0, "ren"),
			new RE("^(vertebr|larv|minuti)a$", 0, "e"), 
			new RE("^(maharaj|raj|myn|mull)a$", 0, "hs"), 
			new RE("^aide-de-camp$", 8, "s-de-camp"),
			new RE("^weltanschauung$", 0, "en"), 
			new RE("^lied$", 0, "er"), 
			new RE("^tooth$", 4, "eeth"), 
			new RE("^[lm]ouse$", 4, "ice"),
			new RE("^foot$", 3, "eet"), 
			new RE("goose", 4, "eese"), 
			new RE("^(co|no)$", 0, "'s"), 
			new RE("^blond$", 0, "es"), 
			new RE("^datum", 2, "a"),
			new RE("([a-z]+osis|[a-z]+itis|[a-z]+ness)$", 0), 
			new RE("([zsx]|ch|sh)$", 0, "es"), // note: words ending in 's' otfen hit here, add 'es'
			new RE("^(medi|millenni|consorti|sept|memorabili)um$", 2, "a"),
			new RE("^(memorandum|bacterium|curriculum|minimum|maximum|referendum|spectrum|phenomenon|criterion)$", 2, "a"), // Latin stems
			DEFAULT_PLURAL_RULE
	};

	private static final String adjustNumber(String word, int type, boolean dbug) {

		if (word == null || word.length() < 1) return "";

		String check = word.toLowerCase();

		if (Arrays.asList(MASS_NOUNS).contains(check)) {
			if (dbug) console.log(word + " hit MASS_NOUNS");
			return word;
		}

		RE[] rules = type == SINGULARIZE ? SINGULAR_RULES : PLURAL_RULES;
		for (int i = 0; i < rules.length; i++) {
			RE rule = rules[i];
			if (rule.applies(check)) {
				if (dbug) console.log(word + " hit -> " + rule);
				return rules[i].fire(word);
			}
		}

		return word;
	}

	public static final String singularize(String word) {
		return singularize(word, null);
	}
	
	public static final String singularize(String word, Map<String, Object> opts) {
		return adjustNumber(word, SINGULARIZE, Util.boolOpt("dbug", opts));
	}

	public static final String pluralize(String word) {
		return pluralize(word, null);
	}
	
	public static final String pluralize(String word, Map<String, Object> opts) {
		return adjustNumber(word, PLURALIZE, Util.boolOpt("dbug", opts));
	}

	public static final boolean isPlural(String word) {
		return isPlural(word, false);
	}

	public static final boolean isPlural(String word, boolean dbug) {

		if (word == null || word.length() < 1) return false;

		word = word.toLowerCase();

		if (Arrays.asList(MASS_NOUNS).contains(word)) {
			return true;
		}

		String sing = RiTa.singularize(word);
		Map<String, String[]> dict = RiTa._lexicon().dict;
		String[] pos, data = dict.get(sing);

		// Is singularized form is in lexicon as 'nn'?
		if (data != null && data.length == 2) {
			pos = data[1].split(" ");
			if (Arrays.asList(pos).contains("nn")) return true;
		}

		// A general modal form? (for example, ends in 'ness')
		if (word.endsWith("ness") && sing.equals(RiTa.pluralize(word))) {
			return true;
		}

		// Is word without final 's in lexicon as 'nn'?
		if (word.endsWith("s")) {
			data = dict.get(word.substring(0, word.length() - 1));
			if (data != null && data.length == 2) {
				pos = data[1].split(" ");
				for (int i = 0; i < pos.length; i++) {
					if (Arrays.asList(pos).contains("nn")) return true;
				}
			}
		}

		if (RE.test(DEFAULT_IS_PLURAL, word)) return true;

		RE[] rules = SINGULAR_RULES;
		for (int i = 0; i < rules.length; i++) {
			RE rule = rules[i];
			if (rule.applies(word)) {
				if (dbug) console.log(word + " hit -> " + rule);
				return true;
			}
		}

		if (dbug) console.log("isPlural: no rules hit for '" + word + "'");

		return false;
	}
}
