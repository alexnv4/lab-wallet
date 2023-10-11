package ru.alexnv.apps.wallet.domain.service;

import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.exceptions.PlayerAlreadyExistsException;

/**
 * 
 */
public class RegistrationService {
	
	/**
	 * Список игроков, устанавливается внешим слоем приложения
	 */
	private final List<Player> players;

	/**
	 * @param players
	 */
	public RegistrationService(List<Player> players) {
		this.players = players;
	}
	
	/**
	 * Регистрация пользователя
	 * При совпадении фактического параметра login с полем login любого игрока из списка - исключение PlayerAlreadyExistsException
	 * При успешной регистрации добавляет игрока в список
	 * @param login
	 * @param password
	 * @return игрок
	 * @throws PlayerAlreadyExistsException - такой игрок уже существует
	 */
	public Player register(String login, String password) throws PlayerAlreadyExistsException {
		for (Player player : players) {
			if (login.equals(player.getLogin()))
				throw new PlayerAlreadyExistsException("Такой игрок уже существует.");
		}

		Player player = new Player(login, password);
		players.add(player);
		return player;
	}

}
