package dev.trainerforge.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import dev.trainerforge.dto.response.PokedexDto;
import dev.trainerforge.model.entities.Pokedex;

class PokedexMapperTest {

    private final PokedexMapper mapper = new PokedexMapperImpl(mock(EntityReferenceMapper.class));

    @Test
    void toDtoDistinguishesDatabaseIdentityFromNationalNumber() {
        Pokedex species = new Pokedex();
        species.setId(42L);
        species.setNationalPokedex(152L);

        PokedexDto dto = mapper.toDto(species);

        assertEquals(42L, dto.id());
        assertEquals(152L, dto.nationalPokedex());
    }

    @Test
    void toEntityDoesNotAssignDatabaseIdentityFromTheResponse() {
        Pokedex source = new Pokedex();
        source.setId(99L);
        source.setName("Chikorita");

        Pokedex created = mapper.toEntity(mapper.toDto(source));

        assertNull(created.getId());
        assertEquals("Chikorita", created.getName());
    }

    @Test
    void updatePreservesIdentityWhenTheResponseContainsAnotherId() {
        Pokedex source = new Pokedex();
        source.setId(99L);
        source.setName("Chikorita");
        Pokedex target = new Pokedex();
        target.setId(42L);

        mapper.updateEntityFromDto(mapper.toDto(source), target);

        assertEquals(42L, target.getId());
        assertEquals("Chikorita", target.getName());
    }
}
