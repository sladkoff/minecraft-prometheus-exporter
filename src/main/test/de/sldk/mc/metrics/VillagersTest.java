package de.sldk.mc.metrics;

import io.prometheus.client.CollectorRegistry;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VillagersTest {

    private static final String VILLAGERS_METRIC_NAME = "mc_villagers_total";
    private static final String[] METRIC_LABELS = new String[]{"world", "type", "profession", "level"};

    private Villagers villagersMetric;

    @BeforeAll
    static void beforeAllTests() {
        CollectorRegistry.defaultRegistry.clear();
    }

    @BeforeEach
    void beforeEachTest() {
        villagersMetric = new Villagers(mock(Plugin.class));
        villagersMetric.enable();
    }

    @AfterEach
    void afterEachTest() {
        CollectorRegistry.defaultRegistry.clear();
    }

    @Test
    void givenVillagersExpectCorrectCount() {
        final String worldName = "world_name";
        final long numOfDesertFarmersLevel1 = 2;
        final long numOfPlainsNoneLevel2 = 3;

        List<Entity> mockedVillagers = new ArrayList<>();
        mockedVillagers.addAll(mockVillagers(numOfDesertFarmersLevel1, Villager.Type.DESERT, Villager.Profession.FARMER, 1));
        mockedVillagers.addAll(mockVillagers(numOfPlainsNoneLevel2, Villager.Type.PLAINS, Villager.Profession.NONE, 2));
        Collections.shuffle(mockedVillagers);

        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);
        when(world.getEntities()).thenReturn(mockedVillagers);

        villagersMetric.collect(world);

        assertEquals(numOfDesertFarmersLevel1,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(VILLAGERS_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "desert", "farmer", "1"}));

        assertEquals(numOfPlainsNoneLevel2,
                CollectorRegistry.defaultRegistry
                        .getSampleValue(VILLAGERS_METRIC_NAME,
                                METRIC_LABELS,
                                new String[]{worldName, "plains", "none", "2"}));
    }

    private List<Entity> mockVillagers(long count, Villager.Type type, Villager.Profession profession, int level) {
        return LongStream.range(0, count)
                .mapToObj(i -> mockVillager(type, profession, level))
                .collect(Collectors.toList());
    }

    private Entity mockVillager(Villager.Type type, Villager.Profession profession, int level) {
        Villager e = mock(Villager.class);
        when(e.getType()).thenReturn(EntityType.VILLAGER);
        when(e.getVillagerType()).thenReturn(type);
        when(e.getProfession()).thenReturn(profession);
        when(e.getVillagerLevel()).thenReturn(level);
        return e;
    }
}
