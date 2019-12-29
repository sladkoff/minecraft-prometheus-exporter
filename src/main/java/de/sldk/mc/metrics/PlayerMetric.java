package de.sldk.mc.metrics;

import io.prometheus.client.Collector;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

public abstract class PlayerMetric extends Metric {

    public PlayerMetric(Plugin plugin, Collector collector) {
        super(plugin, collector);
    }

    @Override
    public final void doCollect() {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            collect(player);
        }
    }

    protected abstract void collect(OfflinePlayer player);

}
