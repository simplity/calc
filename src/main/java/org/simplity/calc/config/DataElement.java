package org.simplity.calc.config;

import org.simplity.calc.api.ValueType;

/**
 * Defines a single, named piece of data within the engine's data dictionary.
 * Each data element has a defined type and a rule for how its value is
 * determined.
 */
public class DataElement {

	/**
	 * The role of this data element in the calculation process. Must be one of the
	 * values from the {@link ElementType} enumeration. (e.g. "REQUIRED_INPUT",
	 * "OPTIONAL_INPUT", "OUTPUT", "INTERMEDIATE"
	 */
	public String type;

	/**
	 * The data type of this element's value.
	 * <p>
	 * <b>Note:</b> This value must match one of the names from the
	 * {@link ValueType} enum (e.g., "NUMBER", "BOOLEAN").
	 */
	public String valueType;

	/**
	 * Relevant only if {@link #type} is {@link ElementType#OPTIONAL_INPUT} or
	 * {@link ElementType#REQUIRED_INPUT}. This is the key to the
	 * {@link CalcConfig#schemas} map for validating the input value. * @see
	 * ValueSchema
	 */
	public String schemaName;

	/**
	 * This defines the set of expressions used to calculate the value of this data
	 * element.
	 * <p>
	 * Relevant only if {@link #type} is not {@link ElementType#REQUIRED_INPUT}. It
	 * is also ignore if {@link #type} is {@link ElementType#OPTIONAL_INPUT} and no
	 * value is input.
	 *
	 * @see CalcRule
	 */
	public CalcRule rule;

	/**
	 * Defines the number of decimal places to which this number is to be rounded.
	 * Relevant only for {@code ValueType.NUMBER}. This setting is primarily used
	 * for formatting {@code intermediate} and {@code output} values and will
	 * override any schema-defined settings.
	 */
	public int nbrDecimalPlaces;
}