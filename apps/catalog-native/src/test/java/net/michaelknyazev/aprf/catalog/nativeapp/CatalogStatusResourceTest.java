package net.michaelknyazev.aprf.catalog.nativeapp;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;

/**
 * Routed resource tests for the native catalog product and cache admin
 * endpoints.
 */
@QuarkusTest
class CatalogStatusResourceTest {

    @Test
    void statusReturnsNativeCatalogEnvelope() {
        given()
                .contentType("application/json")
                .body("{\"enabled\":false}")
                .post("/admin/cache")
                .then()
                .statusCode(200)
                .body("enabled", is(false));

        get("/products/PROD-001")
                .then()
                .statusCode(200)
                .body("service", is("catalog-native"))
                .body("cacheEnabled", is(false));
    }

    @Test
    void adminCacheToggleChangesVisibleState() {
        given()
                .contentType("application/json")
                .body("{\"enabled\":true}")
                .post("/admin/cache")
                .then()
                .statusCode(200)
                .body("enabled", is(true));
    }
}
