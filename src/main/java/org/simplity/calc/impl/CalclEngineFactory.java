package org.simplity.calc.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.api.ICalcEngine;
import org.simplity.calc.api.ICalcError;
import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.ValueType;
import org.simplity.calc.config.CalcConfig;

/**
 * The public factory for creating and bootstrapping a CalculatorEngine
 * instance.
 */
public class CalclEngineFactory {

	/**
	 * Creates an engine with the specified configuration. Configuration data is
	 * parsed and validated for any possible errors. In case of any errors, suitable
	 * messages are added to the errors list, and a null value is returned
	 *
	 * @param config
	 * @param customFunctions non-null, possibly empty
	 * @param errors          non-null list to which any errors are added. This
	 *                        happens if there are any errors in the config details.
	 * @return engine that loaded with the configuration. null if there are any
	 *         error in the configuration.
	 */
	public static ICalcEngine createEngine(CalcConfig config, Map<String, ICalcFunction> customFunctions,
			List<ICalcError> errors) {

		final Map<String, ICalcFunction> functions = new HashMap<>();

		functions.putAll(customFunctions);
		/*
		 * we are not handling name-conflicts. Any custom function with the name of a
		 * built-in function will be overridden
		 */
		BuiltinFunctions.getAll(functions);

		Context ctx = new Context(functions);
		ConfigParser.parse(config, ctx);

		final String[] arr = {};
		return new CalcEngine(ctx.varaibles, ctx.inputVariables.toArray(arr), ctx.outputVariables.toArray(arr));
	}

	private static class Context implements IParserContext {

		private final Map<String, ICalcFunction> functions;
		private final List<ICalcError> errors = new ArrayList<>();

		/*
		 * for holding the parsed components
		 */
		protected final Map<String, ValueType> valueTypes = new HashMap<>();
		protected final Map<String, IValidator> validators = new HashMap<>();

		protected final Map<String, Variable> varaibles = new HashMap<>();
		protected final Set<String> inputVariables = new HashSet<>();
		protected final Set<String> outputVariables = new HashSet<>();

		protected Context(Map<String, ICalcFunction> functions) {
			this.functions = functions;
		}

		@Override
		public void addValidator(String name, IValidator validator) {
			this.validators.put(name, validator);
		}

		@Override
		public IValidator getValidator(String name) {
			return this.validators.get(name);
		}

		@Override
		public void addVariable(Variable variable) {
			String name = variable.getName();
			this.varaibles.put(name, variable);
			if (variable.isInput()) {
				this.inputVariables.add(name);
			}
			if (variable.isOutput()) {
				this.outputVariables.add(name);
			}
		}

		@Override
		public void addValueType(String name, ValueType valueType) {
			this.valueTypes.put(name, valueType);
		}

		@Override
		public ValueType getValueType(String name) {
			return this.valueTypes.get(name);
		}

		@Override
		public void addInputName(String name) {
			this.inputVariables.add(name);
		}

		@Override
		public void addOutputName(String name) {
			this.outputVariables.add(name);
		}

		@Override
		public ICalcFunction getFunction(String functionName) {
			return this.functions.get(functionName);
		}

		@Override
		public Variable getVariable(String variableName) {
			return this.varaibles.get(variableName);
		}

		@Override
		public void logError(String message, String entityType, String entityName) {
			this.errors.add(new CalcError(entityType + ':' + entityName, message));
		}

//		@Override
//		public void resolve(String variableName) {
//			if (this.resolved.contains(variableName) || this.convicts.contains(variableName)) {
//				return;
//			}
//
//			if (this.pending.contains(variableName)) {
//				this.convicts.addAll(this.pending);
//				this.pending.clear();
//			}
//
//			// get the rule for this variable and resolve it
//
//		}

	}

}