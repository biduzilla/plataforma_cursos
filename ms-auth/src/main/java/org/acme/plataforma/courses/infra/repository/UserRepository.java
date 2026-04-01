package org.acme.plataforma.courses.infra.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.acme.plataforma.courses.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends PanacheRepositoryBase<User, UUID> {
    public Optional<User> findByEmail(String email);

    public boolean existsByEmail(String email);
}
