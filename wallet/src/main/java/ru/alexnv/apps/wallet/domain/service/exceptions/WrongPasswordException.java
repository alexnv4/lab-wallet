package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - неправильный пароль
 */
public class WrongPasswordException extends Exception {

	private static final long serialVersionUID = 4L;

	public WrongPasswordException(String message) {
		super(message);
	}

}

