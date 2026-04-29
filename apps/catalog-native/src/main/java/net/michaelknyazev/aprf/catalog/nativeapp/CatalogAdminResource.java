package net.michaelknyazev.aprf.catalog.nativeapp;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Small bounded admin endpoint for the D8 Governance and observability
 * corrective-action drill.
 */
@Path("/admin/cache")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatalogAdminResource {

    private final CatalogNativeService service;

    public CatalogAdminResource(CatalogNativeService service) {
        this.service = service;
    }

    @POST
    public CacheStatus toggleCache(CacheRequest request) {
        return service.setCacheEnabled(request.enabled());
    }
}
