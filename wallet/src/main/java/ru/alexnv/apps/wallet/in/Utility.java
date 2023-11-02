package ru.alexnv.apps.wallet.in;

import java.io.PrintStream;

/**
 * Вспомогательный класс Консольный ввод и вывод указаны только здесь в
 * единственном экземпляре
 */
public class Utility {

	/**
	 * Выходной поток данных
	 */
	private final PrintStream outputStream = System.out;
	
	/**
	 * Выходной поток ошибок
	 */
	private final PrintStream errorStream = System.err;

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
	 * Логирование сообщения
	 * 
	 * @param message
	 */
	public void logMessage(String message) {
		errorStream.println(message);
	}

}
