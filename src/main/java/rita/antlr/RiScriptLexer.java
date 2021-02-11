// Generated from ../ritajs/grammar/RiScriptLexer.g4 by ANTLR 4.8
package rita.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RiScriptLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LCOMM=1, BCOMM=2, Q=3, GT=4, LT=5, MDS=6, LP=7, RP=8, LB=9, RB=10, LCB=11, 
		RCB=12, DOT=13, WS=14, FS=15, EXC=16, AST=17, HAT=18, DOL=19, COM=20, 
		CONT=21, BS=22, NL=23, DYN=24, SYM=25, OR=26, EQ=27, TF=28, ENT=29, INT=30, 
		OP=31, CHR=32, MDT=33, MDE=34;
	public static final int
		MD=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "MD"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LCOMM", "BCOMM", "Q", "GT", "LT", "MDS", "LP", "RP", "LB", "RB", "LCB", 
			"RCB", "DOT", "WS", "FS", "EXC", "AST", "HAT", "DOL", "COM", "CONT", 
			"BS", "NL", "DYN", "SYM", "OR", "EQ", "TF", "ENT", "INT", "OP", "CHR", 
			"IDENT", "NIDENT", "MDT", "MDE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, "'>'", "'<'", null, "'('", null, "'['", "']'", 
			"'{'", "'}'", "'.'", null, "'/'", "'!'", "'*'", "'^'", "'$'", "','", 
			null, "'\\'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LCOMM", "BCOMM", "Q", "GT", "LT", "MDS", "LP", "RP", "LB", "RB", 
			"LCB", "RCB", "DOT", "WS", "FS", "EXC", "AST", "HAT", "DOL", "COM", "CONT", 
			"BS", "NL", "DYN", "SYM", "OR", "EQ", "TF", "ENT", "INT", "OP", "CHR", 
			"MDT", "MDE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public RiScriptLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RiScriptLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return Q_sempred((RuleContext)_localctx, predIndex);
		case 5:
			return MDS_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean Q_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return _input.LA(-1)=='}';
		}
		return true;
	}
	private boolean MDS_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return _input.LA(-1)==']';
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2$\u00fd\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\3\2\3\2\3\2\3\2\7\2Q\n\2\f\2\16\2T\13"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3_\n\3\f\3\16\3b\13\3\3\3\3\3"+
		"\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3"+
		"\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3"+
		"\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3"+
		"\27\3\30\5\30\u0096\n\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\32\3\32"+
		"\3\32\3\33\7\33\u00a3\n\33\f\33\16\33\u00a6\13\33\3\33\3\33\7\33\u00aa"+
		"\n\33\f\33\16\33\u00ad\13\33\3\34\7\34\u00b0\n\34\f\34\16\34\u00b3\13"+
		"\34\3\34\3\34\7\34\u00b7\n\34\f\34\16\34\u00ba\13\34\3\35\3\35\3\35\3"+
		"\35\5\35\u00c0\n\35\6\35\u00c2\n\35\r\35\16\35\u00c3\3\36\3\36\6\36\u00c8"+
		"\n\36\r\36\16\36\u00c9\3\36\3\36\3\37\7\37\u00cf\n\37\f\37\16\37\u00d2"+
		"\13\37\3\37\6\37\u00d5\n\37\r\37\16\37\u00d6\3\37\7\37\u00da\n\37\f\37"+
		"\16\37\u00dd\13\37\3 \3 \3 \3!\6!\u00e3\n!\r!\16!\u00e4\3\"\3\"\7\"\u00e9"+
		"\n\"\f\"\16\"\u00ec\13\"\3#\3#\7#\u00f0\n#\f#\16#\u00f3\13#\3$\6$\u00f6"+
		"\n$\r$\16$\u00f7\3%\3%\3%\3%\3R\2&\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n"+
		"\24\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\25*\26,\27.\30\60"+
		"\31\62\32\64\33\66\348\35:\36<\37> @!B\"D\2F\2H#J$\4\2\3\f\5\2\f\f\17"+
		"\17\u202a\u202b\4\2\13\13\"\"\6\2%%\62;C\\c|\3\2\62;\b\2##&&,,>>@@``\n"+
		"\2\13\f\"#&&*,\60\61>@]`}\177\5\2C\\aac|\7\2//\62;C\\aac|\6\2\62;C\\a"+
		"ac|\3\2++\2\u010a\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f"+
		"\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26\3\2"+
		"\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2\2\2"+
		"\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2\2.\3"+
		"\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2\2\2"+
		"\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\3H\3\2\2\2\3J"+
		"\3\2\2\2\4L\3\2\2\2\6Z\3\2\2\2\be\3\2\2\2\nh\3\2\2\2\fj\3\2\2\2\16l\3"+
		"\2\2\2\20q\3\2\2\2\22s\3\2\2\2\24u\3\2\2\2\26w\3\2\2\2\30y\3\2\2\2\32"+
		"{\3\2\2\2\34}\3\2\2\2\36\177\3\2\2\2 \u0081\3\2\2\2\"\u0083\3\2\2\2$\u0085"+
		"\3\2\2\2&\u0087\3\2\2\2(\u0089\3\2\2\2*\u008b\3\2\2\2,\u008d\3\2\2\2."+
		"\u0092\3\2\2\2\60\u0095\3\2\2\2\62\u0099\3\2\2\2\64\u009e\3\2\2\2\66\u00a4"+
		"\3\2\2\28\u00b1\3\2\2\2:\u00c1\3\2\2\2<\u00c5\3\2\2\2>\u00d0\3\2\2\2@"+
		"\u00de\3\2\2\2B\u00e2\3\2\2\2D\u00e6\3\2\2\2F\u00ed\3\2\2\2H\u00f5\3\2"+
		"\2\2J\u00f9\3\2\2\2LM\7\61\2\2MN\7,\2\2NR\3\2\2\2OQ\13\2\2\2PO\3\2\2\2"+
		"QT\3\2\2\2RS\3\2\2\2RP\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7,\2\2VW\7\61\2\2"+
		"WX\3\2\2\2XY\b\2\2\2Y\5\3\2\2\2Z[\7\61\2\2[\\\7\61\2\2\\`\3\2\2\2]_\n"+
		"\2\2\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2b`\3\2\2\2cd\b"+
		"\3\2\2d\7\3\2\2\2ef\6\4\2\2fg\7A\2\2g\t\3\2\2\2hi\7@\2\2i\13\3\2\2\2j"+
		"k\7>\2\2k\r\3\2\2\2lm\6\7\3\2mn\7*\2\2no\3\2\2\2op\b\7\3\2p\17\3\2\2\2"+
		"qr\7*\2\2r\21\3\2\2\2st\7+\2\2t\23\3\2\2\2uv\7]\2\2v\25\3\2\2\2wx\7_\2"+
		"\2x\27\3\2\2\2yz\7}\2\2z\31\3\2\2\2{|\7\177\2\2|\33\3\2\2\2}~\7\60\2\2"+
		"~\35\3\2\2\2\177\u0080\t\3\2\2\u0080\37\3\2\2\2\u0081\u0082\7\61\2\2\u0082"+
		"!\3\2\2\2\u0083\u0084\7#\2\2\u0084#\3\2\2\2\u0085\u0086\7,\2\2\u0086%"+
		"\3\2\2\2\u0087\u0088\7`\2\2\u0088\'\3\2\2\2\u0089\u008a\7&\2\2\u008a)"+
		"\3\2\2\2\u008b\u008c\7.\2\2\u008c+\3\2\2\2\u008d\u008e\7^\2\2\u008e\u008f"+
		"\5\60\30\2\u008f\u0090\3\2\2\2\u0090\u0091\b\26\2\2\u0091-\3\2\2\2\u0092"+
		"\u0093\7^\2\2\u0093/\3\2\2\2\u0094\u0096\7\17\2\2\u0095\u0094\3\2\2\2"+
		"\u0095\u0096\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0098\7\f\2\2\u0098\61"+
		"\3\2\2\2\u0099\u009a\7&\2\2\u009a\u009b\7&\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\u009d\5F#\2\u009d\63\3\2\2\2\u009e\u009f\7&\2\2\u009f\u00a0\5F#\2\u00a0"+
		"\65\3\2\2\2\u00a1\u00a3\5\36\17\2\u00a2\u00a1\3\2\2\2\u00a3\u00a6\3\2"+
		"\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a7\u00ab\7~\2\2\u00a8\u00aa\5\36\17\2\u00a9\u00a8\3"+
		"\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac"+
		"\67\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b0\5\36\17\2\u00af\u00ae\3\2"+
		"\2\2\u00b0\u00b3\3\2\2\2\u00b1\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2"+
		"\u00b4\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b4\u00b8\7?\2\2\u00b5\u00b7\5\36"+
		"\17\2\u00b6\u00b5\3\2\2\2\u00b7\u00ba\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b99\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb\u00bc\7\60\2\2"+
		"\u00bc\u00bf\5D\"\2\u00bd\u00be\7*\2\2\u00be\u00c0\7+\2\2\u00bf\u00bd"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bb\3\2\2\2\u00c2"+
		"\u00c3\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4;\3\2\2\2"+
		"\u00c5\u00c7\7(\2\2\u00c6\u00c8\t\4\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00c9"+
		"\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"\u00cc\7=\2\2\u00cc=\3\2\2\2\u00cd\u00cf\5\36\17\2\u00ce\u00cd\3\2\2\2"+
		"\u00cf\u00d2\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d4"+
		"\3\2\2\2\u00d2\u00d0\3\2\2\2\u00d3\u00d5\t\5\2\2\u00d4\u00d3\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00db\3\2"+
		"\2\2\u00d8\u00da\5\36\17\2\u00d9\u00d8\3\2\2\2\u00da\u00dd\3\2\2\2\u00db"+
		"\u00d9\3\2\2\2\u00db\u00dc\3\2\2\2\u00dc?\3\2\2\2\u00dd\u00db\3\2\2\2"+
		"\u00de\u00df\t\6\2\2\u00df\u00e0\7?\2\2\u00e0A\3\2\2\2\u00e1\u00e3\n\7"+
		"\2\2\u00e2\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4"+
		"\u00e5\3\2\2\2\u00e5C\3\2\2\2\u00e6\u00ea\t\b\2\2\u00e7\u00e9\t\t\2\2"+
		"\u00e8\u00e7\3\2\2\2\u00e9\u00ec\3\2\2\2\u00ea\u00e8\3\2\2\2\u00ea\u00eb"+
		"\3\2\2\2\u00ebE\3\2\2\2\u00ec\u00ea\3\2\2\2\u00ed\u00f1\t\n\2\2\u00ee"+
		"\u00f0\t\t\2\2\u00ef\u00ee\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2"+
		"\2\2\u00f1\u00f2\3\2\2\2\u00f2G\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f6"+
		"\n\13\2\2\u00f5\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f5\3\2\2\2"+
		"\u00f7\u00f8\3\2\2\2\u00f8I\3\2\2\2\u00f9\u00fa\7+\2\2\u00fa\u00fb\3\2"+
		"\2\2\u00fb\u00fc\b%\4\2\u00fcK\3\2\2\2\25\2\3R`\u0095\u00a4\u00ab\u00b1"+
		"\u00b8\u00bf\u00c3\u00c9\u00d0\u00d6\u00db\u00e4\u00ea\u00f1\u00f7\5\2"+
		"\3\2\7\3\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}