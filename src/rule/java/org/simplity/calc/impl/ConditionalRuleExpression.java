package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * An IExpression that represents a full conditional rule (if-elseif-else). It
 * evaluates a list of conditional branches and falls back to a default
 * expression.
 */
class ConditionalRuleExpression implements IParsedRule {
	private final RuleCase[] cases;
	private final IExpression defaultExpression;
	private final ValueType valueType;

	/**
	 *
	 * @param ruleCases
	 * @param defaultExpression
	 */
	ConditionalRuleExpression(RuleCase[] ruleCases, IExpression defaultExpression, ValueType valueType) {
		this.cases = ruleCases;
		this.defaultExpression = defaultExpression;
		this.valueType = valueType;
	}

	@Override
	public IValue apply(ICalcContext ctx) {
		// Evaluate each conditional case in that order
		for (RuleCase c : this.cases) {
			IValue value = c.condition.evaluate(ctx);
			// We stop in case of any error.
			if (value == null) {
				return null;
			}
			if (value.getBooleanValue()) {
				return c.expression.evaluate(ctx);
			}
		}
		// If no conditions were met, evaluate the default expression.
		return this.defaultExpression.evaluate(ctx);
	}

	@Override
	public ValueType getValueType() {
		return this.valueType;
	}

	/**
	 * Represents a single "if-then" branch in a conditional rule.
	 */
	static class RuleCase {
		/**
		 * boolean expression that acts as "if"
		 */
		final IExpression condition;
		/**
		 * expression that acts as "then". It should evaluate to a value type that
		 * matches the designated value type of the variable to which this rule is
		 * assigned to
		 */
		final IExpression expression;

		RuleCase(IExpression condition, IExpression expression) {
			this.condition = condition;
			this.expression = expression;
		}
	}
}
