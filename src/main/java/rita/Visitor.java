package rita;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.math3.analysis.function.Exp;
import org.checkerframework.framework.qual.AnnotatedFor;

import rita.grammar.RiScriptBaseVisitor;
import rita.grammar.RiScriptParser;
import rita.grammar.RiScriptParser.ChoiceContext;
import rita.grammar.RiScriptParser.ExprContext;
import rita.grammar.RiScriptParser.ScriptContext;
import rita.grammar.RiScriptParser.WeightContext;
import rita.grammar.RiScriptParser.WexprContext;

public class Visitor extends RiScriptBaseVisitor<String> {

	protected RiScript parent;
	protected List<String> pendingSymbols;
	protected Map<String, Object> context;
	protected boolean trace;

	public Visitor(RiScript parent, Map<String, Object> context) {
		this(parent, context, null);
	}

	public Visitor(RiScript parent, Map<String, Object> context, Map<String, Object> opts) {
		super();
		this.trace = Util.boolOpt("trace", opts);
		this.parent = parent;
		this.context = context;
		this.pendingSymbols = new ArrayList<String>();
	}

	public String visitChildren(RuleNode node) {
		// public String visitChildren(ParserRuleContext ctx) {
		// if (ctx.children.size() < 1) return "";
		//System.out.println("visitChildren");
		String result = "";
		// RuleContext ruleContext = node.getRuleContext();

		for (int i = 0; i < node.getChildCount(); i++) {
			ParseTree child = node.getChild(i);
			String visit = this.visit(child);
			result += visit != null ? visit : "";
		}
		//System.out.println("visitChildren(" + node.toStringTree() + " -> " + result);// .toString(parent.parser)+ "): '");
//      + ctx.getText() + '"', ctx.transforms || '[]', '[' + ctx.children.reduce(
//        (acc, c) => acc += c.constructor.name + ',', '').replace(/,$/, ']'));
//    if (!ctx.children) return "";
//
//    this.trace && console.log('visitChildren(' + ctx.constructor.name + '): "'
//      + ctx.getText() + '"', ctx.transforms || '[]', '[' + ctx.children.reduce(
//        (acc, c) => acc += c.constructor.name + ',', '').replace(/,$/, ']'));
//
//    // visit each child, pass transforms, and merge their output
//    return ctx.children.reduce((acc, child) => {
//      child.transforms = ctx.transforms;
//      return acc + this.visit(child);
//    }, '');
		return result;
	}

	public String start(ScriptContext ctx) {
		return visitScript(ctx);
	}

	public String visitChars(RiScriptParser.CharsContext ctx) {
		
		System.out.println("visitChars: '" + ctx.getText() + "'");
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

	private <T> T randomElement(List<T> options) {
		if (options.size() == 0) return null;
		return options.get((int) (Math.random() * options.size()));
	}

	private ExprContext emptyExpr(ParserRuleContext parent) {
		return new ExprContext(parent, -1);
	}
}
