/**
 * 
 */
package ru.alexnv.apps.wallet.dao;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
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
	
	/** Длина хэшированного пароля в БД. */
	private static final int PASSWORD_LENGTH = 128;

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
			
			Reader reader = new CharArrayReader(player.getPassword());
			statement.setCharacterStream(2, reader);
			
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
	 * @throws NotFoundException игрок не найден в БД
	 * @throws DaoException ошибка работы с БД
	 */
	public Player findById(long PK) throws NotFoundException, DaoException {
		final String sql = "SELECT player_id, login, balance FROM wallet_schema.players WHERE player_id=?;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, PK);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new NotFoundException("Не найдено.");
				}

				long id = resultSet.getLong("player_id");
				String login = resultSet.getString("login");
				
				BigDecimal balance = resultSet.getBigDecimal("balance");

				Player player = new Player(id, login, new char[] { '0' }, balance);
				
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
		final String sql = "SELECT player_id, login, balance FROM wallet_schema.players ORDER BY login;";
		List<Player> players = new ArrayList<>();

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

			while (resultSet.next()) {
				long id = resultSet.getLong("player_id");
				String login = resultSet.getString("login");

				BigDecimal balance = resultSet.getBigDecimal("balance");

				Player player = new Player(id, login, new char[] { '0' }, balance);
				
				players.add(player);
			}

			return players;
		} catch (SQLException e) {
			throw new DaoException("Ошибка получения всех игроков: ", e);
		}
	}
	
	@Override
	/**
	 * Поиск игрока по идентификатору в БД.
	 *
	 * @param login логин
	 * @return найденный игрок
	 * @throws NotFoundException игрок не найден в БД
	 * @throws DaoException ошибка работы с БД
	 */
	public Player findByLogin(String login) throws NotFoundException, DaoException {
		final String sql = "SELECT player_id, login, password, balance FROM wallet_schema.players WHERE login=?;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, login);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new NotFoundException("Не найдено.");
				}

				Long id = resultSet.getLong("player_id");
				
				Reader reader = resultSet.getCharacterStream("password");
				char[] passwordHash = readPassword(reader);
				
				BigDecimal balance = resultSet.getBigDecimal("balance");

				Player player = new Player(id, login, passwordHash, balance);
				
				return player;
			} catch (IOException e) {
				e.printStackTrace();
				throw new DaoException("Ошибка чтения хэша пароля. ", e);
			}
		} catch (SQLException e) {
			throw new DaoException("Ошибка поиска игрока: ", e);
		}
	}
	
	/**
	 * Получение пароля в виде массива символов.
	 *
	 * @param reader the reader
	 * @return пароль char[]
	 * @throws IOException ошибка ввода-вывода
	 */
	private char[] readPassword(Reader reader) throws IOException {
        CharArrayWriter charWriter = new CharArrayWriter();
        char[] buffer = new char[PASSWORD_LENGTH];
        int bytesRead;
        while ((bytesRead = reader.read(buffer)) != -1) {
            charWriter.write(buffer, 0, bytesRead);
        }
        return charWriter.toCharArray();
	}

}
