package de.sldk.mc.metrics;

import io.prometheus.client.CollectorRegistry;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntitiesTest {

    private static final String ENTITY_METRIC_NAME = "mc_entities_total";
    private static final String[] METRIC_LABELS = new String[]{"world", "type", "alive"};

    @Test
    void givenTypedEntitiesExpectCorrectCount() {
        final String worldName = "world_name";
        final long numOfPigs = 1;
        final long numOfHorses = 6;
        final long numOfOrbs = 800;
        final long numOfChicken = 9000;
        final long numOfMinecarts = 10000;
        List<Entity> mockedEntities = new ArrayList<>();
        mockedEntities.addAll(mockEntities(numOfPigs, EntityType.PIG));
        mockedEntities.addAll(mockEntities(numOfHorses, EntityType.HORSE));
        mockedEntities.addAll(mockEntities(numOfOrbs, EntityType.EXPERIENCE_ORB));
        mockedEntities.addAll(mockEntities(numOfChicken, EntityType.CHICKEN));
        mockedEntities.addAll(mockEntities(numOfMinecarts, EntityType.MINECART));
        Collections.shuffle(mockedEntities);

        Entities entitiesMetric = new Entities(mock(Plugin.class));
        entitiesMetric.enable();

        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);
        when(world.getEntities()).thenReturn(mockedEntities);

        entitiesMetric.collect(world);

        assertEquals(numOfPigs,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "PIG", "true"}));

        assertEquals(numOfHorses,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "HORSE", "true"}));

        assertEquals(numOfOrbs,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "EXPERIENCE_ORB", "false"}));

        assertEquals(numOfChicken,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "CHICKEN", "true"}));

        assertEquals(numOfMinecarts,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "MINECART", "false"}));
    }

    @Test
    void expectArmorStandAliveToBeFalse() {
        final String worldName = "world_name";
        final long numOfArmorStands = 11;
        List<Entity> mockedEntities = new ArrayList<>(mockEntities(numOfArmorStands, EntityType.ARMOR_STAND));
        Collections.shuffle(mockedEntities);

        Entities entitiesMetric = new Entities(mock(Plugin.class));
        entitiesMetric.enable();

        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);
        when(world.getEntities()).thenReturn(mockedEntities);

        entitiesMetric.collect(world);

        assertEquals(numOfArmorStands,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(ENTITY_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "ARMOR_STAND", "false"}));
    }

    private List<Entity> mockEntities(long count, EntityType type) {
        return LongStream.range(0, count).mapToObj(i -> mockEntity(type)).collect(Collectors.toList());
    }

    private Entity mockEntity(EntityType type) {
        Entity e = mock(Entity.class);
        when(e.getType()).thenReturn(type);
        return e;
    }
}
