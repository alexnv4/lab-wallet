/**
 * 
 */
package ru.alexnv.apps.wallet.infrastructure.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Player;

/**
 * Реализация интерфейса DAO игрока
 */
public class PlayerDaoImpl implements PlayerDao {

	/**
	 * Установленное соединение
	 */
	private Connection connection;

	/**
	 * @param connection
	 */
	public PlayerDaoImpl(Connection connection) {
		super();
		this.connection = connection;
	}

	/**
	 * Добавление игрока в БД
	 * 
	 * @param player объект игрока
	 * @return добавленный игрок, с установленным идентификатором из БД
	 * @throws DaoException ошибка работы с БД
	 */
	@Override
	public Player insert(Player player) throws DaoException {
		final String sql = "INSERT INTO wallet_schema.players(login, password, balance) VALUES(?, ?, ?);";

		try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, player.getLogin());
			statement.setString(2, player.getPassword());
			statement.setBigDecimal(3, player.getBalanceNumeric());
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected < 1) {
				throw new DaoException("Ошибка добавления игрока в базу данных.");
			}
			try (ResultSet resultSet = statement.getGeneratedKeys()) {
				if (!resultSet.next()) {
					throw new DaoException("Ошибка получения сгенерированного ID.");
				}

				long id = resultSet.getInt(1);
				player.setId(id);
				return player;
			}
		} catch (SQLException e) {
			throw new DaoException("Ошибка добавления игрока: ", e);
		}
	}

	@Override
	/**
	 * Обновление баланса игрока в БД
	 * 
	 * @param player объект игрока
	 * @return успешность операции добавления
	 * @throws DaoException ошибка работы с БД
	 */
	public boolean update(Player player) throws DaoException {
		final String sql = "UPDATE wallet_schema.players SET balance=? WHERE player_id=?;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setBigDecimal(1, player.getBalanceNumeric());
			statement.setLong(2, player.getId());
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				return true;
			}
			return false;

		} catch (SQLException e) {
			throw new DaoException("Ошибка обновления баланса игрока id: " + player.getId(), e);
		}
	}

	@Override
	public boolean delete(Player player) throws DaoException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	/**
	 * Поиск игрока по идентификатору в БД
	 * 
	 * @param PK идентификатор игрока
	 * @return найденный игрок
	 * @throws DaoException ошибка работы с БД
	 */
	public Player findById(long PK) throws DaoException {
		final String sql = "SELECT * FROM wallet_schema.players WHERE id = ?;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, PK);
			try (ResultSet resultSet = statement.executeQuery()) {
				resultSet.next();

				long id = resultSet.getLong("player_id");
				String login = resultSet.getString("login");
				String password = resultSet.getString("password");
				BigDecimal balance = resultSet.getBigDecimal("balance");

				Player player = new Player(id, login, password, balance);
				return player;
			}
		} catch (SQLException e) {
			throw new DaoException("Ошибка поиска игрока: ", e);
		}
	}

	@Override
	/**
	 * Получение списка игроков в БД
	 * 
	 * @return список игроков
	 * @throws DaoException ошибка работы с БД
	 */
	public List<Player> getAll() throws DaoException {
		final String sql = "SELECT * FROM wallet_schema.players ORDER BY login;";
		List<Player> players = new ArrayList<>();

		// try (PreparedStatement statement = connection.prepareStatement(sql);
		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				long id = resultSet.getLong("player_id");
				String login = resultSet.getString("login");
				String password = resultSet.getString("password");
				BigDecimal balance = resultSet.getBigDecimal("balance");

				Player player = new Player(id, login, password, balance);
				players.add(player);
			}

			return players;
		} catch (SQLException e) {
			throw new DaoException("Ошибка получения всех игроков: ", e);
		}
	}

}
