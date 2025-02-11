package de.sldk.mc;

import de.sldk.mc.metrics.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MetricRegistry {

    private static final MetricRegistry INSTANCE = new MetricRegistry();
    
    private final List<Metric> metrics = new ArrayList<>();

    private MetricRegistry() {
        
    }
    
    public static MetricRegistry getInstance() {
        return INSTANCE;
    }
    
    public void register(Metric metric) {
        this.metrics.add(metric);
    }

    CompletableFuture<Void> collectMetrics() {
        /* Combine all Completable futures into a single one */
        return CompletableFuture.allOf(this.metrics.stream()
                .map(Metric::collect)
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new));
    }

}
