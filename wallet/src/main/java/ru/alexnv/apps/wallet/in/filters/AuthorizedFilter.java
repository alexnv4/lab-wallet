package ru.alexnv.apps.wallet.in.filters;

import static jakarta.servlet.http.HttpServletResponse.*;

import java.io.IOException;
import java.security.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.in.*;
import ru.alexnv.apps.wallet.in.servlets.OperationsServlet;

/**
 * Фильтр авторизованных запросов.
 */
public class AuthorizedFilter extends HttpFilter implements Filter {
       
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8252598143332039828L;
    
    /** Вспомогательный класс. */
    private static Utility util = new Utility();

	/**
	 * Instantiates a new authorized filter.
	 *
	 * @see HttpFilter#HttpFilter()
	 */
    public AuthorizedFilter() {
        super();
    }

	/**
	 * Фильтрация авторизованных запросов.
	 * Проверяется заголовок входящего запроса на содержание строки Authorization: Bearer <token>
	 * Токен считывается и проверяется его валидность.
	 * Проверяется соответствие токена и ID игрока в URI.
	 * Если в результате проверок выявлено несоответствие - ошибка SC_UNAUTHORIZED, доступ к ресурсу запрещён.
	 * В случае успеха проверок доступ к ресурсу разрешается.
	 *
	 * @param request запрос
	 * @param response ответ
	 * @param chain цепочка фильтров
	 * @throws IOException ошибка ввода-вывода
	 * @throws ServletException ошибка сервлета
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		int responseCode = SC_UNAUTHORIZED;

		String authorizationHeader = httpRequest.getHeader(OperationsServlet.AUTHORIZATION_NAME);
		if (authorizationHeader != null && authorizationHeader.startsWith(OperationsServlet.BEARER_NAME)) {
			String token = authorizationHeader.substring(OperationsServlet.BEARER_NAME.length());
			JSONWebToken jwt = createJWT();
			try {
				if (jwt.isValidToken(token)) { // подпись токена валидна
					Long playerIdURI = getPlayerIdFromURI(httpRequest);
					if (playerIdURI != null) { // ID в URI валидный
						Long playerId = jwt.readSubValue(token);
						if (playerIdURI.equals(playerId)) { // ID соответствует токену
							// pass the request along the filter chain
							chain.doFilter(request, response);
							util.logMessage("Запрос по токену: " + token);
							return;
						}
					}
				} else { // токен есть в заголовке, но он не валиден
					responseCode = SC_FORBIDDEN;
				}
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		httpResponse.sendError(responseCode, "Invalid access token");
	}
	
	/**
	 * Создание объекта JWT
	 * 
	 * @return JWT
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public JSONWebToken createJWT() throws JsonMappingException, JsonProcessingException {
		return new JSONWebToken();
	}

	/**
	 * Получение идентификатора игрока из URI.
	 *
	 * @param request запрос
	 * @return идентификатор игрока из ресурса
	 */
	private Long getPlayerIdFromURI(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		if (pathInfo.endsWith("/balance")) {
			String[] parts = pathInfo.split("/");
			if (parts.length == 3 && parts[1].matches("\\d+")) {
				Long playerIdURI = Long.valueOf(parts[1]);
				return playerIdURI;
			}
		}
		return null;
	}

}
