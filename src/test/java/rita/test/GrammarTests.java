package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static rita.Util.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import rita.*;

public class GrammarTests {

	String sentences1 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"($verb | $verb $noun_phrase)\",\"$determiner\": \"(a | the)\",\"$noun\": \"(woman | man)\",\"$verb\": \"shoots\"}";

	String sentences2 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$determiner\": [\"a\", \"the\"],\"$verb_phrase\": [\"$verb $noun_phrase\", \"$verb\"],\"$noun\": [\"woman\", \"man\"],\"$verb\": \"shoots\"}";

	String sentences3 = "\"{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"$verb | $verb $noun_phrase\",\"$determiner\": \"a | the\",\"$noun\": \"woman | man\",\"$verb\": \"shoots\"}";

	String[] grammars = { sentences1, sentences2, sentences3 };

	@Test
	public void testConstructor() {
		Grammar gr = new Grammar("");
		assertTrue(gr instanceof Grammar);
	}

	@Test
	public void testSeqTransform() {
		String[] opts = { "a", "b", "c", "d" };

		String rule = "(" + String.join("|", opts) + ").seq()";
		Grammar rg = new Grammar(opts("start", rule));
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			// console.log(i, ':', res);
			assertEquals(res, opts[i]);
		}

		rule = "(" + String.join("|", opts) + ").seq().capitalize()";
		rg = new Grammar(opts("start", rule));
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			// console.log(i, ':', res);
			assertEquals(res, opts[i].toUpperCase());
		}
	}

	@Test
	public void testRSeqTransform() {
		String[] opts = { "a", "b", "c", "d" };
		String rule = "(" + String.join("|", opts) + ").seq()";
		Grammar rg = new Grammar(opts("start", rule));
		ArrayList<String> result = new ArrayList<String>();
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			// console.log(i, ':', res);
			result.add(res);
		}
		containsMultipleValues(result, opts);

		rule = "(" + String.join("|", opts) + ").seq().capitalize()";
//        rg = new Grammar(opts("start", rule));
		// console.log(rule);
		for (int i = 0; i < 4; i++) {
			String res = rg.expand();
			// console.log(i, ':', res);
			result.add(res);
		}
		String[] opts_u = { "A", "B", "C", "D" };
		containsMultipleValues(result, opts_u);
	}

	@Test
	public void testResolveInlines() {
		String rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"Dave | Jill | Pete\"}";
		Grammar rg = new Grammar(rules);
		String rs = rg.expand(opts("trace", false));
		String[] potentialResults = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };

		assertTrue(Arrays.asList(potentialResults).contains(rs));

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave\",\"Jill\": \"Jill\",\"Pete\": \"Pete\",}";
		rs = rg.expand(opts("trace", false));
		assertTrue(Arrays.asList(potentialResults).contains(rs));

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave | Jill | Pete\",\"Jill\": \"Dave | Jill | Pete\",\"Pete\": \"Dave | Jill | Pete\",}";
		rg = new Grammar(rules);
		rs = rg.expand(opts("trace", false));
		assertTrue(Arrays.asList(potentialResults).contains(rs));

	}

	@Test
	public void testSetRules() {
		Grammar rg = new Grammar();
		assertTrue(rg.rules != null);
		assertTrue(rg.rules.get("start") != null);
		assertTrue(rg.rules.get("noun_phrase") != null);

		for (String g : grammars) {
			rg.setRules(g);
			assertTrue(rg.rules != null);
			assertTrue(rg.rules.get("start") != null);
			assertTrue(rg.rules.get("noun_phrase") != null);
		}

	}

	@Test
	public void testAddRules() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		assertTrue(rg.rules.get("start") != null);
		// TODO:prob.
