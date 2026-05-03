package dev.trainerforge.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.GenerationDto;
import dev.trainerforge.model.entities.Generation;
import dev.trainerforge.repository.GenerationRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class GenerationMapper {

    protected GenerationRepository generationRepo;

    public GenerationMapper(GenerationRepository generationRepo) {
        this.generationRepo = generationRepo;
    }

    public abstract Generation toEntity(GenerationDto generationDto);

    public abstract GenerationDto toDto(Generation generation);

    public abstract void updateEntityFromDto(GenerationDto generationDto, @MappingTarget Generation generation);

    protected Set<Generation> mapGeneration(Long[] generationsIds) {
        Set<Generation> generations = new java.util.HashSet<>();
        if (generationsIds != null) {
            for (Long id : generationsIds) {
                if (id != null) {
                    generationRepo.findById(id).ifPresent(generations::add);
                }
            }
        }
        return generations;
    }

    protected Long[] mapGenerationsIds(Set<Generation> generations) {
        if (generations == null || generations.isEmpty()) {
            return new Long[0];
        }
        return generations.stream().map(Generation::getId).toArray(Long[]::new);
    }
}
