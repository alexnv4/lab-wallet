package ru.alexnv.apps.wallet.in.servlets;

import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import java.math.BigDecimal;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.aop.annotations.Loggable;
import ru.alexnv.apps.wallet.domain.dto.*;
import ru.alexnv.apps.wallet.domain.dto.validators.BalanceChangeDtoValidator;
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
	
	/** The Constant AUTHORIZATION_NAME. */
	public static final String AUTHORIZATION_NAME = "Authorization";
	
	/** The Constant BEARER_NAME. */
	public static final String BEARER_NAME = "Bearer ";
	
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
    	PlayerDto playerDto = getPlayerDto(request);
    	servletsUtil.respondWithJson(response, SC_OK, playerDto);
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
			balanceChangeDto = servletsUtil.readValidDto(request, BalanceChangeDto.class, new BalanceChangeDtoValidator());
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
