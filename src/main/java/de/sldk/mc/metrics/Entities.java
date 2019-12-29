package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class Entities extends WorldMetric {

    private static final Gauge ENTITIES = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world")
            .create();

    public Entities(Plugin plugin) {
        super(plugin, ENTITIES);
    }

    @Override
    public void collect(World world) {
        ENTITIES.labels(world.getName()).set(world.getEntities().size());
    }
}
