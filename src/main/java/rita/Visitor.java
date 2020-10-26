package rita;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.management.RuntimeErrorException;

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
	static final String FUNCTION = "()", DOT = ".";

	protected int indexer;
	protected boolean trace;
	protected RiScript parent;
	protected Map<String, Object> context;
	protected Map<Integer, ChoiceState> sequences;
	protected List<String> appliedTransforms, pendingSymbols;

	public Visitor(RiScript parent) {
		this(parent, null, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx) {
		this(parent, ctx, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx, Map<String, Object> opts) {
		super();
		this.parent = parent;
		this.sequences = new HashMap<Integer, ChoiceState>();
		this.init(ctx, opts);
	}

	Visitor init(Map<String, Object> ctx, Map<String, Object> opts) {
		this.trace = Util.boolOpt("trace", opts);
		this.pendingSymbols = new ArrayList<String>();
		this.context = ctx != null ? ctx : new HashMap<String, Object>();
		return this;
	}

	public String start(ScriptContext ctx) {
		if (trace) System.out.println("start: '" + ctx.getText()
				.replaceAll("\\r?\\n", "\\\\n") + "'");
		this.indexer = 0;
		//pushTransforms(this.context);
		String result = visitScript(ctx);
		//popTransforms(this.context);
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

		ExprContext token = ctx.expr();
		List<TransformContext> txs = ctx.transform();
		String id = ctx.symbol().getText().replaceAll("^\\$", "");

		if (this.trace) System.out.println("visitInline: $"
				+ id + "=" + this.flatten(token) + " tfs=" + flatten(txs));

		String visited = null;
		
    // if we've already resolved (likely as an inline) then reuse it
		Object lookup = this.context.get(id);
		if (lookup instanceof String && !this.parent.isParseable((String) lookup)) {
      if (trace) console.log("resolveInline[0]: $" + id + " -> '" + lookup + "' (already defined)");
      visited = (String) lookup;
		}
		else {
			// otherwise, visit token and add result to context
			visited = this.visit(token);
			this.context.put(id, visited);
		}

		// apply transforms if we have them
		if (txs.size() < 1) return visited;
		String applied = applyTransforms(visited, txs);
		String result = applied != null ? applied : visited + flatten(txs);

		if (this.trace) System.out.println("resolveInline: $"
				+ id + " -> '" + result + "'");

		// return result or defer for later
		return result != null ? result : ctx.getText();
	}

	public String visitChoice(ChoiceContext ctx) {

		List<TransformContext> txs = ctx.transform();
		ChoiceState choice = sequences.get(++this.indexer);
		if (choice == null) {
			choice = new ChoiceState(this, ctx);
			if (!choice.type.equals(ChoiceState.SIMPLE)) {
				sequences.put(choice.id, choice);
			}
		}

		if (trace) System.out.println("visitChoice: '" + ctx.getText()
				+ "' options=['" + flatten(choice.options).replaceAll("\\|", "','")
				+ "'] tfs=" + flatten(txs));

		// make the selection
		ParserRuleContext tok = choice.select();
		if (trace) System.out.println("  select: '"
				+ tok.getText() + "' [" + getRuleName(tok) + "]");

		// now visit the token 
		String visited = visit(tok);

		// now check for transforms
		if (txs.size() < 1) return visited;

		String applied = applyTransforms(visited, txs);
		String result = applied != null ? applied : '(' + visited + ')' + flatten(txs);

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

		if (trace) System.out.println("visitSymbol: $"
				+ ident + " tfs=" + flatten(txs));// + " -> " + result);
		
    // if the symbol is pending just return it
    if (this.pendingSymbols.contains(ident)) {
      if(trace) console.log("resolveSymbol[0]: (pending) $" + ident);//+ " -> " + result);
      return result;
    }

		// now try to resolve from context
		Object resolved = context.get(ident);

		// if it fails, give up / wait for next pass
		if (resolved == null) {
			if (trace) System.out.println("resolveSymbol[1]: '"
					+ ident + "' -> '" + result + "'");
			return result;
		}

    // if the symbol is not fully resolved, save it for next time (as an inline*)
    if (resolved instanceof String && this.parent.isParseable((String) resolved)) {
      this.pendingSymbols.add(ident);
      String tmp = "[$" + ident + "=" + resolved + "]" + flatten(txs);
      if(trace) console.log("resolveSymbol[P]: $" + ident + " -> " + tmp);
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
			System.out.println("visitExpr: '" + ctx.getText() + "'");// + "' tfs=" + flatten(txs));
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
		if (text.equals("\n")) return " "; // why do we need this?
		if (!text.equals(Visitor.EOF)) {
			if (trace) System.out.println("visitTerminal: '" + text + "'");
		}
		return null;
	}

	public String visitTransform(TransformContext ctx) { // should never happen
		throw new RuntimeException("[ERROR] visitTransform: '" + ctx.getText() + "'");
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object applyTransform(Object target, String tx) {

		Object result = null;
		String raw = target + Visitor.DOT + tx;

		if (trace) System.out.println("applyTransform: '" +
				(target instanceof String ? target : target.getClass().getSimpleName())
				+ "' tf=" + tx);

		// check for function
		if (tx.endsWith(Visitor.FUNCTION)) {
			tx = tx.substring(0, tx.length() - 2);

			// 0. Static function (only join/format on String, maybe RiTa)
			
			// 1. Member (String) method
			Method meth = Util.getMethod(target, tx);
			if (meth != null) { // String method
				result = Util.invoke(target, meth);
			}
			
			// 2. Function in context
			else if (this.context != null && this.context.containsKey(tx)) {
				Object func = this.context.get(tx);
				if (func instanceof Function) {
					result = ((Function<String, String>) func).apply((String) target);
				}
			}

			// 3. Function in Map
			else if (target instanceof Map) {
				Map m = (Map) target;
				if (m.containsKey(tx)) {
					Object func = m.get(tx);
					if (func instanceof Supplier) {
						result = ((Supplier) func).get();
					}
				}
			}
			
			else {
        if (this.trace) System.err.println("[WARN] Unresolved transform: " + raw);
        return raw;
			}
		}
		// check for property
		else {
			try {
				result = Util.getProperty(target, tx);
			} catch (RiTaException e) {
        if (this.trace) System.err.println("[WARN] Unresolved transform: " + raw);
        return raw;
			}
		}

		if (trace) System.out.println("resolveTransform: '"
				+ target + "' -> '" + result + "'");

		return result;
	}

	////////////////////////////// Other ///////////////////////////////////

	private static final ExprContext EMPTY = new ExprContext(null, -1);

	@SuppressWarnings("unchecked")
	private static final Set<Class> PRIMITIVES = new HashSet<Class>(
			Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class,
					Integer.class, Long.class, Float.class, Double.class));

	private static boolean isPrimitive(Object o) {
		return PRIMITIVES.contains(o.getClass());
	}

	// simplify 
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

	String getRuleName(ParseTree ctx) {
		if (ctx instanceof RuleNode) return getRuleName((RuleNode) ctx);
		if (ctx instanceof RuleContext) return getRuleName((RuleContext) ctx);
		if (ctx instanceof TerminalNode) return getRuleName((TerminalNode) ctx);
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
			sb.append(ruleIndex < 0 ? "n/a" : ruleNames[ruleIndex] + " <- ");
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
		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			String visit = this.visit(child);
			result += visit != null ? visit : "";
		}
		return result;
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
