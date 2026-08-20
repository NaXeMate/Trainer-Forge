package dev.trainerforge.performance;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
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

import dev.trainerforge.model.entities.Move;
import dev.trainerforge.model.entities.Pokemon;
import dev.trainerforge.model.entities.PokemonItem;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.repository.PokemonRepository;
import dev.trainerforge.repository.TeamRepository;
import jakarta.persistence.EntityManagerFactory;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(
        classes = DtoQueryPerformanceIntegrationTest.PersistenceTestConfiguration.class,
        properties = "spring.jpa.properties.hibernate.generate_statistics=true"
)
@Transactional(readOnly = true)
class DtoQueryPerformanceIntegrationTest {

    private static final long MAX_POKEMON_LIST_QUERIES = 10;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics statistics;

    @BeforeEach
    void resetQueryStatistics() {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        statistics = sessionFactory.getStatistics();
        statistics.clear();
    }

    @Test
    void resolvesTeamDtoReferencesWithOneQuery() {
        List<String> teamReferences = teamRepository.findAll().stream()
                .map(this::resolveTeamReferences)
                .toList();

        assertAll(
                () -> assertFalse(teamReferences.isEmpty()),
                () -> assertEquals(1, statistics.getPrepareStatementCount())
        );
    }

    @Test
    void resolvesPokemonDtoReferencesUsingBatchedLazyLoads() {
        List<String> pokemonReferences = pokemonRepository.findAll().stream()
                .map(this::resolvePokemonReferences)
                .toList();

        long queryCount = statistics.getPrepareStatementCount();

        assertAll(
                () -> assertFalse(pokemonReferences.isEmpty()),
                () -> assertTrue(
                        queryCount <= MAX_POKEMON_LIST_QUERIES,
                        () -> "Expected at most " + MAX_POKEMON_LIST_QUERIES
                                + " queries when mapping the Pokemon list, but executed " + queryCount
                )
        );
    }

    private String resolveTeamReferences(Team team) {
        return team.getTrainer().getUsername() + "|" + team.getVideogame().getName();
    }

    private String resolvePokemonReferences(Pokemon pokemon) {
        return pokemon.getSpecies().getId()
                + "|" + pokemon.getAbility().getName()
                + "|" + pokemon.getMove1().getName()
                + "|" + relationName(pokemon.getMove2())
                + "|" + relationName(pokemon.getMove3())
                + "|" + relationName(pokemon.getMove4())
                + "|" + relationName(pokemon.getEquippedItem())
                + "|" + pokemon.getNature().getName();
    }

    private String relationName(Move move) {
        return move == null ? "" : move.getName();
    }

    private String relationName(PokemonItem item) {
        return item == null ? "" : item.getName();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Pokemon.class)
    @EnableJpaRepositories(basePackageClasses = PokemonRepository.class)
    static class PersistenceTestConfiguration {
    }
}
