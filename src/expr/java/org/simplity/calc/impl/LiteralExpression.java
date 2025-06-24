package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * An expression that represents a constant, literal value (e.g., 5, "hello",
 * true).
 * <p>
 * This is a terminal node in an expression tree. Its evaluation simply returns
 * the value it was constructed with, ignoring the context.
 */
public final class LiteralExpression implements IExpression {

	private final IValue value;

	/**
	 * Constructs a new LiteralExpression.
	 *
	 * @param value The non-null, constant {@link IValue} this expression
	 *              represents.
	 */
	public LiteralExpression(IValue value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return The type of the literal value it holds.
	 */
	@Override
	public ValueType getValueType() {
		return this.value.getType();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Evaluating a literal simply returns the value itself. The context is ignored.
	 */
	@Override
	public IValue evaluate(ICalcContext ctx) {
		return this.value;
	}
}