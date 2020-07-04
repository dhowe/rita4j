/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package rita.test;

import org.junit.Test;

import rita.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConjugatorTests {

	@Test
	public void testPastParticiple() {

		assertEquals(RiTa.pastParticiple("sleep"), "slept");
		assertEquals(RiTa.pastParticiple("withhold"), "withheld");

		assertEquals(RiTa.pastParticiple("cut"), "cut");
		assertEquals(RiTa.pastParticiple("go"), "gone");
		assertEquals(RiTa.pastParticiple("swim"), "swum");
		assertEquals(RiTa.pastParticiple("would"), "would");
		assertEquals(RiTa.pastParticiple("might"), "might");
		assertEquals(RiTa.pastParticiple("run"), "run");
		assertEquals(RiTa.pastParticiple("speak"), "spoken");
		assertEquals(RiTa.pastParticiple("break"), "broken");
		assertEquals(RiTa.pastParticiple(""), "");

		// PROBLEMS

		assertEquals(RiTa.pastParticiple("awake"), "awoken");
		assertEquals(RiTa.pastParticiple("become"), "became");
		assertEquals(RiTa.pastParticiple("drink"), "drunk");
		assertEquals(RiTa.pastParticiple("plead"), "pled");
		assertEquals(RiTa.pastParticiple("run"), "run");
		assertEquals(RiTa.pastParticiple("shine"), "shone");
		// or shined
		assertEquals(RiTa.pastParticiple("shrink"), "shrunk");
		// or shrunken
		assertEquals(RiTa.pastParticiple("stink"), "stunk");
		assertEquals(RiTa.pastParticiple("study"), "studied");
	}

	@Test
	public void testPresentParticiple() {

		assertEquals(RiTa.presentParticiple("sleep"), "sleeping");
		assertEquals(RiTa.presentParticiple("withhold"), "withholding");

		assertEquals(RiTa.presentParticiple("cut"), "cutting");
		assertEquals(RiTa.presentParticiple("go"), "going");
		assertEquals(RiTa.presentParticiple("run"), "running");
		assertEquals(RiTa.presentParticiple("speak"), "speaking");
		assertEquals(RiTa.presentParticiple("break"), "breaking");
		assertEquals(RiTa.presentParticiple("become"), "becoming");
		assertEquals(RiTa.presentParticiple("plead"), "pleading");
		assertEquals(RiTa.presentParticiple("awake"), "awaking");
		assertEquals(RiTa.presentParticiple("study"), "studying");

		assertEquals(RiTa.presentParticiple("lie"), "lying");
		assertEquals(RiTa.presentParticiple("swim"), "swimming");
		assertEquals(RiTa.presentParticiple("run"), "running");
		assertEquals(RiTa.presentParticiple("dig"), "digging");
		assertEquals(RiTa.presentParticiple("set"), "setting");
		assertEquals(RiTa.presentParticiple("speak"), "speaking");
		assertEquals(RiTa.presentParticiple("bring"), "bringing");
		assertEquals(RiTa.presentParticiple("speak"), "speaking");

		assertEquals(RiTa.presentParticiple("study "), "studying");
		// space
		assertEquals(RiTa.presentParticiple(" study"), "studying");
		// space
		assertEquals(RiTa.presentParticiple("study  "), "studying");
		// double space
		assertEquals(RiTa.presentParticiple("  study"), "studying");
		// double space
		assertEquals(RiTa.presentParticiple("study    "), "studying");
		// tab space
		assertEquals(RiTa.presentParticiple(" study"), "studying");
		// tab space
		assertEquals(RiTa.presentParticiple(""), "");
	}

	@Test
	public void testConjugate() {
		// Map<String,String> args = new HashMap<>(); //TODO use Map<String,String>
		// here?

		Map<String, Object> args = new HashMap<String, Object>();

		String[] s;
		String[] a;

		assertEquals("swum", RiTa.pastParticiple("swim"));
		// return; //TODO also in rita-script

		args.clear();
		args.put("form", RiTa.GERUND);
		args = Collections.unmodifiableMap(args);

		assertEquals(RiTa.conjugate("be", args), "being");

		s = new String[] { "swim", "need", "open" };
		a = new String[] { "swims", "needs", "opens" };

		args.clear();
		args.put("tense", RiTa.PRESENT_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PRESENT_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);
		args.put("passive", true);

		a = new String[] { "is swum", "is needed", "is opened" };
		for (int i = 0; i < s.length; i++) {
			assertEquals(RiTa.conjugate(s[i], args), a[i]);
		}

		/////////////////////////////////////////////////

		args.clear();
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.FIRST_PERSON);
		args.put("tense", RiTa.PAST_TENSE);

		assertEquals(RiTa.conjugate("swim", args), "swam");

		s = new String[] { "swim", "need", "open", "" };
		a = new String[] { "swam", "needed", "opened", "" };

		assertEquals(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND_PERSON);
		args.put("tense", RiTa.PAST_TENSE);

		a = new String[] { "swam", "needed", "opened", "" };
		assertEquals(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			assertEquals(RiTa.conjugate(s[i], args), a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND_PERSON);
		args.put("tense", RiTa.FUTURE_TENSE);

		a = new String[] { "will swim", "will need", "will open", "" };
		assertEquals(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			assertEquals(RiTa.conjugate(s[i], args), a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);

		a = new String[] { "swam", "needed", "opened", "" };

		assertEquals(a.length, s.length);

		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);
		args.put("form", RiTa.INFINITIVE);

		a = new String[] { "to swim", "to need", "to open", "" };
		assertEquals(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PAST_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);
		args.put("passive", true);

		s = new String[] { "scorch", "burn", "hit", "" };
		a = new String[] { "was scorched", "was burned", "was hit", "" };
		assertEquals(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		s = new String[] { "swim", "need", "open", "" };
		args.clear();
		args.put("tense", RiTa.PRESENT_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);
		args.put("form", RiTa.INFINITIVE);
		args.put("progressive", true);

		a = new String[] { "to be swimming", "to be needing", "to be opening", "" };
		assertEquals(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("tense", RiTa.PRESENT_TENSE);
		args.put("number", RiTa.SINGULAR);
		args.put("person", RiTa.THIRD_PERSON);
		args.put("form", RiTa.INFINITIVE);
		args.put("perfect", true);

		a = new String[] { "to have swum", "to have needed", "to have opened", "" };
		assertEquals(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

		args.clear();
		args.put("number", RiTa.PLURAL);
		args.put("person", RiTa.SECOND_PERSON);
		args.put("tense", RiTa.PAST_TENSE);

		assertEquals(RiTa.conjugate("barter", args), "bartered");
		assertEquals(RiTa.conjugate("run", args), "ran");

		s = new String[] { "compete", "complete", "eject" };
		a = new String[] { "competed", "completed", "ejected" };
		assertEquals(a.length, s.length);
		for (int i = 0; i < s.length; i++) {
			String c = RiTa.conjugate(s[i], args);
			assertEquals(c, a[i]);
		}

	}

}
