package ru.alexnv.apps.wallet.service;

import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.dao.AuditorDao;
import ru.alexnv.apps.wallet.dao.DaoException;

/**
 * Аудит всех действий игрока
 */
public class Auditor {

	/**
	 * Список действий
	 */
	private List<Action> actions;

	/**
	 * DAO аудита, устанавливается извне
	 */
	private AuditorDao auditorDao;

	/**
	 * Создание аудита действий игрока
	 * 
	 * @param auditorDao DAO аудита
	 */
	public Auditor(AuditorDao auditorDao) {
		actions = new ArrayList<>();
		this.auditorDao = auditorDao;
	}

	/**
	 * Добавление действия в аудит
	 * 
	 * @param action
	 */
	public void addAction(Action action) {
		actions.add(action);

		// запись действия в базу
		try {
			auditorDao.insert(action);
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return список действий
	 */
	public List<Action> getActions() {
		return actions;
	}

}
