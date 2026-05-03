package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TypeEffectivenessDto;
import dev.trainerforge.model.entities.TypeEffectiveness;
import dev.trainerforge.repository.TypeEffectivenessRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TypeEffectivenessMapper {

    protected TypeEffectivenessRepository typeEffectivenessRepo;

    public TypeEffectivenessMapper(TypeEffectivenessRepository typeEffectivenessRepo) {
        this.typeEffectivenessRepo = typeEffectivenessRepo;
    }

    public abstract TypeEffectiveness toEntity(TypeEffectivenessDto typeEffectivenessDto);
    
    public abstract TypeEffectivenessDto toDto(TypeEffectiveness typeEffectiveness);
    
    public abstract void updateEntityFromDto(TypeEffectivenessDto typeEffectivenessDto, @MappingTarget TypeEffectiveness typeEffectiveness);

    protected Set<TypeEffectiveness> mapTypeEffectiveness(Long[] typeEffectivenessIds) {
        Set<TypeEffectiveness> typeEffectivenessSet = new HashSet<>();
        if (typeEffectivenessIds != null) {
            for (Long id : typeEffectivenessIds) {
                if (id != null) {
                    typeEffectivenessRepo.findById(id).ifPresent(typeEffectivenessSet::add);
                }
            }
        }
        return typeEffectivenessSet;
    }

    protected Long[] mapTypeEffectivenessIds(Set<TypeEffectiveness> typeEffectivenessSet) {
        if (typeEffectivenessSet == null || typeEffectivenessSet.isEmpty()) {
            return new Long[0];
        }
        return typeEffectivenessSet.stream().map(TypeEffectiveness::getId).toArray(Long[]::new);
    }
}
