package net.michaelknyazev.aprf.catalog.nativeapp;

import java.time.Instant;

/**
 * Small product snapshot carried in the native catalog response.
 */
public record Product(
        String productId,
        String productName,
        double currentPrice,
        int stockLevel,
        Instant lastUpdated
) {
}
