package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;

/**
 * Defines the contract for a component that can validate a given value as per
 * validation rules. The component also has the ability to participate in a dry
 * run.
 */
public interface IValidator {

	/**
	 * carry out validation, and return the errorMessgaeId if the validation fails.
	 *
	 * @param ctx The calculation context, providing access to variables and other
	 *            runtime data. It is also used to log an appropriate error message
	 *            if the validation fails
	 * @return true of the validation passes. false otherwise
	 */
	boolean validate(ICalcContext ctx);

	/**
	 * Dryrun this validator to check if it is semantically possible to use the
	 * validator. More specifically, check if every variable that this component may
	 * be dependent on (outer set assuming all possible paths) can be "resolved" or
	 * "determined" based on the dependencies between the variables. This can can be
	 * typically achieved by recursively using this method on all sub-expressions.
	 *
	 * @param ctx
	 * @return true if all OK. False on any error, in which case suitable error
	 *         messages would have been logged into the context
	 */
	boolean dryrun(DryrunContext ctx);
}
