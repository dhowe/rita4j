package rita.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rita.RiTa;

public class RiTaTests {

	@Test
	public void testIsQuestion() {

		assertTrue(RiTa.isQuestion("What"));
		assertTrue(RiTa.isQuestion("what"));
		assertTrue(RiTa.isQuestion("what"));
		assertTrue(RiTa.isQuestion("what is this"));
		assertTrue(RiTa.isQuestion("what is this?"));
		assertTrue(RiTa.isQuestion("Does it?"));
		assertTrue(RiTa.isQuestion("Would you believe it?"));
		assertTrue(RiTa.isQuestion("Have you been?"));
		assertTrue(RiTa.isQuestion("Is this yours?"));
		assertTrue(RiTa.isQuestion("Are you done?"));
		assertTrue(RiTa.isQuestion("what is this? , where is that?"));
		assertTrue(!RiTa.isQuestion("That is not a toy This is an apple"));
		assertTrue(!RiTa.isQuestion("string"));
		assertTrue(!RiTa.isQuestion("?"));
		assertTrue(!RiTa.isQuestion(""));
	}

	@Test
	public void testIsAbbreviation() { // TODO add second parameter tests

		assertTrue(RiTa.isAbbreviation("Dr."));
		assertTrue(!RiTa.isAbbreviation("dr."));
		// T in java

		assertTrue(!RiTa.isAbbreviation("DR."));
		// F in Processing.lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("Dr. "));
		// space
		assertTrue(!RiTa.isAbbreviation(" Dr."));
		// space
		assertTrue(!RiTa.isAbbreviation("  Dr."));
		// double space
		assertTrue(!RiTa.isAbbreviation("Dr.  "));
		// double space
		assertTrue(!RiTa.isAbbreviation("   Dr."));
		// tab space
		assertTrue(!RiTa.isAbbreviation("Dr.    "));
		// tab space
		assertTrue(!RiTa.isAbbreviation("Dr"));
		assertTrue(!RiTa.isAbbreviation("Doctor"));
		assertTrue(!RiTa.isAbbreviation("Doctor."));

		assertTrue(RiTa.isAbbreviation("Prof."));
		assertTrue(!RiTa.isAbbreviation("prof."));
		// T in java
		assertTrue(!RiTa.isAbbreviation("PRFO."));
		// F in Processing. lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("PrFo."));
		// F in Processing. lowercase is true but uppercase is false
		assertTrue(!RiTa.isAbbreviation("Professor"));
		assertTrue(!RiTa.isAbbreviation("professor"));
		assertTrue(!RiTa.isAbbreviation("PROFESSOR"));
		assertTrue(!RiTa.isAbbreviation("Professor."));

		assertTrue(!RiTa.isAbbreviation("@#$%^&*()"));

		assertTrue(!RiTa.isAbbreviation(""));
		assertTrue(!RiTa.isAbbreviation(null));
		// assertTrue(!RiTa.isAbbreviation(undefined)); //no undefined in JAVA
		// assertTrue(!RiTa.isAbbreviation(1)); //no wrong datatype in JAVA
	}

	@Test
	public void testIsPunctuation() {

		assertTrue(!RiTa.isPunctuation("What the"));
		assertTrue(!RiTa.isPunctuation("What ! the"));
		assertTrue(!RiTa.isPunctuation(".#\"\\!@i$%&}<>"));

		assertTrue(RiTa.isPunctuation("!"));
		assertTrue(RiTa.isPunctuation("?"));
		assertTrue(RiTa.isPunctuation("?!"));
		assertTrue(RiTa.isPunctuation("."));
		assertTrue(RiTa.isPunctuation(".."));
		assertTrue(RiTa.isPunctuation("..."));
		assertTrue(RiTa.isPunctuation("...."));
		assertTrue(RiTa.isPunctuation("%..."));

		assertTrue(!RiTa.isPunctuation("! "));
		// space
		assertTrue(!RiTa.isPunctuation(" !"));
		// space
		assertTrue(!RiTa.isPunctuation("!  "));
		// double space
		assertTrue(!RiTa.isPunctuation("  !"));
		// double space
		assertTrue(!RiTa.isPunctuation("!  "));
		// tab space
		assertTrue(!RiTa.isPunctuation("   !"));

		String punct;

		punct = "$%&^,";
		String[] punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		punct = ",;:!?)([].#\"\\!@$%&}<>|+=-_\\/*{^";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		// TODO: also test multiple characters strings here ****
		punct = "\"��������`'";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		punct = "\"��������`',;:!?)([].#\"\\!@$%&}<>|+=-_\\/*{^";
		punctArr = punct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(RiTa.isPunctuation(punctArr[i]));
		}

