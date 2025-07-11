package org.simplity.calc.engine.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.IValueType;
import org.simplity.calc.engine.config.DataElementDS;

/**
 * utility class (factory-pattern) to provide instances of IValueType
 */
public class ValueTypes {
	/**
	 * represents a numeric value type
	 */
	public static final IValueType NUMBER = new SimpleType(DataType.NUMBER);

	/**
	 * represents a date value type
	 */
	public static final IValueType DATE = new SimpleType(DataType.DATE);

	/**
	 * represents a string value type
	 */
	public static final IValueType STRING = new SimpleType(DataType.STRING);

	/**
	 * represents a boolean value type
	 */
	public static final IValueType BOOLEAN = new SimpleType(DataType.BOOLEAN);

	/**
	 * represents a time-stamp value type
	 */
	public static final IValueType TIMESTAMP = new SimpleType(DataType.TIMESTAMP);

	private static Map<DataType, IValueType> PRIMITIVES = new HashMap<>();
	static {
		PRIMITIVES.put(DataType.NUMBER, NUMBER);
		PRIMITIVES.put(DataType.DATE, DATE);
		PRIMITIVES.put(DataType.STRING, STRING);
		PRIMITIVES.put(DataType.BOOLEAN, BOOLEAN);
		PRIMITIVES.put(DataType.TIMESTAMP, TIMESTAMP);
	}

	/**
	 *
	 * @param dataType
	 * @return an IValue instance for this primitive dataType
	 *
	 * @throws IllegalArgumentException if the dataType is not primitive
	 *
	 */

	public static IValueType newPrimitiveType(DataType dataType) {
		// return singletons rather than creating unnecessary instances
		IValueType type = PRIMITIVES.get(dataType);
		if (type == null) {
			throw new IllegalArgumentException(
					"A primitive value type can not be created for a non-primitive dataType '" + dataType.name() + "'");
		}
		return type;
	}

	/**
	 *
	 * @param valueTypeName unique name/id of the underlying value
	 * @return an IValue instance for this non primitive dataType
	 *
	 */
	public static IValueType newEnumType(String valueTypeName) {
		return new NamedType(DataType.ENUM, valueTypeName);
	}

	/**
	 *
	 * @param valueTypeName unique name/id of the underlying value
	 * @return an IValue instance for this non primitive dataType
	 *
	 */
	public static IValueType newDataStructureType(String valueTypeName) {
		return new NamedType(DataType.DS, valueTypeName);
	}

	/**
	 *
	 * @param valueTypeName unique name/id of the underlying value
	 * @return an IValue instance for this non primitive dataType
	 *
	 */
	public static IValueType newTableType(String valueTypeName) {
		return new NamedType(DataType.TABLE, valueTypeName);
	}

	/**
	 * internally used while building an engine
	 *
	 */
	static IValueType parseValueType(DataElementDS element, IEngineBuilder engineBuilder, String name) {
		/*
		 * 1: dataType is required
		 */
		String dt = element.dataType;
		if (dt == null || dt.isEmpty()) {
			engineBuilder.logError("dataType be specified for an input data element", "DataElement", name);
			return null;
		}

		/*
		 * 2: dataType is of the form "NUMBER" or "ENUM:enum-Name"
		 */
		String[] parts = dt.split(":");

		DataType dataType = null;
		final String text = parts[0].trim().toUpperCase();
		try {
			dataType = DataType.valueOf(text);
		} catch (IllegalArgumentException e) {
			engineBuilder.logError('\'' + text + "' is not a valid dataType.", "DataElement", name);
			return null;
		}

		if (parts.length == 0) {
			/*
			 * 3: un-named data type should be primitive-type
			 */
			IValueType vt = PRIMITIVES.get(dataType);
			if (vt == null) {
				engineBuilder.logError(
						'\'' + text + "' is a named-type. name must be specified like '" + text + ":some_name' ",
						"DataElement", name);
			}
			return vt;
		}

		/*
		 * 4. named-type should be of the form dt:name
		 */
		if (parts.length > 2) {
			engineBuilder.logError("'" + dt
					+ "' is not a valid named data type. Named dataType should be of the form ENUM:enumName is not a valid dataType.",
					"DataElement", name);
			return null;
		}
		String dtName = parts[1].trim().toLowerCase();
		switch (dataType) {
		case ENUM:
			if (engineBuilder.getEnumValues(dtName) != null) {
				return newEnumType(dtName);
			}
			engineBuilder.logError("'" + dtName + "' is not a valid enum definition.", "DataElement", name);
			return null;

		case DS:
			if (engineBuilder.getEnumValues(dtName) != null) {
				return newEnumType(dtName);
			}
			engineBuilder.logError("'" + dtName + "' is not a valid dataStructure definition.", "DataElement", name);
			return null;

		case TABLE:
			if (engineBuilder.getEnumValues(dtName) != null) {
				return newEnumType(dtName);
			}
			engineBuilder.logError("'" + dtName + "' is not a valid table definition.", "DataElement", name);
			return null;

		default:
			engineBuilder.logError("Named dataType '" + dataType + "' is not fully implemented", "DataElement", name);
			return null;

		}
	}

	protected static class SimpleType implements IValueType {
		private final DataType dataType;

		/**
		 * internally guaranteed that this is called only for primitive types
		 */
		protected SimpleType(DataType dataType) {
			this.dataType = dataType;
		}

		@Override
		public DataType getDataType() {
			return this.dataType;
		}

		@Override
		public String getDataTypeName() {
			return this.dataType.name();
		}

		@Override
		public String getValueTypeName() {
			return this.dataType.name();
		}

		@Override
		public String toString() {
			return this.dataType.name();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof IValueType == false) {
				return false;
			}

			IValueType other = (IValueType) obj;
			return this.toString().equals(other.toString());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.toString());
		}
	}

	protected static class NamedType extends SimpleType {
		private final String valueTypeName;

		/**
		 * internally guaranteed that this is called only for non-primitive types
		 */
		protected NamedType(DataType dataType, String valueTypeName) {
			super(dataType);
			this.valueTypeName = valueTypeName;
		}

		@Override
		public String getValueTypeName() {
			return this.valueTypeName;
		}

		@Override
		public String toString() {
			return this.getDataTypeName() + ':' + this.valueTypeName;
		}
	}

	private ValueTypes() {
		// not allowed
	}
}
