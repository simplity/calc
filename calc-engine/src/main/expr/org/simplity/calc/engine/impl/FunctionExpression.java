package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * An immutable expression that represents a function call. In this engine, all
 * operations, including infix operators like '+' and '-', are treated as
 * functions.
 * <p>
 * This expression holds a reference to an {@link ICalcFunction} and an array of
 * argument expressions.
 *
 * <h3>Design Note: Validation</h3> This class is self-validating. Its
 * constructor rigorously checks the provided arguments against the function's
 * defined signature. An {@code IllegalArgumentException} is thrown if the
 * arguments are not valid, ensuring that no invalid {@code FunctionExpression}
 * object can be created.
 *
 * <h3>Thread Safety</h3> This class is immutable and therefore inherently
 * thread-safe.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public final class FunctionExpression implements IExpression {
	private final ICalcFunction function;
	private final IExpression[] arguments;
	private final IValueType returnType;

	/**
	 * Constructs a new FunctionExpression and validates it against the function's
	 * signature.
	 *
	 * @param function  The non-null {@link ICalcFunction} to be executed.
	 * @param arguments A non-null array of {@link IExpression}s.
	 * @throws IllegalArgumentException if the arguments violate the signature
	 *                                  defined by the function.
	 */
	FunctionExpression(ICalcFunction function, IExpression[] arguments) {
		this.function = function;
		this.arguments = arguments;
		this.returnType = function.getReturnType();
		this.validate();
	}

	private void validate() {
		final IValueType[] paramTypes = this.function.getParameterTypes();
		final int nbrParams = paramTypes.length;
		final int nbrArgs = this.arguments.length;

		if (!this.function.lastOneIsVararg()) {
			// --- Case 1: Standard fixed-argument function ---
			if (nbrArgs != nbrParams) {
				throw new IllegalArgumentException(
						"Function expects " + nbrParams + " arguments but " + nbrArgs + " were provided.");
			}
			for (int i = 0; i < nbrParams; i++) {
				matchValueType(paramTypes[i], this.arguments[i].getValueType(), i + 1);
			}
			return;
		}

		// --- Case 2: Variadic argument function ---
		if (nbrParams == 0) {
			throw new IllegalStateException("Function is marked as variadic but has no parameter types defined.");
		}

		final int minRequiredArgs = nbrParams - 1;
		if (nbrArgs < minRequiredArgs) {
			throw new IllegalArgumentException(
					"Function expects at least " + minRequiredArgs + " arguments, but " + nbrArgs + " were provided.");
		}

		// Validate the fixed arguments
		for (int i = 0; i < minRequiredArgs; i++) {
			matchValueType(paramTypes[i], this.arguments[i].getValueType(), i + 1);
		}

		// Validate the variable arguments
		if (nbrArgs > minRequiredArgs) {
			// The vararg type is the LAST one in the parameter types array.
			final IValueType varArgType = paramTypes[nbrParams - 1];
			for (int i = minRequiredArgs; i < nbrArgs; i++) {
				matchValueType(varArgType, this.arguments[i].getValueType(), i + 1);
			}
		}
	}

	/**
	 * Private helper to compare an expected type against an actual type and throw a
	 * formatted error if they do not match.
	 * <p>
	 * This method embodies the engine's polymorphic capabilities: a {@code null}
	 * value for the {@code expectedType} parameter is treated as a wildcard that
	 * successfully matches any {@code actualType}.
	 *
	 * @param expectedType the required {@link DataType}, or {@code null} to allow
	 *                     any type.
	 * @param actualType   the actual {@link DataType} of the argument being
	 *                     checked.
	 * @param position     the one-based argument position, for clear error
	 *                     reporting.
	 * @throws IllegalArgumentException if the types are not compatible.
	 */
	private static void matchValueType(IValueType expectedType, IValueType actualType, int position) {
		if (expectedType == null || expectedType == actualType) {
			return;
		}
		throw new IllegalArgumentException("Argument at position " + position + " should be of type "
				+ expectedType.getDataTypeName() + " but it is of type " + actualType.getDataTypeName());
	}

	@Override
	public IValueType getValueType() {
		return this.returnType;
	}

	@Override
	public IValue evaluate(ICalcContext ctx) {
		final IValue[] argValues = new IValue[this.arguments.length];
		for (int i = 0; i < this.arguments.length; i++) {
			argValues[i] = this.arguments[i].evaluate(ctx);
		}
		return this.function.call(argValues, ctx);
	}

	@Override
	public boolean dryrun(DryrunContext ctx) {
		for (IExpression e : this.arguments) {
			if (!e.dryrun(ctx)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the child expressions that serve as arguments for this function. This is
	 * useful for visitors that need to traverse the expression tree.
	 *
	 * @return a non-null (but possibly empty) array of argument expressions.
	 */
	public IExpression[] getArguments() {
		return this.arguments;
	}
}