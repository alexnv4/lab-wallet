package ru.alexnv.apps.wallet.infrastructure.dao;

import java.util.List;

/**
 * Общее DAO
 * @param <T>
 * @param <PK>
 */
public interface GenericDao<T, PK> {
	
	/**
	 * Добавление в БД
	 * @param object сущность
	 * @return сущность с установленным идентификатором
	 * @throws DaoException
	 */
	T insert(T object) throws DaoException;
	boolean update(T object) throws DaoException;
	boolean delete(T object) throws DaoException;
	T findById(long PK) throws DaoException;
	List<T> getAll() throws DaoException;
}
