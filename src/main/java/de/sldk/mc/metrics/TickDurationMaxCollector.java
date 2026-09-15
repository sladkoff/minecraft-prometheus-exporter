package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationMaxCollector extends Metric {
    private static final String NAME = "tick_duration_max";
    private final TickDurationCollector collector = TickDurationCollector.forServerImplementation(this.getPlugin());

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Max duration of server tick (nanoseconds)")
            .create();

    public TickDurationMaxCollector(Plugin plugin) {
        super(plugin, TD);
    }

    double getTickDurationMax() {
        long[] durations = collector.getTickDurations();
        if (durations == null || durations.length == 0) {
            return Double.NaN;
        }
        long max = Long.MIN_VALUE;
        for (long val : durations) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationMax());
    }
}

