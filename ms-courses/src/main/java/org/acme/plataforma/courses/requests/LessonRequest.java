package org.acme.plataforma.courses.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,

        String videoUrl,
        Integer durationSeconds,

        @NotNull(message = "Ordem é obrigatória")
        Integer order
) {
}