package dev.trainerforge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.TypeEffectiveness;

public interface TypeEffectivenessRepository extends JpaRepository<TypeEffectiveness, Long> {
    Optional<TypeEffectiveness> findByAttackingTypeAndDefendingType(String attackingTypeName, String defendingTypeName);
}
