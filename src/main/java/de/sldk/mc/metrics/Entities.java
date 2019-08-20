package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;

public class Entities extends WorldMetric {

    private Gauge entities = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world")
            .register();

    @Override
    public void collect(World world) {
        entities.labels(world.getName()).set(world.getEntities().size());
    }
}
