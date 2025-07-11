package org.simplity.calc.engine.api;

import java.util.Map;

/**
 * The primary interface for the Calculator Engine. The engine is designed for
 * an initial bootstrap, and then a repeated calls to {@link #calculate(Map)}
 * and finally shut it down.
 */
public interface ICalcEngine {

	/**
	 * Calculates output variables based on the provided raw string inputs.
	 * <p>
	 * The Engine carries out calculations:
	 * <Ul>
	 * <li>Required inputs are checked for existence and validity. ALl input fields
	 * are validated even if the first one fails the validation.</li>
	 * <li>>Optional fields are validated if provided. If an optional field is
	 * invalid</li>
	 * <li>Error result is returned in case of any error in the above steps</li>
	 * <li>Output fields are calculated as per the rules.</li>
	 * <li>Once all the output fields are calculated, a success-result is
	 * returned</li>
	 * <li>No output fields are returned in case of any unexpected error, even if
	 * some values would been calculated successfully.</li>
	 * </ul>
	 *
	 * @param inputs A map of input variable names to their raw string values. It
	 *               should have valid values for every mandatory_input variables.
	 *               It may have values for optional_input fields. Any other values
	 *               in the collection are ignored.
	 * @return A calculation result object.
	 */
	CalcResultDS calculate(Map<String, String> inputs);

	/**
	 * Releases any resources held by the engine.
	 */
	void shutdown();
}
