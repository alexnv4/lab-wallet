package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - отсутствуют средства для снятия.
 */
public class NoMoneyLeftException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3418708942419351481L;

	/**
	 * Instantiates a new no money left exception.
	 *
	 * @param message the message
	 */
	public NoMoneyLeftException(String message) {
		super(message);
	}

}
