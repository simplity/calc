
package org.simplity.calc.engine.api;

import java.util.Map;

/**
 * Represents the output from a calculation run.
 * <p>
 * **Usage:** check for allOk and then access either outputs or errors.
 *
 * <p>
 * This class is intended purely as a data carrier (Data Transfer Object).
 * <ul>
 * <li>Fields are public for direct access; this DTO contains no behavior or
 * business logic.</li>
 * <li>Instances are treated as immutable by convention: - Inputs: do not modify
 * fields after receiving a DTO. - Outputs: do not reuse or modify a DTO once it
 * has been returned or sent to another system.</li>
 * <li>This approach favors simplicity and clarity over ceremony, relying on
 * team discipline and tooling to avoid unintended mutations.</li>
 * </ul>
 */

public final class CalcResultDS {
	private static final CalcErrorDS[] EMPTY_ERROR = {};
	/**
	 * true if the run succeeds. outputs map contains the calculated values
	 */
	public boolean allOk;
	/**
	 * to be accessed only if the run succeeds. allOk = true. could be null/empty if
	 * allOk=false
	 */
	public Map<String, IValue> outputs;
	/**
	 * to be used only if the run fails. could be empty/null if the run succeeds
	 */
	public CalcErrorDS[] errors;

	/**
	 * for the serializers
	 */
	public CalcResultDS() {
		//
	}

	/**
	 * On success
	 *
	 * @param outputs
	 */
	public CalcResultDS(Map<String, IValue> outputs) {

		this.allOk = true;
		this.outputs = outputs;
		this.errors = EMPTY_ERROR;
	}

	/**
	 * On failure
	 *
	 * @param errors
	 */
	public CalcResultDS(CalcErrorDS[] errors) {

		this.allOk = false;
		this.errors = errors;
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		if (this.allOk) {
			final int n = this.outputs == null ? 0 : this.outputs.size();
			return "Success: Calculated " + n + " fields.";
		}
		final int n = this.errors == null ? 0 : this.errors.length;
		return "Failed with " + n + " errors";
	}
}
