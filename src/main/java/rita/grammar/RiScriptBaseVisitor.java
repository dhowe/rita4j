// Generated from ../rita2js/grammar/RiScript.g4 by ANTLR 4.7.1
package rita.grammar;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link RiScriptVisitor}, which
 * can be extended to create a visitor which only needs to handle a subset of
 * the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public class RiScriptBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements RiScriptVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitScript(RiScriptParser.ScriptContext ctx) {
		System.out.println("visitScript: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitExpr(RiScriptParser.ExprContext ctx) {
		System.out.println("visitExpr: '" + ctx.getText() + "'");
		return visitChildren(ctx);

	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitCexpr(RiScriptParser.CexprContext ctx) {
		System.out.println("visitCexpr: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitCond(RiScriptParser.CondContext ctx) {
		System.out.println("visitCond: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitWeight(RiScriptParser.WeightContext ctx) {
		System.out.println("visitWeight: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitChoice(RiScriptParser.ChoiceContext ctx) {
		System.out.println("visitChoice: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitInline(RiScriptParser.InlineContext ctx) {
		System.out.println("visitInline: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitAssign(RiScriptParser.AssignContext ctx) {
		System.out.println("visitAssign: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitChars(RiScriptParser.CharsContext ctx) {
		System.out.println("visitChars: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitSymbol(RiScriptParser.SymbolContext ctx) {
		System.out.println("visitSymbol: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitWexpr(RiScriptParser.WexprContext ctx) {
		System.out.println("visitWexpr: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitTransform(RiScriptParser.TransformContext ctx) {
		System.out.println("visitTransform: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.
	 * </p>
	 */
	@Override
	public T visitOp(RiScriptParser.OpContext ctx) {
		System.out.println("visitOp: '" + ctx.getText() + "'");
		return visitChildren(ctx);
	}
}