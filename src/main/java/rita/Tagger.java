package rita;


public class Tagger
{

	public static final String[] ADJ = {"jj", "jjr", "jjs"};
	public static final String[] ADV = {"rb", "rbr", "rbs", "rp"};
	public static final String[] NOUNS = {"nn", "nns", "nnp", "nnps"};
	public static final String[] VERBS = {"vb", "vbd", "vbg", "vbn", "vbp", "vbz"};
	
  public boolean isAdjective(String word)
  {
	  return checkType(word, ADJ);
  }

  public boolean isAdverb(String word)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isNoun(String word)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public boolean isVerb(String word)
  {
    // TODO Auto-generated method stub
    return false;
  }

  public String tagInline(String text, boolean useSimpleTags)
  {
    // TODO Auto-generated method stub
    return null;
  }

  public String[] tag(String text, boolean useSimpleTags)
  {
    // TODO Auto-generated method stub
    return null;
  }

  private boolean checkType(String word, String[] tagArray) {

	    if (word != null) {

	      if (word.length() == 0) return false;

	      if (word.indexOf(" ") < 0) {

	        let psa = RiTa._lexicon()._posArr(word);

	        if (RiTa.LEX_WARN && psa.length < 1 && this.size() <= 1000) {
	          warn(RiTa.LEX_WARN);
	          RiTa.LEX_WARN = 0; // only once
	        }

	        return psa.filter(p => tagArray.indexOf(p) > -1).length > 0;
	      }

	      throw Error("checkType() expects single word, found: '" + word + "'");
	    }
	  }
}
