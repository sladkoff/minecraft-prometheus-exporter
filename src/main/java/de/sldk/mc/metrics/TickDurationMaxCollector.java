package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tickDuration.TickDurationCollector;
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

    private long getTickDurationMax() {
        long max = Long.MIN_VALUE;
        for (Long val : collector.getTickDurations()) {
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

