package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.ValueType;
import org.simplity.calc.config.ValueSchema;

/**
 * context that is shared with all the components at the time of the bootstrap
 * process
 */
interface IBootstrapContext {

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
	 * accumulate a variable name that is valid in this context
	 *
	 * @param name
	 * @param valueType
	 */
	void addValueType(String name, ValueType valueType);

	ValueType getValueType(String name);

	void addVariable(Variable variable);

	/**
	 * Indicate that a value would be needed for this variable at this stage of the
	 * dry-run
	 *
	 * @param variableName
	 */
	void resolve(String variableName);

	void addInputName(String name);

	void addOutputName(String name);

	ICalcFunction getFunction(String functionName);

	Variable getVariable(String variableName);
}
