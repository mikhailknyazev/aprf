package net.michaelknyazev.aprf.catalog.nativeapp;

/**
 * Small admin request used to toggle the bounded cache path for
 * D8 Governance and observability.
 */
public record CacheRequest(boolean enabled) {
}
