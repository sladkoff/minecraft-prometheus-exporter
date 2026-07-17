package de.sldk.mc.metrics;

import de.sldk.mc.collectors.TpsCollector;
import de.sldk.mc.metrics.folia.FoliaTickStatistics;
import de.sldk.mc.utils.FoliaUtils;
import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Tps extends Metric {

    private static final Gauge TPS = Gauge.build()
            .name(prefix("tps"))
            .help("Server TPS (ticks per second)")
            .create();

    private int taskId;

    private TpsCollector tpsCollector = new TpsCollector();

    private final FoliaTickStatistics foliaTickStatistics;

    public Tps(Plugin plugin) {
        super(plugin, TPS);
        this.foliaTickStatistics = FoliaUtils.isFolia() ? new FoliaTickStatistics() : null;
    }

    @Override
    public void enable() {
        super.enable();
        if (!FoliaUtils.isFolia()) {
            this.taskId = startTask(getPlugin());
        }
    }

    @Override
    public void disable() {
        super.disable();
        if (!FoliaUtils.isFolia()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    private int startTask(Plugin plugin) {
        return Bukkit.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(plugin, tpsCollector, 0, TpsCollector.POLL_INTERVAL);
    }

    @Override
    public void doCollect() {
        if (FoliaUtils.isFolia() && foliaTickStatistics != null) {
            TPS.set(foliaTickStatistics.getTps());
        } else {
            TPS.set(tpsCollector.getAverageTPS());
        }
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
