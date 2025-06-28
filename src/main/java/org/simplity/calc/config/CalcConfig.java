package org.simplity.calc.config;

import java.util.Map;

/**
 * Represents the root of the entire engine configuration.
 * <p>
 * This class is typically deserialized from a single configuration file (e.g.,
 * JSON) and serves as the entry point for bootstrapping the calculation engine.
 * It holds the complete data dictionary and all reusable validation schemas.
 */
public class CalcConfig {
	/**
	 * A map of all reusable validation schemas, keyed by a unique schema name. This
	 * allows schemas to be defined once and applied to multiple input data
	 * elements.
	 *
	 * @see ValueSchema
	 */
	public Map<String, ValueSchema> schemas;

	/**
	 * The data dictionary containing all data elements known to the engine. The map
	 * key is the unique name of the data element.
	 *
	 * @see DataElement
	 */
	public Map<String, DataElement> dataElements;
}