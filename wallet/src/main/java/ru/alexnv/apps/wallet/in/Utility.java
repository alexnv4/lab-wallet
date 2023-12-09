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
	private static final String DELIMITER = "----------------------------";

	/**
	 * Входной поток данных
	 */
	private static final InputStream INPUT_STREAM = System.in;

	/**
	 * Выходной поток данных
	 */
	private static final PrintStream OUTPUT_STREAM = System.out;
	/**
	 * Объект для ввода из консоли
	 */
	private Scanner scanner = new Scanner(INPUT_STREAM);

	/**
	 * Вывод текста и ввод целого числа
	 * 
	 * @param text
	 * @return число
	 * @throws NotNumberException введено не число
	 */
	protected int getUserChoice(String[] text) throws NotNumberException {
		printLine(DELIMITER);
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
		OUTPUT_STREAM.println(line);
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
	protected String[] convertMapToStringArray(Map<Integer, String> map) {
		String[] text = new String[map.size()];
		int i = 0;
		
		for (Map.Entry<Integer, String> entry : map.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();
			text[i] = key.toString() + value;
			i++;
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
	 * @param <T>       значение enum, реализующее интерфейс MenuEnum
	 * @param enumClass класс, реализующий интерфейс MenuEnum
	 * @param choice    выбранное значение enum в виде числа
	 * @return выбор залогиненного меню
	 * @throws IncorrectMenuChoiceException если такого пункта нет в меню
	 */
	protected <T extends Enum<T> & MenuEnum> T getEnumByNumber(Class<T> enumClass, int choice)
			throws IncorrectMenuChoiceException {
		if (choice == MenuEnum.NOT_EXIST) {
			throw new IncorrectMenuChoiceException("Такого пункта в меню нет.");
		}

		T[] enumValues = enumClass.getEnumConstants();
		T result = null;

		for (T enumValue : enumValues) {
			if (enumValue.getChoice() == choice) {
				result = enumValue;
				break;
			}
		}

		if (result == null) {
			throw new IncorrectMenuChoiceException("Такого пункта в меню нет.");
		}

		return result;
	}

}
