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
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

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
	 * Имя файла свойств БД и liquibase
	 */
	private static final String liquibaseFileName = "liquibase.properties";
	
	/**
	 * Чтение файла свойств и установка полей класса
	 */
	public LiquibaseMigrations() {
		props = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(liquibaseFileName)) {
			props.load(is);
			changeLogFile = props.getProperty("changeLogFile");
			defaultSchemaName = props.getProperty("defaultSchemaName");
			liquibaseSchemaName = props.getProperty("liquibaseSchemaName");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Создание служебной схемы liquibase по умолчанию
	 * @param connection установленное соединение
	 * @param schemaName имя схемы
	 * @throws SQLException
	 */
	private void createDefaultSchema(Connection connection, String schemaName) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("create schema if not exists " + schemaName);
		}
	}
	
	/**
	 * Выполнение миграций
	 */
	@SuppressWarnings("deprecation")
	public void migrate() {
		
		DaoFactory daoFactory = new PostgreSqlDaoFactory(true); 
		try (Connection connection = daoFactory.getConnection()) {
			createDefaultSchema(connection, "liquibase_schema");
			
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setDefaultSchemaName(defaultSchemaName);
			database.setLiquibaseSchemaName(liquibaseSchemaName);
			try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
				liquibase.update();
			}
			System.out.println("Миграции успешно выполнены.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (Exception dbe) {
			dbe.printStackTrace();
			System.exit(2);
		}
	}
	
	/**
	 * Выполнение миграций
	 * @param connection установленное соединение
	 */
	@SuppressWarnings("deprecation")
	public void migrate(Connection connection) {
		
		try {
			createDefaultSchema(connection, "liquibase_schema");
			
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setDefaultSchemaName(defaultSchemaName);
			database.setLiquibaseSchemaName(liquibaseSchemaName);
			try (Liquibase liquibase = new Liquibase(changeLogFile, new ClassLoaderResourceAccessor(), database)) {
				liquibase.update();
			}
			System.out.println("Миграции успешно выполнены.");
		} catch (SQLException e) {
			System.out.println("---SQL EXCEPTION---");
			e.printStackTrace();
			System.exit(1);
		} catch (Exception dbe) {
			System.out.println("---EXCEPTION---");
			dbe.printStackTrace();
			System.exit(2);
		}
	}

}
