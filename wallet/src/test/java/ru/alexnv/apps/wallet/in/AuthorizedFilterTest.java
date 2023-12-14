/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.MockedStatic;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.alexnv.apps.wallet.in.filters.AuthorizedFilter;

/**
 * 
 */
class AuthorizedFilterTest {

	private HttpServletRequest request;
	private HttpServletResponse response;

	@BeforeEach
	void setup() {
		// Создаём mock объекты запроса и ответа
		this.request = mock(HttpServletRequest.class);
		this.response = mock(HttpServletResponse.class);
	}
	
	@Test
	final void should_returnNothing_when_PostContentTypeIsJson() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(chain, times(1)).doFilter(request, response);
	}
	
	private static record TokenTestCase(String token, String endpoint, int statusCode) { };
	
	@ParameterizedTest
	@MethodSource("tokenSource")
	final void testTokensWithEndpointsAndRepsponses(TokenTestCase tokenTestCase) throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		PrintWriter mockWriter = mock(PrintWriter.class);
		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn(tokenTestCase.endpoint);
		when(request.getHeader("Authorization")).thenReturn(tokenTestCase.token);
		when(response.getWriter()).thenReturn(mockWriter);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).setStatus(tokenTestCase.statusCode);
		verify(mockWriter, times(1)).flush();
	}
	
	static Stream<Arguments> tokenSource() {
		List<TokenTestCase> cases = new ArrayList<>();
		cases.add(new TokenTestCase("Bearer 123hbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/2/balance", 403));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.TAMPERED_SIGNATURE", "/2/balance", 403));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/3/balance", 401));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/two/balance", 401));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "//balance", 401));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/2.00/balance", 401));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/2/balance/add", 401));
		cases.add(new TokenTestCase("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI", "/2/2/balance", 401));
		
		Iterator<TokenTestCase> caseIter = cases.iterator();
		
		return Stream.of(
				Arguments.of(Named.of("Should respond with 403 when token's first part incorrect", caseIter.next())),
				Arguments.of(Named.of("Should respond with 403 when token's signature is tampered", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when IdURI NotEquals Token's Payload SubId", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when ID is not a number", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when ID is not set", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when ID is of type double", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when URI doesn't end with balance", caseIter.next())),
				Arguments.of(Named.of("Should respond with 401 when URI has multiple IDs", caseIter.next()))
		);
	}
	
	@Test
	final void should_returnRespondWith401_when_AuthorizationHeaderMissing() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		PrintWriter mockWriter = mock(PrintWriter.class);
		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance/");
		when(response.getWriter()).thenReturn(mockWriter);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).setStatus(401);
		verify(mockWriter, times(1)).flush();
	}
	
	@Test
	final void should_returnRespondWith401_when_AuthorizationHeaderNotStartWithBearer() throws IOException, ServletException {
		FilterChain chain = mock(FilterChain.class);

		PrintWriter mockWriter = mock(PrintWriter.class);
		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance/add");
		when(request.getHeader("Authorization")).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");
		when(response.getWriter()).thenReturn(mockWriter);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = new AuthorizedFilter();

		// Вызываем метод doFilter
		authorizedFilter.doFilter(request, response, chain);

		// Проверяем выполнение фильтра
		verify(response, times(1)).setStatus(401);
		verify(mockWriter, times(1)).flush();
	}
	
	@Test
	final void should_throwInvalidKeyException_whenJWTValidateError()
			throws IOException, ServletException, InvalidKeyException, NoSuchAlgorithmException {
		FilterChain chain = mock(FilterChain.class);

		PrintWriter mockWriter = mock(PrintWriter.class);
		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");
		when(response.getWriter()).thenReturn(mockWriter);
		JSONWebToken jwt = mock(JSONWebToken.class);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = spy(AuthorizedFilter.class);
		
		doThrow(InvalidKeyException.class).when(jwt).isValidToken(any());
		try (MockedStatic<JSONWebToken> mockedJWT = mockStatic(JSONWebToken.class)) {
			mockedJWT.when(() -> JSONWebToken.createJWT()).thenReturn(jwt);

			// Вызываем метод doFilter
			authorizedFilter.doFilter(request, response, chain);

			Executable executable = () -> jwt.isValidToken(any());

			// Проверяем, что бросилось исключение
			assertThrows(InvalidKeyException.class, executable);

			// Проверяем выполнение фильтра
			verify(response, times(1)).setStatus(401);
			verify(mockWriter, times(1)).flush();
		}
	}
	
	@Test
	final void should_throwNoSuchAlgorithmException_whenJWTValidateError()
			throws IOException, ServletException, InvalidKeyException, NoSuchAlgorithmException {
		FilterChain chain = mock(FilterChain.class);

		PrintWriter mockWriter = mock(PrintWriter.class);
		// Задаём нужные параметры для запроса
		when(request.getPathInfo()).thenReturn("/2/balance");
		when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOjJ9.d8ViCOHe7bISbLUjKJ0m2bfr6dLpVNoUVMSM_J8oTAI");
		when(response.getWriter()).thenReturn(mockWriter);
		JSONWebToken jwt = mock(JSONWebToken.class);

		// Создаём экземпляр JsonRequestFilter
		AuthorizedFilter authorizedFilter = spy(AuthorizedFilter.class);
		
		doThrow(NoSuchAlgorithmException.class).when(jwt).isValidToken(any());
		try (MockedStatic<JSONWebToken> mockedJWT = mockStatic(JSONWebToken.class)) {
			mockedJWT.when(() -> JSONWebToken.createJWT()).thenReturn(jwt);

			// Вызываем метод doFilter
			authorizedFilter.doFilter(request, response, chain);

			Executable executable = () -> jwt.isValidToken(any());

			// Проверяем, что бросилось исключение
			assertThrows(NoSuchAlgorithmException.class, executable);

			// Проверяем выполнение фильтра
			verify(response, times(1)).setStatus(401);
			verify(mockWriter, times(1)).flush();
		}
	}

}
