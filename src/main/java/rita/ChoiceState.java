package rita;

import java.util.*;

import rita.antlr.RiScriptParser.*;

public class ChoiceState {

	public static final int SIMPLE = 1;
	public static final int RSEQUENCE = 2;
	public static final int SEQUENCE = 4;
	public static final int NOREPEAT = 0;

	private List<ExprContext> options;
	private int type, index = 0;
	private ExprContext last;

	public ChoiceState(Visitor visitor, ChoiceContext ctx) {
		this(visitor, ctx, ChoiceState.SIMPLE);
	}

	public ChoiceState(Visitor parent, ChoiceContext ctx, int type) {

		this.type = type;
		this.options = new ArrayList<ExprContext>();

		List<WexprContext> wexprs = ctx.wexpr();
		for (int i = 0; i < wexprs.size(); i++) {
			WexprContext w = wexprs.get(i);
			WeightContext wctx = w.weight();
			int weight = wctx != null ? Integer.parseInt(wctx.INT().toString()) : 1;
			ExprContext expr = w.expr();
			if (expr == null) expr = parent.emptyExpr(ctx);
			for (int j = 0; j < weight; j++)
				options.add(expr);
		}
		if (parent.trace) System.out.println("visitChoice: " + ctx.getText() + " :: " + options.size() + " opts");
	}

	public ExprContext select() {
		if (options.size() == 0) return null;
		if (options.size() == 1) return options.get(0);
		if (type == ChoiceState.SEQUENCE) return selectSequence();
		if (type == ChoiceState.NOREPEAT) return selectNoRepeat();
		if (type == ChoiceState.RSEQUENCE) return selectRandSequence();
		return randomElement(); // SIMPLE
	}

	protected ExprContext randomElement() {
		return options.get((int) (Math.random() * options.size()));
	}
	
	protected ExprContext selectNoRepeat() {
		ExprContext cand = null;
		while (cand == last) cand = randomElement(); 
		return (this.last = cand);
	}

	protected ExprContext selectSequence() {
		this.last = options.get(index++ % options.size());
		return this.last;
	}

	protected ExprContext selectRandSequence() {
		while (index == options.size()) {
			Collections.shuffle(options);
			ExprContext first = options.get(0);
			if (options.size() < 2 || first != last) {
				index = 0;
			}
		}
		return selectSequence();
	}

}
