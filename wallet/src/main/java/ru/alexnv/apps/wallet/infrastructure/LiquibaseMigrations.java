/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.alexnv.apps.wallet.in.Utility;

/**
 * Миграции БД
 */
public class LiquibaseMigrations {

	/**
	 * Свойства миграций, считываются из файла
	 */
	private Properties props;

	/**
	 * Имя конфигурационного файла с миграциями (XML)
	 */
	private String changeLogFile;

	/**
	 * Схема БД по умолчанию
	 */
	private String defaultSchemaName;

	/**
	 * Схема миграций по умолчанию
	 */
	private String liquibaseSchemaName;

	/**
	 * Вспомогательный класс utility
	 */
	private Utility util = new Utility();

	/**
	 * Чтение файла свойств и установка полей класса
	 */
	public LiquibaseMigrations() {
		props = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(PostgreSqlDaoFactory.DB_PROPERTIES_FILE)) {
			props.load(is);
			changeLogFile = props.getProperty("changeLogFile");
			defaultSchemaName = props.getProperty("defaultSchemaName");
			liquibaseSchemaName = props.getProperty("liquibaseSchemaName");

		} catch (IOException e) {
			util.printLine("Ошибка чтения файла свойств миграций");
		}
	}

	/**
	 * Создание служебной схемы liquibase по умолчанию
	 * 
	 * @param connection установленное соединение
	 * @param schemaName имя схемы
	 * @throws SQLException
	 */
	private void createDefaultSchema(Connection connection, String schemaName) throws SQLException {
		String sql = "create schema if not exists ";
		
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql + schemaName);
		}
	}

	/**
	 * Выполнение миграций
	 */
	public void migrate() {
		DaoFactory daoFactory = new PostgreSqlDaoFactory(true);
		try (Connection connection = daoFactory.getConnection()) {
			migrate(connection);

		} catch (SQLException e) {
			util.printLine("Ошибка работы с БД.");
			System.exit(1);
		} catch (Exception dbe) {
			util.printLine("Ошибка выполнения миграций");
			System.exit(2);
		}
	}

	/**
	 * Выполнение миграций
	 * 
	 * @param connection установленное соединение
	 * @throws SQLException
	 * @throws Exception
	 */
	public void migrate(Connection connection) throws SQLException, Exception {
		createDefaultSchema(connection, liquibaseSchemaName);

		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		database.setDefaultSchemaName(defaultSchemaName);
		database.setLiquibaseSchemaName(liquibaseSchemaName);
		try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
			CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
			updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
			updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, changeLogFile);
			updateCommand.execute();
		}
		util.printLine("Миграции успешно выполнены.");
	}

}
