package de.sldk.mc.metrics;

import de.sldk.mc.metrics.folia.FoliaTickStatistics;
import de.sldk.mc.metrics.tick_duration.TickDurationCollector;
import de.sldk.mc.utils.FoliaUtils;
import io.prometheus.client.Gauge;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;

public class TickDurationMedianCollector extends Metric {
    private static final String NAME = "tick_duration_median";
    private final TickDurationCollector collector;
    private final FoliaTickStatistics foliaTickStatistics;

    private static final Gauge TD = Gauge.build()
            .name(prefix(NAME))
            .help("Median duration of server tick (nanoseconds)")
            .create();

    public TickDurationMedianCollector(Plugin plugin) {
        super(plugin, TD);
        this.collector = FoliaUtils.isFolia() ? null : TickDurationCollector.forServerImplementation(plugin);
        this.foliaTickStatistics = FoliaUtils.isFolia() ? new FoliaTickStatistics() : null;
    }

    private long getTickDurationMedian() {
        if (FoliaUtils.isFolia() && foliaTickStatistics != null) {
            return foliaTickStatistics.getTickDurationMedian();
        }
        long[] tickTimes = collector.getTickDurations();
        Arrays.sort(tickTimes);
        return tickTimes[tickTimes.length / 2];
    }

    @Override
    public void doCollect() {
        TD.set(getTickDurationMedian());
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
