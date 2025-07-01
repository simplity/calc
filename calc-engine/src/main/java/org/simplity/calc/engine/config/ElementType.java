package org.simplity.calc.engine.config;

/**
 * Types of data elements used in a calculation process
 */
public enum ElementType {
	/**
	 * value is expected as an input.
	 */
	REQUIRED_INPUT,
	/**
	 * may or may not be input. A rule may be specified to calculate this in case
	 * the value is not provides as an input.
	 */
	OPTIONAL_INPUT,
	/**
	 * calculation process is expected to calculate this data element
	 */
	OUTPUT,
	/**
	 * intermediate variables are created as part of the rules/expressions with the
	 * purpose of ultimately calculating the output fields
	 */
	INTERMEDIATE

}
