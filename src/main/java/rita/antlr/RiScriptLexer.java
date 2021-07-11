// Generated from ./src/main/java/rita/antlr/RiScriptLexer.g4 by ANTLR 4.8
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
		LCOMM=1, BCOMM=2, LCBQ=3, MDLS=4, LP=5, RP=6, LB=7, RB=8, LCB=9, RCB=10, 
		FS=11, AST=12, DOL=13, COM=14, GT=15, LT=16, DOT=17, WS=18, ESC=19, NL=20, 
		DIDENT=21, DYN=22, SYM=23, OR=24, EQ=25, ENT=26, INT=27, OP=28, CHR=29, 
		IDENT=30, CONT=31, MDLT=32, MDLE=33;
	public static final int
		MDL=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "MDL"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LCOMM", "BCOMM", "LCBQ", "MDLS", "LP", "RP", "LB", "RB", "LCB", "RCB", 
			"FS", "AST", "DOL", "COM", "GT", "LT", "DOT", "WS", "ESC", "NL", "DIDENT", 
			"DYN", "SYM", "OR", "EQ", "ENT", "INT", "OP", "CHR", "IDENT", "CONT", 
			"NIDENT", "MDLT", "MDLE"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'('", null, "'['", "']'", "'{'", "'}'", 
			"'/'", "'*'", "'$'", "','", "'>'", "'<'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LCOMM", "BCOMM", "LCBQ", "MDLS", "LP", "RP", "LB", "RB", "LCB", 
			"RCB", "FS", "AST", "DOL", "COM", "GT", "LT", "DOT", "WS", "ESC", "NL", 
			"DIDENT", "DYN", "SYM", "OR", "EQ", "ENT", "INT", "OP", "CHR", "IDENT", 
			"CONT", "MDLT", "MDLE"
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2#\u00ef\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\3\2\3\2\3\2\3\2\7\2M\n\2\f\2\16\2P\13\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3[\n\3\f\3\16\3^\13\3\3\3\3\3\3\4\3\4\3"+
		"\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3"+
		"\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\23\3\23\3\24\3\24\3\24\3\25\5\25\u008a\n\25\3\25\3\25\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\31\7\31\u009a\n\31\f\31\16"+
		"\31\u009d\13\31\3\31\3\31\7\31\u00a1\n\31\f\31\16\31\u00a4\13\31\3\32"+
		"\7\32\u00a7\n\32\f\32\16\32\u00aa\13\32\3\32\3\32\7\32\u00ae\n\32\f\32"+
		"\16\32\u00b1\13\32\3\33\3\33\6\33\u00b5\n\33\r\33\16\33\u00b6\3\33\3\33"+
		"\3\34\7\34\u00bc\n\34\f\34\16\34\u00bf\13\34\3\34\6\34\u00c2\n\34\r\34"+
		"\16\34\u00c3\3\34\7\34\u00c7\n\34\f\34\16\34\u00ca\13\34\3\35\3\35\3\35"+
		"\3\36\6\36\u00d0\n\36\r\36\16\36\u00d1\3\37\3\37\7\37\u00d6\n\37\f\37"+
		"\16\37\u00d9\13\37\3 \3 \3 \3 \3 \3!\3!\7!\u00e2\n!\f!\16!\u00e5\13!\3"+
		"\"\6\"\u00e8\n\"\r\"\16\"\u00e9\3#\3#\3#\3#\3N\2$\4\3\6\4\b\5\n\6\f\7"+
		"\16\b\20\t\22\n\24\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\25"+
		"*\26,\27.\30\60\31\62\32\64\33\66\348\35:\36<\37> @!B\2D\"F#\4\2\3\r\5"+
		"\2\f\f\17\17\u202a\u202b\4\2\13\13\"\"\3\2*+\6\2%%\62;C\\c|\3\2\62;\b"+
		"\2##&&,,>>@@``\n\2\13\f\"\"&&*,\60\61>@]_}\177\5\2C\\aac|\7\2//\62;C\\"+
		"aac|\6\2\62;C\\aac|\3\2++\2\u00fb\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2"+
		"\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3"+
		"\2\2\2\2\26\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2"+
		"\2\2 \3\2\2\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2"+
		"\2,\3\2\2\2\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2"+
		"\2\2\28\3\2\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\3D\3\2\2\2"+
		"\3F\3\2\2\2\4H\3\2\2\2\6V\3\2\2\2\ba\3\2\2\2\nd\3\2\2\2\fi\3\2\2\2\16"+
		"k\3\2\2\2\20m\3\2\2\2\22o\3\2\2\2\24q\3\2\2\2\26s\3\2\2\2\30u\3\2\2\2"+
		"\32w\3\2\2\2\34y\3\2\2\2\36{\3\2\2\2 }\3\2\2\2\"\177\3\2\2\2$\u0081\3"+
		"\2\2\2&\u0083\3\2\2\2(\u0085\3\2\2\2*\u0089\3\2\2\2,\u008d\3\2\2\2.\u0090"+
		"\3\2\2\2\60\u0095\3\2\2\2\62\u009b\3\2\2\2\64\u00a8\3\2\2\2\66\u00b2\3"+
		"\2\2\28\u00bd\3\2\2\2:\u00cb\3\2\2\2<\u00cf\3\2\2\2>\u00d3\3\2\2\2@\u00da"+
		"\3\2\2\2B\u00df\3\2\2\2D\u00e7\3\2\2\2F\u00eb\3\2\2\2HI\7\61\2\2IJ\7,"+
		"\2\2JN\3\2\2\2KM\13\2\2\2LK\3\2\2\2MP\3\2\2\2NO\3\2\2\2NL\3\2\2\2OQ\3"+
		"\2\2\2PN\3\2\2\2QR\7,\2\2RS\7\61\2\2ST\3\2\2\2TU\b\2\2\2U\5\3\2\2\2VW"+
		"\7\61\2\2WX\7\61\2\2X\\\3\2\2\2Y[\n\2\2\2ZY\3\2\2\2[^\3\2\2\2\\Z\3\2\2"+
		"\2\\]\3\2\2\2]_\3\2\2\2^\\\3\2\2\2_`\b\3\2\2`\7\3\2\2\2ab\5\26\13\2bc"+
		"\7A\2\2c\t\3\2\2\2de\5\22\t\2ef\5\f\6\2fg\3\2\2\2gh\b\5\3\2h\13\3\2\2"+
		"\2ij\7*\2\2j\r\3\2\2\2kl\7+\2\2l\17\3\2\2\2mn\7]\2\2n\21\3\2\2\2op\7_"+
		"\2\2p\23\3\2\2\2qr\7}\2\2r\25\3\2\2\2st\7\177\2\2t\27\3\2\2\2uv\7\61\2"+
		"\2v\31\3\2\2\2wx\7,\2\2x\33\3\2\2\2yz\7&\2\2z\35\3\2\2\2{|\7.\2\2|\37"+
		"\3\2\2\2}~\7@\2\2~!\3\2\2\2\177\u0080\7>\2\2\u0080#\3\2\2\2\u0081\u0082"+
		"\7\60\2\2\u0082%\3\2\2\2\u0083\u0084\t\3\2\2\u0084\'\3\2\2\2\u0085\u0086"+
		"\7^\2\2\u0086\u0087\t\4\2\2\u0087)\3\2\2\2\u0088\u008a\7\17\2\2\u0089"+
		"\u0088\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008c\7\f"+
		"\2\2\u008c+\3\2\2\2\u008d\u008e\7\60\2\2\u008e\u008f\5>\37\2\u008f-\3"+
		"\2\2\2\u0090\u0091\7&\2\2\u0091\u0092\7&\2\2\u0092\u0093\3\2\2\2\u0093"+
		"\u0094\5B!\2\u0094/\3\2\2\2\u0095\u0096\7&\2\2\u0096\u0097\5B!\2\u0097"+
		"\61\3\2\2\2\u0098\u009a\5&\23\2\u0099\u0098\3\2\2\2\u009a\u009d\3\2\2"+
		"\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009e\3\2\2\2\u009d\u009b"+
		"\3\2\2\2\u009e\u00a2\7~\2\2\u009f\u00a1\5&\23\2\u00a0\u009f\3\2\2\2\u00a1"+
		"\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\63\3\2\2"+
		"\2\u00a4\u00a2\3\2\2\2\u00a5\u00a7\5&\23\2\u00a6\u00a5\3\2\2\2\u00a7\u00aa"+
		"\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\u00ab\3\2\2\2\u00aa"+
		"\u00a8\3\2\2\2\u00ab\u00af\7?\2\2\u00ac\u00ae\5&\23\2\u00ad\u00ac\3\2"+
		"\2\2\u00ae\u00b1\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0"+
		"\65\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b4\7(\2\2\u00b3\u00b5\t\5\2\2"+
		"\u00b4\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7"+
		"\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\7=\2\2\u00b9\67\3\2\2\2\u00ba"+
		"\u00bc\5&\23\2\u00bb\u00ba\3\2\2\2\u00bc\u00bf\3\2\2\2\u00bd\u00bb\3\2"+
		"\2\2\u00bd\u00be\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2\2\2\u00c0"+
		"\u00c2\t\6\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c1\3\2"+
		"\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c8\3\2\2\2\u00c5\u00c7\5&\23\2\u00c6"+
		"\u00c5\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2"+
		"\2\2\u00c99\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cc\t\7\2\2\u00cc\u00cd"+
		"\7?\2\2\u00cd;\3\2\2\2\u00ce\u00d0\n\b\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00d1"+
		"\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2=\3\2\2\2\u00d3"+
		"\u00d7\t\t\2\2\u00d4\u00d6\t\n\2\2\u00d5\u00d4\3\2\2\2\u00d6\u00d9\3\2"+
		"\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8?\3\2\2\2\u00d9\u00d7"+
		"\3\2\2\2\u00da\u00db\7^\2\2\u00db\u00dc\5*\25\2\u00dc\u00dd\3\2\2\2\u00dd"+
		"\u00de\b \2\2\u00deA\3\2\2\2\u00df\u00e3\t\13\2\2\u00e0\u00e2\t\n\2\2"+
		"\u00e1\u00e0\3\2\2\2\u00e2\u00e5\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3\u00e4"+
		"\3\2\2\2\u00e4C\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e6\u00e8\n\f\2\2\u00e7"+
		"\u00e6\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2"+
		"\2\2\u00eaE\3\2\2\2\u00eb\u00ec\7+\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee"+
		"\b#\4\2\u00eeG\3\2\2\2\23\2\3N\\\u0089\u009b\u00a2\u00a8\u00af\u00b6\u00bd"+
		"\u00c3\u00c8\u00d1\u00d7\u00e3\u00e9\5\2\3\2\7\3\2\6\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}