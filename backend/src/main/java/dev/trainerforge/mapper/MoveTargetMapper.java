package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveTargetDto;
import dev.trainerforge.model.entities.MoveTarget;
import dev.trainerforge.repository.MoveTargetRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MoveTargetMapper {

    protected MoveTargetRepository moveTargetRepo;

    public MoveTargetMapper(MoveTargetRepository moveTargetRepo) {
        this.moveTargetRepo = moveTargetRepo;
    }

    public abstract MoveTarget toEntity(MoveTargetDto moveTargetDto);
    
    public abstract MoveTargetDto toDto(MoveTarget moveTarget);

    public abstract void updateEntityFromDto(MoveTargetDto moveTargetDto, @MappingTarget MoveTarget moveTarget);

    protected Set<MoveTarget> mapMoveTargets(Long[] moveTargetIds) {
        Set<MoveTarget> moveTargets = new HashSet<>();
        if (moveTargetIds != null) {
            for (Long id : moveTargetIds) {
                if (id != null) {
                    moveTargetRepo.findById(id).ifPresent(moveTargets::add);
                }
            }
        }
        return moveTargets;
    }

    protected Long[] mapMoveTargetIds(Set<MoveTarget> moveTargets) {
        if (moveTargets == null || moveTargets.isEmpty()) {
            return new Long[0];
        }
        return moveTargets.stream().map(MoveTarget::getId).toArray(Long[]::new);
    }
    
}
