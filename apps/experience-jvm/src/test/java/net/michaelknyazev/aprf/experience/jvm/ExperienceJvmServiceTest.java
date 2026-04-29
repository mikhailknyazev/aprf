package net.michaelknyazev.aprf.experience.jvm;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the direct JVM routed service.
 */
class ExperienceJvmServiceTest {

    @Test
    void faultTypeRoutesToMatchingGatewayMethod() {
        CatalogStatus status = new CatalogStatus(
                "catalog-jvm",
                "ready",
                Instant.now().toString(),
                false,
                "SLOW",
                null,
                "FRESH",
                1,
                false,
                "worker-1",
                false
        );
        ExperienceJvmService service = new ExperienceJvmService(new CatalogGatewayStub(status));

        ExperienceStatus response = service.faultTest("slow");

        assertEquals("fault-test-slow", response.state());
        assertEquals("SLOW", response.backend().faultMode());
    }

    private static final class CatalogGatewayStub extends CatalogGateway {
        private final CatalogStatus status;

        private CatalogGatewayStub(CatalogStatus status) {
            super(null);
            this.status = status;
        }

        @Override
        public CatalogStatus getSlowStatus() {
            return status;
        }

        @Override
        public CatalogStatus getStatus() {
            return status;
        }
    }
}
