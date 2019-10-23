package rita;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Lexicon
{
  private static String LEXICON_DELIM = ":";
  private static int MAP_SIZE = 30000;

  protected Map<String, String[]> dict; // data

  public Lexicon(String filePath) throws Exception
  {
    load(filePath);
  }

  public void load(String filePath) throws Exception
  {

    List<String> lines = loadJSON(filePath);

    if (lines == null || lines.size() < 2) {
      throw new Exception("Problem parsing RiLexicon data files");
    }

    dict = new LinkedHashMap<String, String[]>(MAP_SIZE);

    for (int i = 1; i < lines.size() - 1; i++) // ignore JS prefix/suffix
    {
      String line = lines.get(i);
      String[] parts = line.split(LEXICON_DELIM);
      if (parts == null || parts.length != 2) {
        throw new Exception("Illegal entry: " + line);
      }
      dict.put(parts[0], parts[1].split(","));
    }
  }

  public static List<String> loadJSON(String file) throws Exception
  {
    if (file == null) {
      throw new Exception("No dictionary path specified!");
    }

    final List<String> lines = Files.readAllLines(Paths.get(file));

    if (lines == null || lines.size() < 1) {
      throw new Exception("Unable to load lexicon from: " + file);
    }

    // clean out the JSON formatting (TODO: optimize)
    // String clean = data.replaceAll("['\\[\\]]", E).replaceAll(",", "|");

    return lines;
  }

  public String[] alliterations(String word, int minWordLength)
  {
    return null;
  }

  public boolean hasWord(String word)
  {
    return false;
  }

  public boolean isAlliteration(String word1, String word2)
  {

    return false;
  }

  public boolean isRhyme(String word1, String word2)
  {

    return false;
  }

  public String randomWord(String pos, int numSyllabes)
  {
    return null;
  }

  public String[] rhymes(String word)
  {
    return null;
  }

  public String[] similarBy(String word, Map<String, Object> opts)
  {
    return null;
  }

  public String[] words(Pattern regex)
  {
    if (regex != null) return this.dict.keySet().stream().filter
        (word -> regex.matcher(word).matches()).toArray(String[]::new);
    return this.dict.keySet().toArray(new String[0]);
  }

}
