package ru.alexnv.apps.wallet.in.exceptions;

/**
 * Исключение - неправильный выбор пункта меню
 */
public class IncorrectMenuChoiceException extends Exception {

	private static final long serialVersionUID = -7551928389705700359L;

	public IncorrectMenuChoiceException(String message) {
		super(message);
	}
}
