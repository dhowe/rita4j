package rita;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.*;

import rita.antlr.RiScriptBaseVisitor;
import rita.antlr.RiScriptParser;
import rita.antlr.RiScriptParser.*;

/*
 * Note: Transforms are always resolved AFTER the content
 *       in the visitX method where they first appeared
 */
public class Visitor extends RiScriptBaseVisitor<String> {

	private static final String EOF = "<EOF>";
	private static final String FUNCTION = "()";

	protected RiScript parent;
	protected List<String> pendingSymbols;
	protected Map<String, Object> context;
	protected List<String> appliedTransforms;
	protected boolean trace, silent;

	public Visitor(RiScript parent, Map<String, Object> ctx) {
		this(parent, ctx, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx, Map<String, Object> opts) {
		super();
		this.parent = parent;
		this.context = ctx;
		this.trace = Util.boolOpt("trace", opts);
		this.silent = Util.boolOpt("silent", opts);
		this.pendingSymbols = new ArrayList<String>();
		this.appliedTransforms = new ArrayList<String>();
		if (context == null) context = new HashMap<String, Object>();
	}

	public String start(ScriptContext ctx) {
		if (trace) System.out.println("start: '" + ctx.getText()
				.replaceAll("\\r?\\n", "\\\\n") + "'");
		pushTransforms(this.context);
		String result = visitScript(ctx);
		popTransforms(this.context);
		return result;
	}

	public String visitScriptOff(RiScriptParser.ScriptContext ctx) {
		if (trace) {
			String s = "\nvisitScript: '" + ctx.getText() + "'\t" + stack(ctx) + "\n";
			for (int i = 0; i < ctx.getChildCount(); i++) {
				s += "  " + (i + ") " + getRuleName(ctx.getChild(i))
						+ " '" + ctx.getChild(i).getText() + "'\n");
			}
			System.out.println(s);
		}
		return visitChildren(ctx);
	}

	//////////////////////////// Transforms //////////////////////////////////

	public String visitInline(InlineContext ctx) {
		List<TransformContext> txs = ctx.transform();
		ExprContext token = ctx.expr();
		String id = ctx.symbol().getText().replaceAll("^\\$", "");

		if (this.trace) System.out.println("visitInline: $"
				+ id + "=" + this.flatten(token) + " tfs=" + flatten(txs));

		// now visit the token 
		String visited = this.visit(token);

		// add the result to the context
		if (this.trace) System.out.println("resolveInline: $"
				+ id + " -> '" + visited + "'");
		this.context.put(id, visited);

		// finally check for transforms
		if (txs.size() < 1) return visited;

		String result = applyTransforms(visited, ctx.transform());
		return result != null ? result : ctx.getText();
	}

	public String visitChoice(ChoiceContext ctx) {

		List<TransformContext> txs = ctx.transform();
		ChoiceState choice = new ChoiceState(ctx);
		//if (parent instanceof Visitor && ((Visitor) parent).trace) {
		if (trace) System.out.println("visitChoice: '" + ctx.getText()
				+ "' options=['" + flatten(choice.options).replaceAll("\\|", "','")
				+ "'] tfs=" + flatten(ctx.transform()));

		// make the selection
		ParserRuleContext tok = choice.select();
		String tokStr = tok.getText();
		if (trace) System.out.println("  select: '" + tokStr + "' [" + getRuleName(tok) + "]");

		// now visit the token 
		String visited = visit(tok);
		if (txs.size() < 1) return visited;

		// now check for transforms
		String result = applyTransforms(visited, txs);
		if (result == null) result = visited + (txs.size() == 0 ? "" : flatten(txs));

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

		// if already a pending symbol, save for later
		if (this.pendingSymbols.contains(ident)) {
			if (trace) System.out.println("IGNORE PENDING Symbol: \"\" tfs="
					+ flatten(txs) + " -> " + result);
			return result;
		}

		if (trace) System.out.println("visitSymbol: $"
				+ ident + " tfs=" + flatten(txs));// + " -> " + result);

		// now try to resolve from context
		Object resolved = context.get(ident);

		// if it fails, give up / wait for next pass
		if (resolved == null) {
			//if (!silent) System.err.println("[WARN] Unable to resolve $" + ident);
			return result;
		}

		// now apply the transforms
		String applied = applyTransforms(resolved, txs);
		result = applied != null ? applied : resolved + flatten(txs);

		if (trace) System.out.println("resolveSymbol: '"
				+ ident + "' -> " + result);

		return result;
	}

	////////////////////////////////////////////////////////////////////////

	public String visitAssign(AssignContext ctx) {
		ExprContext token = ctx.expr();
		String id = ctx.symbol().getText().replaceAll("^\\$", "");
		if (this.trace) System.out.println("visitAssign: $"
				+ id + "=" + this.flatten(token));
		String result = this.visit(token);
		if (this.trace) System.out.println("resolveAssign: $"
				+ id + " -> '" + result + "' " + parent.ctxStr(context));
		this.context.put(id, result);
		return ""; // no output on vanilla assign
	}

	public String visitExpr(ExprContext ctx) { // trace only
		if (trace) {
			//List<TransformContext> txs = childTransforms(ctx);
			System.out.println("visitExpr: '" + ctx.getText());// + "' tfs=" + flatten(txs));
			printChildren(ctx);
		}
		return visitChildren(ctx);
	}

	public String visitChars(CharsContext ctx) {
		if (trace) System.out.println("visitChars: '" + ctx.getText() + "'");
		return ctx.getText();
	}

	public String visitCexpr(CexprContext ctx) {
		if (trace) System.out.println("visitCexpr: '" + ctx.getText() + "'\t" + stack(ctx));
		List<CondContext> conds = ctx.cond();
		//"cond={" + conds.map(c => c.getText().replace(',', '')) + '}');
		for (int i = 0; i < conds.size(); i++) {
			CondContext cond = conds.get(i);
			String id = cond.SYM().getText().replaceAll("^\\$", "");
			Operator op = Operator.fromString(cond.op().getText());
			String val = cond.chars().getText();
			val = val.replaceAll(",$", "");
			Object sym = this.context.get(id);
			// TODO: not sure about toString below
			boolean accept = sym != null ? op.invoke(sym.toString(), val) : false;
			if (!accept) return this.visitExpr(EMPTY);
		}
		return this.visitExpr(ctx.expr());
	}

	private static final ExprContext EMPTY = new ExprContext(null, -1);
		
	////////////////////////////////////////////////////////////////

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

	public String visitTransform(TransformContext ctx) {
		if (trace) System.err.println("[ERROR] visitTransform: '" + ctx.getText() + "'");
		return "";//visitChildren(ctx);
	}

	public String visitOp(OpContext ctx) {
		if (trace) System.out.println("visitOp: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	public String visitTerminal(TerminalNode tn) {
		String text = tn.getText();
		if (text.equals("\n")) return " "; // why do we need this?
		if (!text.equals(Visitor.EOF)) {
			if (trace) System.out.println("visitTerminal: '" + text + "'");
		}
		return null;
	}

	///////////////////////////// Transforms ///////////////////////////////

	private String applyTransforms(Object term, List<TransformContext> tfs) {
		if (term == null || tfs == null || tfs.size() < 1) return null;
		if (tfs.size() > 1) throw new RuntimeException("Invalid # Transforms: " + tfs.size());

		Object result = term;

		// make sure it is resolved
		if (term instanceof String) {
			result = this.parent.normalize((String) term);
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
	private Object applyTransform(Object target, String tx) {

		Object result = null;

		if (trace) System.out.println("applyTransform: '" +
				(target instanceof String ? target : target.getClass().getSimpleName())
				+ "' tf=" + tx);

		// check for function
		if (tx.endsWith(Visitor.FUNCTION)) {
			tx = tx.substring(0, tx.length() - 2);

			// 1. Static method - no cases in Java
			Method meth = Util.getStatic(String.class, tx, String.class);
			if (meth != null) {  // String static
				result = Util.invokeStatic(meth, target);
			}

			// 2. Member (String) method
			if (result == null) {
				meth = Util.getMethod(target, tx);
				if (meth != null) { // String method
					result = Util.invoke(target, meth);
				}
			}

			// 3. Function in context
			if (result == null) {
				//Map<String, Function<String, String>> cf = contextFunctions();
				if (this.context != null && this.context.containsKey(tx)) {
					Object func = this.context.get(tx);
					if (func instanceof Function) {
						result = ((Function<String, String>) func).apply((String) target);
					}
				}
			}

			// 4. Function in Map
			if (result == null && target instanceof Map) {
				Map m = (Map) target;
				if (m.containsKey(tx)) {
					Object func = m.get(tx);
					if (func instanceof Supplier) {
						result = ((Supplier) func).get();
					}
				}
			}
		}
		// check for property ?
		else {
			result = Util.getProperty(target, tx);
		}

		if (trace) System.out.println("resolveTransform: '"
				+ target + "' -> '" + result + "'");

		return result;
	}

	////////////////////////////// Other ///////////////////////////////////

	@SuppressWarnings("unchecked")
	private static final Set<Class> PRIMITIVES = new HashSet<Class>(
			Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class,
					Integer.class, Long.class, Float.class, Double.class));

	private static boolean isPrimitive(Object o) {
		return PRIMITIVES.contains(o.getClass());
	}

	String getRuleName(RuleContext ctx) {
		return RiScriptParser.ruleNames[ctx.getRuleIndex()];
	}

	String getRuleName(TerminalNode ctx) {
		int type = ctx.getSymbol().getType();
		if (type < 0) return "TerminalNode";
		return this.parent.lexer.getRuleNames()[type];
	}

	String getRuleName(RuleNode ctx) {
		return getRuleName(ctx.getRuleContext());
	}

	//	String getRuleName(RuleContext ctx) {
	//		return ctx instanceof TerminalNode
	//				? this.parent.lexer.getRuleNames()[((TerminalNode) ctx).getSymbol().getType()]
	//				: RiScriptParser.ruleNames[ctx.getRuleIndex()];
	//	}

	String getRuleName(ParseTree ctx) {
		if (ctx instanceof TerminalNode) return getRuleName((TerminalNode) ctx);
		if (ctx instanceof RuleNode) return getRuleName((RuleNode) ctx);
		if (ctx instanceof RuleContext) return getRuleName((RuleContext) ctx);
		throw new RuntimeException("fail: " + ctx.getClass().getCanonicalName());
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
			if (ruleIndex < 0)
				sb.append("n/a");
			else
				sb.append(ruleNames[ruleIndex] + " <- ");
			p = p.parent;
		}
		return sb.toString().replaceAll(" <- $", "]");
	}

	public void printChildren(RuleContext ctx) {
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree child = ctx.getChild(i);
			console.log("  child[" + i + "]: '" + child.getText() +
					"' [" + this.getRuleName(child) + "]");
		}
	}

	public String visitChildren(RuleNode node) {
		String result = "";
		//String nodeStr = node.getText();//to debug
		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			//String childStr = child.getText();//to debug
			String visit = this.visit(child);
			result += visit != null ? visit : "";
		}
		return result;
	}

