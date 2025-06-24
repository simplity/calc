package org.simplity.calc.config;

/**
 * Represents a single "if-then" branch within a Rule.
 */
public class ConditionalExpression {
	/**
	 * The boolean expression to evaluate as the condition.
	 */
	public String condition;

	/**
	 * The expression to evaluate for the value if the condition is true.
	 */
	public String expression;
}
