/**
 * 
 */
package ru.alexnv.apps.wallet.domain.service.exceptions;

/**
 * Исключение - транзакция не уникальна
 */
public class TransactionIdNotUniqueException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -379520585992296303L;

	/**
	 * @param message
	 */
	public TransactionIdNotUniqueException(String message) {
		super(message);
	}

}
