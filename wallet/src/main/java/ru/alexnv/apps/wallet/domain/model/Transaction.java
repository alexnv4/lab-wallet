package ru.alexnv.apps.wallet.domain.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Transaction {

	/**
	 * Баланс до совершения транзакции
	 */
	private BigDecimal balanceBefore;
	/**
	 * Баланс после совершения транзакции
	 */
	private BigDecimal balanceAfter;
	/**
	 * Игрок
	 */
	private Player player;
	/**
	 * Дата совершения транзакции
	 */
	private final Date date = new Date();	
	/**
	 * Уникальный идентификатор транзакции
	 */
	private final UUID uuid = UUID.randomUUID();
	/**
	 * Вид транзакции (дебет или кредит)
	 */
	private String description;

	/**
	 * @param player
	 */
	public Transaction(Player player) {
		super();
		this.player = player;
	}

	/**
	 * @return баланс до транзакции
	 */
	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}
	
	/**
	 * @param balanceBefore
	 */
	public void setBalanceBefore(BigDecimal balanceBefore) {
		this.balanceBefore = balanceBefore;
	}
	
	/**
	 * @return баланс после транзакции
	 */
	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}
	
	/**
	 * @param balanceAfter
	 */
	public void setBalanceAfter(BigDecimal balanceAfter) {
		this.balanceAfter = balanceAfter;
	}
	
	/**
	 * @return игрок
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return вид транзакции
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Формат вывода транзакции
	 */
	@Override
	public String toString() {
		return String.format("%s %s %s %s %s", date.toString(), player.getLogin(), description,
				balanceBefore.toString(), balanceAfter.toString());
		
	}

}

