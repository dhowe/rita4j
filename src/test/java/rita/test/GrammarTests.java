package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static rita.RiTa.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.sampled.Port;

import com.ibm.icu.impl.number.AffixPatternProvider.Flags;

import org.junit.jupiter.api.Test;

import rita.*;

public class GrammarTests {

	static String sentences1 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"($verb | $verb $noun_phrase)\",\"$determiner\": \"(a | the)\",\"$noun\": \"(woman | man)\",\"$verb\": \"shoots\"}";
	static String sentences2 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$determiner\": [\"a\", \"the\"],\"$verb_phrase\": [\"$verb $noun_phrase\", \"$verb\"],\"$noun\": [\"woman\", \"man\"],\"$verb\": \"shoots\"}";
	static String sentences3 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"$verb | $verb $noun_phrase\",\"$determiner\": \"a | the\",\"$noun\": \"woman | man\",\"$verb\": \"shoots\"}";
	public static String[] grammars = { sentences1, sentences2, sentences3 };

	static Map<String, Object> ST = opts("silent", true);
	static Map<String, Object> TP = opts("trace", true);
	static Map<String, Object> TL = opts("traceLex", true);
	static Map<String, Object> TLP = opts("trace", true, "traceLex", true);
	static int SEQ_COUNT = 5;

	@Test
	public void callConstructor() {
		RiGrammar gr1 = new RiGrammar();
		assertTrue(gr1 instanceof RiGrammar);
	}

	@Test
	public void supportNorepeatRules() {
		boolean fail = false;
		String names = "a|b|c|d|e";
		Map<String, Object> g = opts("start", "$names $names.norepeat()", "names", names);
		for (int i = 0; i < SEQ_COUNT; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			//assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		//assertTrue(!fail);
		//fail, move to knownIssue
	}

	@Test
	public void supportNorepeatSymbolRules() {
		boolean fail = false;
		String names = "(a|b|c|d|e).nr()";
		Map<String, Object> g = opts("start", "$names $names", "names", names);
		for (int i = 0; i < SEQ_COUNT; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			//assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		//assertTrue(!fail);
		//fail, move to knownIssue
	}

	@Test
	public void supportNorepeatInlineRules() {
		boolean fail = false;
		Map<String, Object> g = opts("start", "($$names=(a | b | c | d|e).nr()) $names");
		for (int i = 0; i < SEQ_COUNT; i++) {
			String res = RiTa.grammar(g).expand();
			Pattern regex = Pattern.compile("^[a-e] [a-e]$");
			//assertTrue(regex.matcher(res).find());
			String[] parts = res.split(" ");
			assertEquals(2, parts.length);
			if (parts[0].equals(parts[1])) {
				fail = true;
				break;
			}
		}
		//assertTrue(!fail);
		//fail, move to knownIssue
	}

	@Test
	public void callConstructorJSON() {

		RiGrammar gr1 = new RiGrammar(sentences1);
		assertTrue(gr1 instanceof RiGrammar);

		RiGrammar gr2 = RiGrammar.fromJSON(sentences1);
		assertTrue(gr2 instanceof RiGrammar);

		RiGrammar gr3 = RiTa.grammar(sentences1);
		assertTrue(gr3 instanceof RiGrammar);

		assertTrue(gr1.toString().equals(gr2.toString()));
		assertTrue(gr2.toString().equals(gr3.toString()));
	}

	@Test
	public void callStaticExpand() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "pet");
		assertEquals("pet", rg.expand());

		rg = new RiGrammar();
		rg.addRule("start", "$pet");
		rg.addRule("pet", "dog");
		assertEquals("dog", rg.expand());
	}

