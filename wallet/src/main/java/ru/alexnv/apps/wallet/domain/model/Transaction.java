package ru.alexnv.apps.wallet.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Модель предметной области - транзакция
 */
public class Transaction {

	/**
	 * Идентификатор транзакции
	 */
	private Long id;

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
	private LocalDateTime dateTime;

	/**
	 * @param id
	 * @param balanceBefore
	 * @param balanceAfter
	 * @param player
	 */
	public Transaction(Long id, Player player, BigDecimal balanceBefore, BigDecimal balanceAfter) {
		this.id = id;
		this.player = player;
		this.balanceBefore = balanceBefore;
		this.balanceAfter = balanceAfter;
		this.dateTime = LocalDateTime.now();
	}

	/**
	 * @param id
	 * @param balanceBefore
	 * @param balanceAfter
	 * @param player
	 * @param dateTime
	 */
	public Transaction(Long id, BigDecimal balanceBefore, BigDecimal balanceAfter, Player player, LocalDateTime dateTime) {
		this(id, player, balanceBefore, balanceAfter);
		this.dateTime = dateTime;
	}

	/**
	 * @return баланс до транзакции
	 */
	public BigDecimal getBalanceBefore() {
		return balanceBefore;
	}

	/**
	 * @return баланс после транзакции
	 */
	public BigDecimal getBalanceAfter() {
		return balanceAfter;
	}

	/**
	 * @return игрок
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Формат вывода транзакции
	 */
	@Override
	public String toString() {
		return String.format("%s %s %s %s", dateTime.toString(), player.getLogin(), balanceBefore.toString(),
				balanceAfter.toString());

	}

	/**
	 * @return the dateTime
	 */
	public LocalDateTime getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
