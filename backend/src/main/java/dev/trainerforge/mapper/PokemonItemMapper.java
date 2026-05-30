package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokemonItemDto;
import dev.trainerforge.model.entities.PokemonItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokemonItemMapper {

    PokemonItem toEntity(PokemonItemDto pokemonItemDto);

    PokemonItemDto toDto(PokemonItem pokemonItem);

    void updateEntityFromDto(PokemonItemDto pokemonItemDto, @MappingTarget PokemonItem pokemonItem);
}
