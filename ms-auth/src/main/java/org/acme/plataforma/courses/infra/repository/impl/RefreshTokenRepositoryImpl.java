package org.acme.plataforma.courses.infra.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.domain.entity.RefreshToken;
import org.acme.plataforma.courses.infra.repository.RefreshTokenRepository;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    @Override
    public Optional<RefreshToken> findByTokenHash(String hash) {
        return find("tokenHash", hash).firstResultOptional();
    }

    @Override
    public void revokeAllFromUser(UUID userId) {
        update("used = true where user.id = ?1", userId);
    }
}
