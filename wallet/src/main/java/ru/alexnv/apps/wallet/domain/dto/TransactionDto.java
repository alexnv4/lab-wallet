/**
 * 
 */
package ru.alexnv.apps.wallet.domain.dto;

/**
 * Класс DTO транзакции.
 */
public class TransactionDto extends AbstractDto {
	
	/** Идентификатор транзакции. */
	private Long id;
	
	/** Баланс до транзакции. */
	private String balanceBefore;
	
	/** Баланс после транзакции. */
	private String balanceAfter;
	
	/** Дата и время выполнения транзакции. */
	private String dateTime;

	/**
	 * Создание DTO транзакции.
	 *
	 * @param id идентификатор транзакции
	 * @param balanceBefore баланс до транзакции
	 * @param balanceAfter баланс после транзакции
	 * @param dateTime дата и время
	 */
	public TransactionDto(Long id, String balanceBefore, String balanceAfter, String dateTime) {
		super();
		this.id = id;
		this.balanceBefore = balanceBefore;
		this.balanceAfter = balanceAfter;
		this.dateTime = dateTime;
	}

	/**
	 * Создание DTO транзакции.
	 */
	public TransactionDto() {
		
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the balance before.
	 *
	 * @return the balanceBefore
	 */
	public String getBalanceBefore() {
		return balanceBefore;
	}

	/**
	 * Sets the balance before.
	 *
	 * @param balanceBefore the balanceBefore to set
	 */
	public void setBalanceBefore(String balanceBefore) {
		this.balanceBefore = balanceBefore;
	}

	/**
	 * Gets the balance after.
	 *
	 * @return the balanceAfter
	 */
	public String getBalanceAfter() {
		return balanceAfter;
	}

	/**
	 * Sets the balance after.
	 *
	 * @param balanceAfter the balanceAfter to set
	 */
	public void setBalanceAfter(String balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	/**
	 * Gets the date time.
	 *
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the date time.
	 *
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

}
