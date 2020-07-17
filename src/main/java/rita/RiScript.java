package rita;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.*;

import rita.antlr.RiScriptLexer;
import rita.antlr.RiScriptParser;
import rita.antlr.RiScriptParser.ScriptContext;

public class RiScript {

	protected RiScriptLexer lexer;
	protected RiScriptParser parser;
	protected Visitor visitor;

	protected static Map<String, Function<String, String>> transforms;

	public static String eval(String input) {

		return RiScript.eval(input, null);
	}

	public static String eval(String input, Map<String, Object> ctx) {

		return new RiScript().lexParseVisit(input, ctx, null);
	}

	public static String eval(String input, Map<String, Object> ctx, Map<String, Object> opts) {

		return new RiScript().lexParseVisit(input, ctx);
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

		ScriptContext tree = this.lexParse(input, opts);
		Visitor vis = this.createVisitor(context, opts);
		return vis.start(tree);
	}

	private Visitor createVisitor(Map<String, Object> context, Map<String, Object> opts) {
		return new Visitor(this, context, opts);
	}

	public static boolean isParseable(String s) { // public for testing
		return PARSEABLE_RE.matcher(s).find();
	}

	public static String articlize(String s) {
		String phones = RiTa.phones(s, Util.opts("silent", true));
		return (phones != null && phones.length() > 0
				&& RE.test("[aeiou]", phones.substring(0,1)) ? "an " : "a ") + s;
	}

	private static final Pattern PARSEABLE_RE = Pattern.compile("([\\\\(\\\\)]|\\\\$[A-Za-z_][A-Za-z_0-9-]*)");

	public static void main(String[] args) {
		RiScript rs = new RiScript();
		Map<String, Object> opts = Util.opts();
		String s = rs.lexParseVisit("[$a=(A | B)]", opts, Util.opts("trace", true));
		System.out.println("\nResult: '" + s + "', opts: " + opts);
	}
}
