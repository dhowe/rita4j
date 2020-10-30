package rita;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;

import rita.antlr.RiScriptBaseVisitor;
import rita.antlr.RiScriptParser.*;

// JS: this is an inner class in Visitor

public class ChoiceState {

	static final String SIMPLE = "", RSEQUENCE = "rseq", SEQUENCE = "seq", NOREPEAT = "norep";
	static final String[] TYPES = { RSEQUENCE, SEQUENCE, NOREPEAT };

	int id;
	String type;
	List<ParserRuleContext> options;

	private int cursor = 0;
	private ParserRuleContext last;

	public ChoiceState(Visitor parent, ChoiceContext ctx) {
		this(parent, ctx, SIMPLE);
	}

	public ChoiceState(Visitor parent, ChoiceContext ctx, String type) {

		this.type = type;
		this.id = parent.indexer;
		this.options = new ArrayList<ParserRuleContext>();

		List<WexprContext> wexprs = ctx.wexpr();
		for (int i = 0; i < wexprs.size(); i++) {
			WexprContext w = wexprs.get(i);
			ParserRuleContext expr = w.expr();
			WeightContext wctx = w.weight(); // handle weight
			int weight = wctx != null ? Integer.parseInt(wctx.INT().toString()) : 1;
			if (expr == null) expr = ParserRuleContext.EMPTY;
			for (int j = 0; j < weight; j++)
				options.add(expr);
		}

		this.handleSequence(ctx);

		if (type.equals(RSEQUENCE)) {
			this.options = RandGen.randomOrdering(this.options);
		}
	}

	private void handleSequence(ChoiceContext ctx) { // TODO: use contains instead
		List<TransformContext> txs = ctx.transform();
		if (txs.size() > 0) {
			String[] tfs = txs.get(0).getText().replaceAll("^\\.", "").split("\\.");
			//console.log(tfs);
			for (String tf : tfs) {
				for (String t : TYPES) {
					if (tf.equals(t + Visitor.FUNCTION)) {
						this.type = t;
						return;
					}
				}
			}
		}
	}

	public ParserRuleContext select() {
		if (options.size() == 0) return null;
		if (options.size() == 1) return options.get(0);
		if (type.equals(ChoiceState.SEQUENCE)) return selectSequence();
		if (type.equals(ChoiceState.NOREPEAT)) return selectNoRepeat();
		if (type.equals(ChoiceState.RSEQUENCE)) return selectRandSequence();
		return RandGen.randomItem(this.options);//randomElement(); // SIMPLE
	}

	protected ParserRuleContext selectNoRepeat() {
		ParserRuleContext cand;
		do {
			cand = RandGen.randomItem(this.options);
		} while (cand == this.last);

		return (this.last = cand);
	}

	protected ParserRuleContext selectNoRepeatX() {
		ParserRuleContext cand = null;
		if (last != null) {
			// need to test for equality here
			while (last.equals(cand)) {
				cand = RandGen.randomItem(this.options);
			}
		}
		else {
			cand = RandGen.randomItem(this.options);
		}
		return (this.last = cand);

	}

	protected ParserRuleContext selectSequence() {
		this.last = options.get(cursor++ % options.size());
		return this.last;
	}

	protected ParserRuleContext selectRandSequence() {
		while (cursor == options.size()) {
			Collections.shuffle(options);
			ParserRuleContext first = options.get(0);
			// need to test for equality here
			if (first != last) cursor = 0;
		}
		return selectSequence();
	}

}
