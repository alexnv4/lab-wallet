package ru.alexnv.apps.wallet.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import ru.alexnv.apps.wallet.infrastructure.dao.AuditorDao;
import ru.alexnv.apps.wallet.infrastructure.dao.AuditorDaoImpl;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDao;
import ru.alexnv.apps.wallet.infrastructure.dao.PlayerDaoImpl;
import ru.alexnv.apps.wallet.infrastructure.dao.TransactionDao;
import ru.alexnv.apps.wallet.infrastructure.dao.TransactionDaoImpl;

public class PostgreSqlDaoFactory implements DaoFactory {

	/**
	 * Имя файла, в котором хранятся настройки БД
	 */
	private static final String liquibaseFileName = "liquibase.properties";

	/**
	 * Имя пользователя БД
	 */
	private String user;

	/**
	 * Пароль пользователя БД
	 */
	private String password;

	/**
	 * JDBC URL для соединения
	 */
	private String url;

	/**
	 * Свойства для подключения
	 */
	private Properties props;

	// private String driver;

	/**
	 * Соединение с БД
	 */
	private Connection connection = null;

	public PostgreSqlDaoFactory(boolean readProperties) {
		if (readProperties) {
			props = new Properties();
			try (InputStream is = getClass().getClassLoader().getResourceAsStream(liquibaseFileName)) {
				props.load(is);
				// driver = props.getProperty("driver");
				url = props.getProperty("url");
				user = props.getProperty("username");
				password = props.getProperty("password");

			} catch (IOException e) {
				e.printStackTrace();
			}

			// try {
			// Class.forName(driver);
			// } catch (ClassNotFoundException e) {
			// e.printStackTrace();
			// }
		}
	}

	public PostgreSqlDaoFactory() {

	}

	/**
	 * Получение соединения с БД
	 * 
	 * @return соединение
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(this.url, this.user, this.password);
		}
		return connection;
	}

	/**
	 * @param url      JDBC URL
	 * @param user     пользователь
	 * @param password пароль
	 * @return соединение
	 * @throws SQLException
	 */
	public Connection getConnection(String url, String user, String password) throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(url, user, password);
		}
		return connection;
	}

	@Override
	public PlayerDao getPlayerDao(Connection connection) {
		return new PlayerDaoImpl(connection);
	}

	@Override
	public TransactionDao getTransactionDao(Connection connection) {
		return new TransactionDaoImpl(connection);
	}

	@Override
	public AuditorDao getAuditorDao(Connection connection) {
		return new AuditorDaoImpl(connection);
	}

}
