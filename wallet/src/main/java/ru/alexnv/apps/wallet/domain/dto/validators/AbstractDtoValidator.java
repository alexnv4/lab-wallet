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
	public AbstractDtoValidator() {
		violations = new ArrayList<String>();
	}
	
	/**
	 * Валидация. Переопределяется в дочерних классах.
	 *
	 * @param dto DTO
	 * @return список нарушений
	 */
	public abstract List<String> validate(AbstractDto dto);

}