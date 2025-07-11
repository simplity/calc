package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEvaluatorFunction;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * A factory for creating instances of {@link ICalcFunction}.
 * <p>
 * This class bundles a function's execution logic (an
 * {@link IEvaluatorFunction} lambda) with its essential metadata (return type,
 * parameter signature) into a single, concrete {@code ICalcFunction} object.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public final class CalcFunctions {
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private CalcFunctions() {
		// Not meant to be instantiated
	}

	/**
	 * Creates an instance of {@link ICalcFunction}.
	 *
	 * @param function   The lambda function that implements the execution logic.
	 *                   Cannot be null.
	 * @param returnType The fixed {@link DataType} that this function always
	 *                   returns. Cannot be null.
	 * @param argTypes   An array defining the function's parameter signature. See
	 *                   {@link ICalcFunction} for the conventions. Cannot be null,
	 *                   but can be an empty array for a no-argument function.
	 * @param isVarArgs  {@code true} if the function is variadic, {@code false}
	 *                   otherwise.
	 * @return a non-null, concrete instance of {@code ICalcFunction}.
	 */
	public static ICalcFunction newCalcFunction(IEvaluatorFunction function, IValueType returnType,
			IValueType[] argTypes, boolean isVarArgs) {
		return new CalcFunction(function, returnType, argTypes, isVarArgs);
	}

	/**
	 * A private, immutable, concrete implementation of the ICalcFunction interface.
	 * It serves as a simple data container for a function's properties and its
	 * execution logic.
	 */
	private static class CalcFunction implements ICalcFunction {
		private final IEvaluatorFunction function;
		private final IValueType[] argTypes;
		private final boolean lastOneIsVararg;
		private final IValueType returnType;

		protected CalcFunction(IEvaluatorFunction function, IValueType returnType, IValueType[] argTypes,
				boolean lastOneIsVararg) {
			this.function = function;
			this.argTypes = argTypes;
			this.lastOneIsVararg = lastOneIsVararg;
			this.returnType = returnType;
		}

		@Override
		public IValueType[] getParameterTypes() {
			return this.argTypes;
		}

		@Override
		public boolean lastOneIsVararg() {
			return this.lastOneIsVararg;
		}

		@Override
		public IValueType getReturnType() {
			return this.returnType;
		}

		@Override
		public IValue call(IValue[] args, ICalcContext ctx) {
			return this.function.apply(args, ctx);
		}
	}
}