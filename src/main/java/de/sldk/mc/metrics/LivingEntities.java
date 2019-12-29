package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class LivingEntities extends WorldMetric {

    private static final Gauge LIVING_ENTITIES = Gauge.build()
            .name(prefix("living_entities_total"))
            .help("Living entities loaded per world")
            .labelNames("world")
            .create();

    public LivingEntities(Plugin plugin) {
        super(plugin, LIVING_ENTITIES);
    }

    @Override
    protected void collect(World world) {
        LIVING_ENTITIES.labels(world.getName()).set(world.getLivingEntities().size());
    }
}
