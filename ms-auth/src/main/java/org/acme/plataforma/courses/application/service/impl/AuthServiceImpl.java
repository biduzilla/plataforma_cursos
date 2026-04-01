package org.acme.plataforma.courses.application.service.impl;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.plataforma.courses.application.dto.LoginRequest;
import org.acme.plataforma.courses.application.dto.RegisterRequest;
import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.application.service.AuthService;
import org.acme.plataforma.courses.application.service.TokenService;
import org.acme.plataforma.courses.domain.entity.User;
import org.acme.plataforma.courses.infra.exception.BadRequestException;
import org.acme.plataforma.courses.infra.exception.UnauthorizedException;
import org.acme.plataforma.courses.infra.repository.UserRepository;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {
    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    @Override
    public TokenPair login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

        if (!BcryptUtil.matches(req.password(), user.passwordHash)) {
            throw new UnauthorizedException("Credenciais inválidas");
        }
        return buildTokenPair(user);
    }

    @Override
    @Transactional
    public TokenPair register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("Email já cadastrado");
        }

        validatePassword(req.password());

        User user = new User();
        user.email = req.email();
        user.name = req.name();
        user.passwordHash = BcryptUtil.bcryptHash(req.password());

        userRepository.persist(user);

        return buildTokenPair(user);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new BadRequestException("Senha deve ter no mínimo 8 caracteres");
        }
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new BadRequestException("Senha deve conter letras");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new BadRequestException("Senha deve conter números");
        }
    }

    private TokenPair buildTokenPair(User user) {
        return new TokenPair(
                tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user)
        );
    }
}
