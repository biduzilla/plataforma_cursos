package org.acme.plataforma.courses.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CourseRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,

        String description,

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.0", message = "Preço não pode ser negativo")
        BigDecimal price
) {
}