	private void pushTransforms(Map<String, Object> ctx) {
		for (String tx : RiScript.transforms.keySet()) {
			if (!ctx.containsKey(tx)) {
				ctx.put(tx, RiScript.transforms.get(tx));
				this.appliedTransforms.add(tx);
			}
		}
	}

	private void popTransforms(Map<String, Object> ctx) {
		for (String tx : appliedTransforms)
			ctx.remove(tx);
	}

	public static void main(String[] args) {
		//Function<String, String> up = x -> x.toUpperCase();
		//assertEq(RiTa.evaluate("(a | a).up()", opts("up", up))
		//RiTa.evaluate("$a.toUpperCase()", opts("a", "$b", "b", "hello"), 
		//opts("singlePass", false, "trace", true));

		//		Map<String, Object> opts = opts();
		//		RiTa.evaluate("[$b=(a | a)].toUpperCase()",
		//				opts, opts("singlePass", false, "trace", true));
		//		System.out.println(opts.get("b"));
		//		Map<String, Object> opts = opts();
		//		RiTa.evaluate("[$b=(a | a)].toUpperCase()",
		//				opts, opts("singlePass", true, "trace", true));
		//System.out.println(opts.get("b"));
		//System.out.println(StringEscapeUtils.unescapeHtml4("&#35;"));
	}
}
