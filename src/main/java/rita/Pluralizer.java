package rita;

import java.util.Arrays;

public class Pluralizer
{
	private static final RE NULL_PLURALS = new RE( // these don't change for plural/singular
			"^(bantu|bengalese|bengali|beninese|boche|bonsai|booze|cellulose|digitalis|mess|moose|" + "burmese|chinese|colossus|congolese|discus|electrolysis|emphasis|expertise|finess|flu|fructose|gabonese|gauze|glucose|grease|guyanese|haze|incense|japanese|javanese|journalese|" + "lebanese|malaise|manganese|mayonnaise|maltese|menopause|merchandise|nitrocellulose|olympics|overuse|paradise|poise|polymerase|portuguese|prose|recompense|remorse|repose|senegalese|siamese|singhalese|innings|" + "sleaze|sinhalese|sioux|sudanese|suspense|swiss|taiwanese|togolese|vietnamese|unease|aircraft|anise|antifreeze|applause|archdiocese|" + "anopheles|apparatus|asparagus|barracks|bellows|bison|bluefish|bob|bourgeois|" + "bream|brill|butterfingers|cargo|carp|catfish|chassis|clothes|chub|cod|codfish|" + "coley|contretemps|corps|crawfish|crayfish|crossroads|cuttlefish|dace|deer|dice|" + "dogfish|doings|dory|downstairs|eldest|earnings|economics|electronics|" + "firstborn|fish|flatfish|flounder|fowl|fry|fries|works|globefish|goldfish|golf|" + "grand|grief|gudgeon|gulden|haddock|hake|halibut|headquarters|herring|hertz|horsepower|" + "goods|hovercraft|hundredweight|ironworks|jackanapes|kilohertz|kurus|kwacha|ling|lungfish|" + "mackerel|macaroni|means|megahertz|moorfowl|moorgame|mullet|nepalese|offspring|pampas|parr|pants|" + "patois|pekinese|penn'orth|perch|pickerel|pike|pince-nez|plaice|potpourri|precis|quid|rand|" + "rendezvous|revers|roach|roux|salmon|samurai|series|seychelles|seychellois|shad|" + "sheep|shellfish|smelt|spaghetti|spacecraft|species|starfish|stockfish|sunfish|superficies|" + "sweepstakes|swordfish|tench|tennis|[a-z]+osis|[a-z]+itis|[a-z]+ness|" + "tobacco|tope|triceps|trout|tuna|tunafish|tunny|turbot|trousers|turf|dibs|" + "undersigned|veg|waterfowl|waterworks|waxworks|whiting|wildfowl|woodworm|" + "yen|aries|pisces|forceps|lieder|jeans|physics|mathematics|news|odds|politics|remains|" + "acoustics|aesthetics|aquatics|basics|ceramics|classics|cosmetics|dialectics|dynamics|ethics|harmonics|heroics|mechanics|metrics|optics|physics|polemics|pyrotechnics|" + "surroundings|thanks|statistics|goods|aids|wildlife)$", 0);

	private static final RE[] SINGULAR_RULES = {
			NULL_PLURALS,
			new RE("ves$", 3, "f"),
			new RE("(men|women)$", 2, "an"),
			new RE("(houses|horses|cases)$", 1), //End with: e -> es
			new RE("^(toes|wheezes|oozes|uses)$", 1), //Word: e -> es
			new RE("^(whizzes)$", 3),
			new RE("^(octopus|pinch)es$", 2),
			// new RE("^[lm]ice$", 3, "ousea_"),
			new RE("^(meninges|phalanges)$", 3, "x"), // x -> ges
			new RE("^(curi|formul|vertebr|larv|uln|alumn|signor|alg|minuti)ae$", 1), // ?
			new RE("^(apices|cortices)$", 4, "ex")
	};

