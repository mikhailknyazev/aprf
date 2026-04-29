package net.michaelknyazev.aprf.experience.nativeapp;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * Routed resource tests for the native experience product-view endpoint.
 */
@QuarkusTest
class ExperienceStatusResourceTest {

    @InjectMock
    CatalogGateway catalogGateway;

    @Test
    void statusReturnsNativeExperienceEnvelope() {
        when(catalogGateway.getStatus()).thenReturn(new CatalogStatus(
                "catalog-native",
                "ready",
                Instant.now().toString(),
                false,
                "NONE",
                null,
                "FRESH",
                1,
                false,
                "vert.x-eventloop-thread-1",
                false
        ));

        get("/product-views/PROD-001")
                .then()
                .statusCode(200)
                .body("service", is("experience-native"))
                .body("backend.service", is("catalog-native"))
                .body("fallback", is(false));
    }
}
