/**
 * 
 */
package ru.alexnv.apps.wallet.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import ru.alexnv.apps.wallet.domain.dto.TransactionDto;
import ru.alexnv.apps.wallet.domain.model.Transaction;

/**
 * Интерфейс маппинга сущности транзакции в DTO
 */
@Mapper
public interface TransactionMapper {
	
	/** Создание маппера MapStruct. */
	TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
	
	/**
	 * Преобразование сущности транзакции в DTO.
	 *
	 * @param transaction сущность транзакция
	 * @return DTO транзакции
	 */
	TransactionDto toDto(Transaction transaction);
	
	/**
	 * Преобразование DTO в сущность транзакции.
	 *
	 * @param transactionDto DTO транзакции
	 * @return сущность транзакция
	 */
	@Mapping(target = "player", ignore = true)
	Transaction toEntity(TransactionDto transactionDto);

}
