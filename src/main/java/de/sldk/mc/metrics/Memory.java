package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Memory extends Metric {

    private Gauge memory = Gauge.build()
            .name(prefix("jvm_memory"))
            .help("JVM memory usage")
            .labelNames("type")
            .register();

    @Override
    public void collect() {
        memory.labels("max").set(Runtime.getRuntime().maxMemory());
        memory.labels("free").set(Runtime.getRuntime().freeMemory());
    }
}
