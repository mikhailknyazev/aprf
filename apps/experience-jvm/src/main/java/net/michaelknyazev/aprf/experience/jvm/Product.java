package net.michaelknyazev.aprf.experience.jvm;

import java.time.Instant;

/**
 * Product snapshot as seen through the JVM experience service.
 */
public record Product(
        String productId,
        String productName,
        double currentPrice,
        int stockLevel,
        Instant lastUpdated
) {
}
