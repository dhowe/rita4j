package rita;

import java.util.*;
import java.util.Comparator;

import org.antlr.v4.parse.ANTLRParser.finallyClause_return;

import java.math.BigDecimal;

import rita.Markov.Node;

import static rita.Util.opts;

public class Markov {

	public static String SS = "<s>", SE = "</s>";

	public int n;
	public List<String> input = new ArrayList<>();
	public Node root;

	protected int mlm, maxAttempts = 99;
	protected boolean trace, disableInputChecks, logDuplicates;
	protected int treeifyTimes = 0;

	public Markov(int n) {
		this(n, null);
	}

	public Markov(int n, Map<String, Object> opts) {

		this.n = n;
		this.root = new Node("ROOT");

		this.maxAttempts = Util.intOpt("maxAttempts", opts, 99);
		this.trace = Util.boolOpt("trace", opts);
		this.logDuplicates = Util.boolOpt("logDuplicates", opts); // ?
		this.disableInputChecks = Util.boolOpt("disableInputChecks", opts);

		this.mlm = Util.intOpt("maxLengthMatch", opts, 0);
		if (mlm != 0 && mlm <= n) throw new RiTaException("[Markov] maxLengthMatch(mlm) must be > N");
	}

	public String toString() {
		Node root = this.root;
		System.out.println("root's children: " + this.root.children.toString() + "last words' children"
				+ this.root.children.get(this.input.get(this.input.size() - 2)).children);// if dont visit the last word's children, and the root's children before call astree, they disappear???
		return root.asTree(true).replaceAll("\\{\\}", "");
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

	public String[] generate(int num, Map<String, Object> opts) {

		int minLength = Util.intOpt("minLength", opts, 5);
		int maxLength = Util.intOpt("maxLength", opts, 35);
		float temp = Util.floatOpt("temperature", opts, 0);
		boolean allowDups = Util.boolOpt("allowDuplicates", opts);
		String[] startTokens = this.startTokens(opts);

		List<String> result = new ArrayList<>();
		List<Node> tokens = new ArrayList<>();

		int tries = 0;
		while (result.size() < num) {

			Node[] arr = this.initSentence(startTokens);
			if (tokens != null && arr != null) {
				tokens.addAll(new ArrayList<>(Arrays.asList(arr)));
			}

			if (tokens == null) throw new RiTaException("[Markov] No sentence starts with: '" + startTokens + "'");

			while (tokens != null && tokens.size() < maxLength) {
				Node[] tokensArray = tokens.toArray(new Node[tokens.size()]);
				Node parent = this._pathTo(tokensArray);
				if (parent == null || parent.isLeaf()) {
					fail(tokens, "no parent", ++tries);
					break;
				}

				Node next = this._selectNext(parent, temp, tokensArray);
				if (next == null) {
					fail(tokens, "no next", ++tries);
					break; // possible if all children excluded
				}

				tokens.add(next);
				if (next.token == Markov.SE) {
					tokens.remove(tokens.size() - 1);
					if (tokens.size() >= minLength) {
						List<String> rawtoks = new ArrayList<>();
						tokens.forEach(t -> rawtoks.add(t.token));

						// TODO: do we need this if checking mlm with each word? yes
						if (isSubArrayList(rawtoks, this.input)) {
							fail(tokens, "in input", ++tries);
							break;
						}

						String sent = this._flatten(tokens);
						if (!allowDups && result.contains(sent)) {
							fail(tokens, "is dup", ++tries);
							break;
						}

						if (this.trace) System.out.println("-- GOOD " + sent.replaceAll(" +", " "));

						result.add(sent.replaceAll(" +", " "));
						break;
					}

					fail(tokens, "too short", ++tries);
					break;
				}
			}
			if (tokens != null && tokens.size() >= maxLength) {
				fail(tokens, "too long", ++tries);
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public void addText(String s) {
		addText(s, 1);
	}

	public void addText(String text, int multiplier) {
		addText(RiTa.sentences(text), multiplier);
	}

	public void addText(String[] sents) {
		addText(sents, 1);
	}

	public void addText(String[] sents, int multiplier) {

		// add new tokens for each sentence start/end
		List<String> toAdd = new ArrayList<String>();
		List<String> tokens = new ArrayList<String>();
		for (int k = 0; k < multiplier; k++) {
			for (int i = 0; i < sents.length; i++) {
				String[] words = RiTa.tokenize(sents[i]);
				toAdd.clear(); // Q: is toAdd needed here?
				toAdd.add(Markov.SS);
				toAdd.addAll(Arrays.asList(words));
				toAdd.add(Markov.SE);
				tokens.addAll(toAdd);
			}
			this._treeify(tokens.toArray(new String[0]));
		}

		if (!this.disableInputChecks || this.mlm != 0) {
			this.input.addAll(tokens);
		}
	}

	public String[] completions(String[] preArray) {
		throw new RuntimeException("Implement me"); // TODO: + check tests
	}

	public String[] completions(String[] preArray, String[] postArray) {
		throw new RuntimeException("Implement me"); // TODO: + check tests
	}

	public float probability(String[] dataArray) {
		if (dataArray == null || dataArray.length == 0) {
			return 0;
		}
		Node tn = this._pathTo(dataArray);
		if (tn != null && tn.token.length() > 0) {
			return (float) tn.nodeProb(true);
		}
		else {
			return 0;
		}
	}

	public double probability(String path) {
		double p = 0;
		if (path.length() != 0) {
			Node tn = this.root.child(path);
			if (tn != null) p = tn.nodeProb(true); //true=excludeMetaTags
		}
		return p;
	}

	public Map<String, Object> probabilities(String path) {
		return probabilities(RiTa.tokenize(path), 0);
	}

	public Map<String, Object> probabilities(String path, double temp) {
		return probabilities(RiTa.tokenize(path), temp);
	}

	public Map<String, Object> probabilities(String[] path) {
		return probabilities(path, 0);
	}

	public Map<String, Object> probabilities(String[] path, double temp) {
		Map<String, Object> probs = new HashMap<String, Object>();
		Node parent = this._pathTo(path);
		if (parent != null) {
			Node[] children = parent.childNodes();
			List<Integer> weights = new ArrayList<>();
			for (Node n : children) {
				weights.add(n.count);
			}
			int[] wArr = weights.stream().mapToInt(i -> i).toArray();
			double[] pdist = RandGen.ndist(wArr, temp);
			for (int i = 0; i < children.length; i++) {
				probs.put(children[i].token, pdist[i]);
			}
		}
		return probs;
	}

	////////////////////////////// end API ////////////////////////////////

	private void fail(List<Node> tokens, String msg, int t) {
		this._logError(t, tokens, msg);
		if (t >= this.maxAttempts) {
			throw new RiTaException("[Markov] Exceeded maxAttempts:" + t);
		}
	}

	private String[] startTokens(Map<String, Object> opts) {
		String[] startTokens = new String[0];
		if (opts.containsKey("startTokens")) {
			Object st = opts.get("startTokens");
			if (st.getClass().getName() == "java.lang.String")
				startTokens = RiTa.tokenize((String) st);
			else
				startTokens = (String[]) st;
		}
		return startTokens;
	}

	private boolean validateMlms(Node word, Node[] nodes) {
		List<String> check = new ArrayList<>();
		for (Node node : nodes) {
			check.add(node.token);
		}
		check.add(word.token); // string
		int lastX = this.mlm + 1;
		List<String> subArr = new ArrayList<String>(check.subList(check.size() - lastX, check.size()));
		return !isSubArrayList(subArr, this.input);
	}

	private Node _selectNext(Node parent, double temp, Node[] tokens) {
		// basic case: just prob. select from children
		if (this.mlm != 0 || this.mlm > tokens.length) {
			return parent.pselect();
		}

		Node[] children = parent.childNodes();
		List<Integer> weights = new ArrayList<>();
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
			if (selector < pTotal && validateMlms(next, tokens)) {
				return next;
			}
		}
		return null;
	}

	public Node[] initSentence() {
		return initSentence(null, this.root);
	}

	public Node[] initSentence(String[] initWith) {
		return initSentence(initWith, this.root);
	}

	protected Node[] initSentence(String[] initWith, Node root) {

		List<Node> tokens = new ArrayList<>();

		if (initWith.length > 0) {
			Node st = this._pathTo(initWith, root);
			if (st == null)
				return null; // fail
			while (!st.isRoot()) {
				tokens.add(0, st);
				st = st.parent;
			}
		}
		else {
			tokens.add(this.root.child(Markov.SS).pselect());
		}
		return tokens.toArray(new Node[tokens.size()]);
	}

	private void _treeify(String[] tokens) {
		_treeify(tokens, this.root);
	}

	private void _treeify(String[] tokens, Node root) {
		int order = 0;
		for (int i = 0; i < tokens.length; i++) {
			Node node = root;
			String[] words = Arrays.copyOfRange(tokens, i, i + this.n);
			for (int j = 0; j < words.length; j++) {
				if (words[j] != null) {
					node = node.addChild(words[j], this.treeifyTimes + order);
					order++;
				}
			}
		}
		this.treeifyTimes += order;
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

	public String _flatten(List<Node> tokens) {
		return _flatten(tokens.toArray(new Node[tokens.size()]));
	}

	public void _logError(int tries, List<Node> toks, String msg) {
		if (this.trace)
			System.out
					.println(tries + " FAIL" + (msg.length() > 0 ? "(" + msg + ")" : "") + ": " + this._flatten(toks));
	}

	public int size() {
		return this.root.childCount();
	}

	private boolean isSubArrayList(List<String> find, List<String> arr) {
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
		protected Map<String, Node> children;
		protected Node parent;
		protected String token;
		protected int numChildren = -1; // for cache
		protected int count = 0;
		protected int inputOrder = -1; // for sorting according to the input order

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
			List<Integer> weights = new ArrayList<>();
			for (Node n : children) {
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
			return childCount(true);
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

		public double nodeProb() {
			return nodeProb(false);
		}

		public double nodeProb(boolean excludeMetaTags) {
			if (this.parent == null) {
				throw new RiTaException("no parent");
			}
			//System.out.println("Markov.Node.nodeProb(): node name: " + this.token + "; using" + this.count + "/"
			//		+ this.parent.childCount(excludeMetaTags) + "; parent: " + this.parent.token);
			double result = (double) this.count / (double) this.parent.childCount(excludeMetaTags);
			// System.out.println("C:" + this.count + " " +
			// this.parent.childCount(excludeMetaTags) + "->" + result);
			return result;
		}

		public Node addChild(String word, int order) {
			return addChild(word, 1, order);
		}

		// Increments count for a child node and returns it
		public Node addChild(String word, int count, int order) {
			if (children == null)
				children = new HashMap<String, Node>();
			Node node = this.children.get(word);
			if (node == null) {
				node = new Node(this, word);
				node.inputOrder = order;
				this.children.put(word, node);
			}
			node.count += count;
			return node;
		}

		public String toString() {
			if (this.parent == null)
				return "Root";
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
			// TODO:addsort => now sort with input order
			ArrayList<Node> l = new ArrayList<Node>(mn.children.values());
			if (sort) {
				l.sort(this.ByInputOrder);
			}
			for (int j = 0; j < depth; j++)
				indent += "  ";

			for (int i = 0; i < l.size(); i++) {

				Node node = l.get(i);
				if (node != null) {
					str += indent + "\"" + this.encode(node.token) + "\"";
					if (!node.isRoot()) {
						//System.out.println(
						//		"Markov.Node.stringify() node name: " + node.token + "; prob: " + node.nodeProb() + "; parent: " + node.parent.token);
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
			//System.out.println(this.childCount());
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

		public int getInputOrder() {
			if (!this.isRoot()) {
				return this.inputOrder;
			}
			else {
				return -1;
			}
		}

		public Comparator<Markov.Node> ByInputOrder = new Comparator<Markov.Node>() {
			public int compare(Markov.Node node1, Markov.Node node2) {
				int n1 = node1.getInputOrder();
				int n2 = node2.getInputOrder();

				return n1 - n2;
			}
		};
	}

}

class SortByCount implements Comparator<Node> {
	@Override
	public int compare(Node a, Node b) {
		return b.count - a.count;
	}
}
