package org.simplity.calc.impl;

import org.simplity.calc.api.ICalcContext;
import org.simplity.calc.api.IValue;
import org.simplity.calc.api.ValueType;

/**
 * Internal, immutable class representing the complete definition of a variable.
 */
class Variable {
	// IMP: following MUST be aligned with DataDefinition.TYPES
	public static final int INPUT_TYPE = 0;
	public static final int OUTPUT_TYPE = 1;
	public static final int INTERMEDIATE = 2;

	private final String variableName;
	private final int variableType;
	private final ValueType valueType;
	private final boolean isRequired;
	private final IValidator validator;
	private final IParsedRule rule;
	// We add these from the NumberSchema for easy access during rounding.
	private final int nbrDecimalPlaces;

	/**
	 *
	 * @param name
	 * @param type
	 * @param valueType
	 * @param isRequired
	 * @param parser
	 * @param rule
	 * @param nbrDecimalPlaces
	 */
	Variable(String name, int type, ValueType valueType, boolean isRequired, IValidator parser, IParsedRule rule,
			int nbrDecimalPlaces) {
		this.variableName = name;
		this.variableType = type;
		this.valueType = valueType;
		this.isRequired = isRequired;
		this.validator = parser;
		this.rule = rule;
		this.nbrDecimalPlaces = nbrDecimalPlaces;
	}

	// Getters...
	public String getName() {
		return this.variableName;
	}

	public boolean isInput() {
		return this.variableType == INPUT_TYPE;
	}

	public boolean isOutput() {
		return this.variableType == OUTPUT_TYPE;
	}

	public ValueType getValueType() {
		return this.valueType;
	}

	public boolean isRequired() {
		return this.isRequired;
	}

	public IValidator getValidator() {
		return this.validator;
	}

	public IParsedRule getRule() {
		return this.rule;
	}

	public int getPrecision() {
		return this.nbrDecimalPlaces;
	}

	public IValue parse(String valueToParse, ICalcContext ctx) {
		if (valueToParse == null || valueToParse.isBlank()) {
			if (this.isRequired) {
				ctx.logError(this.variableName, "Value is required");
			}
			return null;
		}
		return this.validator.validate(valueToParse.trim(), ctx, this.variableName);
	}

	public IValue evaluate(ICalcContext ctx) {
		if (this.rule == null) {
			ctx.logError(this.variableName, "No rule specified but being evaluated");
			return null;
		}
		return this.rule.apply(ctx);
	}

}
