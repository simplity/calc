package org.simplity.calc.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.simplity.calc.engine.api.CalcErrorDS;
import org.simplity.calc.engine.api.CalcResultDS;
import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.ICalcFunction;
import org.simplity.calc.engine.api.IEngineShipment;
import org.simplity.calc.engine.config.CalcConfigDS;
import org.simplity.calc.engine.impl.CalcEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

/**
 *
 */
public class WebServer extends Server {
	protected static final Logger logger = LoggerFactory.getLogger(WebServer.class);
	private static final String DEFAULT_CONTEXT = "calculate";

	public static void main(String[] args) {
		String jsonResource = "example-config.json";
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(jsonResource);

		if (inputStream == null) {
			logger.error("Resource '{}' could not be located for configuring the engine. Aborting.", jsonResource);
		}

		try (Reader reader = new InputStreamReader(inputStream)) {
			final CalcConfigDS config = new Gson().fromJson(reader, CalcConfigDS.class);
			WebServer.newServer(7070, config, new HashMap<>());
		} catch (Exception e) {
			logger.error("Error while bootstrapping the engine: " + e.getMessage());
		}
	}

	/**
	 *
	 * @param port
	 * @param config
	 * @param customFunctions
	 * @return null in case errors
	 */
	public static WebServer newServer(int port, CalcConfigDS config, Map<String, ICalcFunction> customFunctions) {
		try {
			IEngineShipment shipment = CalcEngines.newEngine(config, customFunctions);
			if (!shipment.allOk()) {
				logger.error("Errors found in Configuraiton data: Server did not start");
				for (CalcErrorDS error : shipment.getErrors()) {
					logger.error("{} - {}", error.dataElementName, error.message);
					return null;
				}
			}

			String context = config.engineId;
			if (context == null || context.isEmpty()) {
				context = DEFAULT_CONTEXT;
			}

			WebServer server = new WebServer(port);
//			ContextHandler ctx = new ContextHandler(context);
//			ctx.setHandler(new EngineHandler(shipment.getEngine()));
			server.setHandler(new EngineHandler(shipment.getEngine()));
			server.start();
			server.join();
			return server;

		} catch (Exception e) {
			logger.error("Error while starting WebServer: {}", e.getMessage());
			return null;
		}

	}

	/**
	 *
	 * @param port
	 */
	public WebServer(int port) {
		super(port);
	}

	static class EngineHandler extends AbstractHandler {
		private static final int METHOD_NOT_ALLOWED = 405;
		private static final int INVALID_DATA = 400;
		private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {
			//
		}.getType();
		private ICalcEngine engine;

		EngineHandler(ICalcEngine engine) {
			this.engine = engine;
		}

		@Override
		public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
				final HttpServletResponse response) throws IOException, ServletException {
			final String method = baseRequest.getMethod().toUpperCase();
			final long start = System.currentTimeMillis();

			if (method.equals("POST")) {
				this.processRequest(request, response);
			} else if (method.equals("OPTIONS")) {
				/*
				 * we have no issue with CORS. We are ready to respond to any client so long as
				 * the auth is taken care of
				 */
				response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));

			} else {
				logger.error("Rejected a request with method {}", baseRequest.getMethod());
				response.setStatus(METHOD_NOT_ALLOWED);
			}

			logger.info("Responded in " + (System.currentTimeMillis() - start) + "ms\n");
			baseRequest.setHandled(true);
		}

		private void processRequest(final HttpServletRequest request, final HttpServletResponse response)
				throws JsonIOException, IOException {
			CalcResultDS result = null;
			Gson gson = new Gson();
			try (Reader reader = request.getReader()) {
				Map<String, String> inputs = gson.fromJson(reader, MAP_TYPE);
				result = this.engine.calculate(inputs);
			} catch (Exception e) {
				CalcErrorDS[] errors = { new CalcErrorDS("", e.getMessage()) };
				result = new CalcResultDS(errors);
			}

			if (!result.allOk) {
				response.setStatus(INVALID_DATA);
			}
			try (Writer writer = response.getWriter()) {
				gson.toJson(result, writer);
			}

		}
	}
}