		// TODO: and here...
		String nopunct = "Helloasdfnals  FgG   \t kjdhfakjsdhf askjdfh aaf98762348576";
		punctArr = nopunct.split("");
		for (int i = 0; i < punctArr.length; i++) {
			assertTrue(!RiTa.isPunctuation(punctArr[i]));
		}

		assertTrue(!RiTa.isPunctuation(""));

	}

	@Test
	public void testSingularize() {

		String[] tests = {
				"media", "medium",
				"millennia", "millennium",
				"consortia", "consortium",
				"concerti", "concerto",
				"septa", "septum",
				"termini", "terminus",
				"larvae", "larva",
				"vertebrae", "vertebra",
				"memorabilia", "memorabilium",
				"hooves", "hoof",
				"thieves", "thief",
				"rabbis", "rabbi",
				"flu", "flu",
				"safaris", "safari",
				"sheaves", "sheaf",
				"uses", "use",
				"pinches", "pinch",
				"catharses", "catharsis",
				"hankies", "hanky"
		};
		for (int i = 0; i < tests.length; i += 2) {
			// System.out.println("p: " + RiTa.singularize(tests[i]) + " s: " + tests[i +
			// 1]);
			assertEquals(RiTa.singularize(tests[i]), tests[i + 1]);
		}

		assertEquals(RiTa.singularize("pleae"), "pleae"); // special-cased in code
		assertEquals(RiTa.singularize("whizzes"), "whiz");
		assertEquals(RiTa.singularize("selves"), "self");
		assertEquals(RiTa.singularize("bookshelves"), "bookshelf");
		assertEquals(RiTa.singularize("wheezes"), "wheeze");
		assertEquals(RiTa.singularize("diagnoses"), "diagnosis");

		assertEquals("minutia", RiTa.singularize("minutia"));
		assertEquals("blonde", RiTa.singularize("blondes"));
		assertEquals("eye", RiTa.singularize("eyes"));
		assertEquals(RiTa.singularize("swine"), "swine");
		assertEquals(RiTa.singularize("cognoscenti"), "cognoscenti");
		assertEquals(RiTa.singularize("bonsai"), "bonsai");
		assertEquals(RiTa.singularize("taxis"), "taxi");
		assertEquals(RiTa.singularize("chiefs"), "chief");
		assertEquals(RiTa.singularize("monarchs"), "monarch");
		assertEquals(RiTa.singularize("lochs"), "loch");
		assertEquals(RiTa.singularize("stomachs"), "stomach");

		assertEquals(RiTa.singularize("Chinese"), "Chinese");

		assertEquals(RiTa.singularize("people"), "person");
		assertEquals(RiTa.singularize("monies"), "money");
		assertEquals(RiTa.singularize("vertebrae"), "vertebra");
		assertEquals(RiTa.singularize("humans"), "human");
		assertEquals(RiTa.singularize("germans"), "german");
		assertEquals(RiTa.singularize("romans"), "roman");

		assertEquals(RiTa.singularize("memoranda"), "memorandum");
		assertEquals(RiTa.singularize("data"), "datum");
		assertEquals(RiTa.singularize("appendices"), "appendix");
		assertEquals(RiTa.singularize("theses"), "thesis");
		assertEquals(RiTa.singularize("alumni"), "alumnus");

		assertEquals(RiTa.singularize("solos"), "solo");
		assertEquals(RiTa.singularize("music"), "music");

		assertEquals(RiTa.singularize("oxen"), "ox");
		assertEquals(RiTa.singularize("solos"), "solo");
		assertEquals(RiTa.singularize("music"), "music");
		assertEquals(RiTa.singularize("money"), "money");
		assertEquals(RiTa.singularize("beef"), "beef");

		assertEquals(RiTa.singularize("tobacco"), "tobacco");
		assertEquals(RiTa.singularize("cargo"), "cargo");
		assertEquals(RiTa.singularize("golf"), "golf");
		assertEquals(RiTa.singularize("grief"), "grief");

		assertEquals(RiTa.singularize("cakes"), "cake");

		assertEquals("dog", RiTa.singularize("dogs"));
		assertEquals("foot", RiTa.singularize("feet"));
		assertEquals("tooth", RiTa.singularize("teeth"));
		assertEquals("kiss", RiTa.singularize("kisses"));
		assertEquals("child", RiTa.singularize("children"));
		assertEquals("randomword", RiTa.singularize("randomwords"));
		assertEquals("deer", RiTa.singularize("deer"));
		assertEquals("sheep", RiTa.singularize("sheep"));
		assertEquals("shrimp", RiTa.singularize("shrimps"));

		assertEquals(RiTa.singularize("tomatoes"), "tomato");
		assertEquals(RiTa.singularize("photos"), "photo");

		assertEquals(RiTa.singularize("toes"), "toe");

		assertEquals(RiTa.singularize("series"), "series");
		assertEquals(RiTa.singularize("oxen"), "ox");
		assertEquals(RiTa.singularize("men"), "man");
		assertEquals(RiTa.singularize("mice"), "mouse");
		assertEquals(RiTa.singularize("lice"), "louse");
		assertEquals(RiTa.singularize("children"), "child");

		assertEquals(RiTa.singularize("gases"), "gas");
		assertEquals(RiTa.singularize("buses"), "bus");
		assertEquals(RiTa.singularize("happiness"), "happiness");

		assertEquals(RiTa.singularize("crises"), "crisis");
		assertEquals(RiTa.singularize("theses"), "thesis");
		assertEquals(RiTa.singularize("apotheses"), "apothesis");
		assertEquals(RiTa.singularize("stimuli"), "stimulus");
		assertEquals(RiTa.singularize("alumni"), "alumnus");
		assertEquals(RiTa.singularize("corpora"), "corpus");

		assertEquals("man", RiTa.singularize("men"));
		assertEquals("woman", RiTa.singularize("women"));
		assertEquals("congressman", RiTa.singularize("congressmen"));
		assertEquals("alderman", RiTa.singularize("aldermen"));
		assertEquals("freshman", RiTa.singularize("freshmen"));
		assertEquals("fireman", RiTa.singularize("firemen"));
		assertEquals("grandchild", RiTa.singularize("grandchildren"));
		assertEquals("menu", RiTa.singularize("menus"));
		assertEquals("guru", RiTa.singularize("gurus"));

		assertEquals("", RiTa.singularize(""));
		assertEquals("hardness", RiTa.singularize("hardness"));
		assertEquals("shortness", RiTa.singularize("shortness"));
		assertEquals("dreariness", RiTa.singularize("dreariness"));
		assertEquals("unwillingness", RiTa.singularize("unwillingness"));
		assertEquals("deer", RiTa.singularize("deer"));
		assertEquals("fish", RiTa.singularize("fish"));
		assertEquals("ooze", RiTa.singularize("ooze"));

		assertEquals("ooze", RiTa.singularize("ooze"));
		assertEquals("enterprise", RiTa.singularize("enterprises"));
		assertEquals("treatise", RiTa.singularize("treatises"));
		assertEquals("house", RiTa.singularize("houses"));
		assertEquals("chemise", RiTa.singularize("chemises"));

		assertEquals("aquatics", RiTa.singularize("aquatics"));
		assertEquals("mechanics", RiTa.singularize("mechanics"));
		assertEquals("quarter", RiTa.singularize("quarters"));

	}

	@Test
	public void testPluralize() {
		String[] tests = {
				"media", "medium",
				"millennia", "millennium",
				"consortia", "consortium",
				"concerti", "concerto",
				"septa", "septum",
				"termini", "terminus",
				"larvae", "larva",
				"vertebrae", "vertebra",
				"memorabilia", "memorabilium",
				"sheafs", "sheaf",
				"spoofs", "spoof",
				"proofs", "proof",
				"roofs", "roof",
				"disbeliefs", "disbelief",
				"indices", "index",
				"accomplices", "accomplice"
		};
		for (int i = 0; i < tests.length; i += 2) {
			System.out.println("singular: " + tests[i]);
			System.out.println("plural: " + RiTa.pluralize(tests[i + 1]));
			assertEquals(tests[i], RiTa.pluralize(tests[i + 1]));
		}

		// uncountable
		tests = new String[] {
				"turf", "macaroni", "spaghetti", "potpourri", "electrolysis"
		};
		for (int i = 0; i < tests.length; i++) {
			assertEquals(tests[i], RiTa.pluralize(tests[i]));
		}

		assertEquals("blondes", RiTa.pluralize("blonde"));
		assertEquals("eyes", RiTa.pluralize("eye"));
		assertEquals("blondes", RiTa.pluralize("blond"));

		assertEquals("dogs", RiTa.pluralize("dog"));
		assertEquals("feet", RiTa.pluralize("foot"));
		assertEquals("men", RiTa.pluralize("man"));

		assertEquals("beautifuls", RiTa.pluralize("beautiful"));
		assertEquals("teeth", RiTa.pluralize("tooth"));
		assertEquals("cakes", RiTa.pluralize("cake"));
		assertEquals("kisses", RiTa.pluralize("kiss"));
		assertEquals("children", RiTa.pluralize("child"));

		assertEquals("randomwords", RiTa.pluralize("randomword"));
		assertEquals("lice", RiTa.pluralize("louse"));

		assertEquals("sheep", RiTa.pluralize("sheep"));
		assertEquals("shrimps", RiTa.pluralize("shrimp"));
		assertEquals("series", RiTa.pluralize("series"));
		assertEquals("mice", RiTa.pluralize("mouse"));

		assertEquals("", RiTa.pluralize(""));

		assertEquals(RiTa.pluralize("tomato"), "tomatoes");
		assertEquals(RiTa.pluralize("toe"), "toes");

		assertEquals(RiTa.pluralize("deer"), "deer");
		assertEquals(RiTa.pluralize("ox"), "oxen");

		assertEquals(RiTa.pluralize("tobacco"), "tobacco");
		assertEquals(RiTa.pluralize("cargo"), "cargo");
		assertEquals(RiTa.pluralize("golf"), "golf");
		assertEquals(RiTa.pluralize("grief"), "grief");
		assertEquals(RiTa.pluralize("wildlife"), "wildlife");
		assertEquals(RiTa.pluralize("taxi"), "taxis");
		assertEquals(RiTa.pluralize("Chinese"), "Chinese");
		assertEquals(RiTa.pluralize("bonsai"), "bonsai");

		assertEquals(RiTa.pluralize("whiz"), "whizzes");
		assertEquals(RiTa.pluralize("prognosis"), "prognoses");
		assertEquals(RiTa.pluralize("gas"), "gases");
		assertEquals(RiTa.pluralize("bus"), "buses");

		assertEquals("crises", RiTa.pluralize("crisis"));
		assertEquals("theses", RiTa.pluralize("thesis"));
		assertEquals("apotheses", RiTa.pluralize("apothesis"));
		assertEquals("stimuli", RiTa.pluralize("stimulus"));
		assertEquals("alumni", RiTa.pluralize("alumnus"));
		assertEquals("corpora", RiTa.pluralize("corpus"));
		assertEquals("menus", RiTa.pluralize("menu"));

		assertEquals("hardness", RiTa.pluralize("hardness"));
		assertEquals("shortness", RiTa.pluralize("shortness"));
		assertEquals("dreariness", RiTa.pluralize("dreariness"));
		assertEquals("unwillingness", RiTa.pluralize("unwillingness"));
		assertEquals("deer", RiTa.pluralize("deer"));
		assertEquals("fish", RiTa.pluralize("fish"));
		assertEquals("moose", RiTa.pluralize("moose"));

		assertEquals("aquatics", RiTa.pluralize("aquatics"));
		assertEquals("mechanics", RiTa.pluralize("mechanics"));
	}

}
