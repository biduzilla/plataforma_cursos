package org.acme.plataforma.courses.responses;

import java.util.List;
import java.util.UUID;

public record ModuleResponse(
        UUID id,
        String title,
        Integer order,
        List<LessonResponse> lessons
) {}
