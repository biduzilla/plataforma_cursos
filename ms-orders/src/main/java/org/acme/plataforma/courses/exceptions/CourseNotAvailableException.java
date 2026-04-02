package org.acme.plataforma.courses.exceptions;

public class CourseNotAvailableException extends RuntimeException {
    public CourseNotAvailableException(String message) {
        super(message);
    }
}
