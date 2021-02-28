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

		String last = null, expr = input;
		for (int i = 0; !expr.equals(last) && i < MAX_TRIES; i++) {
			last = expr;
			if (trace) System.out.println("\n--------------------- Pass#" + i + " ----------------------");
			expr = lexParseVisit(expr, ctx, opts);
			if (trace) passInfo(ctx, last, expr, i);
			if (i >= RiScript.MAX_TRIES - 1) throw new RiTaException("Unable to resolve: \""
					+ input + "\" after " + RiScript.MAX_TRIES + " tries. An infinite loop?");
			if (onepass || !this.isParseable(expr)) break;
		}

		//System.out.println("expr: " + expr + " parseable?" + isParseable(expr));
		if (!silent && RiTa.SILENT && RE.test(SYM_RE, expr)) {
			System.out.println("[WARN] Unresolved symbol(s) in \"" + expr + "\"");
		}

		return resolveEntities(expr);
	}

	private String resolveEntities(String s) {
		// replace non-breaking-space char with plain space ??
		return unescape(HtmlEscape.unescapeHtml(s));
	}
	
	private String unescape(String s) { // only parens for now
    return s.replaceAll("\\\\\\(", "(").replaceAll("\\\\\\)", ")");
  }

	String ctxStr(Map<String, Object> ctx) {
		return (ctx != null ? ctx.toString().replaceAll(
				"rita.RiScript\\$\\$Lambda[^,]+,", "[F],") : "{}");
	}

	private void passInfo(Map<String, Object> ctx,
			String input, String output, int pass) {
		System.out.println("\nPass#" + pass + ":  "
				+ input.replaceAll("\\r?\\n", "\\\\n")
				+ "\nResult:  '" + output + "'\nContext: " + ctxStr(ctx));
	}

	private boolean dynamic(String l) {
		return l.startsWith("{") || RE.test("([\\/()\\$|\\[\\]])|\\.\\S", l);
	}

	private String[] preparse(String input, Map<String, Object> opts) {

		String rpre = "", rparse = input, rpost = "";

		if (input == null || input.length() == 0) {
			return new String[] { rpre, rparse, rpost };
		}

		if (!Util.boolOpt("nopre", opts)) { // DOC:

			int mode = 0;
			input = input.replaceAll("\\\\n", "");
			String[] lines = input.split("\\r?\\n");

			List<String> pre = new ArrayList<>(),
					parse = new ArrayList<>(),
					post = new ArrayList<>();

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];

				if (mode == 0) {      // pre
					if (dynamic(line)) {
						parse.add(line);
						mode = 1;
					}
					else {
						pre.add(line);
					}
				}
				else if (mode == 1) { // parse
					if (dynamic(line)) {
						parse.add(line);
					}
					else {
						post.add(line);
						mode = 2;
					}
				}
				else if (mode == 2) { // post
					if (dynamic(line)) {
						parse.addAll(post);
						parse.add(line);
						post.clear();
						mode = 1;
					}
					else {
						post.add(line);
					}
				}
			}

			rpre = joinList(pre);
			rparse = joinList(parse);
			rpost = joinList(post);
		}

		return new String[] { rpre, rparse, rpost };
	}

	private String joinList(List<String> l) {
		return l.size() == 0 ? "" : String.join("\n", l.toArray(new String[l.size()]));
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
		if (Util.boolOpt("traceLex", opts)) {
			tokenStream.fill();
			int i = 0;
			for (Token tok : tokenStream.getTokens()) {
				System.out.println((i++) + ")" + tokenToString(tok));
			}
		}
		return tokenStream;
	}

	private String tokenToString(Token t) {
		String txt = "<no text>";
		String ttxt = t.getText();
		if (ttxt != null && ttxt.length() > 0) {
			txt = ttxt.replaceAll("\\n", "\\\\n")
					.replace("\\r", "\\\\r").replace("\\t", "\\\\t");
		}
		int ttype = t.getType();
		String type = (ttype > -1 ? this.lexer
				.getVocabulary().getSymbolicName(ttype) : "EOF");
		return "[ " + t.getLine() + "." + t.getCharPositionInLine()
				+ ": '" + txt + "' -> " + type + " ]";
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

		String parts[] = this.preparse(input, opts);
		String pre = parts[0], parse = parts[1], post = parts[2];

		if (Util.boolOpt("trace", opts) && (pre.length() > 0 || post.length() > 0))
			System.out.println("preParse('" + (pre.length() > 0 ? pre : "")
					+ "', '" + (post.length() > 0 ? post : "") + "'):");

		String visited = "";
		if (parse.length() > 0) {
			ScriptContext tree = this.lexParse(parse, opts);
			visited = this.visitor.init(context, opts).start(tree);
		}

		String result = (pre.length() > 0 && visited.length() > 0)
				? pre + "\n" + visited
				: pre + visited;

		return (result.length() > 0 && post.length() > 0)
				? result + "\n" + post
				: result + post;
	}

	public boolean isParseable(String s) { // public for testing
		//		return PRS_RE.matcher(s).find();
		return RE.test(PRS_RE, s);
	}

	public static String articlize(String s) {
		if (s == null || s.length() < 1) return "";
		String phones = RiTa.phones(s, RiTa.opts("silent", true));
		//System.out.println(phones+" " + phones.substring(0,1));
		return (phones != null && phones.length() > 0 && RE.test("[aeiou]",
				phones.substring(0, 1)) ? "an " : "a ") + s;
	}

	private static final Pattern SYM_RE = Pattern.compile(RiTa.VSYM);
	private static final Pattern PRS_RE = Pattern.compile("[(){}|]|" + RiTa.VSYM);

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
			//			new AbstractMap.SimpleEntry<String, Object>("seq", identity),
			//			new AbstractMap.SimpleEntry<String, Object>("rseq", identity),
			new AbstractMap.SimpleEntry<String, Object>("norepeat", identity),
			new AbstractMap.SimpleEntry<String, Object>("uppercase", uc),
			new AbstractMap.SimpleEntry<String, Object>("uc", uc),
			new AbstractMap.SimpleEntry<String, Object>("nr", identity),
			new AbstractMap.SimpleEntry<String, Object>("s", pluralize)
	};

	public static Map<String, Function<String, String>> transforms;

	static {
		transforms = new HashMap<String, Function<String, String>>();
		for (Map.Entry<String, Object> kv : transformMap) {
			transforms.put(kv.getKey(), (Function<String, String>) kv.getValue());
		}
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
