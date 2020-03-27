package de.sldk.mc.metrics;

import de.sldk.mc.tps.TpsCollector;
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

    public Tps(Plugin plugin) {
        super(plugin, TPS);
    }

    @Override
    public void enable() {
        super.enable();
        this.taskId = startTask(getPlugin());
    }

    @Override
    public void disable() {
        super.disable();
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private int startTask(Plugin plugin) {
        return Bukkit.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(plugin, tpsCollector, 0, TpsCollector.POLL_INTERVAL);
    }

    @Override
    public void doCollect() {
        TPS.set(tpsCollector.getAverageTPS());
    }
}
