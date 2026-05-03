package dev.trainerforge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.trainerforge.model.entities.MoveTarget;

public interface MoveTargetRepository extends JpaRepository<MoveTarget, Long> {}
