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
	protected Visitor visitor;

	public static String eval(String input) {

		return RiScript.eval(input, null);
	}

	public static String eval(String input, Map<String, Object> ctx) {

		return RiScript.eval(input, ctx, null);
	}

	public static String eval(String input, Map<String, Object> ctx, Map<String, Object> opts) {
		return new RiScript().evaluate(input, ctx, opts);
	}

	public String evaluate(String input, Map<String, Object> ctx, Map<String, Object> opts) {

		boolean trace = Util.boolOpt("trace", opts);
		boolean silent = Util.boolOpt("silent", opts);
		boolean onepass = Util.boolOpt("singlePass", opts);

		if (ctx == null) ctx = new HashMap<String, Object>();

		String last = input;
		String expr = lexParseVisit(input, ctx, opts);
		if (trace) passInfo(ctx, last, expr, 0);
		if (!onepass) {
			for (int i = 0; isParseable(expr) && !expr.equals(last) && i < MAX_TRIES; i++) {
				last = expr;
				if (trace) System.out.println("\n--------------------- Pass#" + (i + 2) + " ----------------------\n");
				expr = lexParseVisit(expr, ctx, opts);
				if (trace) passInfo(ctx, last, expr, i + 1);
				if (i >= RiScript.MAX_TRIES - 1) throw new RiTaException("Unable to resolve: \""
						+ input + "\" after " + RiScript.MAX_TRIES + " tries. An infinite loop?");
			}
		}
		//System.out.println("expr: " + expr + " parsable?" + isParseable(expr));
		if (!silent && RiTa.SILENT && RE.test("\\$[A-Za-z_]", expr)) {
			System.out.println("[WARN] Unresolved symbol(s) in \"" + expr + "\"");
		}

		return resolveEntities(expr);
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
		System.out.println("\nPass#" + (pass + 1) + ":  " + input.replaceAll("\\r?\\n", "\\\\n")
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

			result = this.createVisitor(context, opts).start(tree);
		}
		return (this.normalize(pre) + " " + result + " " + this.normalize(post)).trim();
	}

	String normalize(String s) {
		//System.out.println("normalize: '" + s + "'");
		return (s != null && s.length() > 0) ? s.replaceAll("\\r", "")
				.replaceAll("\\\\n", "")
				.replaceAll("\\n", " ") : "";
	}

	private Visitor createVisitor(Map<String, Object> context, Map<String, Object> opts) {
		return new Visitor(this, context, opts);
	}

	public boolean isParseable(String s) { // public for testing
		boolean found = PARSEABLE_RE.matcher(s).find();
		System.out.println("FOUND: "+s+": "+found);
		return found;
	}

	public static String articlize(String s) {
		String phones = RiTa.phones(s, Util.opts("silent", true));
		//System.out.println(phones+" " + phones.substring(0,1));
		return (phones != null && phones.length() > 0
				&& RE.test("[aeiou]", phones.substring(0, 1))
						? "an "
						: "a ")
				+ s;
	}

	private static final Pattern PARSEABLE_RE = Pattern.compile("([\\(\\)]|\\$[A-Za-z_][A-Za-z_0-9-]*)");

	private static final Function<String, String> articlize = s -> RiTa.articlize(s);
	private static final Function<String, String> pluralize = s -> RiTa.pluralize(s);
	private static final Function<String, String> quotify = s -> '"' + s + '"';
	private static final Function<String, String> identity = s -> s;
	private static final Function<String, String> uc = s -> s.toUpperCase();
	private static final Function<String, String> capitalize = s -> {
		return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
	};

	private static final Map.Entry<String, Object>[] namedFunctions = new Map.Entry[] {
			new AbstractMap.SimpleEntry<String, Object>("articlize", articlize),
			new AbstractMap.SimpleEntry<String, Object>("pluralize", pluralize),
			new AbstractMap.SimpleEntry<String, Object>("capitalize", capitalize),
			new AbstractMap.SimpleEntry<String, Object>("quotify", quotify),
			new AbstractMap.SimpleEntry<String, Object>("ucf", capitalize),
			new AbstractMap.SimpleEntry<String, Object>("seq", identity),
			new AbstractMap.SimpleEntry<String, Object>("rseq", identity),
			new AbstractMap.SimpleEntry<String, Object>("norep", identity),
			new AbstractMap.SimpleEntry<String, Object>("uc", uc)
	};

	public static Map<String, Function<String, String>> transforms;

	static {
		transforms = new HashMap<String, Function<String, String>>();
		for (Map.Entry<String, Object> kv : namedFunctions) {
			transforms.put(kv.getKey(), (Function<String, String>) kv.getValue());
		}
	}

	public static void main(String[] args) {
		//		RiScript rs = new RiScript();
		//		Map<String, Object> opts = Util.opts();
		//		String s = rs.lexParseVisit("[$a=(A | B)]", opts, Util.opts("trace", true));
		//		System.out.println("\nResult: '" + s + "', opts: " + opts + " " + transforms);
		//System.out.println(HtmlEscape.unescapeHtml("Eve&nbsp;near Vancouver"));
		new RiScript().lex("$1foo", Util.opts("trace", true));
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
