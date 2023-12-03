package ru.alexnv.apps.wallet.domain.service;

import java.util.List;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.infrastructure.dao.DaoException;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;

/**
 * 
 */
public class RegistrationService {

	/**
	 * Реализация DAO игрока, устанавливается инжектором
	 */
	private final PlayerDao playerDao;

	public RegistrationService(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}

	/**
	 * Регистрация пользователя При совпадении фактического параметра login с полем
	 * login любого игрока из базы - исключение PlayerAlreadyExistsException При
	 * успешной регистрации добавляет игрока в базу
	 * 
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws PlayerAlreadyExistsException - такой игрок уже существует
	 * @throws DatabaseException            - ошибка работы с БД
	 */
	public Player register(String login, String password) throws PlayerAlreadyExistsException, DatabaseException {

		try {
			List<Player> players = playerDao.getAll();

			if (loginExists(players, login)) {
				throw new PlayerAlreadyExistsException("Такой игрок уже существует.");
			}

			// Создание нового игрока и добавление в БД
			Player player = new Player(login, password);
			player = playerDao.insert(player);
			return player;

		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
	}

	/**
	 * Проверка на существование игрока с таким же логином
	 * 
	 * @param players заполненный список игроков
	 * @param login   пытающийся зарегистрироваться логин
	 * @return существует или нет
	 */
	private boolean loginExists(List<Player> players, String login) {
		return players.stream()
				.anyMatch(player -> login.equals(player.getLogin()));
	}

}
