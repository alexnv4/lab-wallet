/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Transaction;

/**
 * DAO транзакции, устанавливает параметры Transaction, Integer общего DAO
 */
public interface TransactionDao extends GenericDao<Transaction, Integer> {

	List<Transaction> getAllWithPlayerId(long playerId) throws DaoException;
}
