/**
 * 
 */
package ru.alexnv.apps.wallet.domain.dto.validators;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ru.alexnv.apps.wallet.domain.dto.AbstractDto;
import ru.alexnv.apps.wallet.domain.dto.BalanceChangeDto;

/**
 * Валидатор для DTO изменения баланса
 * Используется рефлексия для чтения аннотаций
 */
public class BalanceChangeDtoValidator extends AbstractDtoValidator {

	/**
	 * Валидация DTO изменения баланса
	 * 
	 * @param playerDto DTO игрока
	 * @return список нарушений валидации
	 */
	@Override
	public List<String> validate(AbstractDto dto) {
		BalanceChangeDto balanceDto = (BalanceChangeDto) dto;
		
		try {
			validateTransactionId(balanceDto);
			validateBalanceChange(balanceDto);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		return violations;
	}

	/**
	 * @param balanceDto DTO изменения баланса
	 * @throws NoSuchFieldException такого поля в классе нет
	 * @throws SecurityException ошибка безопасности Java
	 */
	private void validateBalanceChange(BalanceChangeDto balanceDto) throws NoSuchFieldException, SecurityException {
		Field reflectionField = balanceDto.getClass().getDeclaredField("balanceChange");
		String balanceChange = balanceDto.getBalanceChange();
		
		if (balanceChange == null || balanceChange.isBlank()) {
			if (reflectionField.isAnnotationPresent(NotBlank.class)) {
				NotBlank notBlankAnno = reflectionField.getDeclaredAnnotation(NotBlank.class);
				violations.add(notBlankAnno.message());
			}
		} else if (reflectionField.isAnnotationPresent(Digits.class)) {
			Digits digitsAnno = reflectionField.getDeclaredAnnotation(Digits.class);
			int integer = digitsAnno.integer();
			int fraction = digitsAnno.fraction();

			BigDecimal balanceChangeNumeric = new BigDecimal(balanceChange);
			if (BigDecimal.ZERO.compareTo(balanceChangeNumeric) == 0) {
				NotBlank notBlankAnno = reflectionField.getDeclaredAnnotation(NotBlank.class);
				violations.add(notBlankAnno.message());				
			}
			
			int fractionInput = balanceChangeNumeric.scale();
			int integerInput = balanceChangeNumeric.precision() - fractionInput;
			
			if (fractionInput > fraction || integerInput > integer) {
				violations.add(digitsAnno.message());
			}
		}
	}

	/**
	 * @param balanceDto DTO изменения баланса
	 * @throws NoSuchFieldException такого поля в классе нет
	 * @throws SecurityException ошибка безопасности Java
	 */
	private void validateTransactionId(BalanceChangeDto balanceDto) throws NoSuchFieldException, SecurityException {
		Field reflectionField = balanceDto.getClass().getDeclaredField("transactionId");
		Long id = balanceDto.getTransactionId();
		
		if (reflectionField.isAnnotationPresent(NotNull.class)) {
			if (id == null) {
				NotNull notNullAnno = reflectionField.getDeclaredAnnotation(NotNull.class);
				violations.add(notNullAnno.message());
			}
		}
		
		if (reflectionField.isAnnotationPresent(Positive.class)) {
			if (id <= 0) {
				Positive positiveAnno = reflectionField.getDeclaredAnnotation(Positive.class);
				violations.add(positiveAnno.message());
			}
		}
	}
	
}
