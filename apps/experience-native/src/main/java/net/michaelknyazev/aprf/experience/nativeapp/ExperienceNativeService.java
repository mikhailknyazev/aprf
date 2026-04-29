package net.michaelknyazev.aprf.experience.nativeapp;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;

/**
 * Upstream native service that exposes the routed path measured in the later
 * APRF disciplines.
 */
@ApplicationScoped
public class ExperienceNativeService {

    private final ResilientCatalogService resilientCatalogService;

    public ExperienceNativeService(ResilientCatalogService resilientCatalogService) {
        this.resilientCatalogService = resilientCatalogService;
    }

    public ExperienceStatus status() {
        return envelopeFor(resilientCatalogService.callNormal(), "ready");
    }

    public ExperienceStatus faultTest(String faultType) {
        CatalogStatus status = switch (faultType.toLowerCase()) {
            case "slow" -> resilientCatalogService.callSlow();
            case "error" -> resilientCatalogService.callError();
            case "timeout" -> resilientCatalogService.callTimeout();
            case "flaky" -> resilientCatalogService.callFlaky();
            default -> resilientCatalogService.callNormal();
        };
        return envelopeFor(status, "fault-test-" + faultType.toLowerCase());
    }

    // Expose whether the routed response is using the fallback path so the
    // D6 Hardened application patterns and D8 Governance and observability
    // proofs can measure it directly.
    private ExperienceStatus envelopeFor(CatalogStatus backend, String state) {
        return new ExperienceStatus(
                "experience-native",
                state,
                Instant.now().toString(),
                backend,
                Thread.currentThread().getName(),
                "fallback".equalsIgnoreCase(backend.state())
        );
    }
}
