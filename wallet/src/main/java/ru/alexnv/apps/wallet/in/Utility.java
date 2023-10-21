package ru.alexnv.apps.wallet.in;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

import ru.alexnv.apps.wallet.in.exceptions.IncorrectMenuChoiceException;
import ru.alexnv.apps.wallet.in.exceptions.NotNumberException;

/**
 * Вспомогательный класс Консольный ввод и вывод указаны только здесь в
 * единственном экземпляре
 */
public class Utility {

	/**
	 * Разделитель для печати меню
	 */
	private static final String delimiter = "----------------------------";

	/**
	 * Входной поток данных
	 */
	private final InputStream inputStream = System.in;

	/**
	 * Выходной поток данных
	 */
	private final PrintStream outputStream = System.out;
	/**
	 * Объект для ввода из консоли
	 */
	private Scanner scanner = new Scanner(inputStream);

	/**
	 * Вывод текста и ввод целого числа
	 * 
	 * @param text
	 * @return число
	 * @throws NotNumberException введено не число
	 */
	protected int getUserChoice(String[] text) throws NotNumberException {
		printLine(delimiter);
		printText(text);

		int choice = getInt();
		return choice;
	}

	/**
	 * Печать текста
	 * 
	 * @param text
	 */
	protected void printText(String[] text) {
		for (String line : text) {
			printLine(line);
		}
	}

	/**
	 * Печать строки
	 * 
	 * @param line
	 */
	public void printLine(String line) {
		outputStream.println(line);
	}

	/**
	 * Ввод строки
	 * 
	 * @param message
	 * @return строка без пробелов в начале и в конце
	 */
	protected String getString(String message) {
		printLine(message);
		StringBuilder input = new StringBuilder();
		if (scanner.hasNextLine()) {
			input.append(scanner.nextLine());
		}
		return input.toString().trim();
	}

	/**
	 * Ввод числа
	 * 
	 * @return число
	 * @throws NotNumberException если введено не число
	 */
	protected int getInt() throws NotNumberException {
		int number = -1;
		if (scanner.hasNextLine()) {
			String input = scanner.nextLine();
			try {
				number = Integer.valueOf(input);
			} catch (NumberFormatException e) {
				throw new NotNumberException("Ошибка. Введено не число.");
			}
		}

		return number;
	}

	/**
	 * Преобразование коллекции Map в текст (String[])
	 * 
	 * @param map
	 * @return текст
	 */
	protected String[] convertMapToStringArray(Map<Integer, ?> map) {
		String[] text = new String[map.size()];
		for (Integer key : map.keySet()) {
			text[key - 1] = key.toString() + map.get(key);
		}
		return text;
	}

	/**
	 * Ввод логина и пароля
	 * 
	 * @return логин и пароль
	 */
	protected String[] getCredentials() {
		String login = getString("Введите логин: ");
		String password = getString("Введите пароль: ");
		String[] credectianls = { login, password };
		return credectianls;
	}

	/**
	 * Получение значения enum из введённой цифры
	 * 
	 * @param choice
	 * @return выбор стартового меню
	 * @throws IncorrectMenuChoiceException если такого пункта нет в меню
	 */
	protected WelcomeMenuChoices getWelcomeEnumByNumber(int choice) throws IncorrectMenuChoiceException {
		WelcomeMenuChoices result = null;
		for (WelcomeMenuChoices welcomeMenuChoice : WelcomeMenuChoices.values()) {
			if (welcomeMenuChoice.getChoice() == choice) {
				result = welcomeMenuChoice;
				break;
			}
		}

		if (result == null) {
			throw new IncorrectMenuChoiceException("Такого пункта в меню нет.");
		}

		return result;
	}

	/**
	 * Получение значения enum из введённой цифры
	 * 
	 * @param choice
	 * @return выбор залогиненного меню
	 * @throws IncorrectMenuChoiceException если такого пункта нет в меню
	 */
	protected LoggedMenuChoices getLoggedEnumByNumber(int choice) throws IncorrectMenuChoiceException {
		LoggedMenuChoices result = null;
		for (LoggedMenuChoices loggedMenuChoice : LoggedMenuChoices.values()) {
			if (loggedMenuChoice.getChoice() == choice) {
				result = loggedMenuChoice;
				break;
			}
		}

		if (result == null) {
			throw new IncorrectMenuChoiceException("Такого пункта в меню нет.");
		}
		return result;
	}

}
