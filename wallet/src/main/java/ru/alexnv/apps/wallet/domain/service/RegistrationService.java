package ru.alexnv.apps.wallet.domain.service;

import java.util.Arrays;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.infrastructure.Argon2Hasher;
import ru.alexnv.apps.wallet.infrastructure.PasswordHasher;
import ru.alexnv.apps.wallet.infrastructure.dao.DaoException;
import ru.alexnv.apps.wallet.infrastructure.dao.NotFoundException;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;

/**
 * Сервис регистрации
 */
public class RegistrationService {

	/**
	 * Реализация DAO игрока, устанавливается инжектором
	 */
	private final PlayerDao playerDao;

	/**
	 * Создание сервиса регистрации
	 * 
	 * @param playerDao DAO игрока
	 */
	public RegistrationService(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}

	/**
	 * Регистрация пользователя
	 * При совпадении фактического параметра login с полем login любого игрока
	 * из базы - исключение PlayerAlreadyExistsException
	 * При успешной регистрации добавляет игрока в базу
	 * 
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws PlayerAlreadyExistsException - такой игрок уже существует
	 * @throws DatabaseException            - ошибка работы с БД
	 */
	public Player register(String login, char[] password) throws PlayerAlreadyExistsException, DatabaseException {
		try {
			Player tempPlayer = readPlayer(login);
			
			if (tempPlayer != null) { // игрок уже зарегистрирован
				throw new PlayerAlreadyExistsException("Такой игрок уже существует.");
			}
			
			// Вычисление хэша и установка хэшированного пароля игроку
			PasswordHasher hasher = new Argon2Hasher();

			// Создание нового игрока и добавление в БД
			Player player = new Player(login, hasher.hashPassword(password));
			player = playerDao.insert(player);

			// Создаём новый пустой пароль с длиной 1 символ
			char[] emptyPass = { '0' };
			player.setPassword(emptyPass);
			
			return player;
			
		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		} finally {
			// Стираем изначально переданный пароль
			Arrays.fill(password, '0');
		}
	}

	/**
	 * Проверка на существование игрока с таким логином в БД
	 * 
	 * @param login пытающийся зарегистрироваться логин
	 * @return null - если не существует, Player - если существует
	 * @throws DaoException ошибка работы с БД
	 */
	private Player readPlayer(String login) throws DaoException {
		try {
			Player player = playerDao.findByLogin(login);
			return player;
		} catch (NotFoundException e) {
			return null;
		}
	}

}
