package org.simplity.calc.engine.impl;

import java.util.Map;

import org.simplity.calc.engine.api.CalcErrorDS;
import org.simplity.calc.engine.api.CalcResultDS;
import org.simplity.calc.engine.api.ICalcEngine;
import org.simplity.calc.engine.api.IEngineShipment;

/**
 * Represents the result of creating an instance of ICalcEngine from the
 * configuration result.
 * <p>
 */
class EngineShipment implements IEngineShipment {
	private final boolean allOk;
	private final ICalcEngine engine;
	private final CalcErrorDS[] errors;

	EngineShipment(ICalcEngine engine) {
		this.engine = engine;
		this.allOk = true;
		this.errors = new CalcErrorDS[0];
	}

	EngineShipment(CalcErrorDS[] errors) {
		this.engine = new DummyEngine();
		this.allOk = false;
		this.errors = errors;
	}

	@Override
	public boolean allOk() {
		return this.allOk;
	}

	@Override
	public ICalcEngine getEngine() {
		return this.engine;
	}

	@Override
	public CalcErrorDS[] getErrors() {
		return this.errors;
	}

	protected static class DummyEngine implements ICalcEngine {
		private static final CalcErrorDS[] ERRORS = {
				new CalcErrorDS("", "Calculator configuration failed. Unable to calculate any variables") };

		@Override
		public CalcResultDS calculate(Map<String, String> inputs) {
			return new CalcResultDS(ERRORS);
		}

		@Override
		public void shutdown() {
			// nothing was up anyways
		}

	}
}
