package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class LoadedChunks extends WorldMetric {

    private Gauge loadedChunks = Gauge.build()
            .name(prefix("loaded_chunks_total"))
            .help("Chunks loaded per world")
            .labelNames("world")
            .register();

    @Override
    public void collect(World world) {
        loadedChunks.labels(world.getName()).set(world.getLoadedChunks().length);
    }
}
