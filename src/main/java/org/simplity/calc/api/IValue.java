package org.simplity.calc.api;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.simplity.calc.impl.ValueFactory;

/**
 * Represents a dynamically-typed, immutable value within the calculation
 * engine.
 * <p>
 * IValue is a container for one of the core data types supported by the engine.
 * The specific type is identified by the {@link ValueType} enum. This design
 * allows for handling mixed-type data in collections and function arguments in
 * a uniform way.
 *
 * <h3>Usage Pattern</h3> The primary way to interact with an {@code IValue} is
 * through its specific, type-safe accessors like {@link #getNumberValue()}.
 * These methods provide runtime safety by throwing an
 * {@link IllegalStateException} if the requested type does not match the actual
 * stored type. The {@link ValueFactory} guarantees that these accessors will
 * not return null (with the exception of {@code getBooleanValue} which returns
 * a primitive).
 *
 * <h3>Immutability and Thread Safety</h3> All implementations of this interface
 * are designed to be **immutable**. The value they hold is set at construction
 * time and can never be changed. As a result, all {@code IValue} objects are
 * inherently **thread-safe**.
 *
 * @author Simplity Technologies
 * @since 1.0
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
	 * specific accessors. It is strongly recommended to use the type-specific
	 * accessors instead.
	 *
	 * @return the underlying, non-null value object.
	 */
	Object getValue();

	/**
	 * Returns the value as a {@link BigDecimal}.
	 *
	 * @return the value as a non-null {@code BigDecimal}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#NUMBER}.
	 */
	BigDecimal getNumberValue();

	/**
	 * Returns the value as a {@link String}.
	 *
	 * @return the value as a non-null {@code String}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#STRING}.
	 */
	String getStringValue();

	/**
	 * Returns the value as a primitive {@code boolean}.
	 * <p>
	 * <b>Design Note:</b> This method returns a primitive {@code boolean} for
	 * ergonomic reasons, as it avoids the need for unboxing in client code. This
	 * implies that a boolean value within the engine can never be null.
	 *
	 * @return the value as a {@code boolean}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#BOOLEAN}.
	 */
	boolean getBooleanValue();

	/**
	 * Returns the value as a {@link LocalDate}.
	 *
	 * @return the value as a non-null {@code LocalDate}.
	 * @throws IllegalStateException if this value is not of type
	 *                               {@link ValueType#DATE}.
	 */
	LocalDate getDateValue();
}