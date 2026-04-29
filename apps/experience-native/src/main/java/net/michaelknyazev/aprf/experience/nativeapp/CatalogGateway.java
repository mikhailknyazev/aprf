package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Thin gateway around the downstream native catalog calls.
 */
@ApplicationScoped
public class CatalogGateway {

    private final CatalogClient catalogClient;

    public CatalogGateway(@RestClient CatalogClient catalogClient) {
        this.catalogClient = catalogClient;
    }

    public CatalogStatus getStatus() {
        return catalogClient.getStatus();
    }

    public CatalogStatus getSlowStatus() {
        return catalogClient.getSlowStatus();
    }

    public CatalogStatus getErrorStatus() {
        return catalogClient.getErrorStatus();
    }

    public CatalogStatus getTimeoutStatus() {
        return catalogClient.getTimeoutStatus();
    }

    public CatalogStatus getFlakyStatus() {
        return catalogClient.getFlakyStatus();
    }
}
