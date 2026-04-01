package org.acme.plataforma.courses.application.service.impl;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.application.service.TokenService;
import org.acme.plataforma.courses.domain.entity.RefreshToken;
import org.acme.plataforma.courses.domain.entity.User;
import org.acme.plataforma.courses.infra.exception.UnauthorizedException;
import org.acme.plataforma.courses.infra.repository.RefreshTokenRepository;
import org.acme.plataforma.courses.infra.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class TokenServiceImpl implements TokenService {
    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Override
    public String generateAccessToken(User user) {
        return Jwt.issuer("ms-auth")
                .subject(user.id.toString())
                .groups(user.role.name())
                .claim("email", user.email)
                .claim("name", user.name)
                .expiresIn(Duration.ofMinutes(15))
                .sign();
    }

    @Override
    @Transactional
    public String generateRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString()
                + UUID.randomUUID().toString();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.user = user;
        refreshToken.tokenHash = tokenHash;
        refreshToken.expiresAt = LocalDateTime.now().plusDays(7);

        refreshTokenRepository.persist(refreshToken);
        return rawToken;
    }

    @Override
    @Transactional
    public TokenPair rotateRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        RefreshToken stored = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Token inválido ou expirado"));
        if (!stored.isValid()) {
            refreshTokenRepository.revokeAllFromUser(stored.user.id);
            throw new UnauthorizedException("Token inválido ou expirado");
        }

        stored.used = true;

        String newAccessToken = generateAccessToken(stored.user);
        String newRefreshToken = generateRefreshToken(stored.user);
        return new TokenPair(newAccessToken,newRefreshToken);
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) {
        String tokenHash = hashToken(rawToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(t->t.used = true);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash do token");
        }
    }
}
