// Generated from ../ritajs/grammar/RiScript.g4 by ANTLR 4.8
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
		LCOMM=1, BCOMM=2, GT=3, LT=4, LP=5, RP=6, LB=7, RB=8, LCB=9, RCB=10, DOT=11, 
		WS=12, FS=13, EXC=14, AST=15, HAT=16, DOL=17, COM=18, NL=19, DYN=20, SYM=21, 
		OR=22, EQ=23, TF=24, ENT=25, INT=26, OP=27, CHR=28;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LCOMM", "BCOMM", "GT", "LT", "LP", "RP", "LB", "RB", "LCB", "RCB", "DOT", 
			"WS", "FS", "EXC", "AST", "HAT", "DOL", "COM", "NL", "DYN", "SYM", "OR", 
			"EQ", "TF", "ENT", "INT", "OP", "CHR", "IDENT", "NIDENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, "'>'", "'<'", "'('", "')'", "'['", "']'", "'{'", "'}'", 
			"'.'", null, "'/'", "'!'", "'*'", "'^'", "'$'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LCOMM", "BCOMM", "GT", "LT", "LP", "RP", "LB", "RB", "LCB", "RCB", 
			"DOT", "WS", "FS", "EXC", "AST", "HAT", "DOL", "COM", "NL", "DYN", "SYM", 
			"OR", "EQ", "TF", "ENT", "INT", "OP", "CHR"
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
	public String getGrammarFileName() { return "RiScript.g4"; }

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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u00d8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\3\2"+
		"\3\2\3\2\3\2\7\2D\n\2\f\2\16\2G\13\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\7\3R\n\3\f\3\16\3U\13\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b"+
		"\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20"+
		"\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\5\24z\n\24\3\24\3\24\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\3\26\3\27\7\27\u0087\n\27\f\27\16\27\u008a\13"+
		"\27\3\27\3\27\7\27\u008e\n\27\f\27\16\27\u0091\13\27\3\30\7\30\u0094\n"+
		"\30\f\30\16\30\u0097\13\30\3\30\3\30\7\30\u009b\n\30\f\30\16\30\u009e"+
		"\13\30\3\31\3\31\3\31\3\31\5\31\u00a4\n\31\6\31\u00a6\n\31\r\31\16\31"+
		"\u00a7\3\32\3\32\6\32\u00ac\n\32\r\32\16\32\u00ad\3\32\3\32\3\33\7\33"+
		"\u00b3\n\33\f\33\16\33\u00b6\13\33\3\33\6\33\u00b9\n\33\r\33\16\33\u00ba"+
		"\3\33\7\33\u00be\n\33\f\33\16\33\u00c1\13\33\3\34\3\34\3\34\3\35\6\35"+
		"\u00c7\n\35\r\35\16\35\u00c8\3\36\3\36\7\36\u00cd\n\36\f\36\16\36\u00d0"+
		"\13\36\3\37\3\37\7\37\u00d4\n\37\f\37\16\37\u00d7\13\37\3E\2 \3\3\5\4"+
		"\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\2=\2\3\2\13"+
		"\5\2\f\f\17\17\u202a\u202b\4\2\13\13\"\"\6\2%%\62;C\\c|\3\2\62;\b\2##"+
		"&&,,>>@@``\13\2\13\f\"#&&*,\60\61>@]]_`}\177\5\2C\\aac|\7\2//\62;C\\a"+
		"ac|\6\2\62;C\\aac|\2\u00e5\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3"+
		"\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2"+
		"\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67"+
		"\3\2\2\2\29\3\2\2\2\3?\3\2\2\2\5M\3\2\2\2\7X\3\2\2\2\tZ\3\2\2\2\13\\\3"+
		"\2\2\2\r^\3\2\2\2\17`\3\2\2\2\21b\3\2\2\2\23d\3\2\2\2\25f\3\2\2\2\27h"+
		"\3\2\2\2\31j\3\2\2\2\33l\3\2\2\2\35n\3\2\2\2\37p\3\2\2\2!r\3\2\2\2#t\3"+
		"\2\2\2%v\3\2\2\2\'y\3\2\2\2)}\3\2\2\2+\u0082\3\2\2\2-\u0088\3\2\2\2/\u0095"+
		"\3\2\2\2\61\u00a5\3\2\2\2\63\u00a9\3\2\2\2\65\u00b4\3\2\2\2\67\u00c2\3"+
		"\2\2\29\u00c6\3\2\2\2;\u00ca\3\2\2\2=\u00d1\3\2\2\2?@\7\61\2\2@A\7,\2"+
		"\2AE\3\2\2\2BD\13\2\2\2CB\3\2\2\2DG\3\2\2\2EF\3\2\2\2EC\3\2\2\2FH\3\2"+
		"\2\2GE\3\2\2\2HI\7,\2\2IJ\7\61\2\2JK\3\2\2\2KL\b\2\2\2L\4\3\2\2\2MN\7"+
		"\61\2\2NO\7\61\2\2OS\3\2\2\2PR\n\2\2\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2S"+
		"T\3\2\2\2TV\3\2\2\2US\3\2\2\2VW\b\3\2\2W\6\3\2\2\2XY\7@\2\2Y\b\3\2\2\2"+
		"Z[\7>\2\2[\n\3\2\2\2\\]\7*\2\2]\f\3\2\2\2^_\7+\2\2_\16\3\2\2\2`a\7]\2"+
		"\2a\20\3\2\2\2bc\7_\2\2c\22\3\2\2\2de\7}\2\2e\24\3\2\2\2fg\7\177\2\2g"+
		"\26\3\2\2\2hi\7\60\2\2i\30\3\2\2\2jk\t\3\2\2k\32\3\2\2\2lm\7\61\2\2m\34"+
		"\3\2\2\2no\7#\2\2o\36\3\2\2\2pq\7,\2\2q \3\2\2\2rs\7`\2\2s\"\3\2\2\2t"+
		"u\7&\2\2u$\3\2\2\2vw\7.\2\2w&\3\2\2\2xz\7\17\2\2yx\3\2\2\2yz\3\2\2\2z"+
		"{\3\2\2\2{|\7\f\2\2|(\3\2\2\2}~\7&\2\2~\177\7&\2\2\177\u0080\3\2\2\2\u0080"+
		"\u0081\5=\37\2\u0081*\3\2\2\2\u0082\u0083\7&\2\2\u0083\u0084\5=\37\2\u0084"+
		",\3\2\2\2\u0085\u0087\5\31\r\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2"+
		"\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008b\3\2\2\2\u008a\u0088"+
		"\3\2\2\2\u008b\u008f\7~\2\2\u008c\u008e\5\31\r\2\u008d\u008c\3\2\2\2\u008e"+
		"\u0091\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090.\3\2\2\2"+
		"\u0091\u008f\3\2\2\2\u0092\u0094\5\31\r\2\u0093\u0092\3\2\2\2\u0094\u0097"+
		"\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2\2\2\u0096\u0098\3\2\2\2\u0097"+
		"\u0095\3\2\2\2\u0098\u009c\7?\2\2\u0099\u009b\5\31\r\2\u009a\u0099\3\2"+
		"\2\2\u009b\u009e\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d"+
		"\60\3\2\2\2\u009e\u009c\3\2\2\2\u009f\u00a0\7\60\2\2\u00a0\u00a3\5;\36"+
		"\2\u00a1\u00a2\7*\2\2\u00a2\u00a4\7+\2\2\u00a3\u00a1\3\2\2\2\u00a3\u00a4"+
		"\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u009f\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\62\3\2\2\2\u00a9\u00ab\7(\2\2"+
		"\u00aa\u00ac\t\4\2\2\u00ab\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ab"+
		"\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b0\7=\2\2\u00b0"+
		"\64\3\2\2\2\u00b1\u00b3\5\31\r\2\u00b2\u00b1\3\2\2\2\u00b3\u00b6\3\2\2"+
		"\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4"+
		"\3\2\2\2\u00b7\u00b9\t\5\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba"+
		"\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bf\3\2\2\2\u00bc\u00be\5\31"+
		"\r\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00bf"+
		"\u00c0\3\2\2\2\u00c0\66\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2\u00c3\t\6\2"+
		"\2\u00c3\u00c4\7?\2\2\u00c48\3\2\2\2\u00c5\u00c7\n\7\2\2\u00c6\u00c5\3"+
		"\2\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		":\3\2\2\2\u00ca\u00ce\t\b\2\2\u00cb\u00cd\t\t\2\2\u00cc\u00cb\3\2\2\2"+
		"\u00cd\u00d0\3\2\2\2\u00ce\u00cc\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf<\3"+
		"\2\2\2\u00d0\u00ce\3\2\2\2\u00d1\u00d5\t\n\2\2\u00d2\u00d4\t\t\2\2\u00d3"+
		"\u00d2\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d5\u00d6\3\2"+
		"\2\2\u00d6>\3\2\2\2\u00d7\u00d5\3\2\2\2\23\2ESy\u0088\u008f\u0095\u009c"+
		"\u00a3\u00a7\u00ad\u00b4\u00ba\u00bf\u00c8\u00ce\u00d5\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}