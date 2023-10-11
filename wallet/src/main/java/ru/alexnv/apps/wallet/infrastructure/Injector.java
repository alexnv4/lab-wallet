package ru.alexnv.apps.wallet.infrastructure;

import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.model.Player;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.in.Entry;
import ru.alexnv.apps.wallet.service.PlayerService;

public class Injector {
	
	/**
	 * Создание и внедрение зависимостей на всех слоях приложения
	 * Список пользователей хранится в памяти (ArrayList)
	 */
	public Injector() {
		
		final List<Player> players = new ArrayList<>();
		final AuthorizationService authorizationService = new AuthorizationService(players);
		final RegistrationService registrationService = new RegistrationService(players);
		final PlayerService playerService = new PlayerService(authorizationService, registrationService);
		new Entry(playerService);
	}

}
