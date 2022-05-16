![Maven Central](https://img.shields.io/maven-central/v/org.rednoise/rita) <a href="http://www.gnu.org/licenses/gpl-3.0.en.html"><img src="https://img.shields.io/badge/license-GPL-orange.svg" alt="npm version"></a>

## RiTa: tools for generative natural language

RiTa is implemented in Java and JavaScript, with a common [API](https://github.com/dhowe/rita4j/blob/master/README.md#api) for both, and is free/libre/open-source via the GPL license.

### Features in v2.0

* Smart lexicon search for words matching part-of-speech, syllable, stress and rhyme patterns
* Fast, heuristic algorithms for inflection, conjugation, stemming, tokenization, and more
* Letter-to-sound engine for feature analysis of arbitrary words (with/without lexicon)
* Integration of the [RiScript](https://observablehq.com/@dhowe/riscript) scripting language, designed for writers
* Powerful new options for generation via grammars and Markov chains

###

* For JavaScript, see [this repo](https://github.com/dhowe/ritajs) or use it with [npm](https://www.npmjs.com/package/rita) or [unpkg](https://unpkg.com/browse/rita/dist/)
* A simple [Processing example](#in-processing) 
* A simple [Java example](#a-simple-example-java)
* For [Developers](#developing)

Note: Version 2.0 contains breaking changes! Please check the [release notes](https://rednoise.org/rita/#whats-new-wrapper)

### Installation

* Via [github packages](https://github.com/dhowe/rita4j/packages/)
* Via [maven central](https://search.maven.org/artifact/org.rednoise/rita)
* Or directly in maven:

```xml
<dependency>
  <groupId>org.rednoise</groupId>
  <artifactId>rita</artifactId>
  <version>2.4</version>
</dependency>
```
## API
  <table cellspacing="0" cellpadding="0" style="vertical-align: top;">
   <tr>
    <th colspan=2 style="vertical-align: top;text-align: left; padding-left: 12px">RiTa
    </th>
    <th colspan=1 style="vertical-align: top;text-align: left;">RiMarkov</th>
    <th colspan=1 style="vertical-align: top;text-align: left;">RiGrammar</th>
   </tr>
   <tr>
    <td style="vertical-align: top; padding-top: 15px">
      <a href="https://rednoise.org/rita/reference/RiTa/addTransform/index.html">RiTa.addTransform()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/alliterations/index.html">RiTa.alliterations()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/analyze/index.html">RiTa.analyze()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/concordance/index.html">RiTa.concordance()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/conjugate/index.html">RiTa.conjugate()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/evaluate/index.html">RiTa.evaluate()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/grammar/index.html">RiTa.grammar()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/hasWord/index.html">RiTa.hasWord()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isAbbrev/index.html">RiTa.isAbbrev()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isAdjective/index.html">RiTa.isAdjective()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isAdverb/index.html">RiTa.isAdverb()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isAlliteration/index.html">RiTa.isAlliteration()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isNoun/index.html">RiTa.isNoun()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isPunct/index.html">RiTa.isPunct()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isQuestion/index.html">RiTa.isQuestion()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isStopWord/index.html">RiTa.isStopWord()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isRhyme/index.html">RiTa.isRhyme()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/isVerb/index.html">RiTa.isVerb()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/kwic/index.html">RiTa.kwic()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/markov/index.html">RiTa.markov()</a><br/>
    </td>
    <td style="vertical-align: top; padding-top: 15px">
      <a href="https://rednoise.org/rita/reference/RiTa/pastPart/index.html">RiTa.pastPart()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/phones/index.html">RiTa.phones()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/pos/index.html">RiTa.pos()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/posInline/index.html">RiTa.posInline()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/presentPart/index.html">RiTa.presentPart()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/pluralize/index.html">RiTa.pluralize()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/randomOrdering/index.html">RiTa.randomOrdering()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/randomSeed/index.html">RiTa.randomSeed()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/randomWord/index.html">RiTa.randomWord()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/rhymes/index.html">RiTa.rhymes()</a><br/>
      <!--a href="./RiTa/scripting/index.html">RiTa.scripting()</a><br/-->
      <a href="https://rednoise.org/rita/reference/RiTa/search/index.html">RiTa.search()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/sentences/index.html">RiTa.sentences()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/singularize/index.html">RiTa.singularize()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/soundsLike/index.html">RiTa.soundsLike()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/spellsLike/index.html">RiTa.spellsLike()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/stem/index.html">RiTa.stem()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/stresses/index.html">RiTa.stresses()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/syllables/index.html">RiTa.syllables()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/tokenize/index.html">RiTa.tokenize()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiTa/untokenize/index.html">RiTa.untokenize()</a><br/>
      <!--a href="./RiTa/VERSION/index.html">RiTa.VERSION</a><br/-->
    </td>
    <td style="vertical-align: top !important; padding-top: 15px; min-width: 125px">
      <a href="https://rednoise.org/rita/reference/RiMarkov/addText/index.html">addText()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/completions/index.html">completions()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/generate/index.html">generate()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/probability/index.html">probability()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/probabilities/index.html">probabilities()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/size/index.html">size()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/toString/index.html">toString()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/toJSON/index.html">toJSON()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiMarkov/fromJSON/index.html">fromJSON()</a><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    </td>
    <td style="vertical-align: top !important; padding-top: 15px; min-width: 125px">
      <a href="https://rednoise.org/rita/reference/RiGrammar/addRule/index.html">addRule()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/addRules/index.html">addRules()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/expand/index.html">expand()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/removeRule/index.html">removeRule()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/toJSON/index.html">toJSON()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/toString/index.html">toString()</a><br/>
      <a href="https://rednoise.org/rita/reference/RiGrammar/fromJSON/index.html">fromJSON()</a><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
    </td>
 </tr>
</table>

## RiScript

RiScript is a simple, writer-focused scripting language included in RiTa. It enables several generative primitives within plain text for expansion at runtime. RiScript primitives can be used as part of any [RiTa grammar](https://rednoise.org/rita/reference/RiTa/grammar/) or executed directly using [RiTa.evaluate()](https://rednoise.org/rita/reference/RiTa/evaluate/). For  documentation, see [this interactive notebook](https://observablehq.com/@dhowe/riscript).

<br>

--------------------

<br>

### Developing
```sh
$ git clone https://github.com/dhowe/rita4j.git
$ cd rita4j
$ mvn install      # when done, you should see "BUILD SUCCESS"
```
The project requires a minimum version of Java 8 and Maven 3.6 to build.<br>

### Eclipse
1. Do steps above under **Developing**
2. In eclipse, File->Import->Maven->Existing Maven Projects and select your 'rita4j' folder
3. Right-click on project, and select 'Run-as' -> 'Maven install' or 'JUnit tests'

<br/>

Please make contributions via [fork-and-pull](https://reflectoring.io/github-fork-and-pull/) - thanks!

<br/>


--------------------

## A Simple Example (Java)

&nbsp; &nbsp; For online examples in JavaScript, see [this page](https://rednoise.org/rita/#examples)

1. Create a new Java project in Eclipse (or your IDE of choice)
2. Download [rita.jar](https://github-registry-files.githubusercontent.com/216313864/bd26cf80-1cad-11ec-9175-4bd56458eebc?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAIWNJYAX4CSVEH53A%2F20220319%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220319T092932Z&X-Amz-Expires=300&X-Amz-Signature=76f6397b84f722b45d62516d547d090047e67250ef398b4fe72b7ee2f455d9c1&X-Amz-SignedHeaders=host&actor_id=0&key_id=0&repo_id=216313864&response-content-disposition=filename%3Drita-2.4.501.jar&response-content-type=application%2Foctet-stream) and add it to the build path for the project. In eclipse: 'Project' > 'Properties' > 'Java Build Path' > 'Libraries' > 'Add External JARs...'
3. Create and run a new class, SimpleExample.java, with the following code:
```Java
import rita.*;

public class SimpleExample {

  public static void main(String[] args) {

    System.out.println(RiTa.analyze("The elephant took a bite!"));
  }
}
```

## In Processing

To install:

1. Open Processing and select 'Sketch' menu > 'Import Library...' > 'Add Library...'
2. Search for 'RiTa' and then install it

Create an example sketch as follows (and/or see the included examples):
```java
import rita.*;
import java.util.*;

void setup() {

  size(600, 200);
  background(50);
  textSize(20);
  noStroke();

  Map data = RiTa.analyze("The elephant took a bite!");

  float y = 15;
  for (Iterator it = data.keySet().iterator(); it.hasNext();) {
    String key = (String) it.next();
    text(key + ": " + data.get(key), 25, y += 26);
  }
}
```


## Contributors

### Code Contributors

This project exists only because of the people who contribute. Thank you!
<a href="https://github.com/dhowe/RiTa/graphs/contributors"><img src="https://opencollective.com/RiTa/contributors.svg?width=890&button=false" /></a>

### Financial Contributors
<a href="https://opencollective.com/rita/donate" target="_blank">
  <img src="https://opencollective.com/rita/contribute/button@2x.png?color=blue" width=300 />
</a>

