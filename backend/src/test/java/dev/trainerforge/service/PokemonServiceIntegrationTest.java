package dev.trainerforge.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import dev.trainerforge.dto.response.PokemonDto;
import dev.trainerforge.mapper.PokemonMapper;
import dev.trainerforge.model.entities.Pokemon;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PokemonServiceIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Autowired
    private PokemonService pokemonService;

    @Autowired
    private PokemonMapper pokemonMapper;

    @Test
    void updatePreservesThePathIdentifierWhenTheDtoIdentifierIsNull() {
        PokemonDto current = pokemonMapper.toDto(pokemonService.findById(1L));
        PokemonDto update = new PokemonDto(
                null,
                current.species(),
                "Updated nickname",
                current.locationFound(),
                current.level(),
                current.shiny(),
                current.gender(),
                current.ability(),
                current.move1(),
                current.move2(),
                current.move3(),
                current.move4(),
                current.equippedItem(),
                current.nature(),
                current.hpEv(),
                current.attackEv(),
                current.defenseEv(),
                current.specialAttackEv(),
                current.specialDefenseEv(),
                current.speedEv()
        );

        Pokemon updated = pokemonService.updatePokemon(1L, update);

        assertAll(
                () -> assertEquals(1L, updated.getId()),
                () -> assertEquals("Updated nickname", updated.getNickname())
        );
    }
}
