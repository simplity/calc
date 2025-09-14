package org.simplity.calc.ws;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A utility to read a standard JSON Schema file and convert it into a
 * simplified set of structure, array, and enum maps using the GSON library.
 * <p>
 * This program traverses the definitions in a JSON Schema and extracts the
 * field names and their types. It handles primitive types, references to other
 * defined objects, and arrays (including arrays of objects with inline
 * definitions).
 */
public class SchemaProcessor {

	private final Map<String, ValueType> fields = new LinkedHashMap<>();
	private final Map<String, Map<String, DataObjectField>> objects = new LinkedHashMap<>();
	private final Map<String, EnumDetails[]> enums = new LinkedHashMap<>();
	private final Map<String, ValueType> arrays = new LinkedHashMap<>();
	private JsonObject defs;

	protected enum DataType {
		STRING, NUMBER, BOOLEAN, DATE, TIMESTAMP, OBJECT, ARRAY, ENUM
	}

	protected static class ValueType {
		public String dataType;
		public String typeDefinition;

		public ValueType() {
			//
		}

		public ValueType(DataType mainType, String subType) {
			this.dataType = mainType.name().toLowerCase();
			this.typeDefinition = subType;
		}

		public ValueType(DataType mainType) {
			this.dataType = mainType.name().toLowerCase();

		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder().append(this.dataType);
			if (this.typeDefinition != null) {
				s.append(":").append(this.typeDefinition);
			}
			return s.toString();
		}
	}

	protected static final class EnumDetails {
		public String value;
		public String label;
		public String labelId;

		public EnumDetails() {
			// Default constructor
		}

		public EnumDetails(String value, String label, String labelId) {
			this.value = value;
			this.label = label;
			this.labelId = labelId;
		}
	}

	protected static final class DataObjectField {
		public String name;
		public ValueType type;

		public DataObjectField(String name, ValueType type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "DataObjectField [name=" + this.name + ", type=" + this.type + "]";
		}
	}

	/**
	 * Processes a JSON Schema file and produces a simplified structure map.
	 *
	 * @param folderName resource folder name. "schema.json" is read from here and
	 *                   the output files are created here
	 * @throws IOException If there is an error reading or parsing the file.
	 */
	public void flattenSchema(String folderName) throws IOException {
		JsonObject rootNode;
		String fileName = folderName + "schema.json";
		try (FileReader reader = new FileReader(new File(fileName))) {
			rootNode = JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			System.err.println("Error reading schema file: " + fileName);
			throw e;
		}

		/*
		 * get all the definitions. We check only for $defs and definitions
		 */
		if (rootNode.has("$defs")) {
			this.defs = rootNode.getAsJsonObject("$defs");
		} else if (rootNode.has("definitions")) {
			this.defs = rootNode.getAsJsonObject("definitions");
		} else {
			this.defs = new JsonObject();
		}

		for (Map.Entry<String, JsonElement> entry : this.defs.entrySet()) {
			JsonObject ele = entry.getValue().getAsJsonObject();
			this.processElement(ele, entry.getKey());
		}

		this.fields.put("Root", new ValueType(DataType.OBJECT, "Root"));
		this.processObject(rootNode, "Root");

		writeFile(folderName, "enums.json", this.enums);
		writeFile(folderName, "objects.json", this.objects);
		writeFile(folderName, "arrays.json", this.arrays);
		writeFile(folderName, "fields.json", this.fields);

	}

