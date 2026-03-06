package com.marketflow.service;


import com.marketflow.domain.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @ConfigProperty(name = "marketflow.jwt.access-token.expiration", defaultValue = "3600")
    long accessTokenExpiration;

    @ConfigProperty(name = "marketflow.jwt.refresh-token.expiration", defaultValue = "604800")
    long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        return Jwt.issuer(issuer)
                .subject(user.id.toString())
                .groups(Set.of(user.role.name()))
                .claim("email", user.email)
                .claim("name", user.name)
                .claim("role", user.role.name())
                .issuedAt(Instant.now())
                .expiresIn(Duration.ofSeconds(accessTokenExpiration))
                .sign();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID()
                + "-"
                + UUID.randomUUID()
                + "-"
                + Instant.now().toEpochMilli();
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpiration;
    }
}