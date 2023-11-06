package ru.alexnv.apps.wallet.in.servlets;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alexnv.apps.wallet.aop.annotations.Loggable;
import ru.alexnv.apps.wallet.domain.dto.BalanceChangeDto;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.TransactionDto;
import ru.alexnv.apps.wallet.domain.dto.validators.DtoValidationException;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.in.Utility;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.DebitException;

/**
 * Сервлет обработки операций авторизованного игрока.
 */
@Loggable
public class OperationsServlet extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** Вспомогательный класс. */
	private Utility util;
	
	/** Вспомогательный класс для HTTP сервлетов. */
	private ServletsUtility servletsUtil;
	
    /**
     * Создание сервлета операций игрока.
     *
     * @see HttpServlet#HttpServlet()
     */
    public OperationsServlet() {
        super();
    	util = new Utility();
    	servletsUtil = new ServletsUtility();
    }

	/**
	 * Запрос баланса игрока.
	 *
	 * @param request авторизованный запрос
	 * @param response ответ
	 * @throws ServletException ошибка сервлета
	 * @throws IOException ошибка ввода-вывода
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String path = request.getPathInfo();
    	
    	if (path != null && path.endsWith("/balance")) {
    		PlayerDto playerDto = getPlayerDto(request);
    		servletsUtil.respondWithJson(response, SC_OK, playerDto);
    	} else if (path.endsWith("/transactions")) {
    		var playerService = (PlayerService) getServletContext().getAttribute("PlayerService");
    		List<TransactionDto> transactionsDto = playerService.getTransactionsHistory();
    		servletsUtil.respondWithJson(response, SC_OK, transactionsDto);
    	}
    }
    
    /**
     * Получение DTO игрока из запроса.
     *
     * @param request запрос
     * @return DTO игрока
     */
    private PlayerDto getPlayerDto(HttpServletRequest request) {
    	var playerService = (PlayerService) getServletContext().getAttribute("PlayerService");
    	PlayerDto playerDto = playerService.getBalance();
    	return playerDto;
    }

	/**
	 * Изменение баланса игрока.
	 *
	 * @param request авторизованный запрос
	 * @param response ответ
	 * @throws ServletException ошибка сервлета
	 * @throws IOException ошибка ввода-вывода
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BalanceChangeDto balanceChangeDto = null;
		int responseCode = SC_BAD_REQUEST;
		
		try {
			balanceChangeDto = servletsUtil.readValidDto(request, BalanceChangeDto.class);
		} catch (IOException e) {
			servletsUtil.respondWithError(response, responseCode, "Ошибка парсинга JSON.");
			return;
		} catch (DtoValidationException e) {
			servletsUtil.respondWithErrors(response, responseCode, e.getViolations());
			return;
		}
		
		// responseCode для операции тоже SC_BAD_REQUEST 
		// Выполнение операции для DTO
		var playerService = (PlayerService) getServletContext().getAttribute("PlayerService");
		var balanceChange = new BigDecimal(balanceChangeDto.getBalanceChange());
		Long transactionId = balanceChangeDto.getTransactionId();
		PlayerDto playerDto = null;
		try {
			// Определение типа операции
			if (balanceChange.compareTo(BigDecimal.ZERO) > 0) {
				playerDto = playerService.credit(balanceChange, transactionId);
			} else {
				playerDto = playerService.debit(balanceChange.negate(), transactionId);
			}
			
			util.logMessage("Выполнена операция изменения баланса.");
			responseCode = SC_OK;
		} catch (TransactionIdNotUniqueException e) {
			servletsUtil.respondWithError(response, responseCode, "Попытка добавления транзакции с неуникальным ID.");
			return;
		} catch (DebitException e) {
			e.printStackTrace();
			servletsUtil.respondWithError(response, responseCode, e.getMessage());
			return;
		}
		
		servletsUtil.respondWithJson(response, responseCode, playerDto);
	}
	
}
