package org.acme.plataforma.courses.domain.entity;

import jakarta.persistence.*;
import org.acme.plataforma.courses.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;
    @Column(nullable = false)
    public String name;
    @Column(unique = true, nullable = false)
    public String email;
    @Column(nullable = false)
    public String passwordHash;
    @Enumerated(EnumType.STRING)
    public Role role = Role.STUDENT;
    public LocalDateTime createdAt = LocalDateTime.now();
}
