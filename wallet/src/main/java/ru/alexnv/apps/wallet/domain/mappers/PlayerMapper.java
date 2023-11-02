/**
 * 
 */
package ru.alexnv.apps.wallet.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ru.alexnv.apps.wallet.domain.dto.PlayerDto;
import ru.alexnv.apps.wallet.domain.model.Player;

/**
 * Интерфейс маппинга сущности игрока в DTO.
 */
@Mapper
public interface PlayerMapper {
	
	/** Создание маппера MapStruct. */
	PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);
	
	/**
	 * Преобразование сущности игрока в DTO.
	 *
	 * @param player сущность игрок
	 * @return DTO игрока
	 */
	PlayerDto toDto(Player player);
	
	/**
	 * Преобразование DTO в сущность игрока.
	 *
	 * @param playerDto DTO игрока
	 * @return сущность игрок
	 */
	Player toEntity(PlayerDto playerDto);
}
