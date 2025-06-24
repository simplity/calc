package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Represents an executable expression or sub-expression in the calculation
 * engine.
 * <p>
 * The core purpose of any IExpression is to be evaluated within a given context
 * to produce an {@link IValue}. It can also report its expected return type for
 * static analysis before evaluation.
 *
 *
 * <p>
 * <b>Architectural Note:</b> This interface represents a node in a normalized,
 * function-based expression tree. It is the "compiled" output from the
 * AstBuilder, not a direct representation of the original user-typed syntax. In
 * this canonical form, all operations (e.g., +, -, *) are converted into
 * standard {@link FunctionExpression} nodes.
 * <p>
 * This design simplifies evaluation, as the engine only needs to handle three
 * types of expressions: literals, variables, and function calls.
 *
 * @see FunctionExpression
 * @see VariableExpression
 * @see LiteralExpression
 */
public interface IExpression {

	/**
	 * Gets the data type that this expression is expected to evaluate to.
	 * <p>
	 * This allows for static validation of an expression tree before execution,
	 * ensuring type compatibility between functions and their arguments.
	 *
	 * @return The non-null {@link ValueType} of the resulting value.
	 */
	ValueType getValueType();

	/**
	 * Evaluates this expression within a given context to produce a result.
	 *
	 * @param ctx The calculation context, providing access to variables and other
	 *            runtime data. Must not be null.
	 * @return The resulting {@link IValue} of the evaluation. Never null.
	 */
	IValue evaluate(ICalcContext ctx);

}