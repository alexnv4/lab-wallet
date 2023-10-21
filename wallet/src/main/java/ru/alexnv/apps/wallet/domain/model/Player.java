package ru.alexnv.apps.wallet.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;

/**
 * Модель предметной области - игрок
 */
public class Player {

	/**
	 * Идентификатор пользователя
	 */
	private long id;

	/**
	 * Логин пользователя
	 */
	private String login;

	/**
	 * Пароль пользователя
	 */
	private String password;

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
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password) {
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
	public Player(String login, String password) {
		this.login = login;
		this.password = password;
		this.id = -1;
		this.balance = new BigDecimal("0.00");
		this.transactions = new ArrayList<>();
	}

	/**
	 * @param login
	 * @param password
	 * @param balance
	 */
	public Player(long id, String login, String password, BigDecimal balance) {
		this(login, password);
		this.id = id;
		this.balance = balance;
	}

	/**
	 * @param login
	 * @param password
	 * @param balance
	 * @param transactions
	 */
	public Player(long id, String login, String password, BigDecimal balance, List<Transaction> transactions) {
		this(id, login, password, balance);
		this.transactions = transactions;
	}

	/**
	 * Дебетовая операция Будет успешной только в том случае, если на счету
	 * достаточно средств (баланс - сумма дебета >= 0)
	 * 
	 * @param amount
	 * @throws NoMoneyLeftException
	 */
	public void debit(BigDecimal amount) throws NoMoneyLeftException {
		if (balance.compareTo(amount) < 0) {
			throw new NoMoneyLeftException("Недостаточно средств для снятия.");
		}

		BigDecimal newBalance = balance.subtract(amount);
		registerTransaction(balance, newBalance);
		this.balance = newBalance;
	}

	/**
	 * Кредит на игрока
	 * 
	 * @param amount
	 */
	public void credit(BigDecimal amount) {
		BigDecimal newBalance = balance.add(amount);
		registerTransaction(balance, newBalance);
		this.balance = newBalance;
	}

	/**
	 * @return список завершённых транзакций
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public Transaction getLastTransaction() {
		return (transactions.get(transactions.size() - 1));
	}

	/**
	 * @param balanceBefore
	 * @param balanceAfter
	 * @param description
	 * @return транзакция
	 */
	private Transaction registerTransaction(BigDecimal balanceBefore, BigDecimal balanceAfter) {
		Transaction transaction = new Transaction(this, balanceBefore, balanceAfter);
		transactions.add(transaction);
		return transaction;
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
	public void setId(long id) {
		this.id = id;
	}

}
