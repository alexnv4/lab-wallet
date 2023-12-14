package ru.alexnv.apps.wallet.service.exceptions;

/**
 * Исключение - ошибка регистрации.
 */
public class RegistrationException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2088218972714902969L;

	/**
	 * Instantiates a new registration exception.
	 *
	 * @param message the message
	 */
	public RegistrationException(String message) {
		super(message);
	}
}
