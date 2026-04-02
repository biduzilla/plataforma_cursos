package org.acme.plataforma.courses.resources;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.plataforma.courses.requests.OrderRequest;
import org.acme.plataforma.courses.responses.OrderResponse;
import org.acme.plataforma.courses.services.IOrderService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {
    @Inject
    IOrderService orderService;
    @Inject
    JsonWebToken jwt;

    @POST
    @Authenticated
    public Response create(@Valid OrderRequest req) {
        var userId = UUID.fromString(jwt.getSubject());
        OrderResponse response = orderService.create(req, userId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PATCH
    @Path("/{id}/cancel")
    @Authenticated
    public OrderResponse cancel(@PathParam("id") UUID id) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return orderService.cancel(id, userId);
    }

    @GET
    @Authenticated
    public List<OrderResponse> myOrders() {
        UUID userId = UUID.fromString(jwt.getSubject());
        return orderService.findByUser(userId);
    }

    @GET
    @Path("/{id}")
    @Authenticated
    public OrderResponse findById(@PathParam("id") UUID id) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return orderService.findById(id, userId);
    }
}
