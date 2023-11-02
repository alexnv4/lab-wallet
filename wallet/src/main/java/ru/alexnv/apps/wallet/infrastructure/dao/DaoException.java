package ru.alexnv.apps.wallet.infrastructure.dao;

/**
 * Исключение при работе с БД.
 */
public class DaoException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2128893132903025002L;

	/**
	 * Создание исключения DAO.
	 */
	public DaoException() {
		super();
	}

	/**
	 * Создание исключения DAO.
	 *
	 * @param message сообщение об ошибке
	 */
	public DaoException(String message) {
		super(message);
	}

	/**
	 * Создание исключения DAO.
	 *
	 * @param message сообщение об ошибке
	 * @param cause причина ошибки
	 */
	public DaoException(String message, Exception cause) {
		super(message, cause);
	}

}
