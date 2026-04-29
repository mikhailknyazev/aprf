package net.michaelknyazev.aprf.catalog.jvm;

/**
 * Response envelope returned by the JVM catalog service.
 * It keeps the routed status shape stable across the direct and fault routes.
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
