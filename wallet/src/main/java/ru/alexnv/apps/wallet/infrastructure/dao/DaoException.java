package ru.alexnv.apps.wallet.infrastructure.dao;

/**
 * Исключение при работе с БД
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 2128893132903025002L;

	public DaoException() {
		super();
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Exception cause) {
		super(message, cause);
	}

}
