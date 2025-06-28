package org.simplity.calc.impl;

import java.util.Map;

import org.simplity.calc.api.ValueType;
import org.simplity.calc.config.CalcConfig;
import org.simplity.calc.config.CalcRule;
import org.simplity.calc.config.DataElement;
import org.simplity.calc.config.ElementType;
import org.simplity.calc.config.RuleScenario;
import org.simplity.calc.config.ValueSchema;
import org.simplity.calc.impl.ConditionalRuleExpression.RuleCase;

class ConfigParser {
	private static final String SCHEMA = "schema";
	private static final String DATA_ELEMENT = "data-element";
	private static final String RULE = "rule";

	private final IParserContext ctx;
	private final CalcConfig config;

	ConfigParser(CalcConfig config, IParserContext ctx) {
		this.config = config;
		this.ctx = ctx;
	}

	void parse() {
		this.parseSchemas();

		Map<String, DataElement> elements = this.config.dataElements;
		if (elements == null || elements.isEmpty()) {
			this.ctx.logError("No data elements specified. Nothing to calculate", "", "");
			return;
		}

		/**
		 * parse data elements
		 */
		this.parseDataElements();
		/**
		 * parse rules
		 */
		this.parseRules();

	}

	/**
	 *
	 * @param schemas non-null non-empty
	 * @param ctx
	 */
	void parseSchemas() {
		Map<String, ValueSchema> schemas = this.config.schemas;
		if (schemas == null || schemas.isEmpty()) {
			// error?. not yet. let's ignore it. Let the user know about missing valueSchema
			// later
			return;
		}

		for (Map.Entry<String, ValueSchema> entry : schemas.entrySet()) {
			ValueSchema schema = entry.getValue();
			String name = entry.getKey();
			if (schema.valueType == null) {
				this.ctx.logError("Schema  must specify a 'valueType' attribute.", SCHEMA, name);
				continue;
			}

			ValueType type = null;
			try {
				type = ValueType.valueOf(schema.valueType.toUpperCase());
			} catch (Exception e) {
				this.ctx.logError("value type '" + schema.valueType + "' is not valid.", SCHEMA, name);
				continue;
			}

			switch (type) {
			case NUMBER:
				if (schema.min == null || schema.max == null) {
					this.ctx.logError("Number Schema  must specify 'min' and 'max' values.", SCHEMA, name);
					continue;
				}
				this.ctx.addValidator(name,
						new NumberSchema(schema.nbrDecimalPlaces, schema.min.doubleValue(), schema.max.doubleValue()));
				continue;

			case STRING:
				if (schema.maxLength == null) {
					this.ctx.logError("String Schema  must specify a 'maxLength' attribute.", SCHEMA, name);
					continue;
				}
				this.ctx.addValidator(name, new StringSchema(schema.minLength != null ? schema.minLength.intValue() : 0,
						schema.maxLength.intValue(), schema.regex));
				continue;

			case DATE:
				if (schema.daysInPast == null || schema.daysInFuture == null) {
					this.ctx.logError("Date Schema  must specify 'daysInPast' and 'daysInFuture' values.", SCHEMA,
							name);
					continue;
				}

				this.ctx.addValidator(name,
						new DateSchema(schema.daysInPast.intValue(), schema.daysInFuture.intValue()));
				continue;

			default:
				this.ctx.logError("No value parser implemented for schema type: " + type, SCHEMA, name);
			}
		}
	}

