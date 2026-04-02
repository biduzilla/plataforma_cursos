package org.acme.plataforma.courses.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import org.acme.plataforma.courses.requests.ErrorResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

public class GlobalExceptionMapper {
    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleUnauthorized(
            io.quarkus.security.UnauthorizedException ex) {
        return RestResponse.status(Response.Status.UNAUTHORIZED,
                new ErrorResponse("Autenticação necessária"));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleForbidden(
            io.quarkus.security.ForbiddenException ex) {
        return RestResponse.status(Response.Status.FORBIDDEN,
                new ErrorResponse("Sem permissão"));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleOrderNotFound(
            OrderNotFoundException ex) {
        return RestResponse.status(Response.Status.NOT_FOUND,
                new ErrorResponse(ex.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleCancellation(
            OrderCancellationException ex) {
        return RestResponse.status(Response.Status.NOT_FOUND,
                new ErrorResponse(ex.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleCourseNotAvailable(
            CourseNotAvailableException ex) {
        return RestResponse.status(Response.Status.NOT_FOUND,
                new ErrorResponse(ex.getMessage()));
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleValidation(
            ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath()
                        .toString()
                        .replaceAll(".*\\.", "") + ": " + cv.getMessage())
                .sorted()
                .toList();
        return RestResponse.status(Response.Status.BAD_REQUEST,
                new ErrorResponse("Erro de validação", errors));
    }
}
