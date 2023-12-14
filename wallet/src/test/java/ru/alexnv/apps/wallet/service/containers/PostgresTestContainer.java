package ru.alexnv.apps.wallet.service.containers;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * TestContainer с PostgreSQL.
 */
public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

	/** Название докер образа. */
	public static final String IMAGE_VERSION = "postgres";
	
	/** Название базы данных. */
	public static final String DB_NAME = "walletdb";
	
	/** Докер контейнер. */
	public static PostgreSQLContainer<?> container;

	/**
	 * Создание тест контейнера.
	 */
	public PostgresTestContainer() {
		super(IMAGE_VERSION);
	}

	/**
	 * Получение тест контейнера PostgreSQL.
	 *
	 * @return синглтон тест контейнер PostgreSQL
	 */
	@SuppressWarnings("resource")
	public static PostgreSQLContainer<?> getInstance() {
		if (container == null) {
			container = new PostgresTestContainer().withDatabaseName(DB_NAME);
		}
		return container;
	}

	/**
	 * Запуск контейнера.
	 */
	@Override
	public void start() {
		super.start();
		System.setProperty("DB_URL", container.getJdbcUrl());
		System.setProperty("DB_USERNAME", container.getUsername());
		System.setProperty("DB_PASSWORD", container.getPassword());
	}

	/**
	 * 
	 */
	@Override
	public void stop() {
		
	}

}
