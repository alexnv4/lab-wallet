package ru.alexnv.apps.wallet.service.containers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

	public static final String IMAGE_VERSION = "postgres";
	public static final String DB_NAME = "walletdb";
	public static PostgreSQLContainer<?> container;

	public PostgresTestContainer() {
		super(IMAGE_VERSION);
	}

	@SuppressWarnings("resource")
	public static PostgreSQLContainer<?> getInstance() {
		if (container == null) {
			container = new PostgresTestContainer().withDatabaseName(DB_NAME);
		}
		return container;
	}

	@Override
	public void start() {
		super.start();
		System.setProperty("DB_URL", container.getJdbcUrl());
		System.setProperty("DB_USERNAME", container.getUsername());
		System.setProperty("DB_PASSWORD", container.getPassword());
	}

	@Override
	public void stop() {
		// container.stop();
	}

}
