package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.config.ValueSchema;

/**
 * Context that provides utilities for the parsing process and accumulates
 * parsed components to provide required reference
 */
interface IParserContext {

	void logError(String message, String entityType, String entityName);

	/**
	 * validators are created by parsing {@link ValueSchema} into an instance of
	 * {@link IValidator} interface
	 *
	 * @param name
	 * @param validator
	 */
	void addValidator(String name, IValidator validator);

	/**
	 *
	 * @param name
	 * @return null if no such schema name
	 */
	IValidator getValidator(String name);

	/**
	 * add a variable that is fully parsed from a dataElement
	 *
	 * @param variable
	 */
	void addVariable(Variable variable);

	/**
	 *
	 * @param variableName
	 * @return variable instance. null if no such dataElement exists, or it is not
	 *         parsed yet
	 */
	Variable getVariable(String variableName);

	/**
	 *
	 * @param functionName
	 * @return null if no such function is defined in this context
	 */
	ICalcFunction getFunction(String functionName);
}
