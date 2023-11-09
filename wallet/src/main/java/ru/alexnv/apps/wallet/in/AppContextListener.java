/**
 * @author alexnv
 */
package ru.alexnv.apps.wallet.in;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ru.alexnv.apps.wallet.domain.service.AuthorizationService;
import ru.alexnv.apps.wallet.domain.service.PlayerOperationsService;
import ru.alexnv.apps.wallet.domain.service.RegistrationService;
import ru.alexnv.apps.wallet.infrastructure.DaoFactory;
import ru.alexnv.apps.wallet.infrastructure.LiquibaseMigrations;
import ru.alexnv.apps.wallet.infrastructure.PostgreSqlDaoFactory;
import ru.alexnv.apps.wallet.service.PlayerService;

/**
 * Инициализация приложения
 */
public class AppContextListener implements ServletContextListener {

	/**
	 * Создание контекста сервлета
	 */
	public AppContextListener() {
		
	}

	/**
	 * Инициализация контекста сервлета. Создаётся соединение с БД.
	 * Выполняется внедрение всех зависимостей. Выполняются миграции.
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		DaoFactory daoFactory = new PostgreSqlDaoFactory(true);
		Connection connection = null;
		try {
			connection = daoFactory.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sce.getServletContext().setAttribute("Connection", connection);
		System.out.println("Соединение с базой данных установлено.");
		
		inject(sce, daoFactory, connection);

		createMigrations();
	}

	/**
	 * Внедрение зависимостей для всех сервисов.
	 * 
	 * @param sce контекст сервлета
	 * @param daoFactory фабрика DAO
	 * @param connection установленное соединение с БД
	 */
	private void inject(ServletContextEvent sce, DaoFactory daoFactory, Connection connection) {
		final AuthorizationService authorizationService = new AuthorizationService(daoFactory.getPlayerDao(connection));
		final RegistrationService registrationService = new RegistrationService(daoFactory.getPlayerDao(connection));
		final PlayerOperationsService playerOperationsService = new PlayerOperationsService(
				daoFactory.getPlayerDao(connection), daoFactory.getTransactionDao(connection));
		final PlayerService playerService = new PlayerService(authorizationService, registrationService,
				playerOperationsService, daoFactory.getAuditorDao(connection));
		sce.getServletContext().setAttribute("AuthorizationService", authorizationService);
		sce.getServletContext().setAttribute("RegistrationService", registrationService);
		sce.getServletContext().setAttribute("PlayerOperationsService", playerOperationsService);
		sce.getServletContext().setAttribute("PlayerService", playerService);
	}

	/**
	 * Запуск миграций
	 */
	private void createMigrations() {
		LiquibaseMigrations lbm = new LiquibaseMigrations();
		lbm.migrate();
	}

	/**
	 * Завершение контекста приложения. Закрывается соединение с БД.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		
		Connection connection = (Connection) ctx.getAttribute("Connection");
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Соединение закрыто");
	}
	

}
