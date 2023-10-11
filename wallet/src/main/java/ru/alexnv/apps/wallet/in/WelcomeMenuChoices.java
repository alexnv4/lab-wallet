package ru.alexnv.apps.wallet.in;

/**
 * Варианты в стартовом меню 
 */
public enum WelcomeMenuChoices {
	CHOICE_REGISTER(1),
	CHOICE_LOGIN(2),
	CHOICE_EXIT(3);
	
	/**
	 * Выбранный пункт меню
	 */
	private final int choice;

	WelcomeMenuChoices(int choice) {
		this.choice = choice;
	}
	
	/**
	 * @return выбор
	 */
	public int getChoice() {
		return choice;
	}
}

