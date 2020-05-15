package de.sldk.mc.metrics;

import org.bukkit.plugin.Plugin;

import io.prometheus.client.Gauge;

public class TickDurationMinCollector extends TickDurationCollector {
    private static final String NAME = "tick_duration_min";

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Min duration of server tick (nanoseconds)")
            .create();

    public TickDurationMinCollector(Plugin plugin) {
        super(plugin, TD, NAME);
    }

    private long getTickDurationMin() {
        long min = Long.MAX_VALUE;
        for (Long val : getTickDurations()) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationMin());
    }
}

