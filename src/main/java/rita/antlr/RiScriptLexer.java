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
		GT=1, LT=2, LP=3, RP=4, LB=5, RB=6, LCB=7, RCB=8, DOT=9, WS=10, EXC=11, 
		AST=12, HAT=13, DOL=14, COM=15, NL=16, DYN=17, SYM=18, OR=19, EQ=20, TF=21, 
		ENT=22, INT=23, OP=24, CHR=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"GT", "LT", "LP", "RP", "LB", "RB", "LCB", "RCB", "DOT", "WS", "EXC", 
			"AST", "HAT", "DOL", "COM", "NL", "DYN", "SYM", "OR", "EQ", "TF", "ENT", 
			"INT", "OP", "CHR", "IDENT", "NIDENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'>'", "'<'", "'('", "')'", "'['", "']'", "'{'", "'}'", "'.'", 
			null, "'!'", "'*'", "'^'", "'$'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "GT", "LT", "LP", "RP", "LB", "RB", "LCB", "RCB", "DOT", "WS", 
			"EXC", "AST", "HAT", "DOL", "COM", "NL", "DYN", "SYM", "OR", "EQ", "TF", 
			"ENT", "INT", "OP", "CHR"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u00b7\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\5\21Y\n\21\3\21\3\21\3\22\3\22\3\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\24\7\24f\n\24\f\24\16\24i\13\24\3\24\3\24\7\24"+
		"m\n\24\f\24\16\24p\13\24\3\25\7\25s\n\25\f\25\16\25v\13\25\3\25\3\25\7"+
		"\25z\n\25\f\25\16\25}\13\25\3\26\3\26\3\26\3\26\5\26\u0083\n\26\6\26\u0085"+
		"\n\26\r\26\16\26\u0086\3\27\3\27\6\27\u008b\n\27\r\27\16\27\u008c\3\27"+
		"\3\27\3\30\7\30\u0092\n\30\f\30\16\30\u0095\13\30\3\30\6\30\u0098\n\30"+
		"\r\30\16\30\u0099\3\30\7\30\u009d\n\30\f\30\16\30\u00a0\13\30\3\31\3\31"+
		"\3\31\3\32\6\32\u00a6\n\32\r\32\16\32\u00a7\3\33\3\33\7\33\u00ac\n\33"+
		"\f\33\16\33\u00af\13\33\3\34\3\34\7\34\u00b3\n\34\f\34\16\34\u00b6\13"+
		"\34\2\2\35\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33"+
		"\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\2\67\2"+
		"\3\2\n\4\2\13\13\"\"\6\2%%\62;C\\c|\3\2\62;\b\2##&&,,>>@@``\13\2\13\f"+
		"\"#&&*,\60\60>@]]_`}\177\5\2C\\aac|\7\2//\62;C\\aac|\6\2\62;C\\aac|\2"+
		"\u00c2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2"+
		"\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2"+
		"/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\39\3\2\2\2\5;\3\2\2\2\7=\3\2\2\2\t"+
		"?\3\2\2\2\13A\3\2\2\2\rC\3\2\2\2\17E\3\2\2\2\21G\3\2\2\2\23I\3\2\2\2\25"+
		"K\3\2\2\2\27M\3\2\2\2\31O\3\2\2\2\33Q\3\2\2\2\35S\3\2\2\2\37U\3\2\2\2"+
		"!X\3\2\2\2#\\\3\2\2\2%a\3\2\2\2\'g\3\2\2\2)t\3\2\2\2+\u0084\3\2\2\2-\u0088"+
		"\3\2\2\2/\u0093\3\2\2\2\61\u00a1\3\2\2\2\63\u00a5\3\2\2\2\65\u00a9\3\2"+
		"\2\2\67\u00b0\3\2\2\29:\7@\2\2:\4\3\2\2\2;<\7>\2\2<\6\3\2\2\2=>\7*\2\2"+
		">\b\3\2\2\2?@\7+\2\2@\n\3\2\2\2AB\7]\2\2B\f\3\2\2\2CD\7_\2\2D\16\3\2\2"+
		"\2EF\7}\2\2F\20\3\2\2\2GH\7\177\2\2H\22\3\2\2\2IJ\7\60\2\2J\24\3\2\2\2"+
		"KL\t\2\2\2L\26\3\2\2\2MN\7#\2\2N\30\3\2\2\2OP\7,\2\2P\32\3\2\2\2QR\7`"+
		"\2\2R\34\3\2\2\2ST\7&\2\2T\36\3\2\2\2UV\7.\2\2V \3\2\2\2WY\7\17\2\2XW"+
		"\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z[\7\f\2\2[\"\3\2\2\2\\]\7&\2\2]^\7&\2\2^"+
		"_\3\2\2\2_`\5\67\34\2`$\3\2\2\2ab\7&\2\2bc\5\67\34\2c&\3\2\2\2df\5\25"+
		"\13\2ed\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2hj\3\2\2\2ig\3\2\2\2jn\7"+
		"~\2\2km\5\25\13\2lk\3\2\2\2mp\3\2\2\2nl\3\2\2\2no\3\2\2\2o(\3\2\2\2pn"+
		"\3\2\2\2qs\5\25\13\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2"+
		"vt\3\2\2\2w{\7?\2\2xz\5\25\13\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2"+
		"\2|*\3\2\2\2}{\3\2\2\2~\177\7\60\2\2\177\u0082\5\65\33\2\u0080\u0081\7"+
		"*\2\2\u0081\u0083\7+\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083"+
		"\u0085\3\2\2\2\u0084~\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0084\3\2\2\2"+
		"\u0086\u0087\3\2\2\2\u0087,\3\2\2\2\u0088\u008a\7(\2\2\u0089\u008b\t\3"+
		"\2\2\u008a\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\7=\2\2\u008f.\3\2\2\2\u0090"+
		"\u0092\5\25\13\2\u0091\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3"+
		"\2\2\2\u0093\u0094\3\2\2\2\u0094\u0097\3\2\2\2\u0095\u0093\3\2\2\2\u0096"+
		"\u0098\t\4\2\2\u0097\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u0099\u009a\3\2\2\2\u009a\u009e\3\2\2\2\u009b\u009d\5\25\13\2\u009c"+
		"\u009b\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2"+
		"\2\2\u009f\60\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a2\t\5\2\2\u00a2\u00a3"+
		"\7?\2\2\u00a3\62\3\2\2\2\u00a4\u00a6\n\6\2\2\u00a5\u00a4\3\2\2\2\u00a6"+
		"\u00a7\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\64\3\2\2"+
		"\2\u00a9\u00ad\t\7\2\2\u00aa\u00ac\t\b\2\2\u00ab\u00aa\3\2\2\2\u00ac\u00af"+
		"\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ad\u00ae\3\2\2\2\u00ae\66\3\2\2\2\u00af"+
		"\u00ad\3\2\2\2\u00b0\u00b4\t\t\2\2\u00b1\u00b3\t\b\2\2\u00b2\u00b1\3\2"+
		"\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b4\u00b5\3\2\2\2\u00b5"+
		"8\3\2\2\2\u00b6\u00b4\3\2\2\2\21\2Xgnt{\u0082\u0086\u008c\u0093\u0099"+
		"\u009e\u00a7\u00ad\u00b4\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}