package org.acme.plataforma.courses.models;

import jakarta.persistence.*;
import org.acme.plataforma.courses.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    public UUID id;

    @Column(nullable = false)
    public UUID userId;

    @Column(nullable = false)
    public UUID courseId;

    @Column(nullable = false)
    public String courseTitle;

    @Column(nullable = false)
    public BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public OrderStatus status = OrderStatus.PENDING;

    public LocalDateTime createdAt = LocalDateTime.now();
    public LocalDateTime updatedAt = LocalDateTime.now();

    public LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
}
