package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static rita.RiTa.opts;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import rita.*;

public class RiScriptTests {

	static final Map<String, Object> TT = opts("trace", true);
	static final Map<String, Object> ST = opts("silent", true);
	static final Map<String, Object> SP = opts("singlePass", true);
	static final Map<String, Object> SPTT = opts("singlePass", true, "trace", true);

	@Test
	public void handleEmptyBuiltins_TRANSFORM() { // TODO: add to JS
		
		assertEq(RiTa.evaluate("().uc()"), "");
		assertEq(RiTa.evaluate("().ucf()"), "");
		assertEq(RiTa.evaluate("().articlize()"), "");
		assertEq(RiTa.evaluate("().capitalize()"), "");
		assertEq(RiTa.evaluate("().pluralize()"), "");
		assertEq(RiTa.evaluate("().quotify()"), "“”");
		assertEq(RiTa.evaluate("().art()"), "");
		
		assertEq(RiTa.evaluate("().toLowerCase()", null, ST), ""); // ?
		assertEq(RiTa.evaluate("().toUpperCase()", null, ST), ""); // ?
	}
	
	@Test
	public void handlePhraseTransforms_TRANSFORM() {
    String g = "$y=(a | a)\n[$x=$y b].ucf()";
    assertEq(RiTa.evaluate(g),"A b");
  }

	@Test
	public void handleVariousTransforms_TRANSFORM() { // TODO: add to JS
		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("(BAZ).toLowerCase().ucf()", ctx), "Baz");

		assertEq(RiTa.evaluate("(a).toUpperCase()", ctx), "A"); // Choice
		assertEq(RiTa.evaluate(".toUpperCase()", ctx, ST), ""); // Symbol

		assertEq(RiTa.evaluate("$a=b\n$a.toUpperCase()", ctx), "B"); // Symbol
		assertEq(RiTa.evaluate("[$b=((a | a)|a)].toUpperCase() dog.", ctx), "A dog.");// Inline
		assertEq(RiTa.evaluate("((a)).toUpperCase()", ctx), "A"); // Nested Choice

