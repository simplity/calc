package org.simplity.calc.api;

/**
 * Defines the contract for a function that can be executed by the calculation
 * engine.
 * <p>
 * This interface represents the complete design of a function, including its
 * execution logic, its fixed return type, and its parameter signature. The
 * parameter signature design supports both fixed-argument functions and
 * functions with a variable-length argument (vararg) list.
 *
 * <h3>Signature Convention</h3> The function signature is defined by the
 * combination of {@link #getParameterTypes()} and {@link #lastOneIsVararg()}.
 * <ul>
 * <li><b>For fixed-argument functions:</b> {@code lastOneIsVararg()} returns
 * {@code false}. {@code getParameterTypes()} returns an array where each
 * element corresponds to a required argument.</li>
 * <li><b>For variadic functions:</b> {@code lastOneIsVararg()} returns
 * {@code true}. The array from {@code getParameterTypes()} defines both the
 * required fixed arguments and the type of the variable part. The required
 * arguments are all elements *except the last one*. The last element's type
 * defines the type for the repeating vararg part. The vararg part can occur 0
 * or more times.</li>
 * </ul>
 *
 * An ICalcFunction implementation should be a stateless, thread-safe singleton.
 */
public interface ICalcFunction {

	/**
	 * Executes the function's logic with the evaluated arguments.
	 *
	 * @param args An array of {@link IValue}s, which are the results of evaluating
	 *             the argument expressions. The number and type of these arguments
	 *             are validated before this method is called.
	 * @param ctx  The calculation context, in case the function needs access to
	 *             other variables or runtime information (e.g., a "today()"
	 *             function).
	 * @return The resulting {@link IValue} of the function's execution.
	 */
	IValue call(IValue[] args, ICalcContext ctx);

	/**
	 * Gets the predetermined, fixed data type of the value that this function
	 * returns.
	 *
	 * @return The non-null {@link ValueType} that this function always returns.
	 */
	ValueType getReturnType();

	/**
	 * Gets the data types that define the function's parameter signature. The
	 * meaning of this array depends on the value of {@link #lastOneIsVararg()}.
	 *
	 * @return An array of {@link ValueType}s defining the function signature.
	 */
	ValueType[] getParameterTypes();

	/**
	 * Specifies if the function has a variadic parameter.
	 * <p>
	 * If this method returns {@code true}, the function is variadic. The contract
	 * is that the type of the *last element* in the array from
	 * {@link #getParameterTypes()} is the type of the repeating argument. This
	 * repeating argument can occur zero or more times after all fixed arguments
	 * have been provided.
	 * <p>
	 * For example, for a signature {@code myFunc(String, Number...)}:
	 * <ul>
	 * <li>{@code getParameterTypes()} would return {@code {STRING, NUMBER}}.</li>
	 * <li>{@code lastOneIsVararg()} would return {@code true}.</li>
	 * <li>This implies one required STRING argument, followed by zero or more
	 * NUMBER arguments.</li>
	 * </ul>
	 * For a signature {@code list(String...)} (zero or more strings):
	 * <ul>
	 * <li>{@code getParameterTypes()} would return {@code {STRING}}.</li>
	 * <li>{@code lastOneIsVararg()} would return {@code true}.</li>
	 * </ul>
	 *
	 * @return {@code true} if the last defined parameter is variadic, {@code false}
	 *         otherwise.
	 */
	boolean lastOneIsVararg();
}