package org.simplity.calc.impl;

import java.time.LocalDate;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;

/**
 * An implementation of IValueParser for the DATE type. It parses a string in
 * "YYYY-MM-DD" format and validates it against a relative date range.
 */
public class DateSchema implements IValidator {
	private final int daysInPast;
	private final int daysInFuture;

	/**
	 *
	 * @param daysInPast
	 * @param daysInFuture
	 */
	public DateSchema(int daysInPast, int daysInFuture) {
		this.daysInPast = daysInPast;
		this.daysInFuture = daysInFuture;
	}

	@Override
	public IValue validate(String textValue, ICalcContext ctx, String fieldName) {
		if (textValue == null || textValue.isEmpty()) {
			ctx.logError("Date value cannot be null or empty", fieldName);
			return null;
		}

		LocalDate date = LocalDate.parse(textValue); // Uses ISO-8601 format by default

		LocalDate today = LocalDate.now();
		LocalDate earliestAllowed = today.minusDays(this.daysInPast);
		LocalDate latestAllowed = today.plusDays(this.daysInFuture);

		if (date.isBefore(earliestAllowed)) {
			ctx.logError("Date " + date + " is before the earliest allowed date of " + earliestAllowed, fieldName);
			return null;
		}
		if (date.isAfter(latestAllowed)) {
			ctx.logError("Date " + date + " is after the latest allowed date of " + latestAllowed, fieldName);
			return null;
		}

		return ValueFactory.newValue(date);
	}
}
