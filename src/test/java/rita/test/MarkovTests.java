package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static rita.RiTa.opts;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import rita.*;

public class MarkovTests {
	String sample = "One reason people lie is to achieve personal power. Achieving personal power is helpful for one who pretends to be more confident than he really is. For example, one of my friends threw a party at his house last month. He asked me to come to his party and bring a date. However, I did not have a girlfriend. One of my other friends, who had a date to go to the party with, asked me about my date. I did not want to be embarrassed, so I claimed that I had a lot of work to do. I said I could easily find a date even better than his if I wanted to. I also told him that his date was ugly. I achieved power to help me feel confident; however, I embarrassed my friend and his date. Although this lie helped me at the time, since then it has made me look down on myself.";
	String sample2 = "One reason people lie is to achieve personal power. Achieving personal power is helpful for one who pretends to be more confident than he really is. For example, one of my friends threw a party at his house last month. He asked me to come to his party and bring a date. However, I did not have a girlfriend. One of my other friends, who had a date to go to the party with, asked me about my date. I did not want to be embarrassed, so I claimed that I had a lot of work to do. I said I could easily find a date even better than his if I wanted to. I also told him that his date was ugly. I achieved power to help me feel confident; however, I embarrassed my friend and his date. Although this lie helped me at the time, since then it has made me look down on myself. After all, I did occasionally want to be embarrassed.";
	String sample3 = sample + " One reason people are dishonest is to achieve power.";
	String sample4 = "The Sun is a barren, rocky world without air and water. It has dark lava on its surface. The Sun is filled with craters. It has no light of its own. It gets its light from the Sun. The Sun keeps changing its shape as it moves around the Sun. It spins on its Sun in 273 days. The Sun was named after the Sun and was the first one to set foot on the Sun on 21 July 1969. They reached the Sun in their space craft named the Sun. The Sun is a huge ball of gases. It has a diameter of two km. It is so huge that it can hold millions of planets inside it. The Sun is mainly made up of hydrogen and helium gas. The surface of the Sun is known as the Sun surface. The Sun is surrounded by a thin layer of gas known as the chromospheres. Without the Sun, there would be no life on the Sun. There would be no plants, no animals and no Sun. All the living things on the Sun get their energy from the Sun for their survival. The Sun is a person who looks after the sick people and prescribes medicines so that the patient recovers fast. In order to become a Sun, a person has to study medicine. The Sun lead a hard life. Its life is very busy. The Sun gets up early in the morning and goes in circle. The Sun works without taking a break. The Sun always remains polite so that we feel comfortable with it. Since the Sun works so hard we should realise its value. The Sun is an agricultural country. Most of the people on the Sun live in villages and are farmers. The Sun grows cereal, vegetables and fruits. The Sun leads a tough life. The Sun gets up early in the morning and goes in circles. The Sun stays and work in the sky until late evening. The Sun usually lives in a dark house. Though the Sun works hard it remains poor. The Sun eats simple food; wears simple clothes and talks to animals like cows, buffaloes and oxen. Without the Sun there would be no cereals for us to eat. The Sun plays an important role in the growth and economy of the sky.";

	@Test
	public void callConstructor() {
		RiMarkov rm = new RiMarkov(3);
		assertTrue(rm != null);
		assertTrue(rm.n == 3);
	}

	@Test
	public void callRiMarkov() {
		RiMarkov rm = new RiMarkov(3);
		assertTrue(rm != null);
		assertEquals(0, rm.size());

		// should throw when options conflict
		assertThrows(RiTaException.class, () -> {
			RiMarkov r = new RiMarkov(3, opts("maxLengthMatch", 2));
		});
	}

