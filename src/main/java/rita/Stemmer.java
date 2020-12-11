package rita;

/**
 * Adapted from the Porter2/Snowball stemmer here:
 * http://snowball.tartarus.org/algorithms/english/stemmer.html
 */
public class Stemmer {

	private final static Am a0[] = {
			am("arsen", -1, -1),
			am("commun", -1, -1),
			am("gener", -1, -1)
	};

	private final static Am a1[] = {
			am("'", -1, 1),
			am("'s'", 0, 1),
			am("'s", -1, 1)
	};

	private final static Am a2[] = {
			am("ied", -1, 2),
			am("s", -1, 3),
			am("ies", 1, 2),
			am("sses", 1, 1),
			am("ss", 1, -1),
			am("us", 1, -1)
	};

	private final static Am a3[] = {
			am("", -1, 3),
			am("bb", 0, 2),
			am("dd", 0, 2),
			am("ff", 0, 2),
			am("gg", 0, 2),
			am("bl", 0, 1),
			am("mm", 0, 2),
			am("nn", 0, 2),
			am("pp", 0, 2),
			am("rr", 0, 2),
			am("at", 0, 1),
			am("tt", 0, 2),
			am("iz", 0, 1)
	};

	private final static Am a4[] = {
			am("ed", -1, 2),
			am("eed", 0, 1),
			am("ing", -1, 2),
			am("edly", -1, 2),
			am("eedly", 3, 1),
			am("ingly", -1, 2)
	};

	private final static Am a5[] = {
			am("anci", -1, 3),
			am("enci", -1, 2),
			am("ogi", -1, 13),
			am("li", -1, 16),
			am("bli", 3, 12),
			am("abli", 4, 4),
			am("alli", 3, 8),
			am("fulli", 3, 14),
			am("lessli", 3, 15),
			am("ousli", 3, 10),
			am("entli", 3, 5),
			am("aliti", -1, 8),
			am("biliti", -1, 12),
			am("iviti", -1, 11),
			am("tional", -1, 1),
			am("ational", 14, 7),
			am("alism", -1, 8),
			am("ation", -1, 7),
			am("ization", 17, 6),
			am("izer", -1, 6),
			am("ator", -1, 7),
			am("iveness", -1, 11),
			am("fulness", -1, 9),
			am("ousness", -1, 10)
	};

	private final static Am a6[] = {
			am("icate", -1, 4),
			am("ative", -1, 6),
			am("alize", -1, 3),
			am("iciti", -1, 4),
			am("ical", -1, 4),
			am("tional", -1, 1),
			am("ational", 5, 2),
			am("ful", -1, 5),
			am("ness", -1, 5)
	};

	private final static Am a7[] = {
			am("ic", -1, 1),
			am("ance", -1, 1),
			am("ence", -1, 1),
			am("able", -1, 1),
			am("ible", -1, 1),
			am("ate", -1, 1),
			am("ive", -1, 1),
			am("ize", -1, 1),
			am("iti", -1, 1),
			am("al", -1, 1),
			am("ism", -1, 1),
			am("ion", -1, 2),
			am("er", -1, 1),
			am("ous", -1, 1),
			am("ant", -1, 1),
			am("ent", -1, 1),
			am("ment", 15, 1),
			am("ement", 16, 1)
	};

	private final static Am a8[] = {
			am("e", -1, 1),
			am("l", -1, 2)
	};

	private final static Am a9[] = {
			am("succeed", -1, -1),
			am("proceed", -1, -1),
			am("exceed", -1, -1),
			am("canning", -1, -1),
			am("inning", -1, -1),
			am("earring", -1, -1),
			am("herring", -1, -1),
			am("outing", -1, -1)
	};

	private final static Am a10[] = {
			am("andes", -1, -1),
			am("atlas", -1, -1),
			am("bias", -1, -1),
			am("cosmos", -1, -1),
			am("dying", -1, 3),
			am("early", -1, 9),
			am("gently", -1, 7),
			am("howe", -1, -1),
			am("idly", -1, 6),
			am("lying", -1, 4),
			am("news", -1, -1),
			am("only", -1, 10),
			am("singly", -1, 11),
			am("skies", -1, 2),
			am("skis", -1, 1),
			am("sky", -1, -1),
			am("tying", -1, 5),
			am("ugly", -1, 8)
	};

	private static final char gV[] = { 17, 65, 16, 1 };
	private static final char gVwXY[] = { 1, 17, 65, 208, 1 };
	private static final char gValidLI[] = { 55, 141, 2 };

