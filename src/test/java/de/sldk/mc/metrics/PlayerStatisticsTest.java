package de.sldk.mc.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.sldk.mc.metrics.statistics.PlayerStatistics;
import io.prometheus.client.CollectorRegistry;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerStatisticsTest {

    private static final String ENTITY_METRIC_NAME = "mc_player_statistic";
    private static final String[] METRIC_LABELS =
            new String[]{"player_name", "player_uid", "statistic"};

    private PlayerStatistics playerStatistics;

    @BeforeAll
    static void beforeAllTests() {
        CollectorRegistry.defaultRegistry.clear();
    }

    @BeforeEach
    void beforeEachTest() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());

        File f = Paths
                .get("src", "test", "resources", "minecraft", "data", "plugins", "PrometheusExporter")
                .toFile().getAbsoluteFile();
        when(plugin.getDataFolder()).thenReturn(f);

        Server server = mock(Server.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getWorldContainer()).thenReturn(
                Paths.get("src", "test", "resources", "minecraft", "data")
                        .toFile().getAbsoluteFile()
        );

        playerStatistics = new PlayerStatistics(plugin);
        playerStatistics.enable();

    }

    @AfterEach
    void afterEachTest() {
        CollectorRegistry.defaultRegistry.clear();
    }

    // TODO make this good
    @Test
    void x() {
        final String worldName = "world_name";
        final String playerName = "unique_player_name";
        final UUID playerUuid = UUID.fromString("eab53112-b93f-11ea-b3de-0242ac130004");
        World world = mock(World.class);
        when(world.getName()).thenReturn(worldName);

        OfflinePlayer offlinePlayer = mock(OfflinePlayer.class);
        when(offlinePlayer.getName()).thenReturn(playerName);
        when(offlinePlayer.getUniqueId()).thenReturn(playerUuid);

        assertNull(offlinePlayer.getPlayer(),
                "Player is required to be null for data to be fetched from files");

        playerStatistics.collect(offlinePlayer);

        assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
                new String[]{playerName, playerUuid.toString().toLowerCase(), "JUMP"})).isEqualTo(1);
        assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
                new String[]{playerName, playerUuid.toString(), "PLAY_ONE_MINUTE"})).isEqualTo(676);
        assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
                new String[]{playerName, playerUuid.toString(), "TIME_SINCE_REST"})).isEqualTo(676);
        assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
                new String[]{playerName, playerUuid.toString(), "WALK_ONE_CM"})).isEqualTo(65);
        assertThat(CollectorRegistry.defaultRegistry.getSampleValue(ENTITY_METRIC_NAME, METRIC_LABELS,
                new String[]{playerName, playerUuid.toString(), "LEAVE_GAME"})).isEqualTo(1);
    }
}
