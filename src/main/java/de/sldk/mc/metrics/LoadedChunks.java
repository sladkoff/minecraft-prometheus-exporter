package de.sldk.mc.metrics;

import de.sldk.mc.collectors.LoadedChunksCollector;
import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class LoadedChunks extends WorldMetric {

    private static final Gauge LOADED_CHUNKS = Gauge.build()
            .name(prefix("loaded_chunks_total"))
            .help("Chunks loaded per world")
            .labelNames("world")
            .create();

    private final LoadedChunksCollector loadedChunksCollector = new LoadedChunksCollector();

    public LoadedChunks(Plugin plugin) {
        super(plugin, LOADED_CHUNKS);
    }

    @Override
    public void enable() {
        super.enable();
        getPlugin().getServer().getPluginManager().registerEvents(loadedChunksCollector, getPlugin());
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(loadedChunksCollector);
    }

    @Override
    protected void clear() {
		LOADED_CHUNKS.clear();
    }

    @Override
    public void collect(World world) {
        LOADED_CHUNKS.labels(world.getName()).set(loadedChunksCollector.getLoadedChunkTotal(world.getName()));
    }
}
