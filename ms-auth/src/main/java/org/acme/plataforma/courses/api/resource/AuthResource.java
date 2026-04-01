package org.acme.plataforma.courses.api.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.plataforma.courses.application.dto.LoginRequest;
import org.acme.plataforma.courses.application.dto.RefreshRequest;
import org.acme.plataforma.courses.application.dto.RegisterRequest;
import org.acme.plataforma.courses.application.dto.TokenPair;
import org.acme.plataforma.courses.application.service.AuthService;
import org.acme.plataforma.courses.application.service.TokenService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @Inject
    TokenService tokenService;

    @POST
    @Path("/register")
    public Response register(@Valid RegisterRequest req) {
        var tokens = authService.register(req);
        return Response.status(Response.Status.CREATED).entity(tokens).build();
    }

    @POST
    @Path("/login")
    public TokenPair login(@Valid LoginRequest req) {
        return authService.login(req);
    }

    @POST
    @Path("/logout")
    @Authenticated
    public Response logout(@Valid RefreshRequest req) {
        tokenService.revokeRefreshToken(req.refreshToken());
        return Response.noContent().build();
    }
}
