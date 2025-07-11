package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * Represents a component that can be used to determine the value of a variable
 * in the calculation process.
 * <p>
 * The core purpose of any {@link ICalculator} is to be evaluated within a given
 * context to produce an {@link IValue}. It can also report its expected return
 * type for static analysis before evaluation.
 *
 * <p>
 * <b>Architectural Note:</b> This interface is identical to IExpression, except
 * that the method name is "calculate". This is not a coincidence, but a design
 * requirement. Both expressions and calculators serve identical purposes for
 * the calculation purpose. This approach is to reflect the underlying possible
 * implementation details.
 * <p>
 */
public interface ICalculator {
	/**
	 * Gets the data type that this calculator is expected to evaluate to.
	 * <p>
	 * This allows for static validation of the entire set of rules and expressions
	 * before execution, ensuring type compatibility between functions and their
	 * arguments.
	 *
	 * @return The non-null {@link DataType} of the resulting value.
	 */
	IValueType getValueType();

	/**
	 * Apply this rule within a given context to produce a result.
	 *
	 * @param ctx The calculation context, providing access to variables and other
	 *            runtime data. Must not be null.
	 * @return The resulting {@link IValue} of the evaluation. Null in case of any
	 *         error while evaluting.
	 */
	IValue calculate(ICalcContext ctx);

	/**
	 * Dryrun this rule to check if it semantically possible to use the rule. More
	 * specifically, check if every variable that this rule may be dependent on
	 * (outer set assuming all possible paths) can be "resolved" or "determined"
	 * based on the dependencies between the variables. This can can be typically
	 * achieved by recursively using this method on all sub-expressions.
	 *
	 * @param ctx
	 * @return true if all OK. False on any error, in which case suitable error
	 *         messages would have been logged into the context
	 */
	boolean dryrun(DryrunContext ctx);

}