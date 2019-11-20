package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class Entities extends WorldMetric {

    private Gauge entities = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world")
            .register();

    public Entities(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void collect(World world) {
        entities.labels(world.getName()).set(world.getEntities().size());
    }
}
