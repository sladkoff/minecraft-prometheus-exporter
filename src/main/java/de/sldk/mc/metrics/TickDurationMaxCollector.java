package de.sldk.mc.metrics;

import de.sldk.mc.metrics.folia.FoliaTickStatistics;
import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import de.sldk.mc.utils.FoliaUtils;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationMaxCollector extends Metric {
    private static final String NAME = "tick_duration_max";
    private final TickDurationCollector collector;
    private final FoliaTickStatistics foliaTickStatistics;

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Max duration of server tick (nanoseconds)")
            .create();

    public TickDurationMaxCollector(Plugin plugin) {
        super(plugin, TD);
        this.collector = FoliaUtils.isFolia() ? null : TickDurationCollector.forServerImplementation(plugin);
        this.foliaTickStatistics = FoliaUtils.isFolia() ? new FoliaTickStatistics() : null;
    }

    private long getTickDurationMax() {
        if (FoliaUtils.isFolia() && foliaTickStatistics != null) {
            return foliaTickStatistics.getTickDurationMax();
        }
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

    @Override
    public boolean isFoliaCapable() {
        return true;
    }

    @Override
    public boolean isAsyncCapable() {
        return FoliaUtils.isFolia();
    }
}
