package de.sldk.mc.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.prometheus.client.CollectorRegistry;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

class EntitiesTest {

	private static final String ENTITY_METRIC_NAME = "mc_entities_total";
	private static final String[] METRIC_LABELS = new String[] {"world", "type", "alive", "spawnable"};

	private Entities entitiesMetric;

	@BeforeAll
	static void beforeAllTests() {
		CollectorRegistry.defaultRegistry.clear();
	}

	@BeforeEach
	void beforeEachTest() {
		entitiesMetric = new Entities(mock(Plugin.class));
		entitiesMetric.enable();
	}

	@AfterEach
	void afterEachTest() {
		CollectorRegistry.defaultRegistry.clear();
	}

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

		World world = mock(World.class);
		when(world.getName()).thenReturn(worldName);
		when(world.getEntities()).thenReturn(mockedEntities);

		entitiesMetric.collect(world);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "pig", "true", "true"})).isEqualTo(numOfPigs);


		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "horse", "true", "true"})).isEqualTo(numOfHorses);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "experience_orb", "false", "true"})).isEqualTo(numOfOrbs);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "chicken", "true", "true"})).isEqualTo(numOfChicken);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "minecart", "false", "true"})).isEqualTo(numOfMinecarts);
	}

	@Test
	void expectArmorStandAliveToBeFalse() {
		final String worldName = "world_name";
		final long numOfArmorStands = 11;
		List<Entity> mockedEntities = new ArrayList<>(mockEntities(numOfArmorStands, EntityType.ARMOR_STAND));

		World world = mock(World.class);
		when(world.getName()).thenReturn(worldName);
		when(world.getEntities()).thenReturn(mockedEntities);

		entitiesMetric.collect(world);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "armor_stand", "false", "true"})).isEqualTo(numOfArmorStands);
	}

	@Test
	void givenUnknownTypeExpectNoError() {
		final String worldName = "world_name";
		final long numOfUnknowns = 33;
		List<Entity> mockedEntities = new ArrayList<>(mockEntities(numOfUnknowns, EntityType.UNKNOWN));

		World world = mock(World.class);
		when(world.getName()).thenReturn(worldName);
		when(world.getEntities()).thenReturn(mockedEntities);

		entitiesMetric.collect(world);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {worldName, "UNKNOWN", "false", "false"})).isEqualTo(numOfUnknowns);
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
