package ru.alexnv.apps.wallet.service;

import java.time.LocalDateTime;

import ru.alexnv.apps.wallet.domain.dto.PlayerDto;

/**
 * Действие, сохраняемое при аудите
 */
public class Action {

	/**
	 * Текущий игрок
	 */
	private PlayerDto playerDto;

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
	 * @param playerDto DTO игрока
	 * @param description описание действия
	 */
	public Action(PlayerDto playerDto, String description) {
		this.playerDto = playerDto;
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
	 * @return DTO игрока
	 */
	public PlayerDto getPlayerDto() {
		return playerDto;
	}

	/**
	 * @param playerDto DTO игрока
	 */
	public void setPlayer(PlayerDto playerDto) {
		this.playerDto = playerDto;
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
