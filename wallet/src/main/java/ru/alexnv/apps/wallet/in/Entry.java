package ru.alexnv.apps.wallet.in;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.alexnv.apps.wallet.in.exceptions.IncorrectBalanceInputException;
import ru.alexnv.apps.wallet.in.exceptions.IncorrectMenuChoiceException;
import ru.alexnv.apps.wallet.in.exceptions.NotNumberException;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

/**
 * Стартовый класс после внедрения зависимостей
 */
public class Entry {
	
	/**
	 * Стартовое меню
	 */
	private final static Map<Integer, String> welcomeMenu = new LinkedHashMap<>();
			
	
	/**
	 * Меню залогиненного игрока
	 */
	private final static Map<Integer, String> loggedMenu = new LinkedHashMap<>();

	/**
	 * Служба приложения, устанавливаемая внешним слоем
	 */
	private final PlayerService playerService;

	/**
	 * Вспомогательный класс utility
	 */
	private Utility util = new Utility();

	/**
	 * @param playerService
	 */
	public Entry(PlayerService playerService) {
		this.playerService = playerService;

		// Заполнение пунктов меню
		welcomeMenu.put(WelcomeMenuChoices.CHOICE_REGISTER.getChoice(), " - Регистрация");
		welcomeMenu.put(WelcomeMenuChoices.CHOICE_LOGIN.getChoice(), " - Вход");
		welcomeMenu.put(WelcomeMenuChoices.CHOICE_EXIT.getChoice(), " - Выход из кошелька");
		
		loggedMenu.put(LoggedMenuChoices.CHOICE_BALANCE.getChoice(), " - Мой текущий баланс");
		loggedMenu.put(LoggedMenuChoices.CHOICE_DEBIT.getChoice(), " - Снятие средств (дебет)");
		loggedMenu.put(LoggedMenuChoices.CHOICE_CREDIT.getChoice(), " - Пополнение средств (кредит)");
		loggedMenu.put(LoggedMenuChoices.CHOICE_HISTORY.getChoice(), " - История пополнений и снятий");
		loggedMenu.put(LoggedMenuChoices.CHOICE_LOGOUT.getChoice(), " - Выход из аккаунта");
		
		// Запуск основного меню
		menuLoop();
	}

	/**
	 * Основной цикл приложения После каждого case устанавливается следующее
	 * состояние приложения
	 */
	private void menuLoop() {
		States screenState = States.WELCOME;
		String[] credentials;

		do {
			switch (screenState) {
			case WELCOME:
				WelcomeMenuChoices welcomeMenuChoice;
				try {
					welcomeMenuChoice = menuInput(welcomeMenu, WelcomeMenuChoices.class);
				} catch (IncorrectMenuChoiceException e) {
					util.printLine(e.getMessage());
					welcomeMenuChoice = WelcomeMenuChoices.CHOICE_NOT_EXIST;
				}
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
				case CHOICE_NOT_EXIST:
					screenState = States.WELCOME;
					break;
				default:
					screenState = States.WELCOME;
					util.printLine("Ошибка ввода. Повторите");
				}
				break;

			case REGISTER:
				credentials = util.getCredentials();
				try {
					String registeredLogin = playerService.registration(credentials[0], credentials[1]);
					util.printLine("Пользователь " + registeredLogin + " зарегистрирован.");
				} catch (RegistrationException e) {
					util.printLine(e.getMessage());
				}

				screenState = States.WELCOME;
				break;

			case AUTHORIZE:
				credentials = util.getCredentials();
				try {
					String authorizedLogin = playerService.authorize(credentials[0], credentials[1]);
					util.printLine("Добро пожаловать, " + authorizedLogin);
					screenState = States.LOGGED_IN;
				} catch (AuthorizationException e) {
					util.printLine(e.getMessage());
					screenState = States.WELCOME;
				}

				break;

			case LOGGED_IN:
				LoggedMenuChoices loggedMenuChoices;
				try {
					loggedMenuChoices = menuInput(loggedMenu, LoggedMenuChoices.class);
				} catch (IncorrectMenuChoiceException e) {
					util.printLine(e.getMessage());
					loggedMenuChoices = LoggedMenuChoices.CHOICE_NOT_EXIST;
				}

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
					List<String> transactionsHistory = playerService.getTransactionsHistory();
					util.printText(transactionsHistory.toArray(new String[0]));

					screenState = States.LOGGED_IN;
					break;
				case CHOICE_LOGOUT:
					playerService.logout();

					screenState = States.WELCOME;
					break;
				case CHOICE_NOT_EXIST:
					screenState = States.LOGGED_IN;
					break;
				default:
					util.printLine("Ошибка ввода. Повторите");
					break;
				}
				break;

			case DEBIT:
				BigDecimal inputDebit = null;
				try {
					inputDebit = balanceInput();

					String result = playerService.debit(inputDebit);
					if (result != null) {
						util.printLine(result);
					}
				} catch (IncorrectBalanceInputException e) {
					util.printLine(e.getMessage());
				}

				screenState = States.LOGGED_IN;
				break;

			case CREDIT:
				BigDecimal inputCredit = null;
				try {
					inputCredit = balanceInput();

					playerService.credit(inputCredit);
				} catch (IncorrectBalanceInputException e) {
					util.printLine(e.getMessage());
				}

				screenState = States.LOGGED_IN;
				break;

			case EXIT:
				break;
			}
		} while (screenState != States.EXIT);
	}
	
	private int menuPrompt(String[] menuText) throws IncorrectMenuChoiceException {
		int choice = 0; // введённое число, может не сооветствовать пункту меню
		
		try {
			choice = util.getUserChoice(menuText);
		} catch (NotNumberException e) {
			throw new IncorrectMenuChoiceException("Ошибка ввода числа.");
		}
		
		return choice;
	}
	
	/**
	 * Печать меню и выбор пункта меню
	 * 
	 * @param menuMap   меню в виде Map
	 * @param enumClass класс enum, реализующий интерфейс MenuEnum
	 * @param <T>       значение меню в виде enum, реализующее интерфейс MenuEnum
	 * @return выбранный пункт меню
	 * @throws IncorrectMenuChoiceException если такого пункта меню нет
	 */
	private <T extends Enum<T> & MenuEnum> T menuInput(Map<Integer, String> menuMap, Class<T> enumClass)
			throws IncorrectMenuChoiceException {
		String[] menuText = util.convertMapToStringArray(menuMap);
		int choice = menuPrompt(menuText);

		// сопоставление введённого числа и пункта меню
		T menuChoice = util.getEnumByNumber(enumClass, choice);
		return menuChoice;
	}

	/**
	 * Ввод баланса
	 * 
	 * @return BigDecimal баланс
	 * @throws IncorrectBalanceInputException если неправильно введён баланс
	 */
	private BigDecimal balanceInput() throws IncorrectBalanceInputException {
		String input = util.getString("Введите количество средств: ");
		BigDecimal balance = null;
		try {
			balance = new BigDecimal(input);
			
			// если введённый баланс отрицательный или ноль
			if (balance.compareTo(new BigDecimal("0.00")) <= 0) {
				throw new IncorrectBalanceInputException("Неправильный ввод средств.");
			}
		} catch (NumberFormatException e) {
			throw new IncorrectBalanceInputException("Неправильный ввод средств.");
		}
		return balance;
	}
}
