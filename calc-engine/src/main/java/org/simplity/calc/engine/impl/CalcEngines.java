package org.simplity.calc.engine.impl;

import java.util.Map;

import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEngineShipment;
import org.simplity.calc.engine.config.CalcConfigDS;

/**
 * The public factory for creating and bootstrapping a ready-to-use
 * {@link ICalcEngine} instance from a configuration object.
 *
 * <h3>Bootstrap Process</h3> This factory orchestrates a comprehensive,
 * multi-stage process to ensure the resulting engine is valid and robust:
 * <ol>
 * <li><b>Configuration Parsing:</b> It parses the entire {@link CalcConfigDS}
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
public final class CalcEngines {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private CalcEngines() {
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
	 * @return A fully validated and ready-to-use {@link ICalcEngine} instance, or
	 *         {@code null} if any errors were found in the configuration.
	 */
	public static IEngineShipment newEngine(CalcConfigDS config, Map<String, ICalcFunction> customFunctions) {
		return new EngineBuilder(config, customFunctions).build();
	}
}