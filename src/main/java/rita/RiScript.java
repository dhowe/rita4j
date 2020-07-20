package rita;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;
import org.apache.commons.text.StringEscapeUtils;

import rita.antlr.RiScriptLexer;
import rita.antlr.RiScriptParser;
import rita.antlr.RiScriptParser.ScriptContext;

public class RiScript {

	public static final int MAX_TRIES = 100;

	protected RiScriptLexer lexer;
	protected RiScriptParser parser;
	protected Visitor visitor;

	protected static Map<String, Function<String, String>> transforms;

	public static String eval(String input) {

		return RiScript.eval(input, null);
	}

	public static String eval(String input, Map<String, Object> ctx) {

		return RiScript.eval(input, ctx, null);
	}

	public static String eval(String input, Map<String, Object> ctx, Map<String, Object> opts) {

		boolean trace = Util.boolOpt("trace", opts);
		boolean silent = Util.boolOpt("silent", opts);
		boolean onepass = Util.boolOpt("singlePass", opts);

		String last = input;
		RiScript rs = new RiScript().pushTransforms(ctx);
		String expr = rs.lexParseVisit(input, ctx, opts);
		if (!onepass && rs.isParseable(expr)) {
			for (int i = 0; i < RiScript.MAX_TRIES && !expr.equals(last); i++) {
				last = expr;
				expr = rs.lexParseVisit(expr, ctx, opts);
				if (trace) System.out.println("\nPass#" + (i + 2) + ": " + expr
						+ "\n-------------------------------------------------------\n");
				if (i >= RiScript.MAX_TRIES - 1) throw new RiTaException("Unable to resolve: \""
						+ input + "\" after " + RiScript.MAX_TRIES + " tries. An infinite loop?");
			}
		}
		// System.out.println("expr: " + expr + " " + RE.test("\\\\$[A-Za-z_]", expr));
		if (!silent && RiTa.SILENT && RE.test("\\$[A-Za-z_]", expr)) {
			System.out.println("[WARN] Unresolved symbol(s) in \"" + expr + "\"");
		}
		String result = rs.popTransforms(ctx).resolveEntities(expr);
		if (trace) System.out.println("Result: '" + result + "'");
		return result;
	}

	String[] preParse(String input, Map<String, Object> opts) {
		String parse = input, pre = "", post = "";
		boolean skipPreParse = Util.boolOpt("skipPreParse", opts);
		// console.log('preParse', parse);
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

		return new String[] { pre, parse, post };
	}

	private RiScript pushTransforms(Map<String, Object> ctx) {
		// TODO
		return this;
	}

	private RiScript popTransforms(Map<String, Object> ctx) {
		// TODO
		return this;
	}

	private String resolveEntities(String s) {
		return StringEscapeUtils.unescapeHtml4(s);
	}

	public CommonTokenStream lex(String input) {
		return this.lex(input, null);
	}

	public CommonTokenStream lex(String input, Map<String, Object> opts) {
		CharStream chars = CharStreams.fromString(input);
		this.lexer = new RiScriptLexer(chars);
		CommonTokenStream tokenStream = new CommonTokenStream(this.lexer);
		if (Util.boolOpt("trace", opts)) {
			System.out.println("-------------------------------------------------------");
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

		// try the parsing
		ScriptContext tree;
		try {
			tree = this.parser.script();
			if (Util.boolOpt("trace", opts)) {
				System.out.println("\n" + tree.toStringTree(
						Arrays.asList(parser.getRuleNames())) + "\n");
			}
		} catch (Exception e) {
			System.err.println("PARSER: '" + input + "'\n" + e.getMessage() + "\n");
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

			if (Util.boolOpt("trace", opts)) System.out.println("preParse(" + (pre.length() > 0 ? pre : "''")
					+ ',' + (post.length() > 0 ? post : "''") + "):");

			result = this.createVisitor(context, opts).start(tree);
		}
		return (this.normalize(pre) + " " + result
				+ " " + this.normalize(post)).trim();
	}

	String normalize(String s) {
		return (s != null && s.length() > 0) ? s.replaceAll("\\r", "")
				.replaceAll("\\\\n", "")
				.replaceAll("\\n", " ") : "";
	}

	private Visitor createVisitor(Map<String, Object> context, Map<String, Object> opts) {
		return new Visitor(this, context, opts);
	}

	public boolean isParseable(String s) { // public for testing
		return PARSEABLE_RE.matcher(s).find();
	}

	public static String articlize(String s) {
		String phones = RiTa.phones(s, Util.opts("silent", true));
		return (phones != null && phones.length() > 0
				&& RE.test("[aeiou]", phones.substring(0, 1)) ? "an " : "a ") + s;
	}

	private static final Pattern PARSEABLE_RE = Pattern.compile("([\\\\(\\\\)]|\\\\$[A-Za-z_][A-Za-z_0-9-]*)");

	public static void main(String[] args) {
		RiScript rs = new RiScript();
		Map<String, Object> opts = Util.opts();
		String s = rs.lexParseVisit("[$a=(A | B)]", opts, Util.opts("trace", true));
		System.out.println("\nResult: '" + s + "', opts: " + opts);
	}
}
