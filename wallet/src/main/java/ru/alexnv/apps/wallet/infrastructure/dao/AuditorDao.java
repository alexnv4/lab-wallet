/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

import ru.alexnv.apps.wallet.service.Action;

/**
 * DAO аудита, устанавливает параметры Action, Integer общего DAO
 */
public interface AuditorDao extends GenericDao<Action, Integer> {

}
