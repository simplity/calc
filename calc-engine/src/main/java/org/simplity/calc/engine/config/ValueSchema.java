package org.simplity.calc.engine.config;

import org.simplity.calc.engine.api.ValueType;

/**
 * A flexible container for all possible validation constraints that can be
 * applied to a value.
 * <p>
 * This class is designed to be easily deserialized from JSON. The engine uses a
 * ValueSchema definition to create a specific, efficient validator instance
 * during the bootstrap process. It is primarily used for validating input data
 * elements.
 */
public class ValueSchema {
	/**
	 * The data type for which this schema is to be applied. The relevance of other
	 * attributes depends on this type.
	 * <p>
	 * <b>Note:</b> This value must match one of the names from the
	 * {@link ValueType} enum (e.g., "NUMBER", "STRING").
	 */
	public String valueType;

	// --- For NUMBER ---
	/**
	 * The number of decimal places allowed. Relevant only for
	 * {@code ValueType.NUMBER}.
	 */
	public int nbrDecimalPlaces = 0;

	/**
	 * The inclusive minimum value allowed. Relevant only for
	 * {@code ValueType.NUMBER}.
	 */
	public Double min;

	/**
	 * The inclusive maximum value allowed. Relevant only for
	 * {@code ValueType.NUMBER}.
	 */
	public Double max;

	// --- For STRING ---
	/**
	 * The minimum character length required. Relevant only for
	 * {@code ValueType.STRING}.
	 */
	public Integer minLength;

	/**
	 * The maximum character length allowed. Relevant only for
	 * {@code ValueType.STRING}.
	 */
	public Integer maxLength;

	/**
	 * A valid Java regular expression that the value must match. Relevant only for
	 * {@code ValueType.STRING}.
	 */
	public String regex;

	// --- For DATE ---
	/**
	 * The maximum number of days in the past a date can be, relative to the
	 * execution date. Relevant only for {@code ValueType.DATE}.
	 */
	public Integer daysInPast;

	/**
	 * The maximum number of days in the future a date can be, relative to the
	 * execution date. Relevant only for {@code ValueType.DATE}.
	 */
	public Integer daysInFuture;
}