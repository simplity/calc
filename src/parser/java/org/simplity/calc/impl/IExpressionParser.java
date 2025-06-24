package org.simplity.calc.impl;

/**
 * An Expression parser parses a string-representation of an expression into an
 * instance of {@link IExpression}, which is highly optimised for the the
 * runtime calcEngine
 */
public interface IExpressionParser {
	/**
	 * parse a string-representation of an expression into its run-time-optimised
	 * instance of {@link IExpression}
	 *
	 * @param expr         String representation of an expression
	 * @param variableName variable name with which this expression is associated
	 *                     with. Used for logging errors if any
	 * @return an instance of {@link IExpression} or null in case of any error. Any
	 *         error would have been logged into the context
	 */
	IExpression parse(String expr, String variableName);

}
