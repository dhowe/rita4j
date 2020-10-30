package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static rita.Util.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import rita.*;

public class GrammarTests {

	String sentences1 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"($verb | $verb $noun_phrase)\",\"$determiner\": \"(a | the)\",\"$noun\": \"(woman | man)\",\"$verb\": \"shoots\"}";
	String sentences2 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$determiner\": [\"a\", \"the\"],\"$verb_phrase\": [\"$verb $noun_phrase\", \"$verb\"],\"$noun\": [\"woman\", \"man\"],\"$verb\": \"shoots\"}";
	String sentences3 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"$verb | $verb $noun_phrase\",\"$determiner\": \"a | the\",\"$noun\": \"woman | man\",\"$verb\": \"shoots\"}";
	String[] grammars = { sentences1, sentences2, sentences3 };

	static Map<String, Object> TT = opts("trace", true);

	@Test
	public void callConstructor() {
		Grammar gr = new Grammar();
		assertTrue(gr instanceof Grammar);
	}

	@Test
	public void supportSeqTransform() {
		String[] seq = { "a", "b", "c", "d" };

		String rule = "(" + String.join("|", seq) + ").seq()";
		Grammar rg = new Grammar(opts("start", rule));
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
//			console.log(i+ ": "+ res);
			eq(res, seq[i]);
		}

		rule = "(" + String.join("|", seq) + ").seq().capitalize()";
		rg = new Grammar(opts("start", rule));
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
//			console.log(i+ ": "+ res);
			eq(res, seq[i].toUpperCase());
		}
	}

	@Test
	public void supportRSeqTransform() {
		String[] seq = { "a", "b", "c", "d" };
		String rule = "(" + String.join("|", seq) + ").rseq()";
		Grammar rg = new Grammar(opts("start", rule));
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			result.add(res);
		}
		containsAll(result, seq);
