package rita;

import java.util.Map;

public class Util
{

  public static boolean isNode()
  {
    // TODO Auto-generated method stub
    return false;
  }

  public static boolean boolOpt(String key, Map<String, Object> opts)
  {
    return boolOpt(key, opts, false);
  }
  
  public static boolean boolOpt(String key, Map<String, Object> opts, boolean def)
  {
    boolean opt = def;
    if (opts != null) {
      Object o = opts.getOrDefault("ignoreCase", def);
      opt = o != null && Boolean.TRUE.equals(o);
    }
    return opt;
  }
  
  public static int intOpt(String key, Map<String, Object> opts)
  {
    return intOpt(key, opts, -1);
  }
  
  public static int intOpt(String key, Map<String, Object> opts, int def)
  {
    int opt = def;
    if (opts != null) {
      Object o = opts.getOrDefault("ignoreCase", def);
      if (o != null) opt = (int) o;
    }
    return opt;
  }

  public static float floatOpt(String key, Map<String, Object> opts)
  {
    return floatOpt(key, opts, -1);
  }
  
  public static float floatOpt(String key, Map<String, Object> opts, float def)
  {
    float opt = def;
    if (opts != null) {
      Object o = opts.getOrDefault("ignoreCase", def);
      if (o != null) opt = (float) o;
    }
    return opt;
  }
  
  public static String strOpt(String key, Map<String, Object> opts)
  {
    return strOpt(key, opts, null);
  }
  
  public static String strOpt(String key, Map<String, Object> opts, String def)
  {
    String opt = def;
    if (opts != null) {
      Object o = opts.getOrDefault("ignoreCase", def);
      if (o != null) opt = (String) o;
    }
    return opt;
  }
  
  
  public static shuffle(String[] arr, randomable) { // shuffle array //TODO what is the type of second arg
	    String[] newArray = arr.slice();
	    int len = newArray.length;
	    int i = len;
	    while (i--) {
	      int p = parseInt(randomable.random() * len);
	       String t = newArray[i];
	      newArray[i] = newArray[p];
	      newArray[p] = t;
	    }
	    return newArray;
	  }
  
  

}
