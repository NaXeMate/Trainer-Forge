package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.NatureDto;
import dev.trainerforge.model.entities.Nature;
import dev.trainerforge.repository.NatureRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class NatureMapper {

    protected NatureRepository natureRepo;

    public NatureMapper(NatureRepository natureRepo) {
        this.natureRepo = natureRepo;
    }

    public abstract Nature toEntity(NatureDto natureDto);

    public abstract NatureDto toDto(Nature nature);

    public abstract void updateEntityFromDto(NatureDto natureDto, @MappingTarget Nature nature);

    protected Set<Nature> mapNatures(Long[] natureIds) {
        Set<Nature> natures = new HashSet<>();
        if (natureIds != null) {
            for (Long id : natureIds) {
                if (id != null) {
                    natureRepo.findById(id).ifPresent(natures::add);
                }
            }
        }
        return natures;
    }

    protected Long[] mapNatureIds(Set<Nature> natures) {
        if (natures == null || natures.isEmpty()) {
            return new Long[0];
        }
        return natures.stream().map(Nature::getId).toArray(Long[]::new);
    }
}