//if (1==1) return;
		result.clear();
		rule = "(" + String.join("|", seq) + ").rseq().capitalize()";
		rg = new Grammar(opts("start", rule));
		// console.log(rule);

		for (int i = 0; i < 4; i++) {
			String res = rg.expand(TT);
			// console.log(i, ':', res);
			result.add(res);
		}
		//console.log(result);

		String[] upperSeq = { "A", "B", "C", "D" };
		containsAll(result, upperSeq);
	}

	@Test
	public void allowRulesStartingWithNum() {
		Grammar rg = new Grammar();
		rg.addRule("start", "$1line talks too much.");
		rg.addRule("$1line", "Dave | Jill | Pete");

		String rs = rg.expand(opts("trace", false));
		assertTrue(rs.equals("Dave talks too much.") || rs.equals("Jill talks too much.") || rs.equals("Pete talks too much."));

		rg = new Grammar();
		rg.addRule("1line", "Dave | Jill | Pete");
		rs = rg.expand("1line", opts("trace", false));
		assertTrue(rs.equals("Dave") || rs.equals("Jill") || rs.equals("Pete"));
	}

	@Test
	public void resolveInlines() {
		String[] expected = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };
		Grammar rg;
		String rules, rs;

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"Dave | Jill | Pete\"}";
		rg = Grammar.fromJSON(rules);
		rs = rg.expand();
		assertTrue(Arrays.asList(expected).contains(rs));

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave\",\"Jill\": \"Jill\",\"Pete\": \"Pete\"}";
		rg = Grammar.fromJSON(rules);
		rs = rg.expand();
		assertTrue(Arrays.asList(expected).contains(rs));

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave | Jill | Pete\",\"Jill\": \"Dave | Jill | Pete\",\"Pete\": \"Dave | Jill | Pete\"}";
		rg = Grammar.fromJSON(rules);
		rs = rg.expand();
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	@Test
	public void setRules() {
		Grammar rg = new Grammar();
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") == null);
		assertTrue(rg.rules.get("noun_phrase") == null);

		for (String g : grammars) {
			rg = Grammar.fromJSON(g);
			assertTrue(rg.rules != null);
			assertTrue(rg.rules.get("start") != null);
			assertTrue(rg.rules.get("noun_phrase") != null);
		}

	}

	@Test
	public void callAddRule() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		assertTrue(rg.rules.get("start") != null);
		assertTrue(rg.rules.get("noun_phrase") == null);
	}

	@Test
	public void callAddRules() {
		Grammar rg = new Grammar();
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") == null);
		assertTrue(rg.rules.get("noun_phrase") == null);
		Map<String, Object> sentenceMap1 = new HashMap<String, Object>();
		sentenceMap1.put("start", "$noun_phrase $verb_phrase.");
		sentenceMap1.put("noun_phrase", "(Bule cars | Red roses)");
		sentenceMap1.put("verb_phrase", "exist in this world");

		rg.addRules(sentenceMap1);
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") != null);
		assertTrue(rg.rules.get("noun_phrase") != null);
		String str = rg.expand();
		assertTrue(str.equals("Bule cars exist in this world.") || str.equals("Red roses exist in this world."));

	}

	@Test
	public void callRemoveRule() {
		for (String g : grammars) {
			Grammar rg = Grammar.fromJSON(g);

			def(rg.rules.get("start"));
			def(rg.rules.get("noun_phrase"));

			rg.removeRule("$noun_phrase");
			def(rg.rules.get("noun_phrase") == null);

			rg.removeRule("$start");
			def(rg.rules.get("start") == null);

			rg.removeRule("");
			rg.removeRule("bad-name");
			rg.removeRule(null);
		}
	}

	@Test
	public void throwOnBadGrammarNames() {
		Grammar rg = new Grammar();
		//		assertThrows(RiTaException.class, () -> rg.expandFrom("wrongName"));
		assertThrows(RiTaException.class, () -> rg.expand());
	}

	@Test
	public void callExpandFrom() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "($bird | $mammal)");
		rg.addRule("$bird", "(hawk | crow)");
		rg.addRule("$mammal", "dog");
		eq(rg.expand("$mammal"), "dog");

		for (int i = 0; i < 30; i++) {
			String res = rg.expand("$bird");
			assertTrue(res.equals("hawk") || res.equals("crow"));
		}

	}

	@Test
	public void callToString() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "pet");
		String str = rg.toString();
		eq(str, "{\n  \"start\": \"pet\"\n}");
		rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		str = rg.toString();
		eq(str, "{\n  \"start\": \"$pet\",\n  \"pet\": \"dog\"\n}");
		rg = new Grammar();
		rg.addRule("$start", "$pet | $iphone");
		rg.addRule("$iphone", "iphoneSE | iphone12");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString();
		eq(str, "{\n  \"start\": \"($pet | $iphone)\",\n  \"iphone\": \"(iphoneSE | iphone12)\",\n  \"pet\": \"(dog | cat)\"\n}");
	}

	@Test
	public void callToStringWithArg() {
		String lb = "<br/>";
		Grammar rg = new Grammar();
		rg.addRule("$start", "pet");
		String str = rg.toString(lb);
		eq(str, "{<br/>  \"start\": \"pet\"<br/>}");
		rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"start\": \"$pet\",<br/>  \"pet\": \"dog\"<br/>}");
		rg = new Grammar();
		rg.addRule("$start", "$pet | $iphone");
		rg.addRule("$pet", "dog | cat");
		rg.addRule("$iphone", "iphoneSE | iphone12");
		str = rg.toString(lb);
		eq(str,
				"{<br/>  \"start\": \"($pet | $iphone)\",<br/>  \"iphone\": \"(iphoneSE | iphone12)\",<br/>  \"pet\": \"(dog | cat)\"<br/>}");
	}

	@Test
	public void callExpand() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "pet");
		eq(rg.expand(), "pet");
		rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		eq(rg.expand(), "dog");

		rg = new Grammar();
		rg.addRule("start", "ani");
		eq(rg.expand(), "ani");
		rg = new Grammar();
		rg.addRule("start", "$ani");
		rg.addRule("ani", "cat");
		eq(rg.expand(), "cat");
	}

	@Test
	public void callExpandWeights() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$rule1");
		rg.addRule("$rule1", "cat | dog | boy");
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		for (int i = 0; i < 30; i++) {
			String res = rg.expand();
			assertTrue(res.equals("cat") || res.equals("dog") || res.equals("boy"));
			if (res.equals("cat")) found1 = true;
			if (res.equals("dog")) found2 = true;
			if (res.equals("boy")) found3 = true;
		}
		assertTrue(found1 && found2 && found3); // found all
	}

	@Test
	public void callExpandFromWeights() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "$bird [9] | $mammal");
		rg.addRule("$bird", "hawk");
		rg.addRule("$mammal", "dog");

		eq(rg.expand("$mammal"), "dog");

		int hawks = 0;
		int dogs = 0;
		for (int i = 0; i < 30; i++) {
			String res = rg.expand("$pet");
			assertTrue(res.equals("hawk") || res.equals("dog"), "got " + res);
			if (res.equals("dog")) dogs++;
			if (res.equals("hawk")) hawks++;
		}
		assertTrue((hawks > dogs * 2), "got h=" + hawks + ", " + dogs);

	}

	@Test
	public void handleTransform() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet.toUpperCase()");
		rg.addRule("$pet", "dog");
		eq(rg.expand(), "DOG");

		rg = new Grammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(dog).toUpperCase()");
		eq(rg.expand(), "DOG");

		rg = new Grammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(ant).articlize()");
		eq(rg.expand(), "an ant");

		rg = new Grammar();
		rg.addRule("$start", "($pet | $animal).articlize().ucf()");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "ant");
		eq(rg.expand(), "An ant");
	}

	@Test
	public void callPluralizePhrasesInTransform() {
		Function<String, String> pluralise = (s) -> {
			s = s.trim();
			if (s.contains(" ")) {
				String[] words = RiTa.tokenize(s);
				int lastIdx = words.length - 1;
				String last = words[lastIdx];
				words[lastIdx] = RiTa.pluralize(last);
				return RiTa.untokenize(words);
			}
			return RiTa.pluralize(s);
		};
		Map<String, Object> ctx = opts("pluralise", pluralise);
		String jsonString = "{\"start\": \"($state feeling).pluralize()\",\"state\": \"bad | bad\"}";
		Grammar rg = Grammar.fromJSON(jsonString, ctx);

		String res = rg.expand();
		eq(res, "bad feelings");

	}

	@Test
	public void alllowContextInExpand() {
		Supplier<String> randomPosition = () -> {
			return "job type";
		};
		Map<String, Object> context = opts("randomPosition", randomPosition);
		Grammar rg = new Grammar(opts("start", "My .randomPosition()."));
		eq("My job type.", rg.expand(context));

		rg = new Grammar(opts("stat", "My .randomPosition()."));
		eq("My job type.", rg.expand("stat", context));

	}

	@Test
	public void handleCustomTransform() {
		String rules = "{ start: \"My .randomPosition().\"}";
		Supplier<String> randomPosition = () -> {
			return "jobArea jobType";
		};
		Map<String, Object> context = opts("randomPosition", randomPosition);
		Grammar rg = Grammar.fromJSON(rules, context);
		eq(rg.expand(TT), "My jobArea jobType.");
	}

	@Test
	public void handleSymbolTransfrom() {
		Grammar rg = Grammar.fromJSON("{\"start\": \"$tmpl\",\"tmpl\": \"$jrSr.capitalize()\",\"jrSr\": \"(junior|junior)\"}");
		eq(rg.expand(), "Junior");

		Grammar rg1 = Grammar.fromJSON("{\"start\": \"$r.capitalize()\",\"r\": \"(a | a)\"}");
		eq(rg1.expand(), "A");

		Grammar rg2 = Grammar.fromJSON("{\"start\": \"$r.pluralize()\",\"r\": \"(mouse | mouse)\"}");
		eq(rg2.expand(), "mice");
	}

	@Test
	public void handleSpecialCharacters() {
		String s = "{ \"$start\": \"hello &#124; name\" }";
		Grammar rg = Grammar.fromJSON(s);
		String res = rg.expand();
		// console.log(res);
		eq(res, "hello | name");

		s = "{ \"$start\": \"hello: name\" }";
		rg = Grammar.fromJSON(s);
		res = rg.expand();
		eq(res, "hello: name");

		s = "{ \"$start\": \"&#8220;hello!&#8221;\" }";
		rg = Grammar.fromJSON(s);

		s = "{ \"$start\": \"&lt;start&gt;\" }";
		rg = Grammar.fromJSON(s);
		res = rg.expand();
		// console.log(res);
		eq(res, "<start>");

		s = "{ \"$start\": \"I don&#96;t want it.\" }";
		rg = Grammar.fromJSON(s);
		res = rg.expand();
		// console.log(res);
		eq(res, "I don`t want it.");

		s = "{ \"$start\": \"&#39;I really don&#39;t&#39;\" }";
		rg = Grammar.fromJSON(s);
		res = rg.expand();
		eq(res, "'I really don't'");

		s = "{ \"$start\": \"hello | name\" }";
		rg = Grammar.fromJSON(s);
		for (int i = 0; i < 10; i++) {
			res = rg.expand();
			//System.out.println(i+") "+res);
			assertTrue(res.equals("hello") || res.equals("name"));
		}
	}

	@Test
	public void callJSONMethods() {

		String s = "{\"$start\":\"$pet $iphone\",\"pet\":\"dog | cat\",\"iphone\":\"iphoneSE | iphone12\"}";
		Grammar rg = Grammar.fromJSON(s);
		Grammar rg2 = Grammar.fromJSON(rg.toJSON());
		eq(rg.toString(), rg2.toString());
		assertTrue(rg.equals(rg2));

		for (String g : grammars) {
			rg = Grammar.fromJSON(g);
			rg2 = Grammar.fromJSON(rg.toJSON());
			eq(rg.toString(), rg2.toString());
			assertTrue(rg.equals(rg2));
		}
	}

	// return true if object is not null
	private void def(Object o) {
		assertTrue(o != null);
	}

	// return true if list contains every item in array
	private void containsAll(ArrayList<String> list, String[] array) {
		int found = 0;
		for (String item : array) {
			if (list.contains(item)) found++;
		}
		assertTrue(found == array.length);
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}

}
