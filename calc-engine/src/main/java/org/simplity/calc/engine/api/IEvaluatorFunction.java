package org.simplity.calc.engine.api;

import java.util.function.BiFunction;

import org.simplity.calc.engine.impl.Values;

/**
 * Evaluator functions are the work horses of the calculation engine. An
 * Evaluator function is invoked at run time with its arguments and a context.
 * The function uses the (optional) parameters to calculate a value to be
 * returned, the context provides utilities to get values for any other fixed
 * variables, or to handle run-time error
 *
 * This interface is primarily designed to be an internal component to implement
 * the (@link ICalcFunction}. This is actually a shortcut to a Type declaration
 * of {@link BiFunction} as <code>
 * BiFunction<IValue[], ICalcContext, IValue> </code>
 *
 * @see ICalcFunction
 * @see ICalcContext
 * @see Values
 */
@FunctionalInterface
public interface IEvaluatorFunction {
	/**
	 *
	 * @param values array of values to be used as the arguments for this function
	 * @param ctx    Provides source for values for any other variable.
	 * @return non-null value
	 */
	IValue apply(IValue[] values, ICalcContext ctx);
}
