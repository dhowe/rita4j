package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import rita.RiTa;

public class ConjugatorTests {

	@Test
	public void callPastPart() {
		eq(RiTa.pastPart("sleep"), "slept");
		eq(RiTa.pastPart("withhold"), "withheld");

		eq(RiTa.pastPart("cut"), "cut");
		eq(RiTa.pastPart("go"), "gone");
		eq(RiTa.pastPart("swim"), "swum");
		eq(RiTa.pastPart("would"), "would");
		eq(RiTa.pastPart("might"), "might");
		eq(RiTa.pastPart("run"), "run");
		eq(RiTa.pastPart("speak"), "spoken");
		eq(RiTa.pastPart("break"), "broken");
		eq(RiTa.pastPart(""), "");

		// PROBLEMS

		eq(RiTa.pastPart("awake"), "awoken");
		eq(RiTa.pastPart("become"), "become");
		eq(RiTa.pastPart("drink"), "drunk");
		//eq(RiTa.pastPart("plead"), "pled"); fail due to "plea" is also marked "vb" in dictionary (plea can be verb?)
		eq(RiTa.pastPart("run"), "run");
		eq(RiTa.pastPart("shine"), "shone");
		// or shined
		eq(RiTa.pastPart("shrink"), "shrunk");
		// or shrunken
		eq(RiTa.pastPart("stink"), "stunk");
		eq(RiTa.pastPart("study"), "studied");

		// is already past part
		eq(RiTa.pastPart("hopped"), "hopped");
		eq(RiTa.pastPart("hated"), "hated");
		eq(RiTa.pastPart("created"), "created");
		eq(RiTa.pastPart("committed"), "committed");
		eq(RiTa.pastPart("submitted"), "submitted");
		eq(RiTa.pastPart("come"), "come");
		eq(RiTa.pastPart("forgotten"), "forgotten");
		eq(RiTa.pastPart("arisen"), "arisen");
		eq(RiTa.pastPart("eaten"), "eaten");
		eq(RiTa.pastPart("chosen"), "chosen");
		eq(RiTa.pastPart("frozen"), "frozen");
		eq(RiTa.pastPart("stolen"), "stolen");
		eq(RiTa.pastPart("worn"), "worn");
		eq(RiTa.pastPart("broken"), "broken");
		eq(RiTa.pastPart("written"), "written");
		eq(RiTa.pastPart("ridden"), "ridden");
		eq(RiTa.pastPart("drawn"), "drawn");
		eq(RiTa.pastPart("known"), "known");
		eq(RiTa.pastPart("grown"), "grown");
		eq(RiTa.pastPart("done"), "done");
		eq(RiTa.pastPart("gone"), "gone");
	}

	@Test
	public void callPresentPart() {

		eq(RiTa.presentPart("sleep"), "sleeping");
		eq(RiTa.presentPart("withhold"), "withholding");

		eq(RiTa.presentPart("cut"), "cutting");
		eq(RiTa.presentPart("go"), "going");
		eq(RiTa.presentPart("run"), "running");
		eq(RiTa.presentPart("speak"), "speaking");
		eq(RiTa.presentPart("break"), "breaking");
		eq(RiTa.presentPart("become"), "becoming");
		eq(RiTa.presentPart("plead"), "pleading");
		eq(RiTa.presentPart("awake"), "awaking");
		eq(RiTa.presentPart("study"), "studying");

		eq(RiTa.presentPart("lie"), "lying");
		eq(RiTa.presentPart("swim"), "swimming");
		eq(RiTa.presentPart("run"), "running");
		eq(RiTa.presentPart("dig"), "digging");
		eq(RiTa.presentPart("set"), "setting");
		eq(RiTa.presentPart("speak"), "speaking");
		eq(RiTa.presentPart("bring"), "bringing");
		eq(RiTa.presentPart("speak"), "speaking");

		eq(RiTa.presentPart(""), "");
	}

