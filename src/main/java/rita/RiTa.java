package rita;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class RiTa {
	protected static Lexicon lexicon;
	protected static Analyzer analyzer;

	protected static Concorder concorder;
	protected static Conjugator conjugator;
	protected static LetterToSound lts;

	//  UNCOMMENT IF/AS NEEDED:
	//  protected static Tagger tagger;
	//  protected static Pluralizer pluralizer;
	//  protected static Tokenizer tokenizer;
	//  protected static Syllabifier syllabifier;
	//  protected static Stemmer stemmer;
	//	protected static Inflector inflector;

	public static Map<String, Function<String, String>> addTransform(String name, Function<String, String> func) {
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
		return alliterations(word, Util.opts("minLength", minWordLength));
	}

	public static String[] alliterations(String word, Map<String, Object> opts) {
		return _lexicon().alliterations(word, opts);
	}

	public static Map<String, String> analyze(String word) {
		return _analyzer().analyze(word);
	}

	public static String articlize(String s) {

		return RiScript.articlize(s);
	}

	public static Map<String, String> concordance(String text, String word) {
		return concordance(text, word, null);
	}

	public static Map<String, String> concordance(String text, String word, Map<String, Object> opts) //TODO
	{
		return concorder.concordance(text, word, opts);
	}

	public static String conjugate(String word, Map<String, Object> opts) {
		return conjugator.conjugate(word, opts);
	}

	public static String conjugate(String word) {

		return conjugate(word, null);
	}

	public static String env() {
		return "Java";
	}

	public static boolean hasWord(String word) {
		return _lexicon().hasWord(word);
	}

	public static boolean isAbbreviation(String input) {
		return isAbbreviation(input, false);
	}

	public static boolean isAbbreviation(String input, Map<String, Object> opts) {
		return isAbbreviation(input, Util.boolOpt("ignoreCase", opts));
	}

	public static boolean isAbbreviation(String input, boolean ignoreCase) {
		if (input == null) return false;
		if (ignoreCase) input = input.substring(0, 1).toUpperCase() + input.substring(1);
		return Arrays.stream(ABBREVIATIONS).anyMatch(input::equals);
	}

	public static boolean isAdjective(String word) {
		return Tagger.isAdjective(word);
	}

	public static boolean isAdverb(String word) {
		return Tagger.isAdverb(word);
	}

	public static boolean isAlliteration(String word1, String word2) {
		return _lexicon().isAlliteration(word1, word2, false);//TODO default?
	}

	public static boolean isAlliteration(String word1, String word2, boolean useLTS) {
		return _lexicon().isAlliteration(word1, word2, useLTS);
	}

	public static boolean isNoun(String word) {
		return Tagger.isNoun(word);
	}

	public static boolean isPunctuation(String text) {
		return text != null && text.length() > 0 && ONLY_PUNCT.matcher(text).matches();
	}

	public static boolean isQuestion(String sentence) { // remove?
		return Arrays.stream(QUESTIONS).anyMatch(tokenize(sentence)[0].toLowerCase()::equals);
	}

	public static boolean isRhyme(String word1, String word2) {
		return _lexicon().isRhyme(word1, word2, false);//TODO default?
	}

	public static boolean isRhyme(String word1, String word2, boolean useLTS) {
		return _lexicon().isRhyme(word1, word2, useLTS);
	}

	public static boolean isVerb(String word) {
		return Tagger.isVerb(word);
	}

	public static String[] kwic(String text, String word) {
		return concorder.kwic(text, word);
	}

	public static String[] kwic(String text, String word, Map<String, Object> opts) //parameter mismatch
	{
		return kwic(text, word);
	}

	public static String pastParticiple(String verb) {
		return Conjugator.pastParticiple(verb);
	}

	public static String phones(String text) {
		return RiTa.phones(text, null);
	}

	public static String phones(String text, Map<String, Object> opts) {
		return _analyzer().analyze(text).get("phones");
	}

	public static String posInline(String text, Map<String, Object> opts) {
		return posInline(text, Util.boolOpt("simple", opts));
	}

	public static String posInline(String text) {
		return posInline(text, false);
	}

	public static String posInline(String text, boolean useSimpleTags) {
		return Tagger.tagInline(text, useSimpleTags);
	}

	public static String[] pos(String text) {
		return pos(text, false);
	}

	public static String[] pos(String text, Map<String, Object> opts) {
		return pos(text, Util.boolOpt("simple", opts));
	}

	public static String[] pos(String text, boolean useSimpleTags) {
		return Tagger.tag(text, useSimpleTags);
	}

	public static String[] pos(String[] text, Map<String, Object> opts) {
		return pos(text, Util.boolOpt("simple", opts));
	}

	public static String[] pos(String[] text) {
		return pos(text, false);
	}

	public static String[] pos(String[] text, boolean useSimpleTags) {
		return Tagger.tag(text, useSimpleTags);
	}

	public static String pluralize(String word) {
		return pluralize(word, null);
	}

	public static String pluralize(String word, Map<String, Object> opts) {
		return Inflector.pluralize(word, opts);
	}

	public static String presentParticiple(String verb) {
		return Conjugator.presentParticiple(verb);
	}

	public static float random() {
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

	public static int[] randomOrdering(int num) {
		return RandGen.randomOrdering(num);
	}

	public static void randomSeed(int theSeed) {
		RandGen.seed(theSeed);
	}

	public static String randomWord(Map<String, Object> opts) {
		return _lexicon().randomWord(opts);
	}

	public static String randomWord(String pos) {
		return randomWord(Util.opts("pos", pos));
	}

	public static String randomWord(int syllables) {
		return randomWord(Util.opts("numSyllables", syllables));
	}

	public static String randomWord(String pos, int syllables) {
		return randomWord(Util.opts("pos", pos, "numSyllables", syllables));
	}

	public static String[] rhymes(String word) {
		return _lexicon().rhymes(word);
	}

	public static String[] rhymes(String word, Map<String, Object> opts) {
		return _lexicon().rhymes(word, opts);
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

	public static String stresses(String text) {
		return _analyzer().analyze(text).get("stresses");
	}

	public static String syllables(String text) {
		return _analyzer().analyze(text).get("syllables");
	}

	public static String[] soundsLike(String word) {
		return _lexicon().soundsLike(word);
	}

	public static String[] spellsLike(String word) {
		return _lexicon().spellsLike(word);
	}

	public static String[] search() {
		return _lexicon().search(null);
	}

	public static String[] search(String word) {
		return _lexicon().search(word);
	}

	public static String[] soundsLike(String word, Map<String, Object> opts) {
		return _lexicon().soundsLike(word, opts);
	}

	public static String[] spellsLike(String word, Map<String, Object> opts) {
		return _lexicon().spellsLike(word, opts);
	}

	public static String[] search(String word, Map<String, Object> opts) {
		return _lexicon().search(word, opts);
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

	public static String[] sentences(String text, Map<String, Object> opts) {
		return sentences(text, Util.strOpt("pattern", opts));
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

	public static String untokenize(String[] words) {
		return Tokenizer.untokenize(words);
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
		return _lexicon().words(regex);
	}

	// //////////////////////////////////////////////////////////////////////////

	public static Lexicon _lexicon() {
		if (RiTa.lexicon == null) {
			RiTa.lts = new LetterToSound();
			try {
				RiTa.lexicon = new Lexicon(DICT_PATH);
			} catch (Exception e) {
				throw new RiTaException("Cannot load dictionary at "
						+ DICT_PATH + " " + System.getProperty("user.dir"));
			}
		}
		return RiTa.lexicon;
	}

	public static Markov createMarkov(int n) {
		return new Markov(n);
	}

	private static Analyzer _analyzer() {
		if (analyzer == null) {
			RiTa._lexicon();
			RiTa.analyzer = new Analyzer();
		}
		return RiTa.analyzer;
	}

	// STATICS
	public static boolean SILENT = false;
	//public static boolean SILENT_LTS = true; //TODO is SILENT_LTS or SILENCE_LTS?
	public static boolean SILENCE_LTS = true;
	public static boolean LEX_WARN = false;
	public static boolean LTS_WARN = false;
	public static boolean SPLIT_CONTRACTIONS = false;

	public static String PHONEME_BOUNDARY = "-";
	public static String WORD_BOUNDARY = " ";
	public static String SYLLABLE_BOUNDARY = "/";
	public static String SENTENCE_BOUNDARY = "|";
	public static String DICT_PATH = "rita_dict.js";

	// CONSTANTS
	public static final int FIRST_PERSON = 1;
	public static final int SECOND_PERSON = 2;
	public static final int THIRD_PERSON = 3;
	public static final int PAST_TENSE = 4;
	public static final int PRESENT_TENSE = 5;
	public static final int FUTURE_TENSE = 6;
	public static final int SINGULAR = 7;
	public static final int PLURAL = 8;
	public static final int NORMAL = 9;
	public static final int INFINITIVE = 1;
	public static final int GERUND = 2;
	public static final int IMPERATIVE = 3;
	public static final int BARE_INFINITIVE = 4;
	public static final int SUBJUNCTIVE = 5;

	public static final String STRESSED = "1";
	public static final String UNSTRESSED = "0";
	public static final String VOWELS = "aeiou";
	public static final String VERSION = "2";

	public static final Pattern ONLY_PUNCT = Pattern.compile("^[^0-9A-Za-z\\s]*$");
	public static final String[] FEATURES = { "TOKENS", "STRESSES", "PHONEMES", "SYLLABLES", "POS", "TEXT" };
	public static final String[] QUESTIONS = { "was", "what", "when", "where", "which", "why", "who", "will", "would", "who", "how", "if", "is",
			"could", "might", "does", "are", "have" };
	public static final String[] ABBREVIATIONS = { "Adm.", "Capt.", "Cmdr.", "Col.", "Dr.", "Gen.", "Gov.", "Lt.", "Maj.", "Messrs.", "Mr.", "Mrs.",
			"Ms.", "Prof.", "Rep.", "Reps.", "Rev.", "Sen.",
			"Sens.", "Sgt.", "Sr.", "St.", "a.k.a.", "c.f.", "i.e.", "e.g.", "vs.", "v.", "Jan.", "Feb.", "Mar.", "Apr.", "Mar.", "Jun.", "Jul.", "Aug.",
			"Sept.", "Oct.", "Nov.", "Dec." };
	public static final String[] STOP_WORDS = null; //TODO

	public static void main(String[] args) {
		System.out.println(RiTa.analyze("absolot"));
	}

}