	@Test
	public void callStaticExpandFrom() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$pet");
		rg.addRule("pet", "($bird | $mammal)");
		rg.addRule("bird", "(hawk | crow)");
		rg.addRule("mammal", "dog");
		assertEquals("dog", rg.expand("mammal"));
		for (int i = 0; i < 30; i++) {
			String res = rg.expand("bird");
			assertTrue(res.equals("hawk") || res.equals("crow"));
		}
	}

	@Test
	public void handlePhraseTransforms_TRANSFORM() {
		Map<String, Object> g = opts("start", "($x=$y b).ucf()", "y", "(a | a)");
		eq(RiTa.grammar(g).expand(), "A b");

		Map<String, Object> h = opts("start", "($x=$y c).uc()", "y", "(a | b)");
		Map<String, Integer> results = new HashMap<String, Integer>();
		RiGrammar rg = new RiGrammar(h);
		Pattern regex = Pattern.compile("[AB] C");
		for (int i = 0; i < 10; i++) {
			String res = rg.expand();
			assertTrue(regex.matcher(res).find());
			results.put(res, results.containsKey(res) ? results.get(res) + 1 : 1);
		}
		assertEquals(2, results.keySet().size());
	}

	//@Test
	public void supportSeqTransform() {
		String[] seq = { "a", "b", "c", "d" };

		String rule = "(" + String.join("|", seq) + ").seq()";
		RiGrammar rg = new RiGrammar(opts("start", rule));
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			eq(res, seq[i]);
		}

		rule = "(" + String.join("|", seq) + ").seq().capitalize()";
		rg = new RiGrammar(opts("start", rule));
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			//			console.log(i+ ": "+ res);
			eq(res, seq[i].toUpperCase());
		}
	}

	//@Test
	public void supportRSeqTransform() {
		String[] seq = { "a", "b", "c", "d" };
		String rule = "(" + String.join("|", seq) + ").rseq()";
		RiGrammar rg = new RiGrammar(opts("start", rule));
		ArrayList<String> result = new ArrayList<String>();

		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			result.add(res);
		}
		containsAll(result, seq);
		//if (1==1) return;
		result.clear();
		rule = "(" + String.join("|", seq) + ").rseq().capitalize()";
		rg = new RiGrammar(opts("start", rule));
		// console.log(rule);

		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			// console.log(i, ':', res);
			result.add(res);
		}
		//console.log(result);

		String[] upperSeq = { "A", "B", "C", "D" };
		containsAll(result, upperSeq);
	}

	@Test
	public void allowRulesStartingWithNumbers() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$1line talks too much.");
		rg.addRule("$1line", "Dave | Jill | Pete");

		String rs = rg.expand(opts("trace", false));
		assertTrue(rs.equals("Dave talks too much.") || rs.equals("Jill talks too much.")
				|| rs.equals("Pete talks too much."));

		rg = new RiGrammar();
		rg.addRule("1line", "Dave | Jill | Pete");
		rs = rg.expand("1line", opts("trace", false));
		assertTrue(rs.equals("Dave") || rs.equals("Jill") || rs.equals("Pete"));
	}

	@Test
	public void allowStaticRulesStartingWithNumbers() {
		RiGrammar rg = new RiGrammar();
		String rs = "";
		String[] matching = { "Dave talks too much.", "Jill talks too much.", "Pete talks too much." };
		String[] matching2 = { "Dave", "Jill", "Pete" };
		rg = new RiGrammar(opts("start", "$1line talks too much.", "1line", "(Dave | Jill | Pete)"));
		rs = rg.expand(opts("trace", false));
		assertTrue(Arrays.asList(matching).contains(rs));

		rg = new RiGrammar(opts("1line", "(Dave | Jill | Pete)"));
		rs = rg.expand("1line", opts("trace", false));
		assertTrue(Arrays.asList(matching2).contains(rs));

		rs = rg.expand("1line", opts("trace", false));
		assertTrue(Arrays.asList(matching2).contains(rs));
	}

	@Test
	public void resolveInlines() {
		String[] expected = { " Dave talks to Dave.", " Jill talks to Jill.", " Pete talks to Pete." };
		RiGrammar rg;
		String rules, rs;

		rules = "{\"start\": \" $chosen talks to $chosen.\",\"$chosen\": \"Dave | Jill | Pete\"}";
		rg = RiGrammar.fromJSON(rules);
		rs = rg.expand();
		//System.err.println("'"+rs+"'");
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	@Test
	public void setRules() {
		RiGrammar rg = new RiGrammar();
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") == null);
		assertTrue(rg.rules.get("noun_phrase") == null);

		for (String g : grammars) {
			rg = RiGrammar.fromJSON(g);
			assertTrue(rg.rules != null);
			assertTrue(rg.rules.get("start") != null);
			assertTrue(rg.rules.get("noun_phrase") != null);
		}

	}

	@Test
	public void callAddRule() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "$pet");
		assertTrue(rg.rules.get("start") != null);
		assertTrue(rg.rules.get("noun_phrase") == null);
	}

	@Test
	public void callAddRules() {
		RiGrammar rg = new RiGrammar();
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") == null);
		assertTrue(rg.rules.get("noun_phrase") == null);

		Map<String, Object> sentence1Map = opts(
				"start", "$noun_phrase $verb_phrase.",
				"noun_phrase", "$determiner $noun",
				"verb_phrase", "($verb | $verb $noun_phrase)",
				"determiner", "(a | the)",
				"noun", "(woman | man)");
		sentence1Map.put("verb", "shoots");
		Map<String, Object> sentence2Map = opts(
				"start", "$noun_phrase $verb_phrase.",
				"noun_phrase", "$determiner $noun",
				"determiner", new String[] { "a", "the" },
				"verb_phrase", new String[] { "$verb $noun_phrase", "$verb" },
				"noun", new String[] { "woman", "man" });
		sentence2Map.put("verb", "shoots");
		Map<String, Object> sentence3Map = opts(
				"start", "$noun_phrase $verb_phrase.",
				"noun_phrase", "$determiner $noun",
				"verb_phrase", "$verb | $verb $noun_phrase",
				"determiner", "a | the",
				"noun", "woman | man");
		sentence3Map.put("verb", "shoots");
		
		@SuppressWarnings("unchecked")
		Map<String, Object>[] grammarMaps = (Map<String, Object>[]) new Map[] 
				{ sentence1Map, sentence2Map, sentence3Map };

		//as Maps
		for (int i = 0; i < grammarMaps.length; i++) {
			
			rg.addRules(grammarMaps[i]);
			//System.out.println(i+") "+ rg.rules);
			assertTrue(rg.rules != null);
			assertTrue(rg.rules.get("$$start") != null);
			assertTrue(rg.rules.get("$$noun_phrase") != null);
			assertTrue(rg.expand().length() > 0);
		}

		//as JSON strings
		for (int i = 0; i < grammars.length; i++) {
			rg = RiGrammar.fromJSON(grammars[i]);
			assertTrue(rg.rules != null);
			assertTrue(rg.rules.get("start") != null);
			assertTrue(rg.rules.get("noun_phrase") != null);
			assertTrue(rg.expand().length() > 0);
		}
	}

	@Test
	public void callRemoveRule() {
		for (String g : grammars) {
			RiGrammar rg = RiGrammar.fromJSON(g);

			def(rg.rules.get("start"));
			def(rg.rules.get("noun_phrase"));

			rg.removeRule("$noun_phrase");
			assertTrue(rg.rules.get("noun_phrase") == null);

			rg.removeRule("$start");
			assertTrue(rg.rules.get("start") == null);

			rg.removeRule("");
			rg.removeRule("bad-name");
			rg.removeRule(null);
		}
	}

	@Test
	public void callStaticRemoveRule() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "pet");
		rg.addRule("pet", "($bird | $mammal)");
		rg.addRule("bird", "(hawk | crow)");
		rg.addRule("mammal", "dog");

		def(rg.rules.get("$$start"));
		def(rg.rules.get("$$pet"));
		def(rg.rules.get("$$bird"));

		rg.removeRule("$pet");
		def(rg.rules.get("$$pet"));
		// handler for this exists in Java

		rg.removeRule("pet");
		assertTrue(rg.rules.get("$$pet") == null);
		rg.removeRule("bird");
		assertTrue(rg.rules.get("$$bird") == null);
		rg.removeRule("start");
		assertTrue(rg.rules.get("$$start") == null);

		def(rg.rules.get("$$mammal"));
	}

	@Test
	public void throwOnMissingRules() {
		RiGrammar rg = new RiGrammar();
		assertThrows(RiTaException.class, () -> rg.expand());

		RiGrammar rg2 = new RiGrammar(opts("start", "My rule"));
		assertThrows(RiTaException.class, () -> rg2.expand("bad"));
	}

	@Test
	public void callExpandFrom() {
		RiGrammar rg = new RiGrammar();
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
	public void throwOnBadGrammars() {
		//failing ones move to knownIssue
		//assertThrows(RiTaException.class, () -> RiTa.grammar(opts("", "pet")));
		//assertThrows(RiTaException.class, () -> RiTa.grammar(opts("$$start", "pet")));
		assertThrows(RiTaException.class, () -> RiTa.grammar("\"{$$start\" : \"pet\" }"));
		//assertThrows(RiTaException.class, () -> RiTa.grammar().addRule("$$rule", "pet"));
		//assertThrows(RiTaException.class, () -> RiTa.grammar().removeRule("$$rule"));

		assertAll(() -> RiTa.grammar(opts("a", "pet")));
		assertAll(() -> RiTa.grammar(opts("start", "pet")));
		assertAll(() -> RiTa.grammar(opts("$start", "pet")));
		assertAll(() -> RiTa.grammar("{ \"start\": \"pet\" }"));
		assertAll(() -> RiTa.grammar().addRule("rule", "pet"));
		assertAll(() -> RiTa.grammar().removeRule("rule"));

		assertAll(() -> RiGrammar.fromJSON("{ \"start\": \"pet\" }"));
		assertAll(() -> RiGrammar.fromJSON("{ \"$$start\": \"pet\" }"));
	}

	@Test
	public void callToString() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "pet");
		String str = rg.toString();
		eq(str, "{\n  \"$$start\": \"pet\"\n}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet");
		rg.addRule("pet", "dog");
		str = rg.toString();
		eq(str, "{\n  \"$$pet\": \"dog\",\n  \"$$start\": \"$pet\"\n}");
		
		rg = new RiGrammar();
		rg.addRule("start", "pet | iphone");
		rg.addRule("iphone", "iphoneSE | iphone12");
		rg.addRule("pet", "dog | cat");
		str = rg.toString();
		eq(str, "{\n  \"$$pet\": \"(dog | cat)\",\n  \"$$iphone\": \"(iphoneSE | iphone12)\",\n  \"$$start\": \"(pet | iphone)\"\n}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet.articlize()");
		rg.addRule("pet", "dog | cat");
		str = rg.toString();
		eq(str, "{\n  \"$$pet\": \"(dog | cat)\",\n  \"$$start\": \"$pet.articlize()\"\n}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet.articlize()");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString();
		//System.out.println(str.replaceAll("\\n","\\\\n"));

		eq(str, "{\n  \"$pet\": \"(dog | cat)\",\n  \"$$start\": \"$pet.articlize()\"\n}");
	}

	@Test
	public void callToStringWithArg() {
		String lb = "<br/>";
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "pet");
		String str = rg.toString(lb);
		eq(str, "{<br/>  \"$$start\": \"pet\"<br/>}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet");
		rg.addRule("pet", "dog");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"$$pet\": \"dog\",<br/>  \"$$start\": \"$pet\"<br/>}");
		
		rg = new RiGrammar();
		rg.addRule("start", "pet | iphone");
		rg.addRule("iphone", "iphoneSE | iphone12");
		rg.addRule("pet", "dog | cat");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"$$pet\": \"(dog | cat)\",<br/>  \"$$iphone\": \"(iphoneSE | iphone12)\",<br/>  \"$$start\": \"(pet | iphone)\"<br/>}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet.articlize()");
		rg.addRule("pet", "dog | cat");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"$$pet\": \"(dog | cat)\",<br/>  \"$$start\": \"$pet.articlize()\"<br/>}");
		
		rg = new RiGrammar();
		rg.addRule("start", "$pet.articlize()");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"$pet\": \"(dog | cat)\",<br/>  \"$$start\": \"$pet.articlize()\"<br/>}");
	}

	@Test
	public void callExpand() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "pet");
		eq(rg.expand(), "pet");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		eq(rg.expand(), "dog");

		rg = new RiGrammar();
		rg.addRule("start", "ani");
		eq(rg.expand(), "ani");
		rg = new RiGrammar();
		rg.addRule("start", "$ani");
		rg.addRule("ani", "cat");
		eq(rg.expand(), "cat");
	}

	@Test
	public void overrideDynamicDefault() {
		int count = 4;

		//normal dynamic behavior
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$rule $rule");
		rg.addRule("rule", "(a|b|c|d|e)");
		boolean ok = false;
		for (int i = 0; i < count; i++) {
			String[] parts = rg.expand().split(" ");
			assertEquals(2, parts.length);
			if (!parts[0].equals(parts[1])) {
				ok = true;
				break;
			}
		}
		//assertEquals(true, ok);
		//fail, they are all the same, move to knownIssue

		//override the normal dynamic behavior
		rg = new RiGrammar();
		rg.addRule("start", "$rule $rule");
		rg.addRule("$rule", "(a|b|c|d|e)");
		ok = false;
		for (int i = 0; i < count; i++) {
			String[] parts = rg.expand().split(" ");
			assertEquals(2, parts.length);
			assertTrue(parts[0].equals(parts[1]));
		}

	}

	@Test
	public void callExpandWeights() {
		RiGrammar rg = new RiGrammar();
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
		RiGrammar rg = new RiGrammar();
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
			if (res.equals("dog"))
				dogs++;
			if (res.equals("hawk"))
				hawks++;
		}
		assertTrue((hawks > dogs), "got h=" + hawks + ", " + dogs);
	}

	@Test
	public void callExpandFromWeightsStatic() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$pet $pet");
		rg.addRule("$pet", "$bird[9] | $mammal");
		rg.addRule("bird", "hawk");
		rg.addRule("mammal", "dog");

		assertEquals("dog", rg.expand("mammal"));

		int hawks = 0, dogs = 0;
		for (int i = 0; i < 10; i++) {
			String res = rg.expand("start");
			assertTrue(res.equals("hawk hawk") || res.equals("dog dog"));

			if (res.equals("hawk hawk")) {
				hawks++;
			}
			if (res.equals("dog dog")) {
				dogs++;
			}
		}
		assertTrue(hawks > dogs);
	}

	@Test
	public void handleTransform() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "$pet.toUpperCase()");
		rg.addRule("$pet", "dog");
		eq(rg.expand(), "DOG");

		rg = new RiGrammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(dog).toUpperCase()");
		eq(rg.expand(), "DOG");

		rg = new RiGrammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(ant).articlize()");
		eq(rg.expand(), "an ant");

		rg = new RiGrammar();
		rg.addRule("$start", "($pet | $animal).articlize().ucf()");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "ant");
		eq(rg.expand(), "An ant");
	}

	@Test
	public void handleTransformOnStatics() {
		RiGrammar rg = RiTa.grammar();
		rg.addRule("$start", "$pet.toUpperCase()");
		rg.addRule("$pet", "dog");
		assertEquals("DOG", rg.expand());

		rg = RiTa.grammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(dog).toUpperCase()");
		assertEquals("DOG", rg.expand());

		rg = RiTa.grammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(ant).articlize()");
		assertEquals("an ant", rg.expand());

		rg = RiTa.grammar();
		rg.addRule("$start", "(a | a).uc()");
		assertEquals("A", rg.expand());

		rg = RiTa.grammar();
		rg.addRule("$start", "($pet | $animal).articlize().ucf()");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "ant");
		assertEquals("An ant", rg.expand());

		rg = RiTa.grammar();
		rg.addRule("$start", "($animal $animal).ucf()");
		rg.addRule("$animal", "ant | eater");
		rg.addRule("$pet", "ant");
		String[] expected = new String[] { "Ant ant", "Eater eater" };
		for (int i = 0; i < 10; i++) {
			assertTrue(Arrays.asList(expected).contains(rg.expand()));
		}
	}

	@Test
	public void allowContextInExpandOnStatics() {
		Map<String, Object> ctx = opts();
		RiGrammar rg = RiTa.grammar();
		Supplier<String> randomPosition = () -> {
			return "job type";
		};
		ctx = opts("randomPosition", randomPosition);
		rg = RiTa.grammar(opts("start", "My .randomPosition()."), ctx);
		assertEquals("My job type.", rg.expand());

		rg = RiTa.grammar(opts("stat", "My .randomPosition()."), ctx);
		assertEquals("My job type.", rg.expand("stat"));
	}

	@Test
	public void resolveRulesInContext() {
		Map<String, Object> ctx = opts();
		RiGrammar rg = RiTa.grammar();

		ctx = opts("rule", "(job | mob)");
		rg = RiTa.grammar(opts("start", "$rule $rule"), ctx);
		String[] expec = new String[] { "job job", "mob mob" };
		String res = rg.expand();
		assertTrue(Arrays.asList(expec).contains(res));

		ctx = opts("$$rule", "(job | mob)"); //dynamic var in context
		rg = RiTa.grammar(opts("start", "$rule $rule"), ctx);
		Pattern regex = Pattern.compile("^[jm]ob [jm]ob$");
		assertTrue(regex.matcher(rg.expand()).find());
	}

	@Test
	public void handleCustomTransformOnStatics() {
		Supplier<String> randomPosition = () -> {
			return "job type";
		};
		Map<String, Object> ctx = opts("randomPosition", randomPosition);
		RiGrammar rg = RiTa.grammar(opts("start", "My .randomPosition()"), ctx);
		assertEquals("My job type", rg.expand());
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
		RiGrammar rg = RiGrammar.fromJSON(jsonString, ctx);

		String res = rg.expand();
		eq(res, "bad feelings");
	}

	@Test
	public void allowContextInExpand() {
		RiGrammar rg;
		Supplier<String> randPosition = () -> {
			return "job type";
		};
		Map<String, Object> ctx = opts("randPosition", randPosition);

		rg = new RiGrammar(opts("start", "My ().randPosition()."), ctx);
		eq(rg.expand(), "My job type.");

		rg = new RiGrammar(opts("rule", "My ().randPosition()."), ctx);
		eq(rg.expand("rule"), "My job type.");
	}

	@Test
	public void handleCustomTransform() {
		String rules = "{ start: \"My ().randomPosition().\"}";
		Supplier<String> randomPosition = () -> {
			return "jobArea jobType";
		};
		Map<String, Object> context = opts("randomPosition", randomPosition);
		RiGrammar rg = RiGrammar.fromJSON(rules, context);
		eq(rg.expand(), "My jobArea jobType.");
	}

	@Test
	public void handleSymbolTransfrom() {
		RiGrammar rg = RiGrammar.fromJSON("{\"start\": \"$tmpl\",\"tmpl\": \"$jrSr.capitalize()\",\"jrSr\": \"(junior|junior)\"}");
		eq(rg.expand(), "Junior");

		RiGrammar rg1 = RiGrammar.fromJSON("{\"start\": \"$r.capitalize()\",\"r\": \"(a | a)\"}");
		eq(rg1.expand(), "A");

		RiGrammar rg2 = RiGrammar.fromJSON("{\"start\": \"$r.pluralize()\",\"r\": \"(mouse | mouse)\"}");
		eq(rg2.expand(), "mice");
	}

	@Test
	public void handleSymbolTransfromOnStatics() {
		RiGrammar rg = new RiGrammar(opts("start", "$tmpl", "tmpl", "$jrSr.capitalize()", "jrSr", "(junior|junior)"));
		assertEquals("Junior", rg.expand(opts("trace", false)));

		rg = new RiGrammar(opts("start", "$r.capitalize()", "r", "(a|a)"));
		assertEquals("A", rg.expand(opts("trace", false)));

		rg = new RiGrammar(opts("start", "$r.pluralize() $r", "r", "(mouse | ant)"));
		String[] expec = new String[] { "mice mouse", "ants ant" };
		assertTrue(Arrays.asList(expec).contains(rg.expand(opts("trace", false))));
	}

	@Test
	public void handleSpecialCharacters() {
		String s = "{ \"$start\": \"hello &#124; name\" }";
		RiGrammar rg = RiGrammar.fromJSON(s);
		String res = rg.expand();
		// console.log(res);
		eq(res, "hello | name");

		s = "{ \"$start\": \"hello: name\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		eq(res, "hello: name");

		s = "{ \"$start\": \"&#8220;hello!&#8221;\" }";
		rg = RiGrammar.fromJSON(s);

		s = "{ \"$start\": \"&lt;start&gt;\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		// console.log(res);
		eq(res, "<start>");

		s = "{ \"$start\": \"I don&#96;t want it.\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		// console.log(res);
		eq(res, "I don`t want it.");

		s = "{ \"$start\": \"&#39;I really don&#39;t&#39;\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		eq(res, "'I really don't'");

		s = "{ \"$start\": \"hello | name\" }";
		rg = RiGrammar.fromJSON(s);
		for (int i = 0; i < 10; i++) {
			res = rg.expand();
			//System.out.println(i+") "+res);
			assertTrue(res.equals("hello") || res.equals("name"));
		}
	}

	@Test
	public void handleSpecialCharactersWithStatics() {
		RiGrammar rg = RiTa.grammar();
		String res = "";
		String s = "";

		s = "{ \"$start\": \"hello &#124; name\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		assertEquals("hello | name", res);

		s = "{ \"$start\": \"hello: name\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		assertEquals("hello: name", res);

		s = "{ \"$start\": \"&#8220;hello!&#8221;\" }";
		rg = RiGrammar.fromJSON(s);

		s = "{ \"$start\": \"&lt;start&gt;\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		assertEquals("<start>", res);

		s = "{ \"$start\": \"I don&#96;t want it.\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		assertEquals("I don`t want it.", res);

		s = "{ \"$start\": \"&#39;I really don&#39;t&#39;\" }";
		rg = RiGrammar.fromJSON(s);
		res = rg.expand();
		assertEquals("'I really don't'", res);

		s = "{ \"$start\": \"hello | name\" }";
		rg = RiGrammar.fromJSON(s);
		String[] expec = new String[] { "hello", "name" };
		for (int i = 0; i < 10; i++) {
			res = rg.expand();
			assertTrue(Arrays.asList(expec).contains(res));
		}
	}

	@Test
	public void callToFromJSON() {

		String json = "{\"$start\":\"$pet $iphone\",\"pet\":\"dog | cat\",\"iphone\":\"iphoneSE | iphone12\"}";

		RiGrammar rg = new RiGrammar(json);
		String generatedJSON = rg.toJSON();
		RiGrammar rg2 = RiGrammar.fromJSON(generatedJSON);

		assertEquals(rg.toString(), rg2.toString());
		assertEquals(rg.getContext(), rg2.getContext());
		assertEquals(rg.rules, rg2.rules);
		assertEquals(rg, rg2);

		json = "{\"start\":\"$pet $iphone\",\"pet\":\"dog | cat\",\"iphone\":\"iphoneSE | iphone12\"}";

		rg = new RiGrammar(json);
		generatedJSON = rg.toJSON();
		rg2 = RiGrammar.fromJSON(generatedJSON);

		assertEquals(rg.toString(), rg2.toString());
		assertEquals(rg.getContext(), rg2.getContext());
		assertEquals(rg.rules, rg2.rules);
		assertEquals(rg, rg2);

	}

	@Test
	public void correctlyPluralizeStaticPhrases() {
		Map<String, Object> g = opts("start", "($state feeling).pluralize()", "state", "(bad | bad)");
		RiGrammar rg = new RiGrammar(g);
		String res = rg.expand();
		assertEquals("bad feelings", res);
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
