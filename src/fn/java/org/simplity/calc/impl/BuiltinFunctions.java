package org.simplity.calc.impl;

import java.util.HashMap;
import java.util.Map;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.ICalcFunction;
import org.simplity.calc.api.IEvaluatorFunction;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * A central registry for all built-in functions and operators. This class uses
 * the "static inner class singleton" pattern to define each function. This
 * keeps the function implementations organized, stateless, and efficient.
 */
public final class BuiltinFunctions {

	/**
	 * Unary operators
	 */

	private static final IEvaluatorFunction NEGATE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().negate());

	private static final IEvaluatorFunction NOT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(!args[0].getBooleanValue());

	/**
	 * binary arithmetic
	 */

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

	// Comparators. Need to work on non-numbers

	private static final IEvaluatorFunction EQ = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().equals(args[1].getNumberValue()));

	private static final IEvaluatorFunction NEQ = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(!args[0].getNumberValue().equals(args[1].getNumberValue()));

	private static final IEvaluatorFunction GT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) > 0);

	private static final IEvaluatorFunction LT = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) < 0);

	private static final IEvaluatorFunction GTE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) >= 0);

	private static final IEvaluatorFunction LTE = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getNumberValue().compareTo(args[1].getNumberValue()) <= 0);

	// boolean binary operators
	private static final IEvaluatorFunction AND = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getBooleanValue() && args[1].getBooleanValue());

	private static final IEvaluatorFunction OR = (IValue[] args, ICalcContext ctx) -> ValueFactory
			.newValue(args[0].getBooleanValue() || args[1].getBooleanValue());

	private static final Map<String, ICalcFunction> REGISTRY = new HashMap<>();

	// pre-define argument types used by the functions
	private static final ValueType[] NUMBER1 = { ValueType.NUMBER };
	private static final ValueType[] NUMBER2 = { ValueType.NUMBER, ValueType.NUMBER };
	private static final ValueType[] BOOL1 = { ValueType.BOOLEAN };
	private static final ValueType[] BOOL2 = { ValueType.BOOLEAN, ValueType.BOOLEAN };

	// add all the functions to the registry
	static {
		/*
		 * unary. NOTE that unary minus needs a name-change
		 */
		register("!", NOT, ValueType.BOOLEAN, BOOL1);
		register("unary-", NEGATE, ValueType.NUMBER, NUMBER1);

		// arithmetic binary

		registerNmericBinary("+", ADD);
		registerNmericBinary("-", SUBTRACT);
		registerNmericBinary("*", MULTIPLY);
		registerNmericBinary("/", DIVIDE);
		registerNmericBinary("%", REMAINDER);

		// numeric comparators
		registerNmericComparator("=", EQ);
		registerNmericComparator("!=", NEQ);
		registerNmericComparator(">", GT);
		registerNmericComparator("<", LT);
		registerNmericComparator(">=", GTE);
		registerNmericComparator("<=", LTE);

		// Logical Operators. Note that they are NOT '&&' '||'
		register("&", AND, ValueType.BOOLEAN, BOOL2);
		register("|", OR, ValueType.BOOLEAN, BOOL2);

	}

	/**
	 * Binary Operators may be overloaded. A naming convention is used to name the
	 * function based on the operator symbol and the operand types. This is the
	 * utility to format the name. It does not check for the validity of the
	 * operator or the value types
	 *
	 * @param operation binary operator symbol.
	 * @param leftType  non-null valueType of the left operand
	 * @param rightType non-null valueType of the right operand
	 * @return formatted name as per the convention.
	 */
	public static String formatFunctionName(String operation, ValueType leftType, ValueType rightType) {
		return operation + ':' + leftType.name().toLowerCase() + ',' + rightType.name().toLowerCase();
	}

	private static void registerNmericBinary(String operator, IEvaluatorFunction fn) {
		String name = formatFunctionName(operator, ValueType.NUMBER, ValueType.NUMBER);
		ICalcFunction function = CalcFunctionFactory.newCalcFunction(fn, ValueType.NUMBER, NUMBER2, false);
		REGISTRY.put(name, function);
	}

	private static void registerNmericComparator(String operator, IEvaluatorFunction fn) {
		String name = formatFunctionName(operator, ValueType.BOOLEAN, ValueType.NUMBER);
		ICalcFunction function = CalcFunctionFactory.newCalcFunction(fn, ValueType.NUMBER, NUMBER2, false);
		REGISTRY.put(name, function);
	}

	private static void register(String name, IEvaluatorFunction function, ValueType returnType, ValueType[] argTypes) {
		ICalcFunction f = CalcFunctionFactory.newCalcFunction(function, returnType, argTypes, false);
		REGISTRY.put(name, f);
	}

	static void getAll(Map<String, ICalcFunction> registry) {
		registry.putAll(REGISTRY);
	}

}