	private boolean bYFound;
	private int ip1, ip2, cursor, limit, limitBw, bra, ket;
	protected StringBuffer current;

	//////////////////////// statics ////////////////////////

	public static String[] stemAll(String[] words) {
		Stemmer s = new Stemmer();
		String[] out = new String[words.length];
		for (int i = 0; i < words.length; i++) {
			out[i] = s._stem(words[i]);
		}
		return out;
	}

	public static String stem(String str) {
		Stemmer s = new Stemmer();
		if (str.contains(" ")) {
			return RiTa.untokenize(stemAll(RiTa.tokenize(str)));
		}
		return s._stem(str);
	}

	//////////////////////// members ////////////////////////

	private Stemmer() {
		current = new StringBuffer();
	}

	protected String _stem(String word) {
		setCurrent(word);
		stemImpl();
		return current.toString();
	}

	private void setCurrent(String value) {
		current = new StringBuffer(value);
		cursor = 0;
		limit = current.length();
		limitBw = 0;
		bra = cursor;
		ket = limit;
	}

	protected boolean inGrouping(char[] s, int min, int max) {
		if (cursor >= limit) return false;
		char ch = current.charAt(cursor);
		String sa = "["; 
		for (char c : s) {
			sa += ((int)c) + ",";
		}
		sa = sa.substring(0,sa.length()-1)+"]";
		
		console.log("inGrouping: "+sa+" min=" + min
				+ ", max=" + max + ", ch("+cursor+")='" + (int)(char)(ch == '\r' ? "\\r" : ch) + "'");
		if (ch > max || ch < min) return false;
		ch -= min;
		if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) {
			//if (ch == '\r') ch = '\\r';//System.out.println("HIT");
			//			console.log("inGrouping: '" + new String(s) + "' min=" + min + ", max=" + max
			//					+ ", ch='" + (ch == '\r' ? "\\r" : ch) + "', '" + (ch == '\r' ? "\\r" : ch)
			//					+ "'>>3=" + (ch >> 3) + " s[" + (ch >> 3) + "] ='" + s[ch >> 3] + "'");
			return false;
		}
		cursor++;
		return true;
	}

	protected boolean inGroupingB(char[] s, int min, int max) {
		if (cursor <= limitBw) return false;
		char ch = current.charAt(cursor - 1);
		if (ch > max || ch < min) return false;
		ch -= min;
		if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) return false;
		cursor--;
		return true;
	}

	protected boolean outGrouping(char[] s, int min, int max) {
		if (cursor >= limit) return false;
		char ch = current.charAt(cursor);
		if (ch > max || ch < min) {
			cursor++;
			return true;
		}
		ch -= min;
		if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) {
			cursor++;
			return true;
		}
		return false;
	}

	protected boolean outGroupingB(char[] s, int min, int max) {
		if (cursor <= limitBw) return false;
		char ch = current.charAt(cursor - 1);
		if (ch > max || ch < min) {
			cursor--;
			return true;
		}
		ch -= min;
		if ((s[ch >> 3] & (0X1 << (ch & 0X7))) == 0) {
			cursor--;
			return true;
		}
		return false;
	}

	protected boolean inRange(int min, int max) {
		if (cursor >= limit) return false;
		char ch = current.charAt(cursor);
		if (ch > max || ch < min) return false;
		cursor++;
		return true;
	}

	protected boolean inRangeB(int min, int max) {
		if (cursor <= limitBw) return false;
		char ch = current.charAt(cursor - 1);
		if (ch > max || ch < min) return false;
		cursor--;
		return true;
	}

	protected boolean outRange(int min, int max) {
		if (cursor >= limit) return false;
		char ch = current.charAt(cursor);
		if (!(ch > max || ch < min)) return false;
		cursor++;
		return true;
	}

	protected boolean outRangeB(int min, int max) {
		if (cursor <= limitBw) return false;
		char ch = current.charAt(cursor - 1);
		if (!(ch > max || ch < min)) return false;
		cursor--;
		return true;
	}

	protected boolean eqS(int sSize, String s) {
		console.log("eqS: " + sSize + "," + s + ", " + this.cursor);
		if (limit - cursor < sSize) return false;
		int i;
		for (i = 0; i != sSize; i++) {
			if (current.charAt(cursor + i) != s.charAt(i)) {
				return false;
			}
		}
		cursor += sSize;
		return true;
	}

	protected boolean eqSB(int sSize, String s) {
		if (cursor - limitBw < sSize) return false;
		int i;
		for (i = 0; i != sSize; i++) {
			if (current.charAt(cursor - sSize + i) != s.charAt(i)) return false;
		}
		cursor -= sSize;
		return true;
	}

	protected boolean eqV(CharSequence s) {
		return eqS(s.length(), s.toString());
	}

	protected boolean eqVB(CharSequence s) {
		return eqSB(s.length(), s.toString());
	}

	protected int findAmong(Am v[], int vSize) {
		int i = 0, j = vSize, c = cursor, l = limit;
		int commonI = 0, commonJ = 0;

		boolean firstKeyInspected = false;

		while (true) {
			int k = i + ((j - i) >> 1);
			int i2, diff = 0;
			int common = commonI < commonJ ? commonI : commonJ; // smaller
			for (i2 = common; i2 < v[k].sSize; i2++) {
				if (c + common == l) {
					diff = -1;
					break;
				}
				diff = current.charAt(c + common) - v[k].s[i2];
				if (diff != 0) break;
				common++;
			}
			if (diff < 0) {
				j = k;
				commonJ = common;
			}
			else {
				i = k;
				commonI = common;
			}
			if (j - i <= 1) {
				if (i > 0) break; // v->s has been inspected
				if (j == i) break; // only one item in v

				// - but now we need to go round once more to get
				// v->s inspected. This looks messy, but is actually
				// the optimal approach.

				if (firstKeyInspected) break;
				firstKeyInspected = true;
			}
		}
		while (true) {
			Am w = v[i];
			if (commonI >= w.sSize) {
				cursor = c + w.sSize;
				return w.result;
			}
			i = w.substringI;
			if (i < 0) return 0;
		}
	}

	// findAmongB is for backwards processing. Same comments apply
	protected int findAmongB(Am v[], int vSize) {
		int i = 0, j = vSize, c = cursor, lb = limitBw;
		int commonI = 0, commonJ = 0;
		boolean firstKeyInspected = false;

		while (true) {
			int k = i + ((j - i) >> 1);
			int diff = 0;
			int common = commonI < commonJ ? commonI : commonJ;
			Am w = v[k];
			int i2;
			for (i2 = w.sSize - 1 - common; i2 >= 0; i2--) {
				if (c - common == lb) {
					diff = -1;
					break;
				}
				diff = current.charAt(c - 1 - common) - w.s[i2];
				if (diff != 0) break;
				common++;
			}
			if (diff < 0) {
				j = k;
				commonJ = common;
			}
			else {
				i = k;
				commonI = common;
			}
			if (j - i <= 1) {
				if (i > 0) break;
				if (j == i) break;
				if (firstKeyInspected) break;
				firstKeyInspected = true;
			}
		}
		while (true) {
			Am w = v[i];
			if (commonI >= w.sSize) {
				cursor = c - w.sSize;
				return w.result;
			}
			i = w.substringI;
			if (i < 0) return 0;
		}
	}

	/* to replace chars between cBra and cKet in current by the
	 * chars in s.
	 */
	protected int replaceS(int cBra, int cKet, String s) {
		int adjustment = s.length() - (cKet - cBra);
		current.replace(cBra, cKet, s);
		limit += adjustment;
		if (cursor >= cKet) cursor += adjustment;
		else if (cursor > cBra) cursor = cBra;
		return adjustment;
	}

	protected void sliceCheck() {
		if (bra < 0 ||
				bra > ket ||
				ket > limit ||
				limit > current.length())   // this line could be removed
		{
			throw new RuntimeException("faulty slice operation");
			// FIXME: report error somehow.
			/*
			fprintf(stderr, "faulty slice operation:\n");
			debug(z, -1, 0);
			exit(1);
			*/
		}
	}

	public String toString() {
		return "Stemmer[" + this.current + ", " + this.cursor + "]";
	}

	protected void sliceFrom(String s) {
		//if (1 == 1) throw new RuntimeException();
		sliceCheck();
		replaceS(bra, ket, s);
	}

	protected void sliceFrom(CharSequence s) {
		sliceFrom(s.toString());
	}

	protected void sliceDel() {
		sliceFrom("");
	}

	protected void insert(int cBra, int cKet, String s) {
		int adjustment = replaceS(cBra, cKet, s);
		if (cBra <= bra) bra += adjustment;
		if (cBra <= ket) ket += adjustment;
	}

	protected void insert(int cBra, int cKet, CharSequence s) {
		insert(cBra, cKet, s.toString());
	}

	/* Copy the slice into the supplied StringBuffer */
	protected StringBuffer sliceTo(StringBuffer s) {
		sliceCheck();
		s.replace(0, s.length(), current.substring(bra, ket));
		return s;
	}

	/* Copy the slice into the supplied StringBuilder */
	protected StringBuilder sliceTo(StringBuilder s) {
		sliceCheck();
		s.replace(0, s.length(), current.substring(bra, ket));
		return s;
	}

	protected StringBuffer assignTo(StringBuffer s) {
		s.replace(0, s.length(), current.substring(0, limit));
		return s;
	}

	protected StringBuilder assignTo(StringBuilder s) {
		s.replace(0, s.length(), current.substring(0, limit));
		return s;
	}

	private boolean rPrelude() {
		int v_1;
		int v_2;
		int v_3;
		int v_4;
		int v_5;
		// (, line 25
		// unset YFound, line 26
		bYFound = false;
		// do, line 27
		v_1 = cursor;
		lab0: do {
			// (, line 27
			// [, line 27
			bra = cursor;
			// literal, line 27
			if (!(eqS(1, "'"))) {
				break lab0;
			}
			// ], line 27
			ket = cursor;
			// delete, line 27
			sliceDel();
		} while (false);
		cursor = v_1;
		// do, line 28
		v_2 = cursor;
		lab1: do {
			// (, line 28
			// [, line 28
			bra = cursor;
			// literal, line 28
			if (!(eqS(1, "y"))) {
				break lab1;
			}
			// ], line 28
			ket = cursor;
			// <-, line 28
			sliceFrom("Y");
			// set YFound, line 28
			bYFound = true;
		} while (false);
		cursor = v_2;
		// do, line 29
		v_3 = cursor;

		do {
			// repeat, line 29
			replab3: while (true) {
				v_4 = cursor;
				lab4: do {
					// (, line 29
					// goto, line 29
					golab5: while (true) {
						v_5 = cursor;
						lab6: do {
							// (, line 29
							if (!(inGrouping(gV, 97, 121))) {
								console.log("break lab6.1, " + this.toString());
								break lab6;
							}
							// [, line 29
							bra = cursor;
							// literal, line 29
							if (!(eqS(1, "y"))) {
								console.log("break lab6.2, " + this.toString());

								break lab6;
							}

							// ], line 29
							ket = cursor;
							cursor = v_5;
							break golab5;
						} while (false);

						cursor = v_5;
						if (cursor >= limit) {

							break lab4;
						}
						cursor++;
						console.log(cursor);
					}
					// <-, line 29
					sliceFrom("Y");
					// set YFound, line 29
					bYFound = true;
					continue replab3;
				} while (false);
				cursor = v_4;
				break replab3;
			}
		} while (false);
		cursor = v_3;
		return true;
	}

	private boolean rMarkRegions() {
		int v_1;
		int v_2;
		// (, line 32
		ip1 = limit;
		ip2 = limit;
		// do, line 35
		v_1 = cursor;
		lab0: do {
			// (, line 35
			// or, line 41
			lab1: do {
				v_2 = cursor;
				lab2: do {
					// among, line 36
					if (findAmong(a0, 3) == 0) {
						break lab2;
					}
					break lab1;
				} while (false);
				cursor = v_2;
				// (, line 41
				// gopast, line 41
				golab3: while (true) {
					lab4: do {
						if (!(inGrouping(gV, 97, 121))) {
							break lab4;
						}
						break golab3;
					} while (false);
					if (cursor >= limit) {
						break lab0;
					}
					cursor++;
				}
				// gopast, line 41
				golab5: while (true) {
					lab6: do {
						if (!(outGrouping(gV, 97, 121))) {
							break lab6;
						}
						break golab5;
					} while (false);
					if (cursor >= limit) {
						break lab0;
					}
					cursor++;
				}
			} while (false);
			// setmark p1, line 42
			ip1 = cursor;
			// gopast, line 43
			golab7: while (true) {
				lab8: do {
					if (!(inGrouping(gV, 97, 121))) {
						break lab8;
					}
					break golab7;
				} while (false);
				if (cursor >= limit) {
					break lab0;
				}
				cursor++;
			}
			// gopast, line 43
			golab9: while (true) {
				lab10: do {
					if (!(outGrouping(gV, 97, 121))) {
						break lab10;
					}
					break golab9;
				} while (false);
				if (cursor >= limit) {
					break lab0;
				}
				cursor++;
			}
			// setmark p2, line 43
			ip2 = cursor;
		} while (false);
		cursor = v_1;
		return true;
	}

	private boolean rShortv() {
		int v_1;
		// (, line 49
		// or, line 51
		lab0: do {
			v_1 = limit - cursor;
			lab1: do {
				// (, line 50
				if (!(outGroupingB(gVwXY, 89, 121))) {
					break lab1;
				}
				if (!(inGroupingB(gV, 97, 121))) {
					break lab1;
				}
				if (!(outGroupingB(gV, 97, 121))) {
					break lab1;
				}
				break lab0;
			} while (false);
			cursor = limit - v_1;
			// (, line 52
			if (!(outGroupingB(gV, 97, 121))) {
				return false;
			}
			if (!(inGroupingB(gV, 97, 121))) {
				return false;
			}
			// atlimit, line 52
			if (cursor > limitBw) {
				return false;
			}
		} while (false);
		return true;
	}

	private boolean r_R1() {
		if (!(ip1 <= cursor)) {
			return false;
		}
		return true;
	}

	private boolean r_R2() {
		if (!(ip2 <= cursor)) {
			return false;
		}
		return true;
	}

	private boolean step1a() {
		int amongVar;
		int v_1;
		int v_2;
		// (, line 58
		// try, line 59
		v_1 = limit - cursor;
		lab0: do {
			// (, line 59
			// [, line 60
			ket = cursor;
			// substring, line 60
			amongVar = findAmongB(a1, 3);
			if (amongVar == 0) {
				cursor = limit - v_1;
				break lab0;
			}
			// ], line 60
			bra = cursor;
			switch (amongVar) {
			case 0:
				cursor = limit - v_1;
				break lab0;
			case 1:
				// (, line 62
				// delete, line 62
				sliceDel();
				break;
			}
		} while (false);
		// [, line 65
		ket = cursor;
		// substring, line 65
		amongVar = findAmongB(a2, 6);
		if (amongVar == 0) {
			return false;
		}
		// ], line 65
		bra = cursor;
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 66
			// <-, line 66
			sliceFrom("ss");
			break;
		case 2:
			// (, line 68
			// or, line 68
			lab1: do {
				v_2 = limit - cursor;
				lab2: do {
					// (, line 68
					// hop, line 68
					{
						int c = cursor - 2;
						if (limitBw > c || c > limit) {
							break lab2;
						}
						cursor = c;
					}
					// <-, line 68
					sliceFrom("i");
					break lab1;
				} while (false);
				cursor = limit - v_2;
				// <-, line 68
				sliceFrom("ie");
			} while (false);
			break;
		case 3:
			// (, line 69
			// next, line 69
			if (cursor <= limitBw) {
				return false;
			}
			cursor--;
			// gopast, line 69
			golab3: while (true) {
				lab4: do {
					if (!(inGroupingB(gV, 97, 121))) {
						break lab4;
					}
					break golab3;
				} while (false);
				if (cursor <= limitBw) {
					return false;
				}
				cursor--;
			}
			// delete, line 69
			sliceDel();
			break;
		}
		return true;
	}

	private boolean step1b() {
		int amongVar;
		int v_1;
		int v_3;
		int v_4;
		// (, line 74
		// [, line 75
		ket = cursor;
		// substring, line 75
		amongVar = findAmongB(a4, 6);
		if (amongVar == 0) {
			return false;
		}
		// ], line 75
		bra = cursor;
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 77
			// call R1, line 77
			if (!r_R1()) {
				return false;
			}
			// <-, line 77
			sliceFrom("ee");
			break;
		case 2:
			// (, line 79
			// test, line 80
			v_1 = limit - cursor;
			// gopast, line 80
			golab0: while (true) {
				lab1: do {
					if (!(inGroupingB(gV, 97, 121))) {
						break lab1;
					}
					break golab0;
				} while (false);
				if (cursor <= limitBw) {
					return false;
				}
				cursor--;
			}
			cursor = limit - v_1;
			// delete, line 80
			sliceDel();
			// test, line 81
			v_3 = limit - cursor;
			// substring, line 81
			amongVar = findAmongB(a3, 13);
			if (amongVar == 0) {
				return false;
			}
			cursor = limit - v_3;
			switch (amongVar) {
			case 0:
				return false;
			case 1:
			// (, line 83
			// <+, line 83
			{
				int c = cursor;
				insert(cursor, cursor, "e");
				cursor = c;
			}
				break;
			case 2:
				// (, line 86
				// [, line 86
				ket = cursor;
				// next, line 86
				if (cursor <= limitBw) {
					return false;
				}
				cursor--;
				// ], line 86
				bra = cursor;
				// delete, line 86
				sliceDel();
				break;
			case 3:
				// (, line 87
				// atmark, line 87
				if (cursor != ip1) {
					return false;
				}
				// test, line 87
				v_4 = limit - cursor;
				// call shortv, line 87
				if (!rShortv()) {
					return false;
				}
				cursor = limit - v_4;
			// <+, line 87
			{
				int c = cursor;
				insert(cursor, cursor, "e");
				cursor = c;
			}
				break;
			}
			break;
		}
		return true;
	}

	private boolean step1c() {
		int v_1;
		int v_2;
		// (, line 93
		// [, line 94
		ket = cursor;
		// or, line 94
		lab0: do {
			v_1 = limit - cursor;
			lab1: do {
				// literal, line 94
				if (!(eqSB(1, "y"))) {
					break lab1;
				}
				break lab0;
			} while (false);
			cursor = limit - v_1;
			// literal, line 94
			if (!(eqSB(1, "Y"))) {
				return false;
			}
		} while (false);
		// ], line 94
		bra = cursor;
		if (!(outGroupingB(gV, 97, 121))) {
			return false;
		}
		// not, line 95
		{
			v_2 = limit - cursor;
			lab2: do {
				// atlimit, line 95
				if (cursor > limitBw) {
					break lab2;
				}
				return false;
			} while (false);
			cursor = limit - v_2;
		}
		// <-, line 96
		sliceFrom("i");
		return true;
	}

	private boolean step2() {
		int amongVar;
		// (, line 99
		// [, line 100
		ket = cursor;
		// substring, line 100
		amongVar = findAmongB(a5, 24);
		if (amongVar == 0) {
			return false;
		}
		// ], line 100
		bra = cursor;
		// call R1, line 100
		if (!r_R1()) {
			return false;
		}
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 101
			// <-, line 101
			sliceFrom("tion");
			break;
		case 2:
			// (, line 102
			// <-, line 102
			sliceFrom("ence");
			break;
		case 3:
			// (, line 103
			// <-, line 103
			sliceFrom("ance");
			break;
		case 4:
			// (, line 104
			// <-, line 104
			sliceFrom("able");
			break;
		case 5:
			// (, line 105
			// <-, line 105
			sliceFrom("ent");
			break;
		case 6:
			// (, line 107
			// <-, line 107
			sliceFrom("ize");
			break;
		case 7:
			// (, line 109
			// <-, line 109
			sliceFrom("ate");
			break;
		case 8:
			// (, line 111
			// <-, line 111
			sliceFrom("al");
			break;
		case 9:
			// (, line 112
			// <-, line 112
			sliceFrom("ful");
			break;
		case 10:
			// (, line 114
			// <-, line 114
			sliceFrom("ous");
			break;
		case 11:
			// (, line 116
			// <-, line 116
			sliceFrom("ive");
			break;
		case 12:
			// (, line 118
			// <-, line 118
			sliceFrom("ble");
			break;
		case 13:
			// (, line 119
			// literal, line 119
			if (!(eqSB(1, "l"))) {
				return false;
			}
			// <-, line 119
			sliceFrom("og");
			break;
		case 14:
			// (, line 120
			// <-, line 120
			sliceFrom("ful");
			break;
		case 15:
			// (, line 121
			// <-, line 121
			sliceFrom("less");
			break;
		case 16:
			// (, line 122
			if (!(inGroupingB(gValidLI, 99, 116))) {
				return false;
			}
			// delete, line 122
			sliceDel();
			break;
		}
		return true;
	}

	private boolean step3() {
		int amongVar;
		// (, line 126
		// [, line 127
		ket = cursor;
		// substring, line 127
		amongVar = findAmongB(a6, 9);
		if (amongVar == 0) {
			return false;
		}
		// ], line 127
		bra = cursor;
		// call R1, line 127
		if (!r_R1()) {
			return false;
		}
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 128
			// <-, line 128
			sliceFrom("tion");
			break;
		case 2:
			// (, line 129
			// <-, line 129
			sliceFrom("ate");
			break;
		case 3:
			// (, line 130
			// <-, line 130
			sliceFrom("al");
			break;
		case 4:
			// (, line 132
			// <-, line 132
			sliceFrom("ic");
			break;
		case 5:
			// (, line 134
			// delete, line 134
			sliceDel();
			break;
		case 6:
			// (, line 136
			// call R2, line 136
			if (!r_R2()) {
				return false;
			}
			// delete, line 136
			sliceDel();
			break;
		}
		return true;
	}

	private boolean step4() {
		int amongVar;
		int v_1;
		// (, line 140
		// [, line 141
		ket = cursor;
		// substring, line 141
		amongVar = findAmongB(a7, 18);
		if (amongVar == 0) {
			return false;
		}
		// ], line 141
		bra = cursor;
		// call R2, line 141
		if (!r_R2()) {
			return false;
		}
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 144
			// delete, line 144
			sliceDel();
			break;
		case 2:
			// (, line 145
			// or, line 145
			lab0: do {
				v_1 = limit - cursor;
				lab1: do {
					// literal, line 145
					if (!(eqSB(1, "s"))) {
						break lab1;
					}
					break lab0;
				} while (false);
				cursor = limit - v_1;
				// literal, line 145
				if (!(eqSB(1, "t"))) {
					return false;
				}
			} while (false);
			// delete, line 145
			sliceDel();
			break;
		}
		return true;
	}

	private boolean step5() {
		int amongVar;
		int v_1;
		int v_2;
		// (, line 149
		// [, line 150
		ket = cursor;
		// substring, line 150
		amongVar = findAmongB(a8, 2);
		if (amongVar == 0) {
			return false;
		}
		// ], line 150
		bra = cursor;
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 151
			// or, line 151
			lab0: do {
				v_1 = limit - cursor;
				lab1: do {
					// call R2, line 151
					if (!r_R2()) {
						break lab1;
					}
					break lab0;
				} while (false);
				cursor = limit - v_1;
				// (, line 151
				// call R1, line 151
				if (!r_R1()) {
					return false;
				}
				// not, line 151
				{
					v_2 = limit - cursor;
					lab2: do {
						// call shortv, line 151
						if (!rShortv()) {
							break lab2;
						}
						return false;
					} while (false);
					cursor = limit - v_2;
				}
			} while (false);
			// delete, line 151
			sliceDel();
			break;
		case 2:
			// (, line 152
			// call R2, line 152
			if (!r_R2()) {
				return false;
			}
			// literal, line 152
			if (!(eqSB(1, "l"))) {
				return false;
			}
			// delete, line 152
			sliceDel();
			break;
		}
		return true;
	}

	private boolean rException2() {
		// (, line 156
		// [, line 158
		ket = cursor;
		// substring, line 158
		if (findAmongB(a9, 8) == 0) {
			return false;
		}
		// ], line 158
		bra = cursor;
		// atlimit, line 158
		if (cursor > limitBw) {
			return false;
		}
		return true;
	}

	private boolean rException1() {
		int amongVar;
		// (, line 168
		// [, line 170
		bra = cursor;
		// substring, line 170
		amongVar = findAmong(a10, 18);
		if (amongVar == 0) {
			return false;
		}
		// ], line 170
		ket = cursor;
		// atlimit, line 170
		if (cursor < limit) {
			return false;
		}
		switch (amongVar) {
		case 0:
			return false;
		case 1:
			// (, line 174
			// <-, line 174
			sliceFrom("ski");
			break;
		case 2:
			// (, line 175
			// <-, line 175
			sliceFrom("sky");
			break;
		case 3:
			// (, line 176
			// <-, line 176
			sliceFrom("die");
			break;
		case 4:
			// (, line 177
			// <-, line 177
			sliceFrom("lie");
			break;
		case 5:
			// (, line 178
			// <-, line 178
			sliceFrom("tie");
			break;
		case 6:
			// (, line 182
			// <-, line 182
			sliceFrom("idl");
			break;
		case 7:
			// (, line 183
			// <-, line 183
			sliceFrom("gentl");
			break;
		case 8:
			// (, line 184
			// <-, line 184
			sliceFrom("ugli");
			break;
		case 9:
			// (, line 185
			// <-, line 185
			sliceFrom("earli");
			break;
		case 10:
			// (, line 186
			// <-, line 186
			sliceFrom("onli");
			break;
		case 11:
			// (, line 187
			// <-, line 187
			sliceFrom("singl");
			break;
		}
		return true;
	}

	private boolean rPostlude() {
		int v_1;
		int v_2;
		// (, line 203
		// Boolean test YFound, line 203
		if (!(bYFound)) {
			return false;
		}
		// repeat, line 203
		replab0: while (true) {
			v_1 = cursor;
			lab1: do {
				// (, line 203
				// goto, line 203
				golab2: while (true) {
					v_2 = cursor;
					lab3: do {
						// (, line 203
						// [, line 203
						bra = cursor;
						// literal, line 203
						if (!(eqS(1, "Y"))) {
							break lab3;
						}
						// ], line 203
						ket = cursor;
						cursor = v_2;
						break golab2;
					} while (false);
					cursor = v_2;
					if (cursor >= limit) {
						break lab1;
					}
					cursor++;
				}
				// <-, line 203
				sliceFrom("y");
				continue replab0;
			} while (false);
			cursor = v_1;
			break replab0;
		}
		return true;
	}

	private boolean stemImpl() {
		System.out.println(this.toString());
		int v_1;
		int v_2;
		int v_3;
		int v_4;
		int v_5;
		int v_6;
		int v_7;
		int v_8;
		int v_9;
		int v_10;
		int v_11;
		int v_12;
		int v_13;
		// (, line 205
		// or, line 207
		lab0: do {
			v_1 = cursor;
			lab1: do {
				// call exception1, line 207
				if (!rException1()) {
					break lab1;
				}
				break lab0;
			} while (false);
			cursor = v_1;
			lab2: do {
				// not, line 208
				{
					v_2 = cursor;
					lab3: do {
						// hop, line 208
						{
							int c = cursor + 3;
							if (0 > c || c > limit) {
								break lab3;
							}
							cursor = c;
						}
						break lab2;
					} while (false);
					cursor = v_2;
				}
				break lab0;
			} while (false);
			cursor = v_1;
			// (, line 208
			// do, line 209
			v_3 = cursor;
			lab4: do {
				// call prelude, line 209
				if (!rPrelude()) {
					break lab4;
				}
			} while (false);
			cursor = v_3;
			// do, line 210
			v_4 = cursor;
			lab5: do {
				// call markRegions, line 210
				if (!rMarkRegions()) {
					break lab5;
				}
			} while (false);
			cursor = v_4;
			// backwards, line 211
			limitBw = cursor;
			cursor = limit;
			// (, line 211
			// do, line 213
			v_5 = limit - cursor;
			lab6: do {
				// call Step_1a, line 213
				if (!step1a()) {
					break lab6;
				}
			} while (false);
			cursor = limit - v_5;
			// or, line 215
			lab7: do {
				v_6 = limit - cursor;
				lab8: do {
					// call exception2, line 215
					if (!rException2()) {
						break lab8;
					}
					break lab7;
				} while (false);
				cursor = limit - v_6;
				// (, line 215
				// do, line 217
				v_7 = limit - cursor;
				lab9: do {
					// call Step_1b, line 217
					if (!step1b()) {
						break lab9;
					}
				} while (false);
				cursor = limit - v_7;
				// do, line 218
				v_8 = limit - cursor;
				lab10: do {
					// call Step_1c, line 218
					if (!step1c()) {
						break lab10;
					}
				} while (false);
				cursor = limit - v_8;
				// do, line 220
				v_9 = limit - cursor;
				lab11: do {
					// call Step_2, line 220
					if (!step2()) {
						break lab11;
					}
				} while (false);
				cursor = limit - v_9;
				// do, line 221
				v_10 = limit - cursor;
				lab12: do {
					// call Step_3, line 221
					if (!step3()) {
						break lab12;
					}
				} while (false);
				cursor = limit - v_10;
				// do, line 222
				v_11 = limit - cursor;
				lab13: do {
					// call Step_4, line 222
					if (!step4()) {
						break lab13;
					}
				} while (false);
				cursor = limit - v_11;
				// do, line 224
				v_12 = limit - cursor;
				lab14: do {
					// call Step_5, line 224
					if (!step5()) {
						break lab14;
					}
				} while (false);
				cursor = limit - v_12;
			} while (false);
			cursor = limitBw;                        // do, line 227
			v_13 = cursor;
			lab15: do {
				// call postlude, line 227
				if (!rPostlude()) {
					break lab15;
				}
			} while (false);
			cursor = v_13;
		} while (false);

		return true;
	}

	private static Am am(String s, int substringI, int result) {
		return new Am(s, substringI, result);
	}

	static class Am {
		private Am(String s, int substringI, int result) {
			this.sSize = s.length();
			this.s = s.toCharArray();
			this.substringI = substringI;
			this.result = result;
		}

		private final int sSize; /* search string */
		private final char[] s; /* search string */
		private final int substringI; /* index to longest matching substring */
		private final int result; /* result of the lookup */
	}

}
