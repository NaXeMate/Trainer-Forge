package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.PokedexDto;
import dev.trainerforge.model.entities.Pokedex;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface PokedexMapper {

    // Database identity is exposed for reads, never assigned from a response DTO.
    @Mapping(target = "id", ignore = true)
    Pokedex toEntity(PokedexDto pokedexDto);

    PokedexDto toDto(Pokedex pokedex);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PokedexDto pokedexDto, @MappingTarget Pokedex pokedex);
}
