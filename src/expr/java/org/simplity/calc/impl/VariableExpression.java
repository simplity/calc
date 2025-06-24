package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * An expression that represents a variable to be resolved at runtime.
 * <p>
 * This expression holds the name of a variable. Upon evaluation, it retrieves
 * the corresponding value from the {@link ICalcContext}. For static analysis to
 * work, the expected {@link ValueType} of the variable is determined at build
 * time.
 */
public final class VariableExpression implements IExpression {
	private final String variableName;
	private final ValueType valueType;

	/**
	 * Constructs a new VariableExpression.
	 *
	 * @param variableName The name of the variable this expression refers to.
	 * @param valueType    The expected type of this variable, determined at build
	 *                     time.
	 */
	public VariableExpression(String variableName, ValueType valueType) {
		this.variableName = variableName;
		this.valueType = valueType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The expected type of the variable, determined at build time.
	 */
	@Override
	public ValueType getValueType() {
		return this.valueType;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Evaluates this expression by retrieving the variable's value from the
	 * context.
	 *
	 * @throws RuntimeException if the variable is not found in the context.
	 */
	@Override
	public IValue evaluate(ICalcContext ctx) {
		return ctx.determineValue(this.variableName);
	}

	/**
	 * Gets the name of the variable this expression represents.
	 *
	 * @return the variable name.
	 */
	public String getVariableName() {
		return this.variableName;
	}
}