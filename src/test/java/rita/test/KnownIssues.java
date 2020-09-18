package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import rita.RiTa;

import rita.Markov;

public class KnownIssues {
	String longerSample = "From what moment, from what semantic or syntactical change, can one recognize that language has turned into rational discourse? What sharp line divides a description that depicts membranes as being like damp parchment from that other equally qualitative, equally metaphorical description of them laid out over the tunic of the brain, like a film of egg whites? Do Bayles white and red membranes possess greater value, solidity, and objectivity—in terms of scientific discourse—than the horny scales described by the doctors of the eighteenth century? A rather more meticulous gaze, a more measured verbal tread with a more secure footing upon things, a more delicate, though sometimes rather confused choice of adjective—are these not merely the proliferation, in medical language, of a style which, since the days of galenic medicine, has extended whole regions of description around the greyness of things and their shapes? In order to determine the moment at which the mutation in discourse took place, we must look beyond its thematic content or its logical modalities to the region where things and words have not yet been separated, and where—at the most fundamental level of language—seeing and saying are still one. We must re-examine the original distribution of the visible and invisible insofar as it is linked with the division between what is stated and what remains unsaid: thus the articulation of medical language and its object will appear as a single figure. But if one poses no retrospective question, there can be no priority; only the spoken structure of the perceived—that full space in the hollow of which language assumes volume and size—may be brought up into the indifferent light of day. We must place ourselves, and remain once and for all, at the level of the fundamental spatialization and verbalization of the pathological, where the loquacious gaze with which the doctor observes the poisonous heart of things is born and communes with itself. Modern medicine has fixed its own date of birth as being in the last years of the eighteenth century. Reflecting on its situation, it identifies the origin of its positivity with a return—over and above all theory—to the modest but effecting level of the perceived. In fact, this supposed empiricism is not based on a rediscovery of the absolute values of the visible, nor on the predetermined rejection of systems and all their chimeras, but on a reorganization of that manifest and secret space that opened up when a millennial gaze paused over mens sufferings. Nonetheless the rejuvenation of medical perception, the way colours and things came to life under the illuminating gaze of the first clinicians is no mere myth. At the beginning of the nineteenth century, doctors described what for centuries had remained below the threshold of the visible and the expressible, but this did not mean that, after over-indulging in speculation, they had begun to perceive once again, or that they listened to reason rather than to imagination; it meant that the relation between the visible and invisible—which is necessary to all concrete knowledge—changed its structure, revealing through gaze and language what had previously been below and beyond their domain. A new alliance was forged between words and things, enabling one to see and to say. Sometimes, indeed, the discourse was so completely naive that it seems to belong to a more archaic level of rationality, as if it involved a return to the clear, innocent gaze of some earlier, golden age. In 1764, J.F.Meckel set out to study the alterations brought about in the brain by certain disorders (apoplexy, mania, phthisis); he used the rational method of weighing equal volumes and comparing them to determine which parts of the brain had been de-hydrated, which parts had been swollen, and by which diseases. Modern medicine has made hardly any use of this research. Brain pathology achieved its positive form when Bichat, and above all Récamier and Lallemand, used the celebrated hammer, with a broad, thin end. If one proceeds with light taps, no concussion liable to cause disorders can result as the skull is full. It is better to begin from the rear, because, when only the occipital has to be broken, it is often so mobile that one misses ones aim.... In the case of very young children, the bones are too supple to be broken and too thin to be sawn; they have to be cut with strong scissors [3]. The fruit is then opened up. From under the meticulously parted shell, a soft, greyish mass appears, wrapped in viscous, veined skins: a delicate, dingy- looking pulp within which—freed at last and exposed at last to the light of day—shines the seat of knowledge. The antisanal skill of the brain-breaker has replaced the scientific precision of the scales, and yet our science since Bichat identifies with the former; the precise, but immeasurable gesture that opens up the plenitude of concrete things, combined with the delicate network of their properties to the gaze, has produced a more scientific objectivity for us than instrumental arbitrations of quantity. Medical rationality plunges into the marvelous density of perception, offering the grain of things as the first face of truth, with their colours, their spots, their hardness, their adherence. The breadth of the experiment seems to be identified with the domain of the careful gaze, and of an empirical vigilance receptive only to the evidence of visible contents. The eye becomes the depositary and source of clarity; it has the power to bring a truth to light that it receives only to the extent that it has brought it to light; as it opens, the eye first opens the truth: a flexion that marks the transition from the world of classical clarity—from the enlightenment—to the nineteenth century.";

