package rita;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;

import rita.antlr.RiScriptBaseVisitor;
import rita.antlr.RiScriptParser.*;

// TODO: update from js

public class ChoiceState {

	public static final int SIMPLE = 1;
	public static final int RSEQUENCE = 2;
	public static final int SEQUENCE = 4;
	public static final int NOREPEAT = 8;

	private List<ParserRuleContext> options;
	private int type, index = 0;
	private ParserRuleContext last;

	public ChoiceState(RiScriptBaseVisitor<String> visitor, ChoiceContext ctx) {
		this(visitor, ctx, ChoiceState.SIMPLE);
	}

	public ChoiceState(RiScriptBaseVisitor<String> parent, ChoiceContext ctx, int type) {

		this.type = type;
		this.options = new ArrayList<ParserRuleContext>();

		List<WexprContext> wexprs = ctx.wexpr();
		for (int i = 0; i < wexprs.size(); i++) {
			WexprContext w = wexprs.get(i);
			ParserRuleContext expr = w.expr();
			WeightContext wctx = w.weight();
			int weight = wctx != null ? Integer.parseInt(wctx.INT().toString()) : 1;
			if (expr == null) expr = ParserRuleContext.EMPTY;
			for (int j = 0; j < weight; j++)
				options.add(expr);
		}
		if (parent instanceof Visitor) {
			Visitor v = (Visitor) parent;
			if (v.trace) System.out.println("visitChoice: " + ctx.getText()
					+ " ['" + v.flatten(options).replaceAll("\\|", "','")+"'] tfs=" + v.flatten(ctx.transform()));
		}

	}

	public ParserRuleContext select() {
		if (options.size() == 0) return null;
		if (options.size() == 1) return options.get(0);
		if (type == ChoiceState.SEQUENCE) return selectSequence();
		if (type == ChoiceState.NOREPEAT) return selectNoRepeat();
		if (type == ChoiceState.RSEQUENCE) return selectRandSequence();
		return randomElement(); // SIMPLE
	}

	protected ParserRuleContext randomElement() {
		return options.get((int) (Math.random() * options.size()));
	}

	protected ParserRuleContext selectNoRepeat() {
		ParserRuleContext cand = null;
		// need to test for equality here
		while (cand == last) cand = randomElement();
		return (this.last = cand);
	}

	protected ParserRuleContext selectSequence() {
		this.last = options.get(index++ % options.size());
		return this.last;
	}

	protected ParserRuleContext selectRandSequence() {
		while (index == options.size()) {
			Collections.shuffle(options);
			ParserRuleContext first = options.get(0);
			// need to test for equality here
			if (first != last) index = 0;
		}
		return selectSequence();
	}

}
