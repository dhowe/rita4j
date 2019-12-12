package rita;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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


	public static String[] shuffle(String[] arr) { // shuffle array //TODO what is the type of second arg
		String[] newArray = arr;


		Random rand = new Random();

		for (int i = 0; i < newArray.length; i++) {
			int randomIndexToSwap = rand.nextInt(newArray.length);
			String temp = newArray[randomIndexToSwap];
			newArray[randomIndexToSwap] = newArray[i];
			newArray[i] = temp;
		}
		/*
		 * 	  
		 * //  int len = arr.length;
	  //  int i = len;
	   // Random rand = new Random();

	    while (i>0) {
	      int p = parseInt(((Object) randomable).random() * len);
	       String t = newArray[i];
	      newArray[i] = newArray[p];
	      newArray[p] = t;
	      i--;

	    }
		 */
		return newArray;
	}

	public static int minEditDist(String[] source, String[] target) {

		int i, j;

		int cost; // cost
		String sI; // ith character of s
		String tJ; // jth character of t
		int[][] matrix = new int[source.length+1][target.length+1];
		// Step 1 ----------------------------------------------

		for (i = 0; i <= source.length; i++) {
			System.out.println(i);
		      matrix[i][0] = i;
		}

		for (j = 0; j <= target.length; j++) {
			matrix[0][j] = j;
		}

		// Step 2 ----------------------------------------------

		for (i = 1; i <= source.length; i++) {
			sI = source[i - 1];

			// Step 3 --------------------------------------------

			for (j = 1; j <= target.length; j++) {
				tJ = target[j - 1];

				// Step 4 ------------------------------------------

				cost = (sI == tJ) ? 0 : 1;

				// Step 5 ------------------------------------------
				matrix[i][j] = Math.min(Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1), matrix[i - 1][j - 1] + cost);
			}
		}

		// Step 6 ----------------------------------------------
		return matrix[source.length][target.length];
	}



}
