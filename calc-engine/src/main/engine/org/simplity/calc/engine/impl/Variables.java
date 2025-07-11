package org.simplity.calc.engine.impl;

import java.util.Set;

import org.simplity.calc.engine.api.DataType;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.IValueType;
import org.simplity.calc.engine.config.CalculatorDS;
import org.simplity.calc.engine.config.DataElementDS;
import org.simplity.calc.engine.config.ElementType;

/**
 * Utility class to validate a data element and create an instance of a Variable
 */
public class Variables {
	private static final String DATA_ELEMENT = "data-element";

	/**
	 * build a variable based on the DataElementDs
	 *
	 * @param element
	 * @param name
	 * @param engineBuilder
	 * @return an instance of IVariable, or null in case of any error
	 */
	static IVariable toVariable(DataElementDS element, String name, IEngineBuilder engineBuilder) {
		boolean allOk = true;
		// 1: type is required
		ElementType eleType = null;
		if (element.type == null || element.type.isEmpty()) {
			engineBuilder.logError("type must be specified", DATA_ELEMENT, name);
			allOk = false;
		} else {
			try {
				eleType = ElementType.valueOf(element.type.toUpperCase());
			} catch (Exception e) {
				engineBuilder.logError("type '" + element.type + "' is not valid", DATA_ELEMENT, name);
				allOk = false;
			}
		}

		final IValueType valueType = ValueTypes.parseValueType(element, engineBuilder, name);
		int nbrDecimalPlaces = 0;
		if (ValueTypes.NUMBER.equals(valueType) && element.nbrDecimalPlaces > 0) {
			nbrDecimalPlaces = element.nbrDecimalPlaces;
		}

		// 3: schema is required for an input data element
		IValueParser parser = null;
		String errorId = null;
		if (eleType == ElementType.REQUIRED_INPUT || eleType == ElementType.OPTIONAL_INPUT) {
			errorId = element.errorId;
			if (errorId == null || errorId.isEmpty()) {
				engineBuilder.logError(
						"errorId must be specified for an input data element so that an error message is communicated if the a proper value is not provided for this fieldg c",
						DATA_ELEMENT, name);
				allOk = false;
			}

			if (valueType != null) {
				if (ValueTypes.BOOLEAN.equals(valueType) || ValueTypes.BOOLEAN.equals(valueType)) {
					parser = ValueParsers.BOOLEAN_VALIDATOR;
				} else if (valueType.getDataType() == DataType.ENUM) {
					String enumName = valueType.getValueTypeName();
					Set<String> values = engineBuilder.getEnumValues(enumName);
					parser = new EnumParser(enumName, values);
					engineBuilder.logError("schema name must be specified for an input data element", DATA_ELEMENT,
							name);
					allOk = false;
				} else {
					parser = engineBuilder.getValidator(element.schemaName);
					if (parser == null) {
						engineBuilder.logError('\'' + element.schemaName + "' is not a valid valueSchema name.",
								DATA_ELEMENT, name);
						allOk = false;
					}
				}
			}
		}

		CalculatorDS cds = element.calculator;
		if (cds == null && eleType != ElementType.REQUIRED_INPUT) {
			engineBuilder.logError("A calculator must be specified to calculate the value for this data element.",
					DATA_ELEMENT, name);
			allOk = false;
		}
		if (cds != null && eleType == ElementType.REQUIRED_INPUT) {
			engineBuilder.logError("A calculator should not be specified for a mandatory input.", DATA_ELEMENT, name);
			allOk = false;
		}

		if (allOk) {
			return new Variable(name, eleType, valueType, parser, nbrDecimalPlaces, errorId);
		}
		return null;
	}

	private static class EnumParser implements IValueParser {
		private final Set<String> values;
		private final String enumName;

		protected EnumParser(String enumName, Set<String> values) {
			this.values = values;
			this.enumName = enumName;
		}

		@Override
		public IValue parse(String textValue) {
			String value = textValue.trim().toLowerCase();
			if (this.values.contains(value)) {
				return Values.newEnumeratedValue(this.enumName, value);
			}
			return null;
		}

	}

	private static class Variable implements IVariable {

		private final String variableName;
		private final ElementType type;
		private final IValueType valueType;
		private final IValueParser parser;
		private final int nbrDecimalPlaces;
		private final String errorId;

		private final boolean isInput;
		private final boolean isRequiredInput;
		private final boolean isOutput;

		/**
		 * The parsing process design requires us to first create all variables without
		 * the rules, and then parse and add the rules hence we are unable to make this
		 * "final". However, we are ensuring that the rule is set once and for all
		 */
		private ICalculator rule;

		/**
		 *
		 * @param name
		 * @param type
		 * @param valueType
		 * @param parser
		 * @param nbrDecimalPlaces
		 */
		protected Variable(String name, ElementType type, IValueType valueType, IValueParser parser,
				int nbrDecimalPlaces, String errorId) {
			this.variableName = name;
			this.type = type;
			this.valueType = valueType;
			this.parser = parser;
			this.nbrDecimalPlaces = nbrDecimalPlaces;
			this.errorId = errorId;

			this.isRequiredInput = this.type == ElementType.REQUIRED_INPUT;
			this.isInput = this.isRequiredInput || this.type == ElementType.OPTIONAL_INPUT;
			this.isOutput = this.type == ElementType.OUTPUT;
		}

		// Getters...
		@Override
		public String getName() {
			return this.variableName;
		}

		@Override
		public boolean isRequiredInput() {
			return this.isRequiredInput;
		}

		@Override
		public boolean isOutput() {
			return this.isOutput;
		}

		@Override
		public IValueType getValueType() {
			return this.valueType;
		}

		@Override
		public boolean isInput() {
			return this.isInput;
		}

		@Override
		public IValueParser getValidator() {
			return this.parser;
		}

		@Override
		public ICalculator getRule() {
			return this.rule;
		}

		@Override
		public int getPrecision() {
			return this.nbrDecimalPlaces;
		}

		@Override
		public void setRule(ICalculator rule) {
			if (this.rule != null) {
				throw new IllegalStateException("Rule is already set for the variable '" + this.variableName
						+ "'. Once set, the rule should not be reset");
			}
			this.rule = rule;
		}

		@Override
		public IValue parse(String valueToParse, ICalcContext ctx) {
			if (this.parser == null) {
				throw new IllegalArgumentException("Variable '" + this.variableName + "' is of type '"
						+ this.type.name().toLowerCase() + "'. Hence parse() functionality is not valid");
			}
			if (valueToParse == null || valueToParse.isEmpty()) {
				if (this.isRequiredInput) {
					ctx.logError(this.variableName, this.errorId);
				}
				return null;
			}
			IValue parsedValue = this.parser.parse(valueToParse.trim());
			if (parsedValue == null) {
				ctx.logError(this.variableName, this.errorId);
			}

			return parsedValue;
		}

		@Override
		public IValue evaluate(ICalcContext ctx) {
			if (this.rule == null) {
				ctx.logError(this.variableName,
						"A rule is required for this variable if its value is to be evaluated at run time");
				return null;
			}
			return this.rule.calculate(ctx);
		}

		@Override
		public boolean dryrun(DryrunContext dryCtx) {
			if (this.isRequiredInput) {
				return true;
			}
			if (this.rule == null) {
				/*
				 * shouldn't happen. It's a bug if we reach here
				 */
				throw new IllegalStateException("Variable '" + this.variableName + "' is missing it's rule");
			}
			return this.rule.dryrun(dryCtx);
		}

	}

}
