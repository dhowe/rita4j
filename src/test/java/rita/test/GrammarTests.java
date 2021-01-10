package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static rita.RiTa.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import rita.*;

public class GrammarTests {

	static String sentences1 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"($verb | $verb $noun_phrase)\",\"$determiner\": \"(a | the)\",\"$noun\": \"(woman | man)\",\"$verb\": \"shoots\"}";
	static String sentences2 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$determiner\": [\"a\", \"the\"],\"$verb_phrase\": [\"$verb $noun_phrase\", \"$verb\"],\"$noun\": [\"woman\", \"man\"],\"$verb\": \"shoots\"}";
	static String sentences3 = "{\"$start\": \"$noun_phrase $verb_phrase.\",\"$noun_phrase\": \"$determiner $noun\",\"$verb_phrase\": \"$verb | $verb $noun_phrase\",\"$determiner\": \"a | the\",\"$noun\": \"woman | man\",\"$verb\": \"shoots\"}";
	public static String[] grammars = { sentences1, sentences2, sentences3 };

	static Map<String, Object> TT = opts("trace", true);

	@Test
	public void callConstructor() {
		RiGrammar gr1 = new RiGrammar();
		assertTrue(gr1 instanceof RiGrammar);
	}

//	@Test
//	public void haikuJSON() {
//		String haikuGrammar = "{    \"$start\": \"$line5 % $line7 % $line5\",    \"$line5\": \"$syl1 $syl4 |$syl1 $syl3 $syl1 |$syl1 $syl1 $syl3 | $syl1 $syl2 $syl2 | $syl1 $syl2 $syl1 $syl1 | $syl1 $syl1 $syl2 $syl1 | $syl1 $syl1 $syl1 $syl2 | $syl1 $syl1 $syl1 $syl1 $syl1 | $syl2 $syl3 | $syl2 $syl2 $syl1 | $syl2 $syl1 $syl2 | $syl2 $syl1 $syl1 $syl1 | $syl3 $syl2 | $syl3 $syl1 $syl1 | $syl4 $syl1 | $syl5\",    \"$line7\": \"$syl1 $syl1 $line5 | $syl2 $line5 | $line5 $syl1 $syl1 | $line5 $syl2\",    \"$syl1\": \"red | white | black | sky | dawns | breaks | falls | leaf | rain | pool | my | your | sun | clouds | blue | green | night | day | dawn | dusk | birds | fly | grass | tree | branch | through | hell | zen | smile | gray | wave | sea | through | sound | mind | smoke | cranes | fish\",    \"$syl2\": \"drifting | purple | mountains | skyline | city | faces | toward | empty | buddhist | temple | japan | under | ocean | thinking | zooming | rushing | over | rice field | rising | falling | sparkling | snowflake\",    \"$syl3\": \"sunrises | pheasant farms | people farms | samurai | juniper | fishing boats | far away | kimonos | evenings | peasant rain | sad snow fall\",    \"$syl4\": \"aluminum | yakitori | the east village | west of the sun |  chrysanthemums | cherry blossoms\",    \"$syl5\": \"resolutional | non-elemental | rolling foothills rise | toward mountains higher | out over this country | in the springtime again\"}";
//		Grammar g = Grammar.fromJSON(haikuGrammar);
//		System.out.println(g.expand());
//	}

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
	public void handlePhraseTransforms_TRANSFORM() {
		Map<String, Object> g = opts("start", "[$x=$y b].ucf()", "y", "(a | a)");
		eq(RiTa.evaluate(new RiGrammar(g).expand()), "A b");
	}

	@Test
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

	@Test
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
	public void allowRulesStartingWithNum() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("start", "$1line talks too much.");
		rg.addRule("$1line", "Dave | Jill | Pete");

		String rs = rg.expand(opts("trace", false));
		assertTrue(rs.equals("Dave talks too much.") || rs.equals("Jill talks too much.") || rs.equals("Pete talks too much."));

