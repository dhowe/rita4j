package rita;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import rita.Markov.Node;
import static rita.Util.opts;

public class Markov {
	public int n;
	public ArrayList<String> input = new ArrayList<>();
	public Node root;
	// public Node root = new Node();

	private int mlm;
	private int maxAttempts = 99;

	private boolean trace;
	private boolean logDuplicates;
	private boolean disableInputChecks;

	public static String SS = "<s>";
	public static String SE = "</s>";

	public Markov(int n) {
		this.n = n;
		this.root = new Node(null, "ROOT");
	}

	public Markov(int n, Map<String, Object> opts) {
		this.n = n;
		this.root = new Node("ROOT");
		if (opts.containsKey("trace"))
			trace = (boolean) opts.get("trace");
		if (opts.containsKey("maxLengthMatch"))
			mlm = (int) opts.get("maxLengthMatch");
		if (opts.containsKey("logDuplicates"))
			logDuplicates = (boolean) opts.get("logDuplicates");
		if (opts.containsKey("maxAttempts"))
			maxAttempts = (int) opts.get("maxAttempts");
		if (opts.containsKey("disableInputChecks"))
			disableInputChecks = (boolean) opts.get("disableInputChecks");

		if (mlm != 0 && mlm <= n)
			throw new RiTaException("maxLengthMatch(mlm) must be > N");

		// we store inputs to verify we don't duplicate sentences
//	    if (!this.disableInputChecks || this.mlm) this.input = [];

	}

	public String[] tokenize(String text) {
		// TODO: this.tokenize = opts.get("tokenize") || RiTa.tokenize;
		return RiTa.tokenize(text);

	}

	public String untokenize(String[] text) {
		// TODO: this.untokenize = opts.get("untokenize") || RiTa.untokenize;
		return RiTa.untokenize(text);
	}
	
	public String toString() {
	    Node root = this.root;
	    return root.asTree().replaceAll("\\{\\}", "");
	}

	public String[] generate() {
		return generate(1, null);
	}

	public String[] generate(int n) {
		return generate(n, opts());
	}

	public String[] generate(Map<String, Object> opts) {
		return generate(1, opts);
	}

	public String[] generate(int count, Map<String, Object> opts) {

		int num = count;
		int minLength = 5;
		int maxLength = 35;
		float temp = 0;

		boolean allowDuplicates = false;
		String[] startTokens = new String[] {};

		if (opts.containsKey("temperature"))
			temp = (float) opts.get("temperature");
		if (opts.containsKey("minLength"))
			minLength = (int) opts.get("minLength");
		if (opts.containsKey("maxLength"))
			maxLength = (int) opts.get("maxLength");
		if (opts.containsKey("allowDuplicates"))
			allowDuplicates = (boolean) opts.get("allowDuplicates");
		if (opts.containsKey("startTokens")) {
			Object st = opts.get("startTokens");
			if (st.getClass().getName() == "java.lang.String")
				startTokens = this.tokenize((String) st);
			else
				startTokens = (String[]) st;
		}

		ArrayList<String> result = new ArrayList<>();
		ArrayList<Node> tokens = new ArrayList<>();

		int tries = 0;
		BiConsumer<String, Integer> fail = (msg, t) -> {
			this._logError(t, tokens, msg);
			if (t >= this.maxAttempts)
				throw new RiTaException("Exceed maxAttempts:" + t);
		};

		while (result.size() < num) {

			Node[] arr = this._initSentence(startTokens);
			if (tokens != null && arr != null)
				tokens.addAll(new ArrayList<>(Arrays.asList(arr)));
			if (tokens == null)
				throw new RiTaException("No sentence starts with: '" + startTokens + "'");

			while (tokens != null && tokens.size() < maxLength) {
				Node[] tokensArray = tokens.toArray(new Node[tokens.size()]);

				Node parent = this._pathTo(tokensArray);
				if (parent == null || parent.isLeaf()) {
					fail.accept("no parent", ++tries);
					break;
				}

				Node next = this._selectNext(parent, temp, tokensArray);
				if (next == null) {
					fail.accept("no next", ++tries);
					break; // possible if all children excluded
				}

				tokens.add(next);
				if (next.token == Markov.SE) {
					tokens.remove(tokens.size()-1);
					if (tokens.size() >= minLength) {
						ArrayList<String> rawtoks = new ArrayList<>();
						tokens.forEach(t -> rawtoks.add(t.token));

						// TODO: do we need this if checking mlm with each word? yes
						if (isSubArrayList(rawtoks, this.input)) {
							fail.accept("in input", ++tries);
							break;
						}

						String sent = this._flatten(tokens);
						if (!allowDuplicates && result.contains(sent)) {
							fail.accept("is dup", ++tries);
							break;
						}
						if (this.trace)
							System.out.println("-- GOOD " + sent.replaceAll(" +", " "));
						result.add(sent.replaceAll(" +", " "));
						break;
					}
					fail.accept("too short", ++tries);
					break;
				}
			}
			if (tokens != null && tokens.size() >= maxLength) {
				fail.accept("too long", ++tries);
			}
				
		}

		return result.toArray(new String[result.size()]);
	}

