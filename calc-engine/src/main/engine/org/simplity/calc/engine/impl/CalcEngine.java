package org.simplity.calc.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcError;
import org.simplity.calc.engine.api.ICalcResult;
import org.simplity.calc.engine.api.IValue;

/**
 * The concrete implementation of the calculation engine.
 */
final class CalcEngine implements ICalcEngine {

	private final Map<String, Variable> variables;
	private final String[] inputs;
	private final String[] outputs;

	/**
	 *
	 * @param variables
	 * @param functions
	 * @param outputNames
	 */
	CalcEngine(Map<String, Variable> variables, String[] inputs, String[] outputs) {
		this.variables = variables;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	@Override
	public ICalcResult calculate(Map<String, String> inputValues) {
		CalcContext ctx = new CalcContext();
		Map<String, IValue> results = new HashMap<>();

		/**
		 * parse and cache all inputs
		 */
		for (String name : this.inputs) {
			Variable variable = this.variables.get(name);
			String inputValue = inputValues.get(name);
			IValue v = variable.parse(inputValue, ctx);
			if (v != null) {
				ctx.cacheValue(name, v);
			}
		}

		if (ctx.hasErrors()) {
			return CalcResult.failure(ctx.getErrors());
		}

		/**
		 * calculate each value
		 */
		for (String name : this.outputs) {
			IValue value = ctx.determineValue(name);
			if (value != null) {
				results.put(name, value);
			}
		}

		if (ctx.hasErrors()) {
			return CalcResult.failure(ctx.getErrors());
		}
		return CalcResult.success(results);
	}

	@Override
	public void shutdown() {
		/* No-op */ }

	/**
	 * The non-static inner class providing the context for a single calculation
	 * run.
	 */
	private class CalcContext implements ICalcContext {
		private static final ICalcError[] ARR = {};
		private Map<String, IValue> cache = new HashMap<>(CalcEngine.this.variables.size());
		private final Set<String> inProcess = new HashSet<>();
		private final List<ICalcError> errors = new ArrayList<>();

		protected CalcContext() {
		}

		@Override
		public IValue determineValue(String variableName) {
			IValue value = this.cache.get(variableName);
			if (value != null) {
				return value;
			}
			if (!this.inProcess.add(variableName)) {
				// As per our current design of boot-strapping, this should never happen.
				// Defensive code
				this.logError(variableName, "Circular dependency detected: ");
				return null;
			}

			Variable variable = CalcEngine.this.variables.get(variableName);
			if (variable == null) {
				// defensive code. as per the current bootstrap process, this should never
				// happen
				this.logError(variableName, "Varaible not defined.");
				return null;
			}

			value = variable.evaluate(this);
			this.inProcess.remove(variableName);
			if (value != null) {
				this.cache.put(variableName, value);
			}
			return value;
		}

		@Override
		public void logError(String variableName, String message) {
			this.errors.add(new CalcError(variableName, message));
		}

		@Override
		public boolean hasErrors() {
			return !this.errors.isEmpty();
		}

		@Override
		public ICalcError[] getErrors() {
			return this.errors.toArray(ARR);
		}

		@Override
		public void cacheValue(String valName, IValue value) {
			this.cache.put(valName, value);

		}

		@Override
		public boolean hasValue(String variableName) {
			return this.cache.containsKey(variableName);
		}
	}
}
