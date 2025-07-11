package org.simplity.calc.engine.config;

/**
 * Represents a validation requirement
 */
public class ValidatorDS {
	/**
	 * condition the value should conform to. This is boolean-values expression.
	 */
	public String shouldBe;

	/**
	 * Optional messageId to be used to flag this validation error. Defaults to the
	 * messageId at the variable level.
	 */
	public String messageId;
}