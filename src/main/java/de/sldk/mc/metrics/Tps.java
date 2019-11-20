package de.sldk.mc.metrics;

import de.sldk.mc.tps.TpsCollector;
import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Tps extends Metric {

    private int taskId;

    private TpsCollector tpsCollector = new TpsCollector();

    private Gauge tps = Gauge.build()
            .name(prefix("tps"))
            .help("Server TPS (ticks per second)")
            .register();

    public Tps(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void enable() {
        super.enable();
        this.taskId = startTask(getPlugin());
    }

    @Override
    public void disable() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    private int startTask(Plugin plugin) {
        return Bukkit.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(plugin, tpsCollector, 0, TpsCollector.POLL_INTERVAL);
    }

    @Override
    public void doCollect() {
        tps.set(tpsCollector.getAverageTPS());
    }
}
