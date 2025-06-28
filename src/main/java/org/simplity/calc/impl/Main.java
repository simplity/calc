package org.simplity.calc.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simplity.calc.api.ICalcEngine;
import org.simplity.calc.api.ICalcError;
import org.simplity.calc.api.ICalcResult;

/**
 *
 */
public class Main {
	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		/*
		 * Define the path to our config file in the classpath.
		 */
		String configResourcePath = "/org/simplity/calc/example-config.yaml";

		/*
		 * Bootstrap the Engine using the Factory's "fromYamlResource" method.
		 */
		System.out.println("--- Bootstrapping Engine from " + configResourcePath + " ---");
		List<ICalcError> bootstrapErrors = new ArrayList<>();
		ICalcEngine engine = CalcEngineFactory.fromYamlResource(configResourcePath, new HashMap<>(), bootstrapErrors);

		if (engine == null) {
			System.err.println("Engine creation failed with " + bootstrapErrors.size() + " errors:");
			for (ICalcError error : bootstrapErrors) {
				System.err.println("  " + error);
			}
			return;
		}
		System.out.println("Engine bootstrapped successfully.");

		// --- RUN 1: Employee with no bonus provided (engine will use default of 0) ---
		runCalculation1(engine);

		// --- RUN 2: Employee with a bonus provided ---
		runCalculation2(engine);
	}

	private static void runCalculation1(ICalcEngine engine) {
		// Prepare inputs, but DO NOT provide the optional 'bonus_percentage'.
		Map<String, String> inputs = new HashMap<>();
		inputs.put("base_salary", "50000");

		System.out.println("\n--- Running Calculation 1 (No Bonus Provided) ---");
		System.out.println("Inputs: " + inputs);

		ICalcResult result = engine.calculate(inputs);
		processResult(result);
	}

	private static void runCalculation2(ICalcEngine engine) {
		// Prepare inputs, including the optional 'bonus_percentage'.
		Map<String, String> inputs = new HashMap<>();
		inputs.put("base_salary", "50000");
		inputs.put("bonus_percentage", "0.10"); // 10% bonus

		System.out.println("\n--- Running Calculation 2 (With Bonus Provided) ---");
		System.out.println("Inputs: " + inputs);

		ICalcResult result = engine.calculate(inputs);
		processResult(result);
	}

	private static void processResult(ICalcResult result) {
		if (result.isSuccess()) {
			System.out.println("  Calculation successful!");
			// We'll pretty-print the BigDecimal output for clarity.
			BigDecimal finalSalary = result.getOutputs().get("final_salary").getNumberValue();
			System.out.println("  Output -> final_salary: " + finalSalary.toPlainString());
		} else {
			System.err.println("  Calculation failed with " + result.getErrors().length + " errors:");
			for (ICalcError error : result.getErrors()) {
				System.err.println("  " + error);
			}
		}
	}
}