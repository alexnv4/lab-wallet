package ru.alexnv.apps.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import liquibase.exception.DatabaseException;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.mappers.PlayerMapper;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.PlayerOperationsService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.domain.service.exceptions.LoginRepeatException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;
import ru.alexnv.apps.wallet.infrastructure.dao.AuditorDao;
import ru.alexnv.apps.wallet.service.exceptions.*;

/**
 * Сервис игрока
 */
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
	 * Внедрённый доменный сервис операций игрока
	 */
	private final PlayerOperationsService playerOperationsService;

	/**
	 * Аудит действий
	 */
	private Auditor audit;

	/**
	 * Создание сервиса игрока
	 * 
	 * @param authorizationService
	 * @param registrationService
	 * @param playerOperationsService
	 * @param auditorDao
	 */
	public PlayerService(AuthorizationService authorizationService, RegistrationService registrationService,
			PlayerOperationsService playerOperationsService, AuditorDao auditorDao) {
		super();
		this.authorizationService = authorizationService;
		this.registrationService = registrationService;
		this.playerOperationsService = playerOperationsService;
		audit = new Auditor(auditorDao);
	}

	/**
	 * Авторизация 
	 * Управляет вызовом функции из доменного сервиса авторизации
	 * 
	 * @param login
	 * @param password
	 * @return DTO игрока
	 * @throws AuthorizationException - если авторизация не удалась
	 */
	public PlayerDto authorize(String login, String password) throws AuthorizationException {
		Action action = null;
		PlayerDto playerDto = null;

		try {
			playerDto = PlayerMapper.INSTANCE.toDto(authorizationService.authorize(login, password));

			action = new Action(authorizationService.getPlayer(), "игрок " + login + " вошёл в кошелёк");
		} catch (NoSuchPlayerException | WrongPasswordException | DatabaseException | LoginRepeatException e) {
			action = new Action("попытка входа игрока " + login + " в кошелёк");

			throw new AuthorizationException(e.getMessage());
		} finally {
			audit.addAction(action);
		}

		return playerDto;
	}

	/**
	 * Регистрация 
	 * Управляет вызовом функции из доменного сервиса регистрации
	 * 
	 * @param login
	 * @param password
	 * @return логин
	 * @throws RegistrationException - если регистрация не удалась
	 */
	public String registration(String login, String password) throws RegistrationException {
		Action action = null;

		try {
			registrationService.register(login, password);

			action = new Action("игрок " + login + " зарегистрирован");
		} catch (PlayerAlreadyExistsException | DatabaseException e) {
			action = new Action("попытка регистрации игрока " + login);

			throw new RegistrationException(e.getMessage());
		} finally {
			audit.addAction(action);
		}

		return login;
	}

	/**
	 * Запрос баланса игрока
	 * 
	 * @return баланс в формате строки
	 */
	public PlayerDto getBalance() {
		Action action = new Action(authorizationService.getPlayer(), "запрос баланса");
		audit.addAction(action);

		PlayerDto playerDto = PlayerMapper.INSTANCE.toDto(authorizationService.getPlayer());
		return playerDto;
	}

	/**
	 * Выход игрока из кошелька
	 */
	public void logout() {
		Action action = new Action(authorizationService.getPlayer(), "выход из аккаунта");
		audit.addAction(action);

		authorizationService.setPlayer(null);
	}

	/**
	 * Кредит на игрока
	 * 
	 * @param balance добавление баланса
	 * @param transactionId предоставленный идентификатор транзакции
	 * @return DTO игрока
	 * @throws TransactionIdNotUniqueException идентификатор транзакции не уникальный
	 */
	public PlayerDto credit(BigDecimal balance, Long transactionId) throws TransactionIdNotUniqueException {
		PlayerDto playerDto = null;
		try {
			playerDto = playerOperationsService.creditPlayer(balance, transactionId);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		Action action = new Action(authorizationService.getPlayer(), "кредит игрока на сумму " + balance.toString());
		audit.addAction(action);
		
		return playerDto;
	}

	/**
	 * Дебет на игрока
	 * 
	 * @param balance уменьшение баланса
	 * @param transactionId идентификатор транзакции
	 * @return DTO игрока
	 * @throws TransactionIdNotUniqueException идентификатор транзакции не уникальный
	 * @throws DebitException ошибка дебета
	 */
	public PlayerDto debit(BigDecimal balance, Long transactionId) throws TransactionIdNotUniqueException, DebitException {
		PlayerDto playerDto = null;
		Action action = new Action(authorizationService.getPlayer(),
				"попытка дебета игрока на сумму " + balance.toString());
		
		try {
			playerDto = playerOperationsService.debitPlayer(balance, transactionId);
			action = new Action(authorizationService.getPlayer(), "дебет игрока на сумму " + balance.toString());
		} catch (NoMoneyLeftException | DatabaseException e) {
			throw new DebitException(e.getMessage());
		} finally {
			audit.addAction(action);
		}

		return playerDto;
	}

	/**
	 * Получение списка выполненных транзакций игрока
	 * 
	 * @return список транзакций в виде текста
	 */
	public List<String> getTransactionsHistory() {
		Action action = new Action(authorizationService.getPlayer(), "запрос игроком списка транзакций");
		audit.addAction(action);

		try {
			return playerOperationsService.getTransactionsHistory();
		} catch (DatabaseException e) {
			return null;
		}
	}

}