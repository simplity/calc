package org.simplity.calc.engine.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Defines the set of core data types supported by the calculation engine. Each
 * constant holds a reference to the corresponding Java class that represents
 * it.
 */
public enum DataType {
	/**
	 * Numeric value is represented as {@link BigDecimal}
	 */
	NUMBER(BigDecimal.class),
	/**
	 * string/text value is represented as {@link String}
	 */
	STRING(String.class),
	/**
	 * Date value is represented as {@link LocalDate}
	 */
	DATE(LocalDate.class),
	/**
	 * Boolean value is represented as {@link Boolean}
	 */
	BOOLEAN(Boolean.class),
	/**
	 * Date with time in a specific time zone. It is represented as {@link Instant}
	 */
	TIMESTAMP(Instant.class),
	/**
	 * Indicates the value should be one of the enumerated values for that specific
	 * data element e.g. one of the specified state-code
	 */
	ENUM(Object.class),
	/**
	 * this is a data structure that has its data-members. Each data-member in turn
	 * can be of any data-type
	 */
	DS(Object.class),
	/**
	 * Represents a tabular data of primitive values
	 */
	TABLE(Object.class);

	private final Class<?> javaType;

	/**
	 * Associates a ValueType constant with its underlying Java class.
	 *
	 * @param javaType The corresponding Java class (e.g., BigDecimal.class).
	 */
	DataType(Class<?> javaType) {
		this.javaType = javaType;
	}

	/**
	 * Gets the underlying Java {@link Class} associated with this value type. This
	 * provides a programmatic link between the enum and the actual data type.
	 *
	 * @return The {@code Class} object for this type.
	 */
	public Class<?> getJavaType() {
		return this.javaType;
	}
}