		rg = new RiGrammar();
		rg.addRule("1line", "Dave | Jill | Pete");
		rs = rg.expand("1line", opts("trace", false));
		assertTrue(rs.equals("Dave") || rs.equals("Jill") || rs.equals("Pete"));
	}

	@Test
	public void resolveInlines() {
		String[] expected = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };
		RiGrammar rg;
		String rules, rs;

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"Dave | Jill | Pete\"}";
		rg = RiGrammar.fromJSON(rules);
		rs = rg.expand();
		assertTrue(Arrays.asList(expected).contains(rs));

		rules = "{\"start\": \"[$chosen=$person] talks to $chosen.\",\"person\": \"$Dave | $Jill | $Pete\",\"Dave\": \"Dave\",\"Jill\": \"Jill\",\"Pete\": \"Pete\"}";
		rg = RiGrammar.fromJSON(rules);
		rs = rg.expand();
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
			RiGrammar rg = RiGrammar.fromJSON(g);

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
	public void callToString() {
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "pet");
		String str = rg.toString();
		eq(str, "{\n  \"start\": \"pet\"\n}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		str = rg.toString();
		eq(str, "{\n  \"start\": \"$pet\",\n  \"pet\": \"dog\"\n}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet | $iphone");
		rg.addRule("$iphone", "iphoneSE | iphone12");
		rg.addRule("$pet", "dog | cat");
		str = rg.toString();
		eq(str, "{\n  \"start\": \"($pet | $iphone)\",\n  \"iphone\": \"(iphoneSE | iphone12)\",\n  \"pet\": \"(dog | cat)\"\n}");
	}

	@Test
	public void callToStringWithArg() {
		String lb = "<br/>";
		RiGrammar rg = new RiGrammar();
		rg.addRule("$start", "pet");
		String str = rg.toString(lb);
		eq(str, "{<br/>  \"start\": \"pet\"<br/>}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet");
		rg.addRule("$pet", "dog");
		str = rg.toString(lb);
		eq(str, "{<br/>  \"start\": \"$pet\",<br/>  \"pet\": \"dog\"<br/>}");
		rg = new RiGrammar();
		rg.addRule("$start", "$pet | $iphone");
		rg.addRule("$pet", "dog | cat");
		rg.addRule("$iphone", "iphoneSE | iphone12");
		str = rg.toString(lb);
		eq(str,
				"{<br/>  \"start\": \"($pet | $iphone)\",<br/>  \"iphone\": \"(iphoneSE | iphone12)\",<br/>  \"pet\": \"(dog | cat)\"<br/>}");
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
			if (res.equals("dog")) dogs++;
			if (res.equals("hawk")) hawks++;
		}
		assertTrue((hawks > dogs * 2), "got h=" + hawks + ", " + dogs);

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

		rg = new RiGrammar(opts("start", "My .randPosition()."));
		eq(rg.expand(ctx), "My job type.");

		rg = new RiGrammar(opts("rule", "My .randPosition()."));
		eq(rg.expand("rule", ctx), "My job type.");
	}

	@Test
	public void handleCustomTransform() {
		String rules = "{ start: \"My .randomPosition().\"}";
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
	public void callToFromJSON() {

		String s = "{\"$start\":\"$pet $iphone\",\"pet\":\"dog | cat\",\"iphone\":\"iphoneSE | iphone12\"}";
		RiGrammar rg = RiGrammar.fromJSON(s);
		RiGrammar rg2 = RiGrammar.fromJSON(rg.toJSON());
		eq(rg.toString(), rg2.toString());
		assertTrue(rg.equals(rg2));

		/*for (String g : grammars) { //  KnownIssues
			rg = Grammar.fromJSON(g);
			rg2 = Grammar.fromJSON(rg.toJSON());
			eq(rg2.toString(), rg.toString());
			assertTrue(rg.equals(rg2));
		}*/
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
