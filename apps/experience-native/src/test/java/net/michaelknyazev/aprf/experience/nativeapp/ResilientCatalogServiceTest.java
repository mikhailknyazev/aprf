package net.michaelknyazev.aprf.experience.nativeapp;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Service-level tests for the hardened native dependency client.
 */
@QuarkusTest
class ResilientCatalogServiceTest {

    @InjectMock
    CatalogGateway catalogGateway;

    @Inject
    ResilientCatalogService service;

    @Test
    void fallbackIsReturnedWhenGatewayFails() {
        when(catalogGateway.getErrorStatus()).thenThrow(new RuntimeException(new IOException("boom")));

        CatalogStatus response = service.callError();

        assertEquals("fallback", response.state());
        assertEquals("CIRCUIT_OPEN", response.faultMode());
        assertTrue(response.faulty());
    }

    @Test
    void normalPathStillSucceedsForModeratelySlowGateway() {
        when(catalogGateway.getStatus()).thenAnswer(invocation -> {
            Thread.sleep(250L);
            return new CatalogStatus(
                    "catalog-native",
                    "ready",
                    Instant.now().toString(),
                    false,
                    "NONE",
                    null,
                    "FRESH",
                    225L,
                    false,
                    Thread.currentThread().getName(),
                    false
            );
        });

        CatalogStatus response = service.callNormal();

        assertEquals("ready", response.state());
        assertEquals("NONE", response.faultMode());
        assertFalse(response.faulty());
    }
}
