package rita;

import java.util.Map;

import com.google.gson.Gson;

public class Grammar {
	protected Map<String, Object> context, rules;
	protected RiScript compiler;

	public Grammar(String rules) {
		this(rules, null);
	}

	public Grammar(String rules, Map<String, Object> context) {
		this(parseJson(rules), context);
	}

	public Grammar(Map<String, Object> rules) {
		this(rules, null);
	}

	public Grammar(Map<String, Object> rules, Map<String, Object> context) {
		this.rules = rules;
		this.context = context;
		this.compiler = new RiScript();
	}

	public Grammar setRules(Map<String, Object> rules) {
		this.rules = rules;
		return this;
	}

	public Grammar setRules(String json) {
		this.rules = parseJson(json);
		return this;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> parseJson(String rules) {
		Gson gson = new Gson();
		Map<String, Object> rmap;
		try {
			return gson.fromJson(rules, Map.class);
		} catch (Exception e) {
			throw new RiTaException("Grammar appears to be invalid JSON"
					+ ", please check it at http://jsonlint.com/\n" + rules);
		}
	}

	public Grammar addRule(String name, String[] rule) {
		return this.addRule(name, joinChoice(rule));
	}

	public Grammar addRule(String name, String rule) {
		if (name.startsWith("$")) name = name.substring(1);
		if (RE.test("\\|", rule) && !RE.test("^\\(.*\\)$", rule)) {
			rule = '(' + rule + ')';
		}
		this.rules.put(name, rule);
		return this;
	}

	private String joinChoice(String[] opts) {
		String res = "(";
		for (int i = 0; i < opts.length; i++) {
			res += opts[i].contains(" ") ? '(' + opts[i] + ')' : opts[i];
			if (i < opts.length - 1) res += " | ";
		}
		return res + ")";
	}

	public String toString() {
		String s = "";
		for (Map.Entry<String, Object> kv : rules.entrySet()) {
			s += kv.getKey() + ": " + kv.getValue() + "\n";
		}
		return s;
	}

	public static void main(String[] args) {
		System.out.println(new Grammar(Util.opts("start", "(a | b | c)")));
		System.out.println(new Grammar("{\"start\": \"(a | b | c)\"}"));
	}

}
