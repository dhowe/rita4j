package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import rita.*;
import static rita.Util.opts;

public class RiScriptTests {
	Map<String, Object> tf = opts("trace", false);

	@Test
	public void testIsParseable() {
		assertTrue(!RiScript.isParseable("Hello"));
		assertTrue(RiScript.isParseable("("));
		assertTrue(RiScript.isParseable("(A | B)"));
		assertTrue(!RiScript.isParseable("$hello"));
	}

	@Test
	public void testNestedContext() {
		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		String res = RiTa.evaluate("$foo=$bar.color\n$foo", ctx, opts("trace", false));
	}

	// Conditional

	@Test
	public void testBadConditionals() {
		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertThrows(RiTaException.class, () -> RiTa.evaluate("{$a<} foo", ctx, opts("silent", true)));
	}

	@Test
	public void testConditionals() {
		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertEquals(RiTa.evaluate("{$a<1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>1} foo", ctx, tf), "foo");
		ctx.clear();
		ctx.put("a", "hello");
		assertEquals(RiTa.evaluate("{$a=hello} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("{$a=goodbye} foo", ctx, tf), "");
	}

	@Test
	public void testFloatConditionals() {
		Map<String, Object> ctx = opts();
		ctx.put("a", 2);

		assertEquals(RiTa.evaluate("{$a<1.1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>1.1} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("{$a<.1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>.1} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("{$a<0.1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>0.1} foo", ctx, tf), "foo");

		ctx.clear();
		ctx.put("a", .1);
		assertEquals(RiTa.evaluate("{$a<0.1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>=0.1} foo", ctx, tf), "foo");

	}

	@Test
	public void testMultivalConditionals() {
		Map<String, Object> ctx = opts();
		ctx.put("a", 2);

		assertEquals(RiTa.evaluate("{$a<1,$b<1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>1,$b<1} foo", ctx, tf), "");

		ctx.put("b", 2);
		assertEquals(RiTa.evaluate("{$a>1,$b<1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a=ok,$b>=1} foo", ctx, tf), "");
		assertEquals(RiTa.evaluate("{$a>1,$b>=1} foo", ctx, tf), "foo");
	}

	@Test
	public void testMatchingConditional() {
		Map<String, Object> ctx = opts();
		ctx.put("a", "hello");

		assertEquals(RiTa.evaluate("{$a!=ell} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("{$ell} foo", ctx, tf), "foo");

		ctx.clear();
		ctx.put("a", "ello");
		assertEquals(RiTa.evaluate("{$a^=ell} foo", ctx, tf), "foo");
		ctx.clear();
		ctx.put("a", "helloell");
		assertEquals(RiTa.evaluate("{$a$=ell} foo", ctx, tf), "foo");
		ctx.clear();
		ctx.put("a", "helloellx");
		assertEquals(RiTa.evaluate("{$a$=ell} foo", ctx, tf), "");

	}

	@Test
	public void testRSMatchingConditional() {
		Map<String, Object> ctx = opts();

		assertEquals(RiTa.evaluate("$a=hello\n{$a!=ell} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("$a=hello\n{$ell} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("$a=ello\n{$a^=ell} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("$a=helloell\n{$a$=ell} foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("$a=helloellx\n{$a$=ell} foo", ctx, tf), "foo");

	}

	// Evaluation

	@Test
	public void testEvalSimpleExpressions() {
		Map<String, Object> ctx = opts();

		assertEquals(RiTa.evaluate("foo", ctx, tf), "foo");
		assertEquals(RiTa.evaluate("foo.", ctx, tf), "foo.");
		assertEquals(RiTa.evaluate("\"foo\"", ctx, tf), "\"foo\"");
		assertEquals(RiTa.evaluate("'foo'", ctx, tf), "'foo'");
		assertEquals(RiTa.evaluate("foo\nbar", ctx, tf), "foo bar");
		assertEquals(RiTa.evaluate("foo&#10;bar", ctx, tf), "foo\nbar");
		assertEquals(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx, tf), "bar baz");
		assertEquals(RiTa.evaluate("$foo=bar\nbaz", ctx, tf), "baz");
		assertEquals(RiTa.evaluate("$foo=bar\nbaz\n$foo", ctx, tf), "baz bar");

		ctx.put("a", "a");
		ctx.put("b", "b");
		assertEquals(RiTa.evaluate("(a|a)", ctx), "a");

		// assertEquals(RiTa.evaluate("foo.bar", ctx, tf), "foo.bar"); // KNOWN ISSUE

	}

	@Test
	public void testEvalRecursiveExpressions() {
		Map<String, Object> ctx = opts("a", "a", "b", "b");
		assertEquals(RiTa.evaluate("(a|a)", ctx), "a");

		ctx.clear();
		ctx.put("a", "$b");
		ctx.put("b", "(c | c)");
		assertEquals(RiTa.evaluate("$a", ctx), "c");
		assertEquals(RiTa.evaluate("$k = $a\n$k", ctx), "c");
		assertEquals(RiTa.evaluate("$s = $a\n$a = $b\n$c = $d\n$d = c\n$s", ctx), "c");

		ctx.clear();
		ctx.put("s", "$a");
		ctx.put("a", "$b");
		ctx.put("c", "$d");
		ctx.put("d", "c");
		assertEquals(RiTa.evaluate("$s", ctx), "c");
	}

	// Assign
	@Test
	public void testParseAssignments() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=a", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(a) b", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a b");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(a | a)", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=ab", ctx, tf), "");
		assertEquals(ctx.get("foo"), "ab");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=ab bc", ctx, tf), "");
		assertEquals(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(ab) (bc)", ctx, tf), "");
		assertEquals(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(ab bc)", ctx, tf), "");
		assertEquals(ctx.get("foo"), "ab bc");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(a | a) (b | b)", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a b");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=((a | a) | (a | a))", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=a\n$bar=$foo", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=a\n$bar=$foo.", ctx, tf), ""); // empty string
		assertEquals(ctx.get("foo"), "a");
		assertEquals(ctx.get("bar"), "a.");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(a | a)", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(a | a)\n$foo", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=(hi | hi)\n$foo there", ctx, tf), "");
		assertEquals(ctx.get("foo"), "hi");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=The boy walked his dog", ctx, tf), "");
		assertEquals(ctx.get("foo"), "The boy walked his dog");

	}

	@Test
	public void testHandleSentences() {

		Map<String, Object> ctx = opts();
		// known issue in js
		assertEquals(RiTa.evaluate("$foo=.r", ctx), "");
		assertEquals(ctx.get("foo"), ".r");

		ctx.clear();
		assertEquals(RiTa.evaluate(".", ctx, tf), ".");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=a", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=.", ctx, tf), "");
		assertEquals(ctx.get("foo"), ".");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=r.", ctx, tf), "");
		assertEquals(ctx.get("foo"), "r.");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=ran.", ctx, tf), "");
		assertEquals(ctx.get("foo"), "ran.");

		ctx.clear();
		assertEquals(RiTa.evaluate("$start=dog\n$start", ctx, tf), "dog");
		assertEquals(ctx.get("start"), "dog");

		ctx.clear();
		assertEquals(RiTa.evaluate("$start=.\n$start", ctx, tf), ".");
		assertEquals(ctx.get("start"), ".");

		ctx.clear();
		assertEquals(RiTa.evaluate("$noun=I\n$start=$noun ran.\n$start", ctx, tf), "I ran.");
		assertEquals(ctx.get("noun"), "I");

		ctx.clear();
		assertEquals(RiTa.evaluate("$noun=I\n$verb=sat\n$start=$noun $verb.\n$start", ctx, tf), "I ran.");
		assertEquals(ctx.get("noun"), "I");
		assertEquals(ctx.get("verb"), "sat");
		assertEquals(ctx.get("res"), "I sat.");

	}

	@Test
	public void testParseTransformedAssignments() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=(a).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "A");

		assertEquals(RiTa.evaluate("$foo=(a | a).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "A");

		assertEquals(RiTa.evaluate("$foo=(ab).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "AB");

		assertEquals(RiTa.evaluate("$foo=(ab).toUpperCase() (bc).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "AB BC");

		assertEquals(RiTa.evaluate("$foo=(ab bc).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "AB BC");

		assertEquals(RiTa.evaluate("$foo=(a | a).toUpperCase() (b | b).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "A B");

		assertEquals(RiTa.evaluate("$foo=((a | a) | (a | a))", ctx, tf), "");
		assertEquals(ctx.get("foo"), "a");

		assertEquals(RiTa.evaluate("$foo=.toUpperCase()", ctx, tf), "");// empty string
		assertEquals(ctx.get("foo"), "");

	}

	@Test
	public void testTransformsOnLiterals() {
		assertEquals(RiTa.evaluate("How many (teeth).quotify() do you have?"), "How many \"teeth\" do you have?");
		// NEXT: CONSIDER adding context to RiTa.Grammar/grammar.expand
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("That is (ant).articlize().", ctx, tf), "That is an ant.");
		assertEquals(RiTa.evaluate("That is an (ant).articlize()."), "That is an Ant.");

	}

	// it.only("Should handle silents", () => {
	// assertEquals(RiTa.evaluate("The $hero=blue (dog | dog)", ctx, tf),"The blue
	// dog"); assertEquals(ctx.foo,"blue"); });

	// Inline
	@Test
	public void testEvaluateInlineAssignsToVars() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$person=(a | b | c)\n[$a=$person] is $a", ctx, tf);
		String[] possibleResults = { "a is a", "b is b", "c is c" };

		assertTrue(Arrays.asList(possibleResults).contains(rs));

		ctx.put("name", "(Dave1 | Dave2)");
		rs = RiTa.evaluate("$name=(Dave1 | Dave2)\n[$stored=$name] is $stored", ctx, tf);

		String[] possibleNames = { "Dave1", "Dave2" };
		assertTrue(Arrays.asList(possibleNames).contains(ctx.get("stored")));

		String[] possibleResult2 = { "Dave1 is Dave1", "Dave2 is Dave2" };
		assertTrue(Arrays.asList(possibleResult2).contains(rs));

		rs = RiTa.evaluate("$name=(Dave | Dave)\n[$stored=$name] is called $stored", ctx, tf);
		assertEquals(rs, "Dave is called Dave");
	}

	@Test
	public void tesetEvaluateBasicInlineAssigns() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("[$foo=hi]", ctx, tf), "hi");
		assertEquals(RiTa.evaluate("[$foo=(hi | hi)] there", ctx, tf), "hi there");
		assertEquals(RiTa.evaluate("[$foo=(hi | hi).ucf()] there", ctx, tf), "Hi there");

		assertEquals(RiTa.evaluate("$foo=(hi | hi)\n$foo there", ctx, tf),
				RiTa.evaluate("[$foo=(hi | hi)] there", ctx, tf));

		String exp = "A dog is a mammal";
		assertEquals(RiTa.evaluate("$a=a\n($a).toUpperCase()", ctx, tf), "A");

		assertEquals(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx, tf), exp.toLowerCase());
		assertEquals(ctx.get("stored"), "a");

		assertEquals(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal", ctx, tf), exp);
		assertEquals(ctx.get("stored"), "A");

		assertEquals(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx, tf), exp);
		assertEquals(ctx.get("stored"), "a");

		assertEquals(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx), exp);
		assertEquals(ctx.get("stored"), "a");

		assertEquals(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx), exp.toLowerCase());
		assertEquals(ctx.get("stored"), "a");

	}

	@Test
	public void testAssignTransforms() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal.", ctx), "A dog is a mammal.");
		assertEquals(RiTa.evaluate("[$stored=(a | a).toUpperCase()]\n$stored dog is a mammal.", ctx), "A dog is a mammal.");

	}

	@Test
	public void testTransformsOfExprType() {

		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$a=a\n($a).toUpperCase()", ctx, tf), "A");
		assertEquals(RiTa.evaluate("$a=a\n($a | $a).toUpperCase()", ctx, tf), "A");
		assertEquals(RiTa.evaluate("$a=a\n(A).toUpperCase()", ctx, tf), "A");
		assertEquals(RiTa.evaluate("$a=(a).toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("a"), "A");

	}

	@Test
	public void testAssignVariableToResult() {
		Map<String, Object> ctx = opts();
		String result = RiTa.evaluate("[$stored=(a | b)]", ctx);
		String[] results = { "a", "b" };
		assertTrue(Arrays.asList(results).contains(result));
		assertEquals(ctx.get("stored"), result);

		String result2 = RiTa.evaluate("[$a=$stored]", ctx, tf);
		assertEquals(ctx.get("a"), result2);
		assertEquals(result2, ctx.get("stored"));
	}

	@Test
	public void testAssignSilentVariableToResult() {
		Map<String, Object> ctx = opts();
		String result = RiTa.evaluate("[$stored=(a | b)]", ctx);
		assertEquals(result, "");
		result = ctx.get("stored");
		String[] results = { "a", "b" };
		assertTrue(Arrays.asList(results).contains(result));

		String result2 = RiTa.evaluate("$a=$stored", ctx);
		assertEquals(result2, "");
		assertEquals(ctx.get("a"), ctx.get("stored"));
		assertEquals(ctx.get("a"), result);

	}

	@Test
	public void testAssignAVariableToCode() {
		Map<String, Object> ctx = opts();
		ctx.put("animal", "dog");
		assertEquals(RiTa.evaluate("A [$stored=($animal | $animal)] is a mammal", ctx), "A dog is a mammal");
		ctx.clear();
		assertEquals(RiTa.evaluate("[$b=(a | a).toUpperCase()] dog is a $b.", ctx, tf), "A dog is a A.");
	}

	@Test
	public void testReuseAnAssignedVariable() {
		Map<String, Object> ctx = opts();
		String inp = "Once there was a girl called [$hero=(Jane | Jane)].";
		inp += "\n$hero lived in [$home=(Neverland | Neverland)].";
		inp += "\n$hero liked living in $home.";
		String out = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";

		assertEquals(RiTa.evaluate(inp, ctx), out);
	}

	@Test
	public void testAssignASilentVariableToCode() {
		Map<String, Object> ctx = opts();
		/*
		 * assertEquals(RiTa.evaluate("A [$stored=($animal | $animal)] is a mammal",
		 * opts("animal", "dog"), tf),"A dog is a mammal");
		 * assertEquals(RiTa.evaluate("[$b=(a | a).toUpperCase()] dog is a $b.", ctx,
		 * tf),"A dog is a A.");
		 */
		assertEquals(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a $b.toLowerCase().", ctx, tf), "A dog is a a.");
		assertEquals(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a ($b).toLowerCase().", ctx, tf), "A dog is a a.");
	}

	@Test
	public void testReuseSilentAssignedVariables() {
		Map<String, Object> ctx = opts();
		String inp = "Once there was a girl called [$hero=(Jane | Jane)].\n$hero lived in [$home=(Neverland | Neverland)].\n$hero liked living in $home.";
		String out = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";
		assertEquals(RiTa.evaluate(inp, ctx), out);
	}

	// Symbol

	@Test
	public void testBadSymbols() {
		Map<String, Object> ctx = opts();
		assertThrows(RiTaException.class, () -> RiTa.evaluate("$", ctx, opts("silent", true)));
	}

	@Test
	public void testRiScriptInContextSymbol() {
		Map<String, Object> ctx = opts("name", "(Dave | Dave)");
		assertThrows(RiTaException.class, () -> RiTa.evaluate("[$stored=$name] is called $stored", ctx, opts("trace", true)));
	}

	@Test
	public void testLinebreakDefinedVariables() {
		String res;
		Map<String, Object> ctx = opts();

		res = RiTa.evaluate("$foo=hello\n$start=I said $foo to her\n$start", ctx, tf);
		assertEquals(res, "I said hello to her");
		res = RiTa.evaluate("$foo=(hello)\n$start=I said $foo to her\n$start", ctx, tf);
		assertEquals(res, "I said hello to her");
	}

	@Test
	public void testSymbolsInContext() {

		Map<String, Object> ctx = opts();
		ctx.put("a", "(terrier | terrier)");
		assertEquals(RiTa.evaluate("$a.capitalize()", ctx, tf), "Terrier");

		ctx.clear();
		ctx.put("dog", "terrier");
		assertEquals(RiTa.evaluate("the $dog ate", ctx, tf), "the terrier ate");

		ctx.put("verb", "ate");
		assertEquals(RiTa.evaluate("the $dog $verb", ctx, tf), "the terrier ate");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo", ctx, opts("silent", true)), "$foo");
		assertEquals(RiTa.evaluate("a $foo dog", ctx, opts("silent", true)), "a $foo dog");

		ctx.put("foo", "bar");
		assertEquals(RiTa.evaluate("$foo", ctx, tf), "bar");
		ctx.clear();
		ctx.put("dog", "terrier");
		assertEquals(RiTa.evaluate("a $dog", ctx), "a terrier");

		ctx.clear();
		ctx.put("dog", "beagle");
		assertEquals(RiTa.evaluate("I ate the $dog", ctx, tf), "I ate the beagle");
		ctx.clear();
		ctx.put("dog", "lab");
		assertEquals(RiTa.evaluate("The $dog today.", ctx, tf), "The lab today.");
		assertEquals(RiTa.evaluate("I ate the $dog.", ctx, tf), "I ate the lab.");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo\n", ctx, opts("silent", true)), "$foo");
		assertEquals(RiTa.evaluate("a $foo\ndog", ctx, opts("silent", true)), "a $foo dog");
		ctx.put("foo", "bar");
		assertEquals(RiTa.evaluate("$foo\n", ctx, tf), "bar");
		ctx.clear();
		ctx.put("dog", "terrier");
		assertEquals(RiTa.evaluate("a $dog", ctx), "a terrier");
		ctx.clear();
		ctx.put("dog", "beagle");
		assertEquals(RiTa.evaluate("I ate\nthe $dog", ctx, tf), "I ate the beagle");
		ctx.clear();
		ctx.put("dog", "lab");
		assertEquals(RiTa.evaluate("The $dog\ntoday.", ctx, tf), "The lab today.");
		assertEquals(RiTa.evaluate("I ate the\n$dog.", ctx, tf), "I ate the lab.");

		ctx.clear();
		ctx.put("dog", "terrier");
		assertEquals(RiTa.evaluate("$100 is a lot of $dog.", ctx, tf), "$100 is a lot of terrier.");
		assertEquals(RiTa.evaluate("the $dog cost $100", ctx, tf), "the terrier cost $100");
		assertEquals(RiTa.evaluate("the $dog cost $100!", ctx, tf), "the terrier cost $100!");
		assertEquals(RiTa.evaluate("the $dog costot", ctx, tf), "the terrier costot");
		assertEquals(RiTa.evaluate("the $dog^1 was a footnote.", ctx, tf), "the terrier^1 was a footnote.");

	}

	@Test
	public void TestPreciouslyDefinedSymbols() {
		Map<String, Object> ctx = opts();
		ctx.put("dog", "terrier");
		assertEquals(RiTa.evaluate("the $dog ate", ctx, tf), "the terrier ate");
		ctx.put("verb", "ate");
		assertEquals(RiTa.evaluate("the $dog $verb", ctx, tf), "the terrier ate");

		ctx.clear();
		assertEquals(RiTa.evaluate("$foo=bar\n$foo", ctx, tf), "bar");
		assertEquals(RiTa.evaluate("$dog=terrier\na $dog", ctx), "a terrier");
		assertEquals(RiTa.evaluate("$dog=beagle\nI ate the $dog", ctx, tf), "I ate the beagle");
		assertEquals(RiTa.evaluate("$dog=lab\nThe $dog today.", ctx, tf), "The lab today.");
		assertEquals(RiTa.evaluate("$dog=lab\nI ate the $dog.", ctx, tf), "I ate the lab.");
		assertEquals(RiTa.evaluate("$dog=lab\nThe $dog\ntoday.", ctx, tf), "The lab today.");
		assertEquals(RiTa.evaluate("$dog=lab\nI ate the\n$dog.", ctx, tf), "I ate the lab.");

		assertEquals(RiTa.evaluate("$foo=baz\n$bar=$foo\n$bar", ctx, tf), "baz");

		// from known-issues
		ctx.put("foo", "baz");
		ctx.put("bar", "$foo");
		assertEquals(RiTa.evaluate("$bar", ctx), "baz");
		ctx.clear();
		ctx.put("foo", "baz");
		ctx.put("bar", "(A | A)");
		assertEquals(RiTa.evaluate("$bar", ctx), "A");
		ctx.clear();
		ctx.put("foo", "baz");
		ctx.put("bar", "$foo starts with (b | b)");
		assertEquals(RiTa.evaluate("$bar", ctx), "baz starts with b");
		assertEquals(RiTa.evaluate("$start=$foo\n$foo=hello\n$start"), "hello");
		assertEquals(RiTa.evaluate("$start = $noun\n$noun = hello\n$start"), "hello");
	}

	@Test
	public void TestSymbolsFromContext() {
		Map<String, Object> ctx = opts();
		ctx.put("user", opts("name", "jen"));

		assertEquals(RiTa.evaluate("Was $user.name.ucf() (ok | ok) today?", ctx), "Was Jen ok today?");
		assertEquals(RiTa.evaluate("$user.name was ok", ctx), "jen was ok");
		assertEquals(RiTa.evaluate("That was $user.name", ctx), "That was jen");
		assertEquals(RiTa.evaluate("Was that $user.name.ucf()?", ctx), "Was that Jen?");
		assertEquals(RiTa.evaluate("$user.name", ctx), "jen?");
		assertEquals(RiTa.evaluate("$user.name.toUpperCase()", ctx, tf), "JEN");
		assertEquals(RiTa.evaluate("$user.name.uc()", ctx, tf), "JEN");
		assertEquals(RiTa.evaluate("$user.name.ucf()", ctx, tf), "Jen");

		ctx.clear();
		ctx.put("dog", opts("breed", "Corgie"));
		assertEquals(RiTa.evaluate("Was the $dog.breed (ok | ok) today?", ctx, tf), "Was the Corgie ok today?");

	}

	@Test
	public void TestSymbolsWithPropertyTransforms() {
		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		assertEquals(RiTa.evaluate("$foo=$bar.color\n$foo", ctx, tf), "blue");
		assertEquals(RiTa.evaluate("$bar.color", ctx), "blue");

	}

	@Test
	public void TestConcatenateVariables() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=(h | h)\n($foo)ello", ctx, tf), "hello");
		assertEquals(ctx.get("foo"), "h");

		assertEquals(RiTa.evaluate("$foo b c", ctx, tf), "h b c");
		assertEquals(RiTa.evaluate("($foo) b c", ctx, tf), "h b c");
		assertEquals(RiTa.evaluate("($foo)bc", ctx, tf), "hbc");
		assertEquals(ctx.get("foo"), "h");

	}

	// Choice
	@Test
	public void TestBadChoices() {
		Map<String, Object> ctx = opts();
		Map<String, Object> st = opts("silent", true);

		assertThrows(RiTaException.class, () -> RiTa.evaluate("|", ctx, st));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a |", ctx, st));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b", ctx, st));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b | c", ctx, st));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("(a | b) | c", ctx, st));
		
	}

	@Test
	public void TestMultiWordChoices() {
		Map<String, Object> ctx = opts();
		boolean silent = RiTa.SILENCE_LTS;
		RiTa.SILENCE_LTS = true;
		assertEquals(RiTa.evaluate("(A B | A B)"), "A B");
		assertEquals(RiTa.evaluate("(A B).toLowerCase()"), "a b");
		assertEquals(RiTa.evaluate("(A B | A B).toLowerCase()", ctx, tf), "a b");
		assertEquals(RiTa.evaluate("(A B | A B).articlize()", ctx, tf), "an A B");
		RiTa.SILENCE_LTS = silent;
	}

	@Test
	public void TestParseSelectChoices() {
		Map<String, Object> ctx = opts();

		assertEquals(RiTa.evaluate("(|)"), "");
		assertEquals(RiTa.evaluate("(a)"), "a");
		assertEquals(RiTa.evaluate("(a | a)", ctx, tf), "a");

		String[] results = { "a", "" };
		String rs = RiTa.evaluate("(a | )");
		assertTrue(Arrays.asList(results).contains(rs));

		results[1] = "b";
		rs = RiTa.evaluate("(a | b)");
		assertTrue(Arrays.asList(results).contains(rs));

		results[2] = "c";
		rs = RiTa.evaluate("(a | b | c)");
		assertTrue(Arrays.asList(results).contains(rs));

		results[3] = "d";
		rs = RiTa.evaluate("(a | (b | c) | d)");
		assertTrue(Arrays.asList(results).contains(rs));

	}

	@Test
	public void TestParseChoicesFromAnExpression() {

		assertEquals(RiTa.evaluate("x (a | a | a) x"), "x a x");
		assertEquals(RiTa.evaluate("x (a | a | a)"), "x a");
		assertEquals(RiTa.evaluate("x (a | a | a)x"), "x ax");
		assertEquals(RiTa.evaluate("x(a | a | a) x"), "xa x");
		assertEquals(RiTa.evaluate("x(a | a | a)x"), "xax");
		assertEquals(RiTa.evaluate("x (a | a | a) (b | b | b) x"), "x a b x");
		assertEquals(RiTa.evaluate("x (a | a | a)(b | b | b) x"), "x ab x");
		assertEquals(RiTa.evaluate("x (a | a) (b | b) x"), "x a b x");
		String rs = RiTa.evaluate("(a|b)");
		assertTrue(rs.matches("/a|b/"));

		rs = RiTa.evaluate("(a|)");
		assertTrue(rs.matches("/a?/"));

		assertEquals(RiTa.evaluate("(a|a)"), "a");

		String[] results = { "a", "" };
		rs = RiTa.evaluate("(|a|)");
		assertTrue(Arrays.asList(results).contains(rs));

	}

	@Test
	public void TestParseSelectWeightedChoices() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("( a [2] |a [3] )", ctx, tf), "a");

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

		assertEquals(RiTa.evaluate("( a [2] )", ctx, tf), "a");
		assertEquals(RiTa.evaluate("([2] |[3])", ctx, tf), "");

		String[] results = { "a", "b", "" };
		String rs = RiTa.evaluate("(a | b [2] |[3])", ctx, tf);
		assertTrue(Arrays.asList(results).contains(rs));
		rs = RiTa.evaluate("(a | b[2] |[3])", ctx, tf);
		assertTrue(Arrays.asList(results).contains(rs));
	}

	// Transform
	@Test
	public void TestAddTransforms() {
		Map<String, Object> ctx = opts();

		Function<String, String> func = (x) -> {
			return "A";
		};

		int orig = RiTa.addTransform("capA", func).length;
		assertEquals(RiTa.evaluate(".capA()", ctx, tf), "A");
		assertEquals(RiTa.evaluate("(b).capA()", ctx, tf), "A");
		int post = RiTa.addTransform("capA").length;
		assertEquals(post, orig);

	}

	@Test
	public void UseTransfromsInContext() {
		Map<String, Object> ctx = opts();
		Function<String, String> func = s -> ( s != null ? s : "B");
		ctx.put("capB", func);
		assertEquals(RiTa.evaluate(".capB()", ctx, tf), "B");
		assertEquals(RiTa.evaluate("(c).capB()", ctx, tf), "c");
		assertEquals(RiTa.evaluate("(c).toUpperCase()", ctx, tf), "C");
	}

	@Test
	public void TestNoInputTransforms() {
		Map<String, Object> ctx = opts();
		Supplier<String> func = () -> {
			return "A";
		};
		ctx.put("capA", func);

		assertEquals(RiTa.evaluate(".capA()", ctx, tf), "A");

		ctx.clear();
		RiTa.addTransform("capA", func);
		assertEquals(RiTa.evaluate(".capA()", ctx, tf), "A");
		RiTa.addTransform("capA");
	}

	@Test
	public void TestRiTaFunctionTransforms() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("Does $RiTa.env() equal node?", ctx, tf), "Does node equal node?");
	}

	/*
	 * it("XXX", () => { assertEquals(RiTa.evaluate("How many (tooth |
	 * tooth).pluralize() do you have?", 0, {trace:1, skipPreParse: 0}), "How many
	 * teeth do you have?"); });
	 */

	@Test
	public void TestChoiceTransforms() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=.toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "");

