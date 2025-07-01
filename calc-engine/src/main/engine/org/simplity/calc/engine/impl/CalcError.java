package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcError;

/**
 * A structured object representing a single error that occurred during
 * calculation. This is the concrete implementation of the ICalcError interface.
 */
class CalcError implements ICalcError {
	private final String variableName;
	private final String message;

	/**
	 *
	 * @param variableName
	 * @param message
	 */
	public CalcError(String variableName, String message) {
		this.variableName = variableName;
		this.message = message;
	}

	@Override
	public String getVariableName() {
		return this.variableName;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String toString() {
		return this.message;
	}
}
