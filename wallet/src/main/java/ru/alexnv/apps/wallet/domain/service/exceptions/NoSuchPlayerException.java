package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - игрока не существует
 */
public class NoSuchPlayerException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5699416895862207036L;

	/**
	 * @param message
	 */
	public NoSuchPlayerException(String message) {
		super(message);
	}

}
