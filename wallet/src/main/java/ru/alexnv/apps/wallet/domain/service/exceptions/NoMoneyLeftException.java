package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - отсутствуют средства для снятия
 */
public class NoMoneyLeftException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoMoneyLeftException(String message) {
		super(message);
	}

}

