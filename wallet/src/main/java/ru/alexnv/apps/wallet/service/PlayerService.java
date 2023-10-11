package ru.alexnv.apps.wallet.service;

import java.math.BigDecimal;

import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;

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
	 * @return успешность авторизации
	 */
	public boolean authorize(String login, String password) {
		boolean authorized = false;
		try {
			authorizationService.authorize(login, password);
			authorized = true;
		} catch (NoSuchPlayerException | WrongPasswordException e) {
			authorized = false;
		}
		
		Action action;
		if (authorized) 
			action= new Action(authorizationService.getPlayer(), "игрок вошёл в кошелёк");
		else
			action = new Action(null, "попытка входа игрока " + login + " в кошелёк");
		audit.addAction(action);

		return authorized;
	}
	
	/**
	 * Регистрация
	 * Управляет вызовом функции из доменного сервиса регистрации
	 * @param login
	 * @param password
	 * @return успешность регистрации
	 */
	public boolean registration(String login, String password) {
		boolean registered = false;
		try {
			registrationService.register(login, password);
			registered = true;
		} catch (PlayerAlreadyExistsException e) {
			registered = false;
		}
		
		Action action;
		if (registered) 
			action= new Action(null, "игрок " + login + " зарегистрирован");
		else
			action = new Action(null, "попытка регистрации игрока " + login);
		audit.addAction(action);
		
		return registered;
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
