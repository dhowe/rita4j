package rita.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rita.Util.opts;

import java.util.*;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.*;

// Failing tests go here until debugged
public class KnownIssues {

	public void singularizeBugs() { // also in js
		String[] tests = {
				"grooves", "groove",
				"universes", "universe",
				"toothbrushes", "toothbrush",
				"clashes", "clash",
				"addresses", "address",
				"flashes", "flash",
				"conclaves", "conclave",
				"promises", "promise",
				"spouses", "spouse",
				"branches", "branch",
				"lapses", "lapse",
				"quizes", "quiz",
				"spyglasses", "spyglass",
				"overpasses", "overpass",
				"clones", "clones",
				"microwaves", "microwave",
				"hypotheses", "hypothesis",
				"pretenses", "pretense",
				"latches", "latch",
				"fetuses", "fetus",
				"alumni", "alumnus",
				"lighthouses", "lighthouse",
				"genuses", "genus",
				"zombies", "zombie",
				"hearses", "hearse",
				"trenches", "trench",
				"paradoxes", "paradox",
				"hippies", "hippie",
				"yuppies", "yuppie",
				"purses", "purse",
				"hatches", "hatch",
				"witches", "witch",
				"sinuses", "sinus",
				"phrases", "phrase",
				"arches", "arch",
				"duplexes", "duplex",
				"missives", "missive",
				"madhouses", "madhouse",
				"pauses", "pause",
				"heroes", "hero",
				"sketches", "sketch",
				"meshes", "mesh",
				"brasses", "brass",
				"marshes", "marsh",
				"masses", "mass",
				"overpasses", "overpass",
				"impulses", "impulse",
				"pelvises", "pelvis",
				"fetishes", "fetish",
				"abysses", "abyss",
				"lighthouses", "lighthouse",
				"gashes", "gash",
				"directives", "directive",
				"calories", "calorie",
				"moves", "move",
				"expanses", "expanse",
				"briefcases", "briefcase",
		};
		for (int i = 0; i < tests.length; i += 2) {
			System.out.println(tests[i] + " -> " + RiTa.singularize(tests[i])
					+ ", but expecting " + tests[i + 1]);
			assertEquals(RiTa.singularize(tests[i]), tests[i + 1]);
		}
	}
	
	// NOT SURE WHY THIS TEST EXISTS
	//assertEq(RiTa.evaluate("$foo=bar \\nbaz\n$foo", ctx), "bar baz");

	public static void main(String[] args) {
		new KnownIssues().singularizeBugs();
	}

}
