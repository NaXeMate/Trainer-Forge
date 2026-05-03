package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveSecondaryEffectDto;
import dev.trainerforge.model.entities.MoveSecondaryEffect;
import dev.trainerforge.repository.MoveSecondaryEffectRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MoveSecondaryEffectMapper {

    protected MoveSecondaryEffectRepository moveSecondaryEffectRepo;

    public MoveSecondaryEffectMapper(MoveSecondaryEffectRepository moveSecondaryEffectRepo) {
        this.moveSecondaryEffectRepo = moveSecondaryEffectRepo;
    }

    public abstract MoveSecondaryEffect toEntity(MoveSecondaryEffectDto secondaryEffectDto);

    public abstract MoveSecondaryEffectDto toDto(MoveSecondaryEffect secondaryEffect);

    public abstract void updateEntityFromDto(MoveSecondaryEffectDto secondaryEffectDto, @MappingTarget MoveSecondaryEffect secondaryEffect);

    protected Set<MoveSecondaryEffect> mapSecondaryEffects(Long[] secondaryEffectsIds) {
        Set<MoveSecondaryEffect> secondaryEffects = new HashSet<>();
        if (secondaryEffectsIds != null) {
            for (Long id : secondaryEffectsIds) {
                if (id != null) {
                    moveSecondaryEffectRepo.findById(id).ifPresent(secondaryEffects::add);
                }
            }
        }
        return secondaryEffects;
    }

    protected Long[] mapSecondaryEffectsIds(Set<MoveSecondaryEffect> secondaryEffects) {
        if (secondaryEffects == null || secondaryEffects.isEmpty()) {
            return new Long[0];
        }
        return secondaryEffects.stream().map(MoveSecondaryEffect::getId).toArray(Long[]::new);
    }
}
