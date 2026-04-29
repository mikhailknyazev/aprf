package net.michaelknyazev.aprf.experience.nativeapp;

/**
 * Routed response returned by the native experience service.
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
