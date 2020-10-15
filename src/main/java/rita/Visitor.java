package rita;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

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
		ExprContext token = ctx.expr(); // duped in visitAssign
		String id = ctx.symbol().getText().replaceAll("^\\$", "");

		if (this.trace) System.out.println("visitInline: $"
				+ id + "=" + this.flatten(token) + " tfs="+flatten(txs));

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
		ChoiceState choice = new ChoiceState(this, ctx);
		//if (parent instanceof Visitor && ((Visitor) parent).trace) {
		if (trace) System.out.println("visitChoice: '" + ctx.getText()
					+ "' options=['" + flatten(choice.options).replaceAll("\\|", "','")
					+ "'] tfs=" + flatten(ctx.transform()));
		
		// make the selection
		ParserRuleContext tok = choice.select();
		if (trace) System.out.println("  select: '" + tok.getText() + "'");
		
		// now visit the token 
		String visited = tok.getText().equals("") ? "" : this.visit(tok);
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

		if (trace) System.out.println("visitSymbol: '"
				+ ident + "' tfs=" + flatten(txs));// + " -> " + result);

		// now try to resolve from context
		String resolved = fromContext(ident);
		
		// if it fails, give up / wait for next pass
		if (resolved == null) return result;
			
		// now apply the transforms
		String applied = applyTransforms(resolved, txs);
		result = applied != null ? applied : resolved + flatten(txs);
		

		if (trace) System.out.println("resolveSymbol: '"
				+ ident + " -> " + result);

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
			List<TransformContext> txs = childTransforms(ctx);
			System.out.println("visitExpr: '" + ctx.getText() 
				/*+"' flat='"+flatten(rcs) */+ "' tfs=" + flatten(txs));
			//printChildren(ctx);
		}
		return visitChildren(ctx);
	}

	public String visitChars(CharsContext ctx) {
		List<TransformContext> txs = siblingTransforms(ctx, true);
		if (trace) System.out.println("visitChars: '" + ctx.getText() + "' tfs=" + flatten(txs));
		String result = applyTransforms(ctx.getText(), txs);
		return result != null ? result : ctx.getText() + flatten(txs);
	}

	// TODO: [NEXT] need to handle fails in applyTransforms()
	// 			 should return input (x.tf() || x.tp ) if no transform succeeds
	//

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

	private String fromContext(String id) {
		String key = id.startsWith("$") ? id.substring(1) : id;
		Object def =  context != null ? context.get(key) : null;
		if (def != null) {

			if (isPrimitive(def)) return def.toString();

			if (!(def instanceof String)) throw new RuntimeException
				("Symbol returned non-String:  " + def); // TODO: objects?
		}
		return (String) def;
	}

	/*private Object fromContextOrDef(String id, Object def) {
		String key = id.startsWith("$") ? id.substring(1) : id;
		return context != null ? context.getOrDefault(key, def) : def;
	}*/

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

	// ORIGINAL:
	//
	// private ParserRuleContext passTransformsAsChildrenOf(ParserRuleContext prc,
	//			List<TransformContext> txs) {
	//		final ParserRuleContext rc = prc != null ? prc : RuleContext.EMPTY;
	//		if (txs != null) txs.stream().forEach(tx -> rc.addChild(tx));
	//		return prc;
	//	}

	private void passTransformsAsChildrenOf(ParserRuleContext prc,
			List<TransformContext> txs) {
		if (prc == null) throw new RuntimeException("Null ParserRuleContext");
		if (txs != null) txs.stream().forEach(tx -> prc.addChild(tx));
	}

	private String applyTransforms(String term, List<TransformContext> tfs) {
		if (term == null || tfs == null || tfs.size() < 1) return null;
		String result = this.parent.normalize(term);
		if (this.parent.isParseable(result)) { // save for later
			//System.out.println("applyTransforms.isParseable=true: '" + result + "'");
			return null;
		}
		for (TransformContext tf : tfs) {
			result = applyTransform(result, tf);
		}
		return result;
	}
	/*	else {
			List results = new ArrayList();
			for (TransformContext tf : tfs) {
				results.add(applyObjectTransform(target, tf, original));
			}
		}
		if (trace) System.out.println("applyTransforms: '"
				+ term + "' -> '" + result + "'");
		
		return result;
	}
	
	private applyObjectTransforms(Object term, List<TransformContext> tfs) {
	
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
	}*/

	// Attempts to apply transform, returns null on failure
	@SuppressWarnings("unchecked")
	private String applyTransform(String s, TransformContext tfc) {

		if (s == null || tfc == null) return null;
		String tx = tfc.getText().replaceAll("^\\.", "");

		if (trace) System.out.println("applyTransform: '" + s + "' tf=" + tx);

		Object result = null;

		// check for function
		if (tx.endsWith(Visitor.FUNCTION)) {
			tx = tx.substring(0, tx.length() - 2);

			// 1. Static method - no cases in Java
			Method meth = Util.getStatic(String.class, tx, String.class);
			if (meth != null) {  // String static
				result = Util.invokeStatic(meth, s);
			}

			// 2. Member (String) method
			if (result == null) {
				meth = Util.getMethod(s, tx);
				if (meth != null) { // String method
					result = Util.invoke(s, meth);
				}
			}

			// 3. Function in context
			if (result == null) {
				//Map<String, Function<String, String>> cf = contextFunctions();
				if (this.context != null && this.context.containsKey(tx)) {
					Object func = this.context.get(tx);
					if (func instanceof Function) {
						result = ((Function<String, String>) func).apply(s);
					}
				}
			}
		}
		// check for property ?
		else {
			throw new RuntimeException("Check Properties here");
		}
		
		
		if (trace) System.out.println("resolveTransform: '"
				+ s + "' -> '" + result + "'");

		return result != null ? result.toString() : null;
	}

	////////////////////////////// Other ///////////////////////////////////

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
      console.log("  child[" + i + "]: '"+ child.getText()+
        "' type=" + this.getRuleName(child));
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
	

	@SuppressWarnings({ "unchecked" })
	private Map<String, Function<String, String>> contextFunctions() { // why?
		Map<String, Function<String, String>> funcs = new HashMap<String, Function<String, String>>();
		if (context != null) {
			for (Map.Entry<String, Object> entry : this.context.entrySet()) {
				Object value = entry.getValue();
				if (value instanceof Function) {
					funcs.put(entry.getKey(), (Function<String, String>) value);
				}
			}
		}
		console.log(funcs.keySet().toArray());
		return funcs;
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
