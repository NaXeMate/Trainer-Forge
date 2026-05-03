package dev.trainerforge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.Move;

public interface MoveRepository extends JpaRepository<Move, Long> {
    Move findByName(String name);
    List<Move> findByType(String type);
    List<Move> findByClass(String moveClass);
    List<Move> findByPower(int power);
    List<Move> findByPowerBetween(int minPower, int maxPower);
    List<Move> findByAccuracy(int accuracy);
    List<Move> findByAccuracyBetween(int minAccuracy, int maxAccuracy);
    List<Move> findByContact(boolean contact);
    List<Move> findByPriority(int priority);
    List<Move> findByPriorityBetween(int minPriority, int maxPriority);
    List<Move> findByTarget(String target);
    List<Move> findBySecondaryEffect(String secondaryEffect);
    List<Move> findByPp(int pp);
    List<Move> findByPpBetween(int minPp, int maxPp);
    List<Move> findByGeneration(String generation);
}
