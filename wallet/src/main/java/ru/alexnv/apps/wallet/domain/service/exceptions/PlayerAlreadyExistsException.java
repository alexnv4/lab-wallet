package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - такой игрок уже существует
 */
public class PlayerAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 3L;

	public PlayerAlreadyExistsException(String message) {
		super(message);
	}

}

