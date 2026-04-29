package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Main routed product-view endpoint for the native pair.
 */
@Path("/product-views/{productId}")
@Produces(MediaType.APPLICATION_JSON)
public class ExperienceStatusResource {

    private final ExperienceNativeService service;

    public ExperienceStatusResource(ExperienceNativeService service) {
        this.service = service;
    }

    @GET
    public ExperienceStatus status() {
        return service.status();
    }
}
