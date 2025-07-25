package org.simplity.calc.engine.config;

/**
 * Represents the calculation logic for a data element, structured as a series
 * of conditional branches ("if-then") and a final default/fallback expression.
 * <p>
 * The engine evaluates each {@link CalcStepsDS} in the {@link #calcSteps} array
 * in order. The first one whose condition evaluates to {@code true} will have
 * its expression executed, and the result becomes the value of the data
 * element. If no scenarios are present, or if none of their conditions are met,
 * the {@link #defaultExpression} is executed.
 */
public class CalculatorDS {
	/**
	 * The expression to be evaluated if no conditions in the {@link #calcSteps}
	 * array are met. This acts as the final "else" block.
	 */
	public String defaultExpression;

	/**
	 * An optional list of calculation steps.
	 */
	public CalcStepsDS[] calcSteps;
}