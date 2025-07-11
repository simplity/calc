package org.simplity.calc.engine.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.config.ValueSchemaDS;

/**
 * Utility class to create IValueParser instance from a ValueSchema
 */
class ValueParsers {
	/**
	 * Boolean value can only be "true" or "false" No flexibility is provided. Hence
	 * a singleton is used.
	 */
	public static IValueParser BOOLEAN_VALIDATOR = new BooleanParser();

	private static final String SCHEMA = "schema";

	private ValueParsers() {
		// not to be instantiated
	}

	/**
	 * validate the schema and build an instance of IValueParser for the schema.
	 *
	 * @param schema        input
	 * @param name          to be used for logging any error in this schema
	 * @param engineBuilder used for logging any error encountered
	 * @return IValueParser instance if all OK.
	 */
	static IValueParser buildParser(ValueSchemaDS schema, String name, IEngineBuilder engineBuilder) {

		if (schema.valueType == null) {
			engineBuilder.logError("Schema  must specify a 'valueType' attribute.", SCHEMA, name);
			return null;
		}

		DataType type = null;
		try {
			type = DataType.valueOf(schema.valueType.toUpperCase());
		} catch (Exception e) {
			engineBuilder.logError("value type '" + schema.valueType + "' is not valid.", SCHEMA, name);
			return null;
		}

		switch (type) {
		case BOOLEAN:
			return BOOLEAN_VALIDATOR;

		case NUMBER:
			if (schema.min == null || schema.max == null) {
				engineBuilder.logError("Number Schema  must specify 'min' and 'max' values.", SCHEMA, name);
				return null;
			}
			return new NumberParser(schema.nbrDecimalPlaces, schema.min.doubleValue(), schema.max.doubleValue());

		case STRING:
			if (schema.maxLength == null) {
				engineBuilder.logError("String Schema  must specify a 'maxLength' attribute.", SCHEMA, name);
				return null;
			}
			return new StringParser(schema.minLength != null ? schema.minLength.intValue() : 0,
					schema.maxLength.intValue(), schema.regex);

		case DATE:
			if (schema.daysInPast == null || schema.daysInFuture == null) {
				engineBuilder.logError("Date Schema  must specify 'daysInPast' and 'daysInFuture' values.", SCHEMA,
						name);
				return null;
			}

			return new DateParser(schema.daysInPast.intValue(), schema.daysInFuture.intValue());

		default:
			engineBuilder.logError("No value parser implemented for schema type: " + type, SCHEMA, name);
		}

		return null;
	}

	private static class BooleanParser implements IValueParser {

		BooleanParser() {
		}

		@Override
		public IValue parse(String textValue) {
			if (textValue == null || textValue.isEmpty()) {
				return null;
			}
			boolean value = false;
			if ("TRUE".equalsIgnoreCase(textValue)) {
				value = true;
			} else if ("FALSE".equalsIgnoreCase(textValue) == false) {
				return null;
			}
			return Values.newValue(value);
		}
	}

	/**
	 * Validates a string as per a value-schema
	 */
	private static class StringParser implements IValueParser {
		private final int minLength;
		private final int maxLength;
		private final Pattern regexPattern;

		/**
		 *
		 * @param minLength
		 * @param maxLength
		 * @param regex     optional
		 */
		protected StringParser(int minLength, int maxLength, String regex) {
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.regexPattern = regex == null || regex.isEmpty() ? null : Pattern.compile(regex);
		}

		@Override
		public IValue parse(String textValue) {
			if ((textValue == null) || (textValue.length() < this.minLength) || (this.maxLength > 0 && textValue.length() > this.maxLength)) {
				return null;
			}
			if (this.regexPattern != null && !this.regexPattern.matcher(textValue).matches()) {
				return null;
			}
			return Values.newValue(textValue);
		}
	}

	/**
	 * An implementation of IValueParser for the NUMBER type. It parses a string
	 * into a BigDecimal, rounds it, and then validates it against min/max
	 * constraints.
	 */
	private static class NumberParser implements IValueParser {
		private final int nbrDecimalPlaces;
		private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
		private final BigDecimal min;
		private final BigDecimal max;

		protected NumberParser(int nbrDecimalPlaces, double min, double max) {
			this.nbrDecimalPlaces = nbrDecimalPlaces;
			this.min = BigDecimal.valueOf(min);
			this.max = BigDecimal.valueOf(max);
		}

		@Override
		public IValue parse(String textValue) {
			if (textValue == null || textValue.isEmpty()) {
				return null;
			}

			BigDecimal parsedNumber;
			try {
				// 1. Parse the string into a BigDecimal.
				parsedNumber = new BigDecimal(textValue);
			} catch (NumberFormatException e) {
				return null;
			}

			// 2. Immediately round the parsed value according to the schema's rules.
			BigDecimal roundedNumber = parsedNumber.setScale(this.nbrDecimalPlaces, this.roundingMode);

			// 3. Validate the FINAL, rounded value against the min/max constraints.
			if ((roundedNumber.compareTo(this.min) < 0) || (this.max != null && roundedNumber.compareTo(this.max) > 0)) {
				return null;
			}

			// Return a NumberValue containing the rounded and validated number.
			return Values.newValue(roundedNumber);
		}
	}

	private static class DateParser implements IValueParser {
		private final int daysInPast;
		private final int daysInFuture;

		/**
		 *
		 * @param daysInPast
		 * @param daysInFuture
		 */
		protected DateParser(int daysInPast, int daysInFuture) {
			this.daysInPast = daysInPast;
			this.daysInFuture = daysInFuture;
		}

		@Override
		public IValue parse(String textValue) {
			if (textValue == null || textValue.isEmpty()) {
				return null;
			}

			LocalDate date;
			try {
				date = LocalDate.parse(textValue); // Uses ISO-8601 format by default
			} catch (DateTimeParseException e) {
				return null;
			}

			LocalDate today = LocalDate.now();
			LocalDate earliestAllowed = today.minusDays(this.daysInPast);
			LocalDate latestAllowed = today.plusDays(this.daysInFuture);

			if (date.isBefore(earliestAllowed) || date.isAfter(latestAllowed)) {
				return null;
			}

			return Values.newValue(date);
		}
	}

}
