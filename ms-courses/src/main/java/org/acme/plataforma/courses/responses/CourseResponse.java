package org.acme.plataforma.courses.responses;

import org.acme.plataforma.courses.enums.CourseStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        String description,
        BigDecimal price,
        UUID instructorId,
        CourseStatusEnum status,
        List<ModuleResponse> modules,
        LocalDateTime createdAt
) {}
