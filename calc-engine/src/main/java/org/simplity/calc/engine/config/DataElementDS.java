package org.simplity.calc.engine.config;

import org.simplity.calc.engine.api.DataType;

/**
 * Defines a single, named piece of data within the engine's data dictionary.
 * Each data element has a defined type and a rule for how its value is
 * determined.
 */
public class DataElementDS {

	/**
	 * The role of this data element in the calculation process. Must be one of the
	 * values from the {@link ElementType} enumeration. (e.g. "REQUIRED_INPUT",
	 * "OPTIONAL_INPUT", "OUTPUT", "INTERMEDIATE"
	 */
	public String type;

	/**
	 * The data type of this element's value.
	 * <p>
	 * <b>Note:</b> This value must match one of the names from the {@link DataType}
	 * enum (e.g., "NUMBER", "BOOLEAN"). For ENUM, the format to use is
	 * "ENUM:enum-name" where enum-name is the name under which the enumeration is
	 * indexed in enumerations collection. Likewise, "DS:ds-name",
	 * "TABLE:table-name" etc..
	 */
	public String dataType;

	/**
	 * Relevant only if {@link #type} is {@link ElementType#OPTIONAL_INPUT} or
	 * {@link ElementType#REQUIRED_INPUT}. This is the key to the
	 * {@link CalcConfigDS#schemas} map for validating the input value.
	 *
	 * @see ValueSchemaDS
	 */
	public String schemaName;

	/**
	 * Way to validate an input value in addition to the schema based validation.
	 * This features allows validation based on other field values.
	 * <p>
	 * Relevant only if {@link #type} is not {@link ElementType#REQUIRED_INPUT}. It
	 * is also ignored if {@link #type} is {@link ElementType#OPTIONAL_INPUT} and a
	 * value is input.
	 *
	 * @see CalculatorDS
	 */
	public ValidatorDS validator;

	/**
	 * Id of the message to be used when this data elements fails validation.
	 * Defaults to "invalid_\<name\>". This design allows externalization of error
	 * messages and mault-lingual (i18n) features
	 */
	public String errorId;

	/**
	 * This defines the set of expressions used to calculate the value of this data
	 * element.
	 * <p>
	 * Relevant only if {@link #type} is not {@link ElementType#REQUIRED_INPUT}. It
	 * is also ignored if {@link #type} is {@link ElementType#OPTIONAL_INPUT} and a
	 * value is input.
	 *
	 * @see CalculatorDS
	 */
	public CalculatorDS calculator;

	/**
	 * Defines the number of decimal places to which this number is to be rounded.
	 * Relevant only for {@code ValueType.NUMBER}. This setting is primarily used
	 * for formatting {@code intermediate} and {@code output} values and will
	 * override any schema-defined settings.
	 */
	public int nbrDecimalPlaces;

}