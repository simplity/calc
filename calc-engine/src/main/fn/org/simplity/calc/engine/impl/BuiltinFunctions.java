package org.simplity.calc.engine.impl;

import java.util.HashMap;
import java.util.Map;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEvaluatorFunction;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;

/**
 * A central registry for all built-in functions and operators.
 * <p>
 * This class uses a declarative style to define functions. The execution logic
 * for each function is defined as a stateless lambda (an
 * {@link IEvaluatorFunction}). A static initializer block then uses the
 * {@link CalcFunctions} to create concrete {@link ICalcFunction} instances,
 * bundling the logic with its signature metadata, and registers them into a
 * map.
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

	private static final IEvaluatorFunction NEGATE = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().negate());

	private static final IEvaluatorFunction NOT = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(!args[0].getBooleanValue());

	private static final IEvaluatorFunction ADD = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().add(args[1].getNumberValue()));

	private static final IEvaluatorFunction SUBTRACT = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().subtract(args[1].getNumberValue()));

	private static final IEvaluatorFunction MULTIPLY = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().multiply(args[1].getNumberValue()));

	private static final IEvaluatorFunction DIVIDE = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().divide(args[1].getNumberValue()));

	private static final IEvaluatorFunction REMAINDER = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getNumberValue().remainder(args[1].getNumberValue()));

	private static final IEvaluatorFunction AND = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getBooleanValue() && args[1].getBooleanValue());

	private static final IEvaluatorFunction OR = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].getBooleanValue() || args[1].getBooleanValue());

	/*
	 * Value.equals() is overridden to check for the right type and value. Hence it
	 * EQ and NEQ will work for all value types. However we maintain a strict
	 * type-check and allow comparison of the right types
	 */
	private static final IEvaluatorFunction EQ = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].equals(args[1]));

	private static final IEvaluatorFunction NEQ = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(!args[0].equals(args[1]));

	private static final IEvaluatorFunction GT = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].compareTo(args[1]) > 0);

	private static final IEvaluatorFunction LT = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].compareTo(args[1]) < 0);

	private static final IEvaluatorFunction GTE = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].compareTo(args[1]) >= 0);

	private static final IEvaluatorFunction LTE = (IValue[] args, ICalcContext ctx) -> Values
			.newValue(args[0].compareTo(args[1]) <= 0);

	private static final Map<String, ICalcFunction> REGISTRY = new HashMap<>();

	// Pre-defined signatures for reuse.
	private static final IValueType[] NUMBER1 = { ValueTypes.NUMBER };
	private static final IValueType[] NUMBER2 = { ValueTypes.NUMBER, ValueTypes.NUMBER };
	private static final IValueType[] ANY2 = { null, null };
	private static final IValueType[] BOOL1 = { ValueTypes.BOOLEAN };

	private static final IValueType[] BOOL2 = { ValueTypes.BOOLEAN, ValueTypes.BOOLEAN };

	static {
		// Register Unary Operators
		register("!", NOT, ValueTypes.BOOLEAN, BOOL1);
		register("unary-", NEGATE, ValueTypes.NUMBER, NUMBER1);
		// Register Arithmetic Binary Operators
		register("+", ADD, ValueTypes.NUMBER, NUMBER2);
		register("-", SUBTRACT, ValueTypes.NUMBER, NUMBER2);
		register("*", MULTIPLY, ValueTypes.NUMBER, NUMBER2);
		register("/", DIVIDE, ValueTypes.NUMBER, NUMBER2);
		register("%", REMAINDER, ValueTypes.NUMBER, NUMBER2);

		/*
		 * Comparators, including + and != work with only matching types. Matching
		 * argument types are validated by the parser before using these
		 * operator-functions
		 */

		register("=", EQ, ValueTypes.BOOLEAN, ANY2);
		register("!=", NEQ, ValueTypes.BOOLEAN, ANY2);
		register(">", GT, ValueTypes.BOOLEAN, ANY2);
		register("<", LT, ValueTypes.BOOLEAN, ANY2);
		register(">=", GTE, ValueTypes.BOOLEAN, ANY2);
		register("<=", LTE, ValueTypes.BOOLEAN, ANY2);

		// Register Logical Operators
		register("&", AND, ValueTypes.BOOLEAN, BOOL2);
		register("|", OR, ValueTypes.BOOLEAN, BOOL2);
	}

	/**
	 * A helper to register a simple, non-overloaded function.
	 */
	private static void register(String name, IEvaluatorFunction function, IValueType returnType,
			IValueType[] argTypes) {
		ICalcFunction f = CalcFunctions.newCalcFunction(function, returnType, argTypes, false);
		REGISTRY.put(name.toLowerCase(), f);
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