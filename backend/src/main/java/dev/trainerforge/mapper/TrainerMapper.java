package dev.trainerforge.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.input.TrainerInputDto;
import dev.trainerforge.dto.response.TrainerDto;
import dev.trainerforge.model.entities.Trainer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EntityReferenceMapper.class})
public interface TrainerMapper {

    @Mapping(source = "bestFriendUsername", target = "bestFriend")
    Trainer toEntity(TrainerDto trainerDto);

    @Mapping(source = "bestFriend.username", target = "bestFriendUsername")
    TrainerDto toDto(Trainer trainer);

    @Mapping(source = "bestFriendUsername", target = "bestFriend")
    void updateEntityFromDto(TrainerInputDto trainerDto, @MappingTarget Trainer trainer);
}
