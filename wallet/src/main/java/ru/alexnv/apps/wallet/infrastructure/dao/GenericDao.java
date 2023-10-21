package ru.alexnv.apps.wallet.infrastructure.dao;

import java.util.List;

/**
 * Общее DAO
 * 
 * @param <T>
 * @param <PK>
 */
public interface GenericDao<T, PK> {

	/**
	 * Добавление в БД
	 * 
	 * @param object сущность
	 * @return сущность с установленным идентификатором
	 * @throws DaoException
	 */
	T insert(T object) throws DaoException;

	/**
	 * @param object
	 * @return результат выполнения обновления объекта
	 * @throws DaoException
	 */
	boolean update(T object) throws DaoException;

	/**
	 * @param object сущность
	 * @return результат удаления объекта
	 * @throws DaoException
	 */
	boolean delete(T object) throws DaoException;

	/**
	 * @param PK идентификатор 
	 * @return объект, соответствующий идентификатору
	 * @throws DaoException
	 */
	T findById(long PK) throws DaoException;

	/**
	 * @return список всех объектов
	 * @throws DaoException
	 */
	List<T> getAll() throws DaoException;
}
