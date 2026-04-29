package net.michaelknyazev.aprf.catalog.jvm;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Routed resource tests for the JVM catalog product endpoints.
 */
@QuarkusTest
class CatalogStatusResourceTest {

    @Test
    void statusReturnsProductEnvelope() {
        get("/products/PROD-001")
                .then()
                .statusCode(200)
                .body("service", is("catalog-jvm"))
                .body("state", is("ready"))
                .body("product.productId", is("PROD-001"))
                .body("thread", notNullValue());
    }

    @Test
    void errorReturnsFaultEnvelope() {
        get("/products/PROD-001/fault-test/error")
                .then()
                .statusCode(500)
                .body("service", is("catalog-jvm"))
                .body("faultMode", is("ERROR"))
                .body("faulty", is(true));
    }
}
