package ru.alexnv.apps.wallet.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.dao.AuditorDao;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.TransactionDto;
import ru.alexnv.apps.wallet.domain.mappers.PlayerMapper;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.PlayerOperationsService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.domain.service.exceptions.DatabaseException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;
import ru.alexnv.apps.wallet.domain.service.exceptions.NoSuchPlayerException;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.domain.service.exceptions.WrongPasswordException;
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;
import ru.alexnv.apps.wallet.service.exceptions.DebitException;
import ru.alexnv.apps.wallet.service.exceptions.FindPlayerByIdException;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

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
	public PlayerDto authorize(String login, char[] password) throws AuthorizationException {
		Action action = null;
		PlayerDto playerDto = null;

		try {
			playerDto = PlayerMapper.INSTANCE.toDto(authorizationService.authorize(login, password));

			action = new Action(playerDto, "игрок " + login + " вошёл в кошелёк");
		} catch (NoSuchPlayerException | WrongPasswordException | DatabaseException e) {
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
	public String registration(String login, char[] password) throws RegistrationException {
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
	 * @param playerId ID игрока 
	 * @return баланс в формате строки
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public PlayerDto getBalance(Long playerId) throws FindPlayerByIdException {
		Action action;
		PlayerDto playerDto;
		
		try {
			playerDto = playerOperationsService.getBalance(playerId);
			
			action = new Action(playerDto, "запрос баланса");
			audit.addAction(action);
		} catch (DatabaseException e) {
			e.printStackTrace();
			throw new FindPlayerByIdException(e.getMessage());
		}
		return playerDto;
	}

	/**
	 * Кредит на игрока
	 * 
	 * @param playerId ID игрока 
	 * @param balance добавление баланса
	 * @param transactionId предоставленный идентификатор транзакции
	 * @return DTO игрока
	 * @throws TransactionIdNotUniqueException идентификатор транзакции не уникальный
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public PlayerDto credit(Long playerId, BigDecimal balance, Long transactionId) throws TransactionIdNotUniqueException, FindPlayerByIdException {
		PlayerDto playerDto = null;
		
		try {
			playerDto = playerOperationsService.creditPlayer(playerId, balance, transactionId);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		Action action = new Action(playerDto, "кредит игрока на сумму " + balance.toString());
		audit.addAction(action);
		
		return playerDto;
	}

	/**
	 * Дебет на игрока
	 * 
	 * @param playerId ID игрока
	 * @param balance уменьшение баланса
	 * @param transactionId идентификатор транзакции
	 * @return DTO игрока
	 * @throws TransactionIdNotUniqueException идентификатор транзакции не уникальный
	 * @throws DebitException ошибка дебета
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public PlayerDto debit(Long playerId, BigDecimal balance, Long transactionId) throws TransactionIdNotUniqueException, DebitException, FindPlayerByIdException {
		PlayerDto playerDto = new PlayerDto();
		playerDto.setId(playerId);
		
		Action action = new Action(playerDto, "попытка дебета игрока на сумму " + balance.toString());
		
		try {
			playerDto = playerOperationsService.debitPlayer(playerId, balance, transactionId);
			action = new Action(playerDto, "дебет игрока на сумму " + balance.toString());
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
	 * @param playerId ID игрока
	 * @return список транзакций в DTO
	 * @throws FindPlayerByIdException игрок с таким ID не найден
	 */
	public List<TransactionDto> getTransactionsHistory(Long playerId) throws FindPlayerByIdException {
		PlayerDto playerDto = new PlayerDto();
		playerDto.setId(playerId);
		
		Action action = new Action(playerDto, "запрос игроком списка транзакций");
		audit.addAction(action);

		try {
			return playerOperationsService.getTransactionsHistory(playerId);
		} catch (DatabaseException e) {
			return new ArrayList<>();
		}
	}

}