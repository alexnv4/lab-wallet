package ru.alexnv.apps.wallet.in;

import java.util.Map;
import java.util.Scanner;

/**
 * Вспомогательный класс
 * Консольный ввод и вывод указаны только здесь в единственном экземпляре 
 */
public class Utility {
	
	/**
	 * Разделитель для печати меню
	 */
	private static final String delimiter = "----------------------------";
	/**
	 * Объект для ввода из консоли
	 */
	private Scanner scanner = new Scanner(System.in);
	
	/**
	 * Вывод текста и ввод целого числа
	 * @param text
	 * @return число
	 */
	protected int getUserChoice(String[] text) {
		printLine(delimiter);
		printText(text);
		
		int choice = getInt();
		return choice;
	}
	
	/**
	 * Печать текста
	 * @param text
	 */
	protected void printText(String[] text) {
		for (String line : text) {
			printLine(line);
		}
	}
	
	/**
	 * Печать строки
	 * @param line
	 */
	protected void printLine(String line) {
		System.out.println(line);
	}

	/**
	 * Ввод строки
	 * @param message
	 * @return строка
	 */
	protected String getString(String message) {
		printLine(message);
		StringBuilder input = new StringBuilder();
		if (scanner.hasNextLine()) {
			input.append(scanner.nextLine());
		}
		return input.toString();
	}

	/**
	 * Ввод числа
	 * @return число
	 */
	protected int getInt() {
		int number = -1;
		if (scanner.hasNextLine()) {
			String input = scanner.nextLine();
			number = Integer.valueOf(input);
		}
		
		return number;
	}
	
	/**
	 * Преобразование коллекции Map в текст (String[])
	 * @param map
	 * @return текст
	 */
	protected String[] convertMapToStringArray(Map<Integer, ?> map) {
		String[] text = new String[map.size()];
	    for (Integer key : map.keySet()) {
	        text[key-1] = key.toString() + map.get(key);
	    }
	    return text;
	}
	
	/**
	 * Ввод логина и пароля
	 * @return логин и пароль
	 */
	protected String[] getCredentials() {
		String login = getString("Введите логин: ");
		String password = getString("Введите пароль: ");
		String[] credectianls = {login, password};
		return credectianls;
	}
	
	/**
	 * Получение значения enum из введённой цифры
	 * @param choice
	 * @return выбор стартового меню
	 */
	protected WelcomeMenuChoices getWelcomeEnumByNumber(int choice) {
		WelcomeMenuChoices result = null;
		for (WelcomeMenuChoices welcomeMenuChoice : WelcomeMenuChoices.values()) {
			if (welcomeMenuChoice.getChoice() == choice) {
				result = welcomeMenuChoice;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Получение значения enum из введённой цифры
	 * @param choice
	 * @return выбор залогиненного меню
	 */
	protected LoggedMenuChoices getLoggedEnumByNumber(int choice) {
		LoggedMenuChoices result = null;
		for (LoggedMenuChoices loggedMenuChoice : LoggedMenuChoices.values()) {
			if (loggedMenuChoice.getChoice() == choice) {
				result = loggedMenuChoice;
				break;
			}
		}
		return result;
	}

}
