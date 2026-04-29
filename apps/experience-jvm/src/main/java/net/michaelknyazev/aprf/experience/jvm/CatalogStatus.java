package net.michaelknyazev.aprf.experience.jvm;

/**
 * Catalog response shape as seen by the JVM experience service.
 */
public record CatalogStatus(
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
