package de.sldk.mc.metrics;

import de.sldk.mc.utils.PathFileSize;
import io.prometheus.client.Gauge;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldSize extends WorldMetric {

    private final Logger log;
    private static final Gauge WORLD_SIZE = Gauge.build()
            .name(prefix("world_size"))
            .help("World size in bytes")
            .labelNames("world")
            .create();

    public WorldSize(Plugin plugin) {
        super(plugin, WORLD_SIZE);
        this.log = plugin.getLogger();
    }

    @Override
    protected void clear() {
        WORLD_SIZE.clear();
    }

    @Override
    public void collect(World world) {
        CompletableFuture.supplyAsync(() -> {
            try {
                PathFileSize pathUtils = new PathFileSize(world.getWorldFolder().toPath());
                return pathUtils.getSize();
            } catch (Throwable t) {
                log.throwing(this.getClass().getSimpleName(), "collect", t);
            }
            return 0L;
        }).thenAccept(size -> {
            String worldName = world.getName();
            WORLD_SIZE.labels(worldName).set(size);
        });
    }
}
