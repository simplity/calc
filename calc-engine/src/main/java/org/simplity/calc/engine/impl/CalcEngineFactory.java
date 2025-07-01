package org.simplity.calc.engine.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcError;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.config.CalcConfig;

/**
 * The public factory for creating and bootstrapping a ready-to-use
 * {@link ICalcEngine} instance from a configuration object.
 *
 * <h3>Bootstrap Process</h3> This factory orchestrates a comprehensive,
 * multi-stage process to ensure the resulting engine is valid and robust:
 * <ol>
 * <li><b>Configuration Parsing:</b> It parses the entire {@link CalcConfig}
 * object, creating internal representations of all data elements, schemas, and
 * rules.</li>
 * <li><b>Semantic Validation:</b> It validates the configuration for
 * correctness, such as ensuring all referenced variables and schemas are
 * defined.</li>
 * <li><b>Expression Compilation:</b> It compiles all expression strings into an
 * internal, executable {@code IExpression} tree using an ANTLR-based
 * parser.</li>
 * <li><b>Circular Dependency Detection:</b> It performs a "dry run" on all
 * calculations to detect any "catch-22" scenarios or infinite loops in the
 * dependency graph.</li>
 * </ol>
 * If any errors are found during this process, the factory will stop and return
 * {@code null}, having populated the provided error list with detailed
 * messages.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public final class CalcEngineFactory {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private CalcEngineFactory() {
		//
	}

	/**
	 * Creates a calculation engine with the specified configuration. The
	 * configuration data is parsed and validated for any possible errors.
	 *
	 * @param config          The non-null, root configuration object, typically
	 *                        deserialized from a JSON file.
	 * @param customFunctions A non-null, possibly empty map of custom functions to
	 *                        be added to the engine. Note that a custom function
	 *                        cannot override a built-in function name.
	 * @param errors          A non-null, empty list that will be populated with any
	 *                        and all errors found during the bootstrap process.
	 * @return A fully validated and ready-to-use {@link ICalcEngine} instance, or
	 *         {@code null} if any errors were found in the configuration.
	 */
	public static ICalcEngine createEngine(CalcConfig config, Map<String, ICalcFunction> customFunctions,
			List<ICalcError> errors) {
		return new Worker().produceEngine(config, customFunctions, errors);
	}

	/**
	 * Private worker class to encapsulate the state of a single bootstrap process.
	 */
	private static class Worker {
		private List<ICalcError> errors;
		private int existingErrors;

		/**
		 * Orchestrates the entire bootstrap process.
		 */
		protected ICalcEngine produceEngine(CalcConfig config, Map<String, ICalcFunction> customFunctions,
				List<ICalcError> errorCollector) {
			this.errors = errorCollector;
			this.existingErrors = errorCollector.size();
			final ParserContext ctx = new ParserContext(customFunctions);

			/*
			 * Parse all components from the config object and load them into the context.
			 */
			new ConfigParser(config, ctx).parse();

			/*
			 * If parsing the config itself produced errors, stop here.
			 */
			if (this.errors.size() > this.existingErrors) {
				return null;
			}

			/*
			 * Perform a dry run on all output variables to detect circular dependencies.
			 */
			final Map<String, Variable> variables = ctx.variables;
			IDryrunContext dryCtx = new DryContext(variables);
			for (String s : ctx.outputVariables) {
				variables.get(s).dryrun(dryCtx);
			}

			/*
			 * If the dry run found any circular dependencies, stop here.
			 */
			if (this.errors.size() > this.existingErrors) {
				return null;
			}

			/*
			 * If we get here, the configuration is valid and safe. Assemble the final
			 * engine instance.
			 */
			final String[] arr = {};
			final String[] inputs = ctx.inputVariables.toArray(arr);
			final String[] outputs = ctx.outputVariables.toArray(arr);
			return new CalcEngine(variables, inputs, outputs);
		}

		/**
		 * Implements IParserContext to serve as a state holder during parsing.
		 */
		private class ParserContext implements IParserContext {

			protected final Map<String, IValidator> validators = new HashMap<>();
			protected final Map<String, Variable> variables = new HashMap<>();
			protected final Set<String> inputVariables = new HashSet<>();
			protected final Set<String> outputVariables = new HashSet<>();
			protected final Map<String, ICalcFunction> functions = new HashMap<>();

			/**
			 * Initializes the context and function registry.
			 */
			protected ParserContext(Map<String, ICalcFunction> customFunctions) {
				/*
				 * NOTE: we want to be case-insensitive for matching the functions, especially
				 * because it is going to come from an external source Builtin functions are all
				 * indexed by lower-case name.
				 */
				BuiltinFunctions.getAll(this.functions);

				// Load custom functions, checking for name clashes.
				for (Map.Entry<String, ICalcFunction> entry : customFunctions.entrySet()) {
					String name = entry.getKey().toLowerCase();
					if (this.functions.containsKey(name)) {
						this.logError("Function name '" + name
								+ "' is a built-in name and cannot be overridden. Note that the function names are case-insensitive",
								"function", name);
					} else {
						this.functions.put(name, entry.getValue());
					}
				}
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
				this.variables.put(name, variable);
				if (variable.isInput()) {
					this.inputVariables.add(name);
				}
				if (variable.isOutput()) {
					this.outputVariables.add(name);
				}
			}

			@Override
			public ICalcFunction getFunction(String functionName) {
				return this.functions.get(functionName.toLowerCase());
			}

			@Override
			public Variable getVariable(String variableName) {
				return this.variables.get(variableName);
			}

			@Override
			public void logError(String message, String entityType, String entityName) {
				Worker.this.errors.add(new CalcError(entityType + ':' + entityName, message));
			}
		}

		/**
		 * Implements IDryrunContext to detect circular dependencies ("catch-22s").
		 */
		private class DryContext implements IDryrunContext {
			private final Map<String, Variable> variables;

			private final Set<String> clearedOnes = new HashSet<>();
			private final Set<String> faildOens = new HashSet<>();
			// Stack to track the current dependency path
			private final LinkedHashSet<String> beingEvaluated = new LinkedHashSet<>();

			/**
			 * Initializes the dry-run context.
			 */
			protected DryContext(Map<String, Variable> variables) {
				this.variables = variables;
				// Pre-populate the cache with required inputs, as they are always evaluatable.
				for (Variable variable : variables.values()) {
					if (variable.isRequiredInput()) {
						this.clearedOnes.add(variable.getName());
					}
				}
			}

			@Override
			public boolean isEvaluatable(String variableName) {
				if (this.clearedOnes.contains(variableName)) {
					return true;
				}

				if (this.faildOens.contains(variableName)) {
					return false;
				}

				// This is the core cycle detection logic.
				if (this.beingEvaluated.contains(variableName)) {
					this.reportError(variableName);
					this.faildOens.addAll(this.beingEvaluated);
					this.beingEvaluated.clear();
					return false;
				}

				Variable variable = this.variables.get(variableName);
				// A variable can be null if it's an optional input that is not provided.
				if (variable == null) {
					this.faildOens.add(variableName);
					return false;
				}

				this.beingEvaluated.add(variableName);
				boolean isOk = variable.dryrun(this);
				// IMPORTANT: remove from path whether it succeeds or fails.
				this.beingEvaluated.remove(variableName);

				if (isOk) {
					this.clearedOnes.add(variableName);
				} else {
					this.faildOens.add(variableName);
				}
				return isOk;
			}

			/**
			 * Formats and logs a circular dependency error message.
			 */
			private void reportError(String finalLink) {
				StringBuilder sb = new StringBuilder("Circular dependency detected: ");
				for (String s : this.beingEvaluated) {
					sb.append(s).append(" -> ");
				}
				sb.append(finalLink);
				Worker.this.errors.add(new CalcError("dataElement:" + finalLink, sb.toString()));
			}
		}
	}
}