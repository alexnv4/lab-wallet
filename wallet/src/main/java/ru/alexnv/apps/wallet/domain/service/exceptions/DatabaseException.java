/**
 * 
 */
package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение при работе с базой данных на уровне доменных сервисов.
 */
public class DatabaseException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4219758297536451016L;

	/**
	 * Instantiates a new database exception.
	 *
	 * @param message the message
	 */
	public DatabaseException(String message) {
		super(message);
	}

}
