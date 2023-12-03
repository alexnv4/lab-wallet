/**
 * 
 */
package ru.alexnv.apps.wallet.in;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

/**
 * Реализация JSON Web Token.
 */
public class JSONWebToken {

	/** Заголовок JWT с указанием алгоритма. */
	private static final String JWT_HEADER = """
			{
			  "alg": "HS256",
			  "typ": "JWT"
			}
			""";

	/** Секретный ключ для подписи. */
	private static final String SECRET_KEY = "Y18nRG#CHD8kp4Bs_5AS@KZphNCdpVdM";
	
	/** HMAC_SHA256_алгоритм. */
	private static final String HMAC_SHA256_Algorithm = "HmacSHA256";
	
	/** Закодированный заголовок JWT. */
	private final String encodedHeader;
	
	/** Не подписанный токен. */
	private String unsignedToken;
	
	/** Итоговый токен .*/
	private String token;
	
	/** Подпись. */
	private String signature;
	
	/** Subject of the JWT. */
	private Long sub;

	/**
	 * Создание JSON Web Token.
	 *
	 * @throws JsonMappingException ошибка обработки JSON
	 * @throws JsonProcessingException ошибка обработки JSON
	 */
	public JSONWebToken() throws JsonMappingException, JsonProcessingException {
		// кодирование header
		var mapper = new ObjectMapper();
		JsonNode headerNodes = mapper.readTree(JWT_HEADER);
		this.encodedHeader = base64Encode(headerNodes.toString());
	}
	
	/**
	 * @param sub Subject of the JWT (playerId)
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * 
	 */
	public JSONWebToken(Long sub) throws JsonMappingException, JsonProcessingException {
		this();
		this.sub = sub;
	}

	/**
	 * Base 64 кодирование данных.
	 *
	 * @param data данные в виде текста
	 * @return закодированная строка
	 */
	private String base64Encode(String data) {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	/**
	 * Base 64 кодирование данных.
	 *
	 * @param bytes массив байт
	 * @return закодированная строка
	 */
	private String base64Encode(byte[] bytes) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
	
	/**
	 * Алгоритм HMAC для вычисления подписи.
	 *
	 * @param algorithm название алгоритма
	 * @param data данные
	 * @param key ключ
	 * @return закодированная подпись
	 * @throws NoSuchAlgorithmException ошибка поиска алгоритма
	 * @throws InvalidKeyException ошибка ключа
	 */
	private String hmacWithJava(String algorithm, String data, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		Mac mac = Mac.getInstance(algorithm);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
		mac.init(secretKeySpec);
		return base64Encode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Генерация нового токена.
	 *
	 * @return сгенерированный токен
	 * @throws JsonProcessingException ошибка обработки JSON
	 * @throws InvalidKeyException ошибка ключа
	 * @throws NoSuchAlgorithmException ошибка поиска алгоритма
	 */
	public String generate() throws JsonProcessingException, InvalidKeyException, NoSuchAlgorithmException {
		// кодирование payload
		var mapper = new ObjectMapper();
		var payloadNode = mapper.createObjectNode();
		
		payloadNode.put("sub", sub);
		
		String payloadJson = mapper.writeValueAsString(payloadNode);
		String encodedPayload = base64Encode(payloadJson);
		
		// создание подписи
		unsignedToken =  encodedHeader + "." + encodedPayload;
		signature = hmacWithJava(HMAC_SHA256_Algorithm, unsignedToken, SECRET_KEY);
		token = unsignedToken + "." + signature;
		return token;
	}

	/**
	 * Получение токена.
	 *
	 * @return токен
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Проверка валидности предоставленного токена.
	 * Вычисляется подпись по первым двум частям токена и сравнивается с предоставленной.
	 *
	 * @param token предоставленный токен для проверки
	 * @return true, если токен валидный
	 * @throws InvalidKeyException ошибка ключа
	 * @throws NoSuchAlgorithmException ошибка поиска алгоритма
	 */
	public boolean isValidToken(String token) throws InvalidKeyException, NoSuchAlgorithmException {
		String[] parts = token.split("\\.", -1);
		if (parts.length != 3) {
			return false;
		}
		
		String headerEncoded = parts[0];
		String payloadEncoded = parts[1];
		unsignedToken = headerEncoded + "." + payloadEncoded;
		signature = parts[2];
		String calculatedSignature = hmacWithJava(HMAC_SHA256_Algorithm, unsignedToken, SECRET_KEY);
		return calculatedSignature.equals(signature);
	}

	/**
	 * Получить subject
	 * 
	 * @param token токен 
	 * @return значение subject
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public Long readSubValue(String token) throws JsonMappingException, JsonProcessingException {
		if (sub == null) {
			decodePayload(token);
		}
		return sub;
	}
	
	/**
	 * Декодирование payload из токена и установка полей класса из JWT Claims
	 * 
	 * @param token токен
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public void decodePayload(String token) throws JsonMappingException, JsonProcessingException {
		String[] parts = token.split("\\.", -1);
		String payloadEncoded = parts[1];
		
		byte[] decodedPayload = Base64.getUrlDecoder().decode(payloadEncoded);
		String payloadJson = new String(decodedPayload);
		
		var objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(payloadJson);
		this.sub = jsonNode.get("sub").longValue();
	}
	
	/**
	 * Создание объекта JWT
	 * 
	 * @return JWT
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static JSONWebToken createJWT() throws JsonMappingException, JsonProcessingException {
		return new JSONWebToken();
	}
	
	/**
	 * Создание JWT объекта
	 * @param playerId 
	 * 
	 * @return JSONWebToken объект
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	public static JSONWebToken createJWT(Long playerId) throws JsonMappingException, JsonProcessingException {
		return new JSONWebToken(playerId);
	}
	
}
