package org.acme.plataforma.courses.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseArchivedEvent(
        UUID courseId,
        String courseTitle,
        LocalDateTime archivedAt
) {}
