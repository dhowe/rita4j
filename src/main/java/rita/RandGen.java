package rita;

import java.util.*;

public class RandGen {

	private static Random generator;
	static {
		generator = new Random(System.currentTimeMillis());
	}

	private RandGen() {
	}

	public static int randomInt() {
		return generator.nextInt();
	}

	public static final float random() {
		return generator.nextFloat();
	}

	public static final float random(float high) {
		if (high == 0 || high != high) return 0;
		float value = 0;
		do {
			value = generator.nextFloat() * high;
		} while (value == high);
		return value;
	}

	public static final float random(float low, float high) {
		if (low >= high) return low;
		float diff = high - low, value = 0;
		do {
			value = random(diff) + low;
		} while (value == high);
		return value;
	}

	public static int[] randomOrdering(int num) {
		int[] a = new int[num];
		for (int i = 0; i < num; ++i) {
			a[i] = i;
		}
		return a;
	}

	public static final <T> T randomItem(T[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
		//return typeof func === 'function' ? func(item) : item;
	}

	@SuppressWarnings("unchecked")
	public static <T> T randomItem(Collection<T> c) {
		return (T) randomItem(c.toArray());  // TODO: needs testing
	}
	
	public static final float randomItem(float[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
	}

	public static final boolean randomItem(boolean[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
	}

	public static final int randomItem(int[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
	}

	public static final double randomItem(double[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
	}

	public static void seed(int s) {
		generator = new Random(s);
	}

	public static double[] ndist(double[] weights, double t) {
		throw new RuntimeException("Implement me");
	}

	public static int pselect(double sm) {
		throw new RuntimeException("Implement me");
	}

	public static double[] ndist(double[] ds) {
		throw new RuntimeException("Implement me");
	}

}
