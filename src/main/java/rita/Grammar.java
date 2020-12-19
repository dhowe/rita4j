package rita;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import com.google.gson.*;

public class Grammar {

	public static String DEFAULT_RULE_NAME = "start";

	public static Grammar fromJSON(String rules) {
		return fromJSON(rules, null);
	}

	public static Grammar fromJSON(String rules, Map<String, Object> context) {
		return new Grammar(rules, context);
	}

	/////////////////////////////////////////////////////////////////////////////

	public Map<String, Object> rules = new HashMap<String, Object>();
	protected Map<String, Object> context;
	protected RiScript compiler;

	public Grammar() {
		this((Map<String, Object>) null);
	}

	public Grammar(String json) {
		this(json, null);
	}

	public Grammar(String json, Map<String, Object> context) {
		this((Map<String, Object>) null, context);
		System.out.println(json);
		System.out.println();
		System.out.println(json.replaceAll("\"", "\\\""));
		parseJSON(json);
	}

	public Grammar(Map<String, Object> rules) {
		this(rules, null);
	}

	public Grammar(Map<String, Object> rules, Map<String, Object> context) {
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

	public String expand(Map<String, Object> opts) {
		return expand(DEFAULT_RULE_NAME, opts);
	}

	public String expand(String rule, Map<String, Object> opts) {

		Map<String, Object> ctx = Util.deepMerge(this.context, this.rules);
		if (opts != null) ctx = Util.deepMerge(ctx, opts);
		if (rule.startsWith("$")) rule = rule.substring(1);

		Object o = ctx.get(rule);
		if (o == null) throw new RiTaException("Rule " + rule + " not found");

		// a bit strange here as opts entries are included in ctx
		return this.compiler.evaluate((String) o, ctx, opts);
	}

	public Grammar addRules(Map<String, Object> rules) {
		for (Entry<String, Object> entry : rules.entrySet()) {
			this.rules.put(entry.getKey(), entry.getValue());
		}
		return this;
	}

	public Grammar addRule(String name, String[] rule) {
		return this.addRule(name, joinChoice(rule));
	}

	public Grammar addRule(String name, String rule) {
		if (name.startsWith("$")) name = name.substring(1);
		if (RE.test("\\|", rule) && !RE.test("^\\([^\\(\\)]*\\)$", rule)) {
			rule = '(' + rule + ')';
		}
		this.rules.put(name, rule);
		return this;
	}

	public String toJSON() {
		return toJSON(false);
	}

	public String toJSON(boolean pretty) {
		return pretty ? new GsonBuilder().setPrettyPrinting()
				.create().toJson(this.rules, Map.class)
				: new Gson().toJson(this.rules, Map.class);
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

	public Grammar removeRule(String name) {
		if (name != null) {
			if (name.startsWith("$")) name = name.substring(1);
			this.rules.remove(name);
		}
		return this;
	}

	public Grammar addTransform(String name, Function<String, String> f) {
		RiTa.addTransform(name, f);
		return this;
	}

	public Map<String, Function<String, String>> getTransforms() {
		return RiScript.transforms;
	}

	public boolean equals(Object o) {
		return (o != null && o instanceof Grammar)
				&& this.rules.equals(((Grammar) o).rules);
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
					this.addRule((String) o, map.get(o));
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
		System.out.println(new Grammar(Util.opts("start", "(a | b | c)")));
		System.out.println(Grammar.fromJSON("{\"start\": \"(a | b | c)\"}"));
	}

}
