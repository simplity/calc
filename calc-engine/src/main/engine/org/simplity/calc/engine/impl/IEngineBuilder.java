package org.simplity.calc.engine.impl;

import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.config.ValueSchemaDS;

/**
 * Context that provides utilities for the parsing process and accumulates
 * parsed components to provide required reference
 */
interface IEngineBuilder {

	void logError(String message, String entityType, String entityName);

	/**
	 * validators are created by parsing {@link ValueSchemaDS} into an instance of
	 * {@link IValueParser} interface
	 *
	 * @param name
	 * @param validator
	 */
	void addValidator(String name, IValueParser validator);

	/**
	 *
	 * @param name
	 * @return null if no such schema name
	 */
	IValueParser getValidator(String name);

	/**
	 * add a variable that is fully parsed from a dataElement
	 *
	 * @param variable
	 */
	void addVariable(IVariable variable);

	/**
	 *
	 * @param variableName
	 * @return variable instance. null if no such dataElement exists, or it is not
	 *         parsed yet
	 */
	IVariable getVariable(String variableName);

	/**
	 *
	 * @param functionName
	 * @return null if no such function is defined in this context
	 */
	ICalcFunction getFunction(String functionName);

	/**
	 *
	 * @param enumName
	 * @return null if no such instance.
	 */
	Set<String> getEnumValues(String enumName);

	/**
	 *
	 * @param name
	 * @return null if no such instance.
	 */
	Map<String, String> getDataStructureDS(String name);

	/**
	 *
	 * @param name
	 * @return null if no such instance.
	 */
	Map<String, String> getTableDS(String name);
}
