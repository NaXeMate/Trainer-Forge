package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.RegionDto;
import dev.trainerforge.model.entities.Region;
import dev.trainerforge.repository.RegionRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RegionMapper {

    protected RegionRepository regionRepo;

    public RegionMapper(RegionRepository regionRepo) {
        this.regionRepo = regionRepo;
    }

    public abstract Region toEntity(RegionDto regionDto);

    public abstract RegionDto toDto(Region region);

    public abstract void updateEntityFromDto(RegionDto regionDto, @MappingTarget Region region);

    protected Set<Region> mapRegion(Long[] regionsIds) {
        Set<Region> regions = new HashSet<>();
        if (regionsIds != null) {
            for (Long id : regionsIds) {
                if (id != null) {
                    regionRepo.findById(id).ifPresent(regions::add);
                }
            }
        }
        return regions;
    }

    protected Long[] mapRegionsIds(Set<Region> regions) {
        if (regions == null || regions.isEmpty()) {
            return new Long[0];
        }
        return regions.stream().map(Region::getId).toArray(Long[]::new);
    }
}
