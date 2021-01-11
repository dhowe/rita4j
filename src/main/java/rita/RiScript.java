package rita;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.unbescape.html.HtmlEscape;

import rita.antlr.RiScriptLexer;
import rita.antlr.RiScriptParser;
import rita.antlr.RiScriptParser.ScriptContext;

// TODO: 
//   Conditionals

@SuppressWarnings("unchecked")
public class RiScript {

	public static final int MAX_TRIES = 100;

	protected RiScriptLexer lexer;
	protected RiScriptParser parser;
	protected List<String> appliedTransforms;

	protected Visitor visitor;

	public RiScript() {
		this.visitor = new Visitor(this);
		this.appliedTransforms = new ArrayList<String>();
	}

	public static String eval(String input) {

		return RiScript.eval(input, null);
	}

	public static String eval(String input, Map<String, Object> ctx) {

		return RiScript.eval(input, ctx, null);
	}

	public static String eval(String input, Map<String, Object> ctx, Map<String, Object> opts) {
		return new RiScript().evaluate(input, ctx, opts);
	}

	public String evaluate(String input) {
		return evaluate(input, null);
	}

	public String evaluate(String input, Map<String, Object> ctx) {
		return evaluate(input, null, null);
	}

	public String evaluate(String input, Map<String, Object> ctx, Map<String, Object> opts) {

		boolean trace = Util.boolOpt("trace", opts);
		boolean silent = Util.boolOpt("silent", opts);
		boolean onepass = Util.boolOpt("singlePass", opts);

		if (ctx == null) ctx = new HashMap<String, Object>();

		pushTransforms(ctx);

		String last = null, expr = input;
		for (int i = 0; /*isParseable(expr)&& */!expr.equals(last) && i < MAX_TRIES; i++) {
			last = expr;
			if (trace) System.out.println("\n--------------------- Pass#" + i + " ----------------------");
			expr = lexParseVisit(expr, ctx, opts);
			if (trace) passInfo(ctx, last, expr, i);
			if (i >= RiScript.MAX_TRIES - 1) throw new RiTaException("Unable to resolve: \""
					+ input + "\" after " + RiScript.MAX_TRIES + " tries. An infinite loop?");
			if (onepass || !this.isParseable(expr)) break;
		}

		//System.out.println("expr: " + expr + " parsable?" + isParseable(expr));
		if (!silent && RiTa.SILENT && RE.test("\\$[A-Za-z_]", expr)) {
			System.out.println("[WARN] Unresolved symbol(s) in \"" + expr + "\"");
		}

		popTransforms(ctx);

		return resolveEntities(expr);
	}

	private RiScript pushTransforms(Map<String, Object> ctx) {
		for (String tx : RiScript.transforms.keySet()) {
			if (!ctx.containsKey(tx)) {
				ctx.put(tx, RiScript.transforms.get(tx));
				this.appliedTransforms.add(tx);
			}
		}
		return this;
	}

	private RiScript popTransforms(Map<String, Object> ctx) {
		for (String tx : appliedTransforms) {
			ctx.remove(tx);
		}
		return this;
	}

	private String resolveEntities(String s) {
		String k = HtmlEscape.unescapeHtml(s);
		// replace non-breaking-space char with plain space
		return k.replaceAll(" ", " ");
	}

	String ctxStr(Map<String, Object> ctx) {
		return (ctx != null ? ctx.toString().replaceAll("rita.RiScript\\$\\$Lambda[^,]+,", "[F],") : "{}");
	}

	private void passInfo(Map<String, Object> ctx, String input, String output, int pass) {
		System.out.println("\nPass#" + pass + ":  " + input.replaceAll("\\r?\\n", "\\\\n")
				+ "\nResult:  " + output + "\nContext: " + ctxStr(ctx));
	}

	private String[] preParse(String input, Map<String, Object> opts) {
		String parse = input, pre = "", post = "";
		boolean skipPreParse = Util.boolOpt("skipPreParse", opts);
		if (!skipPreParse && !RE.test("^[${]", parse)) {
			Pattern re = Pattern.compile("[()$|{}]");
			String[] words = input.split(" +");
			int preIdx = 0, postIdx = words.length - 1;
			while (preIdx < words.length) {
				if (RE.test(re, words[preIdx])) break;
				preIdx++;
			}
			if (preIdx < words.length) {
				while (postIdx >= 0) {
					if (RE.test(re, words[postIdx])) break;
					postIdx--;
				}
			}
			pre = String.join(" ", Arrays.copyOfRange(words, 0, preIdx));
			parse = String.join(" ", Arrays.copyOfRange(words, preIdx, postIdx + 1));
			post = String.join(" ", Arrays.copyOfRange(words, postIdx + 1, words.length));
		}
		if (false && Util.boolOpt("trace", opts) && parse.length() == 0) {
			System.out.println("NO PARSE: preParse('"
					+ (pre.length() > 0 ? pre : "") + "', '"
					+ (post.length() > 0 ? post : "") + "'):");
		}
		return new String[] { pre, parse, post };
	}

	public CommonTokenStream lex(String input) {
		return this.lex(input, null);
	}

	public CommonTokenStream lex(String input, Map<String, Object> opts) {
		CharStream chars = CharStreams.fromString(input);
		this.lexer = new RiScriptLexer(chars);
		// this.lexer.removeErrorListeners();
		// this.lexer.addErrorListener(ParseErrorListener.INSTANCE);
		CommonTokenStream tokenStream = new CommonTokenStream(this.lexer);
		if (Util.boolOpt("trace", opts)) {
			tokenStream.fill();
			int i = 0;
			for (Token tok : tokenStream.getTokens()) {
				System.out.println((i++) + ")" + tok);
			}
		}
		return tokenStream;
	}

