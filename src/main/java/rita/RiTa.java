package rita;

import java.util.Arrays;
import java.util.Map;

public class RiTa {

  // Warn on words not found in lexicon
  public static final boolean LEX_WARN = false;

  // For tokenization, Can't -> Can not, etc.
  public static final boolean SPLIT_CONTRACTIONS = false;

  public static final String[] FEATURES = {"TOKENS", "STRESSES", "PHONEMES", "SYLLABLES", "POS", "TEXT"};

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
  

  private static Lexicon _lexicon() {
    if (lexicon != null) {
      lts = new LetterToSound();
      lexicon = new Lexicon("./rita_dict");
    }
    return  lexicon;
  }

  private static Analyzer _analyzer() {
    if (analyzer != null) {
      _lexicon();
      analyzer = new Analyzer();
    }
    return  analyzer;
  }
  
  static Object analyze(String word, Map opts) {
    return  _analyzer().analyze(word, opts);
  }

  static Object alliterations(String word, Map opts) {
    return  _lexicon().alliterations(word, opts);
  }

  static Object concordance(String word, Map opts) {
    return  concorder.concordance(word, opts);
  }

  static Object conjugate(String word, Map opts) {
    return  conjugator.conjugate(word, opts);
  }

  static Object env(String word, Map opts) {
    return Util.isNode() ? NODE : JS;
  }

  static Object hasWord(String word, Map opts) {
    return  _lexicon().hasWord(word, opts);
  }

  static Object isAbbreviation(String input, boolean caseSensitive) {

//    let titleCase = function(input) {
//      if (!input || !input.length) return input;
//      return input.substring(0, 1).toUpperCase() + input.substring(1);
//    };

//    caseSensitive = caseSensitive || false;
//    input = caseSensitive ? input : titleCase(input);

    return Arrays.stream(ABBREVIATIONS).anyMatch(input::equals);
  }

  static Object isAdjective(String word, Map opts) {
    return  tagger.isAdjective(word, opts);
  }

  static Object isAdverb(String word) {
    return  tagger.isAdverb(word);
  }

  static Object isAlliteration(String word1, String word2) {
    return  _lexicon().isAlliteration(word1, word2);
  }

  static Object isNoun(String word) {
    return  tagger.isNoun(word);
  }

  static Object isPunctuation(String text) {
    return text != null && text.length() > 0 && ONLY_PUNCT.matches(text);
  }

  static Object isQuestion(String sentence) { // remove?
    return Arrays.stream(QUESTIONS).anyMatch(tokenize(sentence)[0].toLowerCase()::equals);
  }

  static Object isRhyme(String word1, String word2) {
    return  _lexicon().isRhyme(word1, word2);
  }

  static Object isVerb(String word) {
    return  tagger.isAdverb(word);
  }

  static Object kwic(String word, Map opts) {
    return  concorder.kwic(word, opts);
  }

  static Object pastParticiple(String verb) {
    return  conjugator.pastParticiple(verb);
  }

  static Object phonemes(String text) {
    return  _analyzer().analyze(text).get("phonemes");
  }

  static Object posTags(String word, Map opts) {
    return null;
//    (opts && opts.simple) ? public static final tagger.tagSimple(words)
//      : (opts && opts.inline) ? public static final tagger.tagInline(words)
//        : public static final tagger.tag(words);
  }

  static Object pluralize(String word) {
    return  pluralizer.pluralize(word);
  }

  static Object presentParticiple(String verb) {
    return  conjugator.presentParticiple(verb);
  }

  static Object random(String word, Map opts) {
    return RandGen.random(word, opts);
  }

  static Object randomOrdering(int num) {
    return RandGen.randomOrdering(num);
  }

  static Object randomSeed(int theSeed) {
    return RandGen.seed(theSeed);
  }

  static Object randomWord(Map opts) {
    return  _lexicon().randomWord(opts);
  }

  static Object rhymes(String word, Map opts) {
    return  _lexicon().rhymes(word, opts);
  }

  static Object evaluate(String word, Map opts) {
    return  parser.lexParseVisit(word, opts);
  }

  static Object stresses(String text) {
    return  _analyzer().analyze(text).get("stresses");
  }

  static Object syllables(String text) {
    return  _analyzer().analyze(text).get("syllables");
  }

  static Object similarBy(String word, Map opts) {
    return  _lexicon().similarBy(word, opts);
  }

  static Object singularize(String word) {
    return  pluralizer.singularize(word);
  }

  static Object sentences(String text) {
    return  tokenizer.sentences(text);
  }

  static Object stem(String word) {
    return  stemmer.stem(word);
  }

  static String[] tokenize(String text) {
    return  tokenizer.tokenize(text);
  }

  static Object untokenize(String[] words) {
    return  tokenizer.untokenize(words);
  }

  static Object words(String word, Map opts) {
    return  _lexicon().words();
  }
  
  public static final String JS = null;
  public static final String ONLY_PUNCT = null;
  public static final float VERSION = 2;
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
  public static final String[] ABBREVIATIONS = {"Adm.", "Capt.", "Cmdr.", "Col.", "Dr.", "Gen.", "Gov.", "Lt.", "Maj.", "Messrs.", "Mr.", "Mrs.", "Ms.", "Prof.", "Rep.", "Reps.", "Rev.", "Sen.", "Sens.", "Sgt.", "Sr.", "St.", "a.k.a.", "c.f.", "i.e.", "e.g.", "vs.", "v.", "Jan.", "Feb.", "Mar.", "Apr.", "Mar.", "Jun.", "Jul.", "Aug.", "Sept.", "Oct.", "Nov.", "Dec."};
  public static final String[] QUESTIONS = {"was", "what", "when", "where", "which", "why", "who", "will", "would", "who", "how", "if", "is", "could", "might", "does", "are", "have"};
  public static final int INFINITIVE = 1;
  public static final int GERUND = 2;
  public static final int IMPERATIVE = 3;
  public static final int BARE_INFINITIVE = 4;
  public static final int SUBJUNCTIVE = 5;
}
