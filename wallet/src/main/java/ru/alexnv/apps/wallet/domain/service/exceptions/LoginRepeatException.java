/**
 * 
 */
package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - повторный логин игрока с правильным логином и паролем.
 */
public class LoginRepeatException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2665189759952100911L;

	/**
	 * Instantiates a new login repeat exception.
	 *
	 * @param message the message
	 */
	public LoginRepeatException(String message) {
		super(message);
	}

}
