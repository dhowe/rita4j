package rita;

import static rita.Util.opts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.*;
import org.apache.commons.text.StringEscapeUtils;

import rita.antlr.RiScriptBaseVisitor;
import rita.antlr.RiScriptParser;
import rita.antlr.RiScriptParser.*;

public class Visitor extends RiScriptBaseVisitor<String> {

	//	private static final String SYM = "$";
	//	private static final String DOT = ".";
	private static final String EOF = "<EOF>";
	private static final String FUNCTION = "()";

	protected RiScript parent;
	protected List<String> pendingSymbols;
	protected Map<String, Object> context;
	protected List<String> appliedTransforms;
	protected boolean trace;

	public Visitor(RiScript parent, Map<String, Object> ctx) {
		this(parent, ctx, null);
	}

	public Visitor(RiScript parent, Map<String, Object> ctx, Map<String, Object> opts) {
		super();
		this.parent = parent;
		this.context = ctx;
		this.trace = Util.boolOpt("trace", opts);
		this.appliedTransforms = new ArrayList<String>();
		this.pendingSymbols = new ArrayList<String>();
		if (context == null) context = new HashMap<String, Object>();
	}

	public String start(ScriptContext ctx) {
		if (trace) System.out.println("start: '" + ctx.getText()
				.replaceAll("\\r?\\n", "\\\\n") + "'");
		pushTransforms(this.context);
		String result = visitScript(ctx);
		popTransforms(this.context);
		return resolveEntities(result);
	}

	public String visitScriptOff(RiScriptParser.ScriptContext ctx) {
		if (trace) System.out.println("visitScript: '" + ctx.getText() + "'\t" + stack(ctx));
		for (int i = 0; i < ctx.getChildCount(); i++) {
			System.out.println(i + ") " + ctx.getChild(i).getClass() + " '" + ctx.getChild(i).getText() + "'");
		}
		return visitChildren(ctx);
	}

	private String resolveEntities(String s) {
		return StringEscapeUtils.unescapeHtml4(s);
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
		for (String tx : appliedTransforms) ctx.remove(tx);
	}

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

	public String visitInline(InlineContext ctx) {
		ExprContext token = ctx.expr(); // dup in visitAssign
		String id = ctx.symbol().getText().replaceAll("^\\$", "");
		if (this.trace) System.out.println("visitInline: $"
				+ id + "=" + this.flatten(token));
		//passTransformsAsChildrenOf(token, ctx.transform());
		String result = this.visit(token);
		if (this.trace) System.out.println("resolveInline: $"
				+ id + " -> '" + result + "'");
		this.context.put(id, result);
		result = this.applyTransforms(result, ctx.transform());
		return result;
	}

