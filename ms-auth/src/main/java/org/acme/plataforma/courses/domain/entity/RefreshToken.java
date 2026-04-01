package org.acme.plataforma.courses.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(nullable = false, unique = true)
    public String tokenHash;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(nullable = false)
    public LocalDateTime expiresAt;

    public boolean used = false;

    public LocalDateTime createdAt = LocalDateTime.now();

    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiresAt);
    }
}
