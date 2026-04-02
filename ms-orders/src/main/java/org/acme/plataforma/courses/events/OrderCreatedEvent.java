package org.acme.plataforma.courses.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID userId,
        UUID courseId,
        String courseTitle,
        BigDecimal amount,
        LocalDateTime createdAt
) {}
