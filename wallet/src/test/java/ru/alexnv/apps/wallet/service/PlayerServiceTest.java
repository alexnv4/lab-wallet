package ru.alexnv.apps.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;
import ru.alexnv.apps.wallet.service.exceptions.DebitException;
import ru.alexnv.apps.wallet.service.exceptions.FindPlayerByIdException;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

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
		playerOperationsService  = new PlayerOperationsService(
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
		playerOperationsService  = new PlayerOperationsService(
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
		String registeredLogin = playerService.registration(login, password.toCharArray());

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
			playerService.registration(login, password.toCharArray());
		} catch (RegistrationException e) {
			e.printStackTrace();
		}

		// when
		Executable executable = () -> playerService.registration(login, password.toCharArray());

		// then
		assertThrows(RegistrationException.class, executable);
	}

	@Test
	@DisplayName("Возврат true при успешной авторизации")
	void should_ReturnTrue_WhenAuthorized() throws AuthorizationException, RegistrationException {
		String login = "login3";
		String password = "password3";

		playerService.registration(login, password.toCharArray());

		String authorizedLogin = playerService.authorize(login, password.toCharArray()).getLogin();

		assertTrue(login.equals(authorizedLogin));
	}

	@Test
	@DisplayName("AuthorizationException при несуществующем логине")
	void should_ThrowAuthorizationException_WhenAccountNotExists() {
		String login = "login9";
		String password = "password4";

		Executable executable = () -> playerService.authorize(login, password.toCharArray());

		assertThrows(AuthorizationException.class, executable);
	}

	@Test
	@DisplayName("AuthorizationException при неправильном пароле")
	void should_ThrowAuthorizationException_WhenIncorrectPassword() throws RegistrationException {
		String login = "login4";
		String password = "password4";
		String incorrectPassword = "password999";

		playerService.registration(login, password.toCharArray());

		Executable executable = () -> playerService.authorize(login, incorrectPassword.toCharArray());

		assertThrows(AuthorizationException.class, executable);
	}

	@Test
	@DisplayName("Возврат баланса после кредита")
	void should_ReturnBalance_WhenCredited() throws AuthorizationException, RegistrationException, TransactionIdNotUniqueException, FindPlayerByIdException {
		String login = "login6";
		String password = "password6";
		BigDecimal amount = new BigDecimal("5.67");

		playerService.registration(login, password.toCharArray());
		Long playerId = playerService.authorize(login, password.toCharArray()).getId();
		playerService.credit(playerId, amount, 99999L);
		String actual = playerService.getBalance(playerId).getBalance();

		assertEquals(amount.toString(), actual);
	}

	@Test
	@DisplayName("Возврат баланса после дебета")
	void should_ReturnBalance_WhenDebited()
			throws AuthorizationException, RegistrationException, TransactionIdNotUniqueException, DebitException, FindPlayerByIdException {
		String login = "login7";
		String password = "password7";
		BigDecimal amount = new BigDecimal("5.67");

		playerService.registration(login, password.toCharArray());
		Long playerId = playerService.authorize(login, password.toCharArray()).getId();
		playerService.credit(playerId, amount, 99998L);
		playerService.debit(playerId, new BigDecimal("2.34"), 999997L);
		String actual = playerService.getBalance(playerId).getBalance();

		assertEquals("3.33", actual);
	}

}
