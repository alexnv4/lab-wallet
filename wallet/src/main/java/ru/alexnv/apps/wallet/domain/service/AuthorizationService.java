package ru.alexnv.apps.wallet.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.model.Transaction;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;
import ru.alexnv.apps.wallet.infrastructure.dao.DaoException;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;
import ru.alexnv.apps.wallet.infrastructure.dao.TransactionDao;

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
	 * Реализация DAO транзакции, устанавливается инжектором
	 */
	private final TransactionDao transactionDao;

	/**
	 * @param playerDao
	 * @param transactionDao
	 */
	public AuthorizationService(PlayerDao playerDao, TransactionDao transactionDao) {
		this.playerDao = playerDao;
		this.transactionDao = transactionDao;
	}

	/**
	 * Авторизация игрока Сначала проверяется логин: если не существует, то
	 * исключение NoSuchPlayerException Затем проверяется пароль: если пароль
	 * неправильный, то исключение WrongPasswordException При успешной авторизации
	 * устанавливается поле player
	 * 
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws NoSuchPlayerException  - такого игрока не существует
	 * @throws WrongPasswordException - неправильный пароль для существующего игрока
	 * @throws DatabaseException      - ошибка работы с БД
	 */
	public Player authorize(String login, String password)
			throws NoSuchPlayerException, WrongPasswordException, DatabaseException {
		try {
			List<Player> players = playerDao.getAll();

			// Проверка логина и пароля игрока
			for (Player player : players) {
				if (player.getLogin().equals(login)) {
					if (!password.equals(player.getPassword())) {
						throw new WrongPasswordException("Неправильный пароль.");
					}
					// Логин игрока
					this.setPlayer(player);
					return player;
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

	/**
	 * Кредит игрока с добавлением информации в БД
	 * 
	 * @param balance
	 * @throws DatabaseException
	 */
	public void creditPlayer(BigDecimal balance) throws DatabaseException {
		player.credit(balance);

		updatePlayerTransaction();
	}

	/**
	 * @throws DatabaseException
	 */
	private void updatePlayerTransaction() throws DatabaseException {
		try {
			// Обновление баланса игрока в базе
			playerDao.update(player);

			// Добавление транзакции в базу
			transactionDao.insert(player.getLastTransaction());

		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
	}

	/**
	 * Дебет игрока с добавлением информации в БД
	 * 
	 * @param balance
	 * @throws NoMoneyLeftException
	 * @throws DatabaseException
	 */
	public void debitPlayer(BigDecimal balance) throws NoMoneyLeftException, DatabaseException {
		player.debit(balance);

		updatePlayerTransaction();
	}

	/**
	 * @return список транзакций в виде текста
	 * @throws DatabaseException
	 */
	public List<String> getTransactionsHistory() throws DatabaseException {
		try {
			// Загрузить с базы List транзакций на игрока
			List<Transaction> transactions = transactionDao.getAllWithPlayerId(player.getId());

			List<String> history = new ArrayList<>();
			// Установить поле player всем транзакциям и заполнить массив истории
			for (Transaction transaction : transactions) {
				transaction.setPlayer(player);
				history.add(transaction.toString());
			}

			return history;

		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
	}

}
