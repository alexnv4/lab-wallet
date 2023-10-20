/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import ru.alexnv.apps.wallet.service.Action;

/**
 * Реализация интерфейса DAO аудита
 */
public class AuditorDaoImpl implements AuditorDao {
	
	/**
	 * Установленное соединение с БД
	 */
	private Connection connection;

	/**
	 * @param connection
	 */
	public AuditorDaoImpl(Connection connection) {
		super();
		this.connection = connection;
	}

	@Override
	/**
	 * Добавление действия аудита в БД
	 * @param action объект действия
	 * @return добавленное действие
	 * @throws DaoException ошибка работы с БД
	 */
	public Action insert(Action action) throws DaoException {
		
		final String sql = "INSERT INTO wallet_schema.audit(description, date, player_id) VALUES(?, ?, ?);";
		
		try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, action.getDescription());
			statement.setObject(2, action.getDateTime());
			if (action.getPlayer() != null) {
				statement.setLong(3, action.getPlayer().getId());
			} else {
				statement.setNull(3, java.sql.Types.NULL);
			}
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected < 1) {
				throw new DaoException("Ошибка добавления действия в базу данных.");
			}
			try (ResultSet resultSet = statement.getGeneratedKeys()) {
				if (resultSet.next()) {
					//long id = resultSet.getInt(1);
					//action.setId(id);
					return action;
				} else {
					throw new DaoException("Ошибка получения сгенерированного ID.");
				}
			}
		}
		catch (SQLException e) {
			throw new DaoException("Ошибка добавления аудита: ", e);
		}
	}

	@Override
	public boolean update(Action object) throws DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Action object) throws DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Action findById(long PK) throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Action> getAll() throws DaoException {
		// TODO Auto-generated method stub
		return null;
	}

}
