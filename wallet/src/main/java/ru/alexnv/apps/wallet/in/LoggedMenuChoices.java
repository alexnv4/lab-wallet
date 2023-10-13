package ru.alexnv.apps.wallet.in;

/**
 * Варианты в меню залогиненного пользователя
 */
public enum LoggedMenuChoices {
	CHOICE_BALANCE(1),
	CHOICE_DEBIT(2),
	CHOICE_CREDIT(3),
	CHOICE_HISTORY(4),
	CHOICE_LOGOUT(5),
	CHOICE_NOT_EXIST(-1);
	
	/**
	 * Выбранный пункт меню
	 */
	private final int choice;
	
	LoggedMenuChoices(int choice) {
		this.choice = choice;
	}
	
	/**
	 * @return выбор
	 */
	public int getChoice() {
		return choice;
	}
	
}
