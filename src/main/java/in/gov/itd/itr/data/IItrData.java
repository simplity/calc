package in.gov.itd.itr.data;

import java.util.List;

import in.gov.itd.itr.meta.ItrType;

/**
 * Data structure that holds all data for a given type of ITR. It has data that
 * is entered by the assessee as well as the data that is suggested by the
 * system (pre-populated).
 *
 * Only integral and text data elements are saved. Date is represented by
 * Date.milliseconds() of its UTC, while boolean maps to 0/1. All enumerations
 * are represented by their designated seqNo. (Sequence numbers are defined in a
 * static class with constants.)
 *
 * An instance of <code>IItrData</code> can be obtained from an instance of
 * <code>IItrDataStore</code>
 *
 * @author renga
 *
 */
public interface IItrData {

	/**
	 *
	 * @return non-null key with which this data is to be persisted. (pan+AY ??)
	 */
	public String getKey();

	/**
	 * @return ITR form type for which this data is meant for
	 */
	public ItrType getItrType();

	/**
	 *
	 * @return pan for which the itr is created. This is immutable.
	 */
	public String getPan();

	/**
	 *
	 * @return accounting year.
	 */
	public int getAy();

	/**
	 *
	 * @return version 0 means the data is not yet edited by user. version
	 *         number is incremented whenever any data is updated into
	 *         persistence.
	 */
	public int getVersion();

	/**
	 *
	 *
	 * @return time of last persistence. time of creation if it is yet to be
	 *         persisted
	 */
	public long getTimestamp();

	/**
	 *
	 * @param tableName
	 *            non-null must be one of the names defined as constants in
	 *            ItrXXXNames.
	 * @return table, possibly empty one. null if no such table.
	 */
	public IItrTable getTable(String tableName);

	/**
	 *
	 * @param fieldName
	 *            non-null
	 * @return value for this field. 0 if no such field. Note that dates and
	 *         booleans are saved as numbers
	 */
	public long getNumber(String fieldName);

	/**
	 *
	 * @param fieldName
	 *            non-null
	 * @return value for this field. Empty string if no such field.
	 */
	public String getText(String fieldName);

	/**
	 *
	 * @param fieldName
	 *            non-null
	 * @param value
	 * @return null if this value is valid and got updated. non-null if the
	 *         value is rejected.
	 */
	public IValidationError put(String fieldName, long value);

	/**
	 *
	 * @param fieldName
	 *            non-null
	 * @param value
	 *            non-null. can be empty string.
	 * @return null if this value is valid and got updated. non-null if the
	 *         value is rejected.
	 */
	public IValidationError put(String fieldName, String value);

	/**
	 *
	 * @param fieldName
	 *            non-null
	 * @param table
	 *            non-null
	 * @return null if this table is valid and got updated. non-null if the
	 *         value is rejected.
	 */
	public IValidationError put(String fieldName, IItrTable table);

	/**
	 *
	 * @return serialized text that can be used to transport this data across
	 *         layers/network. This text can be used to populate both
	 *         server-side (Java) and client-side (JS) objects
	 */
	public String serialize();

	/**
	 * @param data
	 *            serialized text returned from a previous call to serialize().
	 *            Can be from the client. Data elements are validated.
	 * @param errors
	 *            list to which validation errors, if any, are added
	 * @return true if all ok. false if any data element was rejected because of
	 *         validation.
	 *
	 */
	public boolean deserialize(String data, List<IValidationError> errors);
}
