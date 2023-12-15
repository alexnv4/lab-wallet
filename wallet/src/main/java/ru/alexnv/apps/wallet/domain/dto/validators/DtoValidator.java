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
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import ru.alexnv.apps.wallet.domain.dto.AbstractDto;

/**
 * Реализация Dto валидатора
 */
public class DtoValidator extends AbstractDtoValidator {

	/**
	 * Валидация Dto. Используется reflection для доступа к полям класса Dto.
	 * Считываются аннотации и их значения.
	 *
	 * @param dto Dto для валидации
	 * @return список нарушений валидации
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException
	 */
	@Override
	public List<String> validate(AbstractDto dto) throws IllegalArgumentException, IllegalAccessException {
		// NotEmpty, Null, Size

		// Получение класса DTO
		Class<?> clazz = dto.getClass();

		// Получаем все поля класса
		Field[] fields = clazz.getDeclaredFields();

		// Проходим по каждому полю
		for (Field field : fields) {
			// Делаем поле доступным для чтения и записи, даже если оно приватное
			field.setAccessible(true);

			// Получаем значение поля
			Object fieldValue = field.get(dto);

			// Проверяем наличие аннотации NotNull
			if (field.isAnnotationPresent(NotNull.class) && (fieldValue == null)) {
				NotNull notNullAnno = field.getAnnotation(NotNull.class);
				violations.add(notNullAnno.message());

			}

			// Проверяем наличие аннотации Null
			if (field.isAnnotationPresent(Null.class) && (fieldValue != null)) {
				Null nullAnno = field.getAnnotation(Null.class);
				violations.add(nullAnno.message());

			}

			// Проверяем наличие аннотации Size
			if (field.isAnnotationPresent(Size.class) && (fieldValue != null)) {
				Size sizeAnno = field.getAnnotation(Size.class);
				int min = sizeAnno.min();
				int max = sizeAnno.max();

				int fieldSize = 0;
				if (!(fieldValue instanceof String fieldString)) {
					char[] fieldChars = (char[]) fieldValue;
					fieldSize = fieldChars.length;
				} else {
					fieldSize = fieldString.length();
				}

				if (fieldSize < min || fieldSize > max) {
					violations.add(sizeAnno.message());
				}

			}

			// Проверяем наличие аннотации NotBlank
			if (field.isAnnotationPresent(NotBlank.class)) {
				String fieldString = (String) fieldValue;
				if (fieldValue == null || fieldString.isBlank()) {
					NotBlank notBlankAnno = field.getAnnotation(NotBlank.class);
					violations.add(notBlankAnno.message());
				}
			}

			// Проверяем наличие аннотации Positive
			if (field.isAnnotationPresent(Positive.class) && (fieldValue != null)) {
				Long fieldNumber = (Long) fieldValue;
				if (fieldNumber <= 0) {
					Positive positiveAnno = field.getAnnotation(Positive.class);
					violations.add(positiveAnno.message());
				}

			}

			// Проверяем наличие аннотации Digits
			if (field.isAnnotationPresent(Digits.class) && (fieldValue != null)) {
				Digits digitsAnno = field.getAnnotation(Digits.class);
				int integer = digitsAnno.integer();
				int fraction = digitsAnno.fraction();

				String fieldString = (String) fieldValue;
				if (!fieldString.isBlank()) {
					// Digits применимо к BigDecimal
					try {
						BigDecimal fieldDecimal = new BigDecimal(fieldString);
						int fractionInput = fieldDecimal.scale();
						int integerInput = fieldDecimal.precision() - fractionInput;
						if (fractionInput > fraction || integerInput > integer) {
							violations.add(digitsAnno.message());
						}
					} catch (NumberFormatException e) {
						violations.add(digitsAnno.message());
					}
				}

			}
		}

		return violations;
	}

}
