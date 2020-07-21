package rita;

public class RandGen {

	public static int[] randomOrdering(int num) {
		/*
		 * let o = Array.from(Array(num).keys()); for (let j, x, i = o.length; i; j =
		 * parseInt(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x) {
		 */ /* shuffle */ // }
		// return o;
		return null;
	}

	public static float random(float... f) {
		/*
		 * let crand = randomFloat(); if (!arguments.length) return crand; if
		 * (Array.isArray(arguments[0])) { return arguments[0][Math.floor(crand *
		 * arguments[0].length)]; } return arguments.length === 1 ? crand * arguments[0]
		 * : crand * (arguments[1] - arguments[0]) + arguments[0];
		 */
		return -1;
	}

	public static void seed(int s) {
		/*
		 * this.mt[0] = s >>> 0; for (this.mti = 1; this.mti < this.N; this.mti++) { let
		 * s = this.mt[this.mti - 1] ^ (this.mt[this.mti - 1] >>> 30); this.mt[this.mti]
		 * = (((((s & 0xffff0000) >>> 16) * 1812433253) << 16) + (s & 0x0000ffff) *
		 * 1812433253) + this.mti; this.mt[this.mti] >>>= 0; }
		 */
	}

	public static double[] ndist(double[] weights, double t) {
		// TODO Auto-generated method stub
		return null;
	}

	public static int pselect(double sm) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static double[] ndist(double[] ds) {
		// TODO Auto-generated method stub
		return null;
	}

}
