/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package rita.test;


import org.junit.Test;

import rita.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LexiconTests {


	@Test 
	public void testToPhoneArray() {
		

		String[] result = RiTa._lexicon().toPhoneArray(RiTa._lexicon()._rawPhones("tornado",false));
		String[] ans = { "t", "ao", "r", "n", "ey", "d", "ow" };
		assertArrayEquals(result,ans);
		 

	}

	@Test 
	public void testAlliterations() {


		String[] result;

		// TODO: make sure we have LTS cases in here

		result = RiTa.alliterations("");
		assertTrue(result.length < 1);

		result = RiTa.alliterations("#$%^&*");
		assertTrue(result.length < 1);

		result = RiTa.alliterations("umbrella");
		assertTrue(result.length < 1);

		result = RiTa.alliterations("cat stress");
		System.out.println(RiTa.alliterations("cat stress").length);
		assertTrue(result.length > 2000);

		result = RiTa.alliterations("cat");
		assertTrue(result.length > 2000);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));
		}

		result = RiTa.alliterations("dog");
		assertTrue(result.length > 1000);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog"));
		}

		Map<String, Object> hm = new HashMap<String, Object>(); 

		hm.put("matchMinLength", 15);

		result = RiTa.alliterations("dog", hm);
		assertTrue(result.length < 5); //, "got length=" + result.length
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "dog")); //, "FAIL1: " + result[i]
		}


		hm.clear();
		hm.put("matchMinLength", 16);

		result = RiTa.alliterations("cat", hm);
		assertTrue(result.length < 15);
		for (int i = 0; i < result.length; i++) {
			assertTrue(RiTa.isAlliteration(result[i], "cat"));//, "FAIL2: " + result[i]
		}

	}


	public static boolean contains(String[] arr, String item) {
		for (String n : arr) {
			if (item == n) {
				return true;
			}
		}
		return false;
	}


	@Test 
	public void testRhymes() {

		// TODO: add more tests

		System.out.println(Arrays.asList(RiTa.rhymes("eight")));


		assertTrue(Arrays.asList(RiTa.rhymes("cat")).contains("hat"));
		assertTrue(Arrays.asList(RiTa.rhymes("yellow")).contains("mellow"));
		assertTrue(Arrays.asList(RiTa.rhymes("toy")).contains("boy"));
		assertTrue(Arrays.asList(RiTa.rhymes("sieve")).contains("give"));

		assertTrue(Arrays.asList(RiTa.rhymes("tense")).contains("sense"));
		assertTrue(Arrays.asList(RiTa.rhymes("crab")).contains("drab"));
		assertTrue(Arrays.asList(RiTa.rhymes("shore")).contains("more"));

		assertTrue(Arrays.asList(RiTa.rhymes("mouse")).contains("house"));


		assertTrue(Arrays.asList(RiTa.rhymes("weight")).contains("eight"));
		assertTrue(Arrays.asList(RiTa.rhymes("eight")).contains("weight"));


		assertTrue(!Arrays.asList(RiTa.rhymes("apple")).contains("polo"));
		assertTrue(!Arrays.asList(RiTa.rhymes("this")).contains("these"));

		assertTrue(!Arrays.asList(RiTa.rhymes("hose")).contains("house"));
		assertTrue(!Arrays.asList(RiTa.rhymes("sieve")).contains("mellow"));
		assertTrue(!Arrays.asList(RiTa.rhymes("swag")).contains("grab"));

	}




	@Test 
	public void testSimilarBy() {

		String[] result, answer;

		Map<String, Object> hm = new HashMap<String, Object>(); 

		hm.put("preserveLength", true);


		//similarBy(letter)
		result = RiTa.similarBy("banana", hm);
		System.out.println("result : ");
		System.out.println(Arrays.toString(result));
		assertArrayEquals(result, new String[]{"cabana"});
		/*//TODO do we need one param option?
		result = RiTa.similarBy("");
		assertArrayEquals(result, new String[]{});
		 */
		hm.clear();
		hm.put("preserveLength", false);

		result = RiTa.similarBy("banana", hm);
		assertArrayEquals(result, new String[]{"banal", "bonanza", "cabana", "manna"});

		/*//TODO do we need one param option?
		result = RiTa.similarBy("banana");
		assertArrayEquals(result, new String[]{"banal", "bonanza", "cabana", "manna"});
		 */

		hm.clear();
		hm.put("minAllowedDistance", 1);
		hm.put("preserveLength", true);
		result = RiTa.similarBy("banana", hm);
		assertArrayEquals(result, new String[]{"cabana"});

		hm.clear();
		hm.put("minAllowedDistance", 1);
		hm.put("preserveLength", false);
		result = RiTa.similarBy("banana", hm);
		assertArrayEquals(result, new String[]{"banal", "bonanza", "cabana", "manna"});

		/*//TODO do we need one param option?
		result = RiTa.similarBy("tornado");
		assertArrayEquals(result, new String[]{"torpedo"});

		result = RiTa.similarBy("ice");
		assertArrayEquals(result, new String[]{"ace", "dice", "iced", "icy", "ire", "nice", "rice", "vice"});
		 */
		hm.clear();
		hm.put("minAllowedDistance", 1);
		result = RiTa.similarBy("ice", hm);
		assertArrayEquals(result, new String[]{"ace", "dice", "iced", "icy", "ire", "nice", "rice", "vice"});

		hm.clear();
		hm.put("minAllowedDistance", 2);
		hm.put("preserveLength", true);
		result = RiTa.similarBy("ice", hm);
		assertTrue(result.length > 10);


		hm.clear();
		hm.put("minAllowedDistance", 0);
		hm.put("preserveLength", true);
		result = RiTa.similarBy("ice", hm); // defaults to 1
		assertArrayEquals(result, new String[]{"ace", "icy", "ire"});

		hm.clear();
		hm.put("minAllowedDistance", 1);
		hm.put("preserveLength", true);
		result = RiTa.similarBy("ice", hm);
		assertArrayEquals(result, new String[]{"ace", "icy", "ire"});
		/*//TODO do we need one param option?
		result = RiTa.similarBy("worngword");
		assertArrayEquals(result, new String[]{"foreword", "wormwood"});

		result = RiTa.similarBy("123");
		assertTrue(result.length > 400);
		 */
		//similarBy(sound)

		hm.clear();
		hm.put("type", "sound");
		assertArrayEquals(RiTa.similarBy("tornado", hm), new String[]{"torpedo"});

		hm.clear();
		hm.put("type", "sound");
		result = RiTa.similarBy("try", hm);
		answer = new String[]{"cry", "dry", "fry", "pry", "rye", "tie", "tray", "tree", "tribe", "tried", "tripe", "trite", "true", "wry"};
		assertArrayEquals(result, answer);

		hm.clear();
		hm.put("type", "sound");
		hm.put("minAllowedDistance", 2);
		result = RiTa.similarBy("try", hm);
		assertTrue(result.length > answer.length); // more

		hm.clear();
		hm.put("type", "sound");
		result = RiTa.similarBy("happy", hm);
		answer = new String[]{"happier", "hippie"};
		assertArrayEquals(result, answer);

		hm.clear();
		hm.put("type", "sound");
		hm.put("minAllowedDistance", 2);
		result = RiTa.similarBy("happy", hm);
		assertTrue(result.length > answer.length); // more

		hm.clear();
		hm.put("type", "sound");
		result = RiTa.similarBy("cat", hm);
		answer = new String[]{"at", "bat", "cab", "cache", "calf", "calve", "can", "can\'t", "cap", "capped", "cash", "cashed", "cast", "caste", "catch", "catty", "caught", "chat", "coat", "cot", "curt", "cut", "fat", "hat", "kit", "kite", "mat", "matt", "matte", "pat", "rat", "sat", "tat", "that", "vat"};
		assertArrayEquals(result, answer);

		hm.clear();
		hm.put("type", "sound");
		hm.put("minAllowedDistance", 2);
		result = RiTa.similarBy("cat", hm);
		assertTrue(result.length > answer.length);

		hm.clear();
		hm.put("type", "sound");
		result = RiTa.similarBy("worngword", hm);
		assertArrayEquals(result, new String[]{"watchword", "wayward", "wormwood"});



		//similarBy(soundAndLetter)

		//result = RiTa.similarBy("", { type: 'soundAndLetter' }
		//assertArrayEquals(result, new String[]{});

		hm.clear();
		hm.put("type", "soundAndLetter");
		result = RiTa.similarBy("try", hm);
		assertArrayEquals(result, new String[]{"cry", "dry", "fry", "pry", "tray", "wry"});

		hm.clear();
		hm.put("type", "soundAndLetter");
		result = RiTa.similarBy("daddy", hm);
		assertArrayEquals(result, new String[]{"dandy", "paddy"});

		hm.clear();
		hm.put("type", "soundAndLetter");
		result = RiTa.similarBy("banana", hm);
		assertArrayEquals(result, new String[]{"bonanza"});

		hm.clear();
		hm.put("type", "soundAndLetter");
		result = RiTa.similarBy("worngword", hm);
		assertArrayEquals(result, new String[]{"wormwood"});
	}


	@Test 
	public void testIsRhyme() {



		assertTrue(!RiTa.isRhyme("apple", "polo"));
		assertTrue(!RiTa.isRhyme("this", "these"));

		assertTrue(RiTa.isRhyme("cat", "hat"));
		assertTrue(RiTa.isRhyme("yellow", "mellow"));
		assertTrue(RiTa.isRhyme("toy", "boy"));
		assertTrue(RiTa.isRhyme("sieve", "give"));

		assertTrue(RiTa.isRhyme("solo", "tomorrow"));
		assertTrue(RiTa.isRhyme("tense", "sense"));
		assertTrue(RiTa.isRhyme("crab", "drab"));
		assertTrue(RiTa.isRhyme("shore", "more"));
		assertTrue(!RiTa.isRhyme("hose", "house"));
		assertTrue(!RiTa.isRhyme("sieve", "mellow"));

		assertTrue(RiTa.isRhyme("mouse", "house")); //why??
		// assertTrue(!RiTa.isRhyme("solo", "yoyo"));
		// assertTrue(!RiTa.isRhyme("yoyo", "jojo")); -> Known Issues

		assertTrue(RiTa.isRhyme("yo", "bro"));
		assertTrue(!RiTa.isRhyme("swag", "grab"));
		assertTrue(!RiTa.isRhyme("", ""));

		assertTrue(RiTa.isRhyme("weight", "eight"));
		assertTrue(RiTa.isRhyme("eight", "weight"));
	}


	@Test 
	public void testIsAlliteration() {


		assertTrue(RiTa.isAlliteration("knife", "gnat")); // gnat=lts
		assertTrue(RiTa.isAlliteration("knife", "naughty"));

		assertTrue(RiTa.isAlliteration("sally", "silly"));
		assertTrue(RiTa.isAlliteration("sea", "seven"));
		assertTrue(RiTa.isAlliteration("silly", "seven"));
		assertTrue(RiTa.isAlliteration("sea", "sally"));

		assertTrue(RiTa.isAlliteration("big", "bad"));
		assertTrue(RiTa.isAlliteration("bad", "big")); // swap position

		assertTrue(RiTa.isAlliteration("BIG", "bad")); // CAPITAL LETTERS
		assertTrue(RiTa.isAlliteration("big", "BAD")); // CAPITAL LETTERS
		assertTrue(RiTa.isAlliteration("BIG", "BAD")); // CAPITAL LETTERS
		assertTrue(RiTa.isAlliteration("this", "these"));

		// False
		assertTrue(!RiTa.isAlliteration("", ""));
		assertTrue(!RiTa.isAlliteration("wind", "withdraw"));
		assertTrue(!RiTa.isAlliteration("solo", "tomorrow"));
		assertTrue(!RiTa.isAlliteration("solo", "yoyo"));
		assertTrue(!RiTa.isAlliteration("yoyo", "jojo"));
		assertTrue(!RiTa.isAlliteration("cat", "access"));

		assertTrue(RiTa.isAlliteration("unsung", "sine"));
		assertTrue(RiTa.isAlliteration("job", "gene"));
		assertTrue(RiTa.isAlliteration("jeans", "gentle"));

		assertTrue(RiTa.isAlliteration("abet", "better"));
		assertTrue(RiTa.isAlliteration("psychology", "cholera"));
		assertTrue(RiTa.isAlliteration("consult", "sultan"));
		assertTrue(RiTa.isAlliteration("never", "knight"));
		assertTrue(RiTa.isAlliteration("knight", "navel"));
		assertTrue(RiTa.isAlliteration("monsoon", "super"));
		assertTrue(RiTa.isAlliteration("cat", "kitchen"));

		// not counting assonance
		assertTrue(!RiTa.isAlliteration("octopus", "oblong"));
		assertTrue(!RiTa.isAlliteration("omen", "open"));
		assertTrue(!RiTa.isAlliteration("amicable", "atmosphere"));
	}



}
