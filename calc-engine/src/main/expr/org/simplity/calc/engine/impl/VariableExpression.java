package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * An immutable expression that represents a variable to be resolved from a
 * context at runtime.
 * <p>
 * This expression holds the name of a variable. For static analysis to work,
 * the {@link DataType} of the variable is determined when the expression tree
 * is built and is stored within this object.
 *
 * <h3>Thread Safety</h3> This class is immutable and therefore inherently
 * thread-safe.
 *
 */
public final class VariableExpression implements IExpression {
	private final String variableName;
	private final IValueType valueType;

	/**
	 * Constructs a new VariableExpression.
	 *
	 * @param variableName The non-null name of the variable this expression refers
	 *                     to.
	 * @param valueType    The non-null, predetermined type of this variable.
	 */
	public VariableExpression(String variableName, IValueType valueType) {
		this.variableName = variableName;
		this.valueType = valueType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The expected type of the variable, as determined at build time.
	 */
	@Override
	public IValueType getValueType() {
		return this.valueType;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Evaluates this expression by retrieving the variable's value from the
	 * provided context.
	 *
	 * @param ctx The context to resolve the variable from.
	 * @return The {@link IValue} associated with this variable in the context.
	 * @throws RuntimeException if the variable is not found in the context.
	 */
	@Override
	public IValue evaluate(ICalcContext ctx) {
		// The context's implementation is expected to handle missing variables.
		return ctx.determineValue(this.variableName);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For a variable, this checks if the variable is currently available and ready
	 * for evaluation within the given dry-run context.
	 */
	@Override
	public boolean dryrun(DryrunContext ctx) {
		return ctx.isEvaluatable(this.variableName);
	}

	/**
	 * Gets the name of the variable this expression represents. This is useful for
	 * dependency analysis visitors.
	 *
	 * @return the non-null variable name.
	 */
	public String getVariableName() {
		return this.variableName;
	}
}