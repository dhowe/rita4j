package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import rita.RiTa;
import rita.Util;

// For testing Util.java and other utility classes
public class UtilTests {

	@Test
	public void callRandomOrdering() {
		//this function is done by RandGen Class in Java
	}

	@Test
	public void callDeepMerge() {
		Map<String, Object> map;
		map = Util.deepMerge(RiTa.opts(), RiTa.opts("a", "1"));
		assertTrue(map.equals(RiTa.opts("a", "1")));

		map = Util.deepMerge(RiTa.opts("a", "1"), RiTa.opts("a", "1"));
		assertTrue(map.equals(RiTa.opts("a", "1")));

		map = Util.deepMerge(RiTa.opts("a", "2"), RiTa.opts("a", "1"));
		assertTrue(map.equals(RiTa.opts("a", "1")));

		map = Util.deepMerge(RiTa.opts("a", "2", "b", "2"), RiTa.opts("a", "1"));
		//System.out.println(map);
		assertTrue(map.equals(RiTa.opts("a", "1", "b", "2")));
	}

	@Test
	public void handleSliceArray() {

		String[] res, animals = { "ant", "bison", "camel", "duck", "elephant" };

		res = Util.slice(animals, 2);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "camel", "duck", "elephant" }, res);

		res = Util.slice(animals, 2, 4);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "camel", "duck" }, res);

		res = Util.slice(animals, 1, 5);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "bison", "camel", "duck", "elephant" }, res);

	}
	
	@Test
	public void handleSliceList() {

		String[] tmp = { "ant", "bison", "camel", "duck", "elephant" };
		List<String> animals = new ArrayList<String>(Arrays.asList(tmp));

		String[] res = Util.slice(animals, 2).toArray(new String[0]);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "camel", "duck", "elephant" }, res);

		res = Util.slice(animals, 2, 4).toArray(new String[0]);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "camel", "duck" }, res);

		res = Util.slice(animals, 1, 5).toArray(new String[0]);
		//System.out.println(Arrays.asList(res));
		assertArrayEquals(new String[] { "bison", "camel", "duck", "elephant" }, res);

	}

}
