package org.acme.plataforma.courses.resources;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.plataforma.courses.enums.CourseStatusEnum;
import org.acme.plataforma.courses.mapper.CourseMapper;
import org.acme.plataforma.courses.requests.CourseRequest;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.acme.plataforma.courses.responses.PageResponse;
import org.acme.plataforma.courses.services.ICourseService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CourseResource {
    @Inject
    ICourseService courseService;
    @Inject
    JsonWebToken jwt;

    @Inject
    CourseMapper mapper;

    @GET
    @PermitAll
    public PageResponse<CourseResponse> search(
            @QueryParam("title") String title,
            @QueryParam("status") CourseStatusEnum status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize) {

        return courseService.search(title, status, page, pageSize);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public CourseResponse findById(@PathParam("id") UUID id) {
        return mapper.toResponse(courseService.findById(id));
    }

    @POST
    @RolesAllowed("INSTRUCTOR")
    public Response create(@Valid CourseRequest req) {
        var instructorId = UUID.fromString(jwt.getSubject());
        var response = courseService.create(req, instructorId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("INSTRUCTOR")
    public CourseResponse update(@PathParam("id") UUID id, @Valid CourseRequest req) {
        UUID instructorId = UUID.fromString(jwt.getSubject());
        return courseService.update(id, req, instructorId);
    }

    @PATCH
    @Path("/{id}/publish")
    @RolesAllowed("INSTRUCTOR")
    public CourseResponse publish(@PathParam("id") UUID id) {
        UUID instructorId = UUID.fromString(jwt.getSubject());
        return courseService.publish(id, instructorId);
    }

    @PATCH
    @Path("/{id}/archive")
    @RolesAllowed("INSTRUCTOR")
    public CourseResponse archive(@PathParam("id") UUID id) {
        UUID instructorId = UUID.fromString(jwt.getSubject());
        return courseService.archive(id, instructorId);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("INSTRUCTOR")
    public Response delete(@PathParam("id") UUID id) {
        UUID instructorId = UUID.fromString(jwt.getSubject());
        courseService.delete(id, instructorId);
        return Response.noContent().build();
    }
}
