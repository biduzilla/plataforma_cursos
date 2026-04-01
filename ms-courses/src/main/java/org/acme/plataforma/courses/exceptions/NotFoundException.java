package org.acme.plataforma.courses.exceptions;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
    public NotFoundException(UUID id) {
        super("Registro não encontrado com ID: " + id);
    }
}
