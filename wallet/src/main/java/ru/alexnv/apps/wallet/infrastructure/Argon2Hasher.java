/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Argon2id реализация интерфейса хэширования пароля.
 */
public class Argon2Hasher implements PasswordHasher {

	/** Количество байт соли. */
	private static final int SALT_LENGTH = 16;
	
	/** Количество байт хэша. */
	private static final int HASH_LENGTH = 32;
	
	/** Количество используемой памяти в Кб. */
	private static final int MEMORY = 65536;
	
	/** Количество итераций. */
	private static final int ITERATIONS = 3;
	
	/** Степень параллелизма. */
	private static final int PARALLELISM = 1;
	
	/** Вариант Argon 2 алгоритма. */
	private final Argon2 argon2;
	
	/**
	 * Создание Argon2id варианта.
	 */
	public Argon2Hasher() {
		argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, SALT_LENGTH, HASH_LENGTH);
	}

	/**
	 * Хэширование пароля.
	 *
	 * @param password пароль plain text
	 * @return хэш char[]
	 */
	@Override
	public char[] hashPassword(char[] password) {
		
		try {
			// пример результата
			// $argon2id$v=19$m=65536,t=3,p=1$Ja7VRQAVw8/Z9en3KDoEHA$inXlq6FPIey6jEMaknpepOtaQVinOAD6r/RVm9IczdQ
			return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, password).toCharArray();

		} finally {
			argon2.wipeArray(password);
		}
	}
	
	/**
	 * Верификация пароля.
	 *
	 * @param hash хэш пароля
	 * @param password пароль plain text
	 * @return true если хэш пароля верный
	 */
	public boolean verify(char[] hash, char[] password) {
		try {
			return argon2.verify(String.valueOf(hash), password);
		} finally {
			argon2.wipeArray(password);
			argon2.wipeArray(hash);
		}
	}
}
