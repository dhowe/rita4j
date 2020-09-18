package rita.test;

import rita.Markov;
import rita.RiTa;
import rita.RiTaException;
import rita.RandGen;
import static rita.Util.opts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.function.Function;

public class MarkovTests {
	String sample = "One reason people lie is to achieve personal power. Achieving personal power is helpful for one who pretends to be more confident than he really is. For example, one of my friends threw a party at his house last month. He asked me to come to his party and bring a date. However, I did not have a girlfriend. One of my other friends, who had a date to go to the party with, asked me about my date. I did not want to be embarrassed, so I claimed that I had a lot of work to do. I said I could easily find a date even better than his if I wanted to. I also told him that his date was ugly. I achieved power to help me feel confident; however, I embarrassed my friend and his date. Although this lie helped me at the time, since then it has made me look down on myself.";
	String sample2 = "One reason people lie is to achieve personal power. Achieving personal power is helpful for one who pretends to be more confident than he really is. For example, one of my friends threw a party at his house last month. He asked me to come to his party and bring a date. However, I did not have a girlfriend. One of my other friends, who had a date to go to the party with, asked me about my date. I did not want to be embarrassed, so I claimed that I had a lot of work to do. I said I could easily find a date even better than his if I wanted to. I also told him that his date was ugly. I achieved power to help me feel confident; however, I embarrassed my friend and his date. Although this lie helped me at the time, since then it has made me look down on myself. After all, I did occasionally want to be embarrassed.";
	String sample3 = sample + " One reason people are dishonest is to achieve power.";

	@Test
	public void testCallMarkov() {
		Markov rm = new Markov(3);
		assertTrue(rm != null);
	}

	@Test
	public void testCallCreateMarkov() {
		assertTrue(RiTa.createMarkov(3) != null);
	}

	@Test
	public void testRandomSelect() {
		double[] weights = { 1.0, 2, 6, -2.5, 0 };
		double[] expected = { 2, 2, 1.75, 1.55 };
		double[] temps = { .5, 1, 2, 10 };
		ArrayList<double[]> distrs = new ArrayList<double[]>();
		ArrayList<Double> results = new ArrayList<Double>();

		for (double t : temps) {
			double[] r = RandGen.ndist(weights, t);
			distrs.add(r);
		}

		int numTests = 100;
		int i = 0;

		for (double[] sm : distrs) {
			int sum = 0;
			for (int j = 0; j < numTests; j++) {
				sum += RandGen.pselect(sm);
			}
			double r = sum / numTests;
			results.add(r);
		}
		
		for (int j = 0; j < 4; j++) {
			assertEquals(results.get(j), expected[j], .1);			
		}
	}

	@Test
	public void testRandomndist() {
		assertThrows(RiTaException.class, () -> RandGen.ndist(new double[] { 1.0, 2, 6, -2.5, 0 }));

		double[] weights = { 2, 1 };
		double[] expected = { .666, .333 };
		double[] results = RandGen.ndist(weights);

		for (int i = 0; i < results.length; i++) {
			assertEquals(results[i], expected[i], 0.01);
		}

		double[] weights2 = { 7, 1, 2 };
		double[] expected2 = { .7, .1, .2 };
		double[] results2 = RandGen.ndist(weights2);

		for (int i = 0; i < results2.length; i++) {
			assertEquals(results2[i], expected2[i], 0.01);
		}

	}

	@Test
	public void testRandomndistTemp() {
		double[] weights = { 1.0, 2, 6, -2.5, 0 };
		double[][] expected = {
				{ 0, 0, 1, 0, 0 },
				{ 0.0066, 0.018, 0.97, 0.0002, 0.0024 },
				{ 0.064, 0.11, 0.78, 0.011, 0.039 },
				{ 0.19, 0.21, 0.31, 0.13, 0.17 },
		};
		double[][] results = {
				RandGen.ndist(weights, 0.5),
				RandGen.ndist(weights, 1),
				RandGen.ndist(weights, 2),
				RandGen.ndist(weights, 10)
		};
		for (int i = 0; i < results.length; i++) {
			double[] result = results[i];
			for (int j = 0; j < result.length; j++) {
				assertEquals(result[j], expected[i][j], 0.01);
			}
		}
	}

