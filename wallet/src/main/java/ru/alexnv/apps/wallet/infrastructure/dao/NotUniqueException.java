/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

/**
 * Исключение - поле в БД не уникальное.
 */
public class NotUniqueException extends DaoException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -629535706984052976L;

	/**
	 * Instantiates a new not unique exception.
	 */
	public NotUniqueException() {
		super();
	}

	/**
	 * Instantiates a new not unique exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public NotUniqueException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new not unique exception.
	 *
	 * @param message the message
	 */
	public NotUniqueException(String message) {
		super(message);
	}

}
