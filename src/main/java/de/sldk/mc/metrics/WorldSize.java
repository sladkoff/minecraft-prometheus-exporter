package de.sldk.mc.metrics;

import de.sldk.mc.utils.PathUtils;
import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldSize extends WorldMetric {

    private static final Gauge WORLD_SIZE = Gauge.build()
            .name(prefix("world_size"))
            .help("World size in bytes")
            .labelNames("world")
            .create();

    public WorldSize(Plugin plugin) {
        super(plugin, WORLD_SIZE);
    }

    @Override
    protected void clear() {
        WORLD_SIZE.clear();
    }

    @Override
    public void collect(World world) {
        try {
            PathUtils pathUtils = new PathUtils(world.getWorldFolder().toPath());
            long size = pathUtils.getSize();
            String worldName = world.getName();
            WORLD_SIZE.labels(worldName).set(size);
        } catch (Throwable t) {
            // ignore
        }
    }
}
