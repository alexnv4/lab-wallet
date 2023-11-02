/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.jupiter.api.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import ru.alexnv.apps.wallet.in.filters.JsonRequestFilter;

/**
 * 
 */
class JsonRequestFilterTest {

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
	 * {@link ru.alexnv.apps.wallet.in.filters.JsonRequestFilter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)}.
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	final void should_return400_when_PostContentTypeIsNotJson() throws IOException, ServletException {
		String input = "NOT JSON";

		// Задаём нужные параметры для запроса
		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn("text/plain");
		when(request.getReader()).thenReturn(new BufferedReader(new StringReader(input)));

		// Создаём экземпляр JsonRequestFilter
		JsonRequestFilter jsonRequestFilter = new JsonRequestFilter();

		// Вызываем метод doFilter
		jsonRequestFilter.doFilter(request, response, null);

		// Проверяем статус код
		verify(response, times(1)).sendError(400);
	}

	@Test
	final void should_return400_when_PostContentTypeIsNull() throws IOException, ServletException {
		// Задаём нужные параметры для запроса
		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn(null);

		// Создаём экземпляр JsonRequestFilter
		JsonRequestFilter jsonRequestFilter = new JsonRequestFilter();

		// Вызываем метод doFilter
		jsonRequestFilter.doFilter(request, response, null);

		// Проверяем статус код
		verify(response, times(1)).sendError(400);
	}
	
	@Test
	final void should_returnNothing_when_PostContentTypeIsJson() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getMethod()).thenReturn("POST");
		when(request.getContentType()).thenReturn("application/json");

		// Создаём экземпляр JsonRequestFilter
		JsonRequestFilter jsonRequestFilter = new JsonRequestFilter();

		// Вызываем метод doFilter
		jsonRequestFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(chain, times(1)).doFilter(request, response);
	}
	
	@Test
	final void should_returnNothing_when_GETContentTypeIsJson() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getMethod()).thenReturn("GET");
		when(request.getContentType()).thenReturn("application/json");

		// Создаём экземпляр JsonRequestFilter
		JsonRequestFilter jsonRequestFilter = new JsonRequestFilter();

		// Вызываем метод doFilter
		jsonRequestFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(chain, times(1)).doFilter(request, response);
	}


}
