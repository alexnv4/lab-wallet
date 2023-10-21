/**
 * 
 */
package ru.alexnv.apps.wallet.service.config;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.alexnv.apps.wallet.service.containers.PostgresTestContainer;

/**
 * 
 */
@Testcontainers
public class ContainerEnvironment {

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer = PostgresTestContainer.getInstance();
}
