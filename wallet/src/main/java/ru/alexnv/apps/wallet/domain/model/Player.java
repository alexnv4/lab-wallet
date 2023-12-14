package ru.alexnv.apps.wallet.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.mappers.Default;

/**
 * Модель предметной области - игрок
 */
public class Player implements Cloneable {

	/**
	 * Идентификатор пользователя
	 */
	private Long id;

	/**
	 * Логин пользователя
	 */
	private String login;

	/**
	 * Пароль пользователя
	 */
	private char[] password;

	/**
	 * Баланс в формате 0.00
	 */
	private BigDecimal balance;

	/**
	 * Список завершённых транзакций
	 */
	private List<Transaction> transactions;

	/**
	 * @return login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return password
	 */
	public char[] getPassword() {
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(char[] password) {
		this.password = password;
	}

	/**
	 * @return баланс в формате строки
	 */
	public String getBalance() {
		return balance.toString();
	}

	/**
	 * @return баланс с типом BigDecimal
	 */
	public BigDecimal getBalanceNumeric() {
		return balance;
	}

	/**
	 * @param balance
	 */
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/**
	 * @param login
	 * @param password
	 */
	public Player(String login, char[] password) {
		this.login = login;
		this.password = password;
		this.id = null;
		this.balance = new BigDecimal("0.00");
		this.transactions = new ArrayList<>();
	}

	/**
	 * @param id 
	 * @param login
	 * @param password
	 * @param balance
	 */	
	@Default
	public Player(Long id, String login, char[] password, BigDecimal balance) {
		this(login, password);
		this.id = id;
		this.balance = balance;
	}

	/**
	 * @param id 
	 * @param login
	 * @param password
	 * @param balance
	 * @param transactions
	 */
	public Player(Long id, String login, char[] password, BigDecimal balance, List<Transaction> transactions) {
		this(id, login, password, balance);
		this.transactions = transactions;
	}

	/**
	 * @return список завершённых транзакций
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * Получение последней транзакции игрока
	 * 
	 * @return последняя выполненная транзакция
	 */
	public Transaction getLastTransaction() {
		return (transactions.get(transactions.size() - 1));
	}
	
	/**
	 * Добавление транзакции для игрока
	 * 
	 * @param transaction транзакция
	 */
	public void addTransaction(Transaction transaction) {
		transactions.add(transaction);
	}

	/**
	 * @return ИД игрока
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Player clone() throws CloneNotSupportedException {
		return (Player) super.clone();
	}

}
