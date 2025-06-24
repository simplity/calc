package org.simplity.calc.impl;

import java.util.Map;

import org.simplity.calc.api.ValueType;
import org.simplity.calc.config.CalcConfig;
import org.simplity.calc.config.CalcRule;
import org.simplity.calc.config.ConditionalExpression;
import org.simplity.calc.config.DataElement;
import org.simplity.calc.config.ValueSchema;
import org.simplity.calc.impl.ConditionalRuleExpression.RuleCase;

class ConfigParser {
	private static final String SCHEMA = "schema";
	private static final String DATA_ELEMENT = "data-element";
	private static final String RULE = "data-element";

	private ConfigParser() {
	}

	static void parse(CalcConfig config, IParserContext ctx) {
		Map<String, ValueSchema> schemas = config.schemas;
		if (schemas == null || schemas.isEmpty()) {
			// error?. not yet. let's ignore it. Let the user know about missing valueSchema
			// later
		} else {
			parseSchemas(schemas, ctx);
		}

		Map<String, DataElement> elements = config.dataElements;
		if (elements == null || elements.isEmpty()) {
			ctx.logError("No data elements specified. Nothing to calculate", "", "");
			return;
		}

		/*
		 * boot context needs to know about all the variables before parsing
		 * expressions. So, we parse dataElements in two steps. 1. get only the name and
		 * valueTypes
		 */
		parseValueTypes(elements, ctx);

		/*
		 * create expression parser
		 */
		IExpressionParser parser = new ExpressionParser(ctx);

		/**
		 * parse data elements
		 */
		parseDataElements(elements, parser, ctx);

	}

	/**
	 *
	 * @param schemas non-null non-empty
	 * @param ctx
	 */
	static void parseSchemas(Map<String, ValueSchema> schemas, IParserContext ctx) {
		for (Map.Entry<String, ValueSchema> entry : schemas.entrySet()) {
			ValueSchema schema = entry.getValue();
			String name = entry.getKey();
			if (schema.valueType == null) {
				ctx.logError("Schema  must specify a 'valueType' attribute.", SCHEMA, name);
				continue;
			}

			ValueType type = ValueType.valueOf(schema.valueType.toUpperCase());

			switch (type) {
			case NUMBER:
				if (schema.min == null || schema.max == null) {
					ctx.logError("Number Schema  must specify 'min' and 'max' values.", SCHEMA, name);
					continue;
				}
				ctx.addValidator(name,
						new NumberSchema(schema.nbrDecimalPlaces, schema.min.doubleValue(), schema.max.doubleValue()));
				continue;

			case STRING:
				if (schema.maxLength == null) {
					ctx.logError("String Schema  must specify a 'maxLength' attribute.", SCHEMA, name);
					continue;
				}
				ctx.addValidator(name, new StringSchema(schema.minLength != null ? schema.minLength.intValue() : 0,
						schema.maxLength.intValue(), schema.regex));
				continue;

			case DATE:
				if (schema.daysInPast == null || schema.daysInFuture == null) {
					ctx.logError("Date Schema  must specify 'daysInPast' and 'daysInFuture' values.", SCHEMA, name);
					continue;
				}

				ctx.addValidator(name, new DateSchema(schema.daysInPast.intValue(), schema.daysInFuture.intValue()));
				continue;

			default:
				ctx.logError("No value parser implemented for schema type: " + type, SCHEMA, name);
			}
		}
	}

