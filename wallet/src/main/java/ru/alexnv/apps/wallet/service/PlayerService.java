package ru.alexnv.apps.wallet.service;

import java.math.BigDecimal;

import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

public class PlayerService {

	/**
	 * Внедрённый доменный сервис авторизации
	 */
	private final AuthorizationService authorizationService;
	/**
	 * Внедрённый доменный сервис регистрации
	 */
	private final RegistrationService registrationService;
	
	/**
	 * @param authorizationService
	 * @param registrationService
	 */
	
	private Auditor audit;
	
	public PlayerService(AuthorizationService authorizationService, RegistrationService registrationService) {
		super();
		this.authorizationService = authorizationService;
		this.registrationService = registrationService;
		audit = new Auditor();
	}
	
	/**
	 * Авторизация
	 * Управляет вызовом функции из доменного сервиса авторизации
	 * @param login
	 * @param password
	 * @return логин
	 * @throws AuthorizationException - если авторизация не удалась
	 */
	public String authorize(String login, String password) throws AuthorizationException {
		Action action = null;
		try {
			authorizationService.authorize(login, password);
			
			action = new Action(authorizationService.getPlayer(), "игрок вошёл в кошелёк");
		} catch (NoSuchPlayerException | WrongPasswordException e) {
			action = new Action(null, "попытка входа игрока " + login + " в кошелёк");

			throw new AuthorizationException(e.getMessage());
		} finally {
			audit.addAction(action);
		}
		
		return login;
	}
	
	/**
	 * Регистрация
	 * Управляет вызовом функции из доменного сервиса регистрации
	 * @param login
	 * @param password
	 * @return логин
	 * @throws RegistrationException - если регистрация не удалась
	 */
	public String registration(String login, String password) throws RegistrationException {
		Action action = null;
		try {
			registrationService.register(login, password);

			action = new Action(null, "игрок " + login + " зарегистрирован");
		} catch (PlayerAlreadyExistsException e) {
			action = new Action(null, "попытка регистрации игрока " + login);
			
			throw new RegistrationException(e.getMessage());
		} finally {
			audit.addAction(action);
		}
		
		return login;
	}
	
	/**
	 * Запрос баланса игрока
	 * @return баланс в формате строки
	 */
	public String getBalance() {
		Action action = new Action(authorizationService.getPlayer(), "запрос баланса");
		audit.addAction(action);
		
		return authorizationService.getPlayer().getBalance();
	}
	
	/**
	 * Выход игрока из кошелька
	 */
	public void logout() {
		authorizationService.setPlayer(null);
		
		Action action = new Action(authorizationService.getPlayer(), "выход из аккаунта");
		audit.addAction(action);
	}
	
	/**
	 * Кредит на игрока
	 * @param balance
	 */
	public void credit(BigDecimal balance) {		
		authorizationService.getPlayer().credit(balance);
		
		Action action = new Action(authorizationService.getPlayer(), "кредит игрока на сумму" + balance.toString());
		audit.addAction(action);		
	}
	
	/**
	 * Дебет на игрока
	 * @param balance
	 * @return сообщение об ошибке в случае неудачи
	 */
	public String debit(BigDecimal balance) {
		String result;
		try {
			authorizationService.getPlayer().debit(balance);
			result = null;
		} catch (NoMoneyLeftException e) {
			result = e.getMessage();
		}
		
		Action action;
		if (result == null)
			action = new Action(authorizationService.getPlayer(), "дебет игрока на сумму" + balance.toString());
		else 
			action = new Action(authorizationService.getPlayer(), "попытка дебета игрока на сумму" + balance.toString());
		audit.addAction(action);	
		return result;
	}
	
	/**
	 * Получение списка выполненных транзакций игрока
	 * @return список транзакций
	 */
	public String getTransactionsHistory() {
		Action action = new Action(authorizationService.getPlayer(), "запрос игроком списка транзакций");
		audit.addAction(action);
		
		return authorizationService.getPlayer().getTransactions().toString();
	}
	
}
