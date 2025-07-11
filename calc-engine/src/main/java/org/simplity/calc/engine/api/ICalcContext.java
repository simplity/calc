package org.simplity.calc.engine.api;

/**
 * Defines the public contract for the context of a calculation run. Primary
 * objective this API is for the designers of app-specific functions.
 * Specifically {@link ICalcFunction#call(IValue[], ICalcContext)} method design
 * can make use of the methods available in this interface
 */
public interface ICalcContext {
	/**
	 * Check if this data element has a ready value in the context cache.
	 *
	 * @param name
	 * @return true if the data element has a ready value, false otherwise.
	 */
	boolean hasValue(String name);

	/**
	 * Get the value for this variable. It is retrieved from the current cache,
	 * failing which it is calculated based on the associated calculator.
	 *
	 * @param name
	 * @return non-null value
	 */
	IValue determineValue(String name);

	/**
	 * Log a non-fatal error to the context. This can be used by functions or
	 * expressions to report issues without stopping the entire calculation.
	 *
	 * @param name    with which this message is associated with. null if it is not
	 *                associated with a data element
	 * @param errorId non-null non-empty id that identifies the error message to be
	 *                flashed. The context contains the actual text of the messages
	 *                for all the errorIds
	 *
	 */
	void logError(String name, String errorId);

	/**
	 * Value for the named data element is cached for this run of calculation
	 *
	 * @param name
	 * @param value
	 */
	void cacheValue(String name, IValue value);

	/**
	 *
	 * @return true if the context has logged at least one error
	 */
	boolean hasErrors();
}