	@Test
	public void testInitSentence() {
		Markov rm = new Markov(4);
		String txt = "The young boy ate it. The fat boy gave up.";
		rm.addText(txt);
		Object toks = rm.initSentence();
		// TODO:initSentence()
    // assertEquals(toks.length, 1);
    // assertEquals(toks[0].token, "The");

		rm = new Markov(4);
		rm.addText(RiTa.sentences(sample));
		assertEquals(rm._flatten(rm.initSentence(new String[] { "I", "also" })), "I also");

	}

	@Test
	public void testFailedGenerate() {
		Markov rm = new Markov(4);
		rm.addText(RiTa.sentences(sample));
		assertThrows(RiTaException.class, () -> rm.generate(5));
	}

	@Test
	public void testNonEnglishSentences() {
		String text = "家 安 春 夢 家 安 春 夢 ！ 家 安 春 夢 德 安 春 夢 ？ 家 安 春 夢 安 安 春 夢 。";
		String[] sentArray = getAllRegexMatches("[^，；。？！]+[，；。？！]", text);
		Markov rm = new Markov(4);
		rm.addText(sentArray);
		Map<String, Object> hm = opts();
		hm.put("startTokens", "家");
		String[] result = rm.generate(5, hm);
		assertEquals(result.length, 5);
		for (String r : result) {
			assertTrue(r.matches("^家[^，；。？！]+[，；。？！]$"));
		}

	}

	@Test
	public void testCostomTokenizer() {
		String text = "家安春夢家安春夢！家安春夢德安春夢？家安春夢安安春夢。";
		String[] sentArray = getAllRegexMatches("[^，；。？！]+[，；。？！]", text);
		Map<String, Object> hm = opts();
		Function<String, String[]> tokenize = (sent) -> {
			return sent.split("");
		};
		Function<String[], String> untokenize = (sents) -> {
			return String.join("", sents);
		};
		hm.put("tokenize", tokenize);
		hm.put("untokenize", untokenize);
		Markov rm = new Markov(4, hm);
		rm.addText(sentArray);

		hm.clear();
		hm.put("startTokens", "家");
		String[] result = rm.generate(5, hm);
		assertEquals(result.length, 5);

		for (String r : result) {
			assertTrue(r.matches("^家[^，；。？！]+[，；。？！]$"));
		}

	}

	@Test
	public void testGenerate() {
		Map<String, Object> hm = opts("disableInputChecks", true);
		Markov rm = new Markov(4, hm);
		rm.addText(RiTa.sentences(sample));
		String[] sents = rm.generate(5);
//		System.out.println(Arrays.toString(sents));
		assertEquals(sents.length, 5);
		for (int i = 0; i < sents.length; i++) {
			String s = sents[i];
			String firstL = String.valueOf(s.charAt(0));
			assertEquals(firstL, firstL.toUpperCase());
			assertTrue(s.matches("[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
		}

		rm = new Markov(4);
		rm.addText(sample);
		String[] s = rm.generate();
		// console.log(i + ") " + s);
		String firstL = String.valueOf(s[0].charAt(0));
		assertTrue(s != null && firstL == firstL.toUpperCase());
		assertTrue(s[0].matches("[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
		int num = RiTa.tokenize(s[0]).length;
		assertTrue(num >= 5 && num <= 35);
	}

	@Test
	public void testGenerateMinMaxLength() {
		Markov rm = new Markov(4, opts("disableInputChecks", true));
		int minLength = 7;
		int maxLength = 20;
		rm.addText(RiTa.sentences(sample));
		String[] sents = rm.generate(5, opts("minLength", minLength, "maxLength", maxLength));
		assertEquals(sents.length, 5);
		for (int i = 0; i < sents.length; i++) {
			String s = sents[i];
			String firstL = String.valueOf(s.charAt(0));
			assertEquals(firstL, firstL.toUpperCase());
			assertTrue(s.matches("[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
			int num = RiTa.tokenize(s).length;
			assertTrue(num >= minLength && num <= maxLength);
		}

		rm = new Markov(4, opts("disableInputChecks", true));
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			minLength = (3 + i);
			maxLength = (10 + i);
			String s = rm.generate(opts("minLength", minLength, "maxLength", maxLength))[0];
			String firstL = String.valueOf(s.charAt(0));
			assertEquals(firstL, firstL.toUpperCase(), "FAIL: bad first char in '" + s + "'");
			assertTrue(s.matches("[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
			int num = RiTa.tokenize(s).length;
			assertTrue(num >= minLength && num <= maxLength);
		}

	}

	@Test
	public void testGenerateStart() {
		Markov rm = new Markov(4, opts("disableInputChecks", true));
		String start = "One";
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			String s = rm.generate(opts("startTokens", "start"))[0];
			assertTrue(s.startsWith(start));
		}

		start = "Achieving";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("startTokens", "start"))[0];
//			assertTrue(res instanceof String);
			assertTrue(res.startsWith(start));
		}

		start = "I";
		for (int i = 0; i < 5; i++) {
			String[] arr = rm.generate(2, opts("startTokens", "start"));
			assertEquals(arr.length, 2);
			assertTrue(arr[0].startsWith(start));
		}

	}

	@Test
	public void testGenerateStartArray() {
		Markov rm = new Markov(4, opts("disableInputChecks", true));
		String[] start = { "One" };
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			String s = rm.generate(opts("startTokens", "start"))[0];
			// console.log(i + ") " + s);
			assertTrue(s.startsWith(start[0]));
		}

		start[0] = "Achieving";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("startTokens", "start"))[0];
			assertTrue(res.startsWith(start[0]));
		}

		start[0] = "I";
		for (int i = 0; i < 5; i++) {
			String[] arr = rm.generate(2, opts("startTokens", "start"));
			assertEquals(arr.length, 2);
			assertTrue(arr[0].startsWith(start[0]));
		}

		rm = new Markov(4, opts("disableInputChecks", true));
		rm.addText(RiTa.sentences(sample));

		String[] start2 = { "One", "reason" };
		for (int i = 0; i < 1; i++) {
			String s = rm.generate(opts("startTokens", "start"))[0];
			assertTrue(s.startsWith(String.join(" ", start2)));
		}

		start2[0] = "Achieving";
		start2[1] = "personal";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("startTokens", "start"))[0];
			assertTrue(res.startsWith(String.join(" ", start2)));
		}

