package net.michaelknyazev.aprf.catalog.nativeapp;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * Enables histogram buckets for the routed HTTP metrics behind
 * D8 Governance and observability alerting and corrective action.
 */
public class HttpServerMetricsConfiguration {

    @Produces
    @Singleton
    MeterFilter enableHttpServerRequestHistogram() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if ("http.server.requests".equals(id.getName())) {
                    return DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .build()
                            .merge(config);
                }
                return config;
            }
        };
    }
}
