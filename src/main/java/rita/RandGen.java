package rita;

import java.util.*;
import java.util.stream.DoubleStream;

import rita.Markov.Node;

public class RandGen {

	private static Random generator;
	static {
		generator = new Random(System.currentTimeMillis());
	}

	RandGen() {
		
	}

	public static int randomInt() {
		return generator.nextInt();
	}

	public static final float random() {
		return generator.nextFloat();
	}

	public static final float random(float high) {
		if (high == 0 || high != high)
			return 0;
		float value = 0;
		do {
			value = generator.nextFloat() * high;
		} while (value == high);
		return value;
	}

	public static final float random(float low, float high) {
		if (low >= high)
			return low;
		float diff = high - low, value = 0;
		do {
			value = random(diff) + low;
		} while (value == high);
		return value;
	}
	
	public static final double randomDouble() {
		return randomInt() * (1.0 / 4294967296.0);
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
		// return typeof func === 'function' ? func(item) : item;
	}

	@SuppressWarnings("unchecked")
	public static <T> T randomItem(Collection<T> c) {
		return (T) randomItem(c.toArray()); // TODO: needs testing
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
	
	public static double[] ndist(int[] weights) {
		double[] w = Arrays.stream(weights)
				.mapToDouble(num -> (double)num)
                .toArray();
		return ndist(w);
	}
	
	public static double[] ndist(int[] weights, double temp) {
		double[] w = Arrays.stream(weights)
				.mapToDouble(num -> (double)num)
                .toArray();
		return ndist(w, temp);
	}

	public static double[] ndist(double[] weights) {
		ArrayList<Double> probs = new ArrayList<>(); 
		double sum = DoubleStream.of(weights).sum();
		
		for (int i = 0; i < weights.length; i++) {
	        if (weights[i] < 0) throw new RiTaException("Weights must be positive");
	        probs.add(weights[i]/sum);
	     }
		
		 return probs.stream().mapToDouble(d -> d).toArray();
	}

	public static double[] ndist(double[] weights, double temp) {
		if (temp == 0) return(ndist(weights));
		// have temp, do softmax
		if (temp < 0.01) temp = 0.01;
		ArrayList<Double> probs = new ArrayList<>(); 
		
		double t = temp;
		weights = DoubleStream.of(weights)
				  .map(w -> Math.exp(w/t)).toArray(); 
		double sum = DoubleStream.of(weights).sum();

		for (int i = 0; i < weights.length; i++) {
	        if (weights[i] < 0) throw new RiTaException("Weights must be positive");
	        probs.add(Math.exp(weights[i] / temp)/sum);
	     }
		   
		return probs.stream().mapToDouble(d -> d).toArray();
	}

	public static int pselect(double[] probs) {
		double point = randomDouble();
		int cutoff = 0;
	    for (int i = 0; i < probs.length - 1; ++i) {
	      cutoff += probs[i];
	      if (point < cutoff) return i;
	    }
	    return probs.length - 1;
	}

}
