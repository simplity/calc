package org.simplity.calc.impl;

import java.util.regex.Pattern;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;

/**
 * An implementation of IValueParser for the STRING type. It validates the
 * string against length and regex constraints.
 */
public class StringSchema implements IValidator {
	private final int minLength;
	private final int maxLength;
	private final Pattern regexPattern;

	/**
	 *
	 * @param minLength
	 * @param maxLength
	 * @param regex     optional
	 */
	public StringSchema(int minLength, int maxLength, String regex) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.regexPattern = regex == null || regex.isEmpty() ? null : Pattern.compile(regex);
	}

	@Override
	public IValue validate(String textValue, ICalcContext ctx, String fieldName) {
		if (textValue == null) {
			ctx.logError("String value cannot be null.", fieldName);
			return null;
		}
		if (textValue.length() < this.minLength) {
			ctx.logError(
					"String length " + textValue.length() + " is less than the configured minimum of " + this.minLength,
					fieldName);
			return null;
		}
		if (this.maxLength > 0 && textValue.length() > this.maxLength) {
			ctx.logError("String length " + textValue.length() + " is greater than the configured maximum of "
					+ this.maxLength, fieldName);
			return null;
		}
		if (this.regexPattern != null && !this.regexPattern.matcher(textValue).matches()) {
			ctx.logError("String '" + textValue + "' does not match the required pattern.", fieldName);
			return null;
		}
		return ValueFactory.newValue(textValue);
	}
}
