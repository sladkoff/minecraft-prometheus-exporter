package de.sldk.mc.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.prometheus.client.CollectorRegistry;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class VillagersTest {

	private static final String VILLAGERS_METRIC_NAME = "mc_villagers_total";
	private static final String[] METRIC_LABELS = new String[] {"world", "type", "profession", "level"};

	private static final CollectorRegistry REGISTRY = CollectorRegistry.defaultRegistry;
	private Villagers villagersMetric;
	private World world;

	@BeforeEach
	void beforeEachTest() {
		REGISTRY.clear();
		Plugin plugin = mock(Plugin.class);
		villagersMetric = new Villagers(plugin);
		villagersMetric.enable();
		world = mock(World.class);
	}

	@Test
	void givenVillagersExpectCorrectCount() {
		final String worldName = "world_name";
		final long numOfDesertFarmersLevel1 = 2;
		final long numOfPlainsNoneLevel2 = 3;

		List<Villager> mockedVillagers = Stream.concat(
				mockVillagers(numOfDesertFarmersLevel1, Villager.Type.DESERT, Villager.Profession.FARMER, 1),
				mockVillagers(numOfPlainsNoneLevel2, Villager.Type.PLAINS, Villager.Profession.NONE, 2))
				.collect(Collectors.toList());

		when(world.getName()).thenReturn(worldName);
		when(world.getEntitiesByClass(Villager.class)).thenReturn(mockedVillagers);
		villagersMetric.collect(world);

		assertThat(REGISTRY.getSampleValue(VILLAGERS_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "desert", "farmer", "1"})).isEqualTo(numOfDesertFarmersLevel1);

		assertThat(REGISTRY.getSampleValue(VILLAGERS_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "plains", "none", "2"})).isEqualTo(numOfPlainsNoneLevel2);
	}

	private Stream<Villager> mockVillagers(long count, Villager.Type type, Villager.Profession profession, int level) {
		return LongStream.range(0, count).mapToObj(i -> mockVillager(type, profession, level));
	}

	private Villager mockVillager(Villager.Type type, Villager.Profession profession, int level) {
		Villager e = mock(Villager.class);
		when(e.getType()).thenReturn(EntityType.VILLAGER);
		when(e.getVillagerType()).thenReturn(type);
		when(e.getProfession()).thenReturn(profession);
		when(e.getVillagerLevel()).thenReturn(level);
		return e;
	}
}
