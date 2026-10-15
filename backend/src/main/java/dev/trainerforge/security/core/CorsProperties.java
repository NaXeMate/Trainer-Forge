package dev.trainerforge.security.core;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * CORS configuration.
 *
 * @param allowedOrigins exact frontend origins allowed to access the API from a browser
 */
@ConfigurationProperties("trainerforge.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
