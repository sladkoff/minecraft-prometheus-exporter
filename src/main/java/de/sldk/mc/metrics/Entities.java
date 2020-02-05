package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.stream.Collectors;

public class Entities extends WorldMetric {

    private static final Gauge ENTITIES = Gauge.build()
            .name(prefix("entities_total"))
            .help("Entities loaded per world")
            .labelNames("world", "type", "alive")
            .create();

    public Entities(Plugin plugin) {
        super(plugin, ENTITIES);
    }

    @Override
    public void collect(World world) {
        Map<EntityType, Long> mapEntityTypesToCounts = world.getEntities().stream()
                .collect(Collectors.groupingBy(Entity::getType, Collectors.counting()));

        mapEntityTypesToCounts.keySet()
                .forEach(entityType ->
                        ENTITIES
                                .labels(world.getName(),
                                        entityType.name(),
                                        Boolean.toString(entityType.isAlive()))
                                .set(mapEntityTypesToCounts.get(entityType))
                );
    }
}
