package dev.trainerforge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
class TrainerForgeApplicationTests {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer postgresql = new PostgreSQLContainer("postgres:18");
	
	@Test
	void contextLoads() {}
}
