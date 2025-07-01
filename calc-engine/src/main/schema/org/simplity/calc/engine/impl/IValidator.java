package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;

/**
 * Defines the contract for a component that can parse a raw string value into a
 * validated, type-safe IValue object. An instance of this is created for each
 * schema during engine bootstrap.
 */
public interface IValidator {

	/**
	 * Parses and validates a string input.
	 *
	 * @param textValue The raw string value from the input payload.
	 * @param ctx       into which an error message is added in case of any error in
	 *                  parsing/validating
	 * @param fieldName name to be used for any error reporting
	 * @return A valid IValue object of the correct type, or null in case of any
	 *         error
	 */
	IValue validate(String textValue, ICalcContext ctx, String fieldName);
}
