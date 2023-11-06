package ru.alexnv.apps.wallet.in.servlets;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alexnv.apps.wallet.aop.annotations.Loggable;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.dto.validators.DtoValidationException;
import ru.alexnv.apps.wallet.in.JSONWebToken;
import ru.alexnv.apps.wallet.in.Utility;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;

/**
 * Сервлет авторизации игрока.
 */
@Loggable
public class AuthorizationServlet extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2L;
	
	/** Вспомогательный класс. */
	private Utility util;
	
	/** Вспомогательный класс для HTTP сервлетов. */
	private ServletsUtility servletsUtil;
	
	/**
	 * Создание сервлета авторизации игрока.
     *
     * @see HttpServlet#HttpServlet()
	 */
	public AuthorizationServlet() {
    	util = new Utility();
    	servletsUtil = new ServletsUtility();
	}

	/**
	 * Авторизация игрока. Проверяется валидность входящего DTO в виде JSON.
	 * При успешной авторизации генерируется JWT для последующих запросов игрока.
	 * В ответном JSON содержится ID игрока для последующих запросов ресурсов.
	 *
	 * @param request запрос
	 * @param response ответ
	 * @throws ServletException ошибка сервлета
	 * @throws IOException ошибка ввода-вывода
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PlayerDto playerDto = null;
		int responseCode = SC_BAD_REQUEST;
		
		try {
			playerDto = servletsUtil.readValidDto(request, PlayerDto.class);
		} catch (IOException e) {
			servletsUtil.respondWithError(response, responseCode, "Ошибка парсинга JSON.");
			return;
		} catch (DtoValidationException e) {
			servletsUtil.respondWithErrors(response, responseCode, e.getViolations());
			return;
		}
		
		responseCode = SC_UNAUTHORIZED;
		// Выполнение операции для DTO
		var playerService = (PlayerService) getServletContext().getAttribute("PlayerService");
		try {
			playerDto = playerService.authorize(playerDto.getLogin(), playerDto.getPassword());
			String registeredLogin = playerDto.getLogin();
			util.logMessage(("Пользователь " + registeredLogin + " зашёл в кошелёк."));
			
			// Создание JWT для последующих запросов
			JSONWebToken jwt = createJWT(playerDto.getId());
			String token = jwt.generate();
			response.setHeader("Authorization", token);
			util.logMessage("Токен сгенерирован для игрока " + registeredLogin);
			
			responseCode = SC_OK;
		} catch (AuthorizationException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		// Отправка итогового DTO клиенту в JSON
		servletsUtil.respondWithJson(response, responseCode, playerDto);
	}

	/**
	 * Создание JWT объекта
	 * @param playerId 
	 * 
	 * @return JSONWebToken объект
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public JSONWebToken createJWT(Long playerId) throws JsonMappingException, JsonProcessingException {
		return new JSONWebToken(playerId);
	}

}