	@Test
	public void callRiTaMarkov(){
		RiMarkov rm = RiTa.markov(3);
		assertTrue(rm != null);
		assertEquals(0, rm.size());

		rm = RiTa.markov(3, opts("text", "The dog ran away"));
		assertEquals(4, rm.size());

		RiMarkov rm1 = RiTa.markov(3, opts("text", ""));
		assertEquals(0, rm1.size());
		assertThrows(RiTaException.class, () ->{rm1.generate();});

		rm = RiTa.markov(3,opts("text", sample));
		assertTrue(rm.generate().length > 0);

		RiMarkov rm2 = RiTa.markov(3, opts("text", "Too short."));
		assertThrows(RiTaException.class, () -> {rm2.generate();});

		assertThrows(RiTaException.class, () -> {
			RiMarkov rm3 = RiTa.markov(3, opts("text", 1));
		});

		assertThrows(RiTaException.class, () -> {
			RiMarkov rm4 = RiTa.markov(3, opts("text", false));
		});
		
		rm = RiTa.markov(3, opts("text", new String[]{"Sentence one.", "Sentence two."}));
		assertEquals(6, rm.size());

		rm = RiTa.markov(3, opts("text", RiTa.sentences(sample)));
		assertTrue(rm.generate().length > 0);

		assertThrows(RiTaException.class, () ->{ RiMarkov r1 = RiTa.markov(1);});
		assertThrows(RiTaException.class, () ->{ RiMarkov r1 = RiTa.markov(3, opts("maxLengthMatch", 2));});
	}

	@Test
	public void callRandompSelect() {
		assertEquals(0, RandGen.pselect(new double[] {1}));
		double[] weights = { 1.0, 2, 6, -2.5, 0 };
		double[] expected = { 2, 2, 1.75, 1.55 }; 
		double[] temps = { .5, 1, 2, 10 };
		List<double[]> distrs = new ArrayList<double[]>();
		List<Double> results = new ArrayList<Double>();

		for (double t : temps) {
			double[] r = RandGen.ndist(weights, t);
			distrs.add(r);
		}

		int numTests = 100;
		distrs.forEach(sm -> {
			double sum = 0;
			for (int j = 0; j < numTests; j++) {
				sum += RandGen.pselect(sm);
			}
			results.add(sum / numTests);
		});

		assertTrue(Math.abs(results.get(0) - expected[0]) < 0.1);
		assertTrue(Math.abs(results.get(1) - expected[1]) < 0.2);
		assertTrue(Math.abs(results.get(2) - expected[2]) < 0.4);
		assertTrue(Math.abs(results.get(3) - expected[3]) < 1);

		// TODO: RandGen.pselect2()

		// double[][] distr = new double[][] {{1, 2, 3, 4}, {0.1, 0.2, 0.3, 0.4}, {0.2, 0.3, 0.4, 0.5}};
		// expected = new double[] {3, 0.3, 0.3857};
		// for (int k = 0; k < 10; k++) {
		// 	List<Double> res = new ArrayList<Double>();
		// 	Arrays.asList(distr).forEach((sm) ->{
		// 		double sum = 0;
		// 		for (int j = 0; j < 1000; j++) {
		// 			sum += RandGen.pselect2(sm);
		// 		}
		// 		res.add(sum/1000);
		// 	});
		// 	assertTrue(Math.abs(res.get(0) - expected[0]) < 0.5);
		// 	assertTrue(Math.abs(res.get(1) - expected[1]) < 0.05);
		// 	assertTrue(Math.abs(res.get(2) - expected[2]) < 0.05);
		// }
	}

