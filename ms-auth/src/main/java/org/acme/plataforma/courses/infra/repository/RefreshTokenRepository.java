package org.acme.plataforma.courses.infra.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.domain.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends PanacheRepositoryBase<RefreshToken, UUID> {
    public Optional<RefreshToken> findByTokenHash(String hash);
    public void revokeAllFromUser(UUID userId);
}
