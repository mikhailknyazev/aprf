package net.michaelknyazev.aprf.catalog.nativeapp;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.cache.CacheName;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Native catalog service with the optional cache path used by
 * D8 Governance and observability corrective action.
 */
@ApplicationScoped
public class CatalogNativeService {

    private static final long SLOW_DELAY_MULTIPLIER = 2L;
    private static final long TIMEOUT_DELAY_MULTIPLIER = 3L;
    private static final Duration CACHE_OP_TIMEOUT = Duration.ofSeconds(2);
    private static final String CACHE_KEY = "product";

    private final AtomicReference<Product> currentProduct = new AtomicReference<>(
            new Product("PROD-001", "Quarkus Turbo Widget", 99.99, 142, Instant.now())
    );
    private final AtomicBoolean cacheEnabled = new AtomicBoolean(false);
    private final AtomicInteger flakyCounter = new AtomicInteger();
    private final Cache cache;
    private final boolean initialCacheEnabled;
    private final long statusBaselineDelayMs;

    public CatalogNativeService(
            @CacheName("catalog-status") Cache cache,
            @ConfigProperty(name = "aprf.catalog.cache-enabled", defaultValue = "false") boolean initialCacheEnabled,
            @ConfigProperty(name = "aprf.catalog.status-baseline-delay-ms", defaultValue = "225") long statusBaselineDelayMs
    ) {
        this.cache = cache;
        this.initialCacheEnabled = initialCacheEnabled;
        this.statusBaselineDelayMs = statusBaselineDelayMs;
    }

    @PostConstruct
    void initialize() {
        cacheEnabled.set(initialCacheEnabled);
    }

    // Keep the product snapshot moving so the product output looks like a changing
    // service rather than a frozen fixture.
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
        if (mode == FaultMode.NONE && cacheEnabled.get()) {
            return cachedCurrent();
        }
        return directCurrent(mode);
    }

    public CatalogEnvelope errorResponse(FaultMode mode) {
        return new CatalogEnvelope(
                "catalog-native",
                "error",
                Instant.now().toString(),
                true,
                mode.name(),
                null,
                cacheEnabled.get() ? "CACHED" : "FRESH",
                0,
                false,
                Thread.currentThread().getName(),
                cacheEnabled.get()
        );
    }

    public boolean shouldFailFlaky() {
        // Each replica alternates fail/succeed independently, 
        // so the route is predictably flaky without pretending 
        // to coordinate a cluster-wide 50/50 split.
        return flakyCounter.incrementAndGet() % 2 == 1;
    }

    public CacheStatus setCacheEnabled(boolean enabled) {
        cacheEnabled.set(enabled);
        if (!enabled) {
            cache.invalidateAll().await().atMost(CACHE_OP_TIMEOUT);
        }
        return new CacheStatus(enabled, "Cache " + (enabled ? "ENABLED" : "DISABLED"));
    }

    public boolean isCacheEnabled() {
        return cacheEnabled.get();
    }

    CatalogEnvelope cachedCurrent() {
        long startedAt = System.nanoTime();
        CaffeineCache caffeine = cache.as(CaffeineCache.class);
        CompletableFuture<Object> existing = caffeine.getIfPresent(CACHE_KEY);
        boolean cacheHit = existing != null;
        Product product = cache.<String, Product>get(CACHE_KEY, key -> currentProduct.get())
                .await().atMost(CACHE_OP_TIMEOUT);
        return buildEnvelope(FaultMode.NONE, cacheHit, "CACHED", startedAt, product);
    }

    // This is the baseline path that keeps the uncached cost visible until the
    // cache is deliberately enabled.
    CatalogEnvelope directCurrent(FaultMode mode) {
        long startedAt = System.nanoTime();
        applyDelay(mode);
        return buildEnvelope(mode, false, "FRESH", startedAt, currentProduct.get());
    }

    private CatalogEnvelope buildEnvelope(
            FaultMode mode,
            boolean cacheHit,
            String freshness,
            long startedAt,
            Product product
    ) {
        return new CatalogEnvelope(
                "catalog-native",
                "ready",
                Instant.now().toString(),
                mode != FaultMode.NONE,
                mode.name(),
                product,
                freshness,
                (System.nanoTime() - startedAt) / 1_000_000,
                cacheHit,
                Thread.currentThread().getName(),
                cacheEnabled.get()
        );
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
            case NONE -> cacheEnabled.get() ? 0L : statusBaselineDelayMs;
            case SLOW -> statusBaselineDelayMs * SLOW_DELAY_MULTIPLIER;
            case TIMEOUT -> statusBaselineDelayMs * TIMEOUT_DELAY_MULTIPLIER;
            case ERROR, FLAKY -> 0L;
        };
    }
}
