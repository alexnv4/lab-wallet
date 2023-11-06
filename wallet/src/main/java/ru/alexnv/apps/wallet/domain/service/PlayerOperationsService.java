/**
 * 
 */
package ru.alexnv.apps.wallet.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.TransactionDto;
import ru.alexnv.apps.wallet.domain.mappers.PlayerMapper;
import ru.alexnv.apps.wallet.domain.mappers.TransactionMapper;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.model.Transaction;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.infrastructure.dao.DaoException;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;
import ru.alexnv.apps.wallet.infrastructure.dao.TransactionDao;

/**
 * Сервис предметной области для вариантов использования пользователем
 */
public class PlayerOperationsService {
	
	/**
	 * Сервис авторизации, устанавливается инжектором
	 */
	private final AuthorizationService authorizationService;
	
	/**
	 * Реализация DAO игрока, устанавливается инжектором
	 */
	private final PlayerDao playerDao;
	
	/**
	 * Реализация DAO транзакции, устанавливается инжектором
	 */
	private final TransactionDao transactionDao;

	/**
	 * Создание доменного сервиса операций залогиненного игрока
	 * 
	 * @param authorizationService сервис авторизации
	 * @param playerDao DAO игрока
	 * @param transactionDao DAO транзакции
	 */
	public PlayerOperationsService(AuthorizationService authorizationService,
			PlayerDao playerDao, TransactionDao transactionDao) {
		this.playerDao = playerDao;
		this.authorizationService = authorizationService;
		this.transactionDao = transactionDao;
	}
	
	/**
	 * Кредит игрока с добавлением информации в БД
	 * Для вызова метода player не должен быть null
	 * 
	 * @param amount количество средств
	 * @param transactionId идентификатор транзакции
	 * @return DTO игрока
	 * @throws DatabaseException ошибка работы с базой данных
	 * @throws TransactionIdNotUniqueException транзакция не уникальная
	 */
	public PlayerDto creditPlayer(BigDecimal amount, Long transactionId) throws DatabaseException, TransactionIdNotUniqueException {
		Player player = authorizationService.getPlayer();
		if (player == null) {
			System.err.println("Попытка кредита незалогиненного игрока.");
			return null;
		}
		
		try {
			Transaction transaction = transactionDao.findById(transactionId); // ID не уникален
			throw new TransactionIdNotUniqueException("Транзакция " + transaction.getId() + " не уникальна.");
		} catch (DaoException e) { // Транзакция не найдена, регистрируем новую
			BigDecimal oldBalance = player.getBalanceNumeric();
			BigDecimal newBalance = oldBalance.add(amount);
			registerTransaction(transactionId, oldBalance, newBalance);
			player.setBalance(newBalance);

			updatePlayerTransaction();
		}
		
		return PlayerMapper.INSTANCE.toDto(player); 
	}
	
	/**
	 * Дебетовая операция Будет успешной только в том случае, если на счету
	 * достаточно средств (баланс - сумма дебета >= 0)
	 * Операция записывается в БД
	 * Для вызова метода player не должен быть null
	 * 
	 * @param amount количество средств
	 * @param transactionId идентификатор транзакции 
	 * @return DTO игрока
	 * @throws NoMoneyLeftException столько средств нет
	 * @throws DatabaseException ошибка работы с базой данных
	 * @throws TransactionIdNotUniqueException транзакция не уникальная
	 */
	public PlayerDto debitPlayer(BigDecimal amount, Long transactionId)
			throws NoMoneyLeftException, DatabaseException, TransactionIdNotUniqueException {
		Player player = authorizationService.getPlayer();
		if (player == null) {
			System.err.println("Попытка дебета незалогиненного игрока.");
			return null;
		}
		
		BigDecimal oldBalance = player.getBalanceNumeric();
		if (oldBalance.compareTo(amount) < 0) {
			throw new NoMoneyLeftException("Недостаточно средств для снятия.");
		}
		
		try {
			Transaction transaction = transactionDao.findById(transactionId); // ID не уникален
			throw new TransactionIdNotUniqueException("Транзакция " + transaction.getId() + " не уникальна.");
		} catch (DaoException e) { // Транзакция не найдена, регистрируем новую
			BigDecimal newBalance = oldBalance.subtract(amount);
			registerTransaction(transactionId, oldBalance, newBalance);
			player.setBalance(newBalance);
			
			updatePlayerTransaction();
		}

		return PlayerMapper.INSTANCE.toDto(player); 
	}

	/**
	 * @return список транзакций в виде текста
	 * @throws DatabaseException ошибка работы с базой данных
	 */
	public List<TransactionDto> getTransactionsHistory() throws DatabaseException {
		Player player = authorizationService.getPlayer();
		try {
			// Загрузить с базы List транзакций на игрока
			List<Transaction> transactions = transactionDao.getAllWithPlayerId(player.getId());

			List<String> history = new ArrayList<>();
			List<TransactionDto> transactionsDto = new ArrayList<>();
			// Установить поле player всем транзакциям и заполнить массив истории
			// Преобразование в DTO
			for (Transaction transaction : transactions) {
				transaction.setPlayer(player);
				history.add(transaction.toString());
				transactionsDto.add(TransactionMapper.INSTANCE.toDto(transaction));
			}
			
			return transactionsDto;

		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
	}
	
	/**
	 * @throws DatabaseException ошибка работы с базой данных
	 */
	private void updatePlayerTransaction() throws DatabaseException {
		Player player = authorizationService.getPlayer();
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
	 * Создание новой транзакции и её добавление в список игрока
	 * 
	 * @param transactionId предоставленный идентификатор транзакции 
	 * @param balanceBefore баланс до транзакции
	 * @param balanceAfter баланс после транзакции
	 * @param description описание транзакции
	 * @return транзакция
	 */
	private Transaction registerTransaction(Long transactionId, BigDecimal balanceBefore, BigDecimal balanceAfter) {
		Player player = authorizationService.getPlayer();
		Transaction transaction = new Transaction(transactionId, player, balanceBefore, balanceAfter);
		player.addTransaction(transaction);
		return transaction;
	}
	
}
