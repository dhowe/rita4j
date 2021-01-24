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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u00b5\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\21\5\21Y\n\21\3\21\3\21\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\24\7\24d\n\24\f\24\16\24g\13\24\3\24\3\24\7\24k\n\24\f\24"+
		"\16\24n\13\24\3\25\7\25q\n\25\f\25\16\25t\13\25\3\25\3\25\7\25x\n\25\f"+
		"\25\16\25{\13\25\3\26\3\26\3\26\3\26\5\26\u0081\n\26\6\26\u0083\n\26\r"+
		"\26\16\26\u0084\3\27\3\27\6\27\u0089\n\27\r\27\16\27\u008a\3\27\3\27\3"+
		"\30\7\30\u0090\n\30\f\30\16\30\u0093\13\30\3\30\6\30\u0096\n\30\r\30\16"+
		"\30\u0097\3\30\7\30\u009b\n\30\f\30\16\30\u009e\13\30\3\31\3\31\3\31\3"+
		"\32\6\32\u00a4\n\32\r\32\16\32\u00a5\3\33\3\33\7\33\u00aa\n\33\f\33\16"+
		"\33\u00ad\13\33\3\34\3\34\7\34\u00b1\n\34\f\34\16\34\u00b4\13\34\2\2\35"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\2\67\2\3\2\n\4"+
		"\2\13\13\"\"\6\2%%\62;C\\c|\3\2\62;\b\2##&&,,>>@@``\13\2\13\f\"#&&*,\60"+
		"\60>@]]_`}\177\5\2C\\aac|\7\2//\62;C\\aac|\6\2\62;C\\aac|\2\u00c0\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2"+
		"\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31"+
		"\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2"+
		"\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2"+
		"\61\3\2\2\2\2\63\3\2\2\2\39\3\2\2\2\5;\3\2\2\2\7=\3\2\2\2\t?\3\2\2\2\13"+
		"A\3\2\2\2\rC\3\2\2\2\17E\3\2\2\2\21G\3\2\2\2\23I\3\2\2\2\25K\3\2\2\2\27"+
		"M\3\2\2\2\31O\3\2\2\2\33Q\3\2\2\2\35S\3\2\2\2\37U\3\2\2\2!X\3\2\2\2#\\"+
		"\3\2\2\2%_\3\2\2\2\'e\3\2\2\2)r\3\2\2\2+\u0082\3\2\2\2-\u0086\3\2\2\2"+
		"/\u0091\3\2\2\2\61\u009f\3\2\2\2\63\u00a3\3\2\2\2\65\u00a7\3\2\2\2\67"+
		"\u00ae\3\2\2\29:\7@\2\2:\4\3\2\2\2;<\7>\2\2<\6\3\2\2\2=>\7*\2\2>\b\3\2"+
		"\2\2?@\7+\2\2@\n\3\2\2\2AB\7]\2\2B\f\3\2\2\2CD\7_\2\2D\16\3\2\2\2EF\7"+
		"}\2\2F\20\3\2\2\2GH\7\177\2\2H\22\3\2\2\2IJ\7\60\2\2J\24\3\2\2\2KL\t\2"+
		"\2\2L\26\3\2\2\2MN\7#\2\2N\30\3\2\2\2OP\7,\2\2P\32\3\2\2\2QR\7`\2\2R\34"+
		"\3\2\2\2ST\7&\2\2T\36\3\2\2\2UV\7.\2\2V \3\2\2\2WY\7\17\2\2XW\3\2\2\2"+
		"XY\3\2\2\2YZ\3\2\2\2Z[\7\f\2\2[\"\3\2\2\2\\]\7(\2\2]^\5\67\34\2^$\3\2"+
		"\2\2_`\7&\2\2`a\5\67\34\2a&\3\2\2\2bd\5\25\13\2cb\3\2\2\2dg\3\2\2\2ec"+
		"\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hl\7~\2\2ik\5\25\13\2ji\3\2\2\2"+
		"kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m(\3\2\2\2nl\3\2\2\2oq\5\25\13\2po\3\2\2"+
		"\2qt\3\2\2\2rp\3\2\2\2rs\3\2\2\2su\3\2\2\2tr\3\2\2\2uy\7?\2\2vx\5\25\13"+
		"\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z*\3\2\2\2{y\3\2\2\2|}\7\60"+
		"\2\2}\u0080\5\65\33\2~\177\7*\2\2\177\u0081\7+\2\2\u0080~\3\2\2\2\u0080"+
		"\u0081\3\2\2\2\u0081\u0083\3\2\2\2\u0082|\3\2\2\2\u0083\u0084\3\2\2\2"+
		"\u0084\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085,\3\2\2\2\u0086\u0088\7"+
		"(\2\2\u0087\u0089\t\3\2\2\u0088\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008d\7="+
		"\2\2\u008d.\3\2\2\2\u008e\u0090\5\25\13\2\u008f\u008e\3\2\2\2\u0090\u0093"+
		"\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092\u0095\3\2\2\2\u0093"+
		"\u0091\3\2\2\2\u0094\u0096\t\4\2\2\u0095\u0094\3\2\2\2\u0096\u0097\3\2"+
		"\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009c\3\2\2\2\u0099"+
		"\u009b\5\25\13\2\u009a\u0099\3\2\2\2\u009b\u009e\3\2\2\2\u009c\u009a\3"+
		"\2\2\2\u009c\u009d\3\2\2\2\u009d\60\3\2\2\2\u009e\u009c\3\2\2\2\u009f"+
		"\u00a0\t\5\2\2\u00a0\u00a1\7?\2\2\u00a1\62\3\2\2\2\u00a2\u00a4\n\6\2\2"+
		"\u00a3\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6"+
		"\3\2\2\2\u00a6\64\3\2\2\2\u00a7\u00ab\t\7\2\2\u00a8\u00aa\t\b\2\2\u00a9"+
		"\u00a8\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2"+
		"\2\2\u00ac\66\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00b2\t\t\2\2\u00af\u00b1"+
		"\t\b\2\2\u00b0\u00af\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2"+
		"\u00b3\3\2\2\2\u00b38\3\2\2\2\u00b4\u00b2\3\2\2\2\21\2Xelry\u0080\u0084"+
		"\u008a\u0091\u0097\u009c\u00a5\u00ab\u00b2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}