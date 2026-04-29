package net.michaelknyazev.aprf.catalog.nativeapp;

/**
 * Response envelope returned by the native catalog service.
 */
public record CatalogEnvelope(
        String service,
        String state,
        String timestamp,
        boolean faulty,
        String faultMode,
        Product product,
        String dataFreshness,
        long computationTimeMs,
        boolean cacheHit,
        String thread,
        boolean cacheEnabled
) {
}
