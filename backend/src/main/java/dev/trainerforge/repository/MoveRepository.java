package dev.trainerforge.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Move;
import dev.trainerforge.model.enumerated.MoveClass;

public interface MoveRepository extends JpaRepository<Move, Long> {
    Optional<Move> findByName(String name);
    List<Move> findByTypeId(Long typeId);
    List<Move> findByMoveClass(MoveClass moveClass);
    List<Move> findByPower(int power);
    List<Move> findByPowerBetween(int minPower, int maxPower);
    List<Move> findByAccuracy(int accuracy);
    List<Move> findByAccuracyBetween(int minAccuracy, int maxAccuracy);
    List<Move> findByContact(boolean contact);
    List<Move> findByPriority(int priority);
    List<Move> findByPriorityBetween(int minPriority, int maxPriority);
    List<Move> findByTargetId(Long targetId);
    List<Move> findBySecondaryEffectId(Long secondaryEffectId);
    List<Move> findByPp(int pp);
    List<Move> findByPpBetween(int minPp, int maxPp);
    List<Move> findByGenerationId(Long generationId);
}
