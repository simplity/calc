package org.simplity.calc.config;

/**
 * Represents the definition of a single data element within the data
 * dictionary.
 */
public class DataElement {

	/**
	 * valid types
	 */
	public static final String[] TYPES = { "input", "output", "intermediate" };
	/**
	 * The type of this data element. Must be one of "input", "intermediate", or
	 * "output".
	 */
	public String type;

	/**
	 * The value type of this data element. Must match one of the
	 * {@link org.simplity.calc.api.ValueType} enum names.
	 */
	public String valueType;

	/**
	 * For INPUT types only. If true, this element must be present in the input
	 * payload of a calculate() call. Defaults to false if not specified.
	 */
	public boolean isRequired;

	/**
	 * For INPUT types only. The name of the schema (from the top-level schemas map)
	 * to use for validating this element.
	 */
	public String schemaName;

	/**
	 * For INTERMEDIATE and OUTPUT types only. The rule used to calculate the value
	 * of this data element.
	 */
	public CalcRule rule;
	/**
	 * Number of decimal places to which this number is to be rounded to. Relevant
	 * only for NUMBER types. Default is zero, implying an integral value
	 */

	public int nbrDecimalPlaces;
}