		assertEq(RiTa.evaluate("$a.toUpperCase()\n($a=b)", ctx), "B"); // pending Symbol

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("$dog.ucf()", ctx), "Terrier"); // Symbol in context
	}

	@Test
	public void testCustomRegexes() {  // NO_JS
		String expr = "The $foo\ndog.";
		assertTrue(RE.test("\\$[A-Za-z_]", expr));
		//System.out.println(expr);
	}

	@Test
	public void resolveSymbolsStartingWithNumbers_SYMBOL() {
		assertEq(RiTa.evaluate("$foo=hello\n$start=I said $foo to her\n$start", opts()), "I said hello to her");
		assertEq(RiTa.evaluate("$1foo=hello\n$1start=I said $1foo to her\n$1start", opts()), "I said hello to her");
		assertEq(RiTa.evaluate("$1foo=(hello)\n$1start=I said $1foo to her\n$1start", opts()), "I said hello to her");
	}

	@Test
	public void resolveSymbolsInContext_SYMBOL() {

		Map<String, Object> ctx;
		ctx = opts("a", "(terrier | terrier)");
		assertEq(RiTa.evaluate("$a.capitalize()", ctx), "Terrier");
		//?

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("the $dog ate", ctx), "the terrier ate");

		ctx.put("verb", "ate");
		assertEq(RiTa.evaluate("the $dog $verb", ctx), "the terrier ate");

		ctx = opts();
		assertEq(RiTa.evaluate("$foo", ctx), "$foo");
		assertEq(RiTa.evaluate("a $foo dog", ctx, ST), "a $foo dog");

		ctx = opts("foo", "bar");
		assertEq(RiTa.evaluate("$foo", ctx), "bar");

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("a $dog", ctx), "a terrier");

		ctx = opts("dog", "beagle");
		assertEq(RiTa.evaluate("I ate the $dog", ctx), "I ate the beagle");

		ctx = opts("dog", "lab");
		assertEq(RiTa.evaluate("The $dog today.", ctx), "The lab today.");
		assertEq(RiTa.evaluate("I ate the $dog.", ctx), "I ate the lab.");

		ctx = opts();
		assertEq(RiTa.evaluate("$foo\n", ctx), "$foo");
		assertEq(RiTa.evaluate("a $foo\ndog", ctx), "a $foo dog");
		ctx.put("foo", "bar");
		assertEq(RiTa.evaluate("$foo\n", ctx), "bar");

		ctx = opts("dog", "beagle");
		assertEq(RiTa.evaluate("I ate\nthe $dog", ctx), "I ate the beagle");

		ctx = opts("dog", "lab");
		assertEq(RiTa.evaluate("The $dog\ntoday.", ctx), "The lab today.");
		assertEq(RiTa.evaluate("I ate the\n$dog.", ctx), "I ate the lab.");

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("$100 is a lot of $dog.", ctx), "$100 is a lot of terrier.");
		assertEq(RiTa.evaluate("the $dog cost $100", ctx), "the terrier cost $100");
		assertEq(RiTa.evaluate("the $dog cost $100!", ctx), "the terrier cost $100!");
		assertEq(RiTa.evaluate("the $dog costot", ctx), "the terrier costot");
		assertEq(RiTa.evaluate("the $dog^1 was a footnote.", ctx), "the terrier^1 was a footnote.");

		ctx = opts();
		ctx.put("user", opts("name", "jen"));
		assertEq(RiTa.evaluate("$user.name", ctx), "jen");
		assertEq(RiTa.evaluate("$user.name?", ctx), "jen?");
		assertEq(RiTa.evaluate("Was $user.name.ucf() (ok | ok) today?", ctx), "Was Jen ok today?");
		assertEq(RiTa.evaluate("$user.name was ok", ctx), "jen was ok");
		assertEq(RiTa.evaluate("That was $user.name", ctx), "That was jen");
		assertEq(RiTa.evaluate("Was that $user.name.ucf()?", ctx), "Was that Jen?");
		assertEq(RiTa.evaluate("$user.name.toUpperCase()", ctx), "JEN");
		assertEq(RiTa.evaluate("$user.name.uc()", ctx), "JEN");
		assertEq(RiTa.evaluate("$user.name.ucf()", ctx), "Jen");

		ctx.clear();
		ctx.put("dog", opts("breed", "Corgie"));
		assertEq(RiTa.evaluate("Was the $dog.breed (ok | ok) today?", ctx), "Was the Corgie ok today?");
	}

	@Test
	public void resolvePriorSymbols_SYMBOL() {
		assertEq(RiTa.evaluate("the $dog ate", opts("dog", "terrier")), "the terrier ate");
		assertEq(RiTa.evaluate("the $dog $verb", opts("dog", "terrier", "verb", "ate")), "the terrier ate");

		assertEq(RiTa.evaluate("$foo=bar\n$foo", opts()), "bar");
		assertEq(RiTa.evaluate("$dog=terrier\na $dog", opts()), "a terrier");
		assertEq(RiTa.evaluate("$dog=beagle\nI ate the $dog", opts()), "I ate the beagle");
		assertEq(RiTa.evaluate("$dog=lab\nThe $dog today.", opts()), "The lab today.");
		assertEq(RiTa.evaluate("$dog=lab\nI ate the $dog.", opts()), "I ate the lab.");
		assertEq(RiTa.evaluate("$dog=lab\nThe $dog\ntoday.", opts()), "The lab today.");
		assertEq(RiTa.evaluate("$dog=lab\nI ate the\n$dog.", opts()), "I ate the lab.");
		assertEq(RiTa.evaluate("$foo=baz\n$bar=$foo\n$bar", opts()), "baz");
		assertEq(RiTa.evaluate("$foo=bar\n$foo", opts()), "bar");

		assertEq(RiTa.evaluate("$bar", opts("foo", "baz", "bar", "$foo")), "baz");
		assertEq(RiTa.evaluate("$bar", opts("foo", "baz", "bar", "(A | A)")), "A");
		assertEq(RiTa.evaluate("$bar", opts("foo", "baz", "bar", "$foo starts with (b | b)")), "baz starts with b");
		assertEq(RiTa.evaluate("$start=$foo\n$foo=hello\n$start"), "hello");
		assertEq(RiTa.evaluate("$start = $noun\n$noun = hello\n$start"), "hello");
	}

	@Test
	public void handleArticlize_TRANSFORM() {
		assertEq(RiTa.articlize("dog"), "a dog");
		assertEq(RiTa.articlize("ant"), "an ant");
		assertEq(RiTa.articlize("honor"), "an honor");
		assertEq(RiTa.articlize("eagle"), "an eagle");
		RiTa.SILENCE_LTS = true;
		assertEq(RiTa.articlize("ermintrout"), "an ermintrout"); // LTS
		RiTa.SILENCE_LTS = false;
	}

	@Test
	public void handleArticlizePhrases_TRAMSFORM() {
		assertEq(RiTa.articlize("black dog"), "a black dog");
		assertEq(RiTa.articlize("black ant"), "a black ant");
		assertEq(RiTa.articlize("orange ant"), "an orange ant");
	}

	@Test
	public void pluralizePhrases_ASSIGN() {
		assertEq(RiTa.evaluate("These (bad feeling).pluralize()."), "These bad feelings.");
		assertEq(RiTa.evaluate("She (pluralize).pluralize()."), "She pluralizes.");
		assertEq(RiTa.evaluate("These ($state feeling).pluralize().", opts("state", "bad")), "These bad feelings.");
		assertEq(RiTa.evaluate("$state=(bad | bad)\nThese ($state feeling).pluralize()."), "These bad feelings.");
	}

	@Test
	public void invokeMatchingOperators_OPERATORS() {
		assertEq(Operator.SW.invoke("Hello", "He"), true);
		assertEq(Operator.SW.invoke("Hello", "Hello"), true);
		assertEq(Operator.SW.invoke("Hello", "Hej"), false);
		assertEq(Operator.SW.invoke("Hello", null), false);
		assertEq(Operator.SW.invoke("Hello", ""), true);

		assertEq(Operator.EW.invoke("Hello", "o"), true);
		assertEq(Operator.EW.invoke("Hello", "Hello"), true);
		assertEq(Operator.EW.invoke("Hello", "l1o"), false);
		assertEq(Operator.EW.invoke("Hello", null), false);
		assertEq(Operator.EW.invoke("Hello", ""), true);

		assertEq(Operator.RE.invoke("Hello", "ll"), true);
		assertEq(Operator.RE.invoke("Hello", "e"), true);
		assertEq(Operator.RE.invoke("Hello", "l1"), false);
		assertEq(Operator.RE.invoke("Hello", null), false);
		assertEq(Operator.RE.invoke("Hello", ""), true);

		assertEq(Operator.SW.invoke("$Hello", "$"), true);
		assertEq(Operator.EW.invoke("$Hello", "$"), false);
		assertEq(Operator.RE.invoke("$Hello", "$"), true);
		assertEq(Operator.RE.invoke("hello", "(hello|bye)"), true);
		assertEq(Operator.RE.invoke("bye", "(hello|bye)"), true);
		assertEq(Operator.RE.invoke("by", "(hello|bye)"), false);

		assertThrows(RiTaException.class, () -> Operator.SW.invoke(null, "hello"));
		assertThrows(RiTaException.class, () -> Operator.SW.invoke(null, null));
	}

	@Test
	public void callIsParseable() {
		RiScript rs = new RiScript();
		assertTrue(!rs.isParseable("Hello"));
		assertTrue(rs.isParseable("("));
		assertTrue(rs.isParseable("(A | B)"));
		assertTrue(rs.isParseable("$hello"));
		assertTrue(rs.isParseable("$b"));
	}

	@Test
	public void handleNestedContext_ASSIGN() {

		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		String res = RiTa.evaluate("$foo=$bar.color\n$foo", ctx);
		assertEq(res, "blue");
	}

	@Test
	public void resolveComplexInlines_INLINE() {
		assertEq(RiTa.evaluate("A [$stored=($animal | $animal)] is a mammal", opts("animal", "dog")), "A dog is a mammal");
		assertEq(RiTa.evaluate("[$b=(a | a).toUpperCase()] dog is a $b.", opts()), "A dog is a A.");
		assertEq(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a $b.toLowerCase().", opts()), "A dog is a a.");
		assertEq(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a ($b).toLowerCase().", opts()), "A dog is a a.");

		String[] expected = { "Dave talks to Dave.", "Jill talks to Jill.", "Pete talks to Pete." };

		String rs;
		rs = RiTa.evaluate("$person talks to $person.", opts("person", "(Dave | Jill | Pete)"));
		assertTrue(Arrays.asList(expected).contains(rs));

		rs = RiTa.evaluate("[$person=(Dave | Jill | Pete)] talks to [$person=(Dave | Jill | Pete)].");
		assertTrue(Arrays.asList(expected).contains(rs));

		rs = RiTa.evaluate("[$chosen=(Dave | Jill | Pete)] talks to $chosen.");
		assertTrue(Arrays.asList(expected).contains(rs));

		rs = RiTa.evaluate("[$chosen=$person] talks to $chosen.", opts("person", "(Dave | Jill | Pete)"));
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	// Evaluation

	@Test
	public void evalSimpleExpressions_EVALUATION() {
		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("foo", ctx), "foo");
		assertEq(RiTa.evaluate("foo.", ctx), "foo.");
		assertEq(RiTa.evaluate("\"foo\"", ctx), "\"foo\"");
		assertEq(RiTa.evaluate("'foo'", ctx), "'foo'");
		assertEq(RiTa.evaluate("foo\nbar", ctx), "foo bar");
		assertEq(RiTa.evaluate("foo&#10;bar", ctx), "foo\nbar");
		assertEq(RiTa.evaluate("$foo=bar\nbaz", ctx), "baz");
		assertEq(RiTa.evaluate("$foo=bar\nbaz\n$foo", ctx), "baz bar");

		ctx.put("a", "a");
		ctx.put("b", "b");
		assertEq(RiTa.evaluate("(a|a)", ctx), "a");
		assertEq(RiTa.evaluate("foo.bar", ctx), "foo.bar");
	}

	@Test
	public void resolveRecursiveExpressions_EVALUATION() {
		
		Map<String, Object> ctx = opts("a", "a", "b", "b");
		assertEq(RiTa.evaluate("(a|a)", ctx), "a");

		ctx = opts("a", "$b", "b", "(c | c)");
		assertEq(RiTa.evaluate("$a", ctx), "c");
		assertEq(RiTa.evaluate("$k = $a\n$k", ctx), "c");
		assertEq(RiTa.evaluate("$s = $a\n$a = $b\n$c = $d\n$d = c\n$s", ctx), "c");

		ctx = opts("s", "$a", "a", "$b", "b", "$c", "c", "$d", "d", "c");
		assertEq(RiTa.evaluate("$s", ctx), "c");
	}

	// Assign
	@Test
	public void parseAssignments_ASSIGN() {

		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("$foo=a", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$1foo=a", ctx), "");
		assertEq(ctx.get("1foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(a) b", ctx), "");
		assertEq(ctx.get("foo"), "a b");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(a | a)", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=ab", ctx), "");
		assertEq(ctx.get("foo"), "ab");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=ab bc", ctx), "");
		assertEq(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(ab) (bc)", ctx), "");
		assertEq(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(ab bc)", ctx), "");
		assertEq(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(a | a) (b | b)", ctx), "");
		assertEq(ctx.get("foo"), "a b");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=((a | a) | (a | a))", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=()", ctx), "");
		assertEq(ctx.get("foo"), "");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=a\n$bar=$foo", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=a\n$bar=$foo.", ctx), ""); // empty string
		assertEq(ctx.get("foo"), "a");
		assertEq(ctx.get("bar"), "a.");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(a | a)", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(a | a)\n$foo", ctx), "a");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=(hi | hi)\n$foo there", ctx), "hi there");
		assertEq(ctx.get("foo"), "hi");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=The boy walked his dog", ctx), "");
		assertEq(ctx.get("foo"), "The boy walked his dog");

	}

	@Test
	public void resolveSentences_ASSIGN() {

		Map<String, Object> ctx = opts();

		// Known issue in js
		//assertEq(RiTa.evaluate("$foo=.r", ctx), "");
		//assertEq(ctx.get("foo"), ".r");

		ctx.clear();
		assertEq(RiTa.evaluate(".", ctx), ".");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=a", ctx), "");
		assertEq(ctx.get("foo"), "a");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=.", ctx), "");
		assertEq(ctx.get("foo"), ".");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=r.", ctx), "");
		assertEq(ctx.get("foo"), "r.");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=ran.", ctx), "");
		assertEq(ctx.get("foo"), "ran.");

		ctx.clear();
		assertEq(RiTa.evaluate("$start=dog\n$start", ctx), "dog");
		assertEq(ctx.get("start"), "dog");

		ctx.clear();
		assertEq(RiTa.evaluate("$start=.\n$start", ctx), ".");
		assertEq(ctx.get("start"), ".");

		ctx.clear();
		assertEq(RiTa.evaluate("$noun=I\n$start=$noun ran.\n$start", ctx), "I ran.");
		assertEq(ctx.get("noun"), "I");

		ctx.clear();
		assertEq(RiTa.evaluate("$noun=I\n$verb=sat\n$start=$noun $verb.\n$start", ctx), "I sat.");
		assertEq(ctx.get("noun"), "I");
		assertEq(ctx.get("verb"), "sat");
	}

	@Test
	public void parseTransformedAssignments_ASSIGN() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=(a).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "A");

		assertEq(RiTa.evaluate("$foo=(a | a).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "A");

		assertEq(RiTa.evaluate("$foo=(ab).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "AB");

		assertEq(RiTa.evaluate("$foo=(ab).toUpperCase() (bc).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "AB BC");

		assertEq(RiTa.evaluate("$foo=(ab bc).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "AB BC");

		assertEq(RiTa.evaluate("$foo=(a | a).toUpperCase() (b | b)", ctx), "");
		assertEq(ctx.get("foo"), "A b");

		assertEq(RiTa.evaluate("$foo=(a | a).toUpperCase() (b | b).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "A B");

		assertEq(RiTa.evaluate("$foo=((a | a) | (a | a))", ctx), "");
		assertEq(ctx.get("foo"), "a");

		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx, ST), "");// empty string
		assertEq(ctx.get("foo"), "");

	}

	@Test
	public void resolveTransformsOnLiterals_ASSIGN() {
		assertEq(RiTa.evaluate("How many (teeth).quotify() do you have?"), "How many “teeth” do you have?");
		// NEXT: CONSIDER adding context to RiTa.Grammar/grammar.expand
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("That is (ant).articlize().", ctx), "That is an ant.");
		assertEq(RiTa.evaluate("That is an (ant).capitalize()."), "That is an Ant.");
		assertEq(RiTa.evaluate("(ant).articlize().capitalize()"), "An ant");
		assertEq(RiTa.evaluate("(ant).capitalize().articlize()"), "an Ant");
	}

	@Test
	public void resolveTransformsOnPhrases_ASSIGN() {
		Map<String, Object> ctx = opts("adj", "awful");
		assertEq(RiTa.evaluate("($adj tooth).articlize()", ctx), "an awful tooth");
		assertEq(RiTa.evaluate("How many (bad teeth).quotify()?", opts()), "How many “bad teeth”?");
		assertEq(RiTa.evaluate("(awful tooth).articlize()", opts()), "an awful tooth");
		assertEq(RiTa.evaluate("$adj teeth", ctx), "awful teeth");
		assertEq(RiTa.evaluate("an ($adj tooth)", ctx), "an awful tooth");
	}

	@Test
	public void resolveAcrossAssignmentTypes_ASSIGN() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("The $foo=blue (dog | dog)", ctx), "The");
		assertEq(ctx.get("foo"), "blue dog");

		ctx = opts();
		assertEq(RiTa.evaluate("The [$foo=blue] (dog | dog)", ctx), "The blue dog");
		assertEq(ctx.get("foo"), "blue");

		ctx = opts();
		assertEq(RiTa.evaluate("The [$foo=blue (dog | dog)]", ctx), "The blue dog");
		assertEq(ctx.get("foo"), "blue dog");
	}

	// it.only("Should handle silents", () => {
	// assertEq(RiTa.evaluate("The $hero=blue (dog | dog)", ctx, tf),"The blue
	// dog"); assertEq(ctx.foo,"blue"); });

	// Inline
	@Test
	public void resolveInlineVars_INLINE() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$person=(a | b | c)\n[$a=$person] is $a", ctx);
		String[] possibleResults = { "a is a", "b is b", "c is c" };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		ctx.put("name", "(Dave1 | Dave2)");
		rs = RiTa.evaluate("$name=(Dave1 | Dave2)\n[$stored=$name] is $stored", ctx = opts());

		String[] possibleNames = { "Dave1", "Dave2" };
		assertTrue(Arrays.asList(possibleNames).contains(ctx.get("stored")));

		String[] possibleResult2 = { "Dave1 is Dave1", "Dave2 is Dave2" };
		assertTrue(Arrays.asList(possibleResult2).contains(rs));

		rs = RiTa.evaluate("[$stored=(Dave1 | Dave2)] is $stored", ctx = opts());
		assertTrue(Arrays.asList(possibleNames).contains(ctx.get("stored")));
		assertTrue(Arrays.asList(possibleResult2).contains(rs));

		rs = RiTa.evaluate("$name=(Dave | Dave)\n[$stored=$name] is called $stored", ctx = opts());
		assertEq(rs, "Dave is called Dave");
	}

	@Test
	public void resolveInlineAssigns_INLINE() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("[$foo=hi]", ctx), "hi");
		assertEq(RiTa.evaluate("[$foo=(hi | hi)] there", opts()), "hi there");
		assertEq(RiTa.evaluate("[$foo=(hi | hi).ucf()] there", opts()), "Hi there");

		assertEq(RiTa.evaluate("$foo=(hi | hi)\n$foo there", opts()),
				RiTa.evaluate("[$foo=(hi | hi)] there", opts()));

		String exp = "A dog is a mammal";
		assertEq(RiTa.evaluate("$a=b\n($a).toUpperCase()", opts()), "B");

		assertEq(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx = opts()), exp.toLowerCase());
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal", ctx = opts()), exp);
		assertEq(ctx.get("stored"), "A");

		assertEq(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx = opts()), exp);
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx = opts()), exp);
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx = opts()), exp.toLowerCase());
		assertEq(ctx.get("stored"), "a");
	}

	@Test
	public void resolveInlineTransforms_INLINE() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal.", ctx), "A dog is a mammal.");
		assertEq(RiTa.evaluate("$stored=(a | a).toUpperCase()\n$stored dog is a mammal.", ctx), "A dog is a mammal.");
	}

	@Test
	public void resolveTransformsAcrossTypes_INLINE() {

		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$a=a\n($a).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=a\n($a | $a).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=a\n(A).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=(a).toUpperCase()", ctx), "");
		assertEq(ctx.get("a"), "A");
	}

	@Test
	public void resolveInlineVariables_INLINE() {
		Map<String, Object> ctx = opts();
		String result = RiTa.evaluate("[$stored=(a | b)]", ctx);
		String[] results = { "a", "b" };
		assertTrue(Arrays.asList(results).contains(result));
		assertEq(ctx.get("stored"), result);

		String result2 = RiTa.evaluate("[$a=$stored]", ctx);
		assertEq(ctx.get("a"), result2);
		assertEq(result2, ctx.get("stored"));

		ctx = opts();
		result = RiTa.evaluate("$stored=(a | b)", ctx);
		assertEq(result, "");
		result = (String) ctx.get("stored");
		String[] expec = { "a", "b" };
		assertTrue(Arrays.asList(expec).contains((String) ctx.get("stored")));
		result2 = RiTa.evaluate("$a=$stored", ctx);
		assertEq(ctx.get("a"), ctx.get("stored"));
		assertEq(ctx.get("a"), result);
	}

	@Test
	public void resolveLineBreakDefinedVariables_SYMBOL() {
		String in = "a.\n$b.";
		String out = RiTa.evaluate(in, opts("b", "c"));
		assertEq(out, "a. c.");
		out = RiTa.evaluate("$foo=hello\n$start=I said $foo to her\n$start", opts());
		assertEq(out, "I said hello to her");
		out = RiTa.evaluate("$foo=(hello)\n$start=I said $foo to her\n$start", opts());
		assertEq(out, "I said hello to her");
	}

	@Test
	public void reuseAnAssignedVariables_INLINE() {
		Map<String, Object> ctx = opts();
		String inp = "Once there was a girl called [$hero=(Jane | Jane)].";
		inp += "\n$hero lived in [$home=(Neverland | Neverland)].";
		inp += "\n$hero liked living in $home.";
		String exp = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";
		String out = RiTa.evaluate(inp, ctx);
		//System.out.println(out);
		assertEq(out, exp);

		ctx = opts();
		inp = "Once there was a girl called [$hero=(Jane | Jane)].\n$hero lived in [$home=(Neverland | Neverland)].\n$hero liked living in $home.";
		out = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";
		assertEq(RiTa.evaluate(inp, ctx), out);
	}

	// Symbol

	@Test
	public void returnInputForUndefinedSymbol_SYMBOL() {
		assertEq(RiTa.evaluate("$a", opts(), ST), "$a");
		assertEq(RiTa.evaluate("$a.capitalize()", opts(), ST), "$a.capitalize()");
		assertEq(RiTa.evaluate("The $a.capitalize() dog.", opts(), ST), "The $a.capitalize() dog.");
	}

	@Test
	public void resolveSymbolsWithPropertyTransforms_SYMBOL() {
		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		assertEq(RiTa.evaluate("$foo=$bar.color\n$foo", ctx), "blue");
		assertEq(RiTa.evaluate("$bar.color", ctx), "blue");
	}

	@Test
	public void concatenateSymbolsInParens() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=(h | h)\n($foo)ello", ctx), "hello");
		assertEq(ctx.get("foo"), "h");

		assertEq(RiTa.evaluate("$foo b c", ctx), "h b c");
		assertEq(RiTa.evaluate("($foo) b c", ctx), "h b c");
		assertEq(RiTa.evaluate("($foo)bc", ctx), "hbc");
		assertEq(ctx.get("foo"), "h");

	}

	// Choice
	@Test
	public void throwOnBadChoices_CHOICE() {
		Map<String, Object> ctx = opts();
		assertThrows(RiTaException.class, () -> RiTa.evaluate("|", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a |", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b | c", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("(a | b) | c", ctx, ST));
	}

	@Test
	public void resolveMultiWordChoices_CHOICE() {
		Map<String, Object> ctx = opts();
		boolean silent = RiTa.SILENCE_LTS;
		RiTa.SILENCE_LTS = true;
		assertEq(RiTa.evaluate("(A B | A B)"), "A B");
		assertEq(RiTa.evaluate("(A B).toLowerCase()"), "a b");
		assertEq(RiTa.evaluate("(A B | A B).toLowerCase()", ctx), "a b");
		assertEq(RiTa.evaluate("(A B | A B).articlize()", ctx), "an A B");
		RiTa.SILENCE_LTS = silent;
	}

	@Test
	public void resolveChoicesViaScripting_CHOICE() {
		RiScript interp = RiTa.scripting();
		Map<String, Object> ctx = opts();
		String[] expected;
		assertEq(interp.evaluate("(|)"), "");
		assertEq(interp.evaluate("(a)"), "a");
		assertEq(interp.evaluate("(a | a)", ctx), "a");

		expected = new String[] { "a", "" };
		String rs = interp.evaluate("(a | )");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b" };
		rs = interp.evaluate("(a | b)");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b", "c" };
		rs = interp.evaluate("(a | b | c)");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b", "c", "d" };
		rs = interp.evaluate("(a | (b | c) | d)");
		assertTrue(Arrays.asList(expected).contains(rs));
	}
	
	@Test
	public void resolveChoices_CHOICE() {
		Map<String, Object> ctx = opts();
		String[] expected;
		assertEq(RiTa.evaluate("(|)"), "");
		assertEq(RiTa.evaluate("(a)"), "a");
		assertEq(RiTa.evaluate("(a | a)", ctx), "a");

		expected = new String[] { "a", "" };
		String rs = RiTa.evaluate("(a | )");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b" };
		rs = RiTa.evaluate("(a | b)");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b", "c" };
		rs = RiTa.evaluate("(a | b | c)");
		assertTrue(Arrays.asList(expected).contains(rs));

		expected = new String[] { "a", "b", "c", "d" };
		rs = RiTa.evaluate("(a | (b | c) | d)");
		assertTrue(Arrays.asList(expected).contains(rs));
	}

	@Test
	public void handleSymbolsInMultiwordTransforms_SYMBOL() {
		String res = RiTa.evaluate("($a dog).pluralize()\n$a=the");
		assertEquals("the dogs", res);
	}

	@Test
	public void parseSelectChoicesTX_CHOICE() {
		assertEq(RiTa.evaluate("(a | a).toUpperCase()", opts()), "A");
		assertEq(RiTa.evaluate("(a | a).up()", opts(), ST), "a.up()");
		Function<String, String> up = x -> x.toUpperCase();
		assertEq(RiTa.evaluate("(a | a).up()", opts("up", up)), "A");
		assertEq(RiTa.evaluate("$a", opts("a", 1)), "1");
	}

	@Test
	public void resolveChoicesInExpressions_CHOICE() {

		assertEq(RiTa.evaluate("x (a | a | a) x"), "x a x");
		assertEq(RiTa.evaluate("x (a | a | a)"), "x a");
		assertEq(RiTa.evaluate("x (a | a | a)x"), "x ax");
		assertEq(RiTa.evaluate("x(a | a | a) x"), "xa x");
		assertEq(RiTa.evaluate("x(a | a | a)x"), "xax");
		assertEq(RiTa.evaluate("x (a | a | a) (b | b | b) x"), "x a b x");
		assertEq(RiTa.evaluate("x (a | a | a)(b | b | b) x"), "x ab x");
		assertEq(RiTa.evaluate("x (a | a) (b | b) x"), "x a b x");
		assertEq(RiTa.evaluate("(a|a)"), "a");

		assertTrue(Arrays.asList("a", "b").contains(RiTa.evaluate("(a|b)")));
		assertTrue(Arrays.asList("a", "").contains(RiTa.evaluate("(a|)")));
		assertTrue(Arrays.asList("a", "").contains(RiTa.evaluate("(|a|)")));
	}

	@Test
	public void resolveWeightedChoices_CHOICE() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("( a [2] |a [3] )", ctx), "a");

		Map<String, Integer> result = new HashMap<String, Integer>();
		result.put("a", 0);
		result.put("b", 0);

		for (int i = 0; i < 100; i++) {
			String choice = RiTa.evaluate("(a | b [2])");
			int count = result.get(choice);
			count++;
			result.put(choice, count);
		} // console.log(result);
		assertTrue(result.get("b") > result.get("a"));

		assertEq(RiTa.evaluate("( a [2] )", ctx), "a");
		assertEq(RiTa.evaluate("([2] |[3])", ctx), "");

		String[] results = { "a", "b", "" };
		String rs = RiTa.evaluate("(a | b [2] |[3])", ctx);
		assertTrue(Arrays.asList(results).contains(rs));
		rs = RiTa.evaluate("(a | b[2] |[3])", ctx);
		assertTrue(Arrays.asList(results).contains(rs));
	}

	// Transform
	@Test
	public void resolveAddedTransforms_TRANSFORM() {

		Function<String, String> func = (x) -> "A";
		Map<String, Function<String, String>> txs1 = RiTa.addTransform("capA", func);
		assertEq(RiTa.evaluate(".capA()", null), "A");
		assertEq(RiTa.evaluate("(b).capA()", null), "A");
		Map<String, Function<String, String>> txs2 = RiTa.addTransform("capA", null);
		assertEq(txs1.size(), txs2.size());
	}

	@Test
	public void resolveTransformsInContext_TRANSFORM() {
		Function<String, String> func = (s) -> s != null
				&& s.length() > 0 ? s : "B";
		Map<String, Object> ctx = opts("capB", func);
		assertEq(RiTa.evaluate(".capB()", ctx), "B");
		assertEq(RiTa.evaluate("(c).capB()", ctx), "c");
		assertEq(RiTa.evaluate("(c).toUpperCase()"), "C");
	}

	@Test
	public void resolveNoInputTransforms_TRANSFORM() {

		// set capA()
		Function<String, String> func = (s) -> "A";
		Map<String, Object> ctx = opts("capA", func);
		assertEq(RiTa.evaluate(".capA()", ctx), "A");

		// reset capA() to new function
		func = s -> (s.length() > 0 ? s : "B");
		ctx = opts("capA", func);

		assertEq(RiTa.evaluate(".capA()", ctx), "B");
		assertEq(RiTa.evaluate("().capA()", ctx), "B");
		assertEq(RiTa.evaluate("(A).capA()", ctx), "A");

		// remove capA()
		RiTa.addTransform("capA", (s) -> "A");
		assertEq(RiTa.evaluate(".capA()", opts()), "A");
		RiTa.addTransform("capA", null);
	}

	/*@Test // TODO: add only if we have use cases (tokenize, untokenize?)
	public void testRiTaFunctionTransforms() { // TODO: Handle called RiTa functions?
		RiScript rs = new RiScript();
		Map<String, Object> ctx = opts();
		assertEq(rs.evaluate("Does $RiTa.env() equal node?", ctx), "Does node equal node?");
	}*/

	@Test
	public void resolveSeqTransforms_TRANSFORM() {
		String[] options = { "a", "b", "c", "d" };
		String rule = "(a | b | c | d).seq()";
		RiScript rs = new RiScript();
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule);
			//System.out.println(i+") "+res);
			assertEq(res, options[i]);
		}
		String rule2 = "(a | b | c | d).seq().capitalize()";
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule2);
			assertEq(res, options[i].toUpperCase());
		}
	}

	@Test
	public void resolveRseqTransforms_TRANSFORM() {
		String[] options = { "a", "b", "c", "d" };
		ArrayList<String> result = new ArrayList<String>();
		String rule = "(a | b | c | d).rseq()";
		RiScript rs = new RiScript();
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule);
			result.add(res);
		}
		for (int i = 0; i < result.size(); i++) {
			assertTrue(Arrays.asList(options).contains(result.get(i)));
		}

		String rule2 = "(a | b | c | d).rseq().capitalize()";
		result = new ArrayList<String>();
		String[] expected = { "A", "B", "C", "D" };
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule2);
			result.add(res);
		}
		for (int i = 0; i < result.size(); i++) {
			assertTrue(Arrays.asList(expected).contains(result.get(i)));
		}

		String last = null;
		for (int i = 0; i < options.length * 10; i++) {
			String res = rs.evaluate(rule);
			assertTrue(!res.equals(last));
			last = res;
		}
	}

	@Test
	public void resolveInterleavedSeqTransforms_TRANSFORM() {
		String[] options = { "a", "b", "c", "d" };
		String rule = "(a | b | c | d).seq() (a | b | c | d).seq()";
		RiScript rs = new RiScript();
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule);
			assertEq(res, options[i] + " " + options[i]);
		}
	}

	@Test
	public void resolveInterleavedRseqTransforms_TRANSFORM() {
		String[] options = { "a", "b", "c", "d" };
		String rule = "(a | b | c | d).rseq() (a | b | c | d).rseq()";
		RiScript rs = new RiScript();
		ArrayList<String> res1 = new ArrayList<String>();
		ArrayList<String> res2 = new ArrayList<String>();
		for (int i = 0; i < options.length; i++) {
			String res = rs.evaluate(rule);
			String[] parts = res.split(" ");
			res1.add(parts[0]);
			res2.add(parts[1]);
		}
		for (int i = 0; i < res1.size(); i++) {
			assertTrue(Arrays.asList(options).contains(res1.get(i)));
		}
		for (int i = 0; i < res2.size(); i++) {
			assertTrue(Arrays.asList(options).contains(res2.get(i)));
		}
	}

	@Test
	public void resolveNorepTransforms_TRANSFORM() {
		String rule = "(a | b | c | d).nore()";
		RiScript rs = new RiScript();
		String last = null;
		for (int i = 0; i < 10; i++) {
			String res = rs.evaluate(rule, null);
			//System.out.println(i+") "+res);
			assertTrue(!res.equals(last));
			last = res;
		}
	}

	@Test
	public void resolveChoiceTransforms_TRANSFORM() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx, ST), "");
		assertEq(ctx.get("foo"), "");

		ctx.clear();
		assertEq(RiTa.evaluate("(a).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("((a)).toUpperCase()", ctx), "A");

		String rs = RiTa.evaluate("(a | b).toUpperCase()");
		String[] possibleResults = { "A", "B" };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		assertEq(RiTa.evaluate("(a | a).capitalize()"), "A");
		assertEq(RiTa.evaluate("The (boy | boy).toUpperCase() ate."), "The BOY ate.");
		assertEq(RiTa.evaluate("How many (tooth | tooth).pluralize() do you have?"), "How many teeth do you have?");
	}

	@Test
	public void resolveSymbolTransforms_TRANSFORM() {
		Map<String, Object> ctx = opts();
		ctx.put("dog", "spot");
		assertEq(RiTa.evaluate("$dog.toUpperCase()", ctx), "SPOT");
		assertEq(RiTa.evaluate("$dog.capitalize()", ctx), "Spot");
		assertEq(RiTa.evaluate("($dog).capitalize()", ctx), "Spot");
		ctx.clear();
		assertEq(RiTa.evaluate("$dog.toUpperCase()", ctx, ST), "$dog.toUpperCase()");
		ctx.put("dog", "spot");
		assertEq(RiTa.evaluate("The $dog.toUpperCase()", ctx), "The SPOT");
		assertEq(RiTa.evaluate("The (boy | boy).toUpperCase() ate."), "The BOY ate.");
		assertEq(RiTa.evaluate("The (girl).toUpperCase() ate."), "The GIRL ate.");

		assertEq(RiTa.evaluate("$dog.articlize().capitalize()", ctx), "A spot");
		assertEq(RiTa.evaluate("[$a=$dog] $a.articlize().capitalize()", ctx), "spot A spot");
		ctx.clear();
		ctx.put("dog", "abe");
		RiTa.SILENCE_LTS = true;
		assertEq(RiTa.evaluate("[$a=$dog] $a.articlize().capitalize()", ctx), "abe An abe");
		assertEq(RiTa.evaluate("(abe | abe).articlize().capitalize()", ctx), "An abe");
		assertEq(RiTa.evaluate("(abe | abe).capitalize().articlize()", ctx), "an Abe");
		assertEq(RiTa.evaluate("(Abe Lincoln).articlize().capitalize()", ctx), "An Abe Lincoln");
		assertEq(RiTa.evaluate("<li>$start</li>\n$start=($jrSr).capitalize()\n$jrSr=(junior|junior)"),
				"<li>Junior</li>");
	}

	@Test
	public void resolveObjectProperties_TRANSFORM() {

		class Hair {
			@SuppressWarnings("unused")
			String color = "white";
		}
		class Dog {
			@SuppressWarnings("unused")
			String name = "Spot";
			@SuppressWarnings("unused")
			String color = "white";
			@SuppressWarnings("unused")
			Hair hair = new Hair();
		}
		assertEq(RiTa.evaluate("It was a $dog.hair.color dog.",
				opts("dog", new Dog())),
				"It was a white dog.");
		assertEq(RiTa.evaluate("It was a $dog.color.toUpperCase() dog.",
				opts("dog", new Dog())),
				"It was a WHITE dog.");
		assertEq(RiTa.evaluate("$a.b",
				opts("a", opts("b", 1)), SP),
				"1");
	}

	@Test
	public void resolveMemberFunctions_TRANSFORM() {
		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		Supplier<String> func = () -> {
			return "red";
		};
		dog.put("getColor", func);
		String input = "$dog.name.ucf() was a $dog.getColor() dog.";
		String expected = "Spot was a red dog.";
		assertEq(RiTa.evaluate(input, opts("dog", dog)), expected);
	}

	@Test
	public void resolveTransformsEndingWithPunc_TRANSFORM() {
		String rs = RiTa.evaluate("(a | b).toUpperCase().");
		String[] possibleResults = { "A.", "B." };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		assertEq(RiTa.evaluate("The (boy | boy).toUpperCase()!"), "The BOY!");
		assertEq(RiTa.evaluate("The $dog.toUpperCase()?", opts("dog", "spot")), "The SPOT?");
		assertEq(RiTa.evaluate("The (boy | boy).toUpperCase()."), "The BOY.");

		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		dog.put("color", "white");
		dog.put("hair", opts("color", "white"));
		assertEq(RiTa.evaluate("It was $dog.hair.color.", opts("dog", dog)), "It was white.");
		assertEq(RiTa.evaluate("It was $dog.color.toUpperCase()!", opts("dog", dog)), "It was WHITE!");

		Supplier<String> func = () -> {
			return "red";
		};
		Map<String, Object> col = opts("getColor", func);
		assertEq(RiTa.evaluate("It was $dog.getColor()?", opts("dog", col)), "It was red?");

		Map<String, Object> ctx = opts();
		ctx.put("user", opts("name", "jen"));
		assertEq(RiTa.evaluate("That was $user.name!", ctx), "That was jen!");
		assertEq(RiTa.evaluate("That was $user.name.", ctx), "That was jen.");
	}

	@Test
	public void resolveHandleTransformsOnLiterals_TRANSFORM() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("How many (teeth).toUpperCase() do you have?", ctx), "How many TEETH do you have?");
		assertEq(RiTa.evaluate("How many (teeth).quotify() do you have?", ctx), "How many “teeth” do you have?");
		assertEq(RiTa.evaluate("That is (ant).articlize()."), "That is an ant.");
	}

	@Test
	public void resolveCustomTransforms_TRANSFORM() {
		Function<String, String> blah = s -> "Blah";
		Function<String, String> blah2 = s -> "Blah2";
		assertEq(RiTa.evaluate("That is (ant).blah().", opts("blah", blah)), "That is Blah.");
		Map<String, Object> ctx = opts();
		ctx.put("blah2", blah2);
		assertEq(RiTa.evaluate("That is (ant).blah2().", ctx), "That is Blah2.");

		RiTa.addTransform("blah3", (s) -> "Blah3");
		assertEq(RiTa.evaluate("That is (ant).blah3().", opts()), "That is Blah3.");
		RiTa.addTransform("blah3", null);
		
		Supplier<String> randPos = () -> {
			return "jobArea jobType";
		};
		assertEq(RiTa.evaluate("a .randPos().", opts("randPos", randPos)), "a jobArea jobType.");

	}

	// Grammar
	@Test
	public void evaluatePostDefinedSymbols_GRAMMAR() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=$bar\n$bar=baz\n$foo", ctx), "baz");
	}

	@Test
	public void optimiseViaPreParsing_GRAMMAR() {
		Map<String, Object> ctx = opts("nothing", "NOTHING", "hang", "HANG");
		Map<String, Object> spp = opts();//"skipPreParse", false);
		String input = "Eve near Vancouver, Washington is devastated that the SAT exam was postponed. Junior year means NOTHING if you can\"t HANG out. At least that\"s what she thought. Summer is going to suck.";
		String rs = RiTa.evaluate(input, ctx, spp);
		assertEq(rs, input.replace("$hang", "HANG").replace("$nothing", "NOTHING"));

		input = "Eve near Vancouver,\nWashington is devastated that the SAT exam was postponed. Junior year means NOTHING if you can\"t HANG out. At least that\"s what she thought. Summer is going to suck.";
		rs = RiTa.evaluate(input, ctx, spp);
		assertEq(rs, input.replace("$hang", "HANG").replace("$nothing", "NOTHING").replace("\n", " "));

		input = "Eve&nbsp;near Vancouver";
		rs = RiTa.evaluate(input, ctx);
		assertEq(rs, "Eve near Vancouver");

		input = "This is not a &#124;.";
		rs = RiTa.evaluate(input, ctx, spp);
		assertEq(rs, "This is not a |.");
	}

	@Test
	public void resolveSymbolsWithTransforms_GRAMMAR() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$foo=$bar.toUpperCase()\n$bar=baz\n$foo", ctx);
		assertEq(rs, "BAZ");

		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx, ST), "");
		assertEq(ctx.get("foo"), "");

		assertEq(RiTa.evaluate("$foo.capitalize()\n$foo=(a|a)", null), "A");
		assertEq(RiTa.evaluate("$start=$r.capitalize()\n$r=(a|a)\n$start", ctx), "A");
	}

	@Test
	public void resolveConvertedGrammars_GRAMMAR() {
		Map<String, Object> ctx = opts();
		String script = String.join("\n",
				"$start = $nounp $verbp.",
				"$nounp = $determiner $noun",
				"$determiner = (the | the)",
				"$verbp = $verb $nounp",
				"$noun = (woman | woman)",
				"$verb = shoots",
				"$start") + "\n";
		String rs = RiTa.evaluate(script, ctx);
		assertEq(rs, "the woman shoots the woman.");
	}

	@Test
	public void resolvePriorAssignments_GRAMMAR() {
		assertEq(RiTa.evaluate("$foo=dog\n$bar=$foo\n$baz=$foo\n$baz"), "dog");
		assertEq(RiTa.evaluate("$foo=hi\n$foo there"), "hi there");
		assertEq(RiTa.evaluate("$foo=a\n$foo"), "a");
		String script = "$noun=(woman | woman)\n$start=$noun\n$start";
		assertEq(RiTa.evaluate(script), "woman");
	}

	@Test
	public void resolveTransformPropertiesAndMethods_TRANSFORM() {
		Map<String, Object> ctx = opts("bar", new MockClass());
		String rs = RiTa.evaluate("$foo=$bar.prop\n$foo", ctx);
		assertEq(rs, "result");
		rs = RiTa.evaluate("$foo=$bar.getProp()\n$foo", ctx);
		assertEq(rs, "result");
	}

	// Entities

	@Test
	public void decodeHTMLEntities_ENTITIES() {
		assertEq(RiTa.evaluate("The &num; symbol"), "The # symbol");
		assertEq(RiTa.evaluate("The &#x00023; symbol"), "The # symbol");
		assertEq(RiTa.evaluate("The &#35; symbol", null), "The # symbol");
		assertEq(RiTa.evaluate("The&num;symbol"), "The#symbol");

		String[] arr = { "&lsqb;", "&lbrack;", "&#x0005B;", "&#91;" };
		for (String e : arr) {
			assertEq(RiTa.evaluate("The " + e + " symbol"), "The [ symbol");
		}
		String[] arr2 = { "&rsqb;", "&rbrack;", "&#x0005D;", "&#93;" };
		for (String e : arr2) {
			assertEq(RiTa.evaluate("The " + e + " symbol"), "The ] symbol", e);
		}
		
		assertEq("This is $100", RiTa.evaluate("This is &#36;100"));
		assertEq("This is $100", RiTa.evaluate("This is &#x00024;100"));
	}

	@Test
	public void allowBasicPunctuation_ENTITIES() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("The -;:.!?\'`", ctx), "The -;:.!?'`");
		assertEq(RiTa.evaluate("The -;:.!?\"`", ctx), "The -;:.!?\"`");
		assertEq(RiTa.evaluate(",.;:'?!-_`“”’‘…‐–—―^*", ctx), ",.;:'?!-_`“”’‘…‐–—―^*");
		assertEq(RiTa.evaluate(",.;:\"?!-_`“”’‘…‐–—―^*", ctx), ",.;:\"?!-_`“”’‘…‐–—―^*");
		assertEq(RiTa.evaluate("/&%©@*"), "/&%©@*");
	}

	@Test
	public void allowSpaceForFormatting_ENTITIES() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("&nbsp;The dog&nbsp;", ctx), " The dog ");
		assertEq(RiTa.evaluate("&nbsp; The dog&nbsp;", ctx), "  The dog ");
		assertEq(RiTa.evaluate("The &nbsp;dog", ctx), "The  dog");
		assertEq(RiTa.evaluate("The&nbsp; dog", ctx), "The  dog");
		assertEq(RiTa.evaluate("The &nbsp; dog", ctx), "The   dog");

	}

	@Test
	public void showLiteralDollarSigns_ENTITIES() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("This is &#x00024", ctx), "This is $");
		assertEq(RiTa.evaluate("This is &#36", ctx), "This is $");
		ctx = opts("dollar", "&#36");
		assertEq(RiTa.evaluate("This is $dollar", ctx), "This is $");
	}

	// Operators
	@Test
	public void invokeAssignmentOperators_OPERATORS() {
		assertEq(Operator.EQ.invoke("hello", "hello"), true);
		assertEq(Operator.EQ.invoke("hello", ""), false);
		assertEq(Operator.EQ.invoke("hello", null), false);

		assertEq(Operator.NE.invoke("hello", "hello"), false);
		assertEq(Operator.NE.invoke("hello", ""), true);
		assertEq(Operator.NE.invoke("hello", null), true);

		assertEq(Operator.EQ.invoke("true", "false"), false);
		assertEq(Operator.EQ.invoke("false", "false"), true);
		assertEq(Operator.EQ.invoke("false", null), false);

		assertEq(Operator.NE.invoke("hello", ""), true);
		assertEq(Operator.NE.invoke("hello", "false"), true);

		assertThrows(RiTaException.class, () -> Operator.NE.invoke(null, null));
	}

	@Test
	public void invokeEqualityOperators_OPERATORS() {
		assertEq(Operator.EQ.invoke("hello", "hello"), true);
		assertEq(Operator.EQ.invoke("hello", ""), false);
		assertEq(Operator.EQ.invoke("hello", null), false);

		assertEq(Operator.NE.invoke("hello", "hello"), false);
		assertEq(Operator.NE.invoke("hello", ""), true);
		assertEq(Operator.NE.invoke("hello", null), true);

		assertEq(Operator.EQ.invoke("true", "false"), false);
		assertEq(Operator.EQ.invoke("false", "false"), true);
		assertEq(Operator.EQ.invoke("false", null), false);

		assertEq(Operator.NE.invoke("hello", ""), true);
		assertEq(Operator.NE.invoke("hello", "false"), true);

		assertThrows(RiTaException.class, () -> Operator.NE.invoke(null, null));
	}

	@Test
	public void invokeComparisonOperators_OPERATORS() {

		assertEq(Operator.GT.invoke("2", "1"), true);
		assertEq(Operator.GT.invoke("1", "2"), false);
		assertEq(Operator.GT.invoke("1", "1"), false);
		assertEq(Operator.GT.invoke("2.0", "1"), true);
		assertEq(Operator.GT.invoke("1.0", "2"), false);
		assertEq(Operator.GT.invoke("1.0", "1"), false);
		assertEq(Operator.GT.invoke("2.0", "1.00"), true);
		assertEq(Operator.GT.invoke("1.0", "2.00"), false);
		assertEq(Operator.GT.invoke("1.0", "1.00"), false);

		assertEq(Operator.LT.invoke("2", "1"), false);
		assertEq(Operator.LT.invoke("1", "2"), true);
		assertEq(Operator.LT.invoke("1", "1"), false);
		assertEq(Operator.LT.invoke("2.0", "1"), false);
		assertEq(Operator.LT.invoke("1.0", "2"), true);
		assertEq(Operator.LT.invoke("1.0", "1"), false);
		assertEq(Operator.LT.invoke("2.0", "1.00"), false);
		assertEq(Operator.LT.invoke("1.0", "2.00"), true);
		assertEq(Operator.LT.invoke("1.0", "1.00"), false);

		assertEq(Operator.LE.invoke("2", "1"), false);
		assertEq(Operator.LE.invoke("1", "2"), true);
		assertEq(Operator.LE.invoke("1", "1"), true);
		assertEq(Operator.LE.invoke("2.0", "1"), false);
		assertEq(Operator.LE.invoke("1.0", "2"), true);
		assertEq(Operator.LE.invoke("1.0", "1"), true);
		assertEq(Operator.LE.invoke("2.0", "1.00"), false);
		assertEq(Operator.LE.invoke("1.0", "2.00"), true);
		assertEq(Operator.LE.invoke("1.0", "1.00"), true);

		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", ""));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", "h"));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("", ""));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", null));
	}

	@Test
	public void resolveTransformedSymbolsInContext_SYMBOL() {

		Map<String, Object> ctx = opts("a", "(terrier | terrier)");
		assertEq(RiTa.evaluate("$a.capitalize()", ctx), "Terrier");
	}

	// Conditionals ======================================================

	@Test
	public void throwOnBadConditionals_CONDITIONALS() {

		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertThrows(RiTaException.class, () -> RiTa.evaluate("{$a<} foo", ctx, ST));
	}

	@Test
	public void resolveConditionals_CONDITIONALS() {

		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertEq(RiTa.evaluate("{$a<1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>1} foo", ctx), "foo");
		ctx.clear();

		ctx.put("a", "hello");
		assertEq(RiTa.evaluate("{$a=hello} foo", ctx), "foo");
		assertEq(RiTa.evaluate("{$a=goodbye} foo", ctx), "");
	}

	@Test
	public void resolveFloatConditionals_CONDITIONALS() {

		Map<String, Object> ctx = opts();
		ctx.put("a", 2);

		assertEq(RiTa.evaluate("{$a<1.1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>1.1} foo", ctx), "foo");
		assertEq(RiTa.evaluate("{$a<.1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>.1} foo", ctx), "foo");
		assertEq(RiTa.evaluate("{$a<0.1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>0.1} foo", ctx), "foo");

		ctx.clear();
		ctx.put("a", .1);
		assertEq(RiTa.evaluate("{$a<0.1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>=0.1} foo", ctx), "foo");
	}

	@Test
	public void resolveMultivalConditionals_CONDITIONALS() {
		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertEq(RiTa.evaluate("{$a<1,$b<1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>1,$b<1} foo", ctx), "");

		ctx.put("b", 2);
		assertEq(RiTa.evaluate("{$a>1,$b<1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a=ok,$b>=1} foo", ctx), "");
		assertEq(RiTa.evaluate("{$a>1,$b>=1} foo", ctx), "foo");
	}

	@Test
	public void resolveMatchingConditional_CONDITIONALS() {

		Map<String, Object> ctx = opts();
		ctx.put("a", "hello");

		assertEq(RiTa.evaluate("{$a!=ell} foo", ctx), "foo");
		assertEq(RiTa.evaluate("{$a*=ell} foo", ctx), "foo");

		ctx.clear();
		ctx.put("a", "ello");
		assertEq(RiTa.evaluate("{$a^=ell} foo", ctx), "foo");
		ctx.clear();
		ctx.put("a", "helloell");
		assertEq(RiTa.evaluate("{$a$=ell} foo", ctx), "foo");
		ctx.clear();
		ctx.put("a", "helloellx");
		assertEq(RiTa.evaluate("{$a$=ell} foo", ctx), "");

	}

	@Test
	public void resolveConditionalsInRiscript_CONDITIONALS() {

		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("$a=hello\n{$a!=ell} foo", ctx), "foo");
		assertEq(RiTa.evaluate("$a=hello\n{$a*=ell} foo", ctx), "foo");
		assertEq(RiTa.evaluate("$a=ello\n{$a^=ell} foo", ctx), "foo");
		assertEq(RiTa.evaluate("$a=helloell\n{$a$=ell} foo", ctx), "foo");
		assertEq(RiTa.evaluate("$a=helloellx\n{$a$=ell} foo", ctx), "");
	}

	private static void assertEq(Object a, Object b) { // swap order of args
		assertEquals(b, a);
	}

	private static void assertEq(Object a, Object b, String msg) { // swap order of args
		assertEquals(b, a, msg);
	}

	public static void main(String[] args) {
		//System.out.println(RiTa.evaluate("(Abe Lincoln).articlize().capitalize()"));
	}
}