	static void parseDataElements(Map<String, DataElement> elements, IExpressionParser expressionParser,
			IParserContext ctx) {
		for (Map.Entry<String, DataElement> entry : elements.entrySet()) {
			String name = entry.getKey();
			DataElement element = entry.getValue();

			boolean allOk = true;
			// 1: type is required
			int eleType = -1;
			if (element.type == null || element.type.isEmpty()) {
				ctx.logError("type must be specified", DATA_ELEMENT, name);
				allOk = false;
			} else {
				String text = element.type.toLowerCase();
				String[] valids = DataElement.TYPES;
				for (int i = 0; i < valids.length; i++) {
					if (text.equals(valids[i])) {
						eleType = i;
						break;
					}
				}
				if (eleType == -1) {
					ctx.logError('\'' + text + "' is not a valid type.", DATA_ELEMENT, name);
					allOk = false;
				}
			}

			// 2: valueType is required
			ValueType valueType = ctx.getValueType(name);
			int nbrDecimalPlaces = 0;
			if (valueType == null) {
				ctx.logError("valueType must be specified for a data element", DATA_ELEMENT, name);
				allOk = false;
			} else if (valueType == ValueType.NUMBER && element.nbrDecimalPlaces > 0) {
				nbrDecimalPlaces = element.nbrDecimalPlaces;
			}

			// 3: schema is required for an input data element
			IValidator validator = null;
			if (eleType == Variable.INPUT_TYPE) {
				if (element.schemaName == null || element.schemaName.isEmpty()) {
					ctx.logError("schema name must be specified for an input data element", DATA_ELEMENT, name);
					allOk = false;
				}
				validator = ctx.getValidator(element.schemaName);
				if (validator == null) {
					ctx.logError('\'' + element.schemaName + "' is not a valid valueSchema name.", DATA_ELEMENT, name);
					allOk = false;
				}
			}

			// 4: Rule is required unless it is a mandatory-input field
			IParsedRule parsedRule = null;
			CalcRule rule = element.rule;
			if (eleType == Variable.INPUT_TYPE && element.isRequired) {
				if (rule != null) {
					ctx.logError("A rule should not be specified for a mandatory input.", DATA_ELEMENT, name);
					allOk = false;
				}
			} else if (rule == null) {
				ctx.logError("A rule must be specified to calculate the value for this data element.", DATA_ELEMENT,
						name);
				allOk = false;
			} else {
				parsedRule = parseRule(rule, name, expressionParser, valueType, ctx);
				if (parsedRule == null) {
					allOk = false;
				}
			}

			if (!allOk) {
				continue;
			}

			Variable variable = new Variable(name, eleType, valueType, element.isRequired, validator, parsedRule,
					nbrDecimalPlaces);
			ctx.addVariable(variable);
			if (eleType == Variable.INPUT_TYPE) {
				ctx.addInputName(name);
			} else if (eleType == Variable.OUTPUT_TYPE) {
				ctx.addOutputName(name);
			}
		}

	}

	static void parseValueTypes(Map<String, DataElement> elements, IParserContext ctx) {
		for (Map.Entry<String, DataElement> entry : elements.entrySet()) {
			String name = entry.getKey();
			DataElement element = entry.getValue();

			ValueType valueType = null;
			if (element.valueType == null || element.valueType.isEmpty()) {
				ctx.logError("valueType me must be specified for an input data element", DATA_ELEMENT, name);
				continue;
			}
			try {
				valueType = ValueType.valueOf(element.valueType.toUpperCase());
				ctx.addValueType(name, valueType);
			} catch (IllegalArgumentException e) {
				ctx.logError('\'' + element.valueType + "' is not a valid valueType.", DATA_ELEMENT, name);
			}
		}

	}

	static private IParsedRule parseRule(CalcRule rule, String name, IExpressionParser expressionParser,
			ValueType expectedValueType, IParserContext ctx) {
		boolean allOk = true;
		String expr = rule.defaultExpression;

		IExpression defaultExpression = null;
		RuleCase[] ruleCases = null;
		if (expr == null || expr.isEmpty()) {
			ctx.logError("A rule must specify defaultExpression.", RULE, name);
			allOk = false;
		} else {
			defaultExpression = expressionParser.parse(expr, name);
			if (defaultExpression == null) {
				allOk = false;
			}
		}

		ConditionalExpression[] ruleLines = rule.conditionalExpressions;
		if (ruleLines != null && ruleLines.length > 0) {
			int n = ruleLines.length;
			ruleCases = new RuleCase[n];
			n = 0;
			for (ConditionalExpression exp : ruleLines) {
				IExpression condition = expressionParser.parse(exp.condition, name);
				IExpression expression = expressionParser.parse(exp.expression, name);
				if (condition == null || expression == null) {
					allOk = false;
				} else {
					ruleCases[n] = new RuleCase(condition, expression);
				}
			}
		}

		if (!allOk) {
			return null;
		}
		return new ConditionalRuleExpression(ruleCases, defaultExpression, expectedValueType);
	}

}
