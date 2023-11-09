/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import com.fasterxml.jackson.databind.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.in.servlets.RegisterServlet;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.RegistrationException;

/**
 * Тест сервлета регистрации
 */
class RegistrationServletTest {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@BeforeEach
	void setup() {
		// Создаём mock объекты запроса и ответа
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
	}

	/**
	 * Test method for
	 * {@link ru.alexnv.apps.wallet.in.servlets.RegisterServlet#doPost(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws RegistrationException 
	 * @throws JSONException 
	 */
	@Test
	final void should_return201_whenRegistered() throws ServletException, IOException, RegistrationException {
		String inputJson = """
				{
				  "login": "someLogin",
				  "password": "somePassword"
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

        // Создаём экземпляр RegisterServlet
        RegisterServlet registerServlet = spy(RegisterServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(registerServlet).getServletContext();
        //doReturn(playerService).when(registerServlet).sg.getAttribute("PlayerService");
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.register
		when(playerService.registration("someLogin", "somePassword".toCharArray())).thenReturn("someLogin");
        
        // Вызываем метод doPost
        registerServlet.doPost(request, response);

        // Проверяем статус код
        verify(response).setStatus(201);

        // Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "id": null,
				  "login": "someLogin",
				  "password": "0",
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
	final void should_return409_whenNotRegistered() throws ServletException, IOException, RegistrationException {
		String inputJson = """
				{
				  "login": "someLogin",
				  "password": "somePassword"
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

		// Создаём экземпляр RegisterServlet
		RegisterServlet registerServlet = spy(RegisterServlet.class);

		// Устанавливаем mock контекст сервлета
		doReturn(sg).when(registerServlet).getServletContext();
		when(sg.getAttribute("PlayerService")).thenReturn(playerService);
		
		// Mock регистрации, вызываем исключение RegistrationException
		when(playerService.registration("someLogin", "somePassword".toCharArray())).thenThrow(RegistrationException.class);

		// Вызываем метод doPost
		registerServlet.doPost(request, response);

		// Когда выполнится регистрация 
		Executable executable = () -> playerService.registration("someLogin", "somePassword".toCharArray());
		
		// Проверяем, что бросилось исключение
		assertThrows(RegistrationException.class, executable);

		// Проверяем статус код
		verify(response).setStatus(409);

		// Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "message": "null"
				}
				""";

		writer.flush();
		
		var mapper = new ObjectMapper();
		JsonNode expected = mapper.readTree(responseJson);
		JsonNode actual = mapper.readTree(stringWriter.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	final void should_return400_whenParsingJSONtoPlayerDTOFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "abcde": "1234",
				  "fghjk": "56789"
				}
				""";
		
		PrintWriter mockWriter = mock(PrintWriter.class);
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(response.getWriter()).thenReturn(mockWriter);

        // Создаём экземпляр RegisterServlet
        RegisterServlet registerServlet = new RegisterServlet();

        // Вызываем метод doPost
        registerServlet.doPost(request, response);

        // Проверяем статус код
        verify(response, times(1)).setStatus(400);
        verify(mockWriter, times(1)).flush();        
	}
	
	@Test
	final void should_return400_whenPlayerDTOValidationFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "login": "1",
				  "password": "2"
				}
				""";
		
		PrintWriter mockWriter = mock(PrintWriter.class);
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(response.getWriter()).thenReturn(mockWriter);

        // Создаём экземпляр RegisterServlet
        RegisterServlet registerServlet = new RegisterServlet();

        // Вызываем метод doPost
        registerServlet.doPost(request, response);

        // Проверяем статус код
        verify(response, times(1)).setStatus(400);
        verify(mockWriter, times(1)).flush();
	}

}
