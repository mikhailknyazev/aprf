package net.michaelknyazev.aprf.catalog.jvm;

/**
 * Small fixed set of downstream behaviors used by the published APRF tests.
 */
public enum FaultMode {
    NONE,
    SLOW,
    ERROR,
    TIMEOUT,
    FLAKY
}
