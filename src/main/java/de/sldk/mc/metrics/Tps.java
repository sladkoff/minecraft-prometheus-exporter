package de.sldk.mc.metrics;

import de.sldk.mc.tps.TpsCollector;
import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Tps extends Metric {

    private TpsCollector tpsCollector = new TpsCollector();

    private Gauge tps = Gauge.build()
            .name(prefix("mc_tps"))
            .help("Server TPS (ticks per second)")
            .register();

    public void register(Plugin plugin) {
        Bukkit.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(plugin, tpsCollector, 0, TpsCollector.POLL_INTERVAL);
    }

    @Override
    public void collect() {
        tps.set(tpsCollector.getAverageTPS());
    }
}