	public void addText(String text, int multiplier) {
		String[] sents = RiTa.sentences(text);
		addText(sents, 1);
	}

	public void addText(String s) {
		addText(s, 1);
	}

	public void addText(String[] sents) {
		addText(sents, 1);
	}

	public void addText(String[] sents, int multiplier) {

		// add new tokens for each sentence start/end
		ArrayList<String> tokens = new ArrayList<>();
		for (int k = 0; k < multiplier; k++) {
			for (int i = 0; i < sents.length; i++) {
				String[] words = this.tokenize(sents[i]);
				ArrayList<String> toAdd = new ArrayList<String>(Arrays.asList(words));
				toAdd.add(0, Markov.SS);
				toAdd.add(Markov.SE);
				tokens.addAll(toAdd);
			}
			String[] tokensArr = tokens.toArray(new String[tokens.size()]);
			this._treeify(tokensArr);
		}

		if (!this.disableInputChecks || this.mlm != 0) {
			this.input.addAll(tokens);
		}

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

	////////////////////////////// end API ////////////////////////////////

	private Node _selectNext(Node parent, double temp, Node[] tokens) {
		// basic case: just prob. select from children
		if (this.mlm != 0 || this.mlm > tokens.length) {
			return parent.pselect();
		}

		BiPredicate<Node, Node[]> validateMlms = (word, nodes) -> {
			ArrayList<String> check = new ArrayList<>();
			for (Node node: nodes) {
				check.add(node.token);
			}
			check.add(word.token); // string
			int lastX = this.mlm + 1;
			ArrayList<String> subArr = new ArrayList<String>(check.subList(check.size() - lastX, check.size()));
			return !isSubArrayList(subArr, this.input);
		};

		Node[] children = parent.childNodes();
		ArrayList<Integer> weights = new ArrayList<>();
		for (Node n : children) {
			weights.add(n.count);
		}
		int[] wArr = weights.stream().mapToInt(i -> i).toArray();
		double[] pdist = RandGen.ndist(wArr, temp);
		int tries = children.length * 2;
		float selector = RandGen.random();

		// loop 2x here as selector may skip earlier nodes
		for (int i = 0, pTotal = 0; i < tries; i++) {
			int idx = i % children.length;
			pTotal += pdist[idx];
			Node next = children[idx];
			if (selector < pTotal && validateMlms.test(next, tokens)) {
				return next;
			}
		}
		return null;
	}

	public Node[] _initSentence() {
		return _initSentence(null, this.root);
	}

	public Node[] _initSentence(String[] initWith) {
		return _initSentence(initWith, this.root);
	}

	public Node[] _initSentence(String[] initWith, Node root) {

		ArrayList<Node> tokens = new ArrayList<>();

		if (initWith.length > 0) {
			Node st = this._pathTo(initWith, root);
			if (st == null)
				return null; // fail
			while (!st.isRoot()) {
				tokens.add(0, st);
				st = st.parent;
			}
		} else {
			tokens.add(this.root.child(Markov.SS).pselect());
		}
		return tokens.toArray(new Node[tokens.size()]);
	}

	private void _treeify(String[] tokens) {
		_treeify(tokens, this.root);
	}

	private void _treeify(String[] tokens, Node root) {
		for (int i = 0; i < tokens.length; i++) {
			Node node = root;
			String[] words = Arrays.copyOfRange(tokens, i, i + this.n);
			for (int j = 0; j < words.length; j++) {
				if(words[j] != null) node = node.addChild(words[j]);
			}
		}
	}

	private Node _pathTo(Node[] path) {
		return _pathTo(path, this.root);
	}

	private Node _pathTo(String[] path) {
		return _pathTo(path, this.root);
	}

	private Node _pathTo(String[] path, Node root) {
		if (path == null || path.length == 0 || this.n < 2)
			return root;
		int idx = Math.max(0, path.length - (this.n - 1));
		Node node = root.child(path[idx++]);
		for (int i = idx; i < path.length; i++) {
			if (node != null)
				node = node.child(path[i]);
		}
		return node; // can be undefined
	}

	private Node _pathTo(Node[] path, Node root) {
		if (path == null || path.length == 0 || this.n < 2)
			return root;
		int idx = Math.max(0, path.length - (this.n - 1));
		Node node = root.child(path[idx++]);
		for (int i = idx; i < path.length; i++) {
			if (node != null)
				node = node.child(path[i]);
		}
		return node; // can be undefined
	}

	public String _flatten(Node[] nodes) {
		return "";
	}
	public String _flatten(ArrayList<Node> tokens) {
		return _flatten(tokens.toArray(new Node[tokens.size()]));
	}
	
	public void _logError(int tries, ArrayList<Node> toks, String msg) {
	    if(this.trace)
	    	System.out.println(tries + " FAIL" + (msg.length() > 0 ? "(" + msg + ")" : "") + ": " + this._flatten(toks));
	}
	
	

	public int size() {
		return this.root.childCount();
	}

	private boolean isSubArrayList(ArrayList<String> find, ArrayList<String> arr) {
		if (arr == null || arr.size() == 0)
			return false;
		OUT: for (int i = find.size() - 1; i < arr.size(); i++) {
			for (int j = 0; j < find.size(); j++) {
				if (find.get(find.size() - j - 1) != arr.get(i - j))
					continue OUT;
				if (j == find.size() - 1)
					return true;
			}
		}
		return false;
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
		
		public Node(Node p, String word) {
			token = word;
			parent = p;
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
			ArrayList<Integer> weights = new ArrayList<>();
			for(Node n: children) {
				weights.add(n.count);
			}
			int[] wArr = weights.stream().mapToInt(i -> i).toArray();
		    double[] pdist = RandGen.ndist(wArr);
		    return children[RandGen.pselect(pdist)];
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
				if (this.children == null)
					return 0;
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
				node = new Node(this, word);
				this.children.put(word, node);
			}
			node.count += count;
			return node;
		}

		public String toString() {
			if (this.parent == null) return "Root";
			BigDecimal probBigDecimal = new BigDecimal(this.nodeProb());
			probBigDecimal = probBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
			return this.token + "(" + this.count + "/" + probBigDecimal + "%)";
		}

		public String stringify(Node mn, String str, int depth) {
			return stringify(mn, str, depth, false);
		}

		public String stringify(Node mn, String str, int depth, boolean sort) {
			String indent = "\n";
			if (mn.children == null || mn.children.size() == 0)
				return str;
			// TODO:
			// if (sort) l.sort();
			for (int j = 0; j < depth; j++)
				indent += "  ";

			for (Node node : mn.children.values()) {
				
				if (node != null) {
					str += indent + "'" + this.encode(node.token) + "'";
					if (!node.isRoot()) {
						BigDecimal probBigDecimal = new BigDecimal(node.nodeProb());
						probBigDecimal = probBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
						str += " [" + node.count + ",p=" + probBigDecimal + "]";
					}
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
		
		public String asTree() {
			return asTree(false);
		}

		public String asTree(boolean sort) {
			String s = this.token + " ";
			if (this.parent != null)
				s += "(" + this.count + ")->";
			s += "{";
			System.out.println(this.childCount());
			return this.childCount() != 0 ? stringify(this, s, 1, sort) : s + "}";
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
