package de.sldk.mc.metrics;

import de.sldk.mc.metrics.folia.FoliaTickStatistics;
import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import de.sldk.mc.utils.FoliaUtils;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

public class TickDurationAverageCollector extends Metric {
    private static final String NAME = "tick_duration_average";
    private final TickDurationCollector collector;
    private final FoliaTickStatistics foliaTickStatistics;

    private static final Gauge TD = Gauge.build()
            .name(Metric.prefix(NAME))
            .help("Average duration of server tick (nanoseconds)")
            .create();

    public TickDurationAverageCollector(Plugin plugin) {
        super(plugin, TD);
        this.collector = FoliaUtils.isFolia() ? null : TickDurationCollector.forServerImplementation(plugin);
        this.foliaTickStatistics = FoliaUtils.isFolia() ? new FoliaTickStatistics() : null;
    }

    private long getTickDurationAverage() {
        if (FoliaUtils.isFolia() && foliaTickStatistics != null) {
            return foliaTickStatistics.getTickDurationAverage();
        }
        long sum = 0;
        long[] durations = collector.getTickDurations();
        for (Long val : durations) {
            sum += val;
        }
        return sum / durations.length;
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationAverage());
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
