package org.simplity.calc.api;

/**
 * Defines the public contract for the context of a calculation run.
 */
public interface ICalcContext {
	/**
	 * get the value for this variable. It is retrieved from the current cache,
	 * failing which it is calculated based on the associated rul.
	 *
	 * @param variableName
	 * @return non-null value
	 */
	IValue determineValue(String variableName);

	/**
	 * Logs a non-fatal error to the context. This can be used by functions or
	 * expressions to report issues without stopping the entire calculation.
	 *
	 * @param variableName with which this message is associated with. null if it is
	 *                     not associated with a variable
	 * @param message      non-null non-empty error message
	 *
	 */
	void logError(String variableName, String message);

	/**
	 * value for the named variable is cached for this run of calculation
	 *
	 * @param valName
	 * @param value
	 */
	void cacheValue(String valName, IValue value);

	/**
	 *
	 * @return true if the context has logged at least one error
	 */
	boolean hasErrors();

	/**
	 *
	 * @return an array of logged errors. empty, but non-null, in case there are no
	 *         errors
	 */
	ICalcError[] getErrors();
}
