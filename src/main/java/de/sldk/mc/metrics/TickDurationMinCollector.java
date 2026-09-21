package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationMinCollector extends Metric {
    private static final String NAME = "tick_duration_min";
    private final TickDurationCollector collector = TickDurationCollector.forServerImplementation(this.getPlugin());

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Min duration of server tick (nanoseconds)")
            .create();

    public TickDurationMinCollector(Plugin plugin) {
        super(plugin, TD);
    }

    double getTickDurationMin() {
        long[] durations = collector.getTickDurations();
        if (durations == null || durations.length == 0) {
            return Double.NaN;
        }
        long min = Long.MAX_VALUE;
        for (long val : durations) {
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

