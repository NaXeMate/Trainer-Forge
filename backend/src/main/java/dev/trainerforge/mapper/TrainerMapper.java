package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.TrainerDto;
import dev.trainerforge.model.entities.Trainer;
import dev.trainerforge.repository.TrainerRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TrainerMapper {

    protected TrainerRepository trainerRepo;

    public TrainerMapper(TrainerRepository trainerRepo) {
        this.trainerRepo = trainerRepo;
    }

    public abstract Trainer toEntity(TrainerDto trainerDto);
    
    public abstract TrainerDto toDto(Trainer trainer);
    
    public abstract void updateEntityFromDto(TrainerDto trainerDto, @MappingTarget Trainer trainer);

    protected Set<Trainer> mapTrainer(Long[] trainersIds) {
        Set<Trainer> trainers = new HashSet<>();
        if (trainersIds != null) {
            for (Long id : trainersIds) {
                if (id != null) {
                    trainerRepo.findById(id).ifPresent(trainers::add);
                }
            }
        }
        return trainers;
    }

    protected Long[] mapTrainersIds(Set<Trainer> trainers) {
        if (trainers == null || trainers.isEmpty()) {
            return new Long[0];
        }
        return trainers.stream().map(Trainer::getId).toArray(Long[]::new);
    }
}
