package org.acme.plataforma.courses.resources;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.plataforma.courses.requests.LessonRequest;
import org.acme.plataforma.courses.requests.ModuleRequest;
import org.acme.plataforma.courses.responses.LessonResponse;
import org.acme.plataforma.courses.responses.ModuleResponse;
import org.acme.plataforma.courses.services.ILessonService;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LessonResource {

    @Inject
    ILessonService lessonService;
    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/{courseId}/modules")
    @RolesAllowed("INSTRUCTOR")
    public Response addModule(
            @PathParam("courseId") UUID courseId,
            @Valid ModuleRequest req) {

        UUID instructorId = UUID.fromString(jwt.getSubject());
        ModuleResponse response = lessonService.addModule(courseId, req, instructorId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/modules/{moduleId}/lessons")
    @RolesAllowed("INSTRUCTOR")
    public Response addLesson(
            @PathParam("moduleId") UUID moduleId,
            @Valid LessonRequest req) {

        UUID instructorId = UUID.fromString(jwt.getSubject());
        LessonResponse response = lessonService.addLesson(moduleId, req, instructorId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @DELETE
    @Path("/lessons/{lessonId}")
    @RolesAllowed("INSTRUCTOR")
    public Response deleteLesson(@PathParam("lessonId") UUID lessonId) {
        UUID instructorId = UUID.fromString(jwt.getSubject());
        lessonService.deleteLesson(lessonId, instructorId);
        return Response.noContent().build();
    }
}
