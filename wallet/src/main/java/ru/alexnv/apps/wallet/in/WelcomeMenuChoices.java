package ru.alexnv.apps.wallet.in;

/**
 * Варианты в стартовом меню 
 */
public enum WelcomeMenuChoices implements MenuEnum {
	CHOICE_REGISTER(1),
	CHOICE_LOGIN(2),
	CHOICE_EXIT(3),
	CHOICE_NOT_EXIST(NOT_EXIST);

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
	@Override
	public int getChoice() {
		return choice;
	}
}
