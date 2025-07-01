package org.simplity.calc.engine.api;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Defines the set of core data types supported by the calculation engine. Each
 * constant holds a reference to the corresponding Java class that represents
 * it.
 */
public enum ValueType {
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
	BOOLEAN(Boolean.class);

	private final Class<?> javaType;

	/**
	 * Associates a ValueType constant with its underlying Java class.
	 *
	 * @param javaType The corresponding Java class (e.g., BigDecimal.class).
	 */
	ValueType(Class<?> javaType) {
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