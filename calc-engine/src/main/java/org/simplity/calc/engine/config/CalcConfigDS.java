package org.simplity.calc.engine.config;

import java.util.Map;

/**
 * Represents the root of the entire engine configuration data structure. This
 * is designed as a Data structure
 * <p>
 * This class is typically deserialized from a single configuration file (e.g.,
 * JSON) and serves as the entry point for bootstrapping the calculation engine.
 * It holds the complete data dictionary and all reusable validation schemas.
 */
public class CalcConfigDS {
	/**
	 * unique id associated with this configuration. If a server is designed to
	 * serve several engines, this is the unique name with which this instance would
	 * be associated with. When the engine is deployed as a web-service, this name
	 * would be the Context with which the service is exposed;
	 * <p>
	 * For example, if the engineId is "normal-salary", and the web-serice is hosted
	 * on https://com.example/ then this engine-service is available as
	 * https://com.example/normal-salary
	 */
	public String engineId;
	/**
	 * A map of all reusable validation schemas, keyed by a unique schema name. This
	 * allows schemas to be defined once and applied to multiple input data
	 * elements.
	 *
	 * @see ValueSchemaDS
	 */
	public Map<String, ValueSchemaDS> schemas;

	/**
	 * The data dictionary containing all data elements known to the engine. The map
	 * key is the unique name of the data element.
	 *
	 * @see DataElementDS
	 */
	public Map<String, DataElementDS> dataElements;

	/**
	 * While a ValueSchema specifies simple constraints on the possible value for an
	 * input field, a Validator allows more complex validations on individual input
	 * fields as well as inter-field validations
	 */
	public ValidatorDS[] validators;

	/**
	 * message texts for messageIds. Input values are validated before the output
	 * fields are calculated. Validation errors are associated with errorIds. The
	 * actual message to be rendered are provided in this map. This feature allows a
	 * multi-lingual (I18n) user interaction
	 */
	public Map<String, String> messages;

	/**
	 *
	 */
	public Map<String, Map<String, String>> enumerations;

	/**
	 *
	 */
	public Map<String, Map<String, String>> dataStructures;

	/**
	 *
	 */
	public Map<String, Map<String, String>> tables;

}