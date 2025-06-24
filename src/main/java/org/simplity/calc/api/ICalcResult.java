package org.simplity.calc.api;

import java.util.Map;

/**
 * The public interface for the result of a calculation.
 */
public interface ICalcResult {
	/**
	 * @return true if the calculation completed without any errors, false
	 *         otherwise.
	 */
	boolean isSuccess();

	/**
	 * Gets the map of output variable names to their calculated values.
	 *
	 * @return output values. empty in case of any error.
	 */
	Map<String, IValue> getOutputs();

	/**
	 * Gets the list of all errors that occurred during the calculation.
	 *
	 * @return An unmodifiable list of errors. Will be empty if isSuccess() is true.
	 */
	ICalcError[] getErrors();
}
