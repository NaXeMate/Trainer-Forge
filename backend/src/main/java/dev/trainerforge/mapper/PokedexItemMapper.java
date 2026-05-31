package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokedexItemDto;
import dev.trainerforge.model.entities.PokedexItem;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokedexItemMapper {

    PokedexItem toEntity(PokedexItemDto pokedexItemDto);

    PokedexItemDto toDto(PokedexItem pokedexItem);

    void updateEntityFromDto(PokedexItemDto pokedexItemDto, @MappingTarget PokedexItem pokedexItem);
}
