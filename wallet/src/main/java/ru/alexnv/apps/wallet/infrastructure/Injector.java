package ru.alexnv.apps.wallet.infrastructure;

import java.sql.Connection;
import java.sql.SQLException;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.in.Entry;
import ru.alexnv.apps.wallet.service.PlayerService;

public class Injector {
	
	/**
	 * Создание и внедрение зависимостей на всех слоях приложения
	 * Список пользователей хранится в БД
	 */
	public Injector() {
		
		DaoFactory daoFactory = new PostgreSqlDaoFactory(true);
		Connection connection = null;
		try {
			connection = daoFactory.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} 

		final AuthorizationService authorizationService = new AuthorizationService(daoFactory.getPlayerDao(connection),
				daoFactory.getTransactionDao(connection));
		final RegistrationService registrationService = new RegistrationService(daoFactory.getPlayerDao(connection));
		final PlayerService playerService = new PlayerService(authorizationService, registrationService, 
				daoFactory.getAuditorDao(connection));
		
		// Запуск миграций
		LiquibaseMigrations lbm = new LiquibaseMigrations();
		lbm.migrate();
		
		new Entry(playerService);
		
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
