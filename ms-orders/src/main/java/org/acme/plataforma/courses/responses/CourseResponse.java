package org.acme.plataforma.courses.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        BigDecimal price,
        String status
) {
}
