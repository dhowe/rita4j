package rita;

import static rita.RiTa.opts;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.text.DecimalFormat;
import java.util.regex.*;

public class RiMarkov {
	public int n;
	public Node root;
	public List<String> input;
	protected Function<String, String[]> _tokenize = null;
	protected Function<String[], String> _untokenize = null;
	private static DecimalFormat DF = new DecimalFormat("0.000");
	private static final Pattern MULTI_SP_RE = Pattern.compile(" +");

	protected int mlm, treeifyTimes, maxAttempts;
	protected boolean trace, disableInputChecks;// logDuplicates;

	public List<String> sentenceStarts;
	public Set<String> sentenceEnds;

	public RiMarkov(int n){
		this(n, null);
	}

	@SuppressWarnings("uncheck")
	public RiMarkov(int n, Map<String, Object> opts){
		if (n < 2) throw new RiTaException("minimum N is 2");
		this.n = n;
		this.root = new Node(null, "ROOT");
		this.trace = Util.boolOpt("trace", opts);
		this.mlm = Util.intOpt("maxLengthMatch", opts, 0);
		if (this.mlm != 0 && this.mlm < this.n) throw new RiTaException("maxLengthMatch must be >= N");
		this.maxAttempts = Util.intOpt("maxAttempts", opts, 999);
		if (opts != null && opts.containsKey("tokenize")) {
			this._tokenize = (Function<String, String[]>) opts.get("tokenize");
		}
		if (opts != null && opts.containsKey("untokenize")) {
			this._untokenize = (Function<String[], String>) opts.get("untokenize");
		}
		this.disableInputChecks = Util.boolOpt("disableInputChecks", opts);
		this.sentenceStarts = new ArrayList<String>();
		this.sentenceEnds = new HashSet<String>();
		if (!this.disableInputChecks || this.mlm > 0) this.input = new ArrayList<String>();
		if (opts != null && opts.containsKey("text")) {
			String textstr = null;
			String[] textarr = null;
			try {
				textstr = Util.strOpt("text", opts, null);
			} catch (Exception e) {
				//not String
				try {
					textarr = (String[]) opts.get("text");
				} catch (Exception ee) {
					throw new RiTaException("invalid text option");
				}
			}
			if (textarr != null) {
				this.addText(textarr);
			} else if (textstr != null) {
				this.addText(textstr);
			}
		}
	}

	public void addText(String text){
		this.addText(text, 1);
	}

	public void addText(String[] text){
		this.addText(text, 1);
	}

	public void addText(String text, int multiplier){
		this.addText(RiTa.sentences(text), multiplier);
	}

	public void addText(String[] sents, int multiplier){
		List<String> allWords = new ArrayList<String>();
		for (int k = 0; k < multiplier; k++){
			for (int i = 0; i < sents.length; i++) {
				if (sents[i].length() < 1) continue;
				String[] words = this._tokenize == null ? RiTa.tokenize(sents[i]) : this._tokenize.apply(sents[i]);
				this.sentenceStarts.add(words[0]);
				this.sentenceEnds.add(words[words.length - 1]);
				allWords.addAll(Arrays.asList(words));
			}
			this.treeify(allWords.toArray(String[]::new));
		}

		if (!this.disableInputChecks || this.mlm > 0) {
			for (int i = 0; i < allWords.size(); i++) {
				this.input.add(allWords.get(i));
			}
		}
	}

	public String[] generate(){
		return this.generate(1, null);
	}

	public String[] generate(int n){
		return this.generate(n, null);
	}

	public String[] generate(Map<String, Object> opts) {
		return this.generate(1, opts);
	}

