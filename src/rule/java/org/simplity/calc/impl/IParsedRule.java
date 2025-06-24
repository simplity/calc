package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Represents a rule that can be used to determine the value of a variable in
 * the calculation process.
 * <p>
 * The core purpose of any ICalcRule is to be evaluated within a given context
 * to produce an {@link IValue}. It can also report its expected return type for
 * static analysis before evaluation.
 *
 * <p>
 * <b>Architectural Note:</b> This interface is identical to IExpression, except
 * that the method name is "apply". This is not a coincidence, but a design
 * requirement. Both expressions and rules serve identical purposes for the
 * calculation purpose. This approach is to reflect the underlying
 * implementation possible implementation details.
 * <p>
 */
public interface IParsedRule {
	/**
	 * Gets the data type that this rule is expected to evaluate to.
	 * <p>
	 * This allows for static validation of the entire set of rules and expressions
	 * before execution, ensuring type compatibility between functions and their
	 * arguments.
	 *
	 * @return The non-null {@link ValueType} of the resulting value.
	 */
	ValueType getValueType();

	/**
	 * APply this rule within a given context to produce a result.
	 *
	 * @param ctx The calculation context, providing access to variables and other
	 *            runtime data. Must not be null.
	 * @return The resulting {@link IValue} of the evaluation. Null in case of any
	 *         error while evaluting.
	 */
	IValue apply(ICalcContext ctx);

}