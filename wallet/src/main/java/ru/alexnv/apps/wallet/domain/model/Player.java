package ru.alexnv.apps.wallet.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.service.exceptions.NoMoneyLeftException;

public class Player {
	
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
		this.balance = new BigDecimal("0.00"); 
		this.transactions = new ArrayList<>();
	}
	
	/**
	 * Дебетовая операция
	 * Будет успешной только в том случае, если на счету достаточно средств (баланс - сумма дебета >= 0)
	 * @param amount
	 * @throws NoMoneyLeftException
	 */
	public void debit(BigDecimal amount) throws NoMoneyLeftException {
		if (balance.compareTo(amount) >= 0) {
			BigDecimal newBalance = balance.subtract(amount);
			registerTransaction(balance, newBalance, "дебет");
			this.balance = newBalance;
		}
		else
			throw new NoMoneyLeftException("Недостаточно средств для снятия.");
	}
	
	/**
	 * Кредит на игрока
	 * @param amount
	 */
	public void credit(BigDecimal amount) {
		BigDecimal newBalance = balance.add(amount);
		registerTransaction(balance, newBalance, "кредит");
		this.balance = newBalance;
	}
	
	/**
	 * @return список завершённых транзакций
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	/**
	 * @param balanceBefore
	 * @param balanceAfter
	 * @param description
	 * @return транзакция
	 */
	private Transaction registerTransaction(BigDecimal balanceBefore, BigDecimal balanceAfter, String description) {
		Transaction transaction = new Transaction(this);
		transaction.setBalanceBefore(balanceBefore);
		transaction.setBalanceAfter(balanceAfter);
		transaction.setDescription(description);
		transactions.add(transaction);
		return transaction;
	}

}
