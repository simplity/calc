package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.impl.ValueFactory;

/**
 * An implementation of IValueParser for the DATE type. It parses a string in
 * "YYYY-MM-DD" format and validates it against a relative date range.
 */
class BooleanSchema implements IValidator {
	public static final BooleanSchema INSTANCE = new BooleanSchema();

	/**
	 *
	 * @param daysInPast
	 * @param daysInFuture
	 */
	private BooleanSchema() {
	}

	@Override
	public IValue validate(String textValue, ICalcContext ctx, String fieldName) {
		if (textValue == null || textValue.isEmpty()) {
			ctx.logError("Boolean value cannot be null or empty", fieldName);
			return null;
		}
		boolean value = false;
		if ("TRUE".equalsIgnoreCase(textValue)) {
			value = true;
		} else if ("FALSE".equalsIgnoreCase(textValue) == false) {
			ctx.logError(textValue + " is not a boolean. only 'true' and 'false' are valid.", fieldName);
			return null;
		}
		return ValueFactory.newValue(value);
	}
}