//		rg.addRule("$start", "$dog", .3);
		assertTrue(rg.rules.get("noun_phrase") != null);
	}

	@Test
	public void testRemoveRules() {
		for (String g : grammars) {
			Grammar rg1 = new Grammar();
			def(rg1.rules.get("start"));
			def(rg1.rules.get("noun_phrase"));

			rg1.removeRule("$noun_phrase");
			def(rg1.rules.get("noun_phrase") == null);

			rg1.removeRule("$start");
			def(rg1.rules.get("start") == null);

			rg1.removeRule("");
			rg1.removeRule("bad-name");
			rg1.removeRule(null);
		}
	}

	@Test
	public void testBadGrammarNames() {
		Grammar rg = new Grammar();
//		assertThrows(RiTaException.class, () -> rg.expandFrom("wrongName"));
		assertThrows(RiTaException.class, () -> rg.expand());
	}

	@Test
	public void testExpandFrom() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "($bird | $mammal)");
		rg.addRule("$bird", "(hawk | crow)");
		rg.addRule("$mammal", "dog");
		assertEquals(rg.expand("$mammal"), "dog");

		for (int i = 0; i < 30; i++) {
			String res = rg.expand("$bird");
			assertTrue(res == "hawk" || res == "crow");
		}

	}

	@Test
	public void testToString() {
		// TODO in js
	}

	@Test
	public void testExpand() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "pet");
		assertEquals(rg.expand(), "pet");
		rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		assertEquals(rg.expand(), "dog");
	}

	@Test
	public void testExpandWeights() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$rule1");
		rg.addRule("$rule1", "cat | dog | boy");
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		for (int i = 0; i < 30; i++) {
			String res = rg.expand();
			assertTrue(res == ("cat") || res == ("dog") || res == ("boy"));
			if (res == ("cat"))
				found1 = true;
			else if (res == ("dog"))
				found2 = true;
			else if (res == ("boy")) found3 = true;
		}
		assertTrue(found1 && found2 && found3); // found all

	}

	@Test
	public void testExpandFromWeights() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "$bird [9] | $mammal");
		rg.addRule("$bird", "hawk");
		rg.addRule("$mammal", "dog");

		assertEquals(rg.expand("$mammal"), "dog");

		int hawks = 0;
		int dogs = 0;
		for (int i = 0; i < 30; i++) {
			String res = rg.expand("$pet");
			assertTrue(res == "hawk" || res == "dog", "got " + res);
			if (res == "dog") dogs++;
			if (res == "hawk") hawks++;
		}
		assertTrue((hawks > dogs * 2), "got h=" + hawks + ", " + dogs);

	}

	@Test
	public void testTransform() {
		Grammar rg = new Grammar();
		rg.addRule("$start", "$pet.toUpperCase()");
		rg.addRule("$pet", "dog");
		assertEquals(rg.expand(), "DOG");

		rg = new Grammar();
		rg.addRule("$start", "($pet | $animal)");
		rg.addRule("$animal", "$pet");
		rg.addRule("$pet", "(dog).toUpperCase()");
		assertEquals(rg.expand(), "DOG");
	}

	@Test
	public void testPluralizePhrasesInTransform() {
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
		String jsonString = "{start: '($state feeling).pluralise()',state: 'bad | bad',}";
		Grammar rg = new Grammar(jsonString, ctx);

		String res = rg.expand();
		assertEquals(res, "bad feelings");

	}

	@Test
	public void testContextInExpand() {
		Supplier<String> randomPosition = () -> {
			return "jobArea jobType";
		};
		Map<String, Object> context = opts("randomPosition", randomPosition);
		Grammar rg = new Grammar(opts("start", "My .randomPosition()."));
		assertEquals(rg.expand(context), "My jobArea jobType.");

		rg = new Grammar(opts("stat", "My .randomPosition()."));
		assertEquals(rg.expand("stat", context), "My jobArea jobType.");

	}

	@Test
	public void testCustomTransform() {
		String rules = "{ start: \"My .randomPosition().\"}";
		Supplier<String> randomPosition = () -> {
			return "jobArea jobType";
		};
		Map<String, Object> context = opts("randomPosition", randomPosition);
		Grammar rg = new Grammar(rules, context);
		assertEquals(rg.expand(), "My jobArea jobType.");
	}

	@Test
	public void testSymbolTransfrom() {
		Grammar rg = new Grammar("{\"start\": \"$tmpl\",\"tmpl\": \"$jrSr.capitalize()\",\"jrSr\": \"(junior|junior)\"}");
		assertEquals(rg.expand(opts("trace", false)), "Junior");

		Grammar rg1 = new Grammar("{\"start\": \"$r.capitalize()\",\"r\": \"(a|a)\"}");
		assertEquals(rg1.expand(opts("trace", false)), "A");

	}

	@Test
	public void testSpecialCharacters() {
		String s = "{ \"$start\": \"hello &#124; name\" }";
		Grammar rg = new Grammar(s);
		String res = rg.expand();
		// console.log(res);
		assertEquals(res, "hello | name");

		s = "{ \"$start\": \"hello: name\" }";
		rg = new Grammar(s);
		res = rg.expand();
		assertEquals(res, "hello: name");

		s = "{ \"$start\": \"&#8220;hello!&#8221;\" }";
		rg = new Grammar(s);

		s = "{ \"$start\": \"&lt;start&gt;\" }";
		rg = new Grammar(s);
		res = rg.expand();
		// console.log(res);
		assertEquals(res, "<start>");

		s = "{ \"$start\": \"I don&#96;t want it.\" }";
		rg = new Grammar(s);
		res = rg.expand();
		// console.log(res);
		assertEquals(res, "I don`t want it.");

		s = "{ \"$start\": \"&#39;I really don&#39;t&#39;\" }";
		rg = new Grammar(s);
		res = rg.expand();
		assertEquals(res, "'I really don't'");

		s = "{ \"$start\": \"hello | name\" }";
		rg = new Grammar(s);
		for (int i = 0; i < 10; i++) {
			res = rg.expand();
			assertTrue(res == "hello" || res == "name");
		}

	}

	private void def(Object o) {
		assertTrue(o != null);
	}

	private void containsMultipleValues(ArrayList<String> arrayList, String[] members) {
		int found = 0;
		for (String item : members) {
			if (arrayList.contains(item)) {
				found++;
			}
		}
		assertTrue(found == members.length);
	}

}
