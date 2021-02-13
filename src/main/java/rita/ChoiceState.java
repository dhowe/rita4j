package rita;

import java.util.*;

import org.antlr.v4.runtime.ParserRuleContext;

import rita.antlr.RiScriptParser.*;

// JS: this is an inner class in Visitor 

public class ChoiceState { //SYNC:

	//static final String SIMPLE = "", RSEQUENCE = "rseq";
	//static final String SEQUENCE = "seq", NOREPEAT = "nore";
	//static final String[] TYPES = { RSEQUENCE, SEQUENCE, NOREPEAT };
	static final String[] NOREPEAT = { "norepeat", "nr" };

	//private int index = 0, type = 0;
	List<ParserRuleContext> options;

	private ParserRuleContext last;

	public ChoiceState(Visitor parent, ChoiceContext ctx) {

		//this.type = 0;
		///this.index = 0;
		this.options = new ArrayList<ParserRuleContext>();

		List<WexprContext> wexprs = ctx.wexpr();
		for (int i = 0; i < wexprs.size(); i++) {
			WexprContext w = wexprs.get(i);
			ParserRuleContext expr = w.expr();
			WeightContext wctx = w.weight(); // handle weight
			int weight = wctx != null ? Integer.parseInt(wctx.INT().toString()) : 1;
			if (expr == null) expr = ParserRuleContext.EMPTY;
			for (int j = 0; j < weight; j++) {
				options.add(expr);
			}
		}
	}

	public ParserRuleContext select(String txStr) {
		if (options.size() == 0) throw new RiTaException("no options");
		if (options.size() == 1) return options.get(0);
		ParserRuleContext res;
    if (txStr.contains('.' + NOREPEAT[0]) || txStr.contains('.' + NOREPEAT[1])) {
      res = this.selectNoRepeat();
    }
    else {
      res = RiTa.random(this.options); // SIMPLE
    }
    return (this.last = res);
		//		if (type.equals(ChoiceState.SEQUENCE)) return selectSequence();
		//		if (type.equals(ChoiceState.NOREPEAT)) return selectNoRepeat();
		//		if (type.equals(ChoiceState.RSEQUENCE)) return selectRandSequence();
		//return RandGen.randomItem(this.options);//randomElement(); // SIMPLE
	}

	protected ParserRuleContext selectNoRepeat() {
		ParserRuleContext cand;
		do {
			cand = RandGen.randomItem(this.options);
		} while (cand == this.last);

		return (this.last = cand);
	}

	/*protected ParserRuleContext selectSequence() {

		return (this.last = options.get(cursor++ % options.size()));
	}

	protected ParserRuleContext selectRandSequence() {
		while (cursor == options.size()) {
			Collections.shuffle(options);
			ParserRuleContext first = options.get(0);
			// need to test for equality here
			if (first != last) cursor = 0;
		}
		return selectSequence();
	}*/

}
