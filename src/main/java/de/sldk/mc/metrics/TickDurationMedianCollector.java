package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tickDuration.TickDurationCollector;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class TickDurationMedianCollector extends Metric {
    private static final String NAME = "tick_duration_median";
    private final TickDurationCollector collector = TickDurationCollector.forServerImplementation(this.getPlugin());

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Median duration of server tick (nanoseconds)")
            .create();

    public TickDurationMedianCollector(Plugin plugin) {
        super(plugin, TD);
    }

    private long getTickDurationMedian() {
        /* Copy the original array - don't want to sort it! */
        long[] tickTimes = collector.getTickDurations().clone();
        Arrays.sort(tickTimes);
        return tickTimes[tickTimes.length / 2];
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationMedian());
    }
}
