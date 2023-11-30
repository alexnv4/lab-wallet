package ru.alexnv.apps.wallet.in;

/**
 * Базовый интерфейс меню для перечислений.
 */
public interface MenuEnum {
	
	/** Пункт меню не существует. */
	public static final int NOT_EXIST = -1;
	
	/**
	 * Получение числового значения пункта меню.
	 *
	 * @return the choice числовое значение
	 */
	int getChoice();
}
