/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

import ru.alexnv.apps.wallet.domain.model.Player;

/**
 * DAO игрока, устанавливает параметры Player, Integer общего DAO
 */
public interface PlayerDao extends GenericDao<Player, Integer> {

	/**
	 * Поиск игрока в БД по логину.
	 *
	 * @param login логин
	 * @return найденный игрок
	 * @throws NotFoundException игрок не найден в БД
	 * @throws DaoException ошибка работы с БД
	 */
	Player findByLogin(String login) throws NotFoundException, DaoException;

}
