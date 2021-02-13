package rita;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import rita.antlr.*;
import rita.antlr.RiScriptParser.*;

public class Visitor extends RiScriptParserBaseVisitor<String> {

	protected RiScript parent;
	protected boolean trace, silent;
	protected Map<String, Object> context;
	protected Map<String, ChoiceState> choices;
	protected List<String> pendingSymbols;

	public Visitor(RiScript parent) {
		this(parent, null, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx) {
		this(parent, ctx, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx, Map<String, Object> opts) {
		super();
		this.parent = parent;
		this.choices = new HashMap<String, ChoiceState>();
		this.init(ctx, opts);
	}

	Visitor init(Map<String, Object> ctx, Map<String, Object> opts) {
		this.trace = Util.boolOpt("trace", opts);
		this.silent = Util.boolOpt("silent", opts);
		this.pendingSymbols = new ArrayList<String>();
		this.context = ctx != null ? ctx : new HashMap<String, Object>();
		return this;
	}

	public String start(ScriptContext ctx) {
		String text = ctx.getText();
		boolean lastBreak = RE.test("\\n$", text);
		if (trace) System.out.println("start: '" + text
				.replaceAll("\\r?\\n", "\\\\n") + "'");
		//printChildren(ctx);
		String result = this.visitChildren(ctx);
		return lastBreak ? result : result.replaceAll("\\r?\\n$", "");
	}

	public String visitLink(LinkContext ctx) {
		String text = ctx.getText();
		String url = ctx.url().getText();
		String exp = ctx.expr().getText();
		String WS = ctx.WS().toString();
		if (trace) System.out.println("visitLink: '"
				+ text + "' link=" + url);
		return '[' + this.visit(ctx.expr()) + ']'
				+ "&lpar;" + url + "&rpar;" + flatten(ctx.WS());
	}

	public String visitLine(LineContext ctx) {
		String line = this.visitChildren(ctx);
		return line.length() > 0 ? line + "\n" : "";
	}

	public String visitChoice(ChoiceContext ctx) {

		String text = ctx.getText()
				.replaceAll("\\.[A-Za-z_0-9][A-Za-z_0-9]*(\\(\\))?", "");

		ChoiceState choice = choices.get(text);
		if (choice == null) {
			choice = new ChoiceState(this, ctx);
			choices.put(text, choice);
		}

		List<TransformContext> txs = ctx.transform();
		String tstr = flatten(txs);

		if (trace) System.out.println("visitChoice: '" + text
				+ "' options=['" + flatten(choice.options).replaceAll("\\|", "','")
				+ "'] tfs=" + tstr);

		// make the selection
		ParserRuleContext tok = choice.select(tstr);
		if (trace) System.out.println("  select: '"
				+ tok.getText() + "' [" + getRuleName(tok) + "]");

		// now visit the token 
		String visited = this.visit(tok).trim();

		// now check for transforms
		if (txs.size() < 1) return visited;

		String applied = applyTransforms(visited, txs);
		String result = applied != null ? applied : RiTa.LP + visited + RiTa.RP + tstr;

		if (this.trace) System.out.println("resolveChoice: '" + result + "'");

		return result;
	}

	public String visitSymbol(SymbolContext ctx) {

		List<TransformContext> txs = ctx.transform();
		String result = ctx.getText();
		TerminalNode tn = ctx.SYM();

		if (tn == null) { // handle transform on empty string
			String applied = applyTransforms("", txs);
			return applied != null ? applied : result;
		}

		// get the symbol identifier
		String ident = tn.getText().replaceAll("^\\$", "");

		if (trace) System.out.println("visitSymbol: $" + ident +
				(context.get(RiTa.DYN + ident) != null ? " [dynamic]" : "")
				+ " tfs=" + flatten(txs));

		// if the symbol is pending just return it
		if (this.pendingSymbols.contains(ident)) {
			if (trace) console.log("resolveSymbol[0]: (pending) $" + ident);//+ " -> " + result);
			return result;
		}

		Object resolved = context.get(ident);
		if (resolved == null) {  // try the context

			// try for a dynamic in context
			resolved = context.get(RiTa.DYN + ident);
			if (resolved != null) {
				return this.resolveDynamic(ident, (String) resolved, txs);
			}

			// otherwise give up, wait for next pass
			if (trace) System.out.println("resolveSymbol[1]: '"
					+ ident + "' -> '" + result + "'");
			return result;
		}

		// if the symbol is not fully resolved, save it for next time (as an inline*)
		if (resolved instanceof String && this.parent.isParseable((String) resolved)) {
			this.pendingSymbols.add(ident);
			String tmp = RiTa.LP + RiTa.SYM + ident + RiTa.EQ + resolved + RiTa.RP + flatten(txs);
			if (trace) console.log("resolveSymbol[P]: $" + ident + " -> " + tmp);
			return tmp;
		}

		// now check for transforms
		if (txs.size() < 1) {
			if (trace) System.out.println("resolveSymbol[2]: '"
					+ ident + "' -> '" + resolved.toString() + "'");
			return resolved.toString(); // Not sure about this
		}

		String applied = applyTransforms(resolved, txs);
		result = applied != null ? applied : resolved + flatten(txs);

		if (trace) System.out.println("resolveSymbol[3]: '"
				+ ident + "' -> '" + result + "'");

		return result;
	}

	public String visitAssign(AssignContext ctx) {
		String result;
		ExprContext token = ctx.expr();
		ParserRuleContext symbol = ctx.symbol();
		if (symbol == null) symbol = ctx.dynamic();
		String id = symbol.getText();

		if (id.startsWith(RiTa.DYN)) {
			if (this.trace) System.out.println("visitAssign: "
					+ id + "=" + this.flatten(token) + " [*DYN*]");
			result = token.getText();
		}
		else {
			id = symbolName(id);
			if (this.trace) System.out.println("visitAssign: $"
					+ id + "=" + this.flatten(token));
			result = this.visit(token);
		}

		this.context.put(id, result);
		if (this.trace) System.out.println("resolveAssign: context["
				+ id + "] -> '" + result + "' ");

		// no output if first on line
		return ctx.start.getCharPositionInLine() == 0 ? "" : result;
	}

	public String visitExpr(ExprContext ctx) { // trace only
		if (trace) {
			//List<TransformContext> txs = childTransforms(ctx);
			System.out.println("visitExpr: '" + ctx.getText() + "'");// + "' tfs=" + flatten(txs));
			//printChildren(ctx);
		}
		return visitChildren(ctx);
	}

	public String visitChars(CharsContext ctx) {
		if (trace) System.out.println("visitChars: '" + ctx.getText() + "'");
		//printChildren(ctx);

		return ctx.getText();
	}

	public String visitCexpr(CexprContext ctx) {
		if (trace) System.out.println("visitCexpr: '" + ctx.getText() + "'\t" + stack(ctx));
		List<CondContext> conds = ctx.cond();
		//"cond={" + conds.map(c => c.getText().replace(',', '')) + '}');
		for (int i = 0; i < conds.size(); i++) {
			CondContext cond = conds.get(i);
			String id = cond.symbol().getText().replaceAll("^\\$", "");
			Operator op = Operator.fromString(cond.op().getText());
			String val = cond.chars().getText().replaceAll(',' + RiTa.SYM, "");
			Object sym = this.context.get(id);
			// TODO: not sure about toString below
			boolean accept = sym != null ? op.invoke(sym.toString(), val) : false;
			if (!accept) return this.visitExpr(EMPTY);
		}
		return this.visitExpr(ctx.expr());
	}

	public String visitCond(CondContext ctx) {
		if (trace) System.out.println("visitCond: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	public String visitWeight(WeightContext ctx) {
		if (trace) System.out.println("visitWeight: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	public String visitWexpr(WexprContext ctx) {
		if (trace) System.out.println("visitWexpr: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	public String visitOp(OpContext ctx) {
		if (trace) System.out.println("visitOp: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	public String visitTerminal(TerminalNode tn) {
		String text = tn.getText();
		if (trace && !text.equals(RiTa.EOF)) {
			System.out.println("visitTerminal: '"
					+ text.replaceAll("\\r?\\n", "\\\\n") + "'");
		}
		return null;//text.equals(RiTa.EOF) ? null : text;
	}

	public String visitTransform(TransformContext ctx) { // should never happen
		throw new RuntimeException("[ERROR] visitTransform: '" + ctx.getText() + "'");
	}

	///////////////////////////// Transforms ///////////////////////////////

	private String applyTransforms(Object term, List<TransformContext> tfs) {

		if (term == null || tfs == null || tfs.size() < 1) {
			return null;
		}

		if (tfs.size() > 1) throw new RuntimeException(
				"Invalid # Transforms: " + tfs.size());

		Object result = term;

		// make sure it is resolved
		if (term instanceof String) {
			result = normalize((String) term);
			if (this.parent.isParseable((String) result)) { // save for later
				//throw new RuntimeException("applyTransforms.isParseable=true: '" + result + "'");
				return null;
			}
		}

		// NOTE: even multiple transforms show up as a single one here [TODO]
		TransformContext tf = tfs.get(0);
		if (tf == null) throw new RuntimeException("Null Transform: " + flatten(tfs));
		String[] transforms = tf.getText().replaceAll("^\\.", "").split("\\.");
		for (int i = 0; i < transforms.length; i++) {
			result = applyTransform(result, transforms[i]);
		}

		return result != null ? result.toString() : null;
	}

	// Attempts to apply transform, returns null on failure
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object applyTransform(Object target, String tx) {

		Object result = null;
		String raw = target + RiTa.DOT + tx;

		if (trace) System.out.println("applyTransform: '" +
				(target instanceof String ? target : target.getClass().getSimpleName())
				+ "' tf=" + tx);

		// check for function
		if (tx.endsWith(RiTa.FUNC)) tx = tx.substring(0, tx.length() - 2);

		// 0. Static function (only join/format on String, maybe RiTa?)

		// 1. Member (String) method
		Method meth = Util.getMethod(target, tx);
		if (meth != null) { // String method
			result = Util.invoke(target, meth);
			if ((target instanceof String && ((String) target).length() == 0)
					&& (result instanceof String && ((String) result).length() == 0)) {
				if (!this.silent && !RiTa.SILENT) System.err.println(
						"[WARN] Unresolved transform[0]: " + raw);
			}
		}

		// 2. Function in context
		if (result == null && this.context != null && this.context.containsKey(tx)) {
			Object func = this.context.get(tx);
			if (func instanceof Function) {
				result = ((Function<String, String>) func).apply((String) target);
			}
			if (func instanceof Supplier) {
				result = ((Supplier<String>) func).get();
			}
		}

		// 2. Function in context
		if (result == null && RiScript.transforms.containsKey(tx)) {
			Object func = RiScript.transforms.get(tx);
			if (func instanceof Function) {
				result = ((Function<String, String>) func).apply((String) target);
			}
			if (func instanceof Supplier) {
				result = ((Supplier<String>) func).get();
			}
		}

		// 3. Function in Map
		if (result == null && target instanceof Map) {
			Map m = (Map) target;
			if (m.containsKey(tx)) {
				Object func = m.get(tx);
				if (func instanceof Supplier) {
					result = ((Supplier) func).get();
				}
			}
		}
		// check for property

		if (result == null) {
			try {
				result = Util.getProperty(target, tx);
			} catch (RiTaException e) {
				if (!this.silent && !RiTa.SILENT) System.err.println(
						"[WARN] Unresolved transform: " + raw);
				return raw;
			}
		}

		if (trace) System.out.println("resolveTransform: '"
				+ target + "' -> '" + result + "'");

		return result;
	}

	////////////////////////////// Other ///////////////////////////////////

	private static final ExprContext EMPTY = new ExprContext(null, -1);

	@SuppressWarnings({ "rawtypes" })
	private static final Set<Class> PRIMITIVES = new HashSet<Class>(
			Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class,
					Integer.class, Long.class, Float.class, Double.class));

	@SuppressWarnings("unused")
	private static boolean isPrimitive(Object o) {
		return PRIMITIVES.contains(o.getClass());
	}

	private static String symbolName(String text) {
		return (text != null && text.length() > 0
				&& text.startsWith(RiTa.SYM))
						? text.substring(1)
						: text;
	}

	private String normalize(String s) {
		return (s != null && s.length() > 0) ? s
				.replaceAll("\\r", "")
				.replaceAll("\\\\n", "")
				.replaceAll("\\n", " ") : "";
	}

	private String resolveDynamic(String ident,
			String resolved, List<TransformContext> txs) {
		if (!resolved.matches("^\\([^()]*\\)$")) { // TODO: compile
			resolved = RiTa.LP + resolved + RiTa.RP;
		}
		String result = resolved + flatten(txs);
		if (trace) console.log("resolveDynamic: "
				+ RiTa.DYN + ident + " -> '" + result + "'");
		return result;
	}

	String getRuleName(ParseTree ctx) {

		Object pl = ctx.getPayload();
		String rn = "UNKNOWN";
		if (pl instanceof Token) {
			rn = this.parent.parser.getVocabulary()
					.getSymbolicName(((Token) pl).getType());
		}
		else if (pl instanceof ParserRuleContext) {
			rn = RiScriptParser.ruleNames[((ParserRuleContext) pl)
					.getRuleIndex()].replaceAll("\\[.*", "[");
		}
		return rn;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	String flatten(List l) {
		if (l == null) return "";
		if (!l.stream().allMatch(RuleContext.class::isInstance)) {
			throw new RuntimeException("Invalid state in flatten(List)");
		}
		List<RuleContext> toks = l;
		String s = (String) toks.stream().map(t -> t.getText())
				.reduce("", (acc, t) -> (acc + "|" + t));
		return s.startsWith("|") ? s.substring(1) : s;
	}

	String flatten(RuleContext ctx) {
		List<RuleContext> l = new ArrayList<RuleContext>();
		l.add(ctx);
		return flatten(l);
	}

	private String stack(RuleContext p) {
		String[] ruleNames = this.parent.parser.getRuleNames();
		StringBuilder sb = new StringBuilder("    [");
		while (p != null) {
			// compute what follows who invoked us
			int ruleIndex = p.getRuleIndex();
			sb.append(ruleIndex < 0 ? "n/a" : ruleNames[ruleIndex] + " <- ");
			p = p.parent;
		}
		return sb.toString().replaceAll(" <- $", "]");
	}

	public void printChildren(RuleContext ctx) {
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree child = ctx.getChild(i);
			console.log("  child[" + i + "]: '" + child.getText()
					+ "' [" + this.getRuleName(child) + "]");
		}
	}

	public String visitChildren(RuleNode node) {
		String result = "";
		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			String visit = this.visit(child);
			result += (visit != null ? visit : "");
		}
		return result;
	}

}
