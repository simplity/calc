package org.simplity.calc.engine.api;

import org.simplity.calc.engine.impl.CalcEngines;

/**
 * Represents the type of the object returned by the method
 * {@link CalcEngines#newEngine}. If the method call succeeds, this returned
 * object contains a usable engine and an empty list of errors. Else it contains
 * an array of errors and a dummy engine that will only generate error if it is
 * used. This design of a dummy engine is used as a defensive programming
 * technique to provide a meaningful error on wrong usage, rather than resulting
 * in a run-time exception
 */
public interface IEngineShipment {
	/**
	 * this is the method to check the result first, before retrieving an engine or
	 * errors
	 *
	 * @return true if a real engine is available in this shipment. false if this
	 *         contains a list of errors, and a dummy engine
	 */
	boolean allOk();

	/**
	 * To be invoked only if {@link #allOk()} returns true.
	 *
	 * @return Usable engine if all OK. A dummy engine is returned if allOk = false;
	 */
	ICalcEngine getEngine();

	/**
	 *
	 * @return non-null array of errors. empty if there are no errors.
	 */

	CalcErrorDS[] getErrors();
}
