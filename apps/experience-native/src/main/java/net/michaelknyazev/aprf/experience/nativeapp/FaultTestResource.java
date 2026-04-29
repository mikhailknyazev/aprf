package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Routed fault-test endpoint that drives the native alert and hardening
 * drills.
 */
@Path("/product-views/{productId}/fault-test")
@Produces(MediaType.APPLICATION_JSON)
public class FaultTestResource {

    private final ExperienceNativeService service;

    public FaultTestResource(ExperienceNativeService service) {
        this.service = service;
    }

    @GET
    @Path("/{faultType}")
    public ExperienceStatus faultTest(@PathParam("faultType") String faultType) {
        return service.faultTest(faultType);
    }
}
