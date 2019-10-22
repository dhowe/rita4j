package rita;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class RiTa
{
  protected static LetterToSound lts;
  protected static Lexicon lexicon;
  protected static Concorder concorder;
  protected static Analyzer analyzer;
  protected static LexParser parser;
  protected static Stemmer stemmer;
  protected static Tagger tagger;
  protected static Pluralizer pluralizer;
  protected static Tokenizer tokenizer;
  protected static Conjugator conjugator;
  protected static Syllabifier syllabifier;

  static Map<String, String> analyze(String word)
  {
    return analyze(word);
  }

  static String[] alliterations(String word)
  {
    return _lexicon().alliterations(word, Integer.MAX_VALUE);
  }

  static String[] alliterations(String word, int minWordLength)
  {
    return alliterations(word, minWordLength);
  }

  static String[] alliterations(String word, Map<String, Object> opts)
  {
    return alliterations(word, Util.intOpt("minWordLength", opts, Integer.MAX_VALUE));
  }

  static Map<String, String> concordance(String text, String word)
  {
    return concordance(text, word, null);
  }

  static Map<String, String> concordance(String text, String word, Map<String, Object> opts)
  {
    return concorder.concordance(text, word, opts);
  }

  static String conjugate(String word, Map<String, Object> opts)
  {
    return conjugator.conjugate(word, opts);
  }

  static String env()
  {
    return Util.isNode() ? NODE : JS;
  }

  static boolean hasWord(String word)
  {
    return _lexicon().hasWord(word);
  }

  static boolean isAbbreviation(String input)
  {
    return isAbbreviation(input, false);
  }

  static boolean isAbbreviation(String input, Map<String, Object> opts)
  {
    return isAbbreviation(input, Util.boolOpt("ignoreCase", opts));
  }

  static boolean isAbbreviation(String input, boolean ignoreCase)
  {
    if (ignoreCase) input = input.substring(0, 1).toUpperCase() + input.substring(1);
    return Arrays.stream(ABBREVIATIONS).anyMatch(input::equals);
  }
  
  static boolean isAdjective(String word)
  {
    return tagger.isAdjective(word);
  }
  
  static boolean isAdverb(String word)
  {
    return tagger.isAdverb(word);
  }

  static boolean isAlliteration(String word1, String word2)
  {
    return _lexicon().isAlliteration(word1, word2);
  }

  static boolean isNoun(String word)
  {
    return tagger.isNoun(word);
  }

  static boolean isPunctuation(String text)
  {
    return text != null && text.length() > 0 && ONLY_PUNCT.matches(text);
  }

  static boolean isQuestion(String sentence)
  { // remove?
    return Arrays.stream(QUESTIONS).anyMatch
        (tokenize(sentence)[0].toLowerCase()::equals);
  }

  static boolean isRhyme(String word1, String word2)
  {
    return _lexicon().isRhyme(word1, word2);
  }

  static boolean isVerb(String word)
  {
    return tagger.isVerb(word);
  }

  static String[] kwic(String text, String word)
  {
    return kwic(text, word, null);
  }

  static String[] kwic(String text, String word, Map<String, Object> opts)
  {
    return concorder.kwic(text, word, opts);
  }

  static String pastParticiple(String verb)
  {
    return conjugator.pastParticiple(verb);
  }

  static String phonemes(String text)
  {
    return _analyzer().analyze(text).get("phonemes");
  }

  static String posTagsInline(String text, Map<String, Object> opts)
  {
    return posTagsInline(text, Util.boolOpt("simple", opts));
  }

  static String posTagsInline(String text)
  {
    return posTagsInline(text, false);
  }

  static String posTagsInline(String text, boolean useSimpleTags)
  {
    return tagger.tagInline(text, useSimpleTags);
  }

  static String[] posTags(String text, Map<String, Object> opts)
  {
    return posTags(text, Util.boolOpt("simple", opts));
  }

  static String[] posTags(String text)
  {
    return posTags(text, false);
  }

  static String[] posTags(String text, boolean useSimpleTags)
  {
    return tagger.tag(text, useSimpleTags);
  }

  static String pluralize(String word)
  {
    return pluralizer.pluralize(word);
  }

  static String presentParticiple(String verb)
  {
    return conjugator.presentParticiple(verb);
  }

  static int[] randomOrdering(int num)
  {
    return RandGen.randomOrdering(num);
  }

  static void randomSeed(int theSeed)
  {
    RandGen.seed(theSeed);
  }

  static String randomWord(Map<String, Object> opts)
  {
    return randomWord(Util.strOpt("pos", opts));
  }

  static String randomWord(String pos)
  {
    return randomWord(pos, -1);
  }

  static String randomWord(int syllables)
  {
    return randomWord(null, syllables);
  }

  static String randomWord(String pos, int syllables)
  {
    return _lexicon().randomWord(pos, syllables);
  }

  static String[] rhymes(String word)
  {
    return _lexicon().rhymes(word);
  }

  static String evaluate(String word, Map<String, Object> opts)
  {
    return parser.lexParseVisit(word, opts);
  }

  static String stresses(String text)
  {
    return _analyzer().analyze(text).get("stresses");
  }

  static String syllables(String text)
  {
    return _analyzer().analyze(text).get("syllables");
  }

  static String[] similarBy(String word, Map<String, Object> opts)
  {
    return _lexicon().similarBy(word, opts);
  }

  static String singularize(String word)
  {
    return pluralizer.singularize(word);
  }

  static String[] sentences(String text)
  {
    return sentences(text, (Pattern)null);
  }
  
  static String[] sentences(String text, Map<String, Object> opts)
  {
    return sentences(text, Util.strOpt("pattern", opts));
  }
  
  static String[] sentences(String text, String regex)
  {
    return sentences(text, Pattern.compile(regex));
  }
  
  static String[] sentences(String text, Pattern regex)
  {
    return tokenizer.sentences(text, regex);
  }

  static String stem(String word)
  {
    return stemmer.stem(word);
  }

  static String[] tokenize(String text)
  {
    return tokenizer.tokenize(text);
  }

  static String untokenize(String[] words)
  {
    return tokenizer.untokenize(words);
  }

  static String[] words(Map<String, Object> opts)
  {
    return words(Util.strOpt("pattern", opts));
  }

  public static String[] words(String regex)
  {
    return words(Pattern.compile(regex));
  }
  
  public static String[] words(Pattern regex)
  {
    return _lexicon().words(regex);
  }

  // //////////////////////////////////////////////////////////////////////////

  private static Lexicon _lexicon()
  {
    if (lexicon != null) {
      lts = new LetterToSound();
      try {
        lexicon = new Lexicon("./rita_dict");
      } catch (Exception e) {
        throw new RiTaException();
      }
    }
    return lexicon;
  }

  private static Analyzer _analyzer()
  {
    if (analyzer != null) {
      _lexicon();
      analyzer = new Analyzer();
    }
    return analyzer;
  }

  public static final String JS = "js";
  public static final String ONLY_PUNCT = null;
  public static final boolean SPLIT_CONTRACTIONS = false;
  public static boolean SILENT_LTS = false;
  public static final boolean LEX_WARN = false;
  public static final String NODE = "node";
  public static final String BROWSER = "browser";
  public static final boolean SILENT = false;
  public static final boolean SILENCE_LTS = true;
  public static final int FIRST_PERSON = 1;
  public static final int SECOND_PERSON = 2;
  public static final int THIRD_PERSON = 3;
  public static final int PAST_TENSE = 4;
  public static final int PRESENT_TENSE = 5;
  public static final int FUTURE_TENSE = 6;
  public static final int SINGULAR = 7;
  public static final int PLURAL = 8;
  public static final int NORMAL = 9;
  public static final String STRESSED = "1";
  public static final String UNSTRESSED = "0";
  public static final String PHONEME_BOUNDARY = "-";
  public static final String WORD_BOUNDARY = " ";
  public static final String SYLLABLE_BOUNDARY = "/";
  public static final String SENTENCE_BOUNDARY = "|";
  public static final String VOWELS = "aeiou";
  public final static String VERSION = "##version##";
  public static final String[] FEATURES = { "TOKENS", "STRESSES", "PHONEMES", "SYLLABLES", "POS", "TEXT" };
  public static final String[] ABBREVIATIONS = { "Adm.", "Capt.", "Cmdr.", "Col.", "Dr.", "Gen.", "Gov.", "Lt.", "Maj.", "Messrs.", "Mr.", "Mrs.", "Ms.", "Prof.", "Rep.", "Reps.", "Rev.", "Sen.",
      "Sens.", "Sgt.", "Sr.", "St.", "a.k.a.", "c.f.", "i.e.", "e.g.", "vs.", "v.", "Jan.", "Feb.", "Mar.", "Apr.", "Mar.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec." };
  public static final String[] QUESTIONS = { "was", "what", "when", "where", "which", "why", "who", "will", "would", "who", "how", "if", "is", "could", "might", "does", "are", "have" };
  public static final int INFINITIVE = 1;
  public static final int GERUND = 2;
  public static final int IMPERATIVE = 3;
  public static final int BARE_INFINITIVE = 4;
  public static final int SUBJUNCTIVE = 5;
}