	private static void writeFile(String folderName, String fileName, Object data) throws IOException {
		File file = new File(folderName + fileName);
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(data));
		} catch (IOException e) {
			System.err.println("Error writing to file: " + file.getAbsolutePath());
			throw e;
		}
	}

	private ValueType processElement(JsonObject fieldDefinition, String qualifiedName) throws IOException {
		ValueType elementType;
		JsonObject element = fieldDefinition;

		// 1. Check for $ref first, as it's a pointer to another definition.
		if (fieldDefinition.has("$ref")) {
			String refPath = fieldDefinition.get("$ref").getAsString();
			String refName = refPath.substring(refPath.lastIndexOf('/') + 1);

			elementType = this.fields.get(refName);
			if (elementType == null) {
				element = this.defs.getAsJsonObject(refName);
				if (element == null) {
					throw new IllegalArgumentException("Reference '" + refName + "' not found in definitions.");
				}
				elementType = this.processElement(element, refName);
			}

			if (elementType == null) {
				// should not happen, but just in case
				throw new IllegalArgumentException("Reference '" + refName + "' not found in fields.");
			}

			// If the reference is to another name, we can add it as a field.
			if (refName.equals(qualifiedName) == false) {
				this.fields.put(qualifiedName, elementType);
			}
			return elementType;
		}

		// 2. Determine the type of the element.

		elementType = elementTypeOf(element, qualifiedName);
		this.fields.put(qualifiedName, elementType);

		// 3. Process the element based on its type.
		switch (elementType.dataType) {
		case "object":
			this.processObject(element, qualifiedName);
			break;

		case "array":
			this.processArray(element, qualifiedName);
			break;

		case "enum":
			this.processEnum(element, qualifiedName);
			break;

		default:
			// For primitive types, just return the type.
		}
		return elementType;
	}

	private ValueType processObject(JsonObject element, String elementName) throws IOException {
		if (!element.has("properties")) {
			throw new IllegalArgumentException("Object must have 'properties' attribute. " + elementName);
		}

		Map<String, DataObjectField> dataObject = new HashMap<>();
		JsonObject properties = element.getAsJsonObject("properties");
		for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
			String fieldName = property.getKey();
			String fullName = qualify(property.getKey(), elementName);
			JsonObject fieldDefinition = property.getValue().getAsJsonObject();
			ValueType elementType = this.processElement(fieldDefinition, fullName);
			dataObject.put(fieldName, new DataObjectField(fieldName, elementType));
		}
		this.objects.put(elementName, dataObject);
		return new ValueType(DataType.OBJECT, elementName);
	}

	private static final String qualify(String fieldName, String parentName) {
		if (parentName == null || parentName.isEmpty()) {
			return fieldName;
		}
		return parentName + "__" + fieldName;
	}

	private ValueType processArray(JsonObject element, String elementName) {
		if (!element.has("items")) {
			throw new IllegalArgumentException("Array definition must have 'items' field: " + elementName);
		}

		JsonObject items = element.getAsJsonObject("items");
		ValueType itemType = elementTypeOf(items, elementName);
		this.arrays.put(elementName, itemType);
		return new ValueType(DataType.ARRAY, elementName);
	}

	private ValueType processEnum(JsonObject element, String elementName) throws IOException {
		if (!element.has("enum")) {
			throw new IllegalArgumentException("Enum definition must have 'enum' field: " + elementName);
		}

		JsonArray eleArray = element.getAsJsonArray("enum");
		Map<String, String> enumLabels = new HashMap<>();

		// Check if there is a description field for enum labels
		JsonElement desc = element.get("description");
		if (desc != null && !desc.isJsonNull() && desc.isJsonPrimitive()) {
			// If description is a string, parse it for labels
			String[] pairs = getList(desc.getAsString());
			if (pairs != null) {
				for (String pair : pairs) {
					String[] keyValue = getPair(pair);
					if (keyValue != null) {
						enumLabels.put(keyValue[0], keyValue[1]);
					}
				}
			}
		}
		int n = eleArray.size();
		EnumDetails[] enumDetails = new EnumDetails[n];
		for (int i = 0; i < n; i++) {
			String value = eleArray.get(i).getAsString();

			String label = enumLabels.get(value);
			if (label == null || label.isEmpty()) {
				label = value; // Use value as label if no label is provided
			}
			String labelId = elementName + "_" + value;
			enumDetails[i] = new EnumDetails(value, label, labelId);
		}
		this.enums.put(elementName, enumDetails);
		return new ValueType(DataType.ENUM, elementName);
	}

	private static String[] getList(String text) throws IOException {
		if (text == null || text.isEmpty()) {
			return null;
		}

		String[] parts;
		if (text.contains(";")) {
			parts = text.split(";");
		} else if (text.contains(",")) {
			parts = text.split(",");
		} else {
			System.err.println("No parts found in text: " + text);
			System.in.read(new byte[1]);
			return null;
		}
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parts;
	}

	private static String[] getPair(String text) throws IOException {
		if (text == null || text.isEmpty()) {
			return null;
		}
		String[] pair = text.split(":");
		if (pair.length != 2) {
			pair = text.split("\\.");
		}
		if (pair.length != 2) {
			int idx = text.indexOf("-");
			if (idx == -1) {
				System.err.println("No value-label pair found in text: " + text);
				System.in.read(new byte[1]);
				return null;
			}
			pair = new String[] { text.substring(0, idx).trim(), text.substring(idx + 1).trim() };
		}
		for (int i = 0; i < pair.length; i++) {
			pair[i] = pair[i].trim();
		}
		return pair;
	}

	/**
	 * get the field type of a field in the definition object. The object is
	 * in-lined before calling this
	 *
	 * @param defObject node of the definition object, not $ref.
	 * @param name      name of the ref, if this is ref, else a name created as
	 *                  parent_name_field_name
	 * @return
	 */
	private static final ValueType elementTypeOf(JsonObject defObject, String name) {
		if (defObject.has("properties")) {
			return new ValueType(DataType.OBJECT, name);
		}
		if (defObject.has("items")) {
			return new ValueType(DataType.ARRAY, name);
		}
		if (defObject.has("enum")) {
			return new ValueType(DataType.ENUM, name);
		}
		if (defObject.has("type")) {
			String type = defObject.get("type").getAsString();
			switch (type) {
			case "number":
				return new ValueType(DataType.NUMBER);
			case "integer":
				return new ValueType(DataType.NUMBER);
			case "boolean":
				return new ValueType(DataType.BOOLEAN);
			default:
			}
		}
		if (defObject.has("format")) {
			String format = defObject.get("format").getAsString();
			switch (format) {
			case "date":
				return new ValueType(DataType.DATE);
			case "date-time":
				return new ValueType(DataType.TIMESTAMP);
			default:
				// If format is not recognized, treat it as a string.
			}
		}
		return new ValueType(DataType.STRING);
	}

	/**
	 * Main method to run the flattener.
	 *
	 * @param args
	 *
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String folder = "res/";
		String fileName = folder + "schema.json";
		SchemaProcessor flattener = new SchemaProcessor();
		// flattener.flattenSchema(folder);
		flattener.checkForUniqueNames(fileName);
	}

	/**
	 * Checks for unique field names in the schema definitions.
	 *
	 * @param fileName
	 * @throws IOException
	 */
	public void checkForUniqueNames(String fileName) throws IOException {
		JsonObject rootNode;
		try (FileReader reader = new FileReader(new File(fileName))) {
			rootNode = JsonParser.parseReader(reader).getAsJsonObject();
		} catch (IOException e) {
			System.err.println("Error reading schema file: " + fileName);
			throw e;
		}

		this.defs = rootNode.getAsJsonObject("definitions");
		if (this.defs == null) {
			System.err.println("No $defs found in schema file: " + fileName);
			return;
		}

		Set<String> uniqueNames = new HashSet<>();

		for (Map.Entry<String, JsonElement> entry : this.defs.entrySet()) {
			JsonObject ele = entry.getValue().getAsJsonObject();
			this.checkUnique(ele, uniqueNames);
		}

	}

	private void checkUnique(JsonObject obj, Set<String> uniqueNames) {
		if (obj.has("properties")) {
			JsonObject properties = obj.getAsJsonObject("properties");
			for (Map.Entry<String, JsonElement> property : properties.entrySet()) {
				String fieldName = property.getKey();
				if (uniqueNames.add(fieldName) == false) {
					System.err.println("Duplicate field name found: " + fieldName);
				}
				JsonObject fieldDefinition = property.getValue().getAsJsonObject();
				this.checkUnique(fieldDefinition, uniqueNames);
			}
		}
	}

}
