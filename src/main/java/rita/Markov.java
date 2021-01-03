package rita;

import static rita.RiTa.opts;

import java.text.DecimalFormat;

import java.util.*;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Markov {

	public static String SS = "<s>", SE = "</s>";
	private static DecimalFormat DF = new DecimalFormat("0.000");

	public int n;
	public Node root;
	public List<String> input;
	protected Function<String, String[]> _tokenize;
	protected Function<String[], String> _untokenize;

	protected int mlm, treeifyTimes, maxAttempts = 999;
	protected boolean trace, disableInputChecks, logDuplicates;

	public Markov(int n) {
		this(n, null);
	}

	@SuppressWarnings("unchecked")
	public Markov(int n, Map<String, Object> opts) {

		this.n = n;
		this.root = new Node("ROOT");

		if (opts != null && opts.containsKey("tokenize")) {
			this._tokenize = (Function<String, String[]>) opts.get("tokenize");
		}

		this.trace = Util.boolOpt("trace", opts);
		this.maxAttempts = Util.intOpt("maxAttempts", opts, 99);
		this.logDuplicates = Util.boolOpt("logDuplicates", opts); // ?
		this.disableInputChecks = Util.boolOpt("disableInputChecks", opts);

		this.mlm = Util.intOpt("maxLengthMatch", opts, 0);
		if (mlm != 0 && mlm <= n) {
			throw new RiTaException("[Markov] maxLengthMatch(mlm) must be > N");
		}
		if (mlm > 0 && disableInputChecks) {
			throw new RiTaException("[Markov] 'disableInputChecks' and "
					+ "'maxLengthMatch' cannot be used together");
		}

		// only create 'input' if we have an mlm
		if (this.mlm != 0 || !this.disableInputChecks) {
			this.input = new ArrayList<>();
		}
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
		List<String> tokens = new ArrayList<String>();
		for (int k = 0; k < multiplier; k++) {
			for (int i = 0; i < sents.length; i++) {
				String[] words = this.doTokenize(sents[i]);
				tokens.add(Markov.SS);
				tokens.addAll(Arrays.asList(words));
				tokens.add(Markov.SE);
			}
			this.treeify(tokens.toArray(new String[tokens.size()]));
		}

		if (!this.disableInputChecks || this.mlm != 0) {
			this.input.addAll(tokens);
		}
	}
	
	/*TODO: ? 
	public void addTokens(String[] words) {
		this.addTokens(words, 1);
	}
	
	public void addTokens(String[] words, int multiplier) {
		for (int k = 0; k < multiplier; k++) {
			this.treeify(words);
		}
		if (!this.disableInputChecks || this.mlm != 0) {
			this.input.addAll(Arrays.asList(words));
		}
	}*/
	
	public String toString() {
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
			// startTokens should be added just once
			if (tokens != null && arr != null && tokens.size() == 0) {
				tokens.addAll(new ArrayList<>(Arrays.asList(arr)));
			}

			if (tokens.size() == 0) throw new RiTaException("[Markov] No sentence starts with: '" + startTokens + "'");

			while (tokens.size() != 0 && tokens.size() < maxLength) {
				Node[] tokensArray = tokens.toArray(new Node[tokens.size()]);
				Node parent = this._pathTo(tokensArray);
				if (parent == null || parent.isLeaf()) {
					fail(tokens, "no parent", ++tries);
					tokens = new ArrayList<>();// to reset tokens
					break;
				}

				Node next = this._selectNext(parent, temp, tokensArray);
				if (next == null) {
					fail(tokens, "no next", ++tries);
					tokens = new ArrayList<>();// to reset tokens
					break; // possible if all children are excluded
				}

				tokens.add(next);
				if (next.token.equals(Markov.SE)) {
					tokens.remove(tokens.size() - 1);

					if (tokens.size() >= minLength) {

						List<String> stringToks = new ArrayList<>();
						tokens.forEach(t -> stringToks.add(t.token));

						if (isSubArrayList(stringToks, this.input)) {
							fail(tokens, "in input", ++tries);
							tokens = new ArrayList<>();// to reset tokens
							break;
						}

						String sent = this._flatten(stringToks);
						if (!allowDups && result.contains(sent)) {
							fail(tokens, "is dup", ++tries);
							tokens = new ArrayList<>();// to reset tokens
							break;
						}

						if (this.trace) System.out.println("-- GOOD " + sent.replaceAll(" +", " "));

						result.add(sent.replaceAll(" +", " "));
						break;
					}

					fail(tokens, "too short", ++tries);
					tokens = new ArrayList<>();// to reset tokens
					break;
				}
			}
			if (tokens != null && tokens.size() >= maxLength) {
				fail(tokens, "too long", ++tries);
				tokens = new ArrayList<>();// to reset tokens
			}
		}

		return result.toArray(new String[result.size()]);
	}

	public String[] completions(String[] preArray) {
		return completions(preArray, null);
	}

	public String[] completions(String[] preArray, String[] postArray) {
		String[] result;
		if (postArray != null) {
			ArrayList<String> res = new ArrayList<String>();
			if (preArray.length + postArray.length > this.n) {
				throw new RiTaException("sum of preArray length and postArray"
						+ " length should be no bigger then n, was: "
						+ (preArray.length + postArray.length));
			}
			Node tn = this._pathTo(preArray);
			if (tn == null) {
				if (!RiTa.SILENT) {
					System.err.println("Markov.completions() WARNING: no node found in preArray");
				}
				return new String[0];
			}
			Node[] next = tn.childNodes();
			for (int i = 0; i < next.length; i++) {
				ArrayList<String> atestList = new ArrayList<String>(Arrays.asList(preArray));
				ArrayList<String> toAdd = new ArrayList<String>();
				toAdd.add(next[i].token);
				for (int j = 0; j < postArray.length; j++) {
					if (!postArray[j].equals(next[i].token)) {
						toAdd.add(postArray[j]);
					}
				}
				atestList.addAll(toAdd);
				String[] atest = new String[atestList.size()];
				for (int j = 0; j < atestList.size(); j++) {
					atest[j] = atestList.get(j);
				}
				if (this._pathTo(atest) != null) {
					res.add(next[i].token);
				}
			}
			result = new String[res.size()];
			for (int i = 0; i < res.size(); i++) {
				result[i] = res.get(i);
			}
		}
		else {
			Map<String, Object> pr = this.probabilities(preArray);
			Set<String> keys = pr.keySet();
			ArrayList<String> keysArrayList = new ArrayList<String>();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				keysArrayList.add(key);
			}
			Collections.sort(keysArrayList, new Comparator<String>() {
				@Override
				public int compare(String str1, String str2) {
					if (pr.get(str1) instanceof Number && pr.get(str2) instanceof Number) {
						double n1 = ((Number) pr.get(str1)).doubleValue();
						double n2 = ((Number) pr.get(str2)).doubleValue();
						int resInt = n2 - n1 > 0 ? 1 : -1;
						return n1 != n2 ? resInt : str1.compareTo(str2);
					}
					else {
						return 0;
					}
				}
			});
			result = new String[keysArrayList.size()];
			for (int i = 0; i < keysArrayList.size(); i++) {
				result[i] = keysArrayList.get(i);
			}
		}
		return result;
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
			if (tn != null) {
				p = tn.nodeProb(true);
			} //true=excludeMetaTags
		}
		return p;
	}

	public Map<String, Object> probabilities(String path) {
		return probabilities(this.doTokenize(path), 0);
	}

	public Map<String, Object> probabilities(String path, double temp) {
		return probabilities(this.doTokenize(path), temp);
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

	public int size() {

		return this.root.childCount();
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
		if (opts == null) {
			return startTokens;
		}
		if (opts.containsKey("startTokens")) {
			Object st = opts.get("startTokens");
			if (st instanceof String) {
				//if (st.getClass().getName().equals("java.lang.String"))
				startTokens = this.doTokenize((String) st);
			}
			else {
				startTokens = (String[]) st;
			}
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
		if (this.mlm > 0 && this.input == null) { // double-check
			throw new RiTaException("[Markov] Invalid state: mlm > 0 and null input list");
		}
		return !isSubArrayList(subArr, this.input);
	}

	private Node _selectNext(Node parent, double temp, Node[] tokens) {
		// basic case: just prob. select from children
		if (this.mlm == 0 || this.mlm > tokens.length) { //if no mlm input or tokens.length < mlm
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
		if (initWith == null) {
			tokens.add(root.child(Markov.SS).pselect());
		}
		else {
			if (initWith.length > 0) {
				Node st = this._pathTo(initWith, root);
				if (st == null) {
					return null;
				}// fail
				while (!st.isRoot()) {
					tokens.add(0, st);
					st = st.parent;
				}
			}
			else {
				tokens.add(this.root.child(Markov.SS).pselect());
			}
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

	private String _flatten(Node[] nodes) {
		if (nodes == null || nodes.length == 0) {
			return "";
		}

		else {
			String res = new String();
			for (int i = 0; i < nodes.length; i++) {
				res += nodes[i].token;
				if (i != nodes.length - 1) {
					res += " ";
				}
			}
			return res;
		}
	}

	private String _flatten(String[] tokens) {
		return this.doUntokenize(tokens);
	}

	private String _flatten(List<String> tokens) {
		return _flatten(tokens.toArray(new String[tokens.size()]));
	}

	private void _logError(int tries, List<Node> toks, String msg) {
		if (this.trace) {
			System.out.println(tries + " FAIL" + (msg.length() > 0
					? "(" + msg + ")"
					: "") + ": " + this._flatten(toks.toArray(new Node[0])));
		}
	}

	private boolean isSubArrayList(List<String> find, List<String> arr) {
		if (arr == null || arr.size() == 0) return false;
		OUT: for (int i = find.size() - 1; i < arr.size(); i++) {
			for (int j = 0; j < find.size(); j++) {
				if (!find.get(find.size() - j - 1).equals(arr.get(i - j))) {
					continue OUT;
				}
				if (j == find.size() - 1) {
					return true;
				}
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
		for (int j = 0; j < depth - 1; j++) {
			indent += "  ";
		}

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

		public String token;
		protected Node parent;
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
			return this.child(word.token);
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
			return this.numChildren;
		}

		public double nodeProb() {
			return nodeProb(false);
		}

		public double nodeProb(boolean excludeMetaTags) {
			if (this.parent == null) {
				throw new RiTaException("no parent for: " + this);
			}
			double result = (double) this.count /
					(double) this.parent.childCount(excludeMetaTags);
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
			return this.childCount() != 0
					? stringulate(this, s, 1, true)
					: s + "}";
		}

		public String toString() {
			if (this.parent == null) return "Root";
			String prob = DF.format(this.nodeProb());
			return this.token + "(" + this.count + "/" + prob + "%)";
		}
	}

	static final Comparator<Markov.Node> byCount = new Comparator<Markov.Node>() {
		public int compare(Markov.Node a, Markov.Node b) {
			return b.count != a.count ? b.count - a.count
					: b.token.toLowerCase().compareTo(a.token.toLowerCase());
		}
	};

	private String[] doTokenize(String s) {
		return this._tokenize != null ? this._tokenize.apply(s) : RiTa.tokenize(s);
	}

	private String doUntokenize(String[] s) {
		return this._untokenize != null ? this._untokenize.apply(s) : RiTa.untokenize(s);
	}

	public static void main(String[] args) {
		Markov rm = new Markov(2);
		rm.addText("The");
		System.out.println(rm.root.asTree());
	}

}
