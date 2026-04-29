package net.michaelknyazev.aprf.experience.jvm;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

/**
 * Upstream JVM service that keeps the routed path direct on purpose.
 *
 * <p>This is the simpler bytecode-based Java baseline in the article's
 * JVM-versus-native comparison. There is no retry, timeout, circuit-breaker,
 * bulkhead, or fallback between this service and the downstream catalog
 * client, so downstream failures appear directly on the routed path.</p>
 */
@ApplicationScoped
public class ExperienceJvmService {

    private final CatalogGateway catalogGateway;

    public ExperienceJvmService(CatalogGateway catalogGateway) {
        this.catalogGateway = catalogGateway;
    }

    public ExperienceStatus status() {
        return envelopeFor(catalogGateway.getStatus(), "ready");
    }

    public ExperienceStatus faultTest(String faultType) {
        CatalogStatus status = switch (faultType.toLowerCase()) {
            case "slow" -> catalogGateway.getSlowStatus();
            case "error" -> catalogGateway.getErrorStatus();
            case "timeout" -> catalogGateway.getTimeoutStatus();
            case "flaky" -> catalogGateway.getFlakyStatus();
            default -> catalogGateway.getStatus();
        };
        return envelopeFor(status, "fault-test-" + faultType.toLowerCase());
    }

    // Keep the routed response shape stable even when the downstream fault mode
    // changes.
    private ExperienceStatus envelopeFor(CatalogStatus backend, String state) {
        return new ExperienceStatus(
                "experience-jvm",
                state,
                Instant.now().toString(),
                backend,
                Thread.currentThread().getName(),
                false
        );
    }
}
