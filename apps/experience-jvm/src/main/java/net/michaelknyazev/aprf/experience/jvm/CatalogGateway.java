package net.michaelknyazev.aprf.experience.jvm;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Thin gateway that keeps the direct JVM dependency path easy to read and mock
 * in tests.
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
