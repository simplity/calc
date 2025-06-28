package org.simplity.calc.impl;

/**
 * context that is shared with all the components at the time of the bootstrap
 * process
 */
interface IDryrunContext {
	/**
	 * check if it is possible that the evaluation process for this variable may get
	 * into an infinite loop (due to depending on itself)
	 *
	 * @param variableName
	 * @return true if all ok. false if an error condition is detected
	 */
	boolean isEvaluatable(String variableName);
}
