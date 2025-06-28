package org.simplity.calc.api;

/**
 * Defines the contract for a function that can be executed by the calculation
 * engine.
 * <p>
 * In this engine, all operations (e.g., +, -, AND, sqrt, lookup) are
 * implemented as functions. Each function has a strict definition for its
 * signature and return type, enabling strong validation at build-time.
 *
 * <h3>Signature Convention for Variadic Arguments</h3> The function signature
 * is defined by combining {@link #getParameterTypes()} and
 * {@link #lastOneIsVararg()}. This model supports both fixed-argument and
 * variadic functions.
 * <ul>
 * <li><b>Fixed-Argument Functions:</b><br>
 * {@code lastOneIsVararg()} returns {@code false}. {@code getParameterTypes()}
 * returns an array where each element defines a required argument. The number
 * of arguments is exact.</li>
 *
 * <li><b>Variadic Functions:</b><br>
 * {@code lastOneIsVararg()} returns {@code true}. The array from
 * {@code getParameterTypes()} now defines a minimum signature.
 * <ul>
 * <li>The "fixed" arguments are all elements from index 0 to N-2.</li>
 * <li>The type of the <b>last element</b> (at index N-1) defines the type for
 * the repeating variadic part.</li>
 * <li>The variadic part can occur <b>zero or more times</b>.</li>
 * </ul>
 * <b>Example 1: a "one-or-more" function like
 * {@code average(number...)}</b><br>
 * {@code getParameterTypes()} returns {@code {NUMBER, NUMBER}}. This means one
 * fixed NUMBER argument, followed by zero-or-more NUMBER arguments.
 * Effectively, a minimum of one argument is required. <br>
 * <br>
 * <b>Example 2: a "zero-or-more" function like {@code list(string...)}</b><br>
 * {@code getParameterTypes()} returns {@code {STRING}}. This means zero fixed
 * arguments, followed by zero-or-more STRING arguments. A call with no
 * arguments is valid.</li>
 * </ul>
 *
 * <h3>Polymorphic Functions (Wildcard Type)</h3> To support functions that can
 * accept an argument of any type (e.g., a generic {@code print()} function), a
 * {@code null} value can be used in the array returned by
 * {@link #getParameterTypes()}. A {@code null} element acts as a wildcard, and
 * the validation logic will accept any {@link ValueType} for that parameter
 * position.
 *
 * <h3>Implementation Notes</h3> Implementations of this interface should be
 * **stateless and thread-safe singletons**. The engine will typically create
 * one instance of each function and reuse it for all calculations.
 *
 * @author Simplity Technologies
 * @since 1.0
 */
public interface ICalcFunction {

	/**
	 * Executes the function's logic with the provided, evaluated arguments.
	 *
	 * @param args An array of {@link IValue}s. The engine guarantees that the size
	 *             and types of this array conform to the function's defined
	 *             signature before this method is called.
	 * @param ctx  The calculation context, in case the function needs to access
	 *             other variables or runtime information (e.g., a "today()"
	 *             function).
	 * @return The non-null {@link IValue} result of the function's execution.
	 */
	IValue call(IValue[] args, ICalcContext ctx);

	/**
	 * Gets the predetermined, fixed data type of the value that this function
	 * returns.
	 *
	 * @return The non-null {@link ValueType} that this function will always return.
	 */
	ValueType getReturnType();

	/**
	 * Gets the data types that define the function's parameter signature.
	 * <p>
	 * <b>Note:</b> The meaning of this array is context-dependent and is controlled
	 * by the value of {@link #lastOneIsVararg()}. A {@code null} element in the
	 * array is treated as a wildcard that accepts any value type. See the
	 * class-level documentation for a detailed explanation.
	 *
	 * @return A non-null array of {@link ValueType}s defining the function
	 *         signature.
	 */
	ValueType[] getParameterTypes();

	/**
	 * Specifies if the last parameter defined in {@link #getParameterTypes()} is
	 * variadic.
	 *
	 * @return {@code true} if the function is variadic, {@code false} otherwise.
	 *         See the class-level documentation for a detailed explanation of the
	 *         signature convention.
	 */
	boolean lastOneIsVararg();
}