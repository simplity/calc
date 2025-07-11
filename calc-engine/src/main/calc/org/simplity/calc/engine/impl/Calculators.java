package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;
import org.simplity.calc.engine.config.CalcStepsDS;
import org.simplity.calc.engine.config.CalculatorDS;

/**
 * Utility class to parse an instance of a RuleDS and create an instance of a
 * IRule
 */
class Calculators {

	public static ICalculator buildCalculator(CalculatorDS rule, String name, IValueType valueType,
			ExpressionBuilder expressionBuilder) {

		boolean allOk = true;
		String expr = rule.defaultExpression;

		IExpression defaultExpression = expressionBuilder.parse(expr, name, valueType);
		if (defaultExpression == null) {
			allOk = false;
		}

		CalcStepsDS[] scenarios = rule.calcSteps;
		if (scenarios == null || scenarios.length == 0) {
			if (allOk) {
				return new ExpressionCalculator(defaultExpression, valueType);
			}
			return null;
		}

		int n = scenarios.length;
		IExpression[][] ruleCases = new IExpression[n][2];
		int i = -1;
		for (CalcStepsDS exp : scenarios) {
			i++;
			IExpression condition = expressionBuilder.parse(exp.when, name, ValueTypes.BOOLEAN);
			IExpression expression = expressionBuilder.parse(exp.value, name, valueType);
			if (condition != null && expression != null) {
				ruleCases[i][0] = condition;
				ruleCases[i][1] = expression;
			} else {
				allOk = false;
			}
		}
		if (allOk) {
			return new IfElseCalculator(ruleCases, defaultExpression, valueType);
		}
		return null;

	}

	/**
	 * Simplest rule. Just an expression
	 */
	private static class ExpressionCalculator implements ICalculator {
		private final IExpression expression;
		private final IValueType valueType;

		protected ExpressionCalculator(IExpression expression, IValueType valueType) {
			this.expression = expression;
			this.valueType = valueType;
		}

		@Override
		public IValue calculate(ICalcContext ctx) {
			return this.expression.evaluate(ctx);
		}

		@Override
		public IValueType getValueType() {
			return this.valueType;
		}

		@Override
		public boolean dryrun(DryrunContext ctx) {

			return this.expression.dryrun(ctx);
		}
	}

	/**
	 * Represents a sequence of if-condition-then-value case. It extends the
	 * Expression rule, which acts as a default if none of the if-conditions come
	 * true
	 */
	private static class IfElseCalculator extends ExpressionCalculator {
		/*
		 * [][0] is condition [][1] is value-expression
		 */
		private final IExpression[][] cases;

		/**
		 *
		 * @param ruleCases
		 * @param defaultExpression
		 */
		protected IfElseCalculator(IExpression[][] ruleCases, IExpression defaultExpression, IValueType valueType) {
			super(defaultExpression, valueType);
			this.cases = ruleCases;
		}

		@Override
		public IValue calculate(ICalcContext ctx) {
			// Evaluate each conditional case in that order
			for (IExpression[] c : this.cases) {
				IValue caseIsTrue = c[0].evaluate(ctx);
				// We stop in case of any error.
				if (caseIsTrue == null) {
					return null;
				}
				if (caseIsTrue.getBooleanValue()) {
					return c[1].evaluate(ctx);
				}
			}

			// If no conditions were met, evaluate the default expression.
			return super.calculate(ctx);
		}

		@Override
		public boolean dryrun(DryrunContext ctx) {

			if (!super.dryrun(ctx)) {
				return false;
			}

			for (IExpression[] c : this.cases) {
				if (c[0].dryrun(ctx) && c[1].dryrun(ctx)) {
					continue;
				}
				return false;
			}
			return true;
		}

	}

}
