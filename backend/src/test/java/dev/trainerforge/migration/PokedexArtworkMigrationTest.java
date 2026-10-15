package dev.trainerforge.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
class PokedexArtworkMigrationTest {

    private static final String ARTWORK_BASE =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/";

    @Container
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @Test
    void upgradesExistingSpritesWhilePreservingCustomUrlsFormsAndOtherData() throws SQLException {
        Flyway previousVersion = Flyway.configure()
                .dataSource(postgresql.getJdbcUrl(), postgresql.getUsername(), postgresql.getPassword())
                .target("11")
                .load();
        previousVersion.migrate();

        try (Connection connection = previousVersion.getConfiguration().getDataSource().getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("""
                        UPDATE pokedex SET image_url = CASE id
                            WHEN 2 THEN 'https://example.com/custom-bayleef.png'
                            WHEN 3 THEN 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/154.png'
                            WHEN 4 THEN 'https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/10001.png'
                        END WHERE id IN (2, 3, 4)
                        """);
            }
            List<CatalogEntry> before = readCatalog(connection);

            Flyway artworkVersion = Flyway.configure()
                    .dataSource(previousVersion.getConfiguration().getDataSource())
                    .target("12")
                    .load();
            assertEquals(1, artworkVersion.migrate().migrationsExecuted);

            List<CatalogEntry> after = readCatalog(connection);
            assertEquals(before.size(), after.size());
            for (int index = 0; index < before.size(); index++) {
                CatalogEntry original = before.get(index);
                CatalogEntry migrated = after.get(index);
                assertEquals(original.otherFields(), migrated.otherFields());

                String expectedUrl = switch ((int) migrated.id()) {
                    case 2, 3 -> original.imageUrl();
                    case 4 -> ARTWORK_BASE + "10001.png";
                    default -> ARTWORK_BASE + migrated.nationalNumber() + ".png";
                };
                assertEquals(expectedUrl, migrated.imageUrl());
            }
            assertTrue(artworkVersion.validateWithResult().validationSuccessful);
            assertEquals(0, artworkVersion.migrate().migrationsExecuted);
        }
    }

    private List<CatalogEntry> readCatalog(Connection connection) throws SQLException {
        List<CatalogEntry> entries = new ArrayList<>();
        try (var statement = connection.createStatement();
                var result = statement.executeQuery("""
                        SELECT id, national_pokedex, image_url, (to_jsonb(p) - 'image_url')::text AS other_fields
                        FROM pokedex p ORDER BY id
                        """)) {
            while (result.next()) {
                entries.add(new CatalogEntry(result.getLong("id"), result.getLong("national_pokedex"),
                        result.getString("image_url"), result.getString("other_fields")));
            }
        }
        return entries;
    }

    private record CatalogEntry(long id, long nationalNumber, String imageUrl, String otherFields) {}
}
