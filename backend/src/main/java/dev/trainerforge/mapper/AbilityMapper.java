package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.AbilityDto;
import dev.trainerforge.model.entities.Ability;
import dev.trainerforge.repository.AbilityRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AbilityMapper {

    protected AbilityRepository abilityRepo;

    public AbilityMapper(AbilityRepository abilityRepo) {
        this.abilityRepo = abilityRepo;
    }

    public abstract Ability toEntity(AbilityDto abilityDto);

    public abstract AbilityDto toDto(Ability ability);

    public abstract void updateEntityFromDto(AbilityDto abilityDto, @MappingTarget Ability ability);

    protected Set<Ability> mapAbilities(Long[] abilitiesIds) {
        Set<Ability> abilities = new HashSet<>();
        if (abilitiesIds != null) {
            for (Long id : abilitiesIds) {
                if (id != null) {
                    abilityRepo.findById(id).ifPresent(abilities::add);
                }
            }
        }
        return abilities;
    }

    protected Long[] mapAbilitiesIds(Set<Ability> abilities) {
        if (abilities == null || abilities.isEmpty()) {
            return new Long[0];
        }
        return abilities.stream().map(Ability::getId).toArray(Long[]::new);
    }
}
