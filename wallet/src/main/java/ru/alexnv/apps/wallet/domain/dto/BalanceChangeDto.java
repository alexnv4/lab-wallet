/**
 * 
 */
package ru.alexnv.apps.wallet.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Класс DTO для изменения баланса. Наследуется от AbstractDto
 */
public class BalanceChangeDto extends AbstractDto {

	/** Значение изменения баланса. */
	@NotBlank(message = "Значение изменения баланса должно быть определено без пробелов.")
	@Digits(integer = 9, fraction = 2, message = "Значение баланса введено не корректно.")
	private String balanceChange;
	
	/** Идентификатор транзакции, предоставленный пользователем. */
	@NotNull(message = "Идентификатор должен быть определён.")
	@Positive(message = "Идентификатор должен быть положительным числом.")
	private Long transactionId;

	/**
	 * Создание DTO изменения баланса с параметрами.
	 *
	 * @param balanceChange изменение баланса
	 * @param transactionId идентификатор транзакции
	 */
	public BalanceChangeDto(String balanceChange, Long transactionId) {
		super();
		this.balanceChange = balanceChange;
		this.transactionId = transactionId;
	}

	/**
	 * Создание DTO изменения баланса.
	 */
	public BalanceChangeDto() {
		super();
	}

	/**
	 * Получение значения изменения баланса.
	 *
	 * @return изменение баланса
	 */
	public String getBalanceChange() {
		return balanceChange;
	}

	/**
	 * Установка значения изменения баланса.
	 *
	 * @param balanceChange изменение баланса
	 */
	public void setBalanceChange(String balanceChange) {
		this.balanceChange = balanceChange;
	}

	/**
	 * Получение значения идентификатора транзакции.
	 *
	 * @return идентификатор транзакции
	 */
	public Long getTransactionId() {
		return transactionId;
	}

	/**
	 * Установка значения идентификатора транзакции.
	 *
	 * @param transactionId идентификатор транзакции
	 */
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
}
