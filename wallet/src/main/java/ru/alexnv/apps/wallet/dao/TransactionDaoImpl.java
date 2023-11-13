/**
 * 
 */
package ru.alexnv.apps.wallet.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Transaction;

/**
 * Реализация интерфейса DAO транзакции
 */
public class TransactionDaoImpl implements TransactionDao {

	/**
	 * Установленное соединение с БД
	 */
	private Connection connection;

	/**
	 * @param connection
	 */
	public TransactionDaoImpl(Connection connection) {
		super();
		this.connection = connection;
	}

	@Override
	/**
	 * Добавление транзакции в БД
	 * 
	 * @param transaction объект транзакции
	 * @return добавленная транзакция
	 * @throws NotUniqueException ошибка уникальности транзакции
	 * @throws DaoException ошибка работы с БД
	 */
	public Transaction insert(Transaction transaction) throws NotUniqueException, DaoException {
		final String sql = "INSERT INTO wallet_schema.transactions(transaction_id, balance_before, balance_after, date, player_id) VALUES(?, ?, ?, ?, ?);";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, transaction.getId());
			statement.setBigDecimal(2, transaction.getBalanceBefore());
			statement.setBigDecimal(3, transaction.getBalanceAfter());
			statement.setObject(4, transaction.getDateTime());
			statement.setLong(5, transaction.getPlayer().getId());
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected != 1) {
				throw new DaoException("Ошибка добавления транзакции в базу данных.");
			}
			
			return transaction;
		} catch (SQLException e) {
			if (e.getSQLState().equals(UNIQUE_VIOLATION_ERROR_CODE)) {
				throw new NotUniqueException("Транзакция не уникальна", e);
			}
			throw new DaoException("Ошибка добавления транзакции: ", e);
		}
	}

	@Override
	public boolean update(Transaction object) throws DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Transaction object) throws DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Поиск транзакции в БД по идентификатору.
	 *
	 * @param PK идентификатор транзакции
	 * @return найденная транзакция
	 * @throws NotFoundException транзакция не найдена в БД
	 * @throws DaoException ошибка работы с БД
	 */
	@Override
	public Transaction findById(long PK) throws NotFoundException, DaoException {
		final String sql = "SELECT * FROM wallet_schema.transactions WHERE transaction_id = ?;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, PK);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new NotFoundException("Не найдено.");
				}

				long id = resultSet.getLong("transaction_id");
				BigDecimal balanceBefore = resultSet.getBigDecimal("balance_before");
				BigDecimal balanceAfter = resultSet.getBigDecimal("balance_after");
				LocalDateTime localDateTime = resultSet.getObject("date", LocalDateTime.class);

				Transaction transaction = new Transaction(id, balanceBefore, balanceAfter, null, localDateTime);
				return transaction;
			}
		} catch (SQLException e) {
			throw new DaoException("Ошибка поиска транзакции: ", e);
		}
	}

	@Override
	public List<Transaction> getAll() throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Получение списка транзакций игрока по его идентификатору
	 * 
	 * @param playerId идентификатор игрока
	 * @return список транзакций игрока без установленного поля Player
	 * @throws DaoException ошибка работы с БД
	 */
	public List<Transaction> getAllWithPlayerId(long playerId) throws DaoException {
		final String sql = "SELECT * FROM wallet_schema.transactions WHERE player_id=?;";
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, playerId);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					long id = resultSet.getLong("transaction_id");
					BigDecimal balanceBefore = resultSet.getBigDecimal("balance_before");
					BigDecimal balanceAfter = resultSet.getBigDecimal("balance_after");
					LocalDateTime localDateTime = resultSet.getObject("date", LocalDateTime.class);

					Transaction transaction = new Transaction(id, balanceBefore, balanceAfter, null, localDateTime);
					transactions.add(transaction);
				}
			}

			return transactions;
		} catch (SQLException e) {
			throw new DaoException("Ошибка получения транзакций игрока: ", e);
		}
	}

}
