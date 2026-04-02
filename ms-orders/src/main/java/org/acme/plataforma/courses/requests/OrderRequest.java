package org.acme.plataforma.courses.requests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRequest(
        @NotNull(message = "courseId é obrigatório")
        UUID courseId
) {}
