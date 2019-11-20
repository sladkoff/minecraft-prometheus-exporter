package de.sldk.mc.metrics;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public abstract class WorldMetric extends Metric {

    public WorldMetric(Plugin plugin) {
        super(plugin);
    }

    @Override
    public final void doCollect() {
        for (World world : Bukkit.getWorlds()) {
            collect(world);
        }
    }

    protected abstract void collect(World world);

}
