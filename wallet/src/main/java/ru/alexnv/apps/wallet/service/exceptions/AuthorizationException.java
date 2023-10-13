package ru.alexnv.apps.wallet.service.exceptions;

/**
 * Исключение - ошибка авторизации
 */
public class AuthorizationException extends Exception {

	private static final long serialVersionUID = -536839371685739308L;

	public AuthorizationException(String message) {
		super(message);
	}
	
	

}
