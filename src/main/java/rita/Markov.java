package rita;

import java.util.*;
import java.text.DecimalFormat;

import static rita.Util.opts;

public class Markov {

	public static String SS = "<s>", SE = "</s>";
	private static DecimalFormat DF = new DecimalFormat("0.000");

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
			this.treeify(tokens.toArray(new String[0]));
		}

		if (!this.disableInputChecks || this.mlm != 0) {
			this.input.addAll(tokens);
		}
	}

	public String toString() {
		// Node root = this.root;
		//		System.out.println("root's children: " + this.root.children.toString() + "last words' children"
		//				+ this.root.children.get(this.input.get(this.input.size() - 2)).children);// if dont visit the last word's children, and the root's children before call astree, they disappear???
		return this.root.asTree().replaceAll("\\{\\}", "");
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

	public String[] completions(String[] preArray) {
		throw new RuntimeException("Implement me"); // TODO: + check tests
	}

	public String[] completions(String[] preArray, String[] postArray) {
		throw new RuntimeException("Implement me"); // TODO: + check tests
	}

	public float probability(String[] dataArray) {
		throw new RuntimeException("Implement me"); // TODO: + check tests
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

	private void treeify(String[] tokens) {
		Node root = this.root;
		for (int i = 0; i < tokens.length; i++) {
			Node node = root;
			//words = tokens.slice(i, i + this.n);
			String[] words = Arrays.copyOfRange(tokens, i, i + this.n);
			for (int j = 0; j < words.length; j++) {
				if (words[j] != null) {
					node = node.addChild(words[j]);
				}
			}
		}
	}

	private void treeifyX(String[] tokens) {

		int order = 0;
		Node root = this.root;
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

	private static String stringulate(Node mn, String str, int depth, boolean sort) {
		String indent = "\n";
		if (mn.children == null || mn.children.size() == 0) {
			return str;
		}

		List<Node> l = new ArrayList<Node>(mn.children.values());
		l.sort(byCount);

		for (int j = 0; j < depth; j++)
			indent += "  ";

		for (int i = 0; i < l.size(); i++) {

			Node node = l.get(i);
			//if (node.token == SS || node.token == SE) continue;
			if (node != null && node.token != null) {
				str += indent + "'" + encode(node.token) + "'";
				if (!node.isRoot()) {
					String prob = DF.format(node.nodeProb());
					str += " [" + node.count + ",p=" + prob + "]";
				}
				if (!node.isLeaf()) str += "  {";

				str = mn.childCount() > 0 ? stringulate(node, str, depth + 1, sort) : str + "}";
			}
		}

		indent = "\n";
		for (int j = 0; j < depth - 1; j++)
			indent += "  ";

		return str + indent + "}";
	}

	private static String encode(String tok) {
		if (tok != null) {
			if (tok.equals("\n")) {
				tok = "\\n";
			}
			else if (tok.equals("\r")) {
				tok = "\\r";
			}
			else if (tok.equals("\t")) {
				tok = "\\t";
			}
			else if (tok.equals("\r\n")) {
				tok = "\\r\\n";
			}
		}
		return tok;
	}

	//////////////////////////////////////////////////////////////////////////

	public class Node {
		
		protected Node parent;
		protected String token;
		protected int count = 0, numChildren = -1;
		protected Map<String, Node> children;

		Node(String word) { // for root only
			this(null, word);
		}

		public Node(Node p, String word) {
			this(p, word, 1); // JC: never more than one 'real' constructor
		}

		private Node(Node par, String word, int cnt) {
			parent = par;
			token = word;
			count = cnt;
		}

		// Find a (direct) child node with matching token
		public Node child(Node word) {
			return this.child(word.token); // JC: delegate to other method
		}

		// Find a (direct) child node with matching token
		public Node child(String word) {
			return this.children != null ? this.children.get(word) : null;
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
			if (children == null) return new Node[0];

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
			if (this.numChildren == -1) { // a sort of cache
				int sum = 0;
				if (this.children == null) return 0;
				for (String k : this.children.keySet()) {
					if (k == null || (excludeMetaTags && (k.equals(Markov.SS) || k.equals(Markov.SE)))) {
						continue;
					}
					sum += this.children.get(k).count;
				}
				this.numChildren = sum;
			}
			//System.out.println(this.token+" "+this.numChildren);
			return this.numChildren;
		}

		public double nodeProb() {
			return nodeProb(false);
		}

		public double nodeProb(boolean excludeMetaTags) {
			if (this.parent == null) {
				throw new RiTaException("no parent for: " + this);
			}
			//System.out.println("Markov.Node.nodeProb(): node name: " + this.token + "; using" + this.count + "/"
			//		+ this.parent.childCount(excludeMetaTags) + "; parent: " + this.parent.token);
			double result = (double) this.count / (double) this.parent.childCount(excludeMetaTags);
			// System.out.println("C:" + this.count + " " +
			// this.parent.childCount(excludeMetaTags) + "->" + result);
			return result;
		}

		public Node addChild(String word) {
			return addChild(word, 1);
		}

		public Node addChild(String word, int count) {
			this.numChildren = -1; // invalidate cache
			if (this.children == null) {
				this.children = new HashMap<String, Node>();
			}
			Node node = this.children.get(word);
			if (node == null) {
				node = new Node(this, word);
				this.children.put(word, node);
			}
			else {
				node.count += count;
			}
			return node;
		}

		public String asTree() {
			String s = this.token + " ";
			if (this.parent != null) {
				s += "(" + this.count + ")->";
			}
			s += "{";
			//System.out.println(this.childCount());
			return this.childCount() != 0 ? stringulate(this, s, 1, true) : s + "}";
		}
		
		public String toString() {
			if (this.parent == null) return "Root";
			String prob = DF.format(this.nodeProb());
			//			BigDecimal probBigDecimal = new BigDecimal(this.nodeProb());
			//			probBigDecimal = probBigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP);
			return this.token + "(" + this.count + "/" + prob + "%)";
		}
	}
	
	static final Comparator<Markov.Node> byCount = new Comparator<Markov.Node>() {
		public int compare(Markov.Node a, Markov.Node b) {
			return b.count != a.count ? b.count - a.count : b.token.toLowerCase().compareTo(a.token.toLowerCase());
		}
	};
	
	public static void main(String[] args) {
		Markov rm = new Markov(2);
		rm.addText("The");
		System.out.println(rm.root.asTree());
	}
}

