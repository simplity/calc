package org.simplity.calc.config;

/**
 * A flexible POJO representing the set of all possible schema constraints. This
 * object is used to create a specific, efficient IValueValidator instance
 * during bootstrap.
 */
public class ValueSchema {
	/**
	 * type of value for which this schema is to be applied Relevance of other
	 * attribute depends on the value type
	 */
	public String valueType;
	// --- For NUMBER ---
	/**
	 * The number of decimal places to round to 0.
	 */
	public int nbrDecimalPlaces = 0;

	/**
	 * The inclusive minimum value allowed for a number.
	 */
	public Double min;
	/**
	 * The inclusive maximum value allowed for a number.
	 */
	public Double max;

	// --- For STRING ---
	/**
	 * The minimum character length for a string.
	 */
	public Integer minLength;
	/**
	 * The maximum character length for a string.
	 */
	public Integer maxLength;
	/**
	 * A valid Java regular expression that the string value must match.
	 */
	public String regex;

	// --- For DATE ---
	/**
	 * The maximum number of days in the past a date can be, relative to the
	 * execution date.
	 */
	public Integer daysInPast;
	/**
	 * The maximum number of days in the future a date can be, relative to the
	 * execution date.
	 */
	public Integer daysInFuture;
}