	@Test
	public void callRandGenNdist() {
		assertThrows(RiTaException.class, () -> RandGen.ndist(new double[] { 1.0, 2, 6, -2.5, 0 }));

		double[] weights = { 2, 1 };
		double[] expected = { .666, .333 };
		double[] results = RandGen.ndist(weights);

		for (int i = 0; i < results.length; i++) {
			eq(results[i], expected[i], 0.01);
		}

		double[] weights2 = { 7, 1, 2 };
		double[] expected2 = { .7, .1, .2 };
		double[] results2 = RandGen.ndist(weights2);

		for (int i = 0; i < results2.length; i++) {
			eq(results2[i], expected2[i], 0.01);
		}

		double[] weights3 = { 1, 23, 2, 34, 5 };
		double[] expected3 = { (double) 1 / 65, (double) 23 / 65, (double) 2 / 65, (double) 34 / 65, (double) 5 / 65 };
		double[] results3 = RandGen.ndist(weights3);
		for (int i = 0; i < results2.length; i++) {
			eq(results3[i], expected3[i], 0.01);
		}

		double[] weights4 = { 1, -1 };
		double[] expected4 = { (double) 2.718281 / 3.086159, (double) 0.367879 / 3.086159 };
		double[] results4 = RandGen.ndist(weights4, 1);
		for (int i = 0; i < results4.length; i++) {
			eq(results4[i], expected4[i], 0.01);
		}

		double[] weights5 = { 1, 2, -1 };
		double[] expected5 = { (double) 2.718281 / 10.475217, (double) 7.389056 / 10.475217, (double) 0.367879 / 10.475217 };
		double[] results5 = RandGen.ndist(weights5, 1);
		for (int i = 0; i < results5.length; i++) {
			eq(results5[i], expected5[i], 0.01);
		}

	}

