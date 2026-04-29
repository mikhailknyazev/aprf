package net.michaelknyazev.aprf.catalog.jvm;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the JVM catalog service behavior that the article depends on.
 */
class CatalogJvmServiceTest {

    @Test
    void mutationKeepsPriceRoundedAndStockBounded() {
        CatalogJvmService service = new CatalogJvmService(225L);
        Product previous = new Product("PROD-001", "Widget", 100.0, 20, Instant.parse("2026-04-19T00:00:00Z"));

        Product mutated = service.mutateSnapshot(previous, 1.031, -50, Instant.parse("2026-04-19T00:00:25Z"));

        assertEquals(103.1, mutated.currentPrice());
        assertEquals(10, mutated.stockLevel());
        assertEquals(Instant.parse("2026-04-19T00:00:25Z"), mutated.lastUpdated());
    }

    @Test
    void flakyFailureAlternatesDeterministically() {
        CatalogJvmService service = new CatalogJvmService(225L);

        assertTrue(service.shouldFailFlaky());
        assertFalse(service.shouldFailFlaky());
    }

    @Test
    void normalStatusCarriesTheBoundedUncachedDelay() {
        CatalogJvmService service = new CatalogJvmService(225L);

        CatalogEnvelope current = service.current(FaultMode.NONE);

        assertTrue(current.computationTimeMs() >= 200);
        assertEquals("FRESH", current.dataFreshness());
    }

    @Test
    void faultDelaysAreDerivedFromTheSharedBaseline() {
        CatalogJvmService service = new CatalogJvmService(225L);

        assertEquals(225L, service.delayFor(FaultMode.NONE));
        assertEquals(450L, service.delayFor(FaultMode.SLOW));
        assertEquals(675L, service.delayFor(FaultMode.TIMEOUT));
    }
}
