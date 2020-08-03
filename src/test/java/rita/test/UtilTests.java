package rita.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import rita.Util;

// For testing Util.java and other utility classes
public class UtilTests {

	@Test
	public void testMapOpts() {
		Map<String, Object> opts, res;
		opts = new HashMap<String, Object>();
		opts.put("context", Util.opts("a", "b"));
		res = Util.mapOpt("context", opts);
		assertTrue(res.equals(Util.opts("a", "b")));

		opts = new HashMap<String, Object>();
		opts.put("context", Util.opts("a", "b"));
		res = Util.mapOpt("contextX", opts);
		assertNull(res);
	}

	@Test
	public void testMergeMaps() {
		Map<String, Object> map;
		map = Util.deepMerge(Util.opts(), Util.opts("a", "1"));
		assertTrue(map.equals(Util.opts("a", "1")));

		map = Util.deepMerge(Util.opts("a", "1"), Util.opts("a", "1"));
		assertTrue(map.equals(Util.opts("a", "1")));

		map = Util.deepMerge(Util.opts("a", "2"), Util.opts("a", "1"));
		assertTrue(map.equals(Util.opts("a", "1")));

		map = Util.deepMerge(Util.opts("a", "2", "b", "2"), Util.opts("a", "1"));
		//System.out.println(map);
		assertTrue(map.equals(Util.opts("a", "1", "b", "2")));
	}

	@Test
	public void testSliceArray() {

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
	public void testSliceList() {

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
