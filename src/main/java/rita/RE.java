package rita;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RE {

	private Matcher regex;
	private String raw;
	private int offset;
	private String suffix;

	public RE(String re, int truncate, String suff) {
		regex = Pattern.compile(re, Pattern.CASE_INSENSITIVE).matcher("\\w+");
		raw = re;
		offset = truncate;
		suffix = suff;
	}

	public RE(String re, int truncate, String suff, int notused) {
		this(re, truncate, suff);
	}

	public RE(String re, int truncate) {
		regex = Pattern.compile(re, Pattern.CASE_INSENSITIVE).matcher("\\w+");
		raw = re;
		offset = truncate;
		suffix = "";
	}
	
	public static boolean test(String pattern, String input) {
		return test(Pattern.compile(pattern), input);
	}
	
	public static boolean test(Pattern pattern, String input) {
		return pattern.matcher(input).find();
	}
	
	public String toString() {
		return "RE: " + raw + " -> " + suffix;
	}

	public boolean applies(String word) {
		return regex.reset(word.trim()).find();
	}

	public String fire(String word) {
		return truncate(word.trim()) + suffix;
	}

	public boolean analyze(String word) {
		return (suffix != "" && word.endsWith(suffix));
	}

	private String truncate(String word) {

		if (offset == 0) return word;

		StringBuffer buffer = new StringBuffer(word);
		int i = 1;  // substring?
		while (i <= offset) {
			buffer.deleteCharAt(buffer.length() - 1);
			i++;
		}

		return buffer.toString();
	}
}
