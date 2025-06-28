package org.simplity.calc.config;

/**
 * Represents a single conditional "if-then" branch within a {@link CalcRule}.
 * It pairs a boolean condition with a result expression.
 */

public class RuleScenario {
	/**
	 * A boolean expression. If this evaluates to {@code true}, the corresponding
	 * {@link #expression} is executed.
	 */
	public String condition;

	/**
	 * The expression to evaluate for the value if the {@link #condition} is true.
	 */
	public String expression;
}
