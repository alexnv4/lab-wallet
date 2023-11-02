/**
 * 
 */
package ru.alexnv.apps.wallet.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * Класс DTO игрока.
 */
public class PlayerDto extends AbstractDto {
	
	/** Идентификатор пользователя, устанавливается после добавления в БД. */
	@Null(message = "Идентификатор обязан быть не определённым")
	private Long id;

	/** Логин пользователя. */
	@NotEmpty(message = "Логин не должен быть пустым или не определённым")
	@Size(min = 3, max = 20, message = "Логин должен быть не менее 3 символов и не более 20 символов.")
	private String login;

	/** Пароль пользователя. */
	@NotEmpty(message = "Пароль не должен быть пустым или не определённым")
	@Size(min = 4, max = 32, message = "Пароль должен быть не менее 4 символов и не более 32 символов.")
	private String password;

	/**
	 * Баланс в формате 0.00
	 */
	private String balance;
	
	/**
	 * Создание DTO игрока.
	 */
	public PlayerDto() {
		
	}

	/**
	 * Создание DTO игрока.
	 *
	 * @param login логин
	 * @param password пароль
	 * @param balance баланс
	 */
	public PlayerDto(String login, String password, String balance) {
		this.login = login;
		this.password = password;
		this.balance = balance;
		this.id = null;
	}

	/**
	 * Gets the login.
	 *
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the balance.
	 *
	 * @return the balance
	 */
	public String getBalance() {
		return balance;
	}

	/**
	 * Sets the login.
	 *
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the balance.
	 *
	 * @param balance the balance to set
	 */
	public void setBalance(String balance) {
		this.balance = balance;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
