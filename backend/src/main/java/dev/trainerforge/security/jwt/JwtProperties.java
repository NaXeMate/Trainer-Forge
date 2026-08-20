package dev.trainerforge.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * JWT configuration.
 *
 * @param secret Base64-encoded signing key with at least 32 decoded bytes
 * @param expirationMs token lifetime in milliseconds
 */
@ConfigurationProperties("trainerforge.jwt")
public record JwtProperties(String secret, @DefaultValue("86400000") long expirationMs) {
}