	// Failing tests we need to work on go here
	@Test
	public void simplestPossibleCode() {
		String[] sentences = new String[] {
				"it is www.google.com",
				"it is 'hell'"
		};
		System.out.println(RiTa.untokenize(RiTa.tokenize(sentences[0])));
		// => expect output: "it is www.google.com"
		System.out.println(RiTa.untokenize(RiTa.tokenize(sentences[1])));
		// => expect output: "it is 'hell'"
		assertEquals(RiTa.untokenize(RiTa.tokenize(sentences[0])), sentences[0]);
		assertEquals(RiTa.untokenize(RiTa.tokenize(sentences[1])), sentences[1]);
	}

	@Test
	public void forTokenizerDebug() {
		//calling tokenize() and untokenize() together seems to cause some werid bugs
		//but I guess normally no one do this
		String[][] tokens = new String[][] {
				new String[] { "it", "is", "www", ".", "google", ".", "com" },
				new String[] { "it", "is", "'", "hell", "'" },
				new String[] { "everything", "else", ",", "seems", "to", "be", "\"", "OK", "\"", "." }
		};

		String[] untokens = new String[] {
				"it is www.google.com",
				"it is 'hell'",
				"everything else, seems to be \"OK\"."
		};

		String[][] usingTokenize = new String[][] {
				RiTa.tokenize(untokens[0]),
				RiTa.tokenize(untokens[1]),
				RiTa.tokenize(untokens[2])
		};

		String[] usingUntokenize = new String[] {
				RiTa.untokenize(tokens[0]),
				RiTa.untokenize(tokens[1]),
				RiTa.untokenize(tokens[2])
		};

		String[] usingTokenizeThenUntokenize = new String[] {
				RiTa.untokenize(RiTa.tokenize(untokens[0])),
				RiTa.untokenize(RiTa.tokenize(untokens[1])),
				RiTa.untokenize(RiTa.tokenize(untokens[2]))
		};

		String[][] usingUntokenizeThenTokenize = new String[][] {
				RiTa.tokenize(RiTa.untokenize(tokens[0])),
				RiTa.tokenize(RiTa.untokenize(tokens[1])),
				RiTa.tokenize(RiTa.untokenize(tokens[2]))
		};

		String[] usingTokenizeThenUntokenizeWithMiddleMan = new String[] {
				RiTa.untokenize(usingTokenize[0]),
				RiTa.untokenize(usingTokenize[1]),
				RiTa.untokenize(usingTokenize[2])
		};

		for (int i = 0; i < tokens.length; i++) {
			System.out.println("tokens:");
			for (int j = 0; j < tokens[i].length; j++) {
				if (j != tokens[i].length - 1) {
					System.out.print(tokens[i][j] + "//");
				}
				else {
					System.out.println(tokens[i][j] + "\\\\");
				}
			}
			System.out.println("usingTokenize:");
			for (int j = 0; j < usingTokenize[i].length; j++) {
				if (j != usingTokenize[i].length - 1) {
					System.out.print(usingTokenize[i][j] + "//");
				}
				else {
					System.out.println(usingTokenize[i][j] + "\\\\");
				}
			}
			//just call tokenize() => everything OK
			System.out.println("using untokenize then tokenize:");
			for (int j = 0; j < usingUntokenizeThenTokenize[i].length; j++) {
				if (j != usingUntokenizeThenTokenize[i].length - 1) {
					System.out.print(usingUntokenizeThenTokenize[i][j] + "//");
				}
				else {
					System.out.println(usingUntokenizeThenTokenize[i][j] + "\\\\");
				}
			}
			//calling untokenize then tokenize seems to be ok
			System.out.println("untokens:");
			System.out.println(untokens[i]);
			System.out.println("usingUntokenize:");
			System.out.println(usingUntokenize[i]);
			// just call untokenize() => everything OK
			System.out.println("using tokenize then untokenize:");
			System.out.println(usingTokenizeThenUntokenize[i]);
			// call tokenize() then untokenize() together => bugs
			// bugs do not appear in js side
			System.out.println("using tokenize then untokenize with middle man");
			System.out.println(usingTokenizeThenUntokenizeWithMiddleMan[i]);
			// this also doesn't work...
			/* bugs only occur when: 1. encounter email and website address
			                         2. when single quotation marks appear after the word is */

			//assertArrayEquals(tokens[i], usingTokenize[i]);
			//assertEquals(untokens[i], usingUntokenize[i]);
			//assertEquals(untokens[i], usingTokenizeThenUntokenize[i]);
			//assertEquals(tokens[i],usingUntokenizeThenTokenize[i]);
		}
	}

	@Test
	public void markovGenerateDebug() {
		int n = 1;
		//different bugs appear when n=1 and n=2,3,4...
		Markov rm = new Markov(n);
		rm.addText(longerSample);
		//System.out.println(rm.input.toString());
		System.out.println(rm.generate());
	}

	@Test
	public void markovToStringBug() {
		//not testable
		//if do not visit (e.g, print it out to console) the value of markov.root.chlidren 
		//and the last word's chlidren, those value kind of gone, causing problems
		//see Markov.toString() (now is Markov.java line 43)
	}

}
