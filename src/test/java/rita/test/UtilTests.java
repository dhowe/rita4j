package rita.test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import rita.ChoiceState;
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

}
