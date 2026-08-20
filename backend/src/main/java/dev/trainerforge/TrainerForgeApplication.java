package dev.trainerforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrainerForgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainerForgeApplication.class, args);
	}
}
