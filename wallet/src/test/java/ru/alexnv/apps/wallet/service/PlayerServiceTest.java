package ru.alexnv.apps.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.PlayerOperationsService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.infrastructure.LiquibaseMigrations;
import ru.alexnv.apps.wallet.infrastructure.PostgreSqlDaoFactory;
import ru.alexnv.apps.wallet.service.config.ContainerEnvironment;
import ru.alexnv.apps.wallet.service.exceptions.*;

@DisplayName("PlayerService test class")
class PlayerServiceTest extends ContainerEnvironment {

	private static PlayerService playerService;
	private static RegistrationService registrationService;
	private static AuthorizationService authorizationService;

	private static java.sql.Connection connection = null;
	private static PlayerOperationsService playerOperationsService = null;

	@BeforeAll
	@DisplayName("Инициализация")
	static void setup() throws Exception {
		String url = System.getProperty("DB_URL");
		String username = System.getProperty("DB_USERNAME");
		String password = System.getProperty("DB_PASSWORD");

		PostgreSqlDaoFactory daoFactory = new PostgreSqlDaoFactory();
		try {
			connection = daoFactory.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		authorizationService = new AuthorizationService(daoFactory.getPlayerDao(connection));
		registrationService = new RegistrationService(daoFactory.getPlayerDao(connection));
		playerOperationsService  = new PlayerOperationsService(authorizationService,
				daoFactory.getPlayerDao(connection), daoFactory.getTransactionDao(connection));
		playerService = new PlayerService(authorizationService, registrationService, playerOperationsService,
				daoFactory.getAuditorDao(connection));

		// Запуск миграций
		LiquibaseMigrations lbm = new LiquibaseMigrations();
		lbm.migrate(connection);
		connection.close();
		connection = null;
	}

	@BeforeEach
	void init() {
		String url = System.getProperty("DB_URL");
		String username = System.getProperty("DB_USERNAME");
		String password = System.getProperty("DB_PASSWORD");

		PostgreSqlDaoFactory daoFactory = new PostgreSqlDaoFactory();
		try {
			connection = daoFactory.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		authorizationService = new AuthorizationService(daoFactory.getPlayerDao(connection));
		registrationService = new RegistrationService(daoFactory.getPlayerDao(connection));
		playerOperationsService  = new PlayerOperationsService(authorizationService,
				daoFactory.getPlayerDao(connection), daoFactory.getTransactionDao(connection));
		playerService = new PlayerService(authorizationService, registrationService, playerOperationsService,
				daoFactory.getAuditorDao(connection));;
	}

	@AfterEach
	void cleanup() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	@Test
	@DisplayName("Возврат true при успешной регистрации")
	void should_ReturnTrue_WhenRegistered() throws RegistrationException {

		// given
		String login = "login1";
		String password = "password1";

		// when
		String registeredLogin = playerService.registration(login, password);

		// then
		assertTrue(login.equals(registeredLogin));
	}

	@Test
	@DisplayName("RegistrationException при регистрации одинакового логина")
	void should_ThrowRegistrationException_WhenNotRegistered() {

		// given
		String login = "login2";
		String password = "password2";
		try {
			playerService.registration(login, password);
		} catch (RegistrationException e) {
			e.printStackTrace();
		}

		// when
		Executable executable = () -> playerService.registration(login, password);

		// then
		assertThrows(RegistrationException.class, executable);
	}

	@Test
	@DisplayName("Возврат true при успешной авторизации")
	void should_ReturnTrue_WhenAuthorized() throws AuthorizationException, RegistrationException {
		String login = "login3";
		String password = "password3";

		playerService.registration(login, password);

		String authorizedLogin = playerService.authorize(login, password).getLogin();

		assertTrue(login.equals(authorizedLogin));
	}

	@Test
	@DisplayName("AuthorizationException при несуществующем логине")
	void should_ThrowAuthorizationException_WhenAccountNotExists() {
		String login = "login9";
		String password = "password4";

		Executable executable = () -> playerService.authorize(login, password);

		assertThrows(AuthorizationException.class, executable);
	}

	@Test
	@DisplayName("AuthorizationException при неправильном пароле")
	void should_ThrowAuthorizationException_WhenIncorrectPassword() throws RegistrationException {
		String login = "login4";
		String password = "password4";
		String incorrectPassword = "password999";

		playerService.registration(login, password);

		Executable executable = () -> playerService.authorize(login, incorrectPassword);

		assertThrows(AuthorizationException.class, executable);
	}

	@Test
	@DisplayName("Возврат null при выходе из аккаунта")
	void should_ReturnNull_WhenLogout() throws AuthorizationException, RegistrationException {
		String login = "login5";
		String password = "password5";

		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.logout();

		assertNull(authorizationService.getPlayer());
	}

	@Test
	@DisplayName("Возврат баланса после кредита")
	void should_ReturnBalance_WhenCredited() throws AuthorizationException, RegistrationException, TransactionIdNotUniqueException {
		String login = "login6";
		String password = "password6";
		BigDecimal amount = new BigDecimal("5.67");

		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.credit(amount, 99999L);
		String actual = playerService.getBalance().getBalance();

		assertEquals(amount.toString(), actual);
	}

	@Test
	@DisplayName("Возврат баланса после дебета")
	void should_ReturnBalance_WhenDebited()
			throws AuthorizationException, RegistrationException, TransactionIdNotUniqueException, DebitException {
		String login = "login7";
		String password = "password7";
		BigDecimal amount = new BigDecimal("5.67");

		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.credit(amount, 99998L);
		playerService.debit(new BigDecimal("2.34"), 999997L);
		String actual = playerService.getBalance().getBalance();

		assertEquals("3.33", actual);
	}

}
