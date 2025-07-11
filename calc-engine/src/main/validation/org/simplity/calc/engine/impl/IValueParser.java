package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.IValue;

/**
 * Defines the contract for a component that can parse a raw string value into a
 * validated, type-safe IValue object. An instance of this is created for each
 * schema during engine bootstrap.
 */
public interface IValueParser {

	/**
	 * Parses and validates a string input.
	 *
	 * @param textValue The raw string value from the input payload.
	 * @return A valid IValue object of the correct type, or null in case of any
	 *         error
	 */
	IValue parse(String textValue);
}
