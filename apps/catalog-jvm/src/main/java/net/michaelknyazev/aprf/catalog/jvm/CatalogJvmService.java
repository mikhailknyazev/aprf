package net.michaelknyazev.aprf.catalog.jvm;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Direct JVM catalog service used as the simpler downstream baseline in the
 * article.
 */
@ApplicationScoped
public class CatalogJvmService {

    private static final long SLOW_DELAY_MULTIPLIER = 2L;
    private static final long TIMEOUT_DELAY_MULTIPLIER = 3L;

    private final AtomicReference<Product> currentProduct = new AtomicReference<>(
            new Product("PROD-001", "Quarkus Turbo Widget", 99.99, 142, Instant.now())
    );
    private final AtomicInteger flakyCounter = new AtomicInteger();
    private final long statusBaselineDelayMs;

    public CatalogJvmService(
            @ConfigProperty(name = "aprf.catalog.status-baseline-delay-ms", defaultValue = "225") long statusBaselineDelayMs
    ) {
        this.statusBaselineDelayMs = statusBaselineDelayMs;
    }

    // Keep the product snapshot moving so the response looks like changing business
    // data instead of a static stub.
    @Scheduled(every = "25s")
    void updateProductData() {
        currentProduct.updateAndGet(previous -> mutateSnapshot(
                previous,
                0.95 + ThreadLocalRandom.current().nextDouble() * 0.15,
                ThreadLocalRandom.current().nextInt(-30, 50),
                Instant.now()
        ));
    }

    Product mutateSnapshot(Product previous, double priceMultiplier, int stockDelta, Instant updatedAt) {
        double nextPrice = Math.round(previous.currentPrice() * priceMultiplier * 100.0) / 100.0;
        int nextStock = Math.max(10, previous.stockLevel() + stockDelta);
        return new Product(
                previous.productId(),
                previous.productName(),
                nextPrice,
                nextStock,
                updatedAt
        );
    }

    public CatalogEnvelope current(FaultMode mode) {
        long startedAt = System.nanoTime();
        applyDelay(mode);
        Product product = currentProduct.get();
        return new CatalogEnvelope(
                "catalog-jvm",
                "ready",
                Instant.now().toString(),
                mode != FaultMode.NONE,
                mode.name(),
                product,
                "FRESH",
                (System.nanoTime() - startedAt) / 1_000_000,
                false,
                Thread.currentThread().getName(),
                false
        );
    }

    public CatalogEnvelope errorResponse(FaultMode mode) {
        return new CatalogEnvelope(
                "catalog-jvm",
                "error",
                Instant.now().toString(),
                true,
                mode.name(),
                null,
                "FRESH",
                0,
                false,
                Thread.currentThread().getName(),
                false
        );
    }

    public boolean shouldFailFlaky() {
        // Keep the contract intentionally simple: each replica alternates
        // fail/succeed independently, so the route is predictably flaky
        // without pretending to coordinate a cluster-wide 50/50 split.
        return flakyCounter.incrementAndGet() % 2 == 1;
    }

    private void applyDelay(FaultMode mode) {
        long delay = delayFor(mode);
        if (delay <= 0L) {
            return;
        }
        try {
            Thread.sleep(delay);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    long delayFor(FaultMode mode) {
        return switch (mode) {
            case NONE -> statusBaselineDelayMs;
            case SLOW -> statusBaselineDelayMs * SLOW_DELAY_MULTIPLIER;
            case TIMEOUT -> statusBaselineDelayMs * TIMEOUT_DELAY_MULTIPLIER;
            default -> 0L;
        };
    }
    
}
