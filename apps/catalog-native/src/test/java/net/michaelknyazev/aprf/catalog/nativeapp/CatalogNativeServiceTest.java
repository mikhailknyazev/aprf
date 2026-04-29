package net.michaelknyazev.aprf.catalog.nativeapp;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Service-level tests for the native catalog cache behavior.
 */
@QuarkusTest
class CatalogNativeServiceTest {

    @Inject
    CatalogNativeService service;

    @Test
    void cacheCanBeEnabledAndDisabled() {
        CacheStatus enabled = service.setCacheEnabled(true);
        CacheStatus disabled = service.setCacheEnabled(false);

        assertTrue(enabled.enabled());
        assertFalse(disabled.enabled());
        assertFalse(service.isCacheEnabled());
    }

    @Test
    void cacheHitBecomesVisibleWhenCacheIsEnabled() {
        service.setCacheEnabled(false);
        CatalogEnvelope fresh = service.current(FaultMode.NONE);

        service.setCacheEnabled(true);
        CatalogEnvelope populated = service.current(FaultMode.NONE);
        CatalogEnvelope cached = service.current(FaultMode.NONE);

        assertEquals("FRESH", fresh.dataFreshness());
        assertEquals("CACHED", populated.dataFreshness());
        assertEquals("CACHED", cached.dataFreshness());
        assertFalse(fresh.cacheHit());
        assertFalse(populated.cacheHit());
        assertTrue(cached.cacheHit());
    }

    @Test
    void uncachedStatusPathIsMeaningfullySlowerThanCachedStatusPath() {
        service.setCacheEnabled(false);
        CatalogEnvelope uncached = service.current(FaultMode.NONE);

        service.setCacheEnabled(true);
        CatalogEnvelope cached = service.current(FaultMode.NONE);

        assertTrue(uncached.computationTimeMs() >= 200);
        assertTrue(cached.computationTimeMs() < uncached.computationTimeMs());
    }

    @Test
    void faultDelaysAreDerivedFromTheSharedBaseline() {
        service.setCacheEnabled(false);

        assertEquals(225L, service.delayFor(FaultMode.NONE));
        assertEquals(450L, service.delayFor(FaultMode.SLOW));
        assertEquals(675L, service.delayFor(FaultMode.TIMEOUT));

        service.setCacheEnabled(true);
        assertEquals(0L, service.delayFor(FaultMode.NONE));
    }
}
