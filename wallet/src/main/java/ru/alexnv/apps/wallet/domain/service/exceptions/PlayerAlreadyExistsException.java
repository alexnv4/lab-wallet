package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - такой игрок уже существует.
 */
public class PlayerAlreadyExistsException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3363999795613980296L;

	/**
	 * Instantiates a new player already exists exception.
	 *
	 * @param message the message
	 */
	public PlayerAlreadyExistsException(String message) {
		super(message);
	}

}
