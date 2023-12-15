/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.*;

import java.security.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 */
class JSONWebTokenTest {
	
	private JSONWebToken jwt;
	
	@BeforeEach
	void setup() throws JsonMappingException, JsonProcessingException {
		jwt = new JSONWebToken();
	}

	/**
	 * Test method for {@link ru.alexnv.apps.wallet.in.JSONWebToken#isValidToken(java.lang.String)}.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	@Test
	final void should_returnTrue_when_correctTokenPassed() throws InvalidKeyException, NoSuchAlgorithmException {
		
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertTrue(tokenValid);
	}
	
	static Stream<Arguments> tokensSource() {
		return Stream.of(
				Arguments.of(Named.of("Should return false when incorrect token passed",
						"INCORRECTOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc")),
				Arguments.of(Named.of("Should return false when incorrect token signature passed",
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.INCORRECT-SIGNATURE")),
				Arguments.of(Named.of("Should return false when token token without dots",
						"not a token")),
				Arguments.of(Named.of("Should return false when incorrect token structure",
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc.")),
				Arguments.of(Named.of("Should return false when token parts are empty",
						"...")),
				Arguments.of(Named.of("Should return false when token starts with a dot",
						".eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc")),
				Arguments.of(Named.of("Should return false when token starts with a space",
						" eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc")),
				Arguments.of(Named.of("Should return false when token ends with a space",
						"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc "))
		);
	}
	
	@ParameterizedTest
	@MethodSource("tokensSource")
	final void should_returnFalseToken_when_incorrectTokenPassed(String token) throws InvalidKeyException, NoSuchAlgorithmException {
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
}
