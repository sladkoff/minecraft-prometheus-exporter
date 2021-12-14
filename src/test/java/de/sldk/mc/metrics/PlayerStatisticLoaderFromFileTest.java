package de.sldk.mc.metrics;

import de.sldk.mc.metrics.player.PlayerStatisticLoaderFromFile;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerStatisticLoaderFromFileTest {
    private static Plugin plugin;
    private static PlayerStatisticLoaderFromFile playerStatisticLoader;

    @BeforeAll
    static void beforeAllTests() {
        plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getAnonymousLogger());
        playerStatisticLoader = new PlayerStatisticLoaderFromFile(plugin);
    }

    @Test
    void use_world_as_fallback_world() {
        assertThat(playerStatisticLoader.getDefaultWorld(
                Paths.get("src", "test", "resources", "server", "invalid.properties").toFile()
                        .getAbsolutePath())).isEqualTo(
                playerStatisticLoader.DEFAULT_WORLD);
    }

    @Test
    void get_default_world() {
        assertThat(playerStatisticLoader.getDefaultWorld(playerStatisticLoader.SERVER_PROPERTIES)).isEqualTo(
                playerStatisticLoader.DEFAULT_WORLD);
    }

    @Test
    void get_custom_world() {
        assertThat(playerStatisticLoader.getDefaultWorld(
                Paths.get("src", "test", "resources", "server", "custom.properties").toFile()
                        .getAbsolutePath())).isEqualTo("custom_world");
    }
}
