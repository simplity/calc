package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * Represents a Variable that is an important component of a Calculation Engine
 */
public interface IVariable {
	/**
	 *
	 * @return name
	 */
	String getName();

	/**
	 *
	 * @return true if this is an input field
	 */
	boolean isInput();

	/**
	 *
	 * @return true if this is a required input
	 */
	boolean isRequiredInput();

	/**
	 *
	 * @return true if this an output
	 */
	boolean isOutput();

	/**
	 *
	 * @return non-null value type
	 */
	IValueType getValueType();

	/**
	 *
	 * @return validator associated with this variable. null if no such validator
	 */
	IValueParser getValidator();

	/**
	 *
	 * @return rule that is associated with this variable. null otherwise
	 */
	ICalculator getRule();

	/**
	 * There is a sequencing issue in building the variables and rules. Hence this
	 * setter is provided. Must be used only once
	 *
	 * @param rule
	 */

	void setRule(ICalculator rule);

	/**
	 *
	 * @return precision, if this is a number. else 0;
	 */
	int getPrecision();

	/**
	 * parse an input string into an instance of IValue a
	 *
	 * @param valueToParse
	 * @param ctx          error message is added to the context in case of any
	 *                     validation errors
	 * @return null if the string is not valid for this variable
	 */
	IValue parse(String valueToParse, ICalcContext ctx);

	/**
	 * evaluate the value for this variable
	 *
	 * @param ctx
	 * @return null in case of any unexpected error
	 */
	IValue evaluate(ICalcContext ctx);

	/**
	 * check for any possible error for the dry run
	 *
	 * @param dryCtx
	 * @return ok if dry run is successful. false if any error is detected and
	 *         logged to the context
	 */
	boolean dryrun(DryrunContext dryCtx);

}
