package org.simplity.calc.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.simplity.calc.engine.api.CalcErrorDS;
import org.simplity.calc.engine.api.ICalcContext;
import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEngineShipment;
import org.simplity.calc.engine.api.IValueType;
import org.simplity.calc.engine.config.CalcConfigDS;
import org.simplity.calc.engine.config.CalculatorDS;
import org.simplity.calc.engine.config.DataElementDS;
import org.simplity.calc.engine.config.ElementType;
import org.simplity.calc.engine.config.ValidatorDS;
import org.simplity.calc.engine.config.ValueSchemaDS;

/**
 * assembles an instance of ICalcEngine based on the supplied configuration
 * data.
 */

class EngineBuilder implements IEngineBuilder {

	private static final CalcErrorDS[] ARRAY = {};
	/*
	 * input received at the time of constructor
	 */
	private final CalcConfigDS config;

	private final Map<String, Set<String>> enums = new HashMap<>();
	private final Map<String, Map<String, String>> dataStructures = new HashMap<>();
	private final Map<String, Map<String, String>> tables = new HashMap<>();
	/*
	 * components that are produced. Shared directly with the parent with
	 * "protected"
	 */
	private final Map<String, ICalcFunction> allFunctions = new HashMap<>();
	protected final List<CalcErrorDS> errors = new ArrayList<>();
	protected final Map<String, IValueParser> valueParsers = new HashMap<>();
	protected final Map<String, IVariable> variables = new HashMap<>();
	protected final Set<String> inputVariables = new HashSet<>();
	protected final Set<String> outputVariables = new HashSet<>();
	protected IValidator[] validators;

	// for parsing expressions
	protected final ExpressionBuilder exprBuilder;

	/**
	 * Initializes the context and function registry.
	 */
	protected EngineBuilder(CalcConfigDS config, Map<String, ICalcFunction> customFunctions) {
		this.config = config;
		this.exprBuilder = new ExpressionBuilder(this);
		/*
		 * NOTE: we want to be case-insensitive for matching the functions, especially
		 * because it is going to come from an external source Builtin functions are all
		 * indexed by lower-case name.
		 */
		BuiltinFunctions.getAll(this.allFunctions);

		// Load custom functions, checking for name clashes.
		for (Map.Entry<String, ICalcFunction> entry : customFunctions.entrySet()) {
			String name = entry.getKey().toLowerCase();
			if (this.allFunctions.containsKey(name)) {
				this.logError("Function name '" + name
						+ "' is a built-in name and cannot be overridden. Note that the function names are case-insensitive",
						"function", name);
			} else {
				this.allFunctions.put(name, entry.getValue());
			}
		}
	}

	protected IEngineShipment build() {
		/*
		 * process all components from the config object and load them into the context.
		 */
		this.processConfig();

		if (this.errors.size() == 0) {

			/*
			 * Perform a dry run on all output variables to detect circular dependencies.
			 */
			DryrunContext dryCtx = new DryrunContext(this.variables, this.errors);
			for (String s : this.outputVariables) {
				this.variables.get(s).dryrun(dryCtx);
			}

			for (IValidator v : this.validators) {
				v.dryrun(dryCtx);
			}
		}

		if (this.errors.size() > 0) {
			return new EngineShipment(this.errors.toArray(ARRAY));
		}

		final String[] StringArr = {};
		final String[] inputs = this.inputVariables.toArray(StringArr);
		final String[] outputs = this.outputVariables.toArray(StringArr);
		Map<String, String> messages = new HashMap<>();
		if (this.config.messages != null) {
			messages.putAll(this.config.messages);
		}
		final ICalcEngine engine = new CalcEngine(this.variables, this.validators, messages, inputs, outputs);
		return new EngineShipment(engine);
	}

	private void processConfig() {
		this.processSchemas();

		Map<String, DataElementDS> elements = this.config.dataElements;
		if (elements == null || elements.isEmpty()) {
			this.logError("No data elements specified. Nothing to calculate", "", "");
			return;
		}

		this.processDataElements();
		this.buildCalculators();
		this.buildValidators();
	}

