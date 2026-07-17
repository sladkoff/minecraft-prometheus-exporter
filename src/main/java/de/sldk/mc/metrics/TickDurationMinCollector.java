package de.sldk.mc.metrics;

import de.sldk.mc.metrics.folia.FoliaTickStatistics;
import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import de.sldk.mc.utils.FoliaUtils;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationMinCollector extends Metric {
    private static final String NAME = "tick_duration_min";
    private final TickDurationCollector collector;
    private final FoliaTickStatistics foliaTickStatistics;

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Min duration of server tick (nanoseconds)")
            .create();

    public TickDurationMinCollector(Plugin plugin) {
        super(plugin, TD);
        this.collector = FoliaUtils.isFolia() ? null : TickDurationCollector.forServerImplementation(plugin);
        this.foliaTickStatistics = FoliaUtils.isFolia() ? new FoliaTickStatistics() : null;
    }

    private long getTickDurationMin() {
        if (FoliaUtils.isFolia() && foliaTickStatistics != null) {
            return foliaTickStatistics.getTickDurationMin();
        }
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

    @Override
    public boolean isFoliaCapable() {
        return true;
    }

    @Override
    public boolean isAsyncCapable() {
        return FoliaUtils.isFolia();
    }
}
