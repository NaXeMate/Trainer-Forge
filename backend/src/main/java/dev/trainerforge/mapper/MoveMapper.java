package dev.trainerforge.mapper;

import java.util.HashSet;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import dev.trainerforge.dto.response.MoveDto;
import dev.trainerforge.model.entities.Move;
import dev.trainerforge.repository.MoveRepository;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MoveMapper {

    protected MoveRepository moveRepo;

    public MoveMapper(MoveRepository moveRepo) {
        this.moveRepo = moveRepo;
    }

    public abstract Move toEntity(MoveDto moveDto);

    public abstract MoveDto toDto(Move move);

    public abstract void updateEntityFromDto(MoveDto moveDto, @MappingTarget Move move);

    protected Set<Move> mapMove(Long[] movesIds) {
        Set<Move> moves = new HashSet<>();
        if (movesIds != null) {
            for (Long id : movesIds) {
                if (id != null) {
                    moveRepo.findById(id).ifPresent(moves::add);
                }
            }
        }
        return moves;
    }

    protected Long[] mapMovesIds(Set<Move> moves) {
        if (moves == null || moves.isEmpty()) {
            return new Long[0];
        }
        return moves.stream().map(Move::getId).toArray(Long[]::new);
    }
}
