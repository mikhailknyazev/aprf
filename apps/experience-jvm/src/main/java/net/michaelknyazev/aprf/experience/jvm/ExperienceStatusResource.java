package net.michaelknyazev.aprf.experience.jvm;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Main routed product-view endpoint used by the JVM lifecycle and continuity
 * checks.
 */
@Path("/product-views/{productId}")
@Produces(MediaType.APPLICATION_JSON)
public class ExperienceStatusResource {

    private final ExperienceJvmService service;

    public ExperienceStatusResource(ExperienceJvmService service) {
        this.service = service;
    }

    @GET
    public ExperienceStatus status() {
        return service.status();
    }
}
