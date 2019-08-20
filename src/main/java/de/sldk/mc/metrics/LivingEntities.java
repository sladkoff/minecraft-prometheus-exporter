package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;

public class LivingEntities extends WorldMetric {

    private Gauge livingEntities = Gauge.build()
            .name(prefix("living_entities_total"))
            .help("Living entities loaded per world")
            .labelNames("world")
            .register();

    @Override
    protected void collect(World world) {
        livingEntities.labels(world.getName()).set(world.getLivingEntities().size());
    }
}
