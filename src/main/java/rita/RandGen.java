package rita;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.ibm.icu.util.TimeZone.SystemTimeZoneType;

import rita.Markov.Node;

public abstract class RandGen {

	private static Random generator;
	static {
		generator = new Random(System.currentTimeMillis());
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

	/////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public static final <T> List<T> randomOrdering(List<T> list) {
		Collections.shuffle(list);
		return list;
	}
	
	public static final float[] randomOrdering(float[] arr) { // hideous java
		List<Float> arrList = new ArrayList<Float>();
		for (int i = 0; i < arr.length; i++) {
			arrList.add(Float.valueOf(arr[i]));
		}
		Float[] ro = randomOrdering(arrList).toArray(new Float[0]);
		float[] p = new float[ro.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = ro[i];
		}
		return p;
	}

	public static final boolean[] randomOrdering(boolean[] arr) {// hideous java
		List<Boolean> arrList = new ArrayList<Boolean>();
		for (int i = 0; i < arr.length; i++) {
			arrList.add(Boolean.valueOf(arr[i]));
		}
		Boolean[] ro = randomOrdering(arrList).toArray(new Boolean[0]);
		boolean[] p = new boolean[ro.length];
		for (int i = 0; i < p.length; i++) {
			p[i] = ro[i];
		}
		return p;
	}
	
	public static final double[] randomOrdering(double[] arr) { // slightly less hideous 
		Double[] ro = randomOrdering(Arrays.stream(arr).boxed()
				.collect(Collectors.toList())).toArray(new Double[0]);
		return Arrays.stream(ro).mapToDouble(Double::doubleValue).toArray();
	}

	public static final int[] randomOrdering(int[] arr) { // slightly less hideous 
		Integer[] ro = randomOrdering(Arrays.stream(arr).boxed()
				.collect(Collectors.toList())).toArray(new Integer[0]);
		return Arrays.stream(ro).mapToInt(Integer::intValue).toArray();
	}
	
	public static final <T> T[] randomOrdering(final T[] arr) {
		int index;
		Random random = new Random();
		T[] result = Arrays.copyOf(arr, arr.length);
		for (int i = result.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			if (index != i) swap(result, i, index);
		}
		return result;
	}

	public static final <T> T randomItem(T[] arr) {
		return arr[(int) Math.floor(random() * arr.length)];
	}

	@SuppressWarnings("unchecked")
	public static final <T> T randomItem(Collection<T> c) {
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

	public static final void seed(int s) {
		generator = new Random(s);
	}

	public static final double[] ndist(int[] weights) {
		double[] w = Arrays.stream(weights)
				.mapToDouble(num -> (double) num)
				.toArray();
		return ndist(w);
	}

	public static final double[] ndist(int[] weights, double temp) {
		double[] w = Arrays.stream(weights)
				.mapToDouble(num -> (double) num)
				.toArray();
		return ndist(w, temp);
	}

	public static final double[] ndist(double[] weights) {
		ArrayList<Double> probs = new ArrayList<>();
		double sum = DoubleStream.of(weights).sum();

		for (int i = 0; i < weights.length; i++) {
			if (weights[i] < 0) throw new RiTaException("Weights must be positive");
			probs.add(weights[i] / sum);
		}

		return probs.stream().mapToDouble(d -> d).toArray();
	}

	public static final double[] ndist(double[] weights, double temp) {
		if (temp == 0) return (ndist(weights));
		// have temp, do softmax
		if (temp < 0.01) temp = 0.01;
		ArrayList<Double> probs = new ArrayList<>();
		double sum = 0;
		for (int i = 0; i < weights.length; i++) {
			double pr = Math.exp((double) weights[i] / temp);
			sum += pr;
			probs.add(pr);
		}
		double[] result = new double[probs.size()];
		for (int i = 0; i < probs.size(); i++) {
			result[i] = (double) probs.get(i) / sum;
		}
		return result;
	}

	public static final int pselect(double[] probs) {
		double sum = 0;
		for (int i = 0; i < probs.length; i++) {
			sum += probs[i];
		}
		if (Math.abs((double) sum - 1) > 0.0001) {
			System.out.println("RandGen.pselect() WARNING: probs must sum to 1, was " + sum);
		}
		//double point = randomDouble();
		//coz inJava nextInt() can return negetive value so the range of randomDouble() is -0.5~0.5
		//not sure if randomDouble() randomInt() are used elsewhere so just modifidy the code here
		double point = (double) generator.nextInt(2147483647) * (1.0 / 2147483647.0);// to generate only positive number
		double cutoff = 0;
		for (int i = 0; i < probs.length - 1; ++i) {
			cutoff += probs[i];
			if (point < cutoff) {
				return i;
			}
		}
		return probs.length - 1;
	}
	
	private static final <T> void swap(T[] arr, int i, int j) {
		T tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

	public static void main(String[] args) {
		Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6 };
		Integer[] rand = randomOrdering(ints);
		console.log(rand);
	}
}
