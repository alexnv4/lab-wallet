package ru.alexnv.apps.wallet.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import ru.alexnv.apps.wallet.dao.AuditorDao;
import ru.alexnv.apps.wallet.dao.AuditorDaoImpl;
import ru.alexnv.apps.wallet.dao.PlayerDao;
import ru.alexnv.apps.wallet.dao.PlayerDaoImpl;
import ru.alexnv.apps.wallet.dao.TransactionDao;
import ru.alexnv.apps.wallet.dao.TransactionDaoImpl;

/**
 * Реализация фабрики DAO для PostgreSQL БД.
 */
public class PostgreSqlDaoFactory implements DaoFactory {

	/** Имя файла, в котором хранятся настройки БД. */
	private static final String liquibaseFileName = "liquibase.properties";

	/** Имя пользователя БД. */
	private String user;

	/** Пароль пользователя БД. */
	private String password;

	/** JDBC URL для соединения. */
	private String url;

	/** Свойства для подключения. */
	private Properties props;

	 /** Драйвер БД. */
 	private String driver;

	/** Соединение с БД. */
	private Connection connection = null;

	/**
	 * Создание PostgreSqlDaoFactory.
	 *
	 * @param readProperties читать ли параметры из файла
	 */
	public PostgreSqlDaoFactory(boolean readProperties) {
		if (readProperties) {
			props = new Properties();
			try (InputStream is = getClass().getClassLoader().getResourceAsStream(liquibaseFileName)) {
				props.load(is);
				driver = props.getProperty("driver");
				url = props.getProperty("url");
				user = props.getProperty("username");
				password = props.getProperty("password");

			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Создание PostgreSqlDaoFactory.
	 */
	public PostgreSqlDaoFactory() {

	}

	/**
	 * Установка соединения с БД.
	 *
	 * @return соединение
	 * @throws SQLException ошибка БД
	 */
	@Override
	public Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
		}
		return connection;
	}

	/**
	 * Установка соединения с БД.
	 *
	 * @param url      JDBC URL
	 * @param user     пользователь
	 * @param password пароль
	 * @return соединение
	 * @throws SQLException ошибка БД
	 */
	public Connection getConnection(String url, String user, String password) throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(url, user, password);
		}
		return connection;
	}

	/**
	 * Получение реализация DAO игрока.
	 *
	 * @param connection установленное соединение
	 * @return DAO игрока
	 */
	@Override
	public PlayerDao getPlayerDao(Connection connection) {
		return new PlayerDaoImpl(connection);
	}

	/**
	 * Получение реализация DAO транзакции.
	 *
	 * @param connection установленное соединение
	 * @return DAO транзакции
	 */
	@Override
	public TransactionDao getTransactionDao(Connection connection) {
		return new TransactionDaoImpl(connection);
	}

	/**
	 * Получение реализация DAO аудита.
	 *
	 * @param connection установленное соединение
	 * @return DAO аудита
	 */
	@Override
	public AuditorDao getAuditorDao(Connection connection) {
		return new AuditorDaoImpl(connection);
	}

	/**
	 * Закрытие соединения с БД.
	 *
	 * @throws SQLException ошибка БД
	 */
	@Override
	public void closeConnection() throws SQLException {
		this.connection.close();		
	}

}
