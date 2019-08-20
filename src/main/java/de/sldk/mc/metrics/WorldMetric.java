package de.sldk.mc.metrics;

import org.bukkit.Bukkit;
import org.bukkit.World;

public abstract class WorldMetric extends Metric {

    @Override
    public final void collect() {
        for (World world : Bukkit.getWorlds()) {
            collect(world);
        }
    }

    protected abstract void collect(World world);

}
