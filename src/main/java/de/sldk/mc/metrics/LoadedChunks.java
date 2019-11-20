package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class LoadedChunks extends WorldMetric {

    private Gauge loadedChunks = Gauge.build()
            .name(prefix("loaded_chunks_total"))
            .help("Chunks loaded per world")
            .labelNames("world")
            .register();

    public LoadedChunks(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void collect(World world) {
        loadedChunks.labels(world.getName()).set(world.getLoadedChunks().length);
    }
}
