package org.simplity.calc.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEvaluatorFunction;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.ValueType;
import org.simplity.calc.engine.impl.CalcFunctionFactory;
import org.simplity.calc.engine.impl.ValueFactory;

/**
 * A central registry for all built-in functions and operators.
 * <p>
 * This class uses a declarative style to define functions. The execution logic
 * for each function is defined as a stateless lambda (an
 * {@link IEvaluatorFunction}). A static initializer block then uses the
 * {@link CalcFunctionFactory} to create concrete {@link ICalcFunction}
 * instances, bundling the logic with its signature metadata, and registers them
 * into a map.
 *
 * <h3>Thread Safety</h3> This class is thread-safe. The registry is populated
 * once in a static initializer and is safely published for read-only access
 * thereafter.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public final class BuiltinFunctions {
	private BuiltinFunctions() {
		// not to be instantiated
	}

	private static final IEvaluatorFunction NEGATE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().negate());

	private static final IEvaluatorFunction NOT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(!args[0].getBooleanValue());

	private static final IEvaluatorFunction ADD = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().add(args[1].getNumberValue()));

	private static final IEvaluatorFunction SUBTRACT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().subtract(args[1].getNumberValue()));

	private static final IEvaluatorFunction MULTIPLY = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().multiply(args[1].getNumberValue()));

	private static final IEvaluatorFunction DIVIDE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().divide(args[1].getNumberValue()));

	private static final IEvaluatorFunction REMAINDER = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().remainder(args[1].getNumberValue()));

	private static final IEvaluatorFunction EQ = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) == 0);

	private static final IEvaluatorFunction NEQ = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) != 0);

	private static final IEvaluatorFunction GT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) > 0);

	private static final IEvaluatorFunction LT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) < 0);

	private static final IEvaluatorFunction GTE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) >= 0);

	private static final IEvaluatorFunction LTE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) <= 0);

	private static final IEvaluatorFunction AND = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getBooleanValue() && args[1].getBooleanValue());

	private static final IEvaluatorFunction OR = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getBooleanValue() || args[1].getBooleanValue());

	private static final Map<String, ICalcFunction> REGISTRY = new HashMap<>();

	// Pre-defined signatures for reuse.
	private static final ValueType[] NUMBER1 = { ValueType.NUMBER };
	private static final ValueType[] NUMBER2 = { ValueType.NUMBER, ValueType.NUMBER };
	private static final ValueType[] BOOL1 = { ValueType.BOOLEAN };
	private static final ValueType[] BOOL2 = { ValueType.BOOLEAN, ValueType.BOOLEAN };

	static {
		// Register Unary Operators
		register("!", NOT, ValueType.BOOLEAN, BOOL1, false);
		register("unary-", NEGATE, ValueType.NUMBER, NUMBER1, false);

		// Register Arithmetic Binary Operators
		registerOverloaded("+", ADD, ValueType.NUMBER, NUMBER2, false);
		registerOverloaded("-", SUBTRACT, ValueType.NUMBER, NUMBER2, false);
		registerOverloaded("*", MULTIPLY, ValueType.NUMBER, NUMBER2, false);
		registerOverloaded("/", DIVIDE, ValueType.NUMBER, NUMBER2, false);
		registerOverloaded("%", REMAINDER, ValueType.NUMBER, NUMBER2, false);

		// Register Numeric Comparators
		registerOverloaded("=", EQ, ValueType.BOOLEAN, NUMBER2, false);
		registerOverloaded("!=", NEQ, ValueType.BOOLEAN, NUMBER2, false);
		registerOverloaded(">", GT, ValueType.BOOLEAN, NUMBER2, false);
		registerOverloaded("<", LT, ValueType.BOOLEAN, NUMBER2, false);
		registerOverloaded(">=", GTE, ValueType.BOOLEAN, NUMBER2, false);
		registerOverloaded("<=", LTE, ValueType.BOOLEAN, NUMBER2, false);

		// Register Logical Operators
		register("&", AND, ValueType.BOOLEAN, BOOL2, false);
		register("|", OR, ValueType.BOOLEAN, BOOL2, false);
	}

	/**
	 * Builds a conventional key for an overloaded function.
	 *
	 * @param name     The simple name of the function (e.g., "+").
	 * @param argTypes The value types of its arguments.
	 * @return A key string (e.g., "+:number,number").
	 */
	public static String buildFunctionKey(String name, ValueType... argTypes) {
		StringBuilder sb = new StringBuilder(name.toLowerCase());
		if (argTypes.length > 0) {
			sb.append(':');
			for (int i = 0; i < argTypes.length; i++) {
				sb.append(argTypes[i].name().toLowerCase());
				if (i < argTypes.length - 1) {
					sb.append(',');
				}
			}
		}
		return sb.toString();
	}

	/**
	 * A helper to register a simple, non-overloaded function.
	 */
	private static void register(String name, IEvaluatorFunction function, ValueType returnType, ValueType[] argTypes,
			boolean isVararg) {
		ICalcFunction f = CalcFunctionFactory.newCalcFunction(function, returnType, argTypes, isVararg);
		REGISTRY.put(name.toLowerCase(), f);
	}

	/**
	 * A helper to register an overloaded function, building its key from its name
	 * and parameter types.
	 */
	private static void registerOverloaded(String name, IEvaluatorFunction function, ValueType returnType,
			ValueType[] argTypes, boolean isVararg) {
		String key = buildFunctionKey(name, argTypes);
		register(key, function, returnType, argTypes, isVararg);
	}

	/**
	 * Populates a given map with all the functions defined in this registry.
	 *
	 * @param registry The map to populate. Must not be null.
	 */
	static void getAll(Map<String, ICalcFunction> registry) {
		registry.putAll(REGISTRY);
	}
}