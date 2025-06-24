package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.IEvaluatorFunction;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Responsible for creating required instance of ICalcFunction<?>
 */
public class CalcFunctionFactory {
	/**
	 * creates an instance of {@link ICalcFunction}
	 *
	 * @param function   to be called to evaluate. This should be a function that
	 *                   receives
	 * @param returnType
	 * @param argTypes   value types of the arguments to be used to call this
	 *                   function. null or empty list if the function accepts no
	 *                   arguments
	 * @param isVarArgs  true if the function treats the last argument as a varArg.
	 *                   That is the last argument can repeat 0 or more times
	 * @return non-null instance
	 */
	public static ICalcFunction newCalcFunction(IEvaluatorFunction function, ValueType returnType, ValueType[] argTypes,
			boolean isVarArgs) {
		return new CalcFunction(function, returnType, argTypes, isVarArgs);
	}

	private static class CalcFunction implements ICalcFunction {
		private final IEvaluatorFunction function;
		private final ValueType[] argTypes;
		private final boolean lastOneIsVararg;
		private final ValueType valueType;

		protected CalcFunction(IEvaluatorFunction function, ValueType valueType, ValueType[] argTypes,
				boolean lastOneIsVararg) {
			this.function = function;
			this.argTypes = argTypes;
			this.lastOneIsVararg = lastOneIsVararg;
			this.valueType = valueType;
		}

		@Override
		public ValueType[] getParameterTypes() {
			return this.argTypes;
		}

		@Override
		public boolean lastOneIsVararg() {
			return this.lastOneIsVararg;
		}

		@Override
		public ValueType getReturnType() {
			return this.valueType;
		}

		@Override
		public IValue call(IValue[] args, ICalcContext ctx) {
			return this.function.apply(args, ctx);
		}
	}
}