	void parseDataElements() {
		for (Map.Entry<String, DataElement> entry : this.config.dataElements.entrySet()) {
			String name = entry.getKey();
			DataElement element = entry.getValue();

			boolean allOk = true;
			// 1: type is required
			ElementType eleType = null;
			if (element.type == null || element.type.isEmpty()) {
				this.ctx.logError("type must be specified", DATA_ELEMENT, name);
				allOk = false;
			} else {
				try {
					eleType = ElementType.valueOf(element.type.toUpperCase());
				} catch (Exception e) {
					this.ctx.logError("type '" + element.type + "' is not valid", DATA_ELEMENT, name);
					allOk = false;
				}
			}

			// 2: valueType is required
			ValueType valueType = null;
			if (element.valueType == null || element.valueType.isEmpty()) {
				this.ctx.logError("valueType must be specified for an input data element", DATA_ELEMENT, name);
				allOk = false;
			} else {
				try {
					valueType = ValueType.valueOf(element.valueType.toUpperCase());
				} catch (IllegalArgumentException e) {
					this.ctx.logError('\'' + element.valueType + "' is not a valid valueType.", DATA_ELEMENT, name);
					allOk = false;
				}
			}

			int nbrDecimalPlaces = 0;
			if (valueType == ValueType.NUMBER && element.nbrDecimalPlaces > 0) {
				nbrDecimalPlaces = element.nbrDecimalPlaces;
			}

			// 3: schema is required for an input data element
			IValidator validator = null;
			if (eleType == ElementType.REQUIRED_INPUT || eleType == ElementType.OPTIONAL_INPUT) {
				if (element.schemaName == null || element.schemaName.isEmpty()) {
					this.ctx.logError("schema name must be specified for an input data element", DATA_ELEMENT, name);
					allOk = false;
				}
				validator = this.ctx.getValidator(element.schemaName);
				if (validator == null) {
					this.ctx.logError('\'' + element.schemaName + "' is not a valid valueSchema name.", DATA_ELEMENT,
							name);
					allOk = false;
				}
			}

			IParsedRule parsedRule = null;
			CalcRule rule = element.rule;
			if (rule == null && eleType != ElementType.REQUIRED_INPUT) {
				this.ctx.logError("A rule must be specified to calculate the value for this data element.",
						DATA_ELEMENT, name);
				allOk = false;
			}
			if (rule != null && eleType == ElementType.REQUIRED_INPUT) {
				this.ctx.logError("A rule should not be specified for a mandatory input.", DATA_ELEMENT, name);
				allOk = false;
			}

			if (allOk) {
				Variable variable = new Variable(name, eleType, valueType, validator, parsedRule, nbrDecimalPlaces);
				this.ctx.addVariable(variable);
			}
		}
	}

	void parseRules() {
		/*
		 * create expression parser
		 */
		IExpressionParser expressionParser = new ExpressionParser(this.ctx);

		for (Map.Entry<String, DataElement> entry : this.config.dataElements.entrySet()) {
			DataElement element = entry.getValue();
			CalcRule rule = element.rule;
			if (rule == null) {
				// rule requirement is already checked
				continue;
			}

			String variableName = entry.getKey();
			Variable variable = this.ctx.getVariable(variableName);
			ValueType valueType = variable == null ? ValueType.NUMBER : variable.getValueType();
			IParsedRule parsedRule = this.parseRule(rule, variableName, expressionParser, valueType);

			if (variable != null && parsedRule != null) {
				variable.setRule(parsedRule);
			}

		}

	}

	private IParsedRule parseRule(CalcRule rule, String name, IExpressionParser expressionParser,
			ValueType expectedValueType) {
		boolean allOk = true;
		String expr = rule.defaultExpression;

		IExpression defaultExpression = null;
		RuleCase[] ruleCases = null;
		if (expr == null || expr.isEmpty()) {
			this.ctx.logError("A rule must specify defaultExpression.", RULE, name);
			allOk = false;
		} else {
			defaultExpression = expressionParser.parse(expr, name);
			if (defaultExpression == null) {
				String msg = "Expresion '" + expr + "' is invalid.";
				this.ctx.logError(msg, "expression", msg);
				allOk = false;
			} else if (!defaultExpression.getValueType().equals(expectedValueType)) {
				String msg = "Variable '" + name + "' is a '" + expectedValueType.name().toLowerCase()
						+ "' but the default expression evaluates to a "
						+ defaultExpression.getValueType().name().toLowerCase() + "'.";
				this.ctx.logError(msg, "expression", msg);
				allOk = false;
			}
		}

		RuleScenario[] scenarios = rule.scenarios;
		if (scenarios != null && scenarios.length > 0) {
			int n = scenarios.length;
			ruleCases = new RuleCase[n];
			n = 0;
			for (RuleScenario exp : scenarios) {
				IExpression condition = expressionParser.parse(exp.condition, name);
				IExpression expression = expressionParser.parse(exp.expression, name);
				if (condition != null && expression != null) {
					ruleCases[n] = new RuleCase(condition, expression);
					continue;
				}

				allOk = false;
				if (condition == null) {
					String msg = "Expresion '" + exp.condition + "' is invalid.";
					this.ctx.logError(msg, "expression", msg);
				}

				if (expression == null) {
					String msg = "Expresion '" + exp.expression + "' is invalid.";
					this.ctx.logError(msg, "expression", msg);
				}
			}
		}

		if (!allOk) {
			return null;
		}
		return new ConditionalRuleExpression(ruleCases, defaultExpression, expectedValueType);
	}

}
