package net.michaelknyazev.aprf.experience.jvm;

/**
 * Routed response returned by the JVM experience service.
 */
public record ExperienceStatus(
        String service,
        String state,
        String timestamp,
        CatalogStatus backend,
        String thread,
        boolean fallback
) {
}
