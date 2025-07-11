package org.simplity.calc.engine.api;

/**
 * A data structure that holds the details of an error encountered during a
 * calculation run This should be treated as an immutable object
 */
public final class CalcErrorDS {
	/**
	 * if this error is attributable to a specific field/dataElement. empty-string
	 * and not null if this is not relevant
	 */
	public String dataElementName = "";
	/**
	 * detailed error message
	 */
	public String message;

	/**
	 * for serializers
	 */
	public CalcErrorDS() {
		//
	}

	/**
	 *
	 * @param variableName with which this message is associated with. Empty string
	 *                     if this NOT associated with any specific variable
	 * @param message
	 */
	public CalcErrorDS(String variableName, String message) {
		this.dataElementName = variableName == null ? "" : variableName;
		this.message = message == null ? "" : message;
	}

	/**
	 *
	 * @param message
	 */
	public CalcErrorDS(String message) {
		this.message = message == null ? "" : message;
	}

	@Override
	public String toString() {
		if (this.dataElementName.isEmpty()) {
			return this.message;
		}
		return this.dataElementName + ": " + this.message;
	}
}
