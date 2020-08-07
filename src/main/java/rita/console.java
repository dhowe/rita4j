package rita;

import java.util.*;

public class console {

  @SuppressWarnings("rawtypes")
  public static void warn(Collection l) {
    int i = 0;
    if (l == null || l.size() < 1) {
      System.out.println("[]");
      return;
    }
    for (Iterator it = l.iterator(); it.hasNext(); i++)
      System.out.println(i + ") '" + it.next() + "'");
  }

  public static void warn(int[] l) {
    if (l == null || l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  public static void warn(float[] l) {
    if (l == null || l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  public static void warn(Object[] l) {
    if (l == null) {
      System.out.println("null");
      return;
    }
    if (l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  @SuppressWarnings("rawtypes")
  public static void warn(Map l) {
    if (l == null || l.size() < 1) {
      System.out.println("[]");
      return;
    }
    for (Iterator it = l.keySet().iterator(); it.hasNext();) {
      Object key = it.next(), val = l.get(key);
      System.out.println(key + "='" + val + "'");
    }
  }

  public static void warn(Object l) {
    if (l instanceof Object[]) {
      warn((Object[]) l);
      return;
    }
    System.out.println(l);
  }
  
  
  @SuppressWarnings("rawtypes")
  public static void log(Collection l) {
    int i = 0;
    if (l == null || l.size() < 1) {
      System.out.println("[]");
      return;
    }
    for (Iterator it = l.iterator(); it.hasNext(); i++)
      System.out.println(i + ") '" + it.next() + "'");
  }

  public static void log(int[] l) {
    if (l == null || l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  public static void log(float[] l) {
    if (l == null || l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  public static void log(Object[] l) {
    if (l == null) {
      System.out.println("null");
      return;
    }
    if (l.length < 1) {
      System.out.println("[]");
      return;
    }
    for (int j = 0; j < l.length; j++)
      System.out.println(j + ") '" + l[j] + "'");
  }

  @SuppressWarnings("rawtypes")
  public static void log(Map l) {
    if (l == null || l.size() < 1) {
      System.out.println("[]");
      return;
    }
    for (Iterator it = l.keySet().iterator(); it.hasNext();) {
      Object key = it.next(), val = l.get(key);
      System.out.println(key + "='" + val + "'");
    }
  }

  public static void log(Object l) {
    if (l instanceof Object[]) {
      log((Object[]) l);
      return;
    }
    System.out.println(l);
  }
  
  public static void main(String[] args) {
    console.log("Hello");
  }

}
