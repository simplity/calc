package org.simplity.calc.engine.config;

/**
 * Represents a single conditional "if-then" branch within a
 * {@link CalculatorDS}. It pairs a boolean condition with a result expression.
 */

public class CalcStepsDS {
	/**
	 * A boolean expression. If this evaluates to {@code true}, the corresponding
	 * {@link #value} is executed.
	 */
	public String when;

	/**
	 * The expression to evaluate for the value if the {@link #when} is true. It
	 * must evaluate to the right valueType for the variable to which this is
	 * attached to
	 */
	public String value;
}
