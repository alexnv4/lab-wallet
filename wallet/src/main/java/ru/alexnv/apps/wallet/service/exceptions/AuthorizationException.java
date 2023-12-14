package ru.alexnv.apps.wallet.service.exceptions;

/**
 * Исключение - ошибка авторизации.
 */
public class AuthorizationException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -536839371685739308L;

	/**
	 * Instantiates a new authorization exception.
	 *
	 * @param message the message
	 */
	public AuthorizationException(String message) {
		super(message);
	}

}