	public String[] generate(int num, Map<String, Object> opts) {
		int minLength = Util.intOpt("minLength", opts, 5);
		int maxLength = Util.intOpt("maxLength", opts, 35);
		float temperature = Util.floatOpt("temperature", opts, 0);

		if (temperature < 0) throw new RiTaException("Temperature option must be greater than 0");

		int tries = 0;
		List<Node> tokens = new ArrayList<Node>();
		List<String> usedStarts = new ArrayList<String>();
		int minIdx = 0;
		List<Integer> sentenceIdxs = new ArrayList<Integer>();
		List<Node> markedNodes = new ArrayList<Node>();
		Predicate<Node> notMarkedPre = n -> {
			String tmap = "";
			for (Node e : tokens) {
				tmap += e.token;
			}
			return n.marked == null ? true : !n.marked.equals(tmap);
		};

		/////////////////////////// local functions //////////////////////////////////
		class Local{
			void unmarkNodes(){
				markedNodes.forEach((n) -> {n.marked = null;});
			}

			int resultCount(){
				return tokens.stream().filter(t -> _isEnd(t)).toArray(Node[]::new).length;
			}

			void markNode(Node node) {
				if (node != null) {
					String tmap = "";
					for (Node e : tokens) {
						tmap += e.token;
					}
					node.marked = tmap;
					markedNodes.add(node);
				}
			}

			boolean notMarked(Node cn){
				String tmap = "";
				for (Node e : tokens) {
					tmap += e.token;
				}
				return cn.marked == null ? true : ! cn.marked.equals(tmap);
			}
			
			boolean validateSentence(Node next, int tries, List<Integer> sentenceIdxs){
				this.markNode(next);
				int sentIdx = this.sentenceIdx(sentenceIdxs);

				if (trace) System.out.println(1 + (tokens.size() - sentIdx) + " " +
				next.token + " [" + Arrays.asList(next.parent.childNodes()).stream().filter(t -> t != next).map(t -> t.token).reduce((a,c) -> a + c +",")+ "]");

				List<String> sentence = new ArrayList<String>(tokens.subList(sentIdx, tokens.size()).stream().map(t -> t.token).toList());
				sentence.add(next.token);

				if (sentence.size() < minLength) {
					this.fail("too-short (pop: " + next.token + ")", tries, sentenceIdxs);
					return false;
				}

				if (!disableInputChecks && isSubArrayList(sentence, input)){
					this.fail("in-input (pop: " + next.token + ")", tries, sentenceIdxs);
					return false;
				}

				String flatSent = doUntokenize(sentence.toArray(String[]::new));
				List<String> cur = tokens.subList(0, sentIdx).stream().map(t -> t.token).toList();
				if (!Util.boolOpt("allowDuplicates", opts) && isSubArrayList(sentence, cur)){
					this.fail("duplicate (pop: " + next.token + ")", tries, sentenceIdxs);
					return false;
				}

				tokens.add(next);

				if (trace) System.out.println("OK (" + this.resultCount() + "/" + num + ") \"" +
				flatSent + "\" sidxs=[" + sentenceIdxs + "]\n");

				return true;
			}

			List<Integer> fail(String msg, int tries, List<Integer> sentenceIdxs){
				int sentIdx = this.sentenceIdx(sentenceIdxs);
				return this.fail(msg, _flatten(tokens.subList(sentIdx, tokens.size()).toArray(Node[]::new)), false, tries, sentenceIdxs);
			}

			List<Integer> fail(String msg, String sentence, int tries, List<Integer> sentenceIdxs){
				return this.fail(msg,sentence,false, tries, sentenceIdxs);
			}

			List<Integer> fail(String msg, boolean forceBacktrack, int tries,List<Integer> sentenceIdxs) {
				int sentIdx = this.sentenceIdx(sentenceIdxs);
				return this.fail(msg, _flatten(tokens.subList(sentIdx, tokens.size()).toArray(Node[]::new)), forceBacktrack, tries, sentenceIdxs);
			}

			List<Integer> fail(String msg, String sentence, boolean forceBacktrack, int tries, List<Integer> sentenceIdxs){
				if (tries > maxAttempts) throwError(tries, this.resultCount());
				Node parent = _pathTo(tokens.toArray(Node[]::new));
				int numChildren = parent != null ? parent.childNodes(opts("filter", notMarkedPre)).length : 0;

				if (trace) System.out.println("Fail:" + msg + "\n  -> \"" + sentence + "\" " +
				tries + " tries, " + this.resultCount() + " successes, numChildren=" + numChildren
				+ (forceBacktrack ? " forceBacktrack*" : (" parent=\"" + parent.token
				  + "\" goodKids=[" + Arrays.asList(parent.childNodes(opts("filter", notMarkedPre))).stream().map(t -> t.token).reduce((a,c) -> a + c +",") + "]"
				  + "\" allKids=[" + Arrays.asList(parent.childNodes()).stream().map(t -> t.token).reduce((a,c) -> a + c +",") + "]")));

				if (forceBacktrack || numChildren == 0) {
					List<Integer> updatedSidxs = this.backtrack(sentenceIdxs);
					return updatedSidxs;
				}
				return null;
			}

			// step back until we have a parent with children
    		// or we have reached our start
    		// return the updated sentenceIdxs
			List<Integer> backtrack(List<Integer> sidxs) {
				Node parent;
				Node[] tc;
				for (int i = 0; i < 99; i ++) {
					Node last = tokens.remove(tokens.size() - 1);
					this.markNode(last);

					if (_isEnd(last)) sidxs.remove(sidxs.size() - 1);

					int sentIdx = this.sentenceIdx(sidxs);
					int backtrackUntil = Math.max(sentIdx, minIdx);

					if (trace) System.out.println("backtrack#" + tokens.size() + 
					"pop \"" + last.token + "\" " + (tokens.size() - sentIdx)
					+ "/" + backtrackUntil + " " + _flatten(tokens.toArray(Node[]::new)));

					parent = _pathTo(tokens.toArray(Node[]::new));
					tc = parent.childNodes(opts("filter", notMarkedPre));

					if (tokens.size() <= backtrackUntil) {

						if (minIdx > 0) {
							if (tokens.size() <= minIdx) {
								if (tc.length < 0) throw new RiTaException("back at barren-seed1: case 0");
								if (trace) System.out.println("case 1");
								return sidxs;
							} else {
								if (tc.length < 0) {
									if (trace) System.out.println("case 2: back at SENT-START: \""
									+ _flatten(tokens.toArray(Node[]::new)) + "\" sentenceIdxs=" + sidxs
									+ " ok=[" + Arrays.asList(parent.childNodes(opts("filter", notMarkedPre))).stream().map(t -> t.token).reduce( (a,c) -> a+c+",")
									+ "] all=[" + Arrays.asList(parent.childNodes()).stream().map(t -> t.token).reduce( (a,c) -> a+c+",") + "]");
									sidxs.remove(sidxs.size() - 1);
								} else {
									if (trace) System.out.println("case 3");
								}
							}
						} else {

							if (trace) System.out.println("case 4: back at start of sentence"
							+ " or 0: " + tokens.size() + sidxs);

							if (tokens.size() < 1) {
								this.selectStart();
								return new ArrayList<Integer>();
							}
						}

						return sidxs;
					}

					if (tc.length > 0) {
						sentIdx = this.sentenceIdx(sidxs);

						if (trace) System.out.println((tokens.size() - sentIdx)
						+ ' ' + _flatten(tokens.toArray(Node[]::new)) + "\n  ok=["
						+ Arrays.asList(tc).stream().map(t -> t.token).reduce( (a,c) -> a+c+",") + "] all=[" + 
						Arrays.asList(parent.childNodes(opts("filter", notMarkedPre))).stream().map(t -> t.token).reduce( (a,c) -> a+c+",") + "]");

						return sidxs;
					}
				}

				throw new RiTaException("Invalid state in backtrack() ["
				+ tokens.stream().map(t -> t.token).reduce((a,c) -> a+c+",") + ']');
			}

			int sentenceIdx(List<Integer> sentenceIdxs){
				int len = sentenceIdxs.size();
				return len != 0 ? sentenceIdxs.get(len - 1) : 0;
			}

			void selectStart(){
				String[] seedArr = null;
				String seedStr = null;
				if ( opts != null && opts.containsKey("seed")){
					try {
						seedStr = Util.strOpt("seed", opts, null);
					} catch (Exception e) {
						try {
							seedArr = (String[]) opts.get("seed");
						} catch (Exception ee) {
							throw new RiTaException("Invalid seed");
						}
					} 
				}
				if (seedStr != null && seedStr.length() > 0){
					seedArr = doTokenize(seedStr);
				} 
				
				if (seedArr != null) {
					Node node  = _pathTo(seedArr, root);
					while(!node.isRoot()) {
						tokens.add(0, node);
						node = node.parent;
					}
				} else if (tokens.size() < 1 || _isEnd(tokens.get(tokens.size() - 1))) {

					String[] usableStarts = sentenceStarts.stream().filter(ss -> this.notMarked(root.child(ss))).toArray(String[]::new);
					if (usableStarts.length < 1) throw new RiTaException("No valid sentence-starts remaining");
					String start = RiTa.random(usableStarts);
					Node startTok = root.child(start);
					markNode(startTok);
					usableStarts = sentenceStarts.stream().filter(ss -> notMarked(root.child(ss))).toArray(String[]::new);//?
					tokens.add(startTok);
				} else {
					throw new RiTaException("Invalid call to selectStart: " + _flatten(tokens.toArray(Node[]::new)));
				}
			}
		}
		Local lo = new Local();
		
		///////////////////////////////////// code ///////////////////////////////////////////////
		lo.selectStart();

		while(lo.resultCount() < num) {
			int sentIdx = lo.sentenceIdx(sentenceIdxs);

			if (tokens.size() - sentIdx >= maxLength) {
				tries ++;
				List<Integer> newsidxs = lo.fail("too-long", "0", true, tries, sentenceIdxs);
				if (newsidxs != null) sentenceIdxs = newsidxs;
				continue;
			}

			Node parent = this._pathTo(tokens.toArray(Node[]::new));
			double doubleTemp = (double)temperature;
			Node next = this._selectNext(parent, doubleTemp, tokens.toArray(Node[]::new), notMarkedPre);

			if (next == null) {
				tries ++;
				List<Integer> newsidxs =lo.fail("mlm-fail(" + this.mlm + ")", this._flatten(tokens.toArray(Node[]::new)), true, tries, sentenceIdxs);
				if (newsidxs != null) sentenceIdxs = newsidxs;
				continue;
			}

			if (this._isEnd(next)) {
				boolean success = lo.validateSentence(next, tries, sentenceIdxs);
				if (success) {
					sentenceIdxs.add(tokens.size());
				}
				continue;
			}

			tokens.add(next);

			if (this.trace) System.out.println(tokens.size() - sentIdx + next.token +
				"[" + Arrays.asList(parent.childNodes(opts("filter", notMarkedPre))).stream().filter(t -> t != next).map(t -> t.token).reduce((a,c) -> a+c+",") + "]");
		}

		lo.unmarkNodes();

		String str = doUntokenize(tokens.stream().map(t-> t.token).toArray(String[]::new));
		return num > 1 ? this._splitEnds(str) : new String[] {str};
	}

