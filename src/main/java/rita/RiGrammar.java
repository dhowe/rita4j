package rita;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.gson.*;

public class RiGrammar {

	public static String DEFAULT_RULE_NAME = "start";
	private static Pattern SYM_RE = Pattern.compile("^\\$[^$]");

	public static RiGrammar fromJSON(String json) {
		return fromJSON(json, null);
	}

	public static RiGrammar fromJSON(String json, Map<String, Object> context) {
		RiGrammar rg = new RiGrammar((Map<String, Object>) null, context);
		rg.parseJSON(json);
		return rg;
	}

	/////////////////////////////////////////////////////////////////////////////

	public Map<String, Object> rules = new HashMap<String, Object>();
	protected Map<String, Object> context;
	protected RiScript compiler;

	public RiGrammar() {
		this((Map<String, Object>) null);
	}

	public RiGrammar(String json) {
		this(json, null);
	}

	public RiGrammar(String json, Map<String, Object> context) {
		this((Map<String, Object>) null, context);
		parseJSON(json);
	}

	public RiGrammar(Map<String, Object> rules) {
		this(rules, null);
	}

	public RiGrammar(Map<String, Object> rules, Map<String, Object> context) {
		this.context = context;
		this.compiler = new RiScript();
		if (rules != null) this.rules = rules;
	}

	public String expand() {
		return expand(DEFAULT_RULE_NAME);
	}

	public String expand(String rule) {
		return expand(rule, null);
	}

	public String expand(Map<String, Object> opts) { // no context allowed here
		return expand(DEFAULT_RULE_NAME, opts);
	}

	public String expand(String rule, Map<String, Object> opts) { // no context allowed here

		Map<String, Object> ctx = Util.deepMerge(this.getContext(), this.rules);
		//if (opts != null) ctx = Util.deepMerge(ctx, opts);
		//if (rule.startsWith("$")) rule = rule.substring(1);
		rule = validateRuleName(rule);

		if (!ctx.containsKey(rule)) {
			if (!rule.startsWith(RiTa.DYN)) throw new RiTaException(
					"Bad rule (post-validation): " + rule);
			rule = rule.substring(2); // check for non-dynamic version
			if (!ctx.containsKey(rule)) throw new RiTaException(
					"Rule " + rule + " not found");
		}

		// a bit strange here as opts entries are included in ctx
		return this.compiler.evaluate((String) ctx.get(rule), ctx, opts);
	}

	private String validateRuleName(String name) {
		if (name == null || name.length() < 1) {
			throw new RiTaException("expected [string] name");
		}
		if (name.startsWith(RiTa.DYN)) {
			name = name.substring(2);
			throw new RiTaException("Grammar rules are dynamic by default;"
					+ " if you need a non-dynamic rule, use \'$"
					+ name + "', otherwise just use '" + name + "'.");
		}
		if (RE.test(SYM_RE, name)) {
			// override dynamic default, context -> 'barevar'
			name = name.substring(1);
		}
		else { // dynamic default, context -> '$$dynvar'
			if (!name.startsWith(RiTa.DYN)) name = RiTa.DYN + name;
		}
		return name;
	}

	public RiGrammar addRules(Map<String, Object> rules) {
		for (Entry<String, Object> entry : rules.entrySet()) {
			this.rules.put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public RiGrammar addRule(String name, String[] rule) {
		return this.addRule(name, joinChoice(rule));
	}

	public RiGrammar addRule(String name, String rule) {
		String rname = validateRuleName(name);
		if (rule == null) throw new RiTaException("<undefined> rule");

		if (name.startsWith(RiTa.SYM)) name = name.substring(1);
		// TODO: compile (pattern: if matches ( ... | ... )
		if (RE.test("\\|", rule) && !RE.test("^\\([^()]*\\)$", rule)) {
			rule = '(' + rule + ')';
		}
		this.rules.put(rname, rule);
		return this;
	}

	public String toJSON() {
		return toJSON(false);
	}

	public String toJSON(boolean pretty) {
		Map<String, Object> nrules = new HashMap<String, Object>();
		for (Object o : this.rules.keySet()) {
			String name = (String) o;
			Object rule = this.rules.get(name);
			if (!name.startsWith(RiTa.DYN)) name = RiTa.SYM + name;
			nrules.put(name, rule);
		}
		return pretty ? new GsonBuilder().setPrettyPrinting()
				.create().toJson(nrules, Map.class)
				: new Gson().toJson(nrules, Map.class);
	}

	@Override
	public String toString() {
		return this.toJSON(true);
	}

	public String toString(String lb) {
		String raw = this.toJSON(true);
		if (lb == null) {
			return raw;
		}
		else {
			return raw.replaceAll("\n", lb);
		}
	}

	public String toStringOld() {
		StringBuffer sb = new StringBuffer();
		rules.forEach((k, v) -> sb.append("\"" + k + "\": \"" + v + "\","));
		return sb.toString();
	}

	public RiGrammar removeRule(String name) {
		if (name != null && name.length() > 0) {
			name = validateRuleName(name);
			this.rules.remove(name);
		}
		return this;
	}

	public RiGrammar addTransform(String name, Function<String, String> f) {
		RiTa.addTransform(name, f);
		return this;
	}

	public Map<String, Function<String, String>> getTransforms() {
		return RiScript.transforms;
	}

	public boolean equals(Object o) {
		return (o != null && o instanceof RiGrammar)
				&& this.rules.equals(((RiGrammar) o).rules);
	}

	/////////////////////////// helpers ////////////////////////////

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void addRule(String key, Object val) {
		if (val instanceof String) {
			addRule(key, (String) val);
		}
		else if (val instanceof String[]) {
			addRule(key, (String[]) val);
		}
		else if (val instanceof List) {
			addRule(key, ((List) val).toArray(new String[0]));
		}
		else {
			throw new RiTaException("Invalid rule: "
					+ val + " type=" + val.getClass().getName());
		}
	}

	private String joinChoice(String[] opts) {
		String res = "(";
		for (int i = 0; i < opts.length; i++) {
			res += opts[i].contains(" ") ? '(' + opts[i] + ')' : opts[i];
			if (i < opts.length - 1) res += " | ";
		}
		return res + ")";
	}

	protected void parseJSON(String json) {
		if (json != null) {
			try {
				@SuppressWarnings("rawtypes")
				Map map = new Gson().fromJson(json, Map.class);
				for (Object o : map.keySet()) {
					String r = (String) o;
					this.addRule(r.startsWith(RiTa.DYN) ? r.substring(2) : r, map.get(o));
				}
			} catch (JsonSyntaxException e) {
				throw new RiTaException("Grammar appears to be invalid JSON"
						+ ", please check it at http://jsonlint.com/\n" + json, e);
			}
		}
	}

	/*private static Map<String, Object> parseJson(String rules) {
		Map<String, Object> result = null;
		if (rules != null) {
			result = new HashMap<String, Object>();
			try {
				@SuppressWarnings("rawtypes")
				Map map = new Gson().fromJson(rules, Map.class);
				for (Object o : map.keySet()) {
					String name = (String) o;
					if (name.startsWith("$")) name = name.substring(1);
					
				}
			} catch (Exception e) {
				throw new RiTaException("Grammar appears to be invalid JSON"
						+ ", please check it at http://jsonlint.com/\n" + rules);
			}
		}
		return result;
	}*/

	public static void main(String[] args) {
		System.out.println(new RiGrammar(RiTa.opts("start", "(a | b | c)")));
		System.out.println(RiGrammar.fromJSON("{\"start\": \"(a | b | c)\"}"));
	}

	public Map<String, Object> getContext() {
		return context;
	}

}