	@Test
	public void callRandomndistTemp() {
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
				eq(result[j], expected[i][j], 0.01);
			}
		}
	}

	// @Test
	// public void callInitSentence() {
	// 	RiMarkov rm = new RiMarkov(4);
	// 	String txt = "The young boy ate it. The fat boy gave up.";
	// 	rm.addText(txt);
	// 	RiMarkov.Node[] toks = rm.initSentence();
	// 	eq(toks.length, 1);
	// 	eq(toks[0].token, "The");

	// 	rm = new RiMarkov(4);
	// 	rm.addText(RiTa.sentences(sample));
	// 	toks = rm.initSentence(new String[] { "I", "also" });
	// 	eq(toks.length, 2);
	// 	eq(toks[0].token + " " + toks[1].token, new String("I also"));
	// }

	@Test
	public void throwOnGenerateForEmptyModel(){
		RiMarkov rm = new RiMarkov(4, opts("maxLengthMatch", 6));
		assertThrows(RiTaException.class, () -> {
			rm.generate(5);
		});
	}

	@Test
	public void throwOnFailedGenerate() {
		RiMarkov rm = new RiMarkov(4, opts("maxLengthMatch", 6));
		rm.addText(RiTa.sentences(sample));
		assertThrows(RiTaException.class, () ->{rm.generate(5);});

		RiMarkov rm1 = new RiMarkov(4, opts("maxLengthMatch", 5));
		rm1.addText(RiTa.sentences(sample));
		assertThrows(RiTaException.class, () -> {rm.generate(5);});

		RiMarkov rm2 = new RiMarkov(4, opts("maxAttempts", 1));
		rm2.addText("This is a text that is too short.");
		assertThrows(RiTaException.class, () -> rm2.generate(5));
	}

	@Test
	public void splitOnCustomTokenizers() {
		String[] sents = new String[] {"asdfasdf-", "aqwerqwer+", "asdfasdf*"};
		Function<String, String[]> tokenizer = (String sent) -> {
			return sent.split("");
		};
		Function<String[], String> untokenizer = (String[] sens) -> {
			return String.join("", sens);
		};

		RiMarkov rm = new RiMarkov(4, opts("tokenize", tokenizer, "untokenize", untokenizer));
		rm.addText(sents);

		String[] se = rm.sentenceEnds.toArray(String[]::new);
		assertArrayEquals(new String[]{"*", "+", "-"}, se);

		// String[] res = rm._splitEnds(String.join("", sents));
		// assertArrayEquals(sents, res);
	}

	@Test
	public void applyCustomTokenizers() {
		String[] sents = new String[] {"asdfasdf-", "aqwerqwer+", "asdfasdf*"};
		Function<String, String[]> tokenizer = (String sent) -> {
			return sent.split("");
		};
		Function<String[], String> untokenizer = (String[] sens) -> {
			return String.join("", sens);
		};
		RiMarkov rm = new RiMarkov(4, opts("tokenize", tokenizer, "untokenize", untokenizer));
		rm.addText(sents);

		assertArrayEquals(new String[] {"a", "a", "a"}, rm.sentenceStarts.toArray(String[]::new));
		assertTrue(rm.sentenceEnds.size() == 3);
		assertTrue(rm.sentenceEnds.contains("-"));
		assertTrue(rm.sentenceEnds.contains("+"));
		assertTrue(rm.sentenceEnds.contains("*"));

		String[] res = rm.generate(2, opts("seed", "as", "maxLength", 20));
		assertEquals(2, res.length);
		assertTrue(Pattern.compile("^as.*[-=*]$").matcher(res[0]).matches());
	}

	@Test
	public void generateNonEnglishSentences() {
		String text = "家 安 春 夢 家 安 春 夢 ！ 家 安 春 夢 德 安 春 夢 ？ 家 安 春 夢 安 安 春 夢 。";
		String[] sentArray = getAllRegexMatches("[^，；。？！]+[，；。？！]", text);
		RiMarkov rm = new RiMarkov(4);
		rm.addText(sentArray);
		Map<String, Object> hm = opts();
		hm.put("seed", "家");
		String[] result = rm.generate(5, hm);
		eq(result.length, 5);
		for (String r : result) {
			assertTrue(r.matches("^[^，；。？！]+[，；。？！]$"));
		}

	}

	@Test
	public void callGenerate() {
		Map<String, Object> hm = opts("disableInputChecks", true);
		RiMarkov rm = new RiMarkov(4, hm);
		rm.addText(RiTa.sentences(sample));
		String[] sents = rm.generate(5);
		eq(sents.length, 5);
		for (int i = 0; i < sents.length; i++) {
			String s = sents[i];
			//console.log(i+") " + s);
			String firstL = String.valueOf(s.charAt(0));
			eq(firstL, firstL.toUpperCase());
			assertTrue(s.matches("(.*)[?.!]$"), "FAIL: bad last char in \"" + s + "\"");
		}

		rm = new RiMarkov(4);
		rm.addText(sample);
		String[] s = rm.generate();
		//console.log("X) " + s[0]);
		String firstL = String.valueOf(s[0].charAt(0));
		assertTrue(s != null && firstL == firstL.toUpperCase());
		assertTrue(s[0].matches("(.*)[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
		int num = RiTa.tokenize(s[0]).length;
		assertTrue(num >= 5 && num <= 35);
	}

	@Test
	public void callGenerateMinMaxLength() {
		RiMarkov rm = new RiMarkov(4, opts("disableInputChecks", true));
		int minLength = 7;
		int maxLength = 20;
		rm.addText(RiTa.sentences(sample));
		String[] sents = rm.generate(5, opts("minLength", minLength, "maxLength", maxLength));
		eq(sents.length, 5);
		for (int i = 0; i < sents.length; i++) {
			String s = sents[i];
			String firstL = String.valueOf(s.charAt(0));
			eq(firstL, firstL.toUpperCase());
			assertTrue(s.matches("(.*)[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
			int num = RiTa.tokenize(s).length;
			assertTrue(num >= minLength && num <= maxLength);
		}

		rm = new RiMarkov(4, opts("disableInputChecks", true));
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			minLength = (3 + i);
			maxLength = (10 + i);
			String s = rm.generate(opts("minLength", minLength, "maxLength", maxLength))[0];
			String firstL = String.valueOf(s.charAt(0));
			eq(firstL, firstL.toUpperCase(), "FAIL: bad first char in '" + s + "'");
			assertTrue(s.matches("(.*)[!?.]$"), "FAIL: bad last char in \"" + s + "\"");
			int num = RiTa.tokenize(s).length;
			assertTrue(num >= minLength && num <= maxLength);
		}

	}

	@Test
	public void callGenerateStart() {
		RiMarkov rm = new RiMarkov(4, opts("disableInputChecks", true));
		String start = "One";
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			String s = rm.generate(opts("seed", start))[0];
			assertTrue(s.startsWith(start));
		}

		start = "Achieving";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("seed", start))[0];
			//			assertTrue(res instanceof String);
			assertTrue(res.startsWith(start));
		}

		start = "I";
		for (int i = 0; i < 5; i++) {
			String[] arr = rm.generate(2, opts("seed", start));
			eq(arr.length, 2);
			assertTrue(arr[0].startsWith(start));
		}

	}

	@Test
	public void callGenerateStartArray() {
		RiMarkov rm = new RiMarkov(4, opts("disableInputChecks", true));
		String[] start = { "One" };
		rm.addText(RiTa.sentences(sample));
		for (int i = 0; i < 5; i++) {
			String s = rm.generate(opts("seed", start))[0];
			// console.log(i + ") " + s);
			assertTrue(s.startsWith(start[0]));
		}

		start[0] = "Achieving";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("seed", start))[0];
			assertTrue(res.startsWith(start[0]));
		}

		start[0] = "I";
		for (int i = 0; i < 5; i++) {
			String[] arr = rm.generate(2, opts("seed", start));
			eq(arr.length, 2);
			assertTrue(arr[0].startsWith(start[0]));
		}

		rm = new RiMarkov(4, opts("disableInputChecks", true));
		rm.addText(RiTa.sentences(sample));

		String[] start2 = { "One", "reason" };
		for (int i = 0; i < 1; i++) {
			String s = rm.generate(opts("seed", start2))[0];
			assertTrue(s.startsWith(String.join(" ", start2)));
		}

		start2[0] = "Achieving";
		start2[1] = "personal";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("seed", start2))[0];
			assertTrue(res.startsWith(String.join(" ", start2)));
		}

		start2[0] = "I";
		start2[1] = "also";
		for (int i = 0; i < 5; i++) {
			String res = rm.generate(opts("seed", start2))[0];
			assertTrue(res.startsWith(String.join(" ", start2)));
		}
	}

	@Test
	public void callGenerateMLM() {
		int mlms = 10;
		RiMarkov rm = new RiMarkov(3, opts("maxLengthMatch", mlms, "trace", false));
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

		mlms = 12;
		rm = new RiMarkov(3, opts("maxLengthMatch", mlms, "trace", false));
		rm.addText(RiTa.sentences(sample2));
		sents = rm.generate(5);
		for (int i = 0; i < sents.length; i++) {
			String sent = sents[i];
			String[] toks = RiTa.tokenize(sent);
			for (int j = 0; j <= toks.length - rm.n; j++) {
				String[] part = Arrays.copyOfRange(toks, j, j + rm.n);
				String res = RiTa.untokenize(part);
				assertTrue(sample2.indexOf(res) > -1, "output not found in text: '" + res + "'");
			}
			for (int j = 0; j <= toks.length - (mlms + 1); j++) {
				String[] part = Arrays.copyOfRange(toks, j, j + (mlms + 1));
				String res = RiTa.untokenize(part);
				assertTrue(sample2.indexOf(res) < 0,
						"Got '" + sent + "'\n\nBut '" + res + "' was found in input:\n\n" + sample + "\n\n" + rm.input);
			}
		}

	}

	@Test
	public void callCompletions() {
		RiMarkov rm = new RiMarkov(4);
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
		String[] expec = { "did", "achieved", "also", "claimed", "could",
				"embarrassed", "had", "said", "wanted"
		}; // first sort by probability, then by alphabet
		assertArrayEquals(res, expec);

		res = rm.completions(new String[] { "XXX" });
		assertArrayEquals(res, new String[] { });

		// ///////////////////// ///////////////////// /////////////////////

		rm = new RiMarkov(4);
		rm.addText((sample2));

		res = rm.completions(new String[] { "I" }, new String[] { "not" });
		assertArrayEquals(res, new String[] { "did" });

		res = rm.completions(new String[] { "achieve" }, new String[] { "power" });
		assertArrayEquals(res, new String[] { "personal" });

		res = rm.completions(new String[] { "to", "achieve" }, new String[] { "power" });
		assertArrayEquals(res, new String[] { "personal" });

		res = rm.completions(new String[] { "He" }, new String[] { "me" });
		assertArrayEquals(res, new String[] { "asked" });

		res = rm.completions(new String[] { "I", "did" });
		assertArrayEquals(res, new String[] { "not", "occasionally" });

		res = rm.completions(new String[] { "I", "did" }, new String[] { "want" });
		assertArrayEquals(res, new String[] { "not", "occasionally" });

	}

	@Test
	public void callProbabilities() {
		RiMarkov rm = new RiMarkov(3);
		rm.addText((sample));

		String[] checks = { "reason", "people", "personal", "the", "is", "XXX" };
		@SuppressWarnings("unchecked")
		Map<String, Object>[] expected = new HashMap[6];

		expected[0] = opts("people", 1.0);
		expected[1] = opts("lie", 1.0);
		expected[2] = opts("power", 1.0);
		expected[3] = opts("time", 0.5, "party", 0.5);
		expected[4] = opts("to", 0.3333333333333333, ".", 0.3333333333333333, "helpful", 0.3333333333333333);
		expected[5] = opts();

		for (int i = 0; i < checks.length; i++) {
			Map<String, Object> res = rm.probabilities(checks[i]);
			eql(res, expected[i]);
		}

	}

	@Test
	public void callProbabilitiesArray() {
		RiMarkov rm = new RiMarkov(4);
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
	public void callProbability() {
		String text = "the dog ate the boy the";
		RiMarkov rm = new RiMarkov(3);
		rm.addText(text);
		eq(rm.probability("dog"), (double) 1 / 6);
		eq(rm.probability("cat"), 0.0);
		eq(rm.probability("the"), .5);

		text = "the dog ate the boy that the dog found.";
		rm = new RiMarkov(3);
		rm.addText(text);

		eq(rm.probability("the"), .3);
		eq(rm.probability("dog"), .2);
		eq(rm.probability("cat"), 0.0);

		rm = new RiMarkov(3);
		rm.addText(sample);
		eq(rm.probability("power"), 0.017045454545454544);

	}

	@Test
	public void callProbabilityArray() {
		RiMarkov rm = new RiMarkov(3);
		rm.addText(sample);
		float expected = 0;

		String[] check = "personal power is".split(" ");
		expected = (float) 1 / 3;
		eq(rm.probability(check), expected);

		check = "personal powXer is".split(" ");
		expected = (float) 0;
		eq(rm.probability(check), expected);

		check = "someone who pretends".split(" ");
		expected = (float) 1 / 2;
		eq(rm.probability(check), expected);

		eq(rm.probability(new String[] { }), 0);
	}

	/* TODO: ?
	@Test
	public void callAddTokens() {
		Markov rm = new Markov(4);
		String[] tokens = RiTa.tokenize(sample);
		rm.addTokens(tokens);
		eq(rm.size(), tokens.length);
	} */

	@Test
	public void callAddText() {
		RiMarkov rm = new RiMarkov(4);
		String[] sents = RiTa.sentences(sample);
		int count = sents.length;
		for (int i = 0; i < sents.length; i++) {
			String[] words = RiTa.tokenize(sents[i]);
			count += words.length;
		}
		rm.addText(sents);

		eq(rm.size(), count + sents.length);

		//TODO:
		//		Node child = rm.root.child(Markov.SS);
		//		String[] result = { "One", "Achieving", "For", "He", "However", "I", "Although" };
		//		assertArrayEquals(Object.keys(child.), result);
		//
		//		String[] se = rm.root.child(Markov.SE);
		//		assertArrayEquals(Object.keys(se.children), new String[] { Markov.SS });

	}

	@Test
	public void callNodeChildCount() {
		RiMarkov rm = new RiMarkov(2);
		eq(0, rm.root.childCount());

		rm = new RiMarkov(2);
		rm.addText("The");
		eq(3, rm.root.childCount());
		eq(1, rm.root.child("The").childCount());
	}

	@Test
	public void callToString() {

		RiMarkov rm;
		String exp;

		rm = new RiMarkov(2);
		exp = "ROOT {   'The' [1,p=0.333]  {     '</s>' [1,p=1.000]   }   '<s>' [1,p=0.333]  {     'The' [1,p=1.000]   }   '</s>' [1,p=0.333] }";
		rm.addText("The");
		//console.log(exp +"\n"+ rm.toString().replaceAll("\n", " "));
		eq(exp, rm.toString().replaceAll("\n", " "));

		rm = new RiMarkov(2);
		exp = "ROOT {   'The' [1,p=0.143]  {     'dog' [1,p=1.000]   }   'the' [1,p=0.143]  {     'cat' [1,p=1.000]   }   'dog' [1,p=0.143]  {     'ate' [1,p=1.000]   }   'cat' [1,p=0.143]  {     '</s>' [1,p=1.000]   }   'ate' [1,p=0.143]  {     'the' [1,p=1.000]   }   '<s>' [1,p=0.143]  {     'The' [1,p=1.000]   }   '</s>' [1,p=0.143] }";
		rm.addText("The dog ate the cat");
		//console.log(rm.toString());
		eq(exp, rm.toString().replaceAll("\n", " "));
	}

	@Test
	public void callSize() {

		RiMarkov rm = new RiMarkov(4);
		eq(rm.size(), 0);
		String[] tokens = RiTa.tokenize(sample);
		String[] sents = RiTa.sentences(sample);
		rm = new RiMarkov(3);
		rm.addText(sample);
		eq(rm.size(), tokens.length + sents.length * 2);

		RiMarkov rm2 = new RiMarkov(4);
		rm2 = new RiMarkov(3);
		rm2.addText(sents);
		eq(rm.size(), rm2.size());

	}

	@Test
	public void failForSentenceInput() {
		RiMarkov rm = new RiMarkov(4);
		rm.addText(new String[] { "I ate the dog." });
		//rm.generate();
		assertThrows(RiTaException.class, () -> rm.generate());
	}

	@Test
	public void handleDisableInputChecks() {
		RiMarkov rm = new RiMarkov(4, opts("disableInputChecks", false));
		rm.addText("I ate the dog.");
		assertTrue(rm.input instanceof Object);

		rm = new RiMarkov(4, opts("disableInputChecks", true));
		rm.addText("I ate the dog.");
		assertTrue(rm.input == null);
	}

//	@Test
//	public void serializeAndDeserialize() {
//		Markov rm = new Markov(4, opts("disableInputChecks", true));
//		rm.addText(new String[] { "I ate the dog." });
//		Markov copy = Markov.fromJSON(rm.toJSON());
//		assertEquals(rm, copy);
//		assertEquals(copy.generate(), rm.generate());
//	}

	/* Helpers */

	private void eql(Map<String, Object> result, Map<String, Object> opts) {
		assertTrue(result.equals(opts));
	}

	static void eq(String a, String b) {
		eq(a, b, null);
	}

	static void eq(String a, String b, String m) {
		assertEquals(b, a, m);
	}

	static void eq(Double a, Double b) {
		eq(a, b, 0.0);
	}

	static void eq(int a, int b) {
		assertEquals(b, a);
	}

	static void eq(float a, float b) {
		assertEquals(b, a);
	}

	static void eq(Double a, Double b, Double d) {
		assertEquals(b, a, d);
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

}
