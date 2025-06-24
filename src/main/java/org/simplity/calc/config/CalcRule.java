package org.simplity.calc.config;

/**
 * Represents the calculation rule for a data element. It consists of a
 * mandatory default expression and an optional list of conditional branches.
 */
public class CalcRule {
	/**
	 * The expression to be evaluated if no conditions are met, or if there are no
	 * conditions.
	 */
	public String defaultExpression;

	/**
	 * An optional list of conditional expressions, evaluated in order. The first
	 * condition that evaluates to true will have its corresponding expression
	 * evaluated as the result.
	 */
	public ConditionalExpression[] conditionalExpressions;
}
