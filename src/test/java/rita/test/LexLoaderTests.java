package rita.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import rita.RiTa;
import rita.console;

public class LexLoaderTests {

	@Test
	public void testLoading() {
		Instant now = Instant.now();
		int numWords = RiTa.words().length;
		long timeMs = Duration.between(now, Instant.now()).toMillis();
		//console.log("1) "+numWords + " words in " + timeMs + "ms");
		assertTrue(numWords > 22000);
		assertTrue(timeMs < 400);

		now = Instant.now();
		numWords = RiTa.words().length;
		timeMs = Duration.between(now, Instant.now()).toMillis();
		//console.log("2) "+numWords + " words in " + timeMs + "ms");
		assertTrue(numWords > 22000);
		assertTrue(timeMs < 20);
	}
}
