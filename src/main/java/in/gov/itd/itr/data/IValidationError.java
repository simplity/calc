
package in.gov.itd.itr.data;

/**
 * represents a validation error
 *
 * @author renga
 *
 */
public interface IValidationError {
	/**
	 *
	 * @return unique id assigned to this message that can be used for I18n
	 */
	public String getId();

	/**
	 *
	 * @return message text in English
	 */
	public String getText();

	/**
	 *
	 * @return run-time parameters to be inserted in the text template for I18N
	 */
	public String[] getParams();
}
