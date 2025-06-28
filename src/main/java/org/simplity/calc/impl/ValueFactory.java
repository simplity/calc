
package org.simplity.calc.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * A factory for creating instances of {@link IValue}.
 *
 * <h3>Design Pattern</h3> This class employs the **Factory Pattern** to
 * decouple the client from concrete implementations. It also uses **private
 * static nested classes** to achieve a high degree of encapsulation, making it
 * impossible to instantiate value objects directly or create them in an invalid
 * state.
 *
 * <h3>Thread Safety</h3> This factory is stateless and all its methods are
 * thread-safe. The {@link IValue} objects it produces are immutable and
 * therefore also thread-safe.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public final class ValueFactory {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private ValueFactory() {
		// This class is not meant to be instantiated.
	}

	/**
	 * Creates a new {@code IValue} of type {@code NUMBER}.
	 *
	 * @param value The value to wrap. Cannot be null.
	 * @return a non-null {@code IValue} instance representing the number.
	 */
	public static IValue newValue(BigDecimal value) {
		Objects.requireNonNull(value, "A non-null BigDecimal value is required.");
		return new NumberValue(value);
	}

	/**
	 * Creates a new {@code IValue} of type {@code BOOLEAN}.
	 *
	 * @param value The primitive boolean value to wrap.
	 * @return a non-null {@code IValue} instance representing the boolean.
	 */
	public static IValue newValue(boolean value) {
		return new BooleanValue(value);
	}

	/**
	 * Creates a new {@code IValue} of type {@code STRING}.
	 *
	 * @param value The value to wrap. Cannot be null.
	 * @return a non-null {@code IValue} instance representing the string.
	 */
	public static IValue newValue(String value) {
		Objects.requireNonNull(value, "A non-null String value is required.");
		return new StringValue(value);
	}

	/**
	 * Creates a new {@code IValue} of type {@code DATE}.
	 *
	 * @param value The value to wrap. Cannot be null.
	 * @return a non-null {@code IValue} instance representing the date.
	 */
	public static IValue newValue(LocalDate value) {
		Objects.requireNonNull(value, "A non-null LocalDate value is required.");
		return new DateValue(value);
	}

	/**
	 * Creates a new {@code IValue} of the specified type. This is intended for
	 * utility functions that may just want an {@code IValue} instance of a specific
	 * type. The default values or 0, empty-string, false and
	 * {@code LocalDate.now()}
	 *
	 * @param valueType non-null.
	 * @return a non-null {@code IValue} instance.
	 */
	public static IValue newDefaultValue(ValueType valueType) {
		Objects.requireNonNull(valueType, "A non-null ValueType value is required.");
		switch (valueType) {
		case NUMBER:
			return new NumberValue(BigDecimal.valueOf(0));
		case BOOLEAN:
			return new BooleanValue(false);
		case DATE:
			return new DateValue(LocalDate.now());
		case STRING:
			return new StringValue("");

		default:
			throw new IllegalArgumentException("Unexpected value: " + valueType);
		}
	}

	/**
	 * A private, abstract base class providing a skeletal implementation of the
	 * {@link IValue} interface. It provides the common, final implementations of
	 * {@code equals()}, {@code hashCode()}, {@code toString()}, and the default
	 * "throwing" behavior for incorrect type accessors, ensuring consistency across
	 * all value types.
	 */
	private abstract static class Value implements IValue {

		/** The underlying, non-null value object. */
		protected final Object value;

		/** The corresponding, non-null value type. */
		protected final ValueType valueType;

		/**
		 * Constructs the base Value.
		 *
		 * @param value     The non-null value object.
		 * @param valueType The non-null type enum.
		 */
		Value(Object value, ValueType valueType) {
			this.value = value;
			this.valueType = valueType;
		}

		@Override
		public final ValueType getType() {
			return this.valueType;
		}

		@Override
		public final Object getValue() {
			return this.value;
		}

		@Override
		public BigDecimal getNumberValue() {
			throw new IllegalStateException("Cannot get a BigDecimal from a value of type " + this.valueType);
		}

		@Override
		public String getStringValue() {
			throw new IllegalStateException("Cannot get a String from a value of type " + this.valueType);
		}

		@Override
		public boolean getBooleanValue() {
			throw new IllegalStateException("Cannot get a boolean from a value of type " + this.valueType);
		}

		@Override
		public LocalDate getDateValue() {
			throw new IllegalStateException("Cannot get a LocalDate from a value of type " + this.valueType);
		}

		@Override
		public final int hashCode() {
			return Objects.hash(this.value);
		}

		@Override
		public final boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Value)) {
				return false;
			}
			Value other = (Value) obj;
			return Objects.equals(this.value, other.value);
		}

		@Override
		public String toString() {
			return this.value.toString();
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the NUMBER type.
	 */
	private static class NumberValue extends Value {
		protected NumberValue(BigDecimal value) {
			super(value, ValueType.NUMBER);
		}

		@Override
		public BigDecimal getNumberValue() {
			return (BigDecimal) this.value;
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the STRING type.
	 */
	private static class StringValue extends Value {
		protected StringValue(String value) {
			super(value, ValueType.STRING);
		}

		@Override
		public String getStringValue() {
			return (String) this.value;
		}
	}

	/** An immutable, package-private implementation of IValue for the DATE type. */
	private static class DateValue extends Value {
		protected DateValue(LocalDate value) {
			super(value, ValueType.DATE);
		}

		@Override
		public LocalDate getDateValue() {
			return (LocalDate) this.value;
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the BOOLEAN type.
	 */
	private static class BooleanValue extends Value {
		protected BooleanValue(boolean value) {
			super(Boolean.valueOf(value), ValueType.BOOLEAN);
		}

		@Override
		public boolean getBooleanValue() {
			return ((Boolean) this.value).booleanValue();
		}
	}
}