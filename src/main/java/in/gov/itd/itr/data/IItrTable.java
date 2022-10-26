package in.gov.itd.itr.data;

import java.util.List;

/**
 * represents a table of data that is part of an ITR Data
 *
 * @author simplity.org
 *
 */
public interface IItrTable {
	/**
	 *
	 * @return number of rows. can be zero.
	 */
	public int length();

	/**
	 *
	 * @return number of columns. non-zero.
	 */
	public int width();

	/**
	 *
	 * @return non-null text representation of the data that can be used to
	 *         carry this across layers/network. can be used to de-serialize()
	 */
	public String serialize();

	/**
	 *
	 * @param data
	 *            non-null text that was the returned value of a call to
	 *            serialize()
	 * @param errors
	 *            to which validation errors, if any are added to. can be null
	 *            if the caller is not interested in the error details
	 * @return true if all ok. false in case of any validation error.
	 */
	public boolean deserialize(String data, List<IValidationError> errors);

	/**
	 *
	 * @return sum of the value that is the column that is of primary interest
	 *         in this table. for example if this table is about TDS, this this
	 *         returned value is the total TDS
	 */
	public long getTotalValue();
}
