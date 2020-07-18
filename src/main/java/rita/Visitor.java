package rita;

import java.util.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import rita.antlr.RiScriptBaseVisitor;
import rita.antlr.RiScriptParser.*;

public class Visitor extends RiScriptBaseVisitor<String> {

	private static final String SYM = "$";
	private static final String EOF = "<EOF>";
	private static final String FUNCTION = "()";

	// private static final String LP = "(";
	// private static final String RP = ")";
	// private static final String OR = "OR";
	// private static final String ASSIGN = "[]";

	protected RiScript parent;
	protected List<String> pendingSymbols;
	protected Map<String, Object> context;
	protected boolean trace;

	public Visitor(RiScript parent, Map<String, Object> context) {
		this(parent, context, null);
	}

	public Visitor(RiScript parent, Map<String, Object> context, Map<String, Object> opts) {
		super();
		if (context == null) context = Util.opts();
		this.trace = Util.boolOpt("trace", opts);
		this.parent = parent;
		this.context = context;
		this.pendingSymbols = new ArrayList<String>();
	}

	public String start(ScriptContext ctx) {
		return visitScript(ctx).trim();
	}

	public String visitInline(InlineContext ctx) {
		ExprContext token = ctx.expr(); // dup in visitAssign
		String id = symbolName(ctx.symbol().getText());
		if (this.trace) System.out.println("visitInline: "
				+ id + " = " + this.flatten(token) + "]");
		String result = this.visit(token);
		this.context.put(id, result);
		return result;
	}

	public String visitAssign(AssignContext ctx) {
		ExprContext token = ctx.expr();
		String id = symbolName(ctx.symbol().getText());
		if (this.trace) System.out.println("handleAssign: "
				+ id + " = " + this.flatten(token) + "]");
		String result = this.visit(token);
		this.context.put(id, result);
		return ""; // no output on vanilla assign
	}

	public String visitChildren(RuleNode node) {
		String result = "";
		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			String visit = this.visit(child);
			result += visit != null ? visit : "";
		}
		return result;
	}

	public String visitTerminal(TerminalNode ctx) {

		String term = ctx.getText();
		if (term.equals(Visitor.EOF)) return "";
		if (this.trace) System.out.println("visitTerminal: '"
				+ term.replaceAll("\\n", "\\\\n") + "'");
		return term;
		// NEXT: WORKING HERE (also rita-js transformed phrases)

		/*
		 * let term = ctx; if (typeof ctx.getText === 'function') { term =
		 * ctx.getText(); } let tfs = ctx.transforms; if (typeof term === 'string') { if
		 * (term === Visitor.EOF) return ''; term = this.parent.normalize(term);
		 * this.trace && /\S/.test(term) && console.log ('visitTerminal("' + term + '")
		 * tfs=[' + (tfs || '') + ']');
		 * 
		 * // Handle unresolved symbols and groups by simply // re-appending transforms
		 * to be handled in next pass //if
		 * (/([()]|\$[A-Za-z_][A-Za-z_0-9-]*)/.test(term)) { if
		 * (this.parent.isParseable(term)) { return term + (tfs ? tfs.reduce((acc, val)
		 * => acc + (typeof val === 'string' ? val : val.getText()), '') : ''); } } else
		 * if (typeof term === 'object') { // Here we've resolved a symbol to an object
		 * in visitSymbol this.trace && console.log('visitTerminal2(' + (typeof term) +
		 * '): "' + JSON.stringify(term) + '" tfs=[' + (tfs || '') + ']'); } else {
		 * this.trace && console.log('visitTerminal2(""): tfs=[' + (tfs || '') + ']'); }
		 * return this.handleTransforms(term, tfs);
		 */
	}

	/* visit the resolved symbol */
	public String visitSymbol(SymbolContext ctx) {

		TerminalNode tn = ctx.SYM();
		// hack: for blank .func() cases
		// if (!tn) return this.handleTransforms('', ctx.transform());

		String ident = symbolName(tn.getText());

		// the symbol is pending so just return it
		if (this.pendingSymbols.contains(ident)) {
			return SYM + ident;
		}

		String text = (String) this.context.get(ident);
		if (text == null) text = SYM + ident; // reset

		// hack to pass transforms along to visitTerminal
//    let textContext = { text, getText: () => text };
//    textContext.transforms = ctx.transforms || [];
//    ctx.transform().map(t => textContext.transforms.push(t.getText()));

		if (this.trace) System.out.println("visitSymbol: $"
				+ ident.replaceAll("^\\$", "") + " -> ...");
		// + " tfs=[" + (textContext.transforms || "") + "] ctx[\""
		// + ident + "\"]=" + (ident === "RiTa" ? "{RiTa}" : textContext.text));

		return this.visitTerminal(new TextContext(text));
	}

	class TextContext extends ParserRuleContext implements TerminalNode {
		private String text;
		private Token symbol;

		public TextContext(String text) {
			this.text = text;
			this.symbol = new CommonToken(Token.HIDDEN_CHANNEL);
		}

		@Override
		public String getText() {
			return this.text;
		}

		@Override
		public Token getSymbol() {
			return this.symbol;
		}
	}

	public String visitChars(CharsContext ctx) {

		if (this.trace) System.out.println
			("visitChars: '" + ctx.getText() + "'");
		return ctx.getText().toString();
	}

	public String visitChoice(ChoiceContext ctx) {

		// compute all options and weights
		List<ExprContext> options = new ArrayList<ExprContext>();

		List<WexprContext> wexprs = ctx.wexpr();
		for (int i = 0; i < wexprs.size(); i++) {
			WexprContext w = wexprs.get(i);
			WeightContext wctx = w.weight();
			int weight = wctx != null ? Integer.parseInt(wctx.INT().toString()) : 1;
			ExprContext expr = w.expr();
			if (expr == null) expr = emptyExpr(ctx);
			for (int j = 0; j < weight; j++) {
				options.add(expr);
			}
		}
		System.out.println("visitChoice: " + ctx.getText() + " :: "
				+ options.size() + " opts");

		ExprContext token = randomElement(options);
		return this.visit(token != null ? token : emptyExpr(ctx));
	}

	// HELPERS ------------------------------------------------------

	protected <T> T randomElement(List<T> options) {
		if (options.size() == 0) return null;
		return options.get((int) (Math.random() * options.size()));
	}

	protected ExprContext emptyExpr(ParserRuleContext parent) {

		return new ExprContext(parent, -1);
	}

	protected String symbolName(String text) {
		return (text.length() > 0 && text.substring(0, 1).equals(Visitor.SYM))
				? text.substring(1)
				: text;
	}

	protected String getRuleName(ParserRuleContext ctx) {
		return this.parent.parser.getRuleNames()[ctx.getRuleIndex()];
	}

	protected String flatten(ParserRuleContext... toks) {
		return Arrays.asList(toks).stream().map(t -> t.getText())
				.reduce("", (acc, t) -> (acc + t));
	}

}
