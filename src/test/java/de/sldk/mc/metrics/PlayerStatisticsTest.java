package de.sldk.mc.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.prometheus.client.CollectorRegistry;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
class PlayerStatisticsTest {

	private static final String ENTITY_METRIC_NAME = "mc_player_statistic";
	private static final String[] METRIC_LABELS = new String[] {"player_name", "player_uid", "statistic"};

	@Mock
	private Plugin plugin;

	@Mock
	private Server server;

	private PlayerStatistics playerStatistics;

	@BeforeEach
	void setup() {
		CollectorRegistry.defaultRegistry.clear();

		mockBukkitDataDirectory();

		playerStatistics = new PlayerStatistics(plugin);
		playerStatistics.enable();
	}

	void mockBukkitDataDirectory() {
		when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
		when(plugin.getServer()).thenReturn(server);
		when(server.getWorldContainer()).thenReturn(
				Paths.get("src", "test", "resources", "minecraft", "data").toFile().getAbsoluteFile());
	}

	// TODO make this good
	@Test
	void x() {
		final String playerName = "unique_player_name";
		final UUID playerUuid = UUID.fromString("eab53112-b93f-11ea-b3de-0242ac130004");

		OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
		when(offlinePlayer.getName()).thenReturn(playerName);
		when(offlinePlayer.getUniqueId()).thenReturn(playerUuid);

		assertThat(offlinePlayer.getPlayer()).describedAs(
				"Player is required to be null for data to be fetched from files").isNull();

		playerStatistics.collect(offlinePlayer);

		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString().toLowerCase(), "JUMP"})).isEqualTo(1);
		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString(), "PLAY_ONE_MINUTE"})).isEqualTo(676);
		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString(), "TIME_SINCE_REST"})).isEqualTo(676);
		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString(), "WALK_ONE_CM"})).isEqualTo(65);
		assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString(), "LEAVE_GAME"})).isEqualTo(1);
	}
}
