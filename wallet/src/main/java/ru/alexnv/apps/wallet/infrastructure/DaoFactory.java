/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure;

import java.sql.Connection;
import java.sql.SQLException;

import ru.alexnv.apps.wallet.dao.AuditorDao;
import ru.alexnv.apps.wallet.dao.PlayerDao;
import ru.alexnv.apps.wallet.dao.TransactionDao;

/**
 * Фабрика DAO
 */
public interface DaoFactory {

	/**
	 * @return установленное соединение
	 * @throws SQLException
	 */
	Connection getConnection() throws SQLException;
	
	/**
	 * Закрытие соединения
	 * 
	 * @throws SQLException
	 */
	void closeConnection() throws SQLException;

	/**
	 * @param connection
	 * @return DAO игрока
	 */
	PlayerDao getPlayerDao(Connection connection);

	/**
	 * @param connection
	 * @return DAO транзакции
	 */
	TransactionDao getTransactionDao(Connection connection);

	/**
	 * @param connection
	 * @return DAO аудита
	 */
	AuditorDao getAuditorDao(Connection connection);

}
