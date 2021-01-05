package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class LoadedChunks extends WorldMetric {

    private static final Gauge LOADED_CHUNKS = Gauge.build()
            .name(prefix("loaded_chunks_total"))
            .help("Chunks loaded per world")
            .labelNames("world")
            .create();

    public LoadedChunks(Plugin plugin) {
        super(plugin, LOADED_CHUNKS);
    }

    @Override
    protected void clear() {
    }

    @Override
    public void collect(World world) {
        LOADED_CHUNKS.labels(world.getName()).set(world.getLoadedChunks().length);
    }
}
