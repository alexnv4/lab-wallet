package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - неправильный пароль
 */
public class WrongPasswordException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8202023052993909738L;

	/**
	 * @param message
	 */
	public WrongPasswordException(String message) {
		super(message);
	}

}
