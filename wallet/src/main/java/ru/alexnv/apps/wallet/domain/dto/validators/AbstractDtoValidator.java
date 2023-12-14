package ru.alexnv.apps.wallet.domain.dto.validators;

import java.util.ArrayList;
import java.util.List;

import ru.alexnv.apps.wallet.domain.dto.AbstractDto;

/**
 * Абстрактный валидатор DTO. Определяет метод validate(dto)
 */
public abstract class AbstractDtoValidator {

	/** Список нарушений валидации */
	protected List<String> violations;

	/**
	 * Создание DTO валидатора.
	 */
	protected AbstractDtoValidator() {
		violations = new ArrayList<>();
	}
	
	/**
	 * Валидация. Переопределяется в дочерних классах.
	 *
	 * @param dto DTO
	 * @return список нарушений
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public abstract List<String> validate(AbstractDto dto) throws IllegalArgumentException, IllegalAccessException;

}