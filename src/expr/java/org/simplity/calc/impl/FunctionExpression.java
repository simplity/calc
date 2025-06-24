package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * An expression that represents a function call. All operations in the engine
 * are implemented as functions.
 * <p>
 * This expression holds a reference to an {@link ICalcFunction} and a list of
 * argument expressions. At construction time, it performs a rigorous validation
 * of the arguments against the function's defined signature.
 */
public final class FunctionExpression implements IExpression {
	private final ICalcFunction function;
	private final IExpression[] arguments;
	private final ValueType returnType;

	/**
	 * Constructs a new FunctionExpression.
	 *
	 * @param function  The {@link ICalcFunction} to be executed.
	 * @param arguments A list of {@link IExpression}s for the function's arguments.
	 */
	FunctionExpression(ICalcFunction function, IExpression[] arguments) {
		this.function = function;
		this.arguments = arguments;
		this.returnType = function.getReturnType();
	}

	@Override
	public ValueType getValueType() {
		return this.returnType;
	}

	@Override
	public IValue evaluate(ICalcContext ctx) {
		IValue[] argValues = new IValue[this.arguments.length];
		int i = 0;
		for (IExpression expr : this.arguments) {
			argValues[i++] = expr.evaluate(ctx);
		}
		return this.function.call(argValues, ctx);
	}

	/**
	 *
	 * @return list of expressions that form the arguments for this function. Could
	 *         be an empty array. Never null
	 */
	public IExpression[] getArguments() {
		return this.arguments;
	}
}