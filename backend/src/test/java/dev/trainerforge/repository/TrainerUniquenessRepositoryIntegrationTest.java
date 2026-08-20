package dev.trainerforge.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.trainerforge.model.entities.Trainer;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = TrainerUniquenessRepositoryIntegrationTest.PersistenceTestConfiguration.class)
@Transactional(readOnly = true)
class TrainerUniquenessRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void detectsExistingAndAvailableUniqueTrainerFields() {
        assertAll(
                () -> assertTrue(trainerRepository.existsByUsername("red")),
                () -> assertFalse(trainerRepository.existsByUsername("new-trainer")),
                () -> assertTrue(trainerRepository.existsByEmail("red@trainerforge.test")),
                () -> assertFalse(trainerRepository.existsByEmail("new@trainerforge.test")),
                () -> assertTrue(trainerRepository.existsByFriendCode("TF-0000-0001")),
                () -> assertFalse(trainerRepository.existsByFriendCode("TF-9999-9999"))
        );
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Trainer.class)
    @EnableJpaRepositories(basePackageClasses = TrainerRepository.class)
    static class PersistenceTestConfiguration {
    }
}
