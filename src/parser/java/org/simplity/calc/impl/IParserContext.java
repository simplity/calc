package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.ValueType;
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
	 * add a variable name-valueType pair from a data-element. T
	 *
	 * @param name
	 * @param valueType
	 */
	void addValueType(String name, ValueType valueType);

	/**
	 * get the value type of a variable that is defined as a datElement
	 *
	 * @param name
	 * @return value type. null if the variable is not defined, or is not parsed yet
	 */

	ValueType getValueType(String name);

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
	 * add a variable name as an input variable
	 *
	 * @param name
	 */
	void addInputName(String name);

	/**
	 * add a variable name as an output variable
	 *
	 * @param name
	 */
	void addOutputName(String name);

	/**
	 *
	 * @param functionName
	 * @return null if no such function is defined in this context
	 */
	ICalcFunction getFunction(String functionName);
}
