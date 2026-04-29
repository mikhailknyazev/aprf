package net.michaelknyazev.aprf.catalog.nativeapp;

/**
 * Immediate acknowledgment returned by the cache toggle endpoint.
 */
public record CacheStatus(boolean enabled, String message) {
}
