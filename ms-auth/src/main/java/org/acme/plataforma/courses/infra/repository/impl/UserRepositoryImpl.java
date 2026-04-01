package org.acme.plataforma.courses.infra.repository.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.plataforma.courses.domain.entity.User;
import org.acme.plataforma.courses.infra.repository.UserRepository;

import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Override
    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
}
