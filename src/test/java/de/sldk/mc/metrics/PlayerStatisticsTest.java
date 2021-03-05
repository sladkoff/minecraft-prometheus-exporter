package de.sldk.mc.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.prometheus.client.CollectorRegistry;
import org.assertj.core.api.AbstractDoubleAssert;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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

	@Test
	void bukkit_api_statistics_should_be_loaded_if_available() {
		final String playerName = "unique_player_name";
		final UUID playerUuid = UUID.fromString("eab53112-b93f-11ea-b3de-0242ac130004");

		OfflinePlayer offlinePlayer = mockOfflinePlayer(playerName, playerUuid);
		Player onlinePlayer = mockOnlinePlayer(offlinePlayer);

		when(onlinePlayer.getStatistic(any(Statistic.class))).thenReturn(0);
		when(onlinePlayer.getStatistic(any(Statistic.class), any(Material.class))).thenReturn(0);
		when(onlinePlayer.getStatistic(any(Statistic.class), any(EntityType.class))).thenReturn(0);

		when(onlinePlayer.getStatistic(eq(Statistic.JUMP))).thenReturn(5);
		when(onlinePlayer.getStatistic(eq(Statistic.PLAY_ONE_MINUTE))).thenReturn(999);
		when(onlinePlayer.getStatistic(eq(Statistic.TIME_SINCE_REST))).thenReturn(999);
		when(onlinePlayer.getStatistic(eq(Statistic.WALK_ONE_CM))).thenReturn(72);
		when(onlinePlayer.getStatistic(eq(Statistic.LEAVE_GAME))).thenReturn(3);

		playerStatistics.collect(offlinePlayer);

		assertPlayerStatistic(playerName, playerUuid, "JUMP").isEqualTo(5);
		assertPlayerStatistic(playerName, playerUuid, "PLAY_ONE_MINUTE").isEqualTo(999);
		assertPlayerStatistic(playerName, playerUuid, "TIME_SINCE_REST").isEqualTo(999);
		assertPlayerStatistic(playerName, playerUuid, "WALK_ONE_CM").isEqualTo(72);
		assertPlayerStatistic(playerName, playerUuid, "LEAVE_GAME").isEqualTo(3);
	}

	@Test
	void file_statistics_should_be_used_as_fallback() {

		final String playerName = "unique_player_name";
		final UUID playerUuid = UUID.fromString("eab53112-b93f-11ea-b3de-0242ac130004");

		OfflinePlayer offlinePlayer = mockOfflinePlayer(playerName, playerUuid);

		assertThat(offlinePlayer.getPlayer()).describedAs(
				"Player is required to be null for data to be fetched from files").isNull();

		playerStatistics.collect(offlinePlayer);

		assertPlayerStatistic(playerName, playerUuid, "JUMP").isEqualTo(1);
		assertPlayerStatistic(playerName, playerUuid, "PLAY_ONE_MINUTE").isEqualTo(676);
		assertPlayerStatistic(playerName, playerUuid, "TIME_SINCE_REST").isEqualTo(676);
		assertPlayerStatistic(playerName, playerUuid, "WALK_ONE_CM").isEqualTo(65);
		assertPlayerStatistic(playerName, playerUuid, "LEAVE_GAME").isEqualTo(1);
	}

	AbstractDoubleAssert<?> assertPlayerStatistic(String playerName, UUID playerUuid, String statisticName) {
		return assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
				new String[] {playerName, playerUuid.toString().toLowerCase(), statisticName}));
	}

	private Player mockOnlinePlayer(OfflinePlayer offlinePlayer) {

		Player onlinePlayer = mock(Player.class);
		when(offlinePlayer.getPlayer()).thenReturn(onlinePlayer);

		return onlinePlayer;
	}

	private OfflinePlayer mockOfflinePlayer(String name, UUID uuid) {

		OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
		when(offlinePlayer.getName()).thenReturn(name);
		when(offlinePlayer.getUniqueId()).thenReturn(uuid);

		return offlinePlayer;
	}

	void mockBukkitDataDirectory() {
		when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
		when(plugin.getServer()).thenReturn(server);
		when(server.getWorldContainer()).thenReturn(
				Paths.get("src", "test", "resources", "minecraft", "data").toFile().getAbsoluteFile());
	}
}