	public ScriptContext parse(CommonTokenStream tokens, String input) {
		return this.parse(tokens, input, null);
	}

	public ScriptContext parse(CommonTokenStream tokens, String input, Map<String, Object> opts) {

		// create the parser
		this.parser = new RiScriptParser(tokens);
		this.parser.removeErrorListeners();
		this.parser.addErrorListener(ParseErrorListener.INSTANCE);

		// try the parsing
		ScriptContext tree;
		try {
			tree = this.parser.script();
			if (Util.boolOpt("trace", opts)) {
				System.out.println("\n" + tree.toStringTree(
						Arrays.asList(parser.getRuleNames())) + "\n");
			}
		} catch (Exception e) {
			if (!Util.boolOpt("silent", opts)) {
				System.err.println("PARSER: '" + input + "'\n" + e.getMessage() + "\n");
			}
			throw e;
		}
		return tree;
	}

	public ScriptContext lexParse(String input) {
		return this.lexParse(input, null);
	}

	public ScriptContext lexParse(String input, Map<String, Object> opts) {

		CommonTokenStream tokens = this.lex(input, opts);
		return this.parse(tokens, input, opts);
	}

	public String lexParseVisit(String input, Map<String, Object> context) {
		return this.lexParseVisit(input, context, null);
	}

	public String lexParseVisit(String input, Map<String, Object> context, Map<String, Object> opts) {

		ScriptContext tree;
		String result = "", parts[] = this.preParse(input, opts);
		String pre = parts[0], parse = parts[1], post = parts[2];

		if (parse.length() > 0) {
			tree = this.lexParse(parts[1], opts);

			if (Util.boolOpt("trace", opts) && (pre.length() > 0 || post.length() > 0))
				System.out.println("preParse('" + (pre.length() > 0 ? pre : "")
						+ "', '" + (post.length() > 0 ? post : "") + "'):");

			result = this.visitor.init(context, opts).start(tree);
		}
		return (this.normalize(pre) + " " + result + " " + this.normalize(post)).trim();
	}

	String normalize(String s) {
		return (s != null && s.length() > 0) ? s.replaceAll("\\r", "")
				.replaceAll("\\\\n", "")
				.replaceAll("\\n", " ") : "";
	}

	public boolean isParseable(String s) { // public for testing (TODO: more needed!!!)
		return PARSEABLE_RE.matcher(s).find();
	}

	public static String articlize(String s) {
		if (s == null || s.length() < 1) return "";
		String phones = RiTa.phones(s, RiTa.opts("silent", true));
		//System.out.println(phones+" " + phones.substring(0,1));
		return (phones != null && phones.length() > 0 && RE.test("[aeiou]",
				phones.substring(0, 1)) ? "an " : "a ") + s;
	}

	private static final Pattern PARSEABLE_RE = Pattern.compile("([\\(\\)]|\\$[A-Za-z_][A-Za-z_0-9-]*)");

	private static final Function<String, String> identity = s -> s;

	private static final Function<String, String> uc = s -> {
		return s != null ? s.toUpperCase() : "";
	};

	private static final Function<String, String> articlize = s -> {
		return RiTa.articlize(s);
	};

	private static final Function<String, String> pluralize = s -> {
		return RiTa.pluralize(s);
	};

	private static final Function<String, String> capitalize = s -> {
		return RiTa.capitalize(s);
	};
	
	private static final Function<String, String> quotify = s -> {
		return "&#8220;" + (s != null ? s : "") + "&#8221;";
	};

	private static final Map.Entry<String, Object>[] transformMap = new Map.Entry[] {
			new AbstractMap.SimpleEntry<String, Object>("articlize", articlize),
			new AbstractMap.SimpleEntry<String, Object>("pluralize", pluralize),
			new AbstractMap.SimpleEntry<String, Object>("capitalize", capitalize),
			new AbstractMap.SimpleEntry<String, Object>("quotify", quotify),
			new AbstractMap.SimpleEntry<String, Object>("ucf", capitalize),
			new AbstractMap.SimpleEntry<String, Object>("art", articlize),
			new AbstractMap.SimpleEntry<String, Object>("seq", identity),
			new AbstractMap.SimpleEntry<String, Object>("rseq", identity),
			new AbstractMap.SimpleEntry<String, Object>("norep", identity),
			new AbstractMap.SimpleEntry<String, Object>("s", pluralize),
			new AbstractMap.SimpleEntry<String, Object>("uppercase", uc),
			new AbstractMap.SimpleEntry<String, Object>("uc", uc)
	};

	public static Map<String, Function<String, String>> transforms;

	static {
		transforms = new HashMap<String, Function<String, String>>();
		for (Map.Entry<String, Object> kv : transformMap) {
			transforms.put(kv.getKey(), (Function<String, String>) kv.getValue());
		}
	}

	public static void main(String[] args) {
		//		RiScript rs = new RiScript();
		//		Map<String, Object> opts = RiTa.opts();
		//		String s = rs.lexParseVisit("[$a=(A | B)]", opts, RiTa.opts("trace", true));
		//		System.out.println("\nResult: '" + s + "', opts: " + opts + " " + transforms);
		//System.out.println(HtmlEscape.unescapeHtml("Eve&nbsp;near Vancouver"));
		//new RiScript().lex("$1foo", RiTa.opts("trace", true));
	}
}

class ParseErrorListener extends BaseErrorListener {

	static final ParseErrorListener INSTANCE = new ParseErrorListener();

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
			int line, int charPositionInLine, String msg, RecognitionException e)
			throws ParseCancellationException {
		throw new RiTaException("ParseError: line " + line + ":"
				+ charPositionInLine + " " + msg);
	}
}
