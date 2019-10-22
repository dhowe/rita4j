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

  public static Map<String, String> analyze(String word)
  {
    return analyze(word);
  }

  public static String[] alliterations(String word)
  {
    return _lexicon().alliterations(word, Integer.MAX_VALUE);
  }

  public static String[] alliterations(String word, int minWordLength)
  {
    return alliterations(word, minWordLength);
  }

  public static String[] alliterations(String word, Map<String, Object> opts)
  {
    return alliterations(word, Util.intOpt("minWordLength", opts, Integer.MAX_VALUE));
  }

  public static Map<String, String> concordance(String text, String word)
  {
    return concordance(text, word, null);
  }

  public static Map<String, String> concordance(String text, String word, Map<String, Object> opts)
  {
    return concorder.concordance(text, word, opts);
  }

  public static String conjugate(String word, Map<String, Object> opts)
  {
    return conjugator.conjugate(word, opts);
  }

  public static String env()
  {
    return Util.isNode() ? NODE : JS;
  }

  public static boolean hasWord(String word)
  {
    return _lexicon().hasWord(word);
  }

  public static boolean isAbbreviation(String input)
  {
    return isAbbreviation(input, false);
  }

  public static boolean isAbbreviation(String input, Map<String, Object> opts)
  {
    return isAbbreviation(input, Util.boolOpt("ignoreCase", opts));
  }

  public static boolean isAbbreviation(String input, boolean ignoreCase)
  {
    if (ignoreCase) input = input.substring(0, 1).toUpperCase() + input.substring(1);
    return Arrays.stream(ABBREVIATIONS).anyMatch(input::equals);
  }

  public static boolean isAdjective(String word)
  {
    return tagger.isAdjective(word);
  }

  public static boolean isAdverb(String word)
  {
    return tagger.isAdverb(word);
  }

  public static boolean isAlliteration(String word1, String word2)
  {
    return _lexicon().isAlliteration(word1, word2);
  }

  public static boolean isNoun(String word)
  {
    return tagger.isNoun(word);
  }

  public static boolean isPunctuation(String text)
  {
    return text != null && text.length() > 0 && ONLY_PUNCT.matcher(text).matches();
  }

  public static boolean isQuestion(String sentence)
  { // remove?
    return Arrays.stream(QUESTIONS).anyMatch
        (tokenize(sentence)[0].toLowerCase()::equals);
  }

  public static boolean isRhyme(String word1, String word2)
  {
    return _lexicon().isRhyme(word1, word2);
  }

  public static boolean isVerb(String word)
  {
    return tagger.isVerb(word);
  }

  public static String[] kwic(String text, String word)
  {
    return kwic(text, word, null);
  }

  public static String[] kwic(String text, String word, Map<String, Object> opts)
  {
    return concorder.kwic(text, word, opts);
  }

  public static String pastParticiple(String verb)
  {
    return conjugator.pastParticiple(verb);
  }

  public static String phonemes(String text)
  {
    return _analyzer().analyze(text).get("phonemes");
  }

  public static String posTagsInline(String text, Map<String, Object> opts)
  {
    return posTagsInline(text, Util.boolOpt("simple", opts));
  }

  public static String posTagsInline(String text)
  {
    return posTagsInline(text, false);
  }

  public static String posTagsInline(String text, boolean useSimpleTags)
  {
    return tagger.tagInline(text, useSimpleTags);
  }

  public static String[] posTags(String text, Map<String, Object> opts)
  {
    return posTags(text, Util.boolOpt("simple", opts));
  }

  public static String[] posTags(String text)
  {
    return posTags(text, false);
  }

  public static String[] posTags(String text, boolean useSimpleTags)
  {
    return tagger.tag(text, useSimpleTags);
  }

  public static String pluralize(String word)
  {
    return pluralizer.pluralize(word);
  }

  public static String presentParticiple(String verb)
  {
    return conjugator.presentParticiple(verb);
  }

  public static int[] randomOrdering(int num)
  {
    return RandGen.randomOrdering(num);
  }

  public static void randomSeed(int theSeed)
  {
    RandGen.seed(theSeed);
  }

  public static String randomWord(Map<String, Object> opts)
  {
    return randomWord(Util.strOpt("pos", opts));
  }

  public static String randomWord(String pos)
  {
    return randomWord(pos, -1);
  }

  public static String randomWord(int syllables)
  {
    return randomWord(null, syllables);
  }

  public static String randomWord(String pos, int syllables)
  {
    return _lexicon().randomWord(pos, syllables);
  }

  public static String[] rhymes(String word)
  {
    return _lexicon().rhymes(word);
  }

  public static String evaluate(String word, Map<String, Object> opts)
  {
    return parser.lexParseVisit(word, opts);
  }

  public static String stresses(String text)
  {
    return _analyzer().analyze(text).get("stresses");
  }

  public static String syllables(String text)
  {
    return _analyzer().analyze(text).get("syllables");
  }

  public static String[] similarBy(String word, Map<String, Object> opts)
  {
    return _lexicon().similarBy(word, opts);
  }

  public static String singularize(String word)
  {
    return pluralizer.singularize(word);
  }

  public static String[] sentences(String text)
  {
    return sentences(text, (Pattern) null);
  }

  public static String[] sentences(String text, Map<String, Object> opts)
  {
    return sentences(text, Util.strOpt("pattern", opts));
  }

  public static String[] sentences(String text, String regex)
  {
    return sentences(text, Pattern.compile(regex));
  }

  public static String[] sentences(String text, Pattern regex)
  {
    return tokenizer.sentences(text, regex);
  }

  public static String stem(String word)
  {
    return stemmer.stem(word);
  }

  public static String[] tokenize(String text)
  {
    return tokenizer.tokenize(text);
  }

  public static String untokenize(String[] words)
  {
    return tokenizer.untokenize(words);
  }

  public static String[] words(Map<String, Object> opts)
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

  // STATICS
  public static boolean SILENT = false;
  public static boolean SILENT_LTS = true;
  public static boolean LEX_WARN = false;
  public static boolean SPLIT_CONTRACTIONS = false;

  public static String PHONEME_BOUNDARY = "-";
  public static String WORD_BOUNDARY = " ";
  public static String SYLLABLE_BOUNDARY = "/";
  public static String SENTENCE_BOUNDARY = "|";
  
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
  
  public static final String JS = "js";
  public static final String NODE = "node";
  public static final String BROWSER = "browser";
  public static final String STRESSED = "1";
  public static final String UNSTRESSED = "0";
  public static final String VOWELS = "aeiou";
  public static final String VERSION = "##version##";
  public static final Pattern ONLY_PUNCT = Pattern.compile("^[^0-9A-Za-z\\s]*$");
  public static final String[] FEATURES = { "TOKENS", "STRESSES", "PHONEMES", "SYLLABLES", "POS", "TEXT" };
  public static final String[] QUESTIONS = { "was", "what", "when", "where", "which", "why", "who", "will", "would", "who", "how", "if", "is", "could", "might", "does", "are", "have" };
  public static final String[] ABBREVIATIONS = { "Adm.", "Capt.", "Cmdr.", "Col.", "Dr.", "Gen.", "Gov.", "Lt.", "Maj.", "Messrs.", "Mr.", "Mrs.", "Ms.", "Prof.", "Rep.", "Reps.", "Rev.", "Sen.",
      "Sens.", "Sgt.", "Sr.", "St.", "a.k.a.", "c.f.", "i.e.", "e.g.", "vs.", "v.", "Jan.", "Feb.", "Mar.", "Apr.", "Mar.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec." };

}
