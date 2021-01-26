// Generated from ../ritajs/grammar/RiScript.g4 by ANTLR 4.8
package rita.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RiScriptParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LCOMM=1, BCOMM=2, GT=3, LT=4, LP=5, RP=6, LB=7, RB=8, LCB=9, RCB=10, DOT=11, 
		WS=12, FS=13, EXC=14, AST=15, HAT=16, DOL=17, COM=18, NL=19, DYN=20, SYM=21, 
		OR=22, EQ=23, TF=24, ENT=25, INT=26, OP=27, CHR=28;
	public static final int
		RULE_script = 0, RULE_expr = 1, RULE_cexpr = 2, RULE_cond = 3, RULE_weight = 4, 
		RULE_choice = 5, RULE_assign = 6, RULE_chars = 7, RULE_dynamic = 8, RULE_symbol = 9, 
		RULE_wexpr = 10, RULE_transform = 11, RULE_op = 12;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "expr", "cexpr", "cond", "weight", "choice", "assign", "chars", 
			"dynamic", "symbol", "wexpr", "transform", "op"
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

	@Override
	public String getGrammarFileName() { return "RiScript.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public RiScriptParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ScriptContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(RiScriptParser.EOF, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<CexprContext> cexpr() {
			return getRuleContexts(CexprContext.class);
		}
		public CexprContext cexpr(int i) {
			return getRuleContext(CexprContext.class,i);
		}
		public List<TerminalNode> NL() { return getTokens(RiScriptParser.NL); }
		public TerminalNode NL(int i) {
			return getToken(RiScriptParser.NL, i);
		}
		public ScriptContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_script; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterScript(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitScript(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitScript(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ScriptContext script() throws RecognitionException {
		ScriptContext _localctx = new ScriptContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_script);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << LP) | (1L << LCB) | (1L << DOT) | (1L << WS) | (1L << FS) | (1L << EXC) | (1L << AST) | (1L << HAT) | (1L << DOL) | (1L << COM) | (1L << NL) | (1L << DYN) | (1L << SYM) | (1L << TF) | (1L << ENT) | (1L << INT) | (1L << CHR))) != 0)) {
				{
				setState(29);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(26);
					expr();
					}
					break;
				case 2:
					{
					setState(27);
					cexpr();
					}
					break;
				case 3:
					{
					setState(28);
					match(NL);
					}
					break;
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public List<SymbolContext> symbol() {
			return getRuleContexts(SymbolContext.class);
		}
		public SymbolContext symbol(int i) {
			return getRuleContext(SymbolContext.class,i);
		}
		public List<ChoiceContext> choice() {
			return getRuleContexts(ChoiceContext.class);
		}
		public ChoiceContext choice(int i) {
			return getRuleContext(ChoiceContext.class,i);
		}
		public List<AssignContext> assign() {
			return getRuleContexts(AssignContext.class);
		}
		public AssignContext assign(int i) {
			return getRuleContext(AssignContext.class,i);
		}
		public List<CharsContext> chars() {
			return getRuleContexts(CharsContext.class);
		}
		public CharsContext chars(int i) {
			return getRuleContext(CharsContext.class,i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(40); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(40);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						setState(36);
						symbol();
						}
						break;
					case 2:
						{
						setState(37);
						choice();
						}
						break;
					case 3:
						{
						setState(38);
						assign();
						}
						break;
					case 4:
						{
						setState(39);
						chars();
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(42); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CexprContext extends ParserRuleContext {
		public TerminalNode LCB() { return getToken(RiScriptParser.LCB, 0); }
		public TerminalNode RCB() { return getToken(RiScriptParser.RCB, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(RiScriptParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(RiScriptParser.WS, i);
		}
		public List<CondContext> cond() {
			return getRuleContexts(CondContext.class);
		}
		public CondContext cond(int i) {
			return getRuleContext(CondContext.class,i);
		}
		public CexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cexpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterCexpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitCexpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitCexpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CexprContext cexpr() throws RecognitionException {
		CexprContext _localctx = new CexprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_cexpr);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(44);
				match(WS);
				}
				}
				setState(49);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(50);
			match(LCB);
			setState(52); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(51);
				cond();
				}
				}
				setState(54); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SYM || _la==TF );
			setState(56);
			match(RCB);
			setState(60);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(57);
					match(WS);
					}
					} 
				}
				setState(62);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(63);
			expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CondContext extends ParserRuleContext {
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public OpContext op() {
			return getRuleContext(OpContext.class,0);
		}
		public CharsContext chars() {
			return getRuleContext(CharsContext.class,0);
		}
		public List<TerminalNode> WS() { return getTokens(RiScriptParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(RiScriptParser.WS, i);
		}
		public TerminalNode COM() { return getToken(RiScriptParser.COM, 0); }
		public CondContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterCond(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitCond(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitCond(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CondContext cond() throws RecognitionException {
		CondContext _localctx = new CondContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_cond);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			symbol();
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(66);
				match(WS);
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(72);
			op();
			setState(76);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(73);
					match(WS);
					}
					} 
				}
				setState(78);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			setState(79);
			chars();
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(80);
				match(WS);
				}
				}
				setState(85);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COM) {
				{
				setState(86);
				match(COM);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WeightContext extends ParserRuleContext {
		public TerminalNode LB() { return getToken(RiScriptParser.LB, 0); }
		public TerminalNode INT() { return getToken(RiScriptParser.INT, 0); }
		public TerminalNode RB() { return getToken(RiScriptParser.RB, 0); }
		public List<TerminalNode> WS() { return getTokens(RiScriptParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(RiScriptParser.WS, i);
		}
		public WeightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_weight; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterWeight(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitWeight(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitWeight(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WeightContext weight() throws RecognitionException {
		WeightContext _localctx = new WeightContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_weight);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(89);
				match(WS);
				}
				}
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95);
			match(LB);
			setState(96);
			match(INT);
			setState(97);
			match(RB);
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(98);
				match(WS);
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ChoiceContext extends ParserRuleContext {
		public TerminalNode LP() { return getToken(RiScriptParser.LP, 0); }
		public List<WexprContext> wexpr() {
			return getRuleContexts(WexprContext.class);
		}
		public WexprContext wexpr(int i) {
			return getRuleContext(WexprContext.class,i);
		}
		public TerminalNode RP() { return getToken(RiScriptParser.RP, 0); }
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(RiScriptParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(RiScriptParser.OR, i);
		}
		public ChoiceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_choice; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterChoice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitChoice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitChoice(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ChoiceContext choice() throws RecognitionException {
		ChoiceContext _localctx = new ChoiceContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_choice);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(104);
			match(LP);
			setState(110);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(105);
					wexpr();
					setState(106);
					match(OR);
					}
					} 
				}
				setState(112);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(113);
			wexpr();
			setState(114);
			match(RP);
			}
			setState(119);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(116);
					transform();
					}
					} 
				}
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(RiScriptParser.EQ, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DynamicContext dynamic() {
			return getRuleContext(DynamicContext.class,0);
		}
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public AssignContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterAssign(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitAssign(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitAssign(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignContext assign() throws RecognitionException {
		AssignContext _localctx = new AssignContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assign);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DYN:
				{
				setState(122);
				dynamic();
				}
				break;
			case SYM:
			case TF:
				{
				setState(123);
				symbol();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(126);
			match(EQ);
			setState(127);
			expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CharsContext extends ParserRuleContext {
		public List<TerminalNode> CHR() { return getTokens(RiScriptParser.CHR); }
		public TerminalNode CHR(int i) {
			return getToken(RiScriptParser.CHR, i);
		}
		public List<TerminalNode> ENT() { return getTokens(RiScriptParser.ENT); }
		public TerminalNode ENT(int i) {
			return getToken(RiScriptParser.ENT, i);
		}
		public List<TerminalNode> INT() { return getTokens(RiScriptParser.INT); }
		public TerminalNode INT(int i) {
			return getToken(RiScriptParser.INT, i);
		}
		public List<TerminalNode> DOT() { return getTokens(RiScriptParser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(RiScriptParser.DOT, i);
		}
		public List<TerminalNode> WS() { return getTokens(RiScriptParser.WS); }
		public TerminalNode WS(int i) {
			return getToken(RiScriptParser.WS, i);
		}
		public List<TerminalNode> EXC() { return getTokens(RiScriptParser.EXC); }
		public TerminalNode EXC(int i) {
			return getToken(RiScriptParser.EXC, i);
		}
		public List<TerminalNode> AST() { return getTokens(RiScriptParser.AST); }
		public TerminalNode AST(int i) {
			return getToken(RiScriptParser.AST, i);
		}
		public List<TerminalNode> GT() { return getTokens(RiScriptParser.GT); }
		public TerminalNode GT(int i) {
			return getToken(RiScriptParser.GT, i);
		}
		public List<TerminalNode> LT() { return getTokens(RiScriptParser.LT); }
		public TerminalNode LT(int i) {
			return getToken(RiScriptParser.LT, i);
		}
		public List<TerminalNode> DOL() { return getTokens(RiScriptParser.DOL); }
		public TerminalNode DOL(int i) {
			return getToken(RiScriptParser.DOL, i);
		}
		public List<TerminalNode> HAT() { return getTokens(RiScriptParser.HAT); }
		public TerminalNode HAT(int i) {
			return getToken(RiScriptParser.HAT, i);
		}
		public List<TerminalNode> COM() { return getTokens(RiScriptParser.COM); }
		public TerminalNode COM(int i) {
			return getToken(RiScriptParser.COM, i);
		}
		public List<TerminalNode> FS() { return getTokens(RiScriptParser.FS); }
		public TerminalNode FS(int i) {
			return getToken(RiScriptParser.FS, i);
		}
		public CharsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_chars; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterChars(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitChars(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitChars(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CharsContext chars() throws RecognitionException {
		CharsContext _localctx = new CharsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_chars);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(133); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(133);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case GT:
					case LT:
					case DOT:
					case WS:
					case FS:
					case EXC:
					case AST:
					case HAT:
					case DOL:
					case COM:
						{
						setState(129);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << DOT) | (1L << WS) | (1L << FS) | (1L << EXC) | (1L << AST) | (1L << HAT) | (1L << DOL) | (1L << COM))) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					case CHR:
						{
						setState(130);
						match(CHR);
						}
						break;
					case ENT:
						{
						setState(131);
						match(ENT);
						}
						break;
					case INT:
						{
						setState(132);
						match(INT);
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(135); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DynamicContext extends ParserRuleContext {
		public TerminalNode DYN() { return getToken(RiScriptParser.DYN, 0); }
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public DynamicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dynamic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterDynamic(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitDynamic(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitDynamic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DynamicContext dynamic() throws RecognitionException {
		DynamicContext _localctx = new DynamicContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_dynamic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			match(DYN);
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TF) {
				{
				{
				setState(138);
				transform();
				}
				}
				setState(143);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SymbolContext extends ParserRuleContext {
		public TerminalNode SYM() { return getToken(RiScriptParser.SYM, 0); }
		public List<TransformContext> transform() {
			return getRuleContexts(TransformContext.class);
		}
		public TransformContext transform(int i) {
			return getRuleContext(TransformContext.class,i);
		}
		public SymbolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_symbol; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterSymbol(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitSymbol(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitSymbol(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SymbolContext symbol() throws RecognitionException {
		SymbolContext _localctx = new SymbolContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_symbol);
		try {
			int _alt;
			setState(156);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SYM:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				match(SYM);
				setState(148);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(145);
						transform();
						}
						} 
					}
					setState(150);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				}
				}
				break;
			case TF:
				enterOuterAlt(_localctx, 2);
				{
				setState(152); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(151);
						transform();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(154); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WexprContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public WeightContext weight() {
			return getRuleContext(WeightContext.class,0);
		}
		public WexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wexpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterWexpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitWexpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitWexpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WexprContext wexpr() throws RecognitionException {
		WexprContext _localctx = new WexprContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_wexpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(158);
				expr();
				}
				break;
			}
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LB || _la==WS) {
				{
				setState(161);
				weight();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TransformContext extends ParserRuleContext {
		public TerminalNode TF() { return getToken(RiScriptParser.TF, 0); }
		public TransformContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_transform; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterTransform(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitTransform(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitTransform(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TransformContext transform() throws RecognitionException {
		TransformContext _localctx = new TransformContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_transform);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(TF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OpContext extends ParserRuleContext {
		public TerminalNode OP() { return getToken(RiScriptParser.OP, 0); }
		public TerminalNode LT() { return getToken(RiScriptParser.LT, 0); }
		public TerminalNode GT() { return getToken(RiScriptParser.GT, 0); }
		public TerminalNode EQ() { return getToken(RiScriptParser.EQ, 0); }
		public OpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).enterOp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof RiScriptListener ) ((RiScriptListener)listener).exitOp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof RiScriptVisitor ) return ((RiScriptVisitor<? extends T>)visitor).visitOp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OpContext op() throws RecognitionException {
		OpContext _localctx = new OpContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_op);
		int _la;
		try {
			setState(168);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OP:
				enterOuterAlt(_localctx, 1);
				{
				setState(166);
				match(OP);
				}
				break;
			case GT:
			case LT:
			case EQ:
				enterOuterAlt(_localctx, 2);
				{
				setState(167);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << EQ))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36\u00ad\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\7\2 \n\2\f\2\16\2#\13\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\6\3+\n\3\r\3\16\3,\3\4\7\4\60\n\4\f\4\16\4\63\13"+
		"\4\3\4\3\4\6\4\67\n\4\r\4\16\48\3\4\3\4\7\4=\n\4\f\4\16\4@\13\4\3\4\3"+
		"\4\3\5\3\5\7\5F\n\5\f\5\16\5I\13\5\3\5\3\5\7\5M\n\5\f\5\16\5P\13\5\3\5"+
		"\3\5\7\5T\n\5\f\5\16\5W\13\5\3\5\5\5Z\n\5\3\6\7\6]\n\6\f\6\16\6`\13\6"+
		"\3\6\3\6\3\6\3\6\7\6f\n\6\f\6\16\6i\13\6\3\7\3\7\3\7\3\7\7\7o\n\7\f\7"+
		"\16\7r\13\7\3\7\3\7\3\7\3\7\7\7x\n\7\f\7\16\7{\13\7\3\b\3\b\5\b\177\n"+
		"\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\6\t\u0088\n\t\r\t\16\t\u0089\3\n\3\n\7"+
		"\n\u008e\n\n\f\n\16\n\u0091\13\n\3\13\3\13\7\13\u0095\n\13\f\13\16\13"+
		"\u0098\13\13\3\13\6\13\u009b\n\13\r\13\16\13\u009c\5\13\u009f\n\13\3\f"+
		"\5\f\u00a2\n\f\3\f\5\f\u00a5\n\f\3\r\3\r\3\16\3\16\5\16\u00ab\n\16\3\16"+
		"\2\2\17\2\4\6\b\n\f\16\20\22\24\26\30\32\2\4\4\2\5\6\r\24\4\2\5\6\31\31"+
		"\2\u00bd\2!\3\2\2\2\4*\3\2\2\2\6\61\3\2\2\2\bC\3\2\2\2\n^\3\2\2\2\fj\3"+
		"\2\2\2\16~\3\2\2\2\20\u0087\3\2\2\2\22\u008b\3\2\2\2\24\u009e\3\2\2\2"+
		"\26\u00a1\3\2\2\2\30\u00a6\3\2\2\2\32\u00aa\3\2\2\2\34 \5\4\3\2\35 \5"+
		"\6\4\2\36 \7\25\2\2\37\34\3\2\2\2\37\35\3\2\2\2\37\36\3\2\2\2 #\3\2\2"+
		"\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2\2#!\3\2\2\2$%\7\2\2\3%\3\3\2\2\2&+"+
		"\5\24\13\2\'+\5\f\7\2(+\5\16\b\2)+\5\20\t\2*&\3\2\2\2*\'\3\2\2\2*(\3\2"+
		"\2\2*)\3\2\2\2+,\3\2\2\2,*\3\2\2\2,-\3\2\2\2-\5\3\2\2\2.\60\7\16\2\2/"+
		".\3\2\2\2\60\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2\62\64\3\2\2\2\63\61"+
		"\3\2\2\2\64\66\7\13\2\2\65\67\5\b\5\2\66\65\3\2\2\2\678\3\2\2\28\66\3"+
		"\2\2\289\3\2\2\29:\3\2\2\2:>\7\f\2\2;=\7\16\2\2<;\3\2\2\2=@\3\2\2\2><"+
		"\3\2\2\2>?\3\2\2\2?A\3\2\2\2@>\3\2\2\2AB\5\4\3\2B\7\3\2\2\2CG\5\24\13"+
		"\2DF\7\16\2\2ED\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2HJ\3\2\2\2IG\3\2"+
		"\2\2JN\5\32\16\2KM\7\16\2\2LK\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OQ"+
		"\3\2\2\2PN\3\2\2\2QU\5\20\t\2RT\7\16\2\2SR\3\2\2\2TW\3\2\2\2US\3\2\2\2"+
		"UV\3\2\2\2VY\3\2\2\2WU\3\2\2\2XZ\7\24\2\2YX\3\2\2\2YZ\3\2\2\2Z\t\3\2\2"+
		"\2[]\7\16\2\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_a\3\2\2\2`^\3"+
		"\2\2\2ab\7\t\2\2bc\7\34\2\2cg\7\n\2\2df\7\16\2\2ed\3\2\2\2fi\3\2\2\2g"+
		"e\3\2\2\2gh\3\2\2\2h\13\3\2\2\2ig\3\2\2\2jp\7\7\2\2kl\5\26\f\2lm\7\30"+
		"\2\2mo\3\2\2\2nk\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rp\3\2"+
		"\2\2st\5\26\f\2tu\7\b\2\2uy\3\2\2\2vx\5\30\r\2wv\3\2\2\2x{\3\2\2\2yw\3"+
		"\2\2\2yz\3\2\2\2z\r\3\2\2\2{y\3\2\2\2|\177\5\22\n\2}\177\5\24\13\2~|\3"+
		"\2\2\2~}\3\2\2\2\177\u0080\3\2\2\2\u0080\u0081\7\31\2\2\u0081\u0082\5"+
		"\4\3\2\u0082\17\3\2\2\2\u0083\u0088\t\2\2\2\u0084\u0088\7\36\2\2\u0085"+
		"\u0088\7\33\2\2\u0086\u0088\7\34\2\2\u0087\u0083\3\2\2\2\u0087\u0084\3"+
		"\2\2\2\u0087\u0085\3\2\2\2\u0087\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089"+
		"\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\21\3\2\2\2\u008b\u008f\7\26\2"+
		"\2\u008c\u008e\5\30\r\2\u008d\u008c\3\2\2\2\u008e\u0091\3\2\2\2\u008f"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\23\3\2\2\2\u0091\u008f\3\2\2"+
		"\2\u0092\u0096\7\27\2\2\u0093\u0095\5\30\r\2\u0094\u0093\3\2\2\2\u0095"+
		"\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u009f\3\2"+
		"\2\2\u0098\u0096\3\2\2\2\u0099\u009b\5\30\r\2\u009a\u0099\3\2\2\2\u009b"+
		"\u009c\3\2\2\2\u009c\u009a\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009f\3\2"+
		"\2\2\u009e\u0092\3\2\2\2\u009e\u009a\3\2\2\2\u009f\25\3\2\2\2\u00a0\u00a2"+
		"\5\4\3\2\u00a1\u00a0\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a4\3\2\2\2\u00a3"+
		"\u00a5\5\n\6\2\u00a4\u00a3\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\27\3\2\2"+
		"\2\u00a6\u00a7\7\32\2\2\u00a7\31\3\2\2\2\u00a8\u00ab\7\35\2\2\u00a9\u00ab"+
		"\t\3\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00a9\3\2\2\2\u00ab\33\3\2\2\2\33\37"+
		"!*,\618>GNUY^gpy~\u0087\u0089\u008f\u0096\u009c\u009e\u00a1\u00a4\u00aa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}