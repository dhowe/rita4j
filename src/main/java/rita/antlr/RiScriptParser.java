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
		GT=1, LT=2, LP=3, RP=4, LB=5, RB=6, LCB=7, RCB=8, DOT=9, WS=10, EXC=11, 
		AST=12, HAT=13, DOL=14, COM=15, NL=16, SYM=17, OR=18, EQ=19, TF=20, ENT=21, 
		INT=22, OP=23, CHR=24;
	public static final int
		RULE_script = 0, RULE_expr = 1, RULE_cexpr = 2, RULE_cond = 3, RULE_weight = 4, 
		RULE_choice = 5, RULE_assign = 6, RULE_chars = 7, RULE_symbol = 8, RULE_wexpr = 9, 
		RULE_transform = 10, RULE_op = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"script", "expr", "cexpr", "cond", "weight", "choice", "assign", "chars", 
			"symbol", "wexpr", "transform", "op"
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
			"EXC", "AST", "HAT", "DOL", "COM", "NL", "SYM", "OR", "EQ", "TF", "ENT", 
			"INT", "OP", "CHR"
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
			setState(27); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(27);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
				case 1:
					{
					setState(24);
					expr();
					}
					break;
				case 2:
					{
					setState(25);
					cexpr();
					}
					break;
				case 3:
					{
					setState(26);
					match(NL);
					}
					break;
				}
				}
				setState(29); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << LP) | (1L << LCB) | (1L << DOT) | (1L << WS) | (1L << EXC) | (1L << AST) | (1L << HAT) | (1L << DOL) | (1L << COM) | (1L << NL) | (1L << SYM) | (1L << TF) | (1L << ENT) | (1L << INT) | (1L << CHR))) != 0) );
			setState(31);
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
			setState(37); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(37);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
					case 1:
						{
						setState(33);
						symbol();
						}
						break;
					case 2:
						{
						setState(34);
						choice();
						}
						break;
					case 3:
						{
						setState(35);
						assign();
						}
						break;
					case 4:
						{
						setState(36);
						chars();
						}
						break;
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(39); 
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
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(41);
				match(WS);
				}
				}
				setState(46);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(47);
			match(LCB);
			setState(49); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(48);
				cond();
				}
				}
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SYM );
			setState(53);
			match(RCB);
			setState(57);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(54);
					match(WS);
					}
					} 
				}
				setState(59);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			setState(60);
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
		public TerminalNode SYM() { return getToken(RiScriptParser.SYM, 0); }
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
			setState(62);
			match(SYM);
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(63);
				match(WS);
				}
				}
				setState(68);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(69);
			op();
			setState(73);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(70);
					match(WS);
					}
					} 
				}
				setState(75);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			setState(76);
			chars();
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(77);
				match(WS);
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COM) {
				{
				setState(83);
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
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(86);
				match(WS);
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(92);
			match(LB);
			setState(93);
			match(INT);
			setState(94);
			match(RB);
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WS) {
				{
				{
				setState(95);
				match(WS);
				}
				}
				setState(100);
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
			setState(101);
			match(LP);
			setState(107);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(102);
					wexpr();
					setState(103);
					match(OR);
					}
					} 
				}
				setState(109);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			}
			setState(110);
			wexpr();
			setState(111);
			match(RP);
			}
			setState(116);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(113);
					transform();
					}
					} 
				}
				setState(118);
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
		public SymbolContext symbol() {
			return getRuleContext(SymbolContext.class,0);
		}
		public TerminalNode EQ() { return getToken(RiScriptParser.EQ, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
			setState(119);
			symbol();
			setState(120);
			match(EQ);
			setState(121);
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
			setState(127); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(127);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case GT:
					case LT:
					case DOT:
					case WS:
					case EXC:
					case AST:
					case HAT:
					case DOL:
					case COM:
						{
						setState(123);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << LT) | (1L << DOT) | (1L << WS) | (1L << EXC) | (1L << AST) | (1L << HAT) | (1L << DOL) | (1L << COM))) != 0)) ) {
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
						setState(124);
						match(CHR);
						}
						break;
					case ENT:
						{
						setState(125);
						match(ENT);
						}
						break;
					case INT:
						{
						setState(126);
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
				setState(129); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
		enterRule(_localctx, 16, RULE_symbol);
		try {
			int _alt;
			setState(143);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SYM:
				enterOuterAlt(_localctx, 1);
				{
				setState(131);
				match(SYM);
				setState(135);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(132);
						transform();
						}
						} 
					}
					setState(137);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
				}
				}
				break;
			case TF:
				enterOuterAlt(_localctx, 2);
				{
				setState(139); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(138);
						transform();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(141); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
		enterRule(_localctx, 18, RULE_wexpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(145);
				expr();
				}
				break;
			}
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LB || _la==WS) {
				{
				setState(148);
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
		enterRule(_localctx, 20, RULE_transform);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
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
		enterRule(_localctx, 22, RULE_op);
		int _la;
		try {
			setState(155);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OP:
				enterOuterAlt(_localctx, 1);
				{
				setState(153);
				match(OP);
				}
				break;
			case GT:
			case LT:
			case EQ:
				enterOuterAlt(_localctx, 2);
				{
				setState(154);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\u00a0\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\2\6\2\36\n\2\r\2\16\2\37\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\6\3(\n\3\r\3\16\3)\3\4\7\4-\n\4\f\4\16\4\60\13\4\3\4\3\4\6"+
		"\4\64\n\4\r\4\16\4\65\3\4\3\4\7\4:\n\4\f\4\16\4=\13\4\3\4\3\4\3\5\3\5"+
		"\7\5C\n\5\f\5\16\5F\13\5\3\5\3\5\7\5J\n\5\f\5\16\5M\13\5\3\5\3\5\7\5Q"+
		"\n\5\f\5\16\5T\13\5\3\5\5\5W\n\5\3\6\7\6Z\n\6\f\6\16\6]\13\6\3\6\3\6\3"+
		"\6\3\6\7\6c\n\6\f\6\16\6f\13\6\3\7\3\7\3\7\3\7\7\7l\n\7\f\7\16\7o\13\7"+
		"\3\7\3\7\3\7\3\7\7\7u\n\7\f\7\16\7x\13\7\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\6\t\u0082\n\t\r\t\16\t\u0083\3\n\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13"+
		"\n\3\n\6\n\u008e\n\n\r\n\16\n\u008f\5\n\u0092\n\n\3\13\5\13\u0095\n\13"+
		"\3\13\5\13\u0098\n\13\3\f\3\f\3\r\3\r\5\r\u009e\n\r\3\r\2\2\16\2\4\6\b"+
		"\n\f\16\20\22\24\26\30\2\4\4\2\3\4\13\21\4\2\3\4\25\25\2\u00af\2\35\3"+
		"\2\2\2\4\'\3\2\2\2\6.\3\2\2\2\b@\3\2\2\2\n[\3\2\2\2\fg\3\2\2\2\16y\3\2"+
		"\2\2\20\u0081\3\2\2\2\22\u0091\3\2\2\2\24\u0094\3\2\2\2\26\u0099\3\2\2"+
		"\2\30\u009d\3\2\2\2\32\36\5\4\3\2\33\36\5\6\4\2\34\36\7\22\2\2\35\32\3"+
		"\2\2\2\35\33\3\2\2\2\35\34\3\2\2\2\36\37\3\2\2\2\37\35\3\2\2\2\37 \3\2"+
		"\2\2 !\3\2\2\2!\"\7\2\2\3\"\3\3\2\2\2#(\5\22\n\2$(\5\f\7\2%(\5\16\b\2"+
		"&(\5\20\t\2\'#\3\2\2\2\'$\3\2\2\2\'%\3\2\2\2\'&\3\2\2\2()\3\2\2\2)\'\3"+
		"\2\2\2)*\3\2\2\2*\5\3\2\2\2+-\7\f\2\2,+\3\2\2\2-\60\3\2\2\2.,\3\2\2\2"+
		"./\3\2\2\2/\61\3\2\2\2\60.\3\2\2\2\61\63\7\t\2\2\62\64\5\b\5\2\63\62\3"+
		"\2\2\2\64\65\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66\67\3\2\2\2\67;\7\n"+
		"\2\28:\7\f\2\298\3\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<>\3\2\2\2=;\3\2"+
		"\2\2>?\5\4\3\2?\7\3\2\2\2@D\7\23\2\2AC\7\f\2\2BA\3\2\2\2CF\3\2\2\2DB\3"+
		"\2\2\2DE\3\2\2\2EG\3\2\2\2FD\3\2\2\2GK\5\30\r\2HJ\7\f\2\2IH\3\2\2\2JM"+
		"\3\2\2\2KI\3\2\2\2KL\3\2\2\2LN\3\2\2\2MK\3\2\2\2NR\5\20\t\2OQ\7\f\2\2"+
		"PO\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2SV\3\2\2\2TR\3\2\2\2UW\7\21\2"+
		"\2VU\3\2\2\2VW\3\2\2\2W\t\3\2\2\2XZ\7\f\2\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2"+
		"\2\2[\\\3\2\2\2\\^\3\2\2\2][\3\2\2\2^_\7\7\2\2_`\7\30\2\2`d\7\b\2\2ac"+
		"\7\f\2\2ba\3\2\2\2cf\3\2\2\2db\3\2\2\2de\3\2\2\2e\13\3\2\2\2fd\3\2\2\2"+
		"gm\7\5\2\2hi\5\24\13\2ij\7\24\2\2jl\3\2\2\2kh\3\2\2\2lo\3\2\2\2mk\3\2"+
		"\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2\2pq\5\24\13\2qr\7\6\2\2rv\3\2\2\2su\5"+
		"\26\f\2ts\3\2\2\2ux\3\2\2\2vt\3\2\2\2vw\3\2\2\2w\r\3\2\2\2xv\3\2\2\2y"+
		"z\5\22\n\2z{\7\25\2\2{|\5\4\3\2|\17\3\2\2\2}\u0082\t\2\2\2~\u0082\7\32"+
		"\2\2\177\u0082\7\27\2\2\u0080\u0082\7\30\2\2\u0081}\3\2\2\2\u0081~\3\2"+
		"\2\2\u0081\177\3\2\2\2\u0081\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0081"+
		"\3\2\2\2\u0083\u0084\3\2\2\2\u0084\21\3\2\2\2\u0085\u0089\7\23\2\2\u0086"+
		"\u0088\5\26\f\2\u0087\u0086\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3"+
		"\2\2\2\u0089\u008a\3\2\2\2\u008a\u0092\3\2\2\2\u008b\u0089\3\2\2\2\u008c"+
		"\u008e\5\26\f\2\u008d\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u008d\3"+
		"\2\2\2\u008f\u0090\3\2\2\2\u0090\u0092\3\2\2\2\u0091\u0085\3\2\2\2\u0091"+
		"\u008d\3\2\2\2\u0092\23\3\2\2\2\u0093\u0095\5\4\3\2\u0094\u0093\3\2\2"+
		"\2\u0094\u0095\3\2\2\2\u0095\u0097\3\2\2\2\u0096\u0098\5\n\6\2\u0097\u0096"+
		"\3\2\2\2\u0097\u0098\3\2\2\2\u0098\25\3\2\2\2\u0099\u009a\7\26\2\2\u009a"+
		"\27\3\2\2\2\u009b\u009e\7\31\2\2\u009c\u009e\t\3\2\2\u009d\u009b\3\2\2"+
		"\2\u009d\u009c\3\2\2\2\u009e\31\3\2\2\2\31\35\37\').\65;DKRV[dmv\u0081"+
		"\u0083\u0089\u008f\u0091\u0094\u0097\u009d";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}