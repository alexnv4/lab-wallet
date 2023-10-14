package ru.alexnv.apps.wallet.domain.service;

import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;

/**
 * Сервис предметной области для авторизации пользователя в кошельке
 */
public class AuthorizationService {
	
	/**
	 * Залогиненный игрок
	 */
	private Player player = null;
	/**
	 * Список игроков, устанавливается внешим слоем приложения
	 */
	private final List<Player> players;

	public AuthorizationService(List<Player> players) {
		this.players = players;
	}

	/**
	 * Авторизация игрока
	 * Сначала проверяется логин: если не существует, то исключение NoSuchPlayerException
	 * Затем проверяется пароль: если пароль неправильный, то исключение WrongPasswordException
	 * При успешной авторизации устанавливается поле player
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws NoSuchPlayerException - такого игрока не существует
	 * @throws WrongPasswordException - неправильный пароль для существующего игрока
	 */
	public Player authorize(String login, String password) throws NoSuchPlayerException, WrongPasswordException {
		for (Player player : players) {
			if (player.getLogin().equals(login)) {
				if (player.getPassword().equals(password)) {
					this.setPlayer(player);
					return player;
				} else {
					throw new WrongPasswordException("Неправильный пароль.");
				}
			}
		}
		throw new NoSuchPlayerException("Такого игрока не существует.");
	}

	/**
	 * @return игрок
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
}
