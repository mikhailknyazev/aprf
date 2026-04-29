package net.michaelknyazev.aprf.catalog.nativeapp;

/**
 * Fixed fault families used by the native catalog route tests.
 */
public enum FaultMode {
    NONE,
    SLOW,
    ERROR,
    TIMEOUT,
    FLAKY
}