	void processSchemas() {
		Map<String, ValueSchemaDS> schemas = this.config.schemas;
		if (schemas == null || schemas.isEmpty()) {
			// error?. not yet. let's ignore it. Let the user know about missing valueSchema
			// later
			return;
		}

		for (Map.Entry<String, ValueSchemaDS> entry : schemas.entrySet()) {
			ValueSchemaDS schema = entry.getValue();
			String name = entry.getKey();

			IValueParser parser = ValueParsers.buildParser(schema, name, this);
			if (parser != null) {
				this.addValidator(name, parser);
			}
		}
	}

	void processDataElements() {
		for (Map.Entry<String, DataElementDS> entry : this.config.dataElements.entrySet()) {
			String name = entry.getKey();
			DataElementDS element = entry.getValue();
			String type = element.type;
			if (type != null && ElementType.CONSTANT.name().equals(type.toUpperCase())) {
				// this.addConstant(element, name);
			}

			IVariable variable = Variables.toVariable(element, name, this);
			if (variable != null) {
				this.addVariable(variable);
			}
		}
	}

	void buildCalculators() {
		for (Map.Entry<String, DataElementDS> entry : this.config.dataElements.entrySet()) {
			DataElementDS element = entry.getValue();
			CalculatorDS ds = element.calculator;
			if (ds == null) {
				// rule requirement is already checked
				continue;
			}

			String variableName = entry.getKey();
			IVariable variable = this.getVariable(variableName);
			IValueType valueType = variable == null ? ValueTypes.NUMBER : variable.getValueType();
			ICalculator parsedRule = Calculators.buildCalculator(ds, variableName, valueType, this.exprBuilder);

			if (variable != null && parsedRule != null) {
				variable.setRule(parsedRule);
			}
		}
	}

	void buildValidators() {
		ValidatorDS[] vds = this.config.validators;
		if (vds == null || vds.length == 0) {
			this.validators = new IValidator[0];
			return;
		}

		this.validators = new IValidator[vds.length];
		int i = -1;
		for (ValidatorDS ds : vds) {
			i++;
			final IExpression exp = this.exprBuilder.parse(ds.shouldBe, "validator", ValueTypes.BOOLEAN);
			if (ds.messageId == null) {
				this.logError("messageId is required for validators", "validator", "" + i);
			}
			if (exp != null && ds.messageId != null) {
				this.validators[i] = new Validator(exp, ds.messageId);
			}
		}
	}

	@Override
	public void addValidator(String name, IValueParser validator) {
		this.valueParsers.put(name, validator);
	}

	@Override
	public IValueParser getValidator(String name) {
		return this.valueParsers.get(name);
	}

	@Override
	public void addVariable(IVariable variable) {
		String name = variable.getName();
		this.variables.put(name, variable);
		if (variable.isInput()) {
			this.inputVariables.add(name);
		}
		if (variable.isOutput()) {
			this.outputVariables.add(name);
		}
	}

	@Override
	public ICalcFunction getFunction(String functionName) {
		return this.allFunctions.get(functionName.toLowerCase());
	}

	@Override
	public IVariable getVariable(String variableName) {
		return this.variables.get(variableName);
	}

	@Override
	public void logError(String message, String entityType, String entityName) {
		this.errors.add(new CalcErrorDS(entityType + ':' + entityName, message));
	}

	@Override
	public Set<String> getEnumValues(String enumName) {
		return this.enums.get(enumName);
	}

	@Override
	public Map<String, String> getDataStructureDS(String name) {
		return this.dataStructures.get(name);
	}

	@Override
	public Map<String, String> getTableDS(String name) {
		return this.tables.get(name);
	}

	/**
	 * Represents a sequence of if-condition-then-value case. It extends the
	 * Expression rule, which acts as a default if none of the if-conditions come
	 * true
	 */
	private static class Validator implements IValidator {
		private final IExpression shouldBe;
		private final String messageId;

		protected Validator(IExpression shouldBe, String messageId) {
			this.shouldBe = shouldBe;
			this.messageId = messageId;
		}

		@Override
		public boolean validate(ICalcContext ctx) {
			if (this.shouldBe.evaluate(ctx).getBooleanValue()) {
				return true;
			}
			ctx.logError("validation", this.messageId);
			return false;
		}

		@Override
		public boolean dryrun(DryrunContext ctx) {
			return this.shouldBe.dryrun(ctx);
		}

	}

}
