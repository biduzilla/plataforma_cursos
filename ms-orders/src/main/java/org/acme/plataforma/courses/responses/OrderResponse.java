package org.acme.plataforma.courses.responses;

import org.acme.plataforma.courses.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        UUID courseId,
        String courseTitle,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {}
