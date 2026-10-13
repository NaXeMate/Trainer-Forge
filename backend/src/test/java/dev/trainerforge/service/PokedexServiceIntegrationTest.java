package dev.trainerforge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.trainerforge.model.entities.Pokedex;
import jakarta.persistence.EntityManager;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PokedexServiceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Autowired
    private PokedexService service;

    @Autowired
    private EntityManager entityManager;

    @Test
    void listRemainsOrderedByDatabaseIdAfterAnEntryIsUpdated() {
        // PostgreSQL can change the physical row order after an update.
        // Use an isolated test database; this transaction is rolled back after the test.
        entityManager.createNativeQuery("UPDATE pokedex SET description = :description WHERE id = 1")
                .setParameter("description", "Updated description for the ordering regression test")
                .executeUpdate();
        entityManager.clear();

        List<Long> ids = service.findAll().stream().map(Pokedex::getId).toList();

        assertFalse(ids.isEmpty());
        assertEquals(ids.stream().sorted().toList(), ids);
        assertEquals(1L, ids.getFirst());
    }
}
