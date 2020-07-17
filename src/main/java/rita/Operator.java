package rita;

public class Operator {

	private static final int EQUALITY = 1;
	private static final int MATCHING = 2;
	private static final int COMPARISON = 3;

	public static final Operator EQ = new Operator("=", EQUALITY);
	public static final Operator NE = new Operator("!=", EQUALITY);

	public static final Operator SW = new Operator("^=", MATCHING);
	public static final Operator EW = new Operator("$=", MATCHING);
	public static final Operator RE = new Operator("*=", MATCHING);

	public static final Operator GT = new Operator(">", COMPARISON);
	public static final Operator LT = new Operator("<", COMPARISON);
	public static final Operator LE = new Operator("<=", COMPARISON);
	public static final Operator GE = new Operator(">=", COMPARISON);

	public static final Operator[] ALL = new Operator[] {
			GT, LT, NE, LE, GE, SW, EQ, EW, RE
	};

	private String value;
	private int type;

	private Operator(String val, int type) {
		this.value = val;
		this.type = type;
	}

	public boolean invoke(String s1, String s2) {
		if (s1 == null) {
			throw new RiTaException("No first operand: " + s1 + " " + s2);
		}
		if (this.type == EQUALITY) {
			if (this == EQ) return s1.equals(s2);
			if (this == NE) return !s1.equals(s2);
		} else if (this.type == MATCHING) {
			if (s2 == null) return false;
			if (this == SW) return s1.startsWith(s2);
			if (this == EW) return s1.endsWith(s2);
			if (this == RE) return rita.RE.test(s2, s1);
		} else if (this.type == COMPARISON) {
			try {
				float f1 = Float.parseFloat(s1);
				float f2 = Float.parseFloat(s2);
				if (this == GT) return f1 > f2;
				if (this == LT) return f1 < f2;
				if (this == GE) return f1 >= f2;
				if (this == LE) return f1 <= f2;
			} catch (Exception e) {
				throw new RiTaException("Expected numeric operands"
						+ ", found [" + s1 + "," + s2 + "]\n", e);
			}
		}
		return false; // never
		
	}

	public static String fromOperator(Operator op) {
		for (int i = 0; i < ALL.length; i++) {
			if (op.equals(ALL[i])) return op.value;
		}
		throw new RiTaException("Invalid Operator: " + op);
	}

	public static Operator fromString(String op) {
		switch (op) {
		case ">":
			return GT;
		case "<":
			return LT;
		case ">=":
			return GE;
		case "<=":
			return LE;
		case "!=":
			return NE;
		case "^=":
			return SW;
		case "$=":
			return EW;
		case "*=":
			return RE;
		case "==":
			return EQ;
		case "=":
			return EQ;
		}
		throw new RiTaException("Invalid Operator: " + op);
	}
}
