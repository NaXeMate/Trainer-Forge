package dev.trainerforge.security.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.trainerforge.repository.TrainerRepository;
import dev.trainerforge.security.jwt.JwtProperties;
import dev.trainerforge.security.jwt.JwtUtil;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TrainerAuthenticationIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private TrainerRepository trainerRepository;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void registersLogsInAndPersistsOnlyTheRequestedProfileChanges() throws Exception {
        String username = "trainer-" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@example.test";
        String password = "Profile-test-123";
        String credentials = """
                {"username":"%s","password":"%s"}
                """.formatted(username, password);
        String registration = """
                {"username":"%s","email":"%s","password":"%s","favoriteGame":"Pokemon FireRed","trainerClass":"NOVICE"}
                """.formatted(username, email, password);
        String registrationToken = body(201, request("POST", "/api/auth/register", null, registration)).get("token").asText();
        HttpResponse<String> initialResponse = request("GET", "/api/trainers/me", registrationToken, null);
        JsonNode initialProfile = body(200, initialResponse);
        assertTrue(initialResponse.headers().firstValue("Cache-Control").orElseThrow().contains("no-store"));
        assertEquals("NOVICE", initialProfile.get("trainerClass").asText());
        assertFalse(initialProfile.has("passwordHash"));
        assertNotEquals(password, trainerRepository.findByUsername(username).orElseThrow().getPasswordHash());

        body(401, request("POST", "/api/auth/login", null, credentials.replace(password, "incorrect")));
        String loginToken = body(200, request("POST", "/api/auth/login", null, credentials)).get("token").asText();
        JsonNode updated = body(200, request("PATCH", "/api/trainers/me", loginToken, """
                {"realName":"  %s  ","trainerClass":"ACE_TRAINER","favoritePokemonId":1}
                """.formatted(username)));
        assertEquals(username, updated.get("realName").asText());
        assertEquals("ACE_TRAINER", updated.get("trainerClass").asText());
        assertEquals(1, updated.get("favoritePokemonId").asLong());
        assertEquals(initialProfile.get("username"), updated.get("username"));
        assertEquals(initialProfile.get("email"), updated.get("email"));
        assertEquals(initialProfile.get("favoriteGameName"), updated.get("favoriteGameName"));
        assertEquals(updated, body(200, request("GET", "/api/trainers/me", loginToken, null)));

        JsonNode cleared = body(200, request("PATCH", "/api/trainers/me", loginToken, """
                {"favoritePokemonId":null}
                """));
        assertTrue(cleared.get("favoritePokemonId").isNull());
        assertEquals(updated.get("realName"), cleared.get("realName"));
    }

    @Test
    void requiresAnExistingAuthenticatedAccountAndRejectsUsernameChanges() throws Exception {
        body(401, request("GET", "/api/trainers/me", null, null));
        body(401, request("PATCH", "/api/trainers/me", "invalid", "{}"));
        body(401, request("GET", "/api/trainers/me", jwt.generate("missing-test-trainer"), null));
        String expired = new JwtUtil(new JwtProperties(jwtProperties.secret(), -60_000)).generate("red");
        body(401, request("GET", "/api/trainers/me", expired, null));

        String red = jwt.generate("red");
        JsonNode profile = body(200, request("GET", "/api/trainers/me", red, null));
        String otherName = body(200, request("GET", "/api/trainers/me", jwt.generate("leaf"), null)).get("realName").asText();
        body(409, request("PATCH", "/api/trainers/me", red, """
                {"realName":"%s"}
                """.formatted(otherName)));
        body(400, request("PATCH", "/api/trainers/me", red, """
                {"realName":null,"username":"renamed"}
                """));
        body(400, request("PATCH", "/api/trainers/me", red, "{"));
        body(400, request("PUT", "/api/trainers/" + profile.get("id").asLong(), red, """
                {"username":"renamed","email":"%s","trainerClass":"CHAMPION"}
                """.formatted(profile.get("email").asText())));
        assertEquals(profile, body(200, request("GET", "/api/trainers/me", red, null)));
    }

    @Test
    void readsTheCompleteLineupWithItsPositionsAndVisibility() throws Exception {
        String red = jwt.generate("red");
        String leaf = jwt.generate("leaf");
        String lineup = """
                {
                  "name":"Sparse private team","modality":"NORMAL","gameId":1,"publicTeam":false,
                  "members":[null,null,null,{"pokedexId":53,"ability":"Static","moves":["Agility","","",""],
                    "stats":{"hp":0,"attack":0,"defense":0,"specialAttack":0,"specialDefense":0,"speed":32}},null,null]
                }
                """;
        JsonNode created = body(201, request("POST", "/api/teams/complete", red, lineup));
        String path = "/api/teams/" + created.get("id").asLong() + "/complete";

        body(401, request("GET", path, null, null));
        body(404, request("GET", path, leaf, null));
        JsonNode own = body(200, request("GET", path, red, null));
        assertEquals(created, own);
        JsonNode members = own.get("lineup").get("members");
        assertEquals(6, members.size());
        assertTrue(members.get(0).isNull());
        assertEquals(32, members.get(3).get("stats").get("speed").asInt());
        body(400, request("PUT", path, red, lineup.replace("\"speed\":32", "\"speed\":33")));
        assertEquals(own, body(200, request("GET", path, red, null)));

        JsonNode demo = body(200, request("GET", "/api/teams/1/complete", leaf, null));
        assertEquals("Kanto Classics", demo.get("lineup").get("name").asText());
        assertTrue(demo.get("lineup").get("publicTeam").asBoolean());
        assertFalse(demo.get("lineup").get("members").get(0).isNull());
    }

    private HttpResponse<String> request(String method, String path, String token, String payload) throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (token != null) request.header("Authorization", "Bearer " + token);
        if (payload != null) request.header("Content-Type", "application/json");
        request.method(method, payload == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(payload));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private JsonNode body(int expectedStatus, HttpResponse<String> response) throws Exception {
        assertEquals(expectedStatus, response.statusCode(), response::body);
        return json.readTree(response.body());
    }
}
