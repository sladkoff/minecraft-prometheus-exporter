package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class Memory extends Metric {

    private Gauge memory = Gauge.build()
            .name(prefix("jvm_memory"))
            .help("JVM memory usage")
            .labelNames("type")
            .register();

    public Memory(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void doCollect() {
        memory.labels("max").set(Runtime.getRuntime().maxMemory());
        memory.labels("free").set(Runtime.getRuntime().freeMemory());
    }
}
