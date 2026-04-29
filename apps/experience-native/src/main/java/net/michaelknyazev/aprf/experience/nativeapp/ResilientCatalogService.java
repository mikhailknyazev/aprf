package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.time.Instant;

/**
 * Hardened dependency client used by the native experience service.
 */
@ApplicationScoped
public class ResilientCatalogService {

    private final CatalogGateway catalogGateway;

    public ResilientCatalogService(CatalogGateway catalogGateway) {
        this.catalogGateway = catalogGateway;
    }

    // The normal path keeps the richer native product-view contract alive while
    // still leaving room for the D8 Governance and observability corrective
    // action to improve it.
    @Retry(maxRetries = 5, delay = 50)
    @Timeout(400)
    @CircuitBreaker(requestVolumeThreshold = 8, failureRatio = 0.75, delay = 500)
    @Bulkhead(20)
    @Fallback(fallbackMethod = "fallbackCatalog")
    public CatalogStatus callNormal() {
        return catalogGateway.getStatus();
    }

    @Retry(maxRetries = 3, delay = 50)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 6, failureRatio = 0.80, delay = 500)
    @Bulkhead(15)
    @Fallback(fallbackMethod = "fallbackCatalog")
    public CatalogStatus callSlow() {
        return catalogGateway.getSlowStatus();
    }

    @Retry(maxRetries = 2, delay = 50)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.90, delay = 500)
    @Fallback(fallbackMethod = "fallbackCatalog")
    public CatalogStatus callError() {
        return catalogGateway.getErrorStatus();
    }

    @Retry(maxRetries = 4, delay = 50)
    @Timeout(200)
    @CircuitBreaker(requestVolumeThreshold = 8, failureRatio = 0.70, delay = 500)
    @Fallback(fallbackMethod = "fallbackCatalog")
    public CatalogStatus callTimeout() {
        return catalogGateway.getTimeoutStatus();
    }

    @Retry(maxRetries = 5, delay = 25)
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.60, delay = 500)
    @Fallback(fallbackMethod = "fallbackCatalog")
    public CatalogStatus callFlaky() {
        return catalogGateway.getFlakyStatus();
    }

    // The fallback stays intentionally simple so the routed response can show
    // that resilience behavior directly.
    public CatalogStatus fallbackCatalog() {
        return new CatalogStatus(
                "catalog-native",
                "fallback",
                Instant.now().toString(),
                true,
                "CIRCUIT_OPEN",
                null,
                "FRESH",
                0,
                false,
                Thread.currentThread().getName(),
                false
        );
    }
}
