package rita;

public class Pluralizer
{

  public String pluralize(String word)
  {

	    if (word == null || word.length() == 0 ) return "";

	    if (MODALS.includes(word.toLowerCase())) return word;

	    lObjectet rules = PLURAL_RULES; //TODO
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

	    if (MODALS.includes(word.toLowerCase())) return word;

	    Object rules = SINGULAR_RULES; //TODO
	    int i = rules.length;

	    while (i--) {
	      if (rules[i].applies(word.toLowerCase())) {
	        return rules[i].fire(word);
	      }
	    }

	    return RiTa.stem(word);
  }
  
  
}
