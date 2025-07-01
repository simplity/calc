package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.ValueType;
import org.simplity.calc.engine.impl.IDryrunContext;

/**
 * An immutable expression that represents a constant, literal value (e.g., 5,
 * "hello", true).
 * <p>
 * This class serves as a terminal node (a leaf) in an expression tree. Its
 * evaluation is trivial: it simply returns the value it was constructed with,
 * ignoring the calculation context.
 *
 * <h3>Thread Safety</h3> This class is immutable and therefore inherently
 * thread-safe.
 *
 * @author Simplity Technologies
 * @since 1.0
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
	 * For a literal, evaluation simply returns the value itself. The context is
	 * ignored.
	 */
	@Override
	public IValue evaluate(ICalcContext ctx) {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * A literal value is always considered "ready" to be evaluated, so this method
	 * always returns {@code true}.
	 */
	@Override
	public boolean dryrun(IDryrunContext ctx) {
		return true;
	}
}