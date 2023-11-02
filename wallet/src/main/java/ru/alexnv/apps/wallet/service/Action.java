package ru.alexnv.apps.wallet.service;

import java.time.LocalDateTime;

import ru.alexnv.apps.wallet.domain.model.Player;

/**
 * Действие, сохраняемое при аудите
 */
public class Action {

	/**
	 * Текущий игрок
	 */
	private Player player;

	/**
	 * Описание совершенного действия
	 */
	private String description;

	/**
	 * Время совершения действия
	 */
	private LocalDateTime dateTime;

	/**
	 * Создание действия игрока с описанием
	 * 
	 * @param player игрок
	 * @param description описание действия
	 */
	public Action(Player player, String description) {
		this.player = player;
		this.description = description;
		this.dateTime = LocalDateTime.now();
	}

	/**
	 * Конструктор на действие незалогиненного игрока
	 * 
	 * @param description описание действия
	 */
	public Action(String description) {
		this(null, description);
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
}
