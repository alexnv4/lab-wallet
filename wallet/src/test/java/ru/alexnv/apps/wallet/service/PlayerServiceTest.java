package ru.alexnv.apps.wallet.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;

@DisplayName("PlayerService test class")
class PlayerServiceTest {

	private static PlayerService playerService;
	private static RegistrationService registrationService;
	private static AuthorizationService authorizationService;
	private static List<Player> players; 
	
	@BeforeAll
	@DisplayName("Инициализация")
	static void setup() {
		players = new ArrayList<>();
		authorizationService = new AuthorizationService(players);
		registrationService = new RegistrationService(players);
		playerService = new PlayerService(authorizationService, registrationService);
	}

	@Test
	@DisplayName("Возврат true при успешной регистрации")
	void should_ReturnTrue_WhenRegistered() {
		
		// given
		String login = "login1";
		String password = "password1";
		
		// when
		boolean registered = playerService.registration(login, password);
		
		// then
		assertTrue(registered);		
	}
	
	@Test
	@DisplayName("Возврат false при неуспешной регистрации")
	void should_ReturnFalse_WhenNotRegistered() {
		
		// given
		String login = "login2";
		String password = "password2";
		playerService.registration(login, password);
		
		// when
		boolean registeredAgain = playerService.registration(login, password);
		
		// then
		assertFalse(registeredAgain);
	}
	
	@Test
	@DisplayName("Возврат true при успешной авторизации")
	void should_ReturnTrue_WhenAuthorized() {
		String login = "login3";
		String password = "password3";
		
		boolean authorized = playerService.registration(login, password);
		
		assertTrue(authorized);
	}
	
	@Test
	@DisplayName("Возврат false при уже существующем игроке")
	void should_ReturnFalse_WhenLoginNotExists() {
		String login = "login4";
		String password = "password4";
		
		boolean authorized = playerService.authorize(login, password);
		
		assertFalse(authorized);
	}
	
	@Test
	@DisplayName("Возврат false при уже неправильном пароле")
	void should_ReturnFalse_WhenIncorrectPassword() {
		String login = "login4";
		String password = "password4";
		String incorrectPassword = "password999";
		
		playerService.registration(login, password);
		
		boolean authorized = playerService.authorize(login, incorrectPassword);
		
		assertFalse(authorized);
	}
	
	@Test
	@DisplayName("Возврат null при выходе из аккаунта")
	void should_ReturnNull_WhenLogout() {
		String login = "login5";
		String password = "password5";
		
		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.logout();
		
		assertNull(authorizationService.getPlayer());
	}
	
	@Test
	@DisplayName("Возврат баланса после кредита")
	void should_ReturnBalance_WhenCredited () {
		String login = "login6";
		String password = "password6";
		BigDecimal amount = new BigDecimal("5.67");
		
		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.credit(amount);
		String actual = playerService.getBalance();
				
		assertEquals(amount.toString(), actual);
	}
	
	@Test
	@DisplayName("Возврат баланса после дебета")
	void should_ReturnBalance_WhenDebited () {
		String login = "login7";
		String password = "password7";
		BigDecimal amount = new BigDecimal("5.67");
		
		playerService.registration(login, password);
		playerService.authorize(login, password);
		playerService.credit(amount);
		playerService.debit(new BigDecimal("2.34"));
		String actual = playerService.getBalance();
				
		assertEquals("3.33", actual);
	}

}
