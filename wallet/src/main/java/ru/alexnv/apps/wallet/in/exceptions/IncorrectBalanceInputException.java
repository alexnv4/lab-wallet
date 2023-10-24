package ru.alexnv.apps.wallet.in.exceptions;

/**
 * Исключение - неправильно введён баланс
 */
public class IncorrectBalanceInputException extends Exception {

	private static final long serialVersionUID = -515342119457205151L;

	public IncorrectBalanceInputException(String message) {
		super(message);
	}

}
