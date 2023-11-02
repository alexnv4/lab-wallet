/**
 * 
 */
package ru.alexnv.apps.wallet.domain.dto.validators;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import ru.alexnv.apps.wallet.domain.dto.AbstractDto;
import ru.alexnv.apps.wallet.domain.dto.PlayerDto;

/**
 * Валидатор для DTO игрока
 * Используется рефлексия для чтения аннотаций
 */
public class PlayerDtoValidator extends AbstractDtoValidator {
	
	/**
	 * Создание валидатора DTO игрока
	 */
	public PlayerDtoValidator() {
		super();
		violations = new ArrayList<String>();
	}

	/**
	 * Поддерживаются аннотации NotEmpty и Size
	 * 
	 * @param playerDto DTO игрока
	 * @return список нарушений валидации
	 */
	@Override
	public List<String> validate(AbstractDto dto) {
		PlayerDto playerDto = (PlayerDto) dto;
		
		try {
			validateLoginField(playerDto);
			validatePasswordField(playerDto);
			validateIdField(playerDto);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		return violations;
	}
	
	/**
	 * Валидация поля ID
	 * 
	 * @param playerDto DTO игрока
	 * @throws NoSuchFieldException такого поля в классе нет
	 * @throws SecurityException ошибка безопасности Java
	 */
	private void validateIdField(PlayerDto playerDto) throws NoSuchFieldException, SecurityException {
		Field reflectionField = playerDto.getClass().getDeclaredField("id");
		Long id = playerDto.getId();
		
		if (reflectionField.isAnnotationPresent(Null.class)) {
			if (id != null) {
				Null nullAnno = reflectionField.getDeclaredAnnotation(Null.class);
				violations.add(nullAnno.message());
			}
		}
	}

	/**
	 * Валидация поля логина
	 * 
	 * @param playerDto DTO игрока
	 * @throws NoSuchFieldException такого поля в классе нет
	 * @throws SecurityException ошибка безопасности Java
	 */
	private void validateLoginField(PlayerDto playerDto) throws NoSuchFieldException, SecurityException {
		Field reflectionField = playerDto.getClass().getDeclaredField("login");
		String login = playerDto.getLogin();
		validateCredentialField(reflectionField, login);
	}
	
	/**
	 * Валидация поля пароля
	 * 
	 * @param playerDto DTO игрока
	 * @throws NoSuchFieldException такого поля в классе нет
	 * @throws SecurityException ошибка безопасности Java
	 */
	private void validatePasswordField(PlayerDto playerDto) throws NoSuchFieldException, SecurityException {
		Field reflectionField = playerDto.getClass().getDeclaredField("password");
		String password = playerDto.getPassword();
		validateCredentialField(reflectionField, password);
	}
	
	/**
	 * Валидация поля учётных данных для входа
	 * 
	 * @param reflectionField поле для рефлексии
	 * @param credential учётные данные
	 */
	private void validateCredentialField(Field reflectionField, String credential) {
		if (reflectionField.isAnnotationPresent(NotEmpty.class)) {
			if (credential == null || credential.isEmpty()) {
				NotEmpty notEmptyAnno = reflectionField.getDeclaredAnnotation(NotEmpty.class);
				violations.add(notEmptyAnno.message());
			} else if (reflectionField.isAnnotationPresent(Size.class)) {
				Size sizeAnno = reflectionField.getDeclaredAnnotation(Size.class);
				int min = sizeAnno.min();
				int max = sizeAnno.max();

				int loginLength = credential.length();
				if (loginLength < min || loginLength > max) {
					violations.add(sizeAnno.message());
				}
			}
		}
	}
	
}
