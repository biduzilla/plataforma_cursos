package org.acme.plataforma.courses.application.service;

import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.domain.entity.User;

public interface TokenService {
    public String generateAccessToken(User user);
    public String generateRefreshToken(User user);
    public TokenPair rotateRefreshToken(String rawToken);
    public void revokeRefreshToken(String rawToken);
}
