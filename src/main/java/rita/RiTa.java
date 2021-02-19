package rita;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class RiTa {

	public static Tagger tagger = new Tagger();
	public static Analyzer analyzer = new Analyzer();
	public static Concorder concorder = new Concorder();

	public static Map<String, Function<String, String>> addTransform(
			String name, Function<String, String> func) {
		if (func != null) {
			RiScript.transforms.put(name, func);
		}
		else {
			RiScript.transforms.remove(name);
		}
		return RiScript.transforms;
	}

	public static String[] alliterations(String word) {
		return alliterations(word, 0);
	}

	public static String[] alliterations(String word, int minWordLength) {
		return alliterations(word, opts("minLength", minWordLength));
	}

	public static String[] alliterations(String word, Map<String, Object> opts) {
		return lexicon().alliterations(word, opts);
	}

	public static Map<String, String> analyze(String word) {
		return analyzer.analyze(word);
	}

	public static String articlize(String s) {
		return RiScript.articlize(s);
	}

	public static Map<String, Integer> concordance(String text) {
		return concordance(text, null);
	}

	public static Map<String, Integer> concordance(String text, Map<String, Object> opts) {
		return concorder.concordance(text, opts);
	}

	public static String conjugate(String word, Map<String, Object> opts) {
		return Conjugator.conjugate(word, opts);
	}

	public static String conjugate(String word) {
		return conjugate(word, null);
	}

	public static boolean hasWord(String word) {
		return lexicon().hasWord(word);
	}

	public static boolean isAbbrev(String input) {
		return isAbbrev(input, false);
	}

	public static boolean isAbbrev(String input, Map<String, Object> opts) {
		return isAbbrev(input, Util.boolOpt("ignoreCase", opts));
	}

	public static boolean isAbbrev(String input, boolean ignoreCase) {
		if (input == null) return false;
		if (ignoreCase) input = input.substring(0, 1).toUpperCase() + input.substring(1);
		return Arrays.stream(ABRV).anyMatch(input::equals);
	}

	public static boolean isAdjective(String word) {
		return tagger.isAdjective(word);
	}

	public static boolean isAdverb(String word) {
		return tagger.isAdverb(word);
	}

	public static boolean isVowel(char c) {
		return RiTa.VOWELS.indexOf(c) > -1;
	}

	public static boolean isVowel(String c) {
		return RiTa.VOWELS.indexOf(c) > -1;
	}

	public static boolean isConsonant(char c) {
		return isConsonant(Character.toString(c));
	}

	public static boolean isConsonant(String c) {
		return RiTa.VOWELS.indexOf(c) < 0 && // TODO: precompile
				"^[a-z\u00C0-\u00ff]+$".matches(c);
	}

	public static boolean isAlliteration(String word1, String word2) {
		return lexicon().isAlliteration(word1, word2, false);
	}

	public static boolean isAlliteration(String word1, String word2, boolean noLTS) {
		return lexicon().isAlliteration(word1, word2, noLTS);
	}

	public static boolean isNoun(String word) {
		return tagger.isNoun(word);
	}

	public static boolean isPunct(String text) {
		return text != null && text.length() > 0 && ONLY_PUNCT.matcher(text).matches();
	}

	public static boolean isPunct(char c) {
		return isPunct(Character.toString(c));
	}

	public static boolean isQuestion(String sentence) { // remove?
		return Arrays.stream(QUESTIONS).anyMatch(tokenize(sentence)[0].toLowerCase()::equals);
	}

	public static boolean isRhyme(String word1, String word2) {
		return lexicon().isRhyme(word1, word2, false);
	}

	public static boolean isRhyme(String word1, String word2, boolean noLTS) {
		return lexicon().isRhyme(word1, word2, noLTS);
	}

	public static boolean isStopWord(String word) {
		return Arrays.asList(RiTa.STOP_WORDS).contains(word.toLowerCase());
	}

	public static boolean isVerb(String word) {
		return tagger.isVerb(word);
	}

	public static String[] kwic(String word) {
		return kwic(word, null);
	}

	public static String[] kwic(String word, int numWords) {
		if (concorder == null) concorder = new Concorder();
		return concorder.kwic(word, numWords);
	}

	public static String[] kwic(String word, Map<String, Object> opts) {
		if (concorder == null) concorder = new Concorder();
		return concorder.kwic(word, opts);
	}

	public static String pastPart(String verb) {
		return Conjugator.pastPart(verb);
	}

	public static String phones(String text) {
		return RiTa.phones(text, null);
	}

	public static String phones(String text, Map<String, Object> opts) {
		return analyzer.analyze(text, opts).get("phones");
	}

	public static String posInline(String text, Map<String, Object> opts) {
		return posInline(text, Util.boolOpt("simple", opts));
	}

	public static String posInline(String text) {
		return posInline(text, false);
	}

	public static String posInline(String text, boolean useSimpleTags) {
		return tagger.tagInline(text, useSimpleTags);
	}

	public static String[] pos(String text) {
		return pos(text, false);
	}

	public static String[] pos(String text, Map<String, Object> opts) {
		return pos(text, Util.boolOpt("simple", opts));
	}

	public static String[] pos(String text, boolean useSimpleTags) {
		return tagger.tag(text, useSimpleTags);
	}

	public static String[] pos(String[] text, Map<String, Object> opts) {
		return pos(text, Util.boolOpt("simple", opts));
	}

	public static String[] pos(String[] text) {
		return pos(text, false);
	}

	public static String[] pos(String[] text, boolean useSimpleTags) {
		return tagger.tag(text, useSimpleTags);
	}

	public static String pluralize(String word) {
		return pluralize(word, null);
	}

	public static String pluralize(String word, Map<String, Object> opts) {
		return Inflector.pluralize(word, opts);
	}

	public static String presentPart(String verb) {
		return Conjugator.presentPart(verb);
	}

	public static int[] randomOrdering(int num) {
		return RandGen.randomOrdering(num);
	}

	public static void randomSeed(int theSeed) {
		RandGen.seed(theSeed);
	}

	public static String randomWord() {
		return lexicon().randomWord(null);
	}

	public static String randomWord(Map<String, Object> opts) {
		return lexicon().randomWord(opts);
	}

	public static String randomWord(String pos) {
		return randomWord(opts("pos", pos));
	}

	public static String randomWord(int syllables) {
		return randomWord(opts("numSyllables", syllables));
	}

	public static String randomWord(String pos, int syllables) {
		return randomWord(opts("pos", pos, "numSyllables", syllables));
	}

	public static String[] rhymes(String word) {
		return lexicon().rhymes(word);
	}

	public static String[] rhymes(String word, Map<String, Object> opts) {
		return lexicon().rhymes(word, opts);
	}

	public static String evaluate(String word) {
		return RiTa.evaluate(word, null);
	}

	public static String evaluate(String word, Map<String, Object> opts) {
		return RiScript.eval(word, opts);
	}

	public static String evaluate(String word, Map<String, Object> ctx, Map<String, Object> opts) {
		return RiScript.eval(word, ctx, opts);
	}

	public static RiGrammar grammar() {
		return grammar((String) null);
	}

	public static RiGrammar grammar(String rules) {
		return grammar(rules, null);
	}

	public static RiGrammar grammar(Map<String, Object> rules) {
		return grammar(rules, null);
	}

	public static RiGrammar grammar(Map<String, Object> rules, Map<String, Object> context) {
		return new RiGrammar(rules, context);
	}

	public static RiGrammar grammar(String rules, Map<String, Object> context) {
		return new RiGrammar(rules, context);
	}

	public static RiMarkov markov(int n) {
		return markov(n, null);
	}

	public static RiMarkov markov(int n, Map<String, Object> options) {
		return new RiMarkov(n, options);
	}

	public static String stresses(String text) {
		return analyzer.analyze(text).get("stresses");
	}

	public static String syllables(String text) {
		return analyzer.analyze(text).get("syllables");
	}

	public static String[] soundsLike(String word) {
		return lexicon().soundsLike(word);
	}

	public static String[] spellsLike(String word) {
		return lexicon().spellsLike(word);
	}

	public static String[] search() {
		return search((String)null, null);
	}

	public static String[] search(String regex) {
		return search(regex, null);
	}
	
	public static String[] search(String regex, Map<String, Object> opts) {
		return lexicon().search(regex, opts);
	}
	
	public static String[] search(Pattern p) {
		return search(p, null);
	}
	
	public static String[] search(Pattern p, Map<String, Object> opts) {
		return lexicon().search(p, opts);
	}

	public static String[] soundsLike(String word, Map<String, Object> opts) {
		return lexicon().soundsLike(word, opts);
	}

	public static String[] spellsLike(String word, Map<String, Object> opts) {
		return lexicon().spellsLike(word, opts);
	}

	public static String singularize(String word) {
		return singularize(word, null);
	}

	public static String singularize(String word, Map<String, Object> opts) {
		return Inflector.singularize(word, opts);
	}

	public static String[] sentences(String text) {
		return sentences(text, (Pattern) null);
	}

	public static String[] sentences(String text, String regex) {
		return sentences(text, Pattern.compile(regex));
	}

	public static String[] sentences(String text, Pattern regex) {
		return Tokenizer.sentences(text, regex);
	}

	public static String stem(String word) {
		return Stemmer.stem(word);
	}

	public static String[] tokenize(String text) {
		return Tokenizer.tokenize(text);
	}

	public static String[] tokenize(String text, String regex) {
		return Tokenizer.tokenize(text, regex);
	}

	public static String untokenize(String[] words) {
		return Tokenizer.untokenize(words);
	}

	public static String untokenize(String[] words, String delim) {
		return Tokenizer.untokenize(words, delim);
	}

	public static String[] words() {
		return words((Pattern) null);
	}

	public static String[] words(Map<String, Object> opts) {
		return words(Util.strOpt("pattern", opts));
	}

	public static String[] words(String regex) {
		return words(Pattern.compile(regex));
	}

	public static String[] words(Pattern regex) {
		return lexicon().words(regex);
	}

	public static RiScript scripting() {
		return new RiScript();
	}

	// /////////////////////////// niapi /////////////////////////////////

	public static String env() {
		return "Java";
	}

	public static float random() { // niapi
		return RandGen.random();
	}

	public static float random(float max) {
		return RandGen.random(max);
	}

	public static float random(float min, float max) {
		return RandGen.random(min, max);
	}

	public static <T> T random(T[] type) {
		return (T) RandGen.randomItem(type);
	}

	public static <T> T random(Collection<T> c) {
		return (T) RandGen.randomItem(c);
	}

	public static final float random(float[] arr) {
		return RandGen.randomItem(arr);
	}

	public static final boolean random(boolean[] arr) {
		return RandGen.randomItem(arr);
	}

	public static final int random(int[] arr) {
		return RandGen.randomItem(arr);
	}

	public static final double random(double[] arr) {
		return RandGen.randomItem(arr);
	}

	public static String capitalize(String s) {
		return s == null || s.length() == 0 ? ""
				: String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
	}

	public static Lexicon lexicon() { // singleton
		if (_lexicon == null) {
			try {
				_lexicon = new Lexicon(DICT_PATH);
			} catch (Exception e) {
				throw new RiTaException("Cannot load dictionary at "
						+ DICT_PATH + " " + System.getProperty("user.dir"), e);
			}
		}
		return _lexicon;
	}

	private static Lexicon _lexicon;

	// /////////////////////////// static /////////////////////////////////

	public static boolean SILENT = false;
	public static boolean SILENCE_LTS = true;
	public static boolean SPLIT_CONTRACTIONS = false;

	public static String PHONEME_BOUNDARY = "-";
	public static String SYLLABLE_BOUNDARY = "/";
	public static String DICT_PATH = "rita_dict.js";

	// CONSTANTS
	public static final int FIRST = 1;
	public static final int SECOND = 2;
	public static final int THIRD = 3;
	public static final int PAST = 4;
	public static final int PRESENT = 5;
	public static final int FUTURE = 6;
	public static final int SINGULAR = 7;
	public static final int PLURAL = 8;
	public static final int NORMAL = 9;
	public static final int INFINITIVE = 1;
	public static final int GERUND = 2;
	//	public static final int IMPERATIVE = 3;
	//	public static final int BARE_INFINITIVE = 4;
	//	public static final int SUBJUNCTIVE = 5;

	public static final char STRESS = '1', NOSTRESS = '0';
	public static final String VOWELS = "aeiou";
	public static final String VERSION = "2";

	public static final Pattern ONLY_PUNCT = Pattern.compile("^[\\p{Punct}|\ufffd]*$");
	public /*tmp,for testing*/ static final String DYN = "$$";
	static final String LP = "(", RP = ")", BN = "\n";
	static final String DOT = ".", SYM = "$", EQ = "=", EOF = "<EOF>";
	//static final String VSYM = "[" + SYM + DYN + "]\\w+", FUNC = LP + RP;
	static final String VSYM = "\\${1,2}\\w+", FUNC = LP + RP;

	public static String[] ABRV = {
			"Adm.", "Capt.", "Cmdr.", "Col.", "Dr.", "Gen.", "Gov.", "Lt.", "Maj.", "Messrs.", "Mr.", "Mrs.", "Ms.",
			"Prof.", "Rep.", "Reps.", "Rev.", "Sen.", "Sens.", "Sgt.", "Sr.", "St.", "A.k.a.", "C.f.", "I.e.", "E.g.", "Vs.", "V.", "Jan.", "Feb.",
			"Mar.", "Apr.", "Mar.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."
	};
	public static String[] QUESTIONS = {
			"was", "what", "when", "where", "which", "why", "who", "will", "would", "who", "how", "if", "is", "could", "might", "does", "are", "have"
	};
	public static String[] STOP_WORDS = {
			"and", "a", "of", "in", "i", "you", "is", "to",
			"that", "it", "for", "on", "have", "with",
			"this", "be", "not", "are", "as", "was", "but", "or", "from",
			"my", "at", "if", "they", "your", "all", "he", "by", "one",
			"me", "what", "so", "can", "will", "do", "an", "about", "we", "just",
			"would", "there", "no", "like", "out", "his", "has", "up", "more", "who",
			"when", "don't", "some", "had", "them", "any", "their", "it's", "only",
			"which", "i'm", "been", "other", "were", "how", "then", "now",
			"her", "than", "she", "well", "also", "us", "very", "because",
			"am", "here", "could", "even", "him", "into", "our", "much",
			"too", "did", "should", "over", "want", "these", "may", "where", "most",
			"many", "those", "does", "why", "please", "off", "going", "its", "i've",
			"down", "that's", "can't", "you're", "didn't", "another", "around",
			"must", "few", "doesn't", "the", "every", "yes", "each", "maybe",
			"i'll", "away", "doing", "oh", "else", "isn't", "he's", "there's", "hi",
			"won't", "ok", "they're", "yeah", "mine", "we're", "what's", "shall",
			"she's", "hello", "okay", "here's", "less"
	};

	public static String[] PHONES = { "aa", "ae", "ah", "ao", "aw", "ay", "b", "ch", "d", "dh", "eh", "er", "ey", "f", "g", "hh", "ih", "iy", "jh", "k",
			"l", "m", "n", "ng", "ow", "oy", "p", "r", "s", "sh", "t", "th", "uh", "uw", "v", "w", "y", "z", "zh" };

	public static final Map<String, Object> opts() {
		return new HashMap<String, Object>();
	}

	public static final Map<String, Object> opts(String key, Object val) {
		return opts(new String[] { key }, new Object[] { val });
	}

	public static final Map<String, Object> opts(String key1, Object val1, String key2, Object val2) {
		return opts(new String[] { key1, key2 }, new Object[] { val1, val2 });
	}

	public static final Map<String, Object> opts(String key1, Object val1, String key2, Object val2, String key3, Object val3) {
		return opts(new String[] { key1, key2, key3 }, new Object[] { val1, val2, val3 });
	}

	public static final Map<String, Object> opts(String key1, Object val1,
			String key2, Object val2, String key3, Object val3, String key4, Object val4) {
		return opts(new String[] { key1, key2, key3, key4 }, new Object[] { val1, val2, val3, val4 });
	}

	public static final Map<String, Object> opts(String key1, Object val1,
			String key2, Object val2, String key3, Object val3, String key4,
			Object val4, String key5, Object val5) {
		return opts(
				new String[] { key1, key2, key3, key4, key5 },
				new Object[] { val1, val2, val3, val4, val5 });
	}

	public static final Map<String, Object> opts(String[] keys, Object[] vals) {
		if (keys.length != vals.length) throw new RuntimeException("Bad Args");
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < keys.length; i++) {
			data.put(keys[i], vals[i]);
		}
		return data;
	}

	public static void main(String[] args) {
		System.out.println(RiTa.analyze("absolot"));
		System.out.println(RiTa.evaluate("( newt | ginko | salamander)"));
	}

}
