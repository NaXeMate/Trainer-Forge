package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.dto.response.TrainerDto;
import dev.trainerforge.model.entities.Trainer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TrainerMapper {

    Trainer toEntity(TrainerDto trainerDto);

    TrainerDto toDto(Trainer trainer);

    void updateEntityFromDto(TrainerInputDto trainerDto, @MappingTarget Trainer trainer);
}
