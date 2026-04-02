package org.acme.plataforma.courses.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.plataforma.courses.responses.CourseResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.UUID;

@RegisterRestClient(configKey = "course-client")
@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
public interface CourseClient {
    @GET
    @Path("/{id}")
    CourseResponse findById(@PathParam("id") UUID id);
}