	@Test
	public void callConjugateVbd() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("tense", RiTa.PAST);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.FIRST);

		String c = RiTa.conjugate("go", args);
		eq(c, "went");

		args.clear();
		args.put("tense", RiTa.PAST);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.FIRST);

		String s = RiTa.conjugate("run", args);
		eq(s, "ran");
	}

	@Test
	public void callConjugate() {
		//no opts should return the verb
		eq("swim", RiTa.conjugate("swim"));
		eq("play", RiTa.conjugate("play"));

		String[] s, a;

		eq("swum", RiTa.pastPart("swim"));

		// Example of using opts
		eq(RiTa.conjugate("be", RiTa.opts("form", RiTa.GERUND)), "being");

		Map<String, Object> args = new HashMap<String, Object>();
		s = new String[] { "swim", "need", "open" };
		a = new String[] { "swims", "needs", "opens" };

		args = new HashMap<String, Object>();
		args.put("tense", RiTa.PRESENT);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PRESENT);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		args.put("passive", true);

		a = new String[] { "is swum", "is needed", "is opened" };
		for (int i = 0; i < s.length; i++) {
			eq(RiTa.conjugate(s[i], args), a[i]);
		}

		/////////////////////////////////////////////////

		args.clear();
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.FIRST);
		args.put("tense", RiTa.PAST);

		eq(RiTa.conjugate("swim", args), "swam");

		s = new String[] { "swim", "need", "open", "" };
		a = new String[] { "swam", "needed", "opened", "" };

		eq(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND);
		args.put("tense", RiTa.PAST);

		a = new String[] { "swam", "needed", "opened", "" };
		eq(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			eq(RiTa.conjugate(s[i], args), a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND);
		args.put("tense", RiTa.FUTURE);

		a = new String[] { "will swim", "will need", "will open", "" };
		eq(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			eq(RiTa.conjugate(s[i], args), a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);

		a = new String[] { "swam", "needed", "opened", "" };

		eq(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);

		a = new String[] { "to swim", "to need", "to open", "" };
		eq(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		args.put("passive", true);

		s = new String[] { "scorch", "burn", "hit", "" };
		a = new String[] { "was scorched", "was burned", "was hit", "" };
		eq(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		s = new String[] { "swim", "need", "open", "" };

		args.clear();
		args.put("tense", RiTa.PRESENT);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("progressive", true);

		a = new String[] { "to be swimming", "to be needing", "to be opening", "" };
		eq(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PRESENT);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("perfect", true);

		a = new String[] { "to have swum", "to have needed", "to have opened", "" };
		eq(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND);
		args.put("tense", RiTa.PAST);

		eq(RiTa.conjugate("barter", args), "bartered");
		eq(RiTa.conjugate("run", args), "ran");

		s = new String[] { "compete", "complete", "eject" };
		a = new String[] { "competed", "completed", "ejected" };
		eq(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			eq(c, a[i]);
		}

		String[] original = new String[] { "run", "walk", "swim", "create" };
		// passive
		String[] expected = new String[] { "is run", "is walked", "is swum", "is created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("passive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		// progressive
		expected = new String[] { "is running", "is walking", "is swimming", "is creating" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("progressive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		// interrogative
		expected = new String[] { "run", "walk", "swim", "create" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("interrogative", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//perfect
		expected = new String[] { "has run", "has walked", "has swum", "has created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("perfect", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//gerund passive
		expected = new String[] { "being run", "being walked", "being swum", "being created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.GERUND);
		args.put("passive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//gerund progressive
		expected = new String[] { "being running", "being walking", "being swimming", "being creating" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.GERUND);
		args.put("progressive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//gerund interrogative
		expected = new String[] { "running", "walking", "swimming", "creating" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.GERUND);
		args.put("interrogative", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//gerund perfect
		expected = new String[] { "having run", "having walked", "having swum", "having created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.GERUND);
		args.put("perfect", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//infinitive passive
		expected = new String[] { "to be run", "to be walked", "to be swum", "to be created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("passive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//infinitive progressive
		expected = new String[] { "to be running", "to be walking", "to be swimming", "to be creating" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("progressive", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//infinitive interrogative
		expected = new String[] { "to run", "to walk", "to swim", "to create" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("interrogative", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}
		//infinitive perfect
		expected = new String[] { "to have run", "to have walked", "to have swum", "to have created" };
		args.clear();
		args.put("person", RiTa.THIRD);
		args.put("form", RiTa.INFINITIVE);
		args.put("perfect", true);
		for (int i = 0; i < 4; i++) {
			assertEquals(expected[i], RiTa.conjugate(original[i], args));
		}

		//deal with non-base form
		args.clear();
		args.put("tense", RiTa.PRESENT);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD);
		assertEquals("walks", RiTa.conjugate("walked", args));
		assertEquals("changes", RiTa.conjugate("changed", args));
		assertEquals("spends", RiTa.conjugate("spent", args));
		assertEquals("eats", RiTa.conjugate("eaten", args));
	}
	
	@Test
	public void acceptStem() {
		Map<String, Object> argsPSPast = new HashMap<String, Object>();
		Map<String, Object> argsPSPresent = new HashMap<String, Object>();
		Map<String, Object> argsSTPresent = new HashMap<String, Object>();
		argsPSPast.put("number", RiTa.PLURAL);
		argsPSPast.put("person", RiTa.SECOND);
		argsPSPast.put("tense", RiTa.PAST);
		argsPSPresent.put("number", RiTa.PLURAL);
		argsPSPresent.put("person", RiTa.SECOND);
		argsPSPresent.put("tense", RiTa.PRESENT);
		argsSTPresent.put("number", RiTa.SINGULAR);
		argsSTPresent.put("person", RiTa.THIRD);
		argsSTPresent.put("tense", RiTa.PRESENT);

		String stem = RiTa.stem("walking");
		assertEquals("walked", RiTa.conjugate(stem, argsPSPast));
		assertEquals("walk", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("walks", RiTa.conjugate(stem, argsSTPresent));
		
		stem = RiTa.stem("writing");
		assertEquals("wrote", RiTa.conjugate(stem, argsPSPast));
		assertEquals("write", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("writes", RiTa.conjugate(stem, argsSTPresent));

		stem = RiTa.stem("asked");
		assertEquals("asked", RiTa.conjugate(stem, argsPSPast));
		assertEquals("ask", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("asks", RiTa.conjugate(stem, argsSTPresent));

		stem = RiTa.stem("admired");
		assertEquals("admired", RiTa.conjugate(stem, argsPSPast));
		assertEquals("admire", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("admires", RiTa.conjugate(stem, argsSTPresent));

		stem = RiTa.stem("cured");
		assertEquals("cured", RiTa.conjugate(stem, argsPSPast));
		assertEquals("cure", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("cures", RiTa.conjugate(stem, argsSTPresent));

		stem = RiTa.stem("studies");
		assertEquals("studied", RiTa.conjugate(stem, argsPSPast));
		assertEquals("study", RiTa.conjugate(stem, argsPSPresent));
		assertEquals("studies", RiTa.conjugate(stem, argsSTPresent));

		//random pairs
		String[][] pairs = new String[][] {
				new String[] { "accompanying", "accompanied" },
				new String[] { "feeling", "felt" },
      			new String[] { "placating", "placated" },
      			new String[] { "centralizing", "centralized" }, 
      			new String[] { "humanized", "humanized" },
      			new String[] { "boosted", "boosted" },
      			new String[] { "wearing", "wore" },
         		new String[] { "aroused", "aroused"},
      			new String[] { "rising", "rose" },
      			new String[] { "raising", "raised" },
      			new String[] { "vibrating", "vibrated" },
      			new String[] { "injection", "injected" },
				new String[] { "vibration", "vibrated" },
		};
		for (int i = 0; i < pairs.length; i++) {
			assertEquals(pairs[i][1], RiTa.conjugate(RiTa.stem(pairs[i][0]), argsPSPast));
		}
	}

	@Test
	public void callUnconjugate() {
		// regular words
    // 3rd person singular
    assertEquals(rita.Conjugator.unconjugate("plays"),"play");
    assertEquals(rita.Conjugator.unconjugate("takes"),"take");
    assertEquals(rita.Conjugator.unconjugate("gets"),"get");
    assertEquals(rita.Conjugator.unconjugate("comes"),"come");
    assertEquals(rita.Conjugator.unconjugate("goes"),"go");
    assertEquals(rita.Conjugator.unconjugate("teaches"),"teach");
    assertEquals(rita.Conjugator.unconjugate("fixes"),"fix");
    assertEquals(rita.Conjugator.unconjugate("misses"),"miss");
    assertEquals(rita.Conjugator.unconjugate("studies"),"study");
    assertEquals(rita.Conjugator.unconjugate("tries"),"try");
    assertEquals(rita.Conjugator.unconjugate("carries"),"carry");
    // ed
    assertEquals(rita.Conjugator.unconjugate("watched"),"watch");
    assertEquals(rita.Conjugator.unconjugate("planted"),"plant");
    assertEquals(rita.Conjugator.unconjugate("watered"),"water");
    assertEquals(rita.Conjugator.unconjugate("pulled"),"pull");
    assertEquals(rita.Conjugator.unconjugate("picked"),"pick");
    assertEquals(rita.Conjugator.unconjugate("liked"),"like");
    assertEquals(rita.Conjugator.unconjugate("moved"),"move");
    assertEquals(rita.Conjugator.unconjugate("tasted"),"taste");
    assertEquals(rita.Conjugator.unconjugate("tried"),"try");
    assertEquals(rita.Conjugator.unconjugate("studied"),"study");
    assertEquals(rita.Conjugator.unconjugate("carried"),"carry");
    // ing
    assertEquals(rita.Conjugator.unconjugate("blowing"),"blow");
    assertEquals(rita.Conjugator.unconjugate("raining"),"rain");
    assertEquals(rita.Conjugator.unconjugate("coming"),"come");
    assertEquals(rita.Conjugator.unconjugate("having"),"have");
    assertEquals(rita.Conjugator.unconjugate("running"),"run");
    assertEquals(rita.Conjugator.unconjugate("putting"),"put"); 
    assertEquals(rita.Conjugator.unconjugate("sitting"),"sit");

    //irregular
    assertEquals(rita.Conjugator.unconjugate("has"),"have");
    assertEquals(rita.Conjugator.unconjugate("had"),"have");
    assertEquals(rita.Conjugator.unconjugate("sat"),"sit");
    assertEquals(rita.Conjugator.unconjugate("shown"),"show");
    assertEquals(rita.Conjugator.unconjugate("ate"),"eat");
    assertEquals(rita.Conjugator.unconjugate("went"),"go");
	assertEquals(rita.Conjugator.unconjugate("met"), "meet");
	
	// base form
	assertEquals(rita.Conjugator.unconjugate("have"), "have");
	assertEquals(rita.Conjugator.unconjugate("eat"), "eat");
	assertEquals(rita.Conjugator.unconjugate("play"), "play");
	assertEquals(rita.Conjugator.unconjugate("go"), "go");
	assertEquals(rita.Conjugator.unconjugate("do"), "do");
	}

	static void eq(String a, String b) {
		eq(a, b, "");
	}

	static void eq(String a, String b, String msg) {
		assertEquals(b, a, msg);
	}

	static void eq(int a, int b){
		assertEquals(b, a);
	}
}
