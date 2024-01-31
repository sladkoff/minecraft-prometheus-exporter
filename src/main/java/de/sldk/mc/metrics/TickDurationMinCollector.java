package de.sldk.mc.metrics;

import de.sldk.mc.metrics.tickDuration.ITickDurationCollector;
import de.sldk.mc.metrics.tickDuration.TickDurationCollector;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationMinCollector extends Metric {
    private static final String NAME = "tick_duration_min";
    private final ITickDurationCollector collector = new TickDurationCollector(this.getPlugin());

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Min duration of server tick (nanoseconds)")
            .create();

    public TickDurationMinCollector(Plugin plugin) {
        super(plugin, TD);
    }

    private long getTickDurationMin() {
        long min = Long.MAX_VALUE;
        for (Long val : collector.getTickDurations()) {
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

