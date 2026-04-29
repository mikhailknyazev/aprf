package net.michaelknyazev.aprf.catalog.nativeapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Exposes the native catalog product route and the bounded fault routes used
 * in the APRF drills.
 */
@Path("/products/{productId}")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogStatusResource {

    private final CatalogNativeService service;

    public CatalogStatusResource(CatalogNativeService service) {
        this.service = service;
    }

    @GET
    public CatalogEnvelope status() {
        return service.current(FaultMode.NONE);
    }

    @GET
    @Path("/fault-test/slow")
    public CatalogEnvelope slow() {
        return service.current(FaultMode.SLOW);
    }

    @GET
    @Path("/fault-test/timeout")
    public CatalogEnvelope timeout() {
        return service.current(FaultMode.TIMEOUT);
    }

    @GET
    @Path("/fault-test/error")
    public Response error() {
        return Response.serverError().entity(service.errorResponse(FaultMode.ERROR)).build();
    }

    @GET
    @Path("/fault-test/flaky")
    public Response flaky() {
        if (service.shouldFailFlaky()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(service.errorResponse(FaultMode.FLAKY))
                    .build();
        }
        return Response.ok(service.directCurrent(FaultMode.FLAKY)).build();
    }

}
