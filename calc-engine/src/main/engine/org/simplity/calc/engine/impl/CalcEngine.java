package org.simplity.calc.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.CalcErrorDS;
import org.simplity.calc.engine.api.CalcResultDS;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.IValue;

/**
 * The concrete implementation of the calculation engine.
 */
class CalcEngine implements ICalcEngine {
	protected static final CalcErrorDS[] ARR = {};

	protected final Map<String, IVariable> variables;
	private final String[] inputs;
	private final String[] outputs;
	private final IValidator[] validators;
	// shared by the context
	protected final Map<String, String> messages;

	/**
	 *
	 * @param variables
	 * @param functions
	 * @param outputNames
	 */
	CalcEngine(Map<String, IVariable> variables, IValidator[] validators, Map<String, String> messages, String[] inputs,
			String[] outputs) {
		this.variables = variables;
		this.inputs = inputs;
		this.outputs = outputs;
		this.validators = validators;
		this.messages = messages;
	}

	@Override
	public CalcResultDS calculate(Map<String, String> inputValues) {
		CalcContext ctx = new CalcContext();
		Map<String, IValue> results = new HashMap<>();

		try {
			/**
			 * parse and cache all inputs
			 */
			for (String name : this.inputs) {
				IVariable variable = this.variables.get(name);
				String inputValue = inputValues.get(name);
				IValue v = variable.parse(inputValue, ctx);
				if (v != null) {
					ctx.cacheValue(name, v);
				}
			}

			if (ctx.hasErrors()) {
				return new CalcResultDS(ctx.getErrors());
			}

			/**
			 * inter-field validations?
			 */
			for (IValidator v : this.validators) {
				v.validate(ctx);
			}

			if (ctx.hasErrors()) {
				return new CalcResultDS(ctx.getErrors());
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
		} catch (Exception e) {
			e.printStackTrace();
			ctx.logError("",
					"Calculation Engine encountered an internal error. Our support team is looking at it. Please retry after some time");
		}

		if (ctx.hasErrors()) {
			return new CalcResultDS(ctx.getErrors());
		}
		return new CalcResultDS(results);
	}

	@Override
	public void shutdown() {
		/* No-op */ }

	/**
	 * The non-static inner class providing the context for a single calculation
	 * run.
	 */
	private class CalcContext implements ICalcContext {
		private Map<String, IValue> cache = new HashMap<>(CalcEngine.this.variables.size());
		private final Set<String> inProcess = new HashSet<>();
		private final List<CalcErrorDS> errors = new ArrayList<>();

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

			IVariable variable = CalcEngine.this.variables.get(variableName);
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
		public void logError(String variableName, String errorId) {
			this.errors.add(new CalcErrorDS(variableName, this.translate(errorId)));
		}

		@Override
		public boolean hasErrors() {
			return !this.errors.isEmpty();
		}

		protected CalcErrorDS[] getErrors() {
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

		private String translate(String messageId) {
			String msg = CalcEngine.this.messages.get(messageId);
			if (msg == null) {
				msg = messageId;
			}
			return msg;
		}
	}
}
