package org.simplity.calc.engine.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.impl.ValueFactory;

/**
 * An implementation of IValueParser for the NUMBER type. It parses a string
 * into a BigDecimal, rounds it, and then validates it against min/max
 * constraints.
 */
class NumberSchema implements IValidator {
	private final int nbrDecimalPlaces;
	private final RoundingMode roundingMode = RoundingMode.HALF_EVEN;
	private final BigDecimal min;
	private final BigDecimal max;

	NumberSchema(int nbrDecimalPlaces, double min, double max) {
		this.nbrDecimalPlaces = nbrDecimalPlaces;
		this.min = BigDecimal.valueOf(min);
		this.max = BigDecimal.valueOf(max);
	}

	@Override
	public IValue validate(String textValue, ICalcContext ctx, String fieldName) throws NumberFormatException {
		if (textValue == null || textValue.isEmpty()) {
			ctx.logError("Number value cannot be null or empty.", fieldName);
			return null;
		}

		// 1. Parse the string into a BigDecimal.
		BigDecimal parsedNumber = new BigDecimal(textValue);

		// 2. Immediately round the parsed value according to the schema's rules.
		BigDecimal roundedNumber = parsedNumber.setScale(this.nbrDecimalPlaces, this.roundingMode);

		// 3. Validate the FINAL, rounded value against the min/max constraints.
		if (roundedNumber.compareTo(this.min) < 0) {
			ctx.logError("Value " + roundedNumber + " is less than the expected minimum of " + this.min, fieldName);
			return null;
		}
		if (this.max != null && roundedNumber.compareTo(this.max) > 0) {
			ctx.logError("Value " + roundedNumber + " is greater than the expected maximum of " + this.max, fieldName);
			return null;
		}

		// Return a NumberValue containing the rounded and validated number.
		return ValueFactory.newValue(roundedNumber);
	}
}
