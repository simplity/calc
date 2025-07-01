
package org.simplity.calc.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.simplity.calc.engine.api.ICalcError;
import org.simplity.calc.engine.api.ICalcResult;
import org.simplity.calc.engine.api.IValue;

/**
 * The concrete, immutable implementation of ICalcResult.
 */
final class CalcResult implements ICalcResult {
	private final Map<String, IValue> outputs;
	private final ICalcError[] errors;

	private CalcResult(Map<String, IValue> outputs, ICalcError[] errors) {
		this.outputs = outputs;
		this.errors = errors;
	}

	public static ICalcResult success(Map<String, IValue> outputs) {
		return new CalcResult(outputs, new ICalcError[] {});
	}

	public static ICalcResult failure(ICalcError[] errors) {
		return new CalcResult(new HashMap<>(), errors);
	}

	@Override
	public boolean isSuccess() {
		return this.errors.length == 0;
	}

	@Override
	public Map<String, IValue> getOutputs() {
		return this.outputs;
	}

	@Override
	public ICalcError[] getErrors() {
		return this.errors;
	}
}
