package org.acme.plataforma.courses.responses;

import java.util.UUID;

public record LessonResponse(
        UUID id,
        String title,
        String videoUrl,
        Integer durationSeconds,
        Integer order
) {}
