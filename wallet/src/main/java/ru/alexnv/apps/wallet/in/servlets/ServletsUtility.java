/**
 * 
 */
package ru.alexnv.apps.wallet.in.servlets;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.*;
import jakarta.ws.rs.core.MediaType;
import ru.alexnv.apps.wallet.domain.dto.*;
import ru.alexnv.apps.wallet.domain.dto.validators.*;
import ru.alexnv.apps.wallet.in.Utility;

/**
 * 
 */
public class ServletsUtility {
	
	/** Шаблон ошибки JSON. */
	public static final String MESSAGE_JSON = """
			{
			  "message": %s
			}
			""";
	
	/**
	 * Вспомогательный класс utility
	 */
	private Utility util = new Utility();

	/**
	 * Ответ с ошибкой.
	 * 
	 * @param response ответ
	 * @param statusCode статус код ответа
	 * @param reason причина ошибки
	 * @throws IOException ошибка ввода-вывода
	 */
	public void respondWithError(HttpServletResponse response, int statusCode, String reason) throws IOException {
		String json = String.format(MESSAGE_JSON, "\"" + reason + "\"");
		
		sendJson(response, statusCode, json);
		util.logMessage(reason);
	}
	
	/**
	 * Ответ с несколькими ошибками.
	 * 
	 * @param response ответ
	 * @param statusCode статус код ответа
	 * @param errors список ошибок 
	 * @throws IOException ошибка ввода-вывода
	 */
	public void respondWithErrors(HttpServletResponse response, int statusCode, List<String> errors) throws IOException {
		for (String error : errors) {
			util.logMessage(error);
		}

		String stringsArray = serializeToJson(errors);
		
		String json = String.format(MESSAGE_JSON, stringsArray);
		
		sendJson(response, statusCode, json);
	}

	/**
	 * Сериализация объекта в JSON
	 * 
	 * @param object объект для сериализации
	 * @return строка в виде JSON
	 * @throws JsonProcessingException ошибка обработки JSON
	 */
	private String serializeToJson(Object object) throws JsonProcessingException {
		var mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
	
	/**
	 * Ответ с JSON. Объект сериализуется.
	 * 
	 * @param response ответ
	 * @param statusCode статус код ответа
	 * @param object объект для сериализации
	 * @throws JsonProcessingException ошибка обработки JSON
	 * @throws IOException ошибка ввода-вывода
	 */
	protected void respondWithJson(HttpServletResponse response, int statusCode, Object object)
			throws JsonProcessingException, IOException {
		String json = serializeToJson(object);

		sendJson(response, statusCode, json);
	}

	/**
	 * Отправка JSON результата
	 *  
	 * @param response @see HttpServletResponse
	 * @param statusCode статус код
	 * @param json текст в виде JSON
	 * @throws IOException ошибка ввода вывода
	 */
	private void sendJson(HttpServletResponse response, int statusCode, String json) throws IOException {
		response.setStatus(statusCode);
		response.setContentType(MediaType.APPLICATION_JSON);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json);
		out.flush();
	}
	
	/**
	 * Валидация любого DTO любым валидатором
	 * 
	 * @param dto DTO для валидации, должно быть унаследовано от AbstractDto
	 * @return список строк нарушений валидации, пустой список - если нарушений не выявлено
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public List<String> validateDto(AbstractDto dto) throws IllegalArgumentException, IllegalAccessException {
		var dtoValidator = new DtoValidator();
		return dtoValidator.validate(dto);
	}

	/**
	 * Десериализация любого DTO из HTTP запроса
	 * 
	 * @param <T> наследник AbstractDto, который будет возвращён
	 * @param request     HTTP запрос
	 * @param dtoClass класс DTO, унаследованный от AbstractDto
	 * @param AbstractDto DTO, унаследованное от AbstractDto
	 * @return десериализованное DTO
	 * @throws IOException ошибка ввода-вывода
	 */
	public <T extends AbstractDto> T deserializeDto(HttpServletRequest request, Class<T> dtoClass) throws IOException {
		var objectMapper = new ObjectMapper();

		String body = request.getReader().lines().collect(Collectors.joining());
		return objectMapper.readValue(body, dtoClass);
	}
	
	/**
	 * Получение валидированного DTO из HTTP запроса
	 * 
	 * @param <T> наследник AbstractDto, который будет возвращён
	 * @param request HTTP запрос
	 * @param dtoClass класс DTO, унаследованный от AbstractDto
	 * @return десериализованное и валидированное DTO
	 * @throws IOException ошибка ввода-вывода
	 * @throws DtoValidationException ошибка валидации DTO, содержит список нарушений
	 */
	public <T extends AbstractDto> T readValidDto(HttpServletRequest request,
			Class<T> dtoClass) throws IOException, DtoValidationException {
		// Десериализация DTO
		T dto = deserializeDto(request, dtoClass);
		// Валидация DTO
		List<String> violations = new ArrayList<>();
		try {
			violations = validateDto(dto);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new DtoValidationException(violations);
		}
		if (!violations.isEmpty()) {
			throw new DtoValidationException(violations);
		}
		return dto;
	}

}
