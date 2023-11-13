package ru.alexnv.apps.wallet.domain.service;

import java.util.Arrays;

import ru.alexnv.apps.wallet.dao.DaoException;
import ru.alexnv.apps.wallet.dao.NotFoundException;
import ru.alexnv.apps.wallet.dao.PlayerDao;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.DatabaseException;
import ru.alexnv.apps.wallet.domain.service.exceptions.LoginRepeatException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;

/**
 * Сервис предметной области для авторизации пользователя в кошельке
 */
public class AuthorizationService {

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
	public Player authorize(String login, char[] password)
			throws NoSuchPlayerException, WrongPasswordException, DatabaseException, LoginRepeatException {
		try {
			Player player = playerDao.findByLogin(login);

			// Вычисление хэша и установка хэшированного пароля игроку
			PasswordHasher hasher = new Argon2Hasher();
			if (!hasher.verify(player.getPassword(), password)) {
				throw new WrongPasswordException("Неправильный пароль.");
			}

			// Логин выполнился успешно
			// Создаём новый пустой пароль с длиной 1 символ
			char[] emptyPass = { '0' };
			player.setPassword(emptyPass);

			return player;
			
		} catch (NotFoundException e) {
			throw new NoSuchPlayerException("Такого игрока не существует.");
		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		} finally {
			// Стираем изначально переданный пароль
			Arrays.fill(password, '0');
		}
	}

}
