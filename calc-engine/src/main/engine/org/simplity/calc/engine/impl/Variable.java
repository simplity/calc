package org.simplity.calc.engine.impl;

import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.IValue;
import org.simplity.calc.engine.api.ValueType;
import org.simplity.calc.engine.config.ElementType;

/**
 * Internal, immutable class representing the complete definition of a variable.
 */
class Variable {
	// IMP: following MUST be aligned with DataDefinition.TYPES
	public static final int INPUT_TYPE = 0;
	public static final int OUTPUT_TYPE = 1;
	public static final int INTERMEDIATE = 2;

	private final String variableName;
	private final ElementType type;
	private final ValueType valueType;
	private final IValidator validator;
	// We add these from the NumberSchema for easy access during rounding.
	private final int nbrDecimalPlaces;

	private final boolean isInput;
	private final boolean isRequiredInput;
	private final boolean isOutput;

	/**
	 * The parsing process design requires us to first create all variables without
	 * the rules, and then parse and add the rules hence we are unable to make this
	 * "final". However, we are ensuring that the rule is set once and for all
	 */
	private IParsedRule rule;

	/**
	 *
	 * @param name
	 * @param type
	 * @param valueType
	 * @param parser
	 * @param rule
	 * @param nbrDecimalPlaces
	 */
	Variable(String name, ElementType type, ValueType valueType, IValidator parser, IParsedRule rule,
			int nbrDecimalPlaces) {
		this.variableName = name;
		this.type = type;
		this.valueType = valueType;
		this.validator = parser;
		this.rule = rule;
		this.nbrDecimalPlaces = nbrDecimalPlaces;

		this.isRequiredInput = this.type.equals(ElementType.REQUIRED_INPUT);
		this.isInput = this.isRequiredInput || this.type.equals(ElementType.OPTIONAL_INPUT);
		this.isOutput = this.type.equals(ElementType.OUTPUT);
	}

	// Getters...
	public String getName() {
		return this.variableName;
	}

	public boolean isRequiredInput() {
		return this.isRequiredInput;
	}

	public boolean isOutput() {
		return this.isOutput;
	}

	public ValueType getValueType() {
		return this.valueType;
	}

	public boolean isInput() {
		return this.isInput;
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

	public void setRule(IParsedRule rule) {
		if (this.rule != null) {
			throw new IllegalStateException("Rule is already set for the variable '" + this.variableName
					+ "'. Once set, the rule should not be reset");
		}
		this.rule = rule;
	}

	public IValue parse(String valueToParse, ICalcContext ctx) {
		if (this.validator == null) {
			throw new IllegalArgumentException("Variable '" + this.variableName + "' is of type '"
					+ this.type.name().toLowerCase() + "'. Hence parse() functionality is not valid");
		}
		if (valueToParse == null || valueToParse.isBlank()) {
			if (this.isRequiredInput) {
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

	public boolean dryrun(IDryrunContext dryCtx) {
		if (this.isInput) {
			/*
			 * TODO: we can not determine this one because we are not sure whether this is
			 * being evaluated after asserting it's presence or not.
			 */
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
