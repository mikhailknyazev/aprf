package net.michaelknyazev.aprf.experience.jvm;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Routed fault-test endpoint used by D6 Hardened application patterns and
 * D8 Governance and observability alert drills.
 */
@Path("/product-views/{productId}/fault-test")
@Produces(MediaType.APPLICATION_JSON)
public class FaultTestResource {

    private final ExperienceJvmService service;

    public FaultTestResource(ExperienceJvmService service) {
        this.service = service;
    }

    @GET
    @Path("/{faultType}")
    public ExperienceStatus faultTest(@PathParam("faultType") String faultType) {
        return service.faultTest(faultType);
    }
}
