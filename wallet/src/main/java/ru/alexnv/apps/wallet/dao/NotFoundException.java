/**
 * 
 */
package ru.alexnv.apps.wallet.dao;

/**
 * The Class NotFoundException.
 */
public class NotFoundException extends DaoException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 958588335357391752L;

	/**
	 * Instantiates a new not found exception.
	 */
	public NotFoundException() {
		super();
	}

	/**
	 * Instantiates a new not found exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public NotFoundException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new not found exception.
	 *
	 * @param message the message
	 */
	public NotFoundException(String message) {
		super(message);
	}

}