	private static final RE[] PLURAL_RULES = {
			new RE("prognosis", 2, "es"),
			NULL_PLURALS,
			new RE("(human|german|roman)$", 0, "s"),
			new RE("^(monarch|loch|stomach)$", 0, "s"),
			new RE("^(piano|photo|solo|ego|tobacco|cargo|taxi)$", 0, "s"),
			new RE("(chief|proof|ref|relief|roof|belief|sheaf|spoof|golf|grief)$", 0, "s"),
			new RE("^(wildlife)$", 0, "s"),
			new RE("^(appendix|index|matrix|apex|cortex)", 2, "ices"),
			new RE("^concerto$", 1, "i"),
			new RE("[bcdfghjklmnpqrstvwxyz]o$", 0, "es"),
			new RE("[bcdfghjklmnpqrstvwxyz]y$", 1, "ies"),
			new RE("^ox$", 0, "en"),
			new RE("^(stimul|alumn|termin)us$", 2, "i"),
			new RE("^corpus$", 2, "ora"),
			new RE("(xis|sis)$", 2, "es"),
			new RE("whiz$", 0, "zes"),
			new RE("([zsx]|ch|sh)$", 0, "es"),
			new RE("[lraeiou]fe$", 2, "ves"),
			new RE("[lraeiou]f$", 1, "ves"),
			new RE("(eu|eau)$", 0, "x"),
			new RE("(man|woman)$", 2, "en"),
			new RE("money$", 2, "ies"),
			new RE("person$", 4, "ople"),
			new RE("motif$", 0, "s"),
			new RE("^meninx|phalanx$", 1, "ges"),
			new RE("schema$", 0, "ta"),
			new RE("^bus$", 0, "ses"),
			new RE("child$", 0, "ren"),
			new RE("^(curi|formul|vertebr|larv|uln|alumn|signor|alg|minuti)a$", 0, "e"),
			new RE("^(maharaj|raj|myn|mull)a$", 0, "hs"),
			new RE("^aide-de-camp$", 8, "s-de-camp"),
			new RE("^weltanschauung$", 0, "en"),
			new RE("^lied$", 0, "er"),
			new RE("^tooth$", 4, "eeth"),
			new RE("^[lm]ouse$", 4, "ice"),
			new RE("^foot$", 3, "eet"),
			new RE("femur", 2, "ora"),
			new RE("goose", 4, "eese"),
			new RE("^(co|no)$", 0, "'s"),
			new RE("^blond$", 0, "es"),
			new RE("^(medi|millenni|consorti|sept|memorabili)um$", 2, "a"),
			// Latin stems
			new RE("^(memorandum|bacterium|curriculum|minimum|" + "maximum|referendum|spectrum|phenomenon|criterion)$", 2, "a")
	};

	private static final RE DEFAULT_PLURAL_RULE = new RE("^((\\w+)(-\\w+)*)(\\s((\\w+)(-\\w+)*))*$", 0, "s");

	private static final String[] MODALS = {"shall", "would", "may", "might", "ought", "should"};
	
	public String pluralize(String word)
	{

		if (word == null || word.length() == 0 ) return "";

		if (Arrays.stream(MODALS).anyMatch(word.toLowerCase()::equals)) return word;

		RE[] rules = PLURAL_RULES; //TODO
		for (int i = 0; i < rules.length; i++) {
			if (rules[i].applies(word.toLowerCase())) {
				return rules[i].fire(word);
			}
		}

		return DEFAULT_PLURAL_RULE.fire(word);
	}

	public String singularize(String word)
	{

		if (word == null || word.length() == 0 ) return "";

		if (Arrays.stream(MODALS).anyMatch(word.toLowerCase()::equals)) return word;

		RE[] rules = SINGULAR_RULES; //TODO
		int i = rules.length;

		while (i-- > 0) {
			if (rules[i].applies(word.toLowerCase())) {
				return rules[i].fire(word);
			}

		}

		return RiTa.stem(word);
	}

	public boolean isPlural(String word) {
		// TODO Auto-generated method stub
		return false;
	}



}
