package ru.alexnv.apps.wallet.in;

import java.math.BigDecimal;
import java.util.Map;

import ru.alexnv.apps.wallet.service.PlayerService;

/**
 * Стартовый класс после внедрения зависимостей
 */
public class Entry {
	
	/**
	 * Вспомогательный класс utility
	 */
	private Utility util = new Utility();

	/**
	 * Стартовое меню
	 */
	private final static Map<Integer, String> welcomeMenu = Map.of(
			WelcomeMenuChoices.CHOICE_REGISTER.getChoice(), " - Регистрация",
			WelcomeMenuChoices.CHOICE_LOGIN.getChoice(), " - Вход",
			WelcomeMenuChoices.CHOICE_EXIT.getChoice(), " - Выход из кошелька");
	
	/**
	 * Меню залогиненного игрока
	 */
	private final static Map<Integer, String> loggedMenu = Map.of(
			LoggedMenuChoices.CHOICE_BALANCE.getChoice(), " - Мой текущий баланс",
			LoggedMenuChoices.CHOICE_DEBIT.getChoice(), " - Снятие средств (дебет)",
			LoggedMenuChoices.CHOICE_CREDIT.getChoice(), " - Пополнение средств (кредит)",
			LoggedMenuChoices.CHOICE_HISTORY.getChoice(), " - История пополнений и снятий",
			LoggedMenuChoices.CHOICE_LOGOUT.getChoice(), " - Выход из аккаунта");
	
	
	/**
	 * Служба приложения, устанавливаемая внешним слоем
	 */
	private final PlayerService playerService;

	/**
	 * @param playerService
	 */
	public Entry(PlayerService playerService) {
		this.playerService = playerService;
		menuLoop();
	}
	
	/**
	 * Основной цикл приложения
	 * После каждого case устанавливается следующее состояние приложения
	 */
	private void menuLoop() {
		
		States screenState = States.WELCOME;
		String[] credentials;
		
		do {
			switch (screenState) {
			case WELCOME:
				WelcomeMenuChoices welcomeMenuChoice = welcomeMenuInput();
				switch (welcomeMenuChoice) {
				case CHOICE_REGISTER:
					screenState = States.REGISTER;
					break;
				case CHOICE_LOGIN:
					screenState = States.AUTHORIZE;
					break;
				case CHOICE_EXIT:
					screenState = States.EXIT;
					break;
				default:
					screenState = States.WELCOME;
					util.printLine("Ошибка ввода. Повторите");
				}
				break;
				
			case REGISTER:
				credentials = util.getCredentials();
				boolean registered = playerService.registration(credentials[0], credentials[1]);
				if (registered) {
					util.printLine("Пользователь " + credentials[0] + " зарегистрирован.");
				}
				else {
					util.printLine("Ошибка регистрации.");
				}
				screenState = States.WELCOME;
				break;
				
			case AUTHORIZE:
				credentials = util.getCredentials();
				boolean authorized = playerService.authorize(credentials[0], credentials[1]);
				if (authorized) {
					util.printLine("Добро пожаловать, " + credentials[0]);
					screenState = States.LOGGED_IN;
				}
				break;
				
			case LOGGED_IN:
				LoggedMenuChoices loggedMenuChoices = loggedMenuInput();
				switch (loggedMenuChoices) {
				case CHOICE_BALANCE:
					String balance = playerService.getBalance();
					util.printLine(balance);

					screenState = States.LOGGED_IN;
					break;
				case CHOICE_DEBIT:
					screenState = States.DEBIT;
					break;
				case CHOICE_CREDIT:
					screenState = States.CREDIT;
					break;
				case CHOICE_HISTORY:
					String transactionsHistory = playerService.getTransactionsHistory();
					util.printLine(transactionsHistory);
					
					screenState = States.LOGGED_IN;
					break;
				case CHOICE_LOGOUT:
					screenState = States.WELCOME;
					break;
				default:
					break;
				}
				break;
				
			case DEBIT:
				BigDecimal inputDebit = balanceInput();
				String result = playerService.debit(inputDebit);
				if (result != null) {
					util.printLine(result);					
				}
				
				screenState = States.LOGGED_IN;
				break;
				
			case CREDIT:
				BigDecimal inputCredit = balanceInput();
				playerService.credit(inputCredit);
				
				screenState = States.LOGGED_IN;
				break;
				
			case EXIT:
				break;
			}
		}
		while (screenState != States.EXIT);
	}
	
	/**
	 * Печать меню и выбор пункта стартового меню
	 * @return выбранный пункт меню
	 */
	private WelcomeMenuChoices welcomeMenuInput() {
		String[] welcomeText = util.convertMapToStringArray(welcomeMenu); 
		int choice = util.getUserChoice(welcomeText);

		WelcomeMenuChoices welcomeMenuChoice = util.getWelcomeEnumByNumber(choice);
		return welcomeMenuChoice;
	}

	/**
	 * Печать меню и выбор пункта меню залогиненного игрока
	 * @return выбранный пункт меню
	 */
	private LoggedMenuChoices loggedMenuInput() {
		String[] loggedText = util.convertMapToStringArray(loggedMenu);
		int choice = util.getUserChoice(loggedText);
		
		LoggedMenuChoices loggedMenuChoice = util.getLoggedEnumByNumber(choice);
		return loggedMenuChoice;
	}

	/**
	 * Ввод баланса
	 * @return BigDecimal баланс
	 */
	private BigDecimal balanceInput() {
		String input = util.getString("Введите количество средств: ");
		BigDecimal balance = new BigDecimal(input);
		return balance;
	}
}

