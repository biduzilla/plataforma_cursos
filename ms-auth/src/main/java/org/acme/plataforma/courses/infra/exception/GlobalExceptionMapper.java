package org.acme.plataforma.courses.infra.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Provider
public class GlobalExceptionMapper {
    @ServerExceptionMapper
    public Response handleNotFoundException(NotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    @ServerExceptionMapper
    public Response handleUnauthorizedException(UnauthorizedException ex) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    @ServerExceptionMapper
    public Response handleBadRequestException(BadRequestException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }
}
