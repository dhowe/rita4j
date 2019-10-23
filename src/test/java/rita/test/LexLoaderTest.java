package rita.test;

import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import rita.RiTa;

public class LexLoaderTest
{
  @Test
  public void testLoading()
  {
    Instant now = Instant.now();
    int numWords = RiTa.words().length;
    long timeMs = Duration.between(now, Instant.now()).toMillis();
    assertTrue(numWords > 25000);
    assertTrue(timeMs < 500);
    
    now = Instant.now();
    numWords = RiTa.words().length;
    timeMs = Duration.between(now, Instant.now()).toMillis();
    assertTrue(numWords > 25000);
    assertTrue(timeMs < 50);
  }
}
