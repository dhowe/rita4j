package rita;


public class Tagger
{

	public static final String[] ADJ = {"jj", "jjr", "jjs"};
	public static final String[] ADV = {"rb", "rbr", "rbs", "rp"};
	public static final String[] NOUNS = {"nn", "nns", "nnp", "nnps"};
	public static final String[] VERBS = {"vb", "vbd", "vbg", "vbn", "vbp", "vbz"};
	
  public boolean isAdjective(String word)
  {
	    return this.checkType(word, ADJ);
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

}
