package rita;

import java.util.Collection;
import java.util.Map;

public class Markov {
	public int n;
	public String[] input;
	// public Node root = new Node();

	public String SS = "<s>";
	public String SE = "</s>";

	public Markov(int n) {

	}

	public Markov(int n, Map<String, Object> opts) {

	}

	public String[] generate() {
		return new String[] { };
	}

	public String[] generate(int n) {
		return new String[] { };
	}

	public String[] generate(int n, Map<String, Object> opts) {
		return new String[] { };
	}

	public String[] generate(Map<String, Object> opts) {
		return new String[] { };
	}

	public void addText(String s) {

	}

	public void addText(String[] s) {

	}

	public String[] completions(String[] preArray) {
		return new String[] { };
	}

	public String[] completions(String[] preArray, String[] postArray) {
		return new String[] { };
	}

	public float probability(String[] dataArray) {
		return (float) 0;
	}

	public float probability(String dataString) {
		return (float) 0;
	}

	public Map<String, Object> probabilities(String pathString) {
		return null;
	}

	public Map<String, Object> probabilities(String[] pathArray) {
		return null;
	}

	public Object _initSentence() {
		return null;
	}

	public Object _initSentence(String[] initWidth) {
		return null;
	}

	public String _flatten(Object nodes) {
		return "";
	}

	public int size() {
		return n;
	}

}
