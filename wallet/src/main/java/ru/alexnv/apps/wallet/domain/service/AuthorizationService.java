package ru.alexnv.apps.wallet.domain.service;

import java.util.List;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.LoginRepeatException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;
import ru.alexnv.apps.wallet.infrastructure.dao.DaoException;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;

/**
 * Сервис предметной области для авторизации пользователя в кошельке
 */
public class AuthorizationService {

	/**
	 * Залогиненный игрок
	 */
	private Player player = null;

	/**
	 * Реализация DAO игрока, устанавливается инжектором
	 */
	private final PlayerDao playerDao;

	/**
	 * @param playerDao
	 */
	public AuthorizationService(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}

	/**
	 * Авторизация игрока 
	 * Сначала проверяется логин: если не существует, то исключение NoSuchPlayerException
	 * Затем проверяется пароль: если пароль неправильный, 
	 * то исключение WrongPasswordException
	 * При успешной авторизации устанавливается поле player
	 * 
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws NoSuchPlayerException  - такого игрока не существует
	 * @throws WrongPasswordException - неправильный пароль для существующего игрока
	 * @throws DatabaseException      - ошибка работы с БД
	 * @throws LoginRepeatException   - повторный логин игрока
	 */
	public Player authorize(String login, String password)
			throws NoSuchPlayerException, WrongPasswordException, DatabaseException, LoginRepeatException {
		try {
			List<Player> players = playerDao.getAll();

			// Проверка логина и пароля игрока
			for (Player player : players) {
				if (player.getLogin().equals(login)) {
					if (!password.equals(player.getPassword())) {
						throw new WrongPasswordException("Неправильный пароль.");
					}
					if (this.player != null && this.player.getLogin().equals(player.getLogin())) {
						throw new LoginRepeatException("Повторный логин игрока."); 
					}
					
					// Логин игрока
					this.setPlayer(player);
					return this.player;
				}
			}

		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
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
	 * Установка залогиненного игрока
	 * 
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
