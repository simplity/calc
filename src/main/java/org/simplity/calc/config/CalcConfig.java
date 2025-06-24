package org.simplity.calc.config;

import java.util.Map;

/**
 * Represents the root of the entire engine configuration. This class is the
 * target for deserialization from the main JSON config file using GSON.
 */
public class CalcConfig {
	/**
	 * A map of all schemas, keyed by a unique schema name. This allows schemas to
	 * be defined once and reused by multiple data elements.
	 */
	public Map<String, ValueSchema> schemas;

	/**
	 * The data dictionary containing all data elements known to the engine. The map
	 * key is the unique name of the data element.
	 */
	public Map<String, DataElement> dataElements;
}
