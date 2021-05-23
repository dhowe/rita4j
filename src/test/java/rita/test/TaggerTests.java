package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import static rita.RiTa.opts;

public class TaggerTests {

	@Test
	public void callPosArray() {
		arrayEq(RiTa.pos(new String[0]), new String[0]);
		arrayEq(RiTa.pos(new String[] { "deal" }), new String[] { "nn" });
		arrayEq(RiTa.pos(new String[] { "freed" }), new String[] { "jj" });
		arrayEq(RiTa.pos(new String[] { "the" }), new String[] { "dt" });
		arrayEq(RiTa.pos(new String[] { "a" }), new String[] { "dt" });
		arrayEq(RiTa.pos("the top seed".split(" ")), new String[] { "dt", "jj", "nn" });
		arrayEq(RiTa.pos("by illegal means".split(" ")), new String[] { "in", "jj", "nn" });
		arrayEq(RiTa.pos("He outnumbers us".split(" ")), new String[] { "prp", "vbz", "prp" });
		arrayEq(RiTa.pos("I outnumber you".split(" ")), new String[] { "prp", "vbp", "prp" });
		arrayEq(RiTa.pos("Elephants dance".split(" ")), new String[] { "nns", "vbp" });
		arrayEq(RiTa.pos("the boy dances".split(" ")), new String[] { "dt", "nn", "vbz" });
		arrayEq(RiTa.pos("Dave dances".split(" ")), new String[] { "nnp", "vbz" });
	}

