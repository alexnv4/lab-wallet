/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure;

/**
 * Интерфейс для хэширования паролей 
 */
public interface PasswordHasher {
	
	/**
	 * Хэширование пароля.
	 *
	 * @param password пароль в виде plain text
	 * @return хэшированный пароль
	 */
	char[] hashPassword(char[] password);
	
	/**
	 * Верификация пароля.
	 *
	 * @param hash хэш пароля
	 * @param password пароль plain text
	 * @return true если хэш пароля верный
	 */
	public boolean verify(char[] hash, char[] password);
}
