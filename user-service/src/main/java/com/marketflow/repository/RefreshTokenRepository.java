package com.marketflow.repository;

import com.marketflow.domain.entity.RefreshToken;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepositoryBase<RefreshToken, UUID> {

    public Optional<RefreshToken> findByToken(String token) {
        return find("token", token).firstResultOptional();
    }

    public void revokeAllByUserId(UUID userId) {
        update("revoked = true WHERE user.id = ?1 AND revoked = false", userId);
    }
}