		start2[0] = "I";
		start2[1] = "also";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("startTokens", "start"))[0];
			assertTrue(res.startsWith(String.join(" ", start2)));
		}

	}

	@Test
	public void testGenerateMLM() {
		int mlms = 10;
		Markov rm = new Markov(3, opts("maxLengthMatch", mlms, "trace", false));
		rm.addText(RiTa.sentences(sample3));
		String[] sents = rm.generate(5);
		for (int i = 0; i < sents.length; i++) {
			String sent = sents[i];
			String[] toks = RiTa.tokenize(sent);

			// All sequences of len=N must be in text
			for (int j = 0; j <= toks.length - rm.n; j++) {
				String[] part = Arrays.copyOfRange(toks, j, j + rm.n);
				String res = RiTa.untokenize(part);
				assertTrue(sample3.indexOf(res) > -1, "output not found in text: '" + res + "'");
			}

			// All sequences of len=mlms+1 must NOT be in text
			for (int j = 0; j <= toks.length - (mlms + 1); j++) {
				String[] part = Arrays.copyOfRange(toks, j, j + (mlms + 1));
				String res = RiTa.untokenize(part);
				assertTrue(sample3.indexOf(res) < 0,
						"Got '" + sent + "'\n\nBut '" + res + "' was found in input:\n\n" + sample + "\n\n" + rm.input);
			}
		}

	}

	@Test
	public void testCompletions() {
		Markov rm = new Markov(4);
		rm.addText((sample));

		String[] res = rm.completions("people lie is".split(" "));
		assertArrayEquals(res, new String[] { "to" });

		res = rm.completions("One reason people lie is".split(" "));
		assertArrayEquals(res, new String[] { "to" });

		res = rm.completions("personal power".split(" "));
		assertArrayEquals(res, new String[] { ".", "is" });

		res = rm.completions(new String[] { "to", "be", "more" });
		assertArrayEquals(res, new String[] { "confident" });

		res = rm.completions(new String[] { "I" }); // testing the sort
		String[] expec = { "did", "claimed", "had", "said", "could",
				"wanted", "also", "achieved", "embarrassed"
		};
		assertArrayEquals(res, expec);

		res = rm.completions(new String[] { "XXX" });
		assertArrayEquals(res, new String[] { });

		// ///////////////////// ///////////////////// /////////////////////

		rm = new Markov(4);
		rm.addText((sample2));

		res = rm.completions(new String[] { "I" }, new String[] { "not" });
		assertArrayEquals(res, new String[] { "did" });

		res = rm.completions(new String[] { "achieve" }, new String[] { "power" });
		assertArrayEquals(res, new String[] { "personal" });

		res = rm.completions(new String[] { "to", "achieve" }, new String[] { "power" });
		assertArrayEquals(res, new String[] { "personal" });

		res = rm.completions(new String[] { "achieve" }, new String[] { "power" });
		assertArrayEquals(res, new String[] { "personal" });

		res = rm.completions(new String[] { "I", "did" });
		assertArrayEquals(res, new String[] { "not", "occasionally" });

		res = rm.completions(new String[] { "I", "did" }, new String[] { "want" });
		assertArrayEquals(res, new String[] { "not", "occasionally" });

	}

	@Test
	public void testProbabilities() {
		Markov rm = new Markov(3);
		rm.addText((sample));

		String[] checks = { "reason", "people", "personal", "the", "is", "XXX" };
		@SuppressWarnings("unchecked")
		Map<String, Object>[] expected = new HashMap[6];
		
		expected[0] = opts("people", 1.0);
		expected[1] = opts("lie", 1.0);
		expected[2] = opts("power", 1.0);
		expected[3] = opts("time", 0.5,"party",0.5);
		expected[4] = opts("to", 0.3333333333333333, ".", 0.3333333333333333, "helpful", 0.3333333333333333);
		expected[5] = opts();

		for (int i = 0; i < checks.length; i++) {
			Map<String, Object> res = rm.probabilities(checks[i]);
			//System.out.println("MarkovTests.testProbabilities():" + checks[i] + " expect: " + expected[i].toString() + " real: " + res.toString());
			eql(res, expected[i]);
		}

	}

	@Test
	public void testProbabilitiesArray() {
		Markov rm = new Markov(4);
		rm.addText(sample2);

		Map<String, Object> res = rm.probabilities("the".split(" "));
		Map<String, Object> expec = opts();
		expec.put("time", 0.5);
		expec.put("party", 0.5);
		eql(res, expec);

		res = rm.probabilities("people lie is".split(" "));
		expec.clear();
		expec.put("to", 1.0);
		eql(res, expec);

		res = rm.probabilities("is");
		expec.clear();
		expec.put("to", 0.3333333333333333);
		expec.put(".", 0.3333333333333333);
		expec.put("helpful", 0.3333333333333333);
		eql(res, expec);

		res = rm.probabilities("personal power".split(" "));
		expec.clear();
		expec.put("is", 0.5);
		expec.put(".", 0.5);
		eql(res, expec);

		res = rm.probabilities(new String[] { "to", "be", "more" });
		expec.clear();
		expec.put("confident", 1.0);
		eql(res, expec);

		res = rm.probabilities("XXX");
		expec.clear();
		eql(res, expec);

		res = rm.probabilities(new String[] { "personal", "XXX" });
		eql(res, expec);

		res = rm.probabilities(new String[] { "I", "did" });
		expec.clear();
		expec.put("not", 0.6666666666666666);
		expec.put("occasionally", 0.3333333333333333);
		eql(res, expec);
	}

	@Test
	public void testProbability() {
		String text = "the dog ate the boy the";
		Markov rm = new Markov(3);
		rm.addText(text);
		assertEquals(rm.probability("dog"), (double) 1 / 6);
		assertEquals(rm.probability("cat"), 0);
		assertEquals(rm.probability("the"), .5);

		text = "the dog ate the boy that the dog found.";
		rm = new Markov(3);
		rm.addText(text);

		assertEquals(rm.probability("the"), .3);
		assertEquals(rm.probability("dog"), .2);
		assertEquals(rm.probability("cat"), 0);

		rm = new Markov(3);
		rm.addText(sample);
		assertEquals(rm.probability("power"), 0.017045454545454544);

	}

	@Test
	public void testProbabilityArray() {
		Markov rm = new Markov(3);
		rm.addText(sample);
		float expected = 0;

		String[] check = "personal power is".split(" ");
		expected = (float) 1 / 3;
		//System.out.println("MarkovTests.testProbabilityArray(): " + check.toString() + " real :" + rm.probability(check));
		assertEquals(rm.probability(check), expected);

		check = "personal powXer is".split(" ");
		expected = (float) 0;
		//System.out.println("MarkovTests.testProbabilityArray(): " + check.toString() + " real :" + rm.probability(check));
		assertEquals(rm.probability(check), expected);

		check = "someone who pretends".split(" ");
		expected = (float) 1 / 2;
		//System.out.println("MarkovTests.testProbabilityArray(): " + check.toString() + " real :" + rm.probability(check));
		assertEquals(rm.probability(check), expected);

		assertEquals(rm.probability(new String[] { }), 0);

	}

	@Test
	public void testAddText() {
		Markov rm = new Markov(4);
		String[] sents = RiTa.sentences(sample);
		int count = 0; // should not include <s> and <s/> coz by default Markov.size() don't count them
		for (int i = 0; i < sents.length; i++) {
			String[] words = RiTa.tokenize(sents[i]);
			count += words.length;
		}
		rm.addText(sents);

		assertEquals(rm.size(), count);

		// TODO:
//    String[] ss = rm.root.child(Markov.SS);
//    String[] result = {"One", "Achieving", "For", "He", "However", "I", "Although"};
//    assertArrayEquals(Object.keys(ss.children), result);
//
//    String[] se = rm.root.child(Markov.SE);
//    assertArrayEquals(Object.keys(se.children), new String[] {Markov.SS});

	}

	@Test
	public void testToString() {
		Markov rm = new Markov(2);
		String exp = "ROOT { \"<s>\" [1,p=0.143] { \"The\" [1,p=1.000] } \"The\" [1,p=0.143] { \"dog\" [1,p=1.000] } \"dog\" [1,p=0.143] { \"ate\" [1,p=1.000] } \"ate\" [1,p=0.143] { \"the\" [1,p=1.000] } \"the\" [1,p=0.143] { \"cat\" [1,p=1.000] } \"cat\" [1,p=0.143] { \"</s>\" [1,p=1.000] } \"</s>\" [1,p=0.143] }";
		rm.addText("The dog ate the cat");
		String output = rm.toString().replaceAll("\n", " ").replaceAll(" +", " ");
		//System.out.println("exp:" + exp);
		//System.out.println("real:" + output);
		assertEquals(exp, output);
	}

	@Test
	public void testSize() {

		Markov rm = new Markov(4);
		assertEquals(rm.size(), 0);
		String[] tokens = RiTa.tokenize(sample);
		String[] sents = RiTa.sentences(sample);
		rm = new Markov(3);
		rm.addText(sample);
		assertEquals(rm.size(), tokens.length + sents.length * 2);

		Markov rm2 = new Markov(4);
		rm2 = new Markov(3);
		rm2.addText(sents);
		assertEquals(rm.size(), rm2.size());

	}

	@Test
	public void testFailForSentenceInput() {
		Markov rm = new Markov(4, opts("disableInputChecks", false));
		rm.addText(new String[] { "I ate the dog." });
		assertThrows(RiTaException.class, () -> rm.generate());
	}

	@Test
	public void testDisableInputChecks() {
		Markov rm = new Markov(4, opts("disableInputChecks", false));
		rm.addText("I ate the dog.");
		assertTrue(rm.input instanceof Object);

		rm = new Markov(4, opts("disableInputChecks", true));
		rm.addText("I ate the dog.");
		assertTrue(rm.input == null);
	}

	@Test
	public void testSerializeAndDeserialize() {
		Markov rm = new Markov(4, opts("disableInputChecks", true));
		rm.addText(new String[] { "I ate the dog." });
		// TODO: fromJSON
		// Markov copy = Markov.fromJSON(rm.toJSON());
//		markovEquals(rm, copy);
//		assertEquals(copy.generate(), rm.generate());
	}

	/* Helpers */

	private void eql(Map<String, Object> result, Map<String, Object> opts) {
		assertTrue(result.equals(opts));
	}

	private String[] getAllRegexMatches(String regexPattern, String input) {
		Pattern pattern = Pattern.compile(regexPattern);
		List<String> list = new ArrayList<String>();
		Matcher m = pattern.matcher(input);
		while (m.find()) {
			list.add(m.group());
		}
		String[] result = new String[list.size()];
		result = list.toArray(result);
		return result;
	}

	private void markovEquals(Markov rm, Markov copy) {
		assertTrue(rm.equals(copy));
		assertEquals(rm.toString(), copy.toString());
		// TODO:root
//		assertEquals(rm.root.toString(), copy.root.toString());
		assertEquals(rm.input, copy.input);
		assertEquals(rm.size(), copy.size());
	}

	public String toString() {
		return "";
	}

}
