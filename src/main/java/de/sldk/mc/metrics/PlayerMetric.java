package de.sldk.mc.metrics;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public abstract class PlayerMetric extends Metric {

    @Override
    public final void collect() {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            collect(player);
        }
    }

    protected abstract void collect(OfflinePlayer player);

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }
}
