package org.simplity.calc.engine.api;

/**
 *
 */
public interface IValueType {

	/**
	 *
	 * @return data type
	 */
	DataType getDataType();

	/**
	 *
	 * @return printable name of the data type, in lower case
	 */
	String getDataTypeName();

	/**
	 *
	 * @return name of the underlying value type, like the name of the Enum.
	 */
	String getValueTypeName();
}
