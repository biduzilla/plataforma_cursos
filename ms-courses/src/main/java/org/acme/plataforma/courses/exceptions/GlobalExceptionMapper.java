package org.acme.plataforma.courses.exceptions;

import io.quarkus.security.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.acme.plataforma.courses.responses.ErrorResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

@Provider
public class GlobalExceptionMapper {
    @ServerExceptionMapper
    public Response handleNotFoundException(NotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    @ServerExceptionMapper
    public Response handleForbiddenException(ForbiddenException ex) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    @ServerExceptionMapper
    public Response handleBadRequestException(BadRequestException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    @ServerExceptionMapper
    public Response handleValidation(
            ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> {
                    String field = cv.getPropertyPath()
                            .toString()
                            .replaceAll(".*\\.", "");

                    return field + ": " + cv.getMessage();
                })
                .sorted()
                .toList();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("Erro de validação", errors))
                .build();
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleUnauthorized(
            UnauthorizedException ex) {

        return RestResponse.status(
                Response.Status.UNAUTHORIZED,
                new ErrorResponse("Autenticação necessária")
        );
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> handleForbidden(
            io.quarkus.security.ForbiddenException ex) {

        return RestResponse.status(
                Response.Status.FORBIDDEN,
                new ErrorResponse("Você não tem permissão para acessar este recurso")
        );
    }
}
