package rita;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import rita.Markov.Node;

public class Markov {
	public int n;
	public String[] input;
	public Node root;
	// public Node root = new Node();
	private Object tokenize;
	private Object untokenize;
	private int trace;
	private int mlm;
	private int maxAttempts = 99;
	private boolean logDuplicates;
	private boolean disableInputChecks;

	public static String SS = "<s>";
	public static String SE = "</s>";

	public Markov(int n) {

	}

	public Markov(int n, Map<String, Object> opts) {
		this.n = n;
		this.root = new Node("ROOT");
		this.trace = (int) opts.get("trace");
		this.mlm = (int) opts.get("maxLengthMatch");
		this.logDuplicates = (boolean) opts.get("logDuplicates");
		this.maxAttempts = (int) opts.get("maxAttempts");
		this.disableInputChecks = (boolean) opts.get("disableInputChecks");
//		this.tokenize = opts.get("tokenize") || RiTa.tokenize;
//		this.untokenize = opts.get("untokenize") || RiTa.untokenize;

		if (this.mlm <= this.n)
			throw new RiTaException("maxLengthMatch(mlm) must be > N");

		// we store inputs to verify we don't duplicate sentences
//	    if (!this.disableInputChecks || this.mlm) this.input = [];

	}

	public String[] generate() {
		return new String[] {};
	}

	public String[] generate(int n) {
		return new String[] {};
	}

	public String[] generate(int n, Map<String, Object> opts) {
		return new String[] {};
	}

	public String[] generate(Map<String, Object> opts) {
		return new String[] {};
	}

	public void addText(String s) {
		addText(s, 1);
	}

	public void addText(String text, int multiplier) {
//		String[] sents = RiTa.sentences(text);

		// add new tokens for each sentence start/end
//	    String[] tokens;
//	    for (int k = 0; k < multiplier; k++) {
//	      for (int i = 0; i < sents.length; i++) {
//	        String sentence = sents[i].replace("\s+", " ").trim();
//	        String[] words = this.tokenize(sents[i]);
////	        tokens.push(Markov.SS, ...words, Markov.SE);
//	      }
//	      this._treeify(tokens);
//	    }

//	    if (!this.disableInputChecks || this.mlm) this.input.push(...tokens);

	}

	public void addText(String[] s) {

	}

	public String[] completions(String[] preArray) {
		return new String[] {};
	}

	public String[] completions(String[] preArray, String[] postArray) {
		return new String[] {};
	}

	public float probability(String[] dataArray) {
		return (float) 0;
	}

	public float probability(String dataString) {
//		float p = 0;
//		if (dataString.length() != 0) {
//			Node tn = this.root.child(dataString);
//			if (tn != null)
//				p = tn.nodeProb(true); // no meta
//		}
		return 0;
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

	private void _treeify(String[] tokens) {
		_treeify(tokens, this.root);
	}

	private void _treeify(String[] tokens, Node root) {
		for (int i = 0; i < tokens.length; i++) {
			Node node = root;
			String[] words = Arrays.copyOfRange(tokens, i, i + this.n);
			for (int j = 0; j < words.length; j++) {
				node = node.addChild(words[j]);
			}
		}
	}

	public String _flatten(Object nodes) {
		return "";
	}

	public int size() {
		return n;
	}

	public class Node {
		protected HashMap<String, Node> children;
		protected Node parent;
		protected String token;
		protected int numChildren = -1; // for cache
		protected int count = 0;

		public Node(Node p, String word, int c) {
			parent = p;
			token = word;
			count = c;
		}

		public Node(String word) {
			token = word;
		}

		// Find a (direct) child node with matching token, given a word or node
		public Node child(Node word) {
			String lookup = word.token;
			return this.children.get(lookup);
		}

		public Node child(String w) {
			return this.children.get(w);
		}

		public Node pselect() {

			Node[] children = this.childNodes();
//		    const rand = Markov.parent.randomizer;
//		    const weights = children.map(n => n.count);
//		    const pdist = rand.ndist(weights);
//		    return children[rand.pselect(pdist)];
			return new Node("");
		}

		public boolean isLeaf() {
			return this.childCount() < 1;
		}

		public boolean isRoot() {
			return this.parent == null;
		}

		public Node[] childNodes() {
			return childNodes(false);
		}

		public Node[] childNodes(boolean sorted) {
			Collection<Node> kids = this.children.values();
			Node[] kidsArray = kids.toArray(new Node[kids.size()]);
			// TODO:
//			if (sorted) Arrays.sort(kids, new SortByCount());
			return kidsArray;
		}

		public int childCount() {
			return childCount(false);
		}

		public int childCount(boolean excludeMetaTags) {
			if (this.numChildren == -1) {
				int sum = 0;
				for (String k : this.children.keySet()) {
					if (excludeMetaTags && (k == Markov.SS || k == Markov.SE))
						continue;
					sum += this.children.get(k).count;
				}
				this.numChildren = sum;
			}
			return this.numChildren;
		}

		public float nodeProb() {
			return nodeProb(false);
		}

		public float nodeProb(boolean excludeMetaTags) {
			if (this.parent == null)
				throw new RiTaException("no parent");
			return this.count / this.parent.childCount(excludeMetaTags);
		}

		public Node addChild(String word) {
			return addChild(word, 1);
		}

		// Increments count for a child node and returns it
		public Node addChild(String word, int count) {
			if (children == null)
				children = new HashMap<String, Node>();

			Node node = this.children.get(word);
			if (node == null) {
				node = new Node(word);
				this.children.put(word, node);
			}
			node.count += count;
			return node;
		}

		public String toString() {
			BigDecimal probBigDecimal = new BigDecimal(this.nodeProb());
			probBigDecimal = probBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
			return this.parent != null ? this.token + "(" + this.count + "/" + probBigDecimal + "%)" : "Root";
		}

		public String stringify(Node mn, String str, int depth) {
			return stringify(mn, str, depth, false);
		}

		public String stringify(Node mn, String str, int depth, boolean sort) {
			String indent = "\n";
			if (mn.children.size() == 0)
				return str;
			// TODO:
			// if (sort) l.sort();
			for (int j = 0; j < depth; j++)
				indent += "  ";
			for (Node node : mn.children.values()) {
				if (node != null) {
					str += indent + "'" + this.encode(node.token) + "'";
					BigDecimal probBigDecimal = new BigDecimal(node.nodeProb());
					probBigDecimal = probBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
					if (!node.isRoot())
						str += " [" + node.count + ",p=" + probBigDecimal + "]";
					if (!node.isLeaf())
						str += "  {";
					str = this.childCount() > 0 ? this.stringify(node, str, depth + 1, sort) : str + "}";
				}
			}
			indent = "\n";
			for (int j = 0; j < depth - 1; j++)
				indent += "  ";
			return str + indent + "}";
		}

		public String asTree(boolean sort) {
			String s = this.token + " ";
			if (this.parent != null)
				s += "(" + this.count + ")->";
			s += "{";
			return this.childCount() != 0 ? this.stringify(this, s, 1, sort) : s + "}";
		}

		private String encode(String tok) {
			if (tok == "\n")
				tok = "\\n";
			if (tok == "\r")
				tok = "\\r";
			if (tok == "\t")
				tok = "\\t";
			if (tok == "\r\n")
				tok = "\\r\\n";
			return tok;
		}

	}

}

class SortByCount implements Comparator<Node> {
	@Override
	public int compare(Node a, Node b) {
		return b.count - a.count;
	}
}
