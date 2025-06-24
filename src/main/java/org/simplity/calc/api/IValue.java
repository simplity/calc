package org.simplity.calc.api;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.simplity.calc.impl.ValueFactory;

/**
 * Represents a dynamically-typed, immutable value within the calculation
 * engine.
 * <p>
 * IValue is a container for one of the core data types supported by the engine,
 * such as numbers, strings, booleans, and dates. The specific type of the
 * contained value is identified by the {@link ValueType} enum. This design
 * allows for handling mixed-type data in collections and function arguments in
 * a uniform way.
 * <p>
 * This interface simulates a "discriminated union" or "sum type," a common
 * pattern for handling a closed set of possible types. Implementations of this
 * interface are expected to be **immutable**.
 *
 * <h3>Usage Pattern</h3> The primary way to interact with an {@code IValue} is
 * through its specific, type-safe accessors (e.g., {@link #getNumberValue()},
 * {@link #getStringValue()}). These methods provide runtime safety by throwing
 * an {@link IllegalStateException} if the requested type does not match the
 * actual stored type.
 *
 * <pre>{@code
 * IValue value = context.get("myVariable");
 * try {
 * 	BigDecimal number = value.getNumberValue();
 * // ... operate on the number
 * } catch (IllegalStateException e) {
 * // ... handle the case where the value was not a number
 * }
 * }</pre>
 *
 * While a generic {@link #getValue()} method exists, its use is discouraged as
 * it bypasses the safety mechanisms and requires manual, unsafe casting.
 *
 * @see ValueType
 * @see ValueFactory
 */
public interface IValue {

	/**
	 * Gets the specific data type of the value being held.
	 *
	 * @return the non-null {@link ValueType} enum constant representing the type of
	 *         this value.
	 */
	ValueType getType();

	/**
	 * Gets the underlying value as a raw {@link Object}.
	 * <p>
	 * <b>Warning:</b> This method bypasses the type-safety checks provided by the
	 * specific accessors. Callers are responsible for checking the
	 * {@link #getType()} and casting the result, which is error-prone. It is
	 * strongly recommended to use the type-specific accessors like
	 * {@link #getNumberValue()} instead.
	 *
	 * @return the underlying value object.
	 */
	Object getValue();

	/**
	 * Returns the value as a {@link BigDecimal}.
	 *
	 * @return the value as a {@code BigDecimal}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#NUMBER}.
	 */
	BigDecimal getNumberValue();

	/**
	 * Returns the value as a {@link String}.
	 *
	 * @return the value as a {@code String}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#STRING}.
	 */
	String getStringValue();

	/**
	 * Returns the value as a {@link boolean}.
	 *
	 * @return the value as a {@code boolean}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#BOOLEAN}.
	 */
	boolean getBooleanValue();

	/**
	 * Returns the value as a {@link LocalDate}.
	 *
	 * @return the value as a {@code LocalDate}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#DATE}.
	 */
	LocalDate getDateValue();

	// Add other accessors as needed, e.g., for lists, objects, etc.
}