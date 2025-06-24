package org.simplity.calc.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Responsible for creating required instance of IValue<?>
 */
public class ValueFactory {
	/**
	 *
	 * @param value
	 * @return non-null instance
	 */

	public static IValue newValue(BigDecimal value) {
		Objects.requireNonNull(value, "Value cannot be null.");
		return new NumberValue(value);
	}

	/**
	 *
	 * @param value
	 * @return non-null instance
	 */
	public static IValue newValue(boolean value) {
		return new BooleanValue(value);
	}

	/**
	 *
	 * @param value
	 * @return non-null instance
	 */
	public static IValue newValue(String value) {
		Objects.requireNonNull(value, "Value cannot be null.");
		return new StringValue(value);
	}

	/**
	 *
	 * @param value
	 * @return non-null instance
	 */
	public static IValue newValue(LocalDate value) {
		Objects.requireNonNull(value, "Value cannot be null.");
		return new DateValue(value);
	}

	private abstract static class Value implements IValue {

		protected final Object value;

		protected final ValueType valueType;

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
			throw new IllegalStateException("Cannot get a Boolean from a value of type " + this.valueType);
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
			// This also handles the case where obj is null
			if (!(obj instanceof Value)) {
				return false;
			}
			Value other = (Value) obj;
			return Objects.equals(this.value, other.value);
		}

		/**
		 * Returns the string representation of the underlying value.
		 *
		 * @return the string representation of the value.
		 */
		@Override
		public String toString() {
			return this.value.toString();
		}
	}

	private static class NumberValue extends Value {

		protected NumberValue(BigDecimal value) {
			super(value, ValueType.NUMBER);
		}

		@Override
		public BigDecimal getNumberValue() {
			return (BigDecimal) this.value;
		}

	}

	private static class StringValue extends Value {

		protected StringValue(String value) {
			super(value, ValueType.STRING);
		}

		@Override
		public String getStringValue() {
			return (String) this.value;
		}

	}

	private static class DateValue extends Value {

		protected DateValue(LocalDate value) {
			super(value, ValueType.DATE);
		}

		@Override
		public LocalDate getDateValue() {
			return (LocalDate) this.value;
		}

	}

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