	// public String toJSON() {

	// }

	// public static fromJSON(json) {
		
	// }

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

	public String toString(){
		return this.toString(this.root, false);
	}

	public String toString(Node root){
		return this.toString(root, false);
	}

	public String toString(Node root, boolean sort){
		return root.asTree(sort).replaceAll("{}", "");
	}

	public int size() {
		return this.root.childCount(true);
	}

    ////////////////////////////// end API ////////////////////////////////

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

	private Node _selectNext(Node parent, double temp, Node[] tokens, Predicate<Node> filter){
		if (parent == null) throw new RiTaException("no parent:" + this._flatten(tokens));

		Node[] children = parent.childNodes(RiTa.opts("filter", filter));
		if (children.length < 1) {
			if (this.trace) System.out.println("No children to select, parent=" + parent.token
			+ " children=ok[], all=[" + Arrays.asList(parent.childNodes()).stream().map(t -> t.token) + "]");
			return null;
		}

		// basic case: just prob. select from children
		if (this.mlm == 0 || this.mlm > tokens.length) { //if no mlm input or tokens.length < mlm
			return parent.pselect(filter);
		}

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

	private boolean _isEnd(String token){
		if (token == null) return false;
		return this.sentenceEnds.contains(token);
	}

	private boolean _isEnd(Node node){
		if (node != null) {
			if (node.token != null) {
				String check = node.token;
				return this.sentenceEnds.contains(check);
			} else {
				return false;
			}
		}
		return false;
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

	private void treeify(String[] tokens) {
		Node root = this.root;
		for (int i = 0; i < tokens.length; i++) {
			Node node = root;
			//words = tokens.slice(i, i + this.n);
			String[] words = Arrays.copyOfRange(tokens, i, i + this.n);
			int wrap = 0;
			for (int j = 0; j < this.n; j++) {
				boolean hidden = false;
				if ( j >= words.length) {
					words[j] = tokens[wrap++];
					hidden = true;
				}
				if (words[j] != null) node = node.addChild(words[j]);
				if (hidden) node.hidden = true;
			}
		}
	}

	private String[] _splitEnds(String str) {
		List<String> se = new ArrayList<String>();
		se.addAll(this.sentenceEnds); 
		String rec = se.stream().collect(Collectors.joining("|", "", ""));
		//rec = rec.substring(0, rec.length()-1);
		rec = rec.replaceAll("([.*+?^${}()\\]\\[\\\\])", "\\\\$1"); 
		rec = "(?<=[" + rec + "])";
		List<String> arr = new ArrayList<String>();
		String[] parts = str.split(rec);
		// for (int i = 0; i < parts.length; i++) {
		// 	if (parts[i].length() < 1) continue;
		// 	if ((i % 2) == 0) {
		// 		arr.add(parts[i]);
		// 	} else {
		// 		String tem = arr.get(arr.size() - 1);
		// 		arr.set(arr.size() - 1, tem + parts[i]);
		// 	}
		// }
		// return arr.stream().map(a -> a.trim()).toArray(String[]::new);
		return parts;
	}	

	private String _flatten(Node node){
		return node.token;
	}

	private String _flatten(Node[] nodes){
		if (nodes == null || nodes.length < 1) return "";
		String[] arr = new String[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			arr[i] = nodes[i] != null ? nodes[i].token : "[undef]";
			if (arr[i] == null) arr[i] = "[null]";
		}
		String sent = this.doUntokenize(arr);
		return sent.replaceAll(" +", " ");
	}

	/////////////////////////////////////////////////
	public class Node {
		public String token;
		protected Map<String, Object> children;
		protected Node parent;
		protected int count;
		protected int numChildren;
		public String marked; // need to be able to store token hash
		public boolean hidden;

		Node(Node parent, String word){
			this(parent, word, 0);
		}

		public Node(Node parent, String word, int count){
			this.children = new HashMap<String, Object>();
			this.parent = parent;
			this.token = word;
			this.count = count;
			this.numChildren = -1;
			this.marked = null;
		}

		public Node child(Node n){
			return this.child(n.token);
		}

		public Node child(String word){
			String lookup = word;
			if (this.children.containsKey(lookup)) {
				return (Node) this.children.get(lookup);
			} else {
				return null;
			}
		}

		public Node pselect(){
			return this.pselect(null);
		}

		public Node pselect(Predicate<Node> filter){
			Map<String, Object>opts = new HashMap<String, Object>();
			opts.put("filter", filter);
			Node[] children = this.childNodes(opts);
			if (children == null || children.length < 1) {
				throw new RiTaException("No eligible child for \"" + this.token
				+ "\" children=[" + Arrays.asList(this.childNodes()).stream().map(t -> t.token).toArray() + "]");
			}
			List<Integer> weights = new ArrayList<>();
			for (Node n : children) {
				weights.add(n.count);
			}
			int[] wArr = weights.stream().mapToInt(i -> i).toArray();
			double[] pdist = RandGen.ndist(wArr);
			int idx = RandGen.pselect(pdist);
			return children[idx];
		}

		public boolean isLeaf(){
			return this.isLeaf(false);
		}

		public boolean isLeaf(boolean ignoreHidden) {
			return this.childCount(ignoreHidden) < 1;
		}

		public boolean isRoot(){
			return this.parent == null;
		}

		public Node[] childNodes(){
			return this.childNodes(null);
		}

		public Node[] childNodes(Map<String, Object> opts){
			boolean sort = Util.boolOpt("sort", opts);
			Predicate<Node> filter = null;
			if (opts != null && opts.containsKey("filter")) {
				filter = (Predicate) opts.get("filter");
			}
			Node[] kids = this.children.values().toArray(Node[]::new);
			if (filter != null) kids = Arrays.asList(kids).stream().filter(filter).toArray(Node[]::new);
			if (sort){
				kids = Arrays.asList(kids).stream().sorted((a,b) -> b.count != a.count ? b.count - a.count : b.token.compareTo(a.token)).toArray(Node[]::new);
			}
			return kids;
		}

		public int childCount(){
			return this.childCount(false);
		}

		public int childCount(boolean ignoreHidden) {
			if (this.numChildren == -1) {
				Map<String, Object> opts = RiTa.opts();
				if (ignoreHidden) {
					opts.put("filter", (Predicate<Node>)(t -> !t.hidden));
				}
				this.numChildren = 0;
				for (Node child : this.childNodes(opts)) {
					this.numChildren += child.count;
				}
			}
			return this.numChildren;
		}

		public double nodeProb(){
			return this.nodeProb(false);
		}

		public double nodeProb(boolean excludeMetaTags){
			if (this.parent == null) throw new RiTaException("no parent");
			return this.count / this.parent.childCount(excludeMetaTags);
		}

		public Node addChild(String word){
			return this.addChild(word, 1);
		}

		public Node addChild(String word, int count){
			this.numChildren = -1;
			Node node = (Node) this.children.get(word);
			if (node == null) {
				node = new Node(this, word);
				this.children.put(word, node);
			}
			node.count += count;
			return node;
		}

		public String toString(){
			return this.parent == null ? "'" + this.token + "' [" + this.count
			+ ",p=" + DF.format(this.nodeProb()) + "]" : "Root";
		}

		public String asTree(){
			return this.asTree(false, false);
		}

		public String asTree(boolean sort){
			return this.asTree(sort, false);
		}

		public String asTree(boolean sort, boolean showHiddenNodes){
			String s = this.token + " ";
			if (this.parent != null) {
				s += "(" + this.count + ")->";
			}
			s += "{";
			return this.childCount() != 0
					? stringulate(this, s, 1, true)
					: s + "}";
		}
	}

	// --------------------------------------------------------------------
	private static String stringulate(Node mn, String str, int depth) {
		return stringulate(mn, str, depth, false, false);
	}

	private static String stringulate(Node mn, String str, int depth, boolean sort) {
		return stringulate(mn, str, depth, sort, false);
	}

	private static String stringulate(Node mn, String str, int depth, boolean sort, boolean ignoreHidden){
		String indent = "\n";
		Map<String, Object> opts = RiTa.opts("sort", true, "filter", (Predicate<Node>)(t -> !t.hidden));
		Node[]l = mn.childNodes(opts);
		if(l.length < 1) return str;
		for (int j = 0; j < depth; j++) {
			indent += "  ";
		}
		for (int i = 0; i < l.length; i++) {
			Node node = l[i];
			if (node != null && node.token != null) {
				str += indent + "'" + encode(node.token) + "'";
				if (!node.isRoot()) str +=  " [" + node.count + ",p=" + DF.format(node.nodeProb()) + "]";
				if (!node.isLeaf(ignoreHidden)) {
					str += "  {";
				}
				str = mn.childCount(ignoreHidden) > 0 ? stringulate(node, str, depth + 1, sort) : str + "}";
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

	public void populate(Node objNode, Map<String, Object> jsonNode){
		if (jsonNode == null) return;
		// TODOs
	}

	private void throwError(int tries, int oks){
		throw new RiTaException("Failed after " + tries + " tries"
		+ (oks > 0 ? " and " + oks + " successes" : "")
		+ ", you may need to adjust options or add more text");
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

	private String[] doTokenize(String s) {
		return this._tokenize != null ? this._tokenize.apply(s) : RiTa.tokenize(s);
	}

	private String doUntokenize(String[] s) {
		return this._untokenize != null ? this._untokenize.apply(s) : RiTa.untokenize(s);
	}

	public static void main(String[] args) {
		RiMarkov rm = new RiMarkov(2);
		rm.addText("The");
		System.out.println(rm.root.asTree());
	}

}
