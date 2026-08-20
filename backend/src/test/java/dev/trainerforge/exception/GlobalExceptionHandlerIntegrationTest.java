package dev.trainerforge.exception;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

import dev.trainerforge.security.jwt.JwtUtil;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GlobalExceptionHandlerIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void unknownApiRouteReturnsNotFound() throws Exception {
        String path = "/api/route-that-does-not-exist";
        assertNotFound(getAsRed(path), path);
    }

    @Test
    void missingDomainResourceStillReturnsNotFound() throws Exception {
        String path = "/api/trainers/999999";
        assertNotFound(getAsRed(path), path);
    }

    private HttpResponse<String> getAsRed(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Authorization", "Bearer " + jwtUtil.generate("red"))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void assertNotFound(HttpResponse<String> response, String path) throws Exception {
        JsonNode error = objectMapper.readTree(response.body());

        assertAll(
                () -> assertEquals(404, response.statusCode()),
                () -> assertEquals(404, error.get("status").asInt()),
                () -> assertEquals("Not Found", error.get("error").asText()),
                () -> assertEquals(path, error.get("path").asText())
        );
    }
}
