package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationAverageCollector extends Metric {
    private static final String NAME = "tick_duration_average";
    private final TickDurationCollector collector = TickDurationCollector.forServerImplementation(this.getPlugin());

    private static final Gauge TD = Gauge.build()
            .name(Metric.prefix(NAME))
            .help("Average duration of server tick (nanoseconds)")
            .create();

    public TickDurationAverageCollector(Plugin plugin) {
        super(plugin, TD);
    }

    double getTickDurationAverage() {
        long[] durations = collector.getTickDurations();
        if (durations == null || durations.length == 0) {
            return Double.NaN;
        }
        long sum = 0;
        for (long val : durations) {
            sum += val;
        }
        return (double) sum / durations.length;
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationAverage());
    }
}
