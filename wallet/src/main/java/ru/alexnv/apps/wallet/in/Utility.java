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
	private static final PrintStream OUTPUT_STREAM = System.out;
	
	/**
	 * Выходной поток ошибок
	 */
	private static final PrintStream ERROR_STREAM = System.err;

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
	 * Логирование сообщения
	 * 
	 * @param message
	 */
	public void logMessage(String message) {
		ERROR_STREAM.println(message);
	}

}
