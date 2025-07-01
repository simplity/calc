package org.simplity.calc.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcError;
import org.simplity.calc.engine.api.ICalcResult;
import org.simplity.calc.engine.impl.CalcEngineFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;

/**
 *
 */
public class WebService {
	private static final String CONFIG_RESOURCE = "/example-config.yaml";
	// We create one engine instance when the service starts up. It's thread-safe.
	private static final ICalcEngine engine = bootstrapEngine();
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Javalin app = Javalin.create().start(7070); // Starts a web server on port 7070

		// This defines our API endpoint: POST /calculate
		app.post("/calculate", ctx -> {
			// 1. Get the JSON input from the request body
			CalculationRequest request = jsonMapper.readValue(ctx.body(), CalculationRequest.class);

			// 2. Call our engine's calculate method
			ICalcResult result = engine.calculate(request.inputs);

			// 3. Convert the result back to JSON and send it as the response
			ctx.json(result);
		});
	}

	private static ICalcEngine bootstrapEngine() {
		System.out.println("Bootstrapping calculation engine...");
		List<ICalcError> errors = new ArrayList<>();
		ICalcEngine ngin = null;
		try {
			ngin = CalcEngineFactory.fromYamlResource(CONFIG_RESOURCE, new HashMap<>(), errors);
		} catch (IOException e) {
			throw new RuntimeException("FATAL: Error while reading cinfig resource " + CONFIG_RESOURCE);
		}

		if (ngin == null) {
			// In a real app, you'd log errors more gracefully
			throw new RuntimeException("FATAL: Engine failed to bootstrap. Check logs.");
		}
		System.out.println("Engine ready.");
		return ngin;
	}

	/**
	 * A simple class to represent the incoming JSON request
	 */
	public static class CalculationRequest {
		/**
		 *
		 */
		public Map<String, String> inputs;
	}
}