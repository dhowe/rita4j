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
		eq(RiTa.pastPart("become"), "became");
		eq(RiTa.pastPart("drink"), "drunk");
		eq(RiTa.pastPart("plead"), "pled");
		eq(RiTa.pastPart("run"), "run");
		eq(RiTa.pastPart("shine"), "shone");
		// or shined
		eq(RiTa.pastPart("shrink"), "shrunk");
		// or shrunken
		eq(RiTa.pastPart("stink"), "stunk");
		eq(RiTa.pastPart("study"), "studied");
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
