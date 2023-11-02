/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.security.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.in.servlets.AuthorizationServlet;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.AuthorizationException;

/**
 * Тест сервлета авторизации
 */
class AuthorizationServletTest {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@BeforeEach
	void setup() {
		// Создаём mock объекты запроса и ответа
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
	}

	@Test
	final void should_return200_whenAuthorized() throws ServletException, IOException, AuthorizationException {
		String inputJson = """
				{
				  "login": "mylogin6",
				  "password": "mypass6"
				}
				""";
		
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = spy(AuthorizationServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(authorizationServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.authorize("mylogin6", "mypass6")).thenReturn(playerDto);

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);
        
        // Проверяем статус код
        verify(response).setStatus(200);

        // Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "id": 0,
				  "login": "mylogin6",
				  "password": null,
				  "balance": null
				}
				""";
		
        writer.flush();

		var mapper = new ObjectMapper();
		JsonNode expected = mapper.readTree(responseJson);
		JsonNode actual = mapper.readTree(stringWriter.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	final void should_return401_and_Throw_AuthorizationException_whenNotAuthorized()
			throws ServletException, IOException, AuthorizationException {
		String inputJson = """
				{
				  "login": "mylogin6",
				  "password": "mypassword999"
				}
				""";

		ServletContext sg = mock(ServletContext.class);
		PlayerService playerService = mock(PlayerService.class);

		// Задаём нужные параметры для запроса
		when(request.getContentType()).thenReturn("application/json");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

		// Создаём StringWriter для записи ответа
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(writer);

		// Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = spy(AuthorizationServlet.class);

		// Устанавливаем mock контекст сервлета
		doReturn(sg).when(authorizationServlet).getServletContext();
		when(sg.getAttribute("PlayerService")).thenReturn(playerService);
		
		// Mock playerService.authorize, вызываем исключение AuthorizationException
		when(playerService.authorize("mylogin6", "mypassword999")).thenThrow(AuthorizationException.class);

		// Вызываем метод doPost
		authorizationServlet.doPost(request, response);

		// Когда выполнится регистрация 
		Executable executable = () -> playerService.authorize("mylogin6", "mypassword999");
		
		// Проверяем, что бросилось исключение
		assertThrows(AuthorizationException.class, executable);

		// Проверяем статус код
		verify(response).setStatus(401);
	}
	
	@Test
	final void should_return401_Throw_InvalidKeyException_whenTokenSecretKeyInvalid()
			throws ServletException, IOException, AuthorizationException, InvalidKeyException, NoSuchAlgorithmException {
		String inputJson = """
				{
				  "login": "mylogin6",
				  "password": "mypassword999"
				}
				""";

		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        JSONWebToken jwt = mock(JSONWebToken.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = spy(AuthorizationServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(authorizationServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.authorize("mylogin6", "mypassword999")).thenReturn(playerDto);
		doThrow(InvalidKeyException.class).when(jwt).generate();
		when(authorizationServlet.createJWT(any())).thenReturn(jwt);

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);

		// Когда выполнится регистрация 
		Executable executable = () -> jwt.generate();
		
		// Проверяем, что бросилось исключение
		assertThrows(InvalidKeyException.class, executable);

		// Проверяем статус код
		verify(response).setStatus(401);
	}
	
	@Test
	final void should_return401_Throw_NoSuchAlgorithmException_whenAlgorithmNotFound()
			throws ServletException, IOException, AuthorizationException, InvalidKeyException, NoSuchAlgorithmException {
		String inputJson = """
				{
				  "login": "mylogin6",
				  "password": "mypassword999"
				}
				""";

		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        JSONWebToken jwt = mock(JSONWebToken.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = spy(AuthorizationServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(authorizationServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.authorize("mylogin6", "mypassword999")).thenReturn(playerDto);
		doThrow(NoSuchAlgorithmException.class).when(jwt).generate();
		when(authorizationServlet.createJWT(any())).thenReturn(jwt);

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);

		// Когда выполнится регистрация 
		Executable executable = () -> jwt.generate();
		
		// Проверяем, что бросилось исключение
		assertThrows(NoSuchAlgorithmException.class, executable);

		// Проверяем статус код
		verify(response).setStatus(401);
	}
	
	@Test
	final void should_return401_Throw_JsonProcessingException_whenJWTgenerateFailed()
			throws ServletException, AuthorizationException, InvalidKeyException, NoSuchAlgorithmException, IOException {
		String inputJson = """
				{
				  "login": "mylogin6",
				  "password": "mypassword999"
				}
				""";

		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = mock(PlayerDto.class);
        JSONWebToken jwt = mock(JSONWebToken.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = spy(AuthorizationServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(authorizationServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.authorize("mylogin6", "mypassword999")).thenReturn(playerDto);
		doThrow(JsonProcessingException.class).when(jwt).generate();
		when(authorizationServlet.createJWT(any())).thenReturn(jwt);

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);

		// Когда выполнится регистрация
		Executable executable = () -> jwt.generate();
		
		// Проверяем, что бросилось исключение
		assertThrows(JsonProcessingException.class, executable);

		// Проверяем статус код
		verify(response).setStatus(401);
	}
	
	@Test
	final void should_return400_whenParsingJSONtoPlayerDTOFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "abcde": "1234",
				  "fghjk": "56789"
				}
				""";
		
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = new AuthorizationServlet();

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);

        // Проверяем статус код
        verify(response, times(1)).sendError(400);
	}
	
	@Test
	final void should_return400_whenPlayerDTOValidationFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "login": "1",
				  "password": "2"
				}
				""";
		
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));

        // Создаём экземпляр AuthorizationServlet
        AuthorizationServlet authorizationServlet = new AuthorizationServlet();

        // Вызываем метод doPost
        authorizationServlet.doPost(request, response);

        // Проверяем статус код
        verify(response, times(1)).sendError(400);
	}

}