	@Test
	public void callPosArrayWithSimple() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("simple", true);
		arrayEq(RiTa.pos(new String[0], hm), new String[0]);
		arrayEq(RiTa.pos(new String[] { "freed" }, hm), new String[] { "a" });
		arrayEq(RiTa.pos(new String[] { "the" }, hm), new String[] { "-" });
		arrayEq(RiTa.pos(new String[] { "a" }, hm), new String[] { "-" });
		arrayEq(RiTa.pos("the top seed".split(" "), hm), new String[] { "-", "a", "n" });
		arrayEq(RiTa.pos("by illegal means".split(" "), hm), new String[] { "-", "a", "n" });
		arrayEq(RiTa.pos("He outnumbers us".split(" "), hm), new String[] { "-", "v", "-" });
		arrayEq(RiTa.pos("I outnumber you".split(" "), hm), new String[] { "-", "v", "-" });
		arrayEq(RiTa.pos("Elephants dance".split(" "), hm), new String[] { "n", "v" });
		arrayEq(RiTa.pos("the boy dances".split(" "), hm), new String[] { "-", "n", "v" });
	}

	@Test
	public void callPosArrayWithInlineSimple() {
		//Map<String,Object> options = opts("inline", true, "simple", true);
		//arrayEq(RiTa.pos(new String[0], options), new String[0]);
		assertEquals("1", "1", "function N/A in java");
	}

	@Test
	public void callInflectedVerbs() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("simple", true);

		arrayEq(RiTa.pos("disbelieves"), new String[] { "vbz" });
		arrayEq(RiTa.pos("disbelieves", hm), new String[] { "v" });

		arrayEq(RiTa.pos("fates"), new String[] { "nns" });
		arrayEq(RiTa.pos("fates", hm), new String[] { "n" });

		arrayEq(RiTa.pos("hates"), new String[] { "vbz" });
		arrayEq(RiTa.pos("hates", hm), new String[] { "v" });

		arrayEq(RiTa.pos("hated"), new String[] { "vbd" });
		arrayEq(RiTa.pos("hated", hm), new String[] { "v" });

		arrayEq(RiTa.pos("hating"), new String[] { "vbg" });
		arrayEq(RiTa.pos("hating", hm), new String[] { "v" });

		arrayEq(RiTa.pos("He rode the horse"), new String[] { "prp", "vbd", "dt", "nn" });
		arrayEq(RiTa.pos("He has ridden the horse"), new String[] { "prp", "vbz", "vbn", "dt", "nn" });

		arrayEq(RiTa.pos("He rowed the boat"), new String[] { "prp", "vbd", "dt", "nn" });
		arrayEq(RiTa.pos("He has rowed the boat"), new String[] { "prp", "vbz", "vbn", "dt", "nn" });

	}

	@Test
	public void callAllPos() {
		arrayEq(RiTa.pos("monkey"), new String[] { "nn" });
		arrayEq(RiTa.pos("monkey's"), new String[] { "nns" });
	}
	
	@Test
	public void callPos() {

		String[] result, answer, resultArr, answerArr;
		String txt;

		arrayEq(RiTa.pos(""), new String[] {});
		arrayEq(RiTa.pos(" "), new String[] {});
		arrayEq(RiTa.pos("freed"), new String[] { "jj" });
		arrayEq(RiTa.pos("biped"), new String[] { "nn" });
		arrayEq(RiTa.pos("greed"), new String[] { "nn" });
		arrayEq(RiTa.pos("creed"), new String[] { "nn" });
		arrayEq(RiTa.pos("weed"), new String[] { "nn" });

		arrayEq(RiTa.pos("the top seed"), new String[] { "dt", "jj", "nn" });
		arrayEq(RiTa.pos("by illegal means"), new String[] { "in", "jj", "nn" });
		arrayEq(RiTa.pos("Joanny Smith ran away"), new String[] { "nnp", "nnp", "vbd", "rb" });

		result = RiTa.pos("mammal");
		answer = new String[] { "nn" };
		arrayEq(result, answer);

		result = RiTa.pos("asfaasd");
		answer = new String[] { "nn" };
		arrayEq(result, answer);

		result = RiTa.pos("innings");
		answer = new String[] { "nns" };
		arrayEq(result, answer);

		result = RiTa.pos("clothes");
		answer = new String[] { "nns" };
		arrayEq(result, answer);

		result = RiTa.pos("teeth");
		answer = new String[] { "nns" };
		arrayEq(result, answer);
		// return;

		result = RiTa.pos("memories");
		answer = new String[] { "nns" };
		arrayEq(result, answer);

		arrayEq(RiTa.pos("flunks"), new String[] { "vbz" });
		arrayEq(RiTa.pos("outnumbers"), new String[] { "vbz" });
		arrayEq(RiTa.pos("He outnumbers us"), new String[] { "prp", "vbz", "prp" });
		arrayEq(RiTa.pos("I outnumber you"), new String[] { "prp", "vbp", "prp" });

		resultArr = RiTa.pos("Elephants dance");
		answerArr = new String[] { "nns", "vbp" };
		arrayEq(answerArr, resultArr);

		result = RiTa.pos("the boy dances");
		answer = new String[] { "dt", "nn", "vbz" };
		arrayEq(result, answer);

		result = RiTa.pos("he dances");
		answer = new String[] { "prp", "vbz" };
		arrayEq(result, answer);

		resultArr = RiTa.pos("Dave dances");
		answerArr = new String[] { "nnp", "vbz" };
		arrayEq(answerArr, resultArr);

		result = RiTa.pos("running");
		answer = new String[] { "vbg" };
		arrayEq(result, answer);

		result = RiTa.pos("asserting");
		answer = new String[] { "vbg" };
		arrayEq(result, answer);

		result = RiTa.pos("assenting");
		answer = new String[] { "vbg" };
		arrayEq(result, answer);

		result = RiTa.pos("Dave");
		answer = new String[] { "nnp" };
		arrayEq(result, answer);

		result = RiTa.pos("They feed the cat");
		answer = new String[] { "prp", "vbp", "dt", "nn" };
		arrayEq(result, answer);

		result = RiTa.pos("There is a cat.");
		answer = new String[] { "ex", "vbz", "dt", "nn", "." };
		arrayEq(result, answer);

		result = RiTa.pos("The boy, dressed in red, ate an apple.");
		answer = new String[] { "dt", "nn", ",", "vbn", "in", "jj", ",", "vbd", "dt", "nn", "." };
		arrayEq(result, answer);

		txt = "The dog ran faster than the other dog.  But the other dog was prettier.";
		result = RiTa.pos(txt);
		answer = new String[] { "dt", "nn", "vbd", "rbr", "in", "dt", "jj", "nn", ".", "cc", "dt", "jj", "nn", "vbd",
				"jjr", "." };
		arrayEq(result, answer);

		// Tests for verb conjugation
		arrayEq(RiTa.pos("is"), new String[] { "vbz" });
		arrayEq(RiTa.pos("am"), new String[] { "vbp" });
		arrayEq(RiTa.pos("be"), new String[] { "vb" });

		result = RiTa.pos("There is a cat.");
		answer = new String[] { "ex", "vbz", "dt", "nn", "." };
		arrayEq(result, answer);

		result = RiTa.pos("There was a cat.");
		answer = new String[] { "ex", "vbd", "dt", "nn", "." };
		arrayEq(result, answer);

		result = RiTa.pos("I am a cat.");
		answer = new String[] { "prp", "vbp", "dt", "nn", "." };
		arrayEq(result, answer);

		result = RiTa.pos("I was a cat.");
		answer = new String[] { "prp", "vbd", "dt", "nn", "." };
		arrayEq(result, answer);

		arrayEq(RiTa.pos("flunk"), new String[] { "vb" });
		arrayEq(RiTa.pos("He flunks the test"), new String[] { "prp", "vbz", "dt", "nn" });

		arrayEq(RiTa.pos("he"), new String[] { "prp" });
		arrayEq(RiTa.pos("outnumber"), new String[] { "vb" });
		arrayEq(RiTa.pos("I outnumbered you"), new String[] { "prp", "vbd", "prp" });
		arrayEq(RiTa.pos("She outnumbered us"), new String[] { "prp", "vbd", "prp" });
		arrayEq(RiTa.pos("I am outnumbering you"), new String[] { "prp", "vbp", "vbg", "prp" });
		arrayEq(RiTa.pos("I have outnumbered you"), new String[] { "prp", "vbp", "vbd", "prp" });

		String[] checks = new String[] { "emphasis", "stress", "discus", "colossus", "fibrosis", "digitalis",
				"pettiness", "mess", "cleanliness", "orderliness", "bronchitis", "preparedness", "highness" };
		for (int i = 0, j = checks.length; i < j; i++) {
			// if (RiTa.pos(checks[i])[0] !== "nn")
			// console.log(checks[i] + ": " + RiTa.pos(checks[i])[0]);
			arrayEq(RiTa.pos(checks[i]), new String[] { "nn" });
		}
	}

	@Test
	public void callPosWithSimple() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("simple", true);

		arrayEq(RiTa.pos("", hm), new String[] {});
		arrayEq(RiTa.pos("biped", hm), new String[] { "n" });
		arrayEq(RiTa.pos("creed", hm), new String[] { "n" });
		arrayEq(RiTa.pos("weed", hm), new String[] { "n" });
		arrayEq(RiTa.pos("is", hm), new String[] { "v" });
		arrayEq(RiTa.pos("am", hm), new String[] { "v" });
		arrayEq(RiTa.pos("be", hm), new String[] { "v" });
		arrayEq(RiTa.pos("freed", hm), new String[] { "a" });
	}

	@Test 
	public void callPosWithInline(){
		assertEquals("1", "1", "function N/A in Java");
	}

	@Test
	public void callPosInline() {

		eq(RiTa.posInline(""), "");
		//eq(RiTa.posInline(" "), ""); // TODO: FAILING
		eq(RiTa.posInline("asdfaasd"), "asdfaasd/nn");

		String result, answer, txt;

		result = RiTa.posInline("clothes");
		answer = "clothes/nns";
		eq(result, answer);

		result = RiTa.posInline("teeth");
		answer = "teeth/nns";
		eq(result, answer);

		result = RiTa.posInline("There is a cat.");
		answer = "There/ex is/vbz a/dt cat/nn .";
		eq(result, answer);

		result = RiTa.posInline("The boy, dressed in red, ate an apple.");
		answer = "The/dt boy/nn , dressed/vbn in/in red/jj , ate/vbd an/dt apple/nn .";
		eq(result, answer);

		txt = "The dog ran faster than the other dog.  But the other dog was prettier.";
		result = RiTa.posInline(txt);
		answer = "The/dt dog/nn ran/vbd faster/rbr than/in the/dt other/jj dog/nn . But/cc the/dt other/jj dog/nn was/vbd prettier/jjr .";
		eq(result, answer);
	}

	@Test 
	public void callPosWithInlineSimple() {
		assertEquals("1", "1", "function N/A in Java");
	}

	@Test
	public void callPosInlineWithSimple() {
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put("simple", true);

		String result, answer;
		String txt;

		// posInline
		eq(RiTa.posInline("asdfaasd", hm), "asdfaasd/n");

		result = RiTa.posInline("clothes", hm);
		answer = "clothes/n";
		eq(result, answer);

		result = RiTa.posInline("teeth", hm);
		answer = "teeth/n";
		eq(result, answer);

		result = RiTa.posInline("There is a cat.", hm);
		answer = "There/- is/v a/- cat/n .";
		eq(result, answer);

		result = RiTa.posInline("The boy, dressed in red, ate an apple.", hm);
		answer = "The/- boy/n , dressed/v in/- red/a , ate/v an/- apple/n .";
		eq(result, answer);

		txt = "The dog ran faster than the other dog.  But the other dog was prettier.";
		result = RiTa.posInline(txt, hm);
		answer = "The/- dog/n ran/v faster/r than/- the/- other/a dog/n . But/- the/- other/a dog/n was/v prettier/a .";
		eq(result, answer);

		// with Map (NOT relevant for Java as we can't have different return types)

		/*
		 * pos, add inline hm.put("inline", true);
		 * 
		 * eq(RiTa.pos("", hm), ""); eq(RiTa.pos("asdfaasd", hm),
		 * "asdfaasd/n");
		 * 
		 * result = RiTa.pos("clothes", hm); answer = "clothes/n"; eq(result,
		 * answer);
		 * 
		 * result = RiTa.pos("teeth", hm); answer = "teeth/n"; eq(result,
		 * answer);
		 * 
		 * result = RiTa.pos("There is a cat.", hm); answer =
		 * "There/- is/v a/- cat/n ."; eq(result, answer);
		 * 
		 * result = RiTa.pos("The boy, dressed in red, ate an apple.", hm); answer =
		 * "The/- boy/n , dressed/v in/- red/a , ate/v an/- apple/n .";
		 * eq(result, answer);
		 * 
		 * txt =
		 * "The dog ran faster than the other dog.  But the other dog was prettier.";
		 * result = RiTa.pos(txt, hm); answer =
		 * "The/- dog/n ran/v faster/r than/- the/- other/a dog/n . But/- the/- other/a dog/n was/v prettier/a ."
		 * ; eq(result, answer);
		 */
	}

	@Test
	public void callIsAdverb() {

		assertTrue(!RiTa.isAdverb(""));
		assertTrue(!RiTa.isAdverb("swim"));
		assertTrue(!RiTa.isAdverb("walk"));
		assertTrue(!RiTa.isAdverb("walker"));
		assertTrue(!RiTa.isAdverb("beautiful"));
		assertTrue(!RiTa.isAdverb("dance"));
		assertTrue(!RiTa.isAdverb("dancing"));
		assertTrue(!RiTa.isAdverb("dancer"));

		// verb
		assertTrue(!RiTa.isAdverb("wash"));
		assertTrue(!RiTa.isAdverb("walk"));
		assertTrue(!RiTa.isAdverb("play"));
		assertTrue(!RiTa.isAdverb("throw"));
		assertTrue(!RiTa.isAdverb("drink"));
		assertTrue(!RiTa.isAdverb("eat"));
		assertTrue(!RiTa.isAdverb("chew"));

		// adj
		assertTrue(!RiTa.isAdverb("wet"));
		assertTrue(!RiTa.isAdverb("dry"));
		assertTrue(!RiTa.isAdverb("furry"));
		assertTrue(!RiTa.isAdverb("sad"));
		assertTrue(!RiTa.isAdverb("happy"));

		// n
		assertTrue(!RiTa.isAdverb("dogs"));
		assertTrue(!RiTa.isAdverb("wind"));
		assertTrue(!RiTa.isAdverb("dolls"));
		assertTrue(!RiTa.isAdverb("frogs"));
		assertTrue(!RiTa.isAdverb("ducks"));
		assertTrue(!RiTa.isAdverb("flowers"));
		assertTrue(!RiTa.isAdverb("fish"));

		// adv
		assertTrue(RiTa.isAdverb("truthfully"));
		assertTrue(RiTa.isAdverb("kindly"));
		assertTrue(RiTa.isAdverb("bravely"));
		assertTrue(RiTa.isAdverb("doggedly"));
		assertTrue(RiTa.isAdverb("sleepily"));
		assertTrue(RiTa.isAdverb("excitedly"));
		assertTrue(RiTa.isAdverb("energetically"));
		assertTrue(RiTa.isAdverb("hard")); // +adj
	}

	@Test
	public void callIsNoun() {

		// nn

		assertTrue(RiTa.isNoun("thieves"));
		assertTrue(RiTa.isNoun("calves"));
		assertTrue(RiTa.isNoun("boxes"));

		assertTrue(RiTa.isNoun("swim"));
		assertTrue(RiTa.isNoun("walk"));
		assertTrue(RiTa.isNoun("walker"));
		assertTrue(RiTa.isNoun("dance"));
		assertTrue(RiTa.isNoun("dancer"));
		assertTrue(RiTa.isNoun("cats"));
		assertTrue(RiTa.isNoun("teeth"));
		assertTrue(RiTa.isNoun("apples"));
		assertTrue(RiTa.isNoun("buses"));
		assertTrue(RiTa.isNoun("prognoses"));
		assertTrue(RiTa.isNoun("oxen"));
		assertTrue(RiTa.isNoun("theses"));
		assertTrue(RiTa.isNoun("stimuli"));
		assertTrue(RiTa.isNoun("crises"));
		assertTrue(RiTa.isNoun("duck"));
		assertTrue(RiTa.isNoun("dog"));

		// verb
		assertTrue(RiTa.isNoun("wash")); // "TODO:also false in processing -> nn" shoulbe be both Verb and Noun ??
		assertTrue(RiTa.isNoun("walk"));
		assertTrue(RiTa.isNoun("play"));
		assertTrue(RiTa.isNoun("throw"));
		assertTrue(RiTa.isNoun("drink")); // TODO:"also false in processing -> nn" shoulbe be both Verb and Noun ??

		assertTrue(!RiTa.isNoun("eat"));
		assertTrue(!RiTa.isNoun("chew"));
		assertTrue(!RiTa.isNoun("moved"));
		assertTrue(!RiTa.isNoun("went"));
		assertTrue(!RiTa.isNoun("spent"));
		assertTrue(!RiTa.isNoun("abates"));

		// adj
		assertTrue(!RiTa.isNoun("hard"));
		assertTrue(!RiTa.isNoun("dry"));
		assertTrue(!RiTa.isNoun("furry"));
		assertTrue(!RiTa.isNoun("sad"));
		assertTrue(!RiTa.isNoun("happy"));
		assertTrue(!RiTa.isNoun("beautiful"));
		assertTrue(RiTa.isNoun("wet")); // +v/adj

		// n
		assertTrue(RiTa.isNoun("dogs"));
		assertTrue(RiTa.isNoun("wind"));
		assertTrue(RiTa.isNoun("dolls"));
		assertTrue(RiTa.isNoun("frogs"));
		assertTrue(RiTa.isNoun("ducks"));
		assertTrue(RiTa.isNoun("flower"));
		assertTrue(RiTa.isNoun("fish"));

		// adv
		assertTrue(!RiTa.isNoun("truthfully"));
		assertTrue(!RiTa.isNoun("kindly"));
		assertTrue(!RiTa.isNoun("bravely"));
		assertTrue(!RiTa.isNoun("scarily"));
		assertTrue(!RiTa.isNoun("sleepily"));
		assertTrue(!RiTa.isNoun("excitedly"));
		assertTrue(!RiTa.isNoun("energetically"));
	}

	@Test
	public void callIsVerb() {
		assertTrue(RiTa.isVerb("abandons"));

		assertTrue(RiTa.isVerb("dance"));
		assertTrue(RiTa.isVerb("swim"));
		assertTrue(RiTa.isVerb("walk"));

		assertTrue(RiTa.isVerb("dances"));
		assertTrue(RiTa.isVerb("swims"));
		assertTrue(RiTa.isVerb("walks"));
		assertTrue(RiTa.isVerb("costs"));

		// inflections
		assertTrue(RiTa.isVerb("danced"));
		assertTrue(RiTa.isVerb("swam"));
		assertTrue(RiTa.isVerb("walked"));
		assertTrue(RiTa.isVerb("costed"));
		assertTrue(RiTa.isVerb("satisfies"));
		assertTrue(RiTa.isVerb("falsifies"));
		assertTrue(RiTa.isVerb("beautifies"));
		assertTrue(RiTa.isVerb("repossesses"));

		assertTrue(!RiTa.isVerb("dancer"));
		assertTrue(!RiTa.isVerb("walker"));
		assertTrue(!RiTa.isVerb("beautiful"));

		// verb
		assertTrue(RiTa.isVerb("eat"));
		assertTrue(RiTa.isVerb("chew"));

		assertTrue(RiTa.isVerb("throw")); // +n
		assertTrue(RiTa.isVerb("walk")); // +n
		assertTrue(RiTa.isVerb("wash")); // +n
		assertTrue(RiTa.isVerb("drink")); // +n

		// assertTrue(RiTa.isVerb("ducks")); // +n -> Known Issues
		assertTrue(RiTa.isVerb("fish")); // +n
		// assertTrue(RiTa.isVerb("dogs")); // +n -> Known Issues

		assertTrue(RiTa.isVerb("wind")); // +n
		assertTrue(RiTa.isVerb("wet")); // +adj
		assertTrue(RiTa.isVerb("dry")); // +adj

		// adj
		assertTrue(!RiTa.isVerb("hard"));
		assertTrue(!RiTa.isVerb("furry"));
		assertTrue(!RiTa.isVerb("sad"));
		assertTrue(!RiTa.isVerb("happy"));

		// n
		assertTrue(!RiTa.isVerb("dolls"));
		assertTrue(!RiTa.isVerb("frogs"));

		// ok
		assertTrue(RiTa.isVerb("flowers"));
		assertTrue(RiTa.isVerb("ducks"));

		// adv
		assertTrue(!RiTa.isVerb("truthfully"));
		assertTrue(!RiTa.isVerb("kindly"));
		assertTrue(!RiTa.isVerb("bravely"));
		assertTrue(!RiTa.isVerb("scarily"));
		assertTrue(!RiTa.isVerb("sleepily"));
		assertTrue(!RiTa.isVerb("excitedly"));
		assertTrue(!RiTa.isVerb("energetically"));

		assertTrue(RiTa.isVerb("hates"));
		assertTrue(RiTa.isVerb("hated"));
		assertTrue(RiTa.isVerb("hating"));
		assertTrue(RiTa.isVerb("dancing"));

		assertTrue(RiTa.isVerb("hates"));
		assertTrue(RiTa.isVerb("hated"));
		assertTrue(RiTa.isVerb("ridden"));

	}

	@Test
	public void callIsAdjective() {

		assertTrue(!RiTa.isAdjective("swim"));
		assertTrue(!RiTa.isAdjective("walk"));
		assertTrue(!RiTa.isAdjective("walker"));
		assertTrue(RiTa.isAdjective("beautiful"));
		assertTrue(!RiTa.isAdjective("dance"));
		assertTrue(!RiTa.isAdjective("dancing"));
		assertTrue(!RiTa.isAdjective("dancer"));

		// verb
		assertTrue(!RiTa.isAdjective("wash"));
		assertTrue(!RiTa.isAdjective("walk"));
		assertTrue(!RiTa.isAdjective("play"));
		assertTrue(!RiTa.isAdjective("throw"));
		assertTrue(!RiTa.isAdjective("drink"));
		assertTrue(!RiTa.isAdjective("eat"));
		assertTrue(!RiTa.isAdjective("chew"));

		// adj
		assertTrue(RiTa.isAdjective("hard"));
		assertTrue(RiTa.isAdjective("wet"));
		assertTrue(RiTa.isAdjective("dry"));
		assertTrue(RiTa.isAdjective("furry"));
		assertTrue(RiTa.isAdjective("sad"));
		assertTrue(RiTa.isAdjective("happy"));
		assertTrue(RiTa.isAdjective("kindly")); // +adv

		// n
		assertTrue(!RiTa.isAdjective("dog"));
		assertTrue(!RiTa.isAdjective("dogs"));
		assertTrue(!RiTa.isAdjective("wind"));
		assertTrue(!RiTa.isAdjective("dolls"));
		assertTrue(!RiTa.isAdjective("frogs"));
		assertTrue(!RiTa.isAdjective("ducks"));
		assertTrue(!RiTa.isAdjective("flowers"));
		assertTrue(!RiTa.isAdjective("fish"));

		// adv
		assertTrue(!RiTa.isAdjective("truthfully"));
		assertTrue(!RiTa.isAdjective("bravely"));
		assertTrue(!RiTa.isAdjective("scarily"));
		assertTrue(!RiTa.isAdjective("sleepily"));
		assertTrue(!RiTa.isAdjective("excitedly"));
		assertTrue(!RiTa.isAdjective("energetically"));
	}

	static void arrayEq(String[] a,String[] b) {
		assertArrayEquals(b, a);
	}

	static void eq(String a, String b) {
		assertEquals(b, a);
	}

}
