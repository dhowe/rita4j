// Generated from ../ritajs/grammar/RiScriptParser.g4 by ANTLR 4.8
package rita.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RiScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RiScriptParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#script}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScript(RiScriptParser.ScriptContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(RiScriptParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(RiScriptParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#cexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCexpr(RiScriptParser.CexprContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#cond}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCond(RiScriptParser.CondContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#weight}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWeight(RiScriptParser.WeightContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#choice}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChoice(RiScriptParser.ChoiceContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(RiScriptParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#chars}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChars(RiScriptParser.CharsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#dynamic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDynamic(RiScriptParser.DynamicContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#symbol}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSymbol(RiScriptParser.SymbolContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#wexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWexpr(RiScriptParser.WexprContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#link}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLink(RiScriptParser.LinkContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#url}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUrl(RiScriptParser.UrlContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#transform}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransform(RiScriptParser.TransformContext ctx);
	/**
	 * Visit a parse tree produced by {@link RiScriptParser#op}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOp(RiScriptParser.OpContext ctx);
}