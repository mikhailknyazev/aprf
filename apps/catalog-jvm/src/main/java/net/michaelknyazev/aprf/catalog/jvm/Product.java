package net.michaelknyazev.aprf.catalog.jvm;

import java.time.Instant;

/**
 * Small product snapshot carried in the catalog response.
 */
public record Product(
        String productId,
        String productName,
        double currentPrice,
        int stockLevel,
        Instant lastUpdated
) {
}
