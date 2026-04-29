package net.michaelknyazev.aprf.experience.nativeapp;

import java.time.Instant;

/**
 * Product snapshot as seen through the native experience service.
 */
public record Product(
        String productId,
        String productName,
        double currentPrice,
        int stockLevel,
        Instant lastUpdated
) {
}
