package dev.trainerforge.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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

import dev.trainerforge.model.entities.PokemonTeam;
import dev.trainerforge.model.entities.Team;
import dev.trainerforge.model.enumerated.TeamModality;
import jakarta.persistence.EntityManager;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = TeamVisibilityRepositoryIntegrationTest.PersistenceTestConfiguration.class)
@Transactional
class TeamVisibilityRepositoryIntegrationTest {

    private static final String RED_USERNAME = "red";
    private static final String LEAF_USERNAME = "leaf";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PokemonTeamRepository pokemonTeamRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void hideRedAndLeafTeams() {
        teamRepository.findById(1L).orElseThrow().setHidden(true);
        teamRepository.findById(2L).orElseThrow().setHidden(true);
        teamRepository.flush();
        entityManager.clear();
    }

    @Test
    void teamQueriesReturnPublicTeamsAndOnlyTheCurrentUsersHiddenTeams() {
        List<Long> redTeamIds = teamIds(teamRepository.findAllAccessibleTo(RED_USERNAME));
        List<Long> leafTeamIds = teamIds(teamRepository.findAllAccessibleTo(LEAF_USERNAME));

        assertAll(
                () -> assertTrue(redTeamIds.contains(1L)),
                () -> assertFalse(redTeamIds.contains(2L)),
                () -> assertFalse(leafTeamIds.contains(1L)),
                () -> assertTrue(leafTeamIds.contains(2L)),
                () -> assertTrue(teamRepository.findAccessibleById(1L, RED_USERNAME).isPresent()),
                () -> assertTrue(teamRepository.findAccessibleById(2L, RED_USERNAME).isEmpty()),
                () -> assertEquals(
                        List.of(),
                        teamIds(teamRepository.findAccessibleByTrainerId(2L, RED_USERNAME))
                ),
                () -> assertEquals(
                        List.of(2L),
                        teamIds(teamRepository.findAccessibleByVideogameId(11L, LEAF_USERNAME))
                ),
                () -> assertEquals(
                        List.of(4L),
                        teamIds(teamRepository.findAccessibleByModality(TeamModality.NORMAL, RED_USERNAME))
                ),
                () -> assertEquals(
                        List.of(1L),
                        teamIds(teamRepository.findAccessibleByIsHidden(true, RED_USERNAME))
                ),
                () -> assertEquals(
                        List.of(2L),
                        teamIds(teamRepository.findAccessibleByIsHidden(true, LEAF_USERNAME))
                )
        );
    }

    @Test
    void pokemonTeamQueriesApplyTheSameVisibilityRuleInTheDatabase() {
        List<Long> redAssociationTeamIds = associationTeamIds(
                pokemonTeamRepository.findAllAccessibleToOrderByTeamIdAscPositionAsc(RED_USERNAME)
        );
        List<Long> redPositionOneTeamIds = associationTeamIds(
                pokemonTeamRepository.findAccessibleByPosition(1, RED_USERNAME)
        );
        List<Long> leafPositionOneTeamIds = associationTeamIds(
                pokemonTeamRepository.findAccessibleByPosition(1, LEAF_USERNAME)
        );

        assertAll(
                () -> assertTrue(redAssociationTeamIds.contains(1L)),
                () -> assertFalse(redAssociationTeamIds.contains(2L)),
                () -> assertEquals(
                        List.of(),
                        associationIds(pokemonTeamRepository.findAccessibleByPokemonId(4L, RED_USERNAME))
                ),
                () -> assertEquals(
                        List.of(4L),
                        associationIds(pokemonTeamRepository.findAccessibleByPokemonId(4L, LEAF_USERNAME))
                ),
                () -> assertTrue(redPositionOneTeamIds.contains(1L)),
                () -> assertFalse(redPositionOneTeamIds.contains(2L)),
                () -> assertFalse(leafPositionOneTeamIds.contains(1L)),
                () -> assertTrue(leafPositionOneTeamIds.contains(2L))
        );
    }

    private List<Long> teamIds(List<Team> teams) {
        return teams.stream().map(team -> team.getId()).sorted().toList();
    }

    private List<Long> associationIds(List<PokemonTeam> associations) {
        return associations.stream().map(association -> association.getId()).sorted().toList();
    }

    private List<Long> associationTeamIds(List<PokemonTeam> associations) {
        return associations.stream().map(association -> association.getTeam().getId()).toList();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Team.class)
    @EnableJpaRepositories(basePackageClasses = TeamRepository.class)
    static class PersistenceTestConfiguration {
    }
}
