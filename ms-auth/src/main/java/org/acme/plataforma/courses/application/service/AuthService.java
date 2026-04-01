package org.acme.plataforma.courses.application.service;

import org.acme.plataforma.courses.application.dto.LoginRequest;
import org.acme.plataforma.courses.application.dto.RegisterRequest;
import org.acme.plataforma.courses.application.dto.TokenPair;

public interface AuthService {
    public TokenPair login(LoginRequest req);
    public TokenPair register(RegisterRequest req);
}
