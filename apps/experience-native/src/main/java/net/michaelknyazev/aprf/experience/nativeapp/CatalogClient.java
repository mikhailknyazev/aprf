package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client for the native experience-to-catalog dependency path.
 *
 * <p>The published reference implementation keeps one product id wired through
 * the whole chain so the routed flow is easy to read in the article and in the
 * source. Readers can parameterize this later if they want a multi-product
 * variant.</p>
 */
@Path("/products/PROD-001")
@RegisterRestClient(configKey = "catalog")
public interface CatalogClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    CatalogStatus getStatus();

    @GET
    @Path("/fault-test/slow")
    @Produces(MediaType.APPLICATION_JSON)
    CatalogStatus getSlowStatus();

    @GET
    @Path("/fault-test/error")
    @Produces(MediaType.APPLICATION_JSON)
    CatalogStatus getErrorStatus();

    @GET
    @Path("/fault-test/timeout")
    @Produces(MediaType.APPLICATION_JSON)
    CatalogStatus getTimeoutStatus();

    @GET
    @Path("/fault-test/flaky")
    @Produces(MediaType.APPLICATION_JSON)
    CatalogStatus getFlakyStatus();
}
