package rita;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rita.grammar.RiScriptBaseVisitor;
import rita.grammar.RiScriptParser.ScriptContext;

public class Visitor extends RiScriptBaseVisitor<String> {

	protected RiScript parent;
	protected List<String> pendingSymbols;
	protected Map<String, Object> context;
	protected boolean trace;

	public Visitor(RiScript parent, Map<String, Object> context) {
		this(parent, context, null);
	}

	public Visitor(RiScript riScript, Map<String, Object> context2, Map<String, Object> opts) {
		super();
		this.trace = Util.boolOpt("trace", opts);
		this.parent = parent;
		this.context = context;
		this.pendingSymbols = new ArrayList<String>();
	}

	public String start(ScriptContext script) {

		return this.visitScript(script);
	}
}
