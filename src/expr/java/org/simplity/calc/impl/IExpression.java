package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Represents a compiled, executable expression within the calculation engine.
 *
 * <h3>Architectural Note</h3> This interface represents a node in a normalized,
 * function-based expression tree. It is the "compiled" output from the
 * {@code AstBuilder}, not a direct representation of the original user-typed
 * syntax. In this canonical form, all operations (e.g., {@code +}, {@code -},
 * {@code *}) are converted into standard {@link FunctionExpression} nodes. This
 * design simplifies evaluation, as the engine only needs to handle three types
 * of expressions: literals, variables, and function calls.
 *
 * <h3>Lifecycle and Validation</h3> An expression undergoes several stages of
 * validation:
 * <ol>
 * <li><b>Build-Time Validation:</b> The {@code AstBuilder} ensures that an
 * expression is structurally and type-wise correct. An invalid expression (e.g.
 * adding a number to a boolean) should never be created.</li>
 * <li><b>Pre-Evaluation Check (Dry Run):</b> The {@link #dryrun} method
 * performs a dynamic, context-sensitive check to determine if the expression is
 * ready to be evaluated (e.g., checking if all required variables are available
 * in the context).</li>
 * <li><b>Evaluation:</b> The {@link #evaluate} method executes the logic,
 * assuming the prior checks have passed.</li>
 * </ol>
 *
 * <h3>Immutability and Thread Safety</h3> An {@code IExpression} tree, once
 * created by the builder, is **immutable**. Its structure and composition do
 * not change. As a result, a compiled expression is inherently **thread-safe**
 * and can be evaluated concurrently by multiple threads with different
 * contexts.
 *
 * @author Simplity Technologies
 * @since 1.0
 * @see FunctionExpression
 * @see VariableExpression
 * @see LiteralExpression
 */
public interface IExpression {

	/**
	 * Gets the data type that this expression is expected to evaluate to. This type
	 * is determined and validated at build-time.
	 *
	 * @return The non-null {@link ValueType} of the resulting value.
	 */
	ValueType getValueType();

	/**
	 * Evaluates this expression within a given context to produce a result.
	 * <p>
	 * This method should only be called after a successful {@link #dryrun} to
	 * ensure all dependencies are available.
	 *
	 * @param ctx The calculation context, providing access to variables and other
	 *            runtime data. Must not be null.
	 * @return The resulting {@link IValue} of the evaluation. Will not be null.
	 */
	IValue evaluate(ICalcContext ctx);

	/**
	 * Performs a "dry run" to check if this expression can be successfully
	 * evaluated with the given context.
	 * <p>
	 * This is not a type check; it is a dynamic readiness check. Its primary
	 * purpose is to verify that all variable dependencies are currently available
	 * and evaluatable within the provided context.
	 *
	 * @param ctx The context to check against.
	 * @return {@code true} if the expression is ready to be evaluated,
	 *         {@code false} otherwise. If {@code false}, it is expected that a
	 *         reason for the failure has been logged to the context.
	 */
	boolean dryrun(IDryrunContext ctx);

}