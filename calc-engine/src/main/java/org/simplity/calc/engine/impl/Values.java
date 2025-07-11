
package org.simplity.calc.engine.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

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
public final class Values {

	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private Values() {
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
	 * Creates a new {@code IValue} of type {@code TIMESTAMP}.
	 *
	 * @param value The value to wrap. Cannot be null.
	 * @return a non-null {@code IValue} instance representing the date.
	 */
	public static IValue newValue(Instant value) {
		Objects.requireNonNull(value, "A non-null LocalDate value is required.");
		return new TimestampValue(value);
	}

	/**
	 * Creates a new {@code IValue} of type {@code ENUMERATED}.
	 *
	 * @param enumName  name of the enumerated list e.g. "state_code"
	 * @param enumValue value e.g. "CA"
	 * @return a non-null {@code IValue} instance representing the date.
	 */
	public static IValue newEnumeratedValue(String enumName, String enumValue) {
		Objects.requireNonNull(enumName, "A non-null enumName value is required.");
		Objects.requireNonNull(enumValue, "A non-null enumValue value is required.");
		return new EnumeratedValue(enumName, enumValue);
	}

	/**
	 * Creates a new {@code IValue} of the specified type. This is intended for
	 * utility functions that may just want an {@code IValue} instance of a specific
	 * type. The default values or 0, empty-string, false and
	 * {@code LocalDate.now()}
	 *
	 * @param valueType non-null.
	 * @return a non-null {@code IValue} instance. Null for non-primitive types
	 */
	public static IValue newDefaultValue(DataType valueType) {
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
			return null;
		}
	}

	/**
	 * A private, abstract base class providing a skeletal implementation of the
	 * {@link IValue} interface. It provides the common, final implementations of
	 * {@code equals()}, {@code hashCode()}, {@code toString()}, and the default
	 * "throwing" behavior for incorrect type accessors, ensuring consistency across
	 * all value types.
	 */
	private abstract static class Value<T> implements IValue {

		/** The underlying, non-null value object. */
		protected final T value;

		/** The corresponding, non-null value type. */
		protected final IValueType valueType;

		/**
		 * Constructs the base Value.
		 *
		 * @param value     The non-null value object.
		 * @param valueType The non-null type enum.
		 */
		Value(T value, IValueType valueType) {
			this.value = value;
			this.valueType = valueType;
		}

		@Override
		public final IValueType getValueType() {
			return this.valueType;
		}

		@Override
		public final Object getValue() {
			return this.value;
		}

		@Override
		public BigDecimal getNumberValue() {
			throw new IllegalStateException(this.getMessage("number"));
		}

		@Override
		public String getStringValue() {
			throw new IllegalStateException(this.getMessage("String"));
		}

		@Override
		public boolean getBooleanValue() {
			throw new IllegalStateException(this.getMessage("boolean"));
		}

		@Override
		public LocalDate getDateValue() {
			throw new IllegalStateException(this.getMessage("LocalDate"));
		}

		@Override
		public Instant getTimestampValue() {
			throw new IllegalStateException(this.getMessage("LocalDate"));
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
			if (!(obj instanceof Value<?>)) {
				return false;
			}
			Value<?> other = (Value<?>) obj;
			if (this.getValueType().equals(other.getValueType()) == false) {
				return false;
			}

			return Objects.equals(this.value, other.value);
		}

		@Override
		public String toString() {
			return this.value.toString();
		}

		@Override
		public int compareTo(IValue otherValue) {
			Objects.requireNonNull(otherValue, "A non-null value is required.");

			if (otherValue.getValueType() != this.valueType) {
				throw new IllegalArgumentException(this.canNotCompare(otherValue.getValueType()));
			}
			return this.doCompare(otherValue);
		}

		/**
		 * concrete classes should override if the comparison is valid
		 *
		 * @param otherValue
		 * @return
		 */
		protected int doCompare(IValue otherValue) {
			throw new IllegalArgumentException("Values of type '" + this.valueType + "' can not be compared");
		}

		private String canNotCompare(IValueType type) {
			return "Value of type " + this.valueType + " can not be campared with another value of type " + type;
		}

		private String getMessage(String type) {
			return "Invalid operation: Can not get a " + type + " from a value of type " + this.valueType;
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the NUMBER type.
	 */
	private static class NumberValue extends Value<BigDecimal> {
		protected NumberValue(BigDecimal value) {
			super(value, ValueTypes.NUMBER);
		}

		@Override
		public BigDecimal getNumberValue() {
			return this.value;
		}

		@Override
		protected int doCompare(IValue otherValue) {
			return this.value.compareTo(otherValue.getNumberValue());
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the STRING type.
	 */
	private static class StringValue extends Value<String> {
		protected StringValue(String value) {
			super(value, ValueTypes.STRING);
		}

		@Override
		public String getStringValue() {
			return this.value;
		}

		@Override
		protected int doCompare(IValue otherValue) {
			return this.value.compareTo(otherValue.getStringValue());
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the STRING type.
	 */
	private static class TimestampValue extends Value<Instant> {
		protected TimestampValue(Instant value) {
			super(value, ValueTypes.TIMESTAMP);
		}

		@Override
		public Instant getTimestampValue() {
			return this.value;
		}

		@Override
		protected int doCompare(IValue otherValue) {
			return this.value.compareTo(otherValue.getTimestampValue());
		}
	}

	/** An immutable, package-private implementation of IValue for the DATE type. */
	private static class DateValue extends Value<LocalDate> {
		protected DateValue(LocalDate value) {
			super(value, ValueTypes.DATE);
		}

		@Override
		public LocalDate getDateValue() {
			return this.value;
		}

		@Override
		protected int doCompare(IValue otherValue) {
			return this.value.compareTo(otherValue.getDateValue());
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the BOOLEAN type.
	 */
	private static class BooleanValue extends Value<Boolean> {
		protected BooleanValue(boolean value) {
			super(Boolean.valueOf(value), ValueTypes.BOOLEAN);
		}

		@Override
		public boolean getBooleanValue() {
			return this.value.booleanValue();
		}
	}

	/**
	 * An immutable, package-private implementation of IValue for the BOOLEAN type.
	 */
	private static class EnumeratedValue extends Value<String> {
		protected EnumeratedValue(String enumName, String enumValue) {
			super(enumValue, ValueTypes.newDataStructureType(enumName));
		}

		// this valueType can only be checked for equality. No other operation is valid
	}
}