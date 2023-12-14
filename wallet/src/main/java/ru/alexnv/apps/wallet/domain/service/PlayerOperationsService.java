/**
 * 
 */
package ru.alexnv.apps.wallet.domain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.dao.DaoException;
import ru.alexnv.apps.wallet.dao.NotFoundException;
import ru.alexnv.apps.wallet.dao.NotUniqueException;
import ru.alexnv.apps.wallet.dao.PlayerDao;
import ru.alexnv.apps.wallet.dao.TransactionDao;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.TransactionDto;
import ru.alexnv.apps.wallet.domain.mappers.PlayerMapper;
import ru.alexnv.apps.wallet.domain.mappers.TransactionMapper;
import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.model.Transaction;
import ru.alexnv.apps.wallet.domain.service.exceptions.DatabaseException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.service.exceptions.FindPlayerByIdException;

/**
 * Сервис предметной области для вариантов использования пользователем
 */
public class PlayerOperationsService {

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
	public PlayerOperationsService(PlayerDao playerDao, TransactionDao transactionDao) {
		this.playerDao = playerDao;
		this.transactionDao = transactionDao;
	}
	
	/**
	 * Кредит игрока с добавлением информации в БД
	 * Для вызова метода player не должен быть null
	 * 
	 * @param playerId ID игрока 
	 * @param amount количество средств
	 * @param transactionId идентификатор транзакции
	 * @return DTO игрока
	 * @throws DatabaseException ошибка работы с базой данных
	 * @throws TransactionIdNotUniqueException транзакция не уникальная
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public PlayerDto creditPlayer(Long playerId, BigDecimal amount, Long transactionId) throws TransactionIdNotUniqueException, FindPlayerByIdException, DatabaseException {
		Player player;
		try {
			player = playerDao.findById(playerId);
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new FindPlayerByIdException("Игрок с ID=" + playerId + " в БД не найден " + e.getMessage());
		} catch (DaoException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		
		BigDecimal oldBalance = player.getBalanceNumeric();
		BigDecimal newBalance = oldBalance.add(amount);
		try {
			Player tempPlayer = player.clone();
			createTransaction(tempPlayer, transactionId, oldBalance, newBalance);
			tempPlayer.setBalance(newBalance);
			
			updatePlayerTransaction(tempPlayer);
			
			player = tempPlayer;
		} catch (NotUniqueException e) {
			throw new TransactionIdNotUniqueException(e.getMessage());
		} catch (DaoException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return PlayerMapper.INSTANCE.toDto(player); 
	}
	
	/**
	 * Дебетовая операция Будет успешной только в том случае, если на счету
	 * достаточно средств (баланс - сумма дебета >= 0)
	 * Операция записывается в БД
	 * Для вызова метода player не должен быть null
	 * 
	 * @param playerId ID игрока 
	 * @param amount количество средств
	 * @param transactionId идентификатор транзакции 
	 * @return DTO игрока
	 * @throws NoMoneyLeftException столько средств нет
	 * @throws DatabaseException ошибка работы с базой данных
	 * @throws TransactionIdNotUniqueException транзакция не уникальная
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public PlayerDto debitPlayer(Long playerId, BigDecimal amount, Long transactionId)
			throws NoMoneyLeftException, DatabaseException, TransactionIdNotUniqueException, FindPlayerByIdException {
		Player player;
		try {
			player = playerDao.findById(playerId);
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new FindPlayerByIdException("Игрок с ID=" + playerId + " в БД не найден " + e.getMessage());
		} catch (DaoException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		
		BigDecimal oldBalance = player.getBalanceNumeric();
		if (oldBalance.compareTo(amount) < 0) {
			throw new NoMoneyLeftException("Недостаточно средств для снятия.");
		}
		BigDecimal newBalance = oldBalance.subtract(amount);
		
		try {
			Player tempPlayer = player.clone();
			createTransaction(tempPlayer, transactionId, oldBalance, newBalance);
			tempPlayer.setBalance(newBalance);
			
			createTransaction(player, transactionId, oldBalance, newBalance);
			player.setBalance(newBalance);

			updatePlayerTransaction(tempPlayer);
			
			player = tempPlayer;
		} catch (NotUniqueException e) {
			throw new TransactionIdNotUniqueException(e.getMessage());
		} catch (DaoException e) {
			throw new DatabaseException(e.getMessage());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return PlayerMapper.INSTANCE.toDto(player); 
	}

	/**
	 * @param playerId ID игрока
	 * @return список транзакций в виде текста
	 * @throws DatabaseException ошибка работы с базой данных
	 */
	public List<TransactionDto> getTransactionsHistory(Long playerId) throws DatabaseException {
		try {
			Player player = playerDao.findById(playerId);
			
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

		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new DatabaseException("Игрок с ID=" + playerId + " в БД не найден " + e.getMessage());
		} catch (DaoException e) {
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
	}
	
	/**
	 * Получение баланса игрока. Баланс передаётся в виде поля в DTO игрока.
	 *
	 * @param playerId идентификатор игрока
	 * @return DTO игрока с балансом
	 * @throws DatabaseException ошибка работы с БД
	 */
	public PlayerDto getBalance(Long playerId) throws DatabaseException {
		PlayerDto playerDto;
		Player player = null;
		
		try {
			player = playerDao.findById(playerId);
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new DatabaseException("Игрок с ID=" + playerId + " в БД не найден " + e.getMessage());
		} catch (DaoException e) {
			e.printStackTrace();
			throw new DatabaseException("Ошибка работы с БД " + e.getMessage());
		}
		playerDto = PlayerMapper.INSTANCE.toDto(player);
		
		return playerDto;
	}
	
	/**
	 * @param player игрок
	 * @throws DaoException ошибка работы с БД
	 */
	private void updatePlayerTransaction(Player player) throws DaoException {
		// Добавление транзакции в базу
		transactionDao.insert(player.getLastTransaction());
		
		// Обновление баланса игрока в базе
		playerDao.update(player);
	}
	
	/**
	 * Создание новой транзакции и её добавление в список игрока
	 * 
	 * @param player игрок 
	 * @param transactionId предоставленный идентификатор транзакции 
	 * @param balanceBefore баланс до транзакции
	 * @param balanceAfter баланс после транзакции
	 * @param description описание транзакции
	 * @return транзакция
	 */
	private Transaction createTransaction(Player player, Long transactionId, BigDecimal balanceBefore, BigDecimal balanceAfter) {
		Transaction transaction = new Transaction(transactionId, player, balanceBefore, balanceAfter);
		player.addTransaction(transaction);
		return transaction;
	}
	
}