		ctx.clear();
		assertEquals(RiTa.evaluate("(a).toUpperCase()"), "A");
		assertEquals(RiTa.evaluate("((a)).toUpperCase()", ctx, tf), "A");

		String rs = RiTa.evaluate("(a | b).toUpperCase()");
		String[] possibleResults = { "A", "B" };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		assertEquals(RiTa.evaluate("(a | a).capitalize()"), "A");
		assertEquals(RiTa.evaluate("The (boy | boy).toUpperCase() ate."), "The BOY ate.");
		assertEquals(RiTa.evaluate("How many (tooth | tooth).pluralize() do you have?"), "How many teeth do you have?");

	}

	@Test
	public void TestSymbolTransforms() {
		Map<String, Object> ctx = opts();
		ctx.put("dog", "spot");
		assertEquals(RiTa.evaluate("$dog.toUpperCase()", ctx, tf), "SPOT");
		assertEquals(RiTa.evaluate("$dog.capitalize()", ctx, tf), "Spot");
		assertEquals(RiTa.evaluate("($dog).capitalize()", ctx, tf), "Spot");
		ctx.clear();
		assertEquals(RiTa.evaluate("$dog.toUpperCase()", ctx, opts("silent", true)), "$dog.toUpperCase()");
		ctx.put("dog", "spot");
		assertEquals(RiTa.evaluate("The $dog.toUpperCase()", ctx), "The SPOT");
		assertEquals(RiTa.evaluate("The (boy | boy).toUpperCase() ate."), "The BOY ate.");
		assertEquals(RiTa.evaluate("The (girl).toUpperCase() ate."), "The GIRL ate.");

		assertEquals(RiTa.evaluate("$dog.articlize().capitalize()", ctx, tf), "A spot");
		assertEquals(RiTa.evaluate("[$a=$dog] $a.articlize().capitalize()", ctx), "spot A spot");
		ctx.clear();
		ctx.put("dog", "abe");
		assertEquals(RiTa.evaluate("[$a=$dog] $a.articlize().capitalize()", ctx), "abe An abe");
		assertEquals(RiTa.evaluate("(abe | abe).articlize().capitalize()", ctx), "An abe");
		assertEquals(RiTa.evaluate("(abe | abe).capitalize().articlize()", ctx), "An abe");
		assertEquals(RiTa.evaluate("(Abe Lincoln).articlize().capitalize()", ctx, tf), "An Abe Lincoln");
		assertEquals(RiTa.evaluate("<li>$start</li>\n$start=($jrSr).capitalize()\n$jrSr=(junior|junior)"),
				"<li>Junior</li>");
	}

	@Test
	public void TestArticlize() {
		assertEquals(RiTa.articlize("dog"), "a dog");
		assertEquals(RiTa.articlize("ant"), "an ant");
		assertEquals(RiTa.articlize("honor"), "an honor");
		assertEquals(RiTa.articlize("eagle"), "an eagle");
		assertEquals(RiTa.articlize("ermintrout"), "an ermintrout");
	}

	@Test
	public void TestObjectProperties() {
		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		dog.put("color", "white");
		dog.put("hair", opts("color", "white"));
		assertEquals(RiTa.evaluate("It was a $dog.hair.color dog.", dog, tf), "It was a white dog.");
		assertEquals(RiTa.evaluate("It was a $dog.color.toUpperCase() dog.", dog, tf), "It was a WHITE dog.");
	}

	@Test
	public void TestMemberFunction() {
		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		Supplier<String> func = () -> {
			return "red";
		};
		dog.put("getColor", func);
		assertEquals(RiTa.evaluate("$dog.name was a $dog.getColor() dog.", dog), "Spot was a red dog.");
	}

	@Test
	public void TestTransfromsEndingWithPunc() {
		String rs = RiTa.evaluate("(a | b).toUpperCase().");
		String[] possibleResults = { "A.", "B." };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		assertEquals(RiTa.evaluate("The (boy | boy).toUpperCase()!"), "The BOY!");
		assertEquals(RiTa.evaluate("The $dog.toUpperCase()?", opts("dog", "spot")), "The SPOT?");
		assertEquals(RiTa.evaluate("The (boy | boy).toUpperCase()."), "The BOY.");

		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		dog.put("color", "white");
		dog.put("hair", opts("color", "white"));
		assertEquals(RiTa.evaluate("It was $dog.hair.color.", dog), "It was white.");
		assertEquals(RiTa.evaluate("It was $dog.color.toUpperCase()!", dog), "It was WHITE!");

		Supplier<String> func = () -> {
			return "red";
		};
		Map<String, Object> col = opts("getColor", func);
		assertEquals(RiTa.evaluate("It was $dog.getColor()?", opts("dog", col)), "It was red?");

		Map<String, Object> ctx = opts();
		ctx.put("user", opts("name", "jen"));
		assertEquals(RiTa.evaluate("That was $user.name!", ctx), "That was jen!");
		assertEquals(RiTa.evaluate("That was $user.name.", ctx), "That was jen.");
	}

	@Test
	public void testHandleTransformsOnLiterals2() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("How many (teeth).toUpperCase() do you have?", ctx, tf), "How many TEETH do you have?");
		assertEquals(RiTa.evaluate("How many (teeth).quotify() do you have?", ctx, tf), "How many \"teeth\" do you have?");
		assertEquals(RiTa.evaluate("That is (ant).articlize()."), "That is an ant.");

	}

	@Test
	public void testCutomTransforms() {
		Supplier<String> blah = () -> {
			return "Blah";
		};
		Supplier<String> blah2 = () -> {
			return "Blah2";
		};
		assertEquals(RiTa.evaluate("That is (ant).Blah().", opts("Blah", blah)), "That is Blah.");
		Map<String, Object> ctx = opts();
		ctx.put("Blah2", blah2);
		assertEquals(RiTa.evaluate("That is (ant).Blah2().", ctx), "That is Blah2.");

	}

	// Grammar
	@Test
	public void testEvaluatePostDefinedSymbols() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=$bar\n$bar=baz\n$foo", ctx, tf), "baz");
	}

	@Test
	public void testOptimisePreParsing() {
		Map<String, Object> ctx = opts("nothing", "NOTHING", "hang", "HANG");
		Map<String, Object> spp = opts("skipPreParse", false);
		String input = "Eve near Vancouver, Washington is devastated that the SAT exam was postponed. Junior year means NOTHING if you can\"t HANG out. At least that\"s what she thought. Summer is going to suck.";
		String rs = RiTa.evaluate(input, ctx, spp);
		assertEquals(rs, input.replace("$hang", "HANG").replace("$nothing", "NOTHING"));

		input = "Eve near Vancouver,\nWashington is devastated that the SAT exam was postponed. Junior year means NOTHING if you can\"t HANG out. At least that\"s what she thought. Summer is going to suck.";
		rs = RiTa.evaluate(input, ctx, spp);
		assertEquals(rs, input.replace("$hang", "HANG").replace("$nothing", "NOTHING").replace("\n", " "));

		input = "Eve&nbsp;near Vancouver";
		rs = RiTa.evaluate(input, ctx, spp);
		assertEquals(rs, "Eve near Vancouver");

		input = "This is not a &#124;.";
		rs = RiTa.evaluate(input, ctx, spp);
		assertEquals(rs, "This is not a |.");

	}

	@Test
	public void testSymbolsWithATransform() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$foo=$bar.toUpperCase()\n$bar=baz\n$foo", ctx, tf);
		assertEquals(rs, "BAZ");

		assertEquals(RiTa.evaluate("$foo=.toUpperCase()", ctx, tf), "");
		assertEquals(ctx.get("foo"), "");

		assertEquals(RiTa.evaluate("$foo.capitalize()\n$foo=(a|a)"), "A");
		assertEquals(RiTa.evaluate("$start=$r.capitalize()\n$r=(a|a)\n$start", ctx, tf), "A");
	}

	@Test
	public void testSymbolsWithPropertyTransforms() {
		Map<String, Object> ctx = opts("bar", opts("ucf", "result"));
		String rs = RiTa.evaluate("$foo=$bar.ucf\n$foo", ctx, tf);
		assertEquals(rs, "result");
	}

	@Test
	public void testPreparsing() {
		Map<String, Object> ctx = opts("bar", opts("ucf", "result"));
		String rs = RiTa.evaluate("$foo=$bar.ucf\n$foo", ctx, tf);
		assertEquals(rs, "result");
	}

	/*
	 * it("Should evaluate symbols even with a bad func transform", () => { let rs =
	 * RiTa.evaluate("$foo=$bar.ucf\n$bar=baz\n$foo", {}, {trace:0});
	 * assertEquals(rs, "baz.ucf"); });
	 * it("Should evaluate symbols even with one bad func transform", () => { let rs
	 * = RiTa.evaluate("$foo=$bar.toUpperCase().ucf\n$bar=baz\n$foo", {},
	 * {trace:0}); assertEquals(rs, "BAZ.ucf"); });
	 */

	@Test
	public void postDefinedSymbolsWithTransforms() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=$bar.toLowerCase().ucf()\n$bar=baz\n$foo", ctx, tf), "Baz");
	}

	@Test
	public void testConvertedGrammar() {
		Map<String, Object> ctx = opts();
		String script = String.join("\n",
				"$start = $nounp $verbp.",
				"$nounp = $determiner $noun",
				"$determiner = (the | the)",
				"$verbp = $verb $nounp",
				"$noun = (woman | woman)",
				"$verb = shoots",
				"$start") + "\n";
		String rs = RiTa.evaluate(script, ctx, tf);
		assertEquals(rs, "the woman shoots the woman.");
	}

	@Test
	public void testPreviousAssignments() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("$foo=dog\n$bar=$foo\n$baz=$foo\n$baz", ctx, tf), "dog");
		assertEquals(RiTa.evaluate("$foo=hi\n$foo there", ctx, tf), "hi there");
		assertEquals(RiTa.evaluate("$foo=a\n$foo", ctx, tf), "a");
	}

	@Test
	public void testPreDefinedVariables() {
		Map<String, Object> ctx = opts();
		String script = String.join("\n",
				"$noun=(woman | woman)",
				"$start=$noun",
				"$start");
		assertEquals(RiTa.evaluate(script, ctx, tf), "woman");
	}
	// Entities

	@Test
	public void testDecodeHTMLEntities() {
		assertEquals(RiTa.evaluate("The &num; symbol"), "The # symbol");
		assertEquals(RiTa.evaluate("The &#x00023; symbol"), "The # symbol");
		assertEquals(RiTa.evaluate("The &#35; symbol"), "The # symbol");
		assertEquals(RiTa.evaluate("The&num;symbol"), "The#symbol");

		String[] arr = { "&lsqb;", "&lbrack;", "&#x0005B;", "&#91;" };
		for (String e : arr) {
			assertEquals(RiTa.evaluate("The " + e + " symbol"), "The [ symbol");
		}

		String[] arr2 = { "&rsqb;", "&rbrack;", "&#x0005D;", "&#93;" };
		for (String e : arr) {
			assertEquals(RiTa.evaluate("The " + e + " symbol"), "The ] symbol");
		}

	}

	@Test
	public void testBasicPunctuation() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("The -;:.!?\'`", ctx, tf), "The -;:.!?'`");
		assertEquals(RiTa.evaluate("The -;:.!?\'`", ctx), "The -;:.!?\"`");
		assertEquals(RiTa.evaluate(",.;:'?!-_`“”’‘…‐–—―^*", ctx, tf), ",.;:'?!-_`“”’‘…‐–—―^*");
		assertEquals(RiTa.evaluate(",.;:\"?!-_`“”’‘…‐–—―^*", ctx, tf), ",.;:\"?!-_`“”’‘…‐–—―^*");
		assertEquals(RiTa.evaluate("/&%©@*"), "/&%©@*");
	}

	@Test
	public void testAllowSpaceForFormatting() {
		Map<String, Object> ctx = opts();
		assertEquals(RiTa.evaluate("&nbsp;The dog&nbsp;", ctx, tf), " The dog ");
		assertEquals(RiTa.evaluate("&nbsp; The dog&nbsp;", ctx, tf), "  The dog ");
		assertEquals(RiTa.evaluate("The &nbsp;dog", ctx, tf), "The  dog");
		assertEquals(RiTa.evaluate("The&nbsp; dog", ctx, tf), "The  dog");
		assertEquals(RiTa.evaluate("The &nbsp; dog", ctx, tf), "The   dog");

	}

	// Operators
	@Test
	public void testAssignmentOperators() {
		assertEquals(Operator.EQ.invoke("hello", "hello"), true);
		assertEquals(Operator.EQ.invoke("hello", ""), false);
		assertEquals(Operator.EQ.invoke("hello", null), false);

		assertEquals(Operator.NE.invoke("hello", "hello"), false);
		assertEquals(Operator.NE.invoke("hello", ""), true);
		assertEquals(Operator.NE.invoke("hello", null), true);

		assertEquals(Operator.EQ.invoke("true", "false"), false);
		assertEquals(Operator.EQ.invoke("false", "false"), true);
		assertEquals(Operator.EQ.invoke("false", null), false);

		assertEquals(Operator.NE.invoke("hello", ""), true);
		assertEquals(Operator.NE.invoke("hello", "false"), true);

		assertThrows(RiTaException.class, () -> Operator.NE.invoke(null, null));
	}

	@Test
	public void testInvokeEqualityOperators() {
		assertEquals(Operator.EQ.invoke("hello", "hello"), true);
		assertEquals(Operator.EQ.invoke("hello", ""), false);
		assertEquals(Operator.EQ.invoke("hello", null), false);

		assertEquals(Operator.NE.invoke("hello", "hello"), false);
		assertEquals(Operator.NE.invoke("hello", ""), true);
		assertEquals(Operator.NE.invoke("hello", null), true);

		assertEquals(Operator.EQ.invoke("true", "false"), false);
		assertEquals(Operator.EQ.invoke("false", "false"), true);
		assertEquals(Operator.EQ.invoke("false", null), false);

		assertEquals(Operator.NE.invoke("hello", ""), true);
		assertEquals(Operator.NE.invoke("hello", "false"), true);

		assertThrows(RiTaException.class, () -> Operator.NE.invoke(null, null));
	}

	@Test
	public void testInvokeComparisonOperators() {

		assertEquals(Operator.GT.invoke("2", "1"), true);
		assertEquals(Operator.GT.invoke("1", "2"), false);
		assertEquals(Operator.GT.invoke("1", "1"), false);
		assertEquals(Operator.GT.invoke("2.0", "1"), true);
		assertEquals(Operator.GT.invoke("1.0", "2"), false);
		assertEquals(Operator.GT.invoke("1.0", "1"), false);
		assertEquals(Operator.GT.invoke("2.0", "1.00"), true);
		assertEquals(Operator.GT.invoke("1.0", "2.00"), false);
		assertEquals(Operator.GT.invoke("1.0", "1.00"), false);

		assertEquals(Operator.LT.invoke("2", "1"), false);
		assertEquals(Operator.LT.invoke("1", "2"), true);
		assertEquals(Operator.LT.invoke("1", "1"), false);
		assertEquals(Operator.LT.invoke("2.0", "1"), false);
		assertEquals(Operator.LT.invoke("1.0", "2"), true);
		assertEquals(Operator.LT.invoke("1.0", "1"), false);
		assertEquals(Operator.LT.invoke("2.0", "1.00"), false);
		assertEquals(Operator.LT.invoke("1.0", "2.00"), true);
		assertEquals(Operator.LT.invoke("1.0", "1.00"), false);

		assertEquals(Operator.LE.invoke("2", "1"), false);
		assertEquals(Operator.LE.invoke("1", "2"), true);
		assertEquals(Operator.LE.invoke("1", "1"), true);
		assertEquals(Operator.LE.invoke("2.0", "1"), false);
		assertEquals(Operator.LE.invoke("1.0", "2"), true);
		assertEquals(Operator.LE.invoke("1.0", "1"), true);
		assertEquals(Operator.LE.invoke("2.0", "1.00"), false);
		assertEquals(Operator.LE.invoke("1.0", "2.00"), true);
		assertEquals(Operator.LE.invoke("1.0", "1.00"), true);
	
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", ""));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", "h"));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("", ""));
		assertThrows(RiTaException.class, () -> Operator.GT.invoke("2", null));

	}

	@Test
	public void testInvokeMatchingOperators() {
		assertEquals(Operator.SW.invoke("Hello", "He"), true);
		assertEquals(Operator.SW.invoke("Hello", "Hello"), true);
		assertEquals(Operator.SW.invoke("Hello", "Hej"), false);
		assertEquals(Operator.SW.invoke("Hello", null), false);
		assertEquals(Operator.SW.invoke("Hello", ""), true);

		assertEquals(Operator.EW.invoke("Hello", "o"), true);
		assertEquals(Operator.EW.invoke("Hello", "Hello"), true);
		assertEquals(Operator.EW.invoke("Hello", "l1o"), false);
		assertEquals(Operator.EW.invoke("Hello", null), false);
		assertEquals(Operator.EW.invoke("Hello", ""), true);

		assertEquals(Operator.RE.invoke("Hello", "ll"), true);
		assertEquals(Operator.RE.invoke("Hello", "e"), true);
		assertEquals(Operator.RE.invoke("Hello", "l1"), false);
		assertEquals(Operator.RE.invoke("Hello", null), false);
		assertEquals(Operator.RE.invoke("Hello", ""), true);

		assertEquals(Operator.SW.invoke("$Hello", "$"), true);
		assertEquals(Operator.EW.invoke("$Hello", "$"), false);
		assertEquals(Operator.RE.invoke("$Hello", "$"), true);
		assertEquals(Operator.RE.invoke("hello", "(hello|bye)"), true);
		assertEquals(Operator.RE.invoke("bye", "(hello|bye)"), true);
		assertEquals(Operator.RE.invoke("by", "(hello|bye)"), false);

		assertThrows(RiTaException.class, () -> Operator.SW.invoke(null, "hello"));
		assertThrows(RiTaException.class, () -> Operator.SW.invoke(null, null));

	}

}
