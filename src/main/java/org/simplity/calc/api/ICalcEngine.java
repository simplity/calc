package org.simplity.calc.api;

import java.util.Map;

/**
 * The primary interface for the Calculator Engine. Note the updated signature
 * for the calculate method.
 */
public interface ICalcEngine {

	/**
	 * Calculates output variables based on the provided raw string inputs.
	 *
	 * @param inputs A map of input variable names to their raw string values.
	 * @return A calculation result object.
	 */
	ICalcResult calculate(Map<String, String> inputs);

	/**
	 * Releases any resources held by the engine.
	 */
	void shutdown();
}
