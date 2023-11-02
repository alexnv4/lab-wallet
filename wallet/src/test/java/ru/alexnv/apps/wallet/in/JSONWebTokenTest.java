/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import static org.junit.jupiter.api.Assertions.*;

import java.security.*;

import org.junit.jupiter.api.*;

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
	 * Test method for {@link ru.alexnv.apps.wallet.in.JSONWebToken#generate()}.
	 */
	//@Test
	final void testGenerate() {
		fail("Not yet implemented"); // TODO
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
	
	@Test
	final void should_returnFalseToken_when_incorrectTokenPassed() throws InvalidKeyException, NoSuchAlgorithmException {
		
		String token = "INCORRECTOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}

	@Test
	final void should_returnFalse_when_incorrectTokenSignaturePassed() throws InvalidKeyException, NoSuchAlgorithmException {
		
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.INCORRECT-SIGNATURE";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_TokenWithoutDots() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = "not a token";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_incorrectTokenStructure() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc.";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_TokenPartsEmpty() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = "...";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_TokenStartsWithDot() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = ".eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_TokenStartsWithSpace() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = " eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
	
	@Test
	final void should_returnFalse_when_TokenEndsWithSpace() throws InvalidKeyException, NoSuchAlgorithmException {
		// протестировать с разными строками токена: с 2 точками, без, с 1 точкой, с точкой в конце
		
		String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI1NjY1OWYwOS02MmVlLTQ4MTktODI5OC05ZmRlYTM2ZjFlNzcifQ.MWlPAKnDYk7c5EqYs7STrGs1I6PvbWjV673KE0Yifhc ";
		
		boolean tokenValid = jwt.isValidToken(token);
		
		assertFalse(tokenValid);
	}
}
