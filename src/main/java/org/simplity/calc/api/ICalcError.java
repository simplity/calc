package org.simplity.calc.api;

/**
 * Defines the public contract for a structured error object.
 */
public interface ICalcError {
	/**
	 * @return The name of the variable on which the error occurred, or null if it's
	 *         a general error.
	 */
	String getVariableName();

	/**
	 * @return The descriptive error message.
	 */
	String getMessage();
}