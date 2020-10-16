package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static rita.Util.opts;

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
	public void testVariousTransforms() {
		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("(BAZ).toLowerCase().ucf()", opts()), "Baz");

		assertEq(RiTa.evaluate("(a).toUpperCase()", ctx), "A"); // Choice
		assertEq(RiTa.evaluate(".toUpperCase()", ctx), ""); // Symbol

		assertEq(RiTa.evaluate("$a=b\n$a.toUpperCase()", ctx), "B"); // Symbol
		assertEq(RiTa.evaluate("[$b=((a | a)|a)].toUpperCase() dog.", ctx), "A dog.");// Inline
		assertEq(RiTa.evaluate("((a)).toUpperCase()", ctx), "A"); // Nested Choice

		assertEq(RiTa.evaluate("$a.toUpperCase()\n($a=b)", ctx), "B"); // pending Symbol

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("$dog.ucf()", ctx), "Terrier"); // Symbol in context
	}

	@Test
	public void testCustomRegexes() {
		String expr = "The $foo\ndog.";
		assertTrue(RE.test("\\$[A-Za-z_]", expr));
		//System.out.println(expr);
	}

	@Test
	public void testSymbolsStartingWithNumbers() {
		assertEq(RiTa.evaluate("$foo=hello\n$start=I said $foo to her\n$start", opts()), "I said hello to her");
		assertEq(RiTa.evaluate("$1foo=hello\n$1start=I said $1foo to her\n$1start", opts()), "I said hello to her");
		assertEq(RiTa.evaluate("$1foo=(hello)\n$1start=I said $1foo to her\n$1start", opts()), "I said hello to her");
	}

	@Test
	public void testSymbolsInContext() {

		Map<String, Object> ctx;

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

		ctx = opts("dog", "terrier");
		assertEq(RiTa.evaluate("a $dog", ctx), "a terrier");

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
	}

	@Test
	public void testArticlize() {
		assertEq(RiTa.articlize("dog"), "a dog");
		assertEq(RiTa.articlize("ant"), "an ant");
		assertEq(RiTa.articlize("honor"), "an honor");
		assertEq(RiTa.articlize("eagle"), "an eagle");
		assertEq(RiTa.articlize("ermintrout"), "an ermintrout"); // LTS
	}

	@Test
	public void testArticlizePhrases() {
		assertEq(RiTa.articlize("black dog"), "a black dog");
		assertEq(RiTa.articlize("black ant"), "a black ant");
		assertEq(RiTa.articlize("orange ant"), "an orange ant");
	}

	@Test
	public void testInvokeMatchingOperators() {
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
	public void testIsParseable() {
		RiScript rs = new RiScript();
		assertTrue(!rs.isParseable("Hello"));
		assertTrue(rs.isParseable("("));
		assertTrue(rs.isParseable("(A | B)"));
		assertTrue(rs.isParseable("$hello"));
		assertTrue(rs.isParseable("$b"));
	}

	@Test
	public void testNestedContext() {

		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		String res = RiTa.evaluate("$foo=$bar.color\n$foo", ctx);
	}

	// Evaluation

	@Test
	public void testEvalSimpleExpressions() {
		Map<String, Object> ctx = opts();

		assertEq(RiTa.evaluate("foo", ctx), "foo");
		assertEq(RiTa.evaluate("foo.", ctx), "foo.");
		assertEq(RiTa.evaluate("\"foo\"", ctx), "\"foo\"");
		assertEq(RiTa.evaluate("'foo'", ctx), "'foo'");
		assertEq(RiTa.evaluate("foo\nbar", ctx), "foo bar");
		assertEq(RiTa.evaluate("foo&#10;bar", ctx), "foo\nbar");
		assertEq(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx,TT), "bar baz");
		assertEq(RiTa.evaluate("$foo=bar\nbaz", ctx), "baz");
		assertEq(RiTa.evaluate("$foo=bar\nbaz\n$foo", ctx), "baz bar");

		ctx.put("a", "a");
		ctx.put("b", "b");
		assertEq(RiTa.evaluate("(a|a)", ctx), "a");
		assertEq(RiTa.evaluate("foo.bar", ctx), "foo.bar");
	}

	@Test
	public void testEvalRecursiveExpressions() {
		Map<String, Object> ctx = opts("a", "a", "b", "b");
		assertEq(RiTa.evaluate("(a|a)", ctx), "a");

		ctx.clear();
		ctx.put("a", "$b");
		ctx.put("b", "(c | c)");
		assertEq(RiTa.evaluate("$a", ctx), "c");
		assertEq(RiTa.evaluate("$k = $a\n$k", ctx), "c");
		assertEq(RiTa.evaluate("$s = $a\n$a = $b\n$c = $d\n$d = c\n$s", ctx), "c");

		ctx.clear();
		ctx.put("s", "$a");
		ctx.put("a", "$b");
		ctx.put("b", "$c");
		ctx.put("c", "$d");
		ctx.put("d", "c");
		assertEq(RiTa.evaluate("$s", ctx), "c");
	}

	// Assign
	@Test
	public void testParseAssignments() {

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
	public void testHandleSentences() {

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
	public void testParseTransformedAssignments() {
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

		assertEq(RiTa.evaluate("$foo=(a | a).toUpperCase() (b | b).toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "A B");

		assertEq(RiTa.evaluate("$foo=((a | a) | (a | a))", ctx), "");
		assertEq(ctx.get("foo"), "a");

		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx), "");// empty string
		assertEq(ctx.get("foo"), "");

	}

	@Test
	public void testTransformsOnLiterals() {
		assertEq(RiTa.evaluate("How many (teeth).quotify() do you have?"), "How many \"teeth\" do you have?");
		// NEXT: CONSIDER adding context to RiTa.Grammar/grammar.expand
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("That is (ant).articlize().", ctx), "That is an ant.");
		assertEq(RiTa.evaluate("That is an (ant).capitalize()."), "That is an Ant.");

	}

	// it.only("Should handle silents", () => {
	// assertEq(RiTa.evaluate("The $hero=blue (dog | dog)", ctx, tf),"The blue
	// dog"); assertEq(ctx.foo,"blue"); });

	// Inline
	@Test
	public void testEvaluateInlineAssignsToVars() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$person=(a | b | c)\n[$a=$person] is $a", ctx);
		String[] possibleResults = { "a is a", "b is b", "c is c" };
		assertTrue(Arrays.asList(possibleResults).contains(rs));

		ctx.put("name", "(Dave1 | Dave2)");
		rs = RiTa.evaluate("$name=(Dave1 | Dave2)\n[$stored=$name] is $stored", ctx);

		String[] possibleNames = { "Dave1", "Dave2" };
		assertTrue(Arrays.asList(possibleNames).contains(ctx.get("stored")));

		String[] possibleResult2 = { "Dave1 is Dave1", "Dave2 is Dave2" };
		assertTrue(Arrays.asList(possibleResult2).contains(rs));

		rs = RiTa.evaluate("$name=(Dave | Dave)\n[$stored=$name] is called $stored", ctx);
		assertEq(rs, "Dave is called Dave");
	}

	@Test
	public void testEvaluateBasicInlineAssigns() {
		Map<String, Object> ctx = opts();
		/*assertEq(RiTa.evaluate("[$foo=hi]", ctx), "hi");
		//if (1==1)return;
		assertEq(RiTa.evaluate("[$foo=(hi | hi)] there", ctx), "hi there");
		assertEq(RiTa.evaluate("[$foo=(hi | hi).ucf()] there", ctx), "Hi there");
		
		assertEq(RiTa.evaluate("$foo=(hi | hi)\n$foo there", ctx),
				RiTa.evaluate("[$foo=(hi | hi)] there", ctx));
		*/
		String exp = "A dog is a mammal";
		assertEq(RiTa.evaluate("$a=b\n($a).toUpperCase()", ctx), "B");

		assertEq(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx), exp.toLowerCase());
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal", ctx), exp);
		assertEq(ctx.get("stored"), "A");

		assertEq(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx), exp);
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("$stored=(a | a)\n$stored.toUpperCase() dog is a mammal", ctx), exp);
		assertEq(ctx.get("stored"), "a");

		assertEq(RiTa.evaluate("[$stored=(a | a)] dog is a mammal", ctx), exp.toLowerCase());
		assertEq(ctx.get("stored"), "a");

	}

	@Test
	public void testAssignTransforms() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("[$stored=(a | a).toUpperCase()] dog is a mammal.", ctx), "A dog is a mammal.");
		assertEq(RiTa.evaluate("$stored=(a | a).toUpperCase()\n$stored dog is a mammal.", ctx), "A dog is a mammal.");
	}

	@Test
	public void testTransformsOfExprType() {

		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$a=a\n($a).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=a\n($a | $a).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=a\n(A).toUpperCase()", ctx), "A");
		assertEq(RiTa.evaluate("$a=(a).toUpperCase()", ctx), "");
		assertEq(ctx.get("a"), "A");
	}

	@Test
	public void testAssignVariableToResult() {
		Map<String, Object> ctx = opts();
		String result = RiTa.evaluate("[$stored=(a | b)]", ctx);
		String[] results = { "a", "b" };
		assertTrue(Arrays.asList(results).contains(result));
		assertEq(ctx.get("stored"), result);

		String result2 = RiTa.evaluate("[$a=$stored]", ctx);
		assertEq(ctx.get("a"), result2);
		assertEq(result2, ctx.get("stored"));
	}

	@Test
	public void testAssignAVariableToCode() {
		Map<String, Object> ctx = opts();
		ctx.put("animal", "dog");
		assertEq(RiTa.evaluate("A [$stored=($animal | $animal)] is a mammal", ctx), "A dog is a mammal");
		ctx.clear();
		assertEq(RiTa.evaluate("[$b=(a | a).toUpperCase()] dog is a $b.", ctx), "A dog is a A.");
	}

	@Test
	public void testLineBreaks() {
		String in = "a.\n$b.";
		String out = RiTa.evaluate(in, opts("b", "c"));
		//System.out.println("RES: '"+out+"'");
		assertEq(out, "a. c.");
	}

	@Test
	public void testReuseAnAssignedVariable() {
		Map<String, Object> ctx = opts();
		String inp = "Once there was a girl called [$hero=(Jane | Jane)].";
		inp += "\n$hero lived in [$home=(Neverland | Neverland)].";
		inp += "\n$hero liked living in $home.";
		String exp = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";
		String out = RiTa.evaluate(inp, ctx);
		//System.out.println(out);
		assertEq(out, exp);
	}

	@Test
	public void testAssignInline() {
		Map<String, Object> ctx = opts();
		/*
		 * assertEq(RiTa.evaluate("A [$stored=($animal | $animal)] is a mammal",
		 * opts("animal", "dog"), tf),"A dog is a mammal");
		 * assertEq(RiTa.evaluate("[$b=(a | a).toUpperCase()] dog is a $b.", ctx,
		 * tf),"A dog is a A.");
		 */
		assertEq(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a $b.toLowerCase().", ctx), "A dog is a a.");
		assertEq(RiTa.evaluate("[$b=(a | a)].toUpperCase() dog is a ($b).toLowerCase().", ctx), "A dog is a a.");

		String[] expected = { "a", "b" };
		String result = RiTa.evaluate("[$stored=(a | b)]", ctx);
		assertTrue(Arrays.asList(expected).contains(result));
		assertTrue(Arrays.asList(expected).contains((String) ctx.get("stored")));

		String result2 = RiTa.evaluate("$a=$stored", ctx);
		assertEq(result2, "");
		assertEq(ctx.get("a"), ctx.get("stored"));
		assertEq(ctx.get("a"), result);
	}

	@Test
	public void testReuseInline() {
		Map<String, Object> ctx = opts();
		String inp = "Once there was a girl called [$hero=(Jane | Jane)].\n$hero lived in [$home=(Neverland | Neverland)].\n$hero liked living in $home.";
		String out = "Once there was a girl called Jane. Jane lived in Neverland. Jane liked living in Neverland.";
		assertEq(RiTa.evaluate(inp, ctx), out);
	}

	// Symbol

	@Test
	public void testLinebreakDefinedVariables() {
		String res;
		Map<String, Object> ctx = opts();
		res = RiTa.evaluate("$foo=hello\n$start=I said $foo to her\n$start", ctx);
		assertEq(res, "I said hello to her");
		res = RiTa.evaluate("$foo=(hello)\n$start=I said $foo to her\n$start", ctx);
		assertEq(res, "I said hello to her");
	}

	@Test
	public void testPreciouslyDefinedSymbols() {
		Map<String, Object> ctx = opts();
		ctx.put("dog", "terrier");
		assertEq(RiTa.evaluate("the $dog ate", ctx), "the terrier ate");
		ctx.put("verb", "ate");
		assertEq(RiTa.evaluate("the $dog $verb", ctx), "the terrier ate");

		ctx.clear();
		assertEq(RiTa.evaluate("$foo=bar\n$foo", ctx), "bar");
		assertEq(RiTa.evaluate("$dog=terrier\na $dog", ctx), "a terrier");
		assertEq(RiTa.evaluate("$dog=beagle\nI ate the $dog", ctx), "I ate the beagle");
		assertEq(RiTa.evaluate("$dog=lab\nThe $dog today.", ctx), "The lab today.");
		assertEq(RiTa.evaluate("$dog=lab\nI ate the $dog.", ctx), "I ate the lab.");
		assertEq(RiTa.evaluate("$dog=lab\nThe $dog\ntoday.", ctx), "The lab today.");
		assertEq(RiTa.evaluate("$dog=lab\nI ate the\n$dog.", ctx), "I ate the lab.");

		assertEq(RiTa.evaluate("$foo=baz\n$bar=$foo\n$bar", ctx), "baz");

		// from known-issues
		ctx.put("foo", "baz");
		ctx.put("bar", "$foo");
		assertEq(RiTa.evaluate("$bar", ctx), "baz");
		ctx.clear();
		ctx.put("foo", "baz");
		ctx.put("bar", "(A | A)");
		assertEq(RiTa.evaluate("$bar", ctx), "A");
		ctx.clear();
		ctx.put("foo", "baz");
		ctx.put("bar", "$foo starts with (b | b)");
		assertEq(RiTa.evaluate("$bar", ctx), "baz starts with b");
		assertEq(RiTa.evaluate("$start=$foo\n$foo=hello\n$start"), "hello");
		assertEq(RiTa.evaluate("$start = $noun\n$noun = hello\n$start"), "hello");
	}

	@Test
	public void testSymbolsFromContext() {


		Map<String, Object> ctx = opts();
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
	public void testSymbolsWithPropertyTransforms() {
		Map<String, Object> ctx = opts();
		ctx.put("bar", opts("color", "blue"));
		assertEq(RiTa.evaluate("$foo=$bar.color\n$foo", ctx), "blue");
		assertEq(RiTa.evaluate("$bar.color", ctx), "blue");
	}

	@Test
	public void testConcatenateVariables() {
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
	public void testBadChoices() {
		Map<String, Object> ctx = opts();
		assertThrows(RiTaException.class, () -> RiTa.evaluate("|", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a |", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("a | b | c", ctx, ST));
		assertThrows(RiTaException.class, () -> RiTa.evaluate("(a | b) | c", ctx, ST));
	}

	@Test
	public void testMultiWordChoices() {
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
	public void testParseSelectChoices() {
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
	public void testParseSelectChoicesTX() {
		assertEq(RiTa.evaluate("(a | a).toUpperCase()", opts()), "A");
		assertEq(RiTa.evaluate("(a | a).up()", opts()), "a.up()");
		Function<String, String> up = x -> x.toUpperCase();
		assertEq(RiTa.evaluate("(a | a).up()", opts("up", up)), "A");
		assertEq(RiTa.evaluate("$a", opts("a", 1)), "1");
	}

	@Test
	public void testParseChoicesFromAnExpression() {

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
	public void testParseSelectWeightedChoices() {
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
	public void testAddTransforms() {

		Function<String, String> func = (x) -> "A";
		Map<String, Function<String, String>> txs1 = RiTa.addTransform("capA", func);
		assertEq(RiTa.evaluate(".capA()", null), "A");
		assertEq(RiTa.evaluate("(b).capA()", null), "A");
		Map<String, Function<String, String>> txs2 = RiTa.addTransform("capA", null);
		assertEq(txs1.size(), txs2.size());
	}

	@Test
	public void testNoInputTransforms() {

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
		RiTa.addTransform("capA", null);
	}

	//@Test
	public void testRiTaFunctionTransforms() { // TODO: Handle called RiTa functions?
		
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("Does $RiTa.env() equal node?", ctx,TT), "Does node equal node?");
	}

	@Test
	public void testChoiceTransforms() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx), "");
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
	public void testSymbolTransforms() {
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
		assertEq(RiTa.evaluate("[$a=$dog] $a.articlize().capitalize()", ctx), "abe An abe");
		assertEq(RiTa.evaluate("(abe | abe).articlize().capitalize()", ctx), "An abe");
		assertEq(RiTa.evaluate("(abe | abe).capitalize().articlize()", ctx), "an Abe");
		assertEq(RiTa.evaluate("(Abe Lincoln).articlize().capitalize()", ctx), "An Abe Lincoln");
		assertEq(RiTa.evaluate("<li>$start</li>\n$start=($jrSr).capitalize()\n$jrSr=(junior|junior)"),
				"<li>Junior</li>");
	}

	@Test
	public void testObjectProperties() { 
		
		//		Map<String, Object> dog = opts();
		//		dog.put("name", "spot");
		//		dog.put("color", "white");
		//		dog.put("hair", opts("color", "white"));
		class Hair {
			String color = "white";
		}
		class Dog {
			String name = "Spot";
			String color = "white";
			Hair hair = new Hair();
		}
		assertEq(RiTa.evaluate("It was a $dog.hair.color dog.", 
				opts("dog", new Dog())), 
				"It was a white dog.");
		assertEq(RiTa.evaluate("It was a $dog.color.toUpperCase() dog.", opts("dog", new Dog())), "It was a WHITE dog.");
		assertEq(RiTa.evaluate("$a.b", opts("a", opts("b", 1)), SP), "1");
	}
	
	@Test
	public void testMemberProp() {
		Map<String, Object> dog = opts();
		dog.put("name", "Spot");
		String input = "$dog.name was a good dog.";
		String expected = "Spot was a good dog.";
		assertEq(RiTa.evaluate(input, opts("dog", dog)), expected);
	}
	
	public void testMemberFunctions() {
		Map<String, Object> dog = opts();
		Supplier<String> func = () -> {
			return "red";
		};
		dog.put("getColor", func);
		String input = "Spot was a $dog.getColor() dog.";
		String expected = "Spot was a red dog.";
		assertEq(RiTa.evaluate(input, opts("dog", dog)), expected);
		
		// TODO: add test with predefined class?
	}
	
	@Test
	public void testMemberTransforms() {
		Map<String, Object> dog = opts();
		dog.put("name", "spot");
		Supplier<String> func = () -> {
			return "red";
		};
		dog.put("getColor", func);
		String input = "$dog.name.ucf() was a $dog.getColor() dog.";
		String expected = "Spot was a red dog.";
		assertEq(RiTa.evaluate(input, opts("dog", dog)), expected);
		
		// TODO: add test with predefined class?
	}
	

	@Test
	public void testTransformsEndingWithPunc() {
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
		assertEq(RiTa.evaluate("It was $dog.hair.color.",  opts("dog",dog)), "It was white.");
		assertEq(RiTa.evaluate("It was $dog.color.toUpperCase()!",  opts("dog",dog)), "It was WHITE!");

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
	public void testHandleTransformsOnLiterals2() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("How many (teeth).toUpperCase() do you have?", ctx), "How many TEETH do you have?");
		assertEq(RiTa.evaluate("How many (teeth).quotify() do you have?", ctx), "How many \"teeth\" do you have?");
		assertEq(RiTa.evaluate("That is (ant).articlize()."), "That is an ant.");

	}

	@Test
	public void testCustomTransforms() {
		Function<String, String> blah = s -> "Blah";
		Function<String, String> blah2 = s -> "Blah2";
		assertEq(RiTa.evaluate("That is (ant).blah().", opts("blah", blah)), "That is Blah.");
		Map<String, Object> ctx = opts();
		ctx.put("blah2", blah2);
		assertEq(RiTa.evaluate("That is (ant).blah2().", ctx), "That is Blah2.");

	}

	// Grammar
	@Test
	public void testEvaluatePostDefinedSymbols() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=$bar\n$bar=baz\n$foo", ctx), "baz");
	}

	@Test
	public void testOptimisePreParsing() {
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
	public void testSymbolsWithTransforms() {
		Map<String, Object> ctx = opts();
		String rs = RiTa.evaluate("$foo=$bar.toUpperCase()\n$bar=baz\n$foo", ctx);
		assertEq(rs, "BAZ");

		assertEq(RiTa.evaluate("$foo=.toUpperCase()", ctx), "");
		assertEq(ctx.get("foo"), "");

		assertEq(RiTa.evaluate("$foo.capitalize()\n$foo=(a|a)", null), "A");
		assertEq(RiTa.evaluate("$start=$r.capitalize()\n$r=(a|a)\n$start", ctx), "A");
	}

	@Test
	public void testTransformProperties() {
		
		class Bar {
			String prop = "result";
		}
		Map<String, Object> ctx = opts("bar", new Bar());
		String rs = RiTa.evaluate("$foo=$bar.prop\n$foo", ctx);
		assertEq(rs, "result");
	}
	
	// TODO: Class must be publicly-defined
	public void testTransformMethods() {
		class Bar {
			public String getProp() { return "result"; }
		}
		Map<String, Object> ctx = opts("bar", new Bar());
		String rs = RiTa.evaluate("$foo=$bar.getProp()\n$foo", ctx);
		assertEq(rs, "result");
	}

	/*
	 * it("Should evaluate symbols even with a bad func transform", () => { let rs =
	 * RiTa.evaluate("$foo=$bar.ucf\n$bar=baz\n$foo", {}, {trace:0}); assertEq(rs,
	 * "baz.ucf"); });
	 * it("Should evaluate symbols even with one bad func transform", () => { let rs
	 * = RiTa.evaluate("$foo=$bar.toUpperCase().ucf\n$bar=baz\n$foo", {},
	 * {trace:0}); assertEq(rs, "BAZ.ucf"); });
	 */

	@Test
	public void postDefinedSymbolsWithTransforms() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=$bar.toLowerCase().ucf()\n$bar=BAZ\n$foo", ctx), "Baz");
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
		String rs = RiTa.evaluate(script, ctx);
		assertEq(rs, "the woman shoots the woman.");
	}

	@Test
	public void testPreviousAssignments() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("$foo=dog\n$bar=$foo\n$baz=$foo\n$baz", ctx), "dog");
		assertEq(RiTa.evaluate("$foo=hi\n$foo there", ctx), "hi there");
		assertEq(RiTa.evaluate("$foo=a\n$foo", ctx), "a");
	}

	@Test
	public void testPreDefinedVariables() {
		Map<String, Object> ctx = opts();
		String script = String.join("\n",
				"$noun=(woman | woman)",
				"$start=$noun",
				"$start");
		assertEq(RiTa.evaluate(script, ctx), "woman");
	}
	// Entities

	@Test
	public void testDecodeHTMLEntities() {
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
	}

	@Test
	public void testBasicPunctuation() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("The -;:.!?\'`", ctx), "The -;:.!?'`");
		assertEq(RiTa.evaluate("The -;:.!?\"`", ctx), "The -;:.!?\"`");
		assertEq(RiTa.evaluate(",.;:'?!-_`“”’‘…‐–—―^*", ctx), ",.;:'?!-_`“”’‘…‐–—―^*");
		assertEq(RiTa.evaluate(",.;:\"?!-_`“”’‘…‐–—―^*", ctx), ",.;:\"?!-_`“”’‘…‐–—―^*");
		assertEq(RiTa.evaluate("/&%©@*"), "/&%©@*");
	}

	@Test
	public void testAllowSpaceForFormatting() {
		Map<String, Object> ctx = opts();
		assertEq(RiTa.evaluate("&nbsp;The dog&nbsp;", ctx), " The dog ");
		assertEq(RiTa.evaluate("&nbsp; The dog&nbsp;", ctx), "  The dog ");
		assertEq(RiTa.evaluate("The &nbsp;dog", ctx), "The  dog");
		assertEq(RiTa.evaluate("The&nbsp; dog", ctx), "The  dog");
		assertEq(RiTa.evaluate("The &nbsp; dog", ctx), "The   dog");

	}

	// Operators
	@Test
	public void testAssignmentOperators() {
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
	public void testInvokeEqualityOperators() {
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
	public void testInvokeComparisonOperators() {

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
	public void testTransformedSymbolsInContext() {

		Map<String, Object> ctx = opts("a", "(terrier | terrier)");
		assertEq(RiTa.evaluate("$a.capitalize()", ctx), "Terrier");
	}

	// Conditionals ======================================================

	@Test
	public void testBadConditionals() {

		Map<String, Object> ctx = opts();
		ctx.put("a", 2);
		assertThrows(RiTaException.class, () -> RiTa.evaluate("{$a<} foo", ctx, ST));
	}

	@Test
	public void testConditionals() {

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
	public void testFloatConditionals() {

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
	public void testMultivalConditionals() {
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
	public void testMatchingConditional() {

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
	public void testRSMatchingConditional() {

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
	}
}
