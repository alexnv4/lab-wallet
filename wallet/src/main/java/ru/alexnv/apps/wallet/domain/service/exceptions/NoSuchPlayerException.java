package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - игрока не существует
 */
public class NoSuchPlayerException extends Exception {

	private static final long serialVersionUID = 2L;

	public NoSuchPlayerException(String message) {
		super(message);
	}

}

