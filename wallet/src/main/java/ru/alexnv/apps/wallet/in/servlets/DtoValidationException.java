/**
 * 
 */
package ru.alexnv.apps.wallet.in.servlets;

import java.util.List;

/**
 * Исключение - ошибка валидации DTO.
 */
public class DtoValidationException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8079654819257993595L;
	
	/** Список нарушений. */
	private List<String> violations;

	/**
	 * Создание исключения со списком нарушений.
	 *
	 * @param violations список нарушений
	 */
	public DtoValidationException(List<String> violations) {
		this.violations = violations;
	}

	/**
	 * Получить список нарушений.
	 *
	 * @return список нарушений
	 */
	public List<String> getViolations() {
		return violations;
	}

}
