package ru.alexnv.apps.wallet.in.exceptions;

/**
 * Исключение - введено не число
 */
public class NotNumberException extends Exception {

	private static final long serialVersionUID = -6843184579698404213L;

	public NotNumberException(String message) {
		super(message);
	}

}
