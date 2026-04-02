package org.acme.plataforma.courses.responses;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
    public ErrorResponse(String message) {
        this(message, List.of(), LocalDateTime.now());
    }

    public ErrorResponse(String message, List<String> details) {
        this(message, details, LocalDateTime.now());
    }
}