	public String visitAssignX(AssignContext ctx) {
		if (trace) System.out.println("visitAssign: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
	}

	//	public String visitScript(ScriptContext ctx) {
	//		if (trace) System.out.println("visitScript: '" + ctx.getText());
	//		return visitChildren(ctx);
	//	}

	public String visitExpr(ExprContext ctx) {
		List<TransformContext> txs = childTransforms(ctx);
		if (trace) System.out.println("visitExpr: '" + ctx.getText() + "' tfs=" + flatten(txs));
		//System.out.println("  chil: "+flatten(ctx.children));
		return visitChildren(ctx);
	}

	public String visitChars(CharsContext ctx) {
		List<TransformContext> txs = siblingTransforms(ctx, true);
		if (trace) System.out.println("visitChars: '" + ctx.getText() + "' tfs=" + flatten(txs));
		return txs.size() > 0 ? applyTransforms(ctx.getText(), txs) : ctx.getText();
	}

	public String visitChoice(ChoiceContext ctx) {
		ChoiceState choiceState = new ChoiceState(this, ctx);
		ParserRuleContext tok = choiceState.select();
		String s = "  select: '" + tok.getText() + "'";
		passTransformsAsChildrenOf(tok, ctx.transform());
		List<TransformContext> txs = childTransforms(tok);
		if (trace) System.out.println(s + " tfs= " + flatten(txs));
		return this.visit(tok);// != null ? token : emptyExpr(ctx));
	}

	// TODO: [NEXT] need to handle fails in applyTransforms()
	// 			 should return input (x.tf() || x.tp ) if no transform succeeds
	//
	
	public String visitSymbol(SymbolContext ctx) {
		String res = ctx.getText();
		TerminalNode tn = ctx.SYM();
		if (tn == null) return applyTransforms("", ctx.transform());
		String ident = tn.getText().replaceAll("^\\$", "");

		if (this.pendingSymbols.contains(ident/*tn.getText()*/)) {
			return res;
		}

		List<TransformContext> txs = ctx.transform();
		//String ident = tn.getText().replaceAll("^\\$", "");
		Object def = fromContextOrDef(ident, null);

		if (def != null) {

			if (isPrimitive(def)) def = def.toString();

			if (def instanceof String) {
				if (this.parent.isParseable((String) def)) {
					this.pendingSymbols.add(ident);
				}
				res = applyTransforms((String) def, txs);
			} else {

				res = applyObjectTransforms(def, txs);
			}
		}

		if (trace) System.out.println("visitSymbol: '"
				+ ident + "' tfs=" + flatten(txs) + " -> " + res);

		return res;
	}

	//	private String symbolName(String t) {
	//		return t.replaceAll("^\\$", "");
	//	}

	@SuppressWarnings("unchecked")
	private static final Set<Class> PRIMITIVES = new HashSet<Class>(
			Arrays.asList(Boolean.class, Character.class, Byte.class, Short.class,
					Integer.class, Long.class, Float.class, Double.class));

	private static boolean isPrimitive(Object o) {
		return PRIMITIVES.contains(o.getClass());
	}

	private Object fromContextOrDef(String ident) {
		return fromContextOrDef(ident, ident);
	}

	private Object fromContextOrDef(String ident, Object def) {
		String key = ident.startsWith("$") ? ident.substring(1) : ident;
		return context != null ? context.getOrDefault(key, def) : def;
	}

	////////////////////////////////////////////////////////////////

	public String visitCexpr(CexprContext ctx) {
		if (trace) System.out.println("visitCexpr: '" + ctx.getText() + "'\t" + stack(ctx));
		return visitChildren(ctx);
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

	public String visitTransform(TransformContext ctx) {
		if (trace) System.out.println("visitTransform: '" + ctx.getText() + "'");
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

	/////////////////////////////TX Funcs////////////////////////////////////////

	private List<TransformContext> siblingTransforms(ParserRuleContext ctx) {
		return siblingTransforms(ctx, false);
	}

	private List<TransformContext> childTransforms(ParserRuleContext ctx) {
		return ctx.getRuleContexts(TransformContext.class);
	}

	private List<TransformContext> siblingTransforms(ParserRuleContext ctx, boolean remove) {
		ParserRuleContext parent = ctx.getParent();
		List<TransformContext> txs = new ArrayList<TransformContext>();
		if (parent != null) {
			for (Iterator<ParseTree> it = parent.children.iterator(); it.hasNext();) {
				ParseTree pt = (ParseTree) it.next();
				//System.out.println("  C: " + pt.getText());
				if (pt instanceof TransformContext) {
					txs.add((TransformContext) pt);
					if (remove) it.remove();
				}
			}
		}
		return txs;
	}

	private ParserRuleContext passTransformsAsChildrenOf(ParserRuleContext ctx,
			List<TransformContext> txs) {
		final ParserRuleContext rc = ctx != null ? ctx : RuleContext.EMPTY;
		if (txs != null) txs.stream().forEach(tx -> rc.addChild(tx));
		return ctx;
	}

	private String applyTransforms(String term, List<TransformContext> tfs) {
		String raw = term + flatten(tfs);
		String result = this.parent.normalize(term);
		if (tfs != null) {
			if (this.parent.isParseable(result)) {
				System.out.println("applyTransforms.isParseable=true: '" + result + "'");
				return raw;
			}
			for (TransformContext tf : tfs) {
				result = applyTransform(term, tf);
			}
		}
		if (trace) System.out.println("applyTransforms: '"
				+ term + "' -> '" + result + "'");
		
		return result;
	}

	private String applyObjectTransforms(Object term, List<TransformContext> tfs) {

		String result = "";
		if (tfs != null) {
			for (TransformContext tf : tfs) {
				result = applyObjectTransform(term, tf);
			}
		}
		if (trace) System.out.println("applyOTransforms: '"
				+ term + "' -> '" + result + "'");
		return result;
	}

	private String applyObjectTransform(Object term, TransformContext tx) {
		//System.out.println("applyObjectTransform: "+term+" tx="+tx.getText());
		String txf = tx.getText(), raw = term + txf;
		txf = txf.replaceAll("^\\.", "");
		Field field = Util.getProperty(term, txf);
		try {
			if (field == null) throw new RuntimeException("[1] prop not found");
			field.setAccessible(true);
			Object result = field.get(term);
			if (!(result instanceof String)) {
				throw new RuntimeException("[2] prop not string");
			}
			return (String) result;
		} catch (Exception e) {
			System.err.println("[WARN] Unable to resolve '" + raw + "'");
		}
		return raw;
	}

	private String applyTransform(String s, TransformContext tx) {
		System.out.println("applyTransform1: " + s + " tx=" + tx.getText());
		Object result = null;
		String txf = tx.getText(), raw = s + txf;
		txf = txf.replaceAll("^\\.", "");

		// check for function
		if (txf.endsWith(Visitor.FUNCTION)) {
			txf = txf.substring(0, txf.length() - 2);

			// 1. Static method - no cases in Java
			Method meth = Util.getStatic(String.class, txf, String.class);
			if (meth != null) {  // String static
				result = Util.invokeStatic(meth, s);
			}

			// 2. Member (String) method
			if (result == null) {
				meth = Util.getMethod(s, txf);
				if (meth != null) { // String method
					result = Util.invoke(s, meth);
				}
			}

			// 3. Function in context
			if (result == null) {
				Map<String, Function<String, String>> funcs = contextFunctions();
				if (funcs.containsKey(txf)) {
					result = funcs.get(txf).apply(s.toString());
				}
			}
		}
		// check for property ?
		else {
			throw new RuntimeException("Check Properties here");
		}
		if (trace) System.out.println("applyTransform: '"
				+ s + "' -> '" + result + "'");

		return result != null ? result.toString() : raw;
	}

	//////////////////////////////Other/////////////////////////////////////

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

	public String visitChildren(RuleNode node) {
		String result = "";
		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			String visit = this.visit(child);
			result += visit != null ? visit : "";
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, Function<String, String>> contextFunctions() {
		Map<String, Function<String, String>> funcs = new HashMap<String, Function<String, String>>();
		if (context != null) {
			for (Map.Entry<String, Object> entry : this.context.entrySet()) {
				Object value = entry.getValue();
				if (value instanceof Function) {
					funcs.put(entry.getKey(), (Function<String, String>) value);
				}
			}
		}
		return funcs;
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
		Map<String, Object> opts = opts();
		RiTa.evaluate("[$b=(a | a)].toUpperCase()",
				opts, opts("singlePass", true, "trace", true));
		//System.out.println(opts.get("b"));
	}
}
