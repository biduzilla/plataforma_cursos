package org.acme.plataforma.courses.exceptions;

import java.util.UUID;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
