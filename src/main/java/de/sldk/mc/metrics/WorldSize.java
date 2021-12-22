package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import java.io.File;
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
        File worldFolder = world.getWorldFolder();
        long size = getFolderSize(worldFolder);
        WORLD_SIZE.labels(world.getName()).set(size);
    }

    private long getFolderSize(File folder) {
        long size = 0;
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            } else {
                size += getFolderSize(file);
            }
        }
        return size;
    }
}
