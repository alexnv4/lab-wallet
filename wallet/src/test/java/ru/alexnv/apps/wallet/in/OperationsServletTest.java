/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.math.BigDecimal;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import com.fasterxml.jackson.databind.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.service.exceptions.TransactionIdNotUniqueException;
import ru.alexnv.apps.wallet.in.servlets.OperationsServlet;
import ru.alexnv.apps.wallet.service.PlayerService;
import ru.alexnv.apps.wallet.service.exceptions.DebitException;

/**
 * 
 */
class OperationsServletTest {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@BeforeEach
	void setup() {
		// Создаём mock объекты запроса и ответа
        this.request = mock(HttpServletRequest.class);
        this.response = mock(HttpServletResponse.class);
	}

	/**
	 * Test method for {@link jakarta.servlet.http.HttpServlet#doGet(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@Test
	final void should_respondWithJson_whenGetRequest() throws IOException, ServletException {
	
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = mock(PlayerDto.class);
		
        // Задаём нужные параметры для запроса
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));
		
        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = spy(OperationsServlet.class);
        
        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(operationsServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);
        //when(request.getHeader(any())).thenReturn("Bearer 1.2.3");
        
        when(playerService.getBalance()).thenReturn(playerDto);
        
		operationsServlet.doGet(request, response);

		// Проверяем статус код
		verify(response).setStatus(200);

		// Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "id": 0,
				  "login": null,
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
	
	/**
	 * Test method for {@link jakarta.servlet.http.HttpServlet#doPut(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}.
	 * @throws IOException 
	 * @throws TransactionIdNotUniqueException 
	 * @throws ServletException 
	 */
	@Test
	final void should_return200_andReturnPlayerDtoJSON_whenCreditOk() throws IOException, TransactionIdNotUniqueException, ServletException {
		String inputJson = """
				{
				  "balanceChange": "123",
				  "transactionId": "111"
				}
				""";
		
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = new PlayerDto();
        
        playerDto.setId(1000L);
        playerDto.setLogin("login1000");
        playerDto.setPassword("password1000");
        playerDto.setBalance("333.00");
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = spy(OperationsServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(operationsServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        //when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.credit(new BigDecimal("123"), 111L)).thenReturn(playerDto);

        // Вызываем метод doPost
		operationsServlet.doPut(request, response);
        
        // Проверяем статус код
        verify(response).setStatus(200);

        // Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "id": 1000,
				  "login": "login1000",
				  "password": "password1000",
				  "balance": "333.00"
				}
				""";
		
        writer.flush();

		var mapper = new ObjectMapper();
		JsonNode expected = mapper.readTree(responseJson);
		JsonNode actual = mapper.readTree(stringWriter.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	final void should_return200_andReturnPlayerDtoJSON_whenDebitOk()
			throws IOException, TransactionIdNotUniqueException, ServletException, DebitException {
		String inputJson = """
				{
				  "balanceChange": "-123",
				  "transactionId": "111"
				}
				""";
		
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        PlayerDto playerDto = new PlayerDto();
        
        playerDto.setId(1000L);
        playerDto.setLogin("login1000");
        playerDto.setPassword("password1000");
        playerDto.setBalance("333.00");
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = spy(OperationsServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(operationsServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.authorize
        //when(playerDto.getLogin()).thenReturn("mylogin6");
		when(playerService.debit(new BigDecimal("123"), 111L)).thenReturn(playerDto);

        // Вызываем метод doPost
		operationsServlet.doPut(request, response);
        
        // Проверяем статус код
        verify(response).setStatus(200);

        // Проверяем возвращаемый JSON
		String responseJson = """
				{
				  "id": 1000,
				  "login": "login1000",
				  "password": "password1000",
				  "balance": "333.00"
				}
				""";
		
        writer.flush();

		var mapper = new ObjectMapper();
		JsonNode expected = mapper.readTree(responseJson);
		JsonNode actual = mapper.readTree(stringWriter.toString());
		assertEquals(expected, actual);
	}
	
	@Test
	final void should_return400_and_Throw_TransactionIdNotUniqueException_when_transactionIdInput_NotUnique()
			throws IOException, TransactionIdNotUniqueException, ServletException, DebitException {
		String inputJson = """
				{
				  "balanceChange": "123",
				  "transactionId": "111"
				}
				""";
		
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = spy(OperationsServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(operationsServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.credit
		doThrow(TransactionIdNotUniqueException.class).when(playerService).credit(new BigDecimal("123"), 111L);

        // Вызываем метод doPost
		operationsServlet.doPut(request, response);
        
		// Когда выполнится операция кредита
		Executable executable = () -> playerService.credit(new BigDecimal("123"), 111L);
		
		// Проверяем, что бросилось исключение
		assertThrows(TransactionIdNotUniqueException.class, executable);

		// Проверяем статус код
		verify(response, times(1)).sendError(400);
	}
	
	@Test
	final void should_return400_and_Throw_DebitException_when_debitFailed()
			throws IOException, TransactionIdNotUniqueException, ServletException, DebitException {
		String inputJson = """
				{
				  "balanceChange": "-123",
				  "transactionId": "112"
				}
				""";
		
		// Начальные mock
        ServletContext sg = mock(ServletContext.class);
        PlayerService playerService = mock(PlayerService.class);
        
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём StringWriter для записи ответа
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = spy(OperationsServlet.class);

        // Устанавливаем mock контекст сервлета
        doReturn(sg).when(operationsServlet).getServletContext();
        when(sg.getAttribute("PlayerService")).thenReturn(playerService);

        // Mock playerService.debit
		doThrow(DebitException.class).when(playerService).debit(new BigDecimal("123"), 112L);

        // Вызываем метод doPost
		operationsServlet.doPut(request, response);
        
		// Когда выполнится операция кредита
		Executable executable = () -> playerService.debit(new BigDecimal("123"), 112L);
		
		// Проверяем, что бросилось исключение
		assertThrows(DebitException.class, executable);

		// Проверяем статус код
		verify(response, times(1)).sendError(400);
	}
	
	@Test
	final void should_return400_whenParsingJSONtoBalanceChangeDTOFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "abcde": "1234",
				  "fghjk": "56789"
				}
				""";
		
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = new OperationsServlet();

        // Вызываем метод doPut
        operationsServlet.doPut(request, response);

        // Проверяем статус код
        verify(response, times(1)).sendError(400);
	}
	
	@Test
	final void should_return400_whenBalanceChangeDTOValidationFailed() throws ServletException, IOException {
		String inputJson = """
				{
				  "balanceChange": "123.55555",
				  "transactionId": "-112"
				}
				""";
		
        // Задаём нужные параметры для запроса
        when(request.getContentType()).thenReturn("application/json");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(inputJson)));
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/wallet/players/1/balance"));

        // Создаём экземпляр OperationsServlet
        OperationsServlet operationsServlet = new OperationsServlet();

        // Вызываем метод doPut
        operationsServlet.doPut(request, response);

        // Проверяем статус код
        verify(response, times(1)).sendError(400);
	}

}
