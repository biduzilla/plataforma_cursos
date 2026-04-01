package org.acme.plataforma.courses.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModuleRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,

        @NotNull(message = "Ordem é obrigatória")
        Integer order
) {}
