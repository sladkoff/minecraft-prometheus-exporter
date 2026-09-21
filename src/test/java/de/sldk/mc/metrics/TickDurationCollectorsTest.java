package de.sldk.mc.metrics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * A server that has not ticked yet reports an empty sample of tick times. The collectors used to index into that
 * empty array, which made the whole scrape fail with an ArrayIndexOutOfBoundsException (median) and an
 * ArithmeticException (average), while min and max silently reported Long.MAX_VALUE and Long.MIN_VALUE.
 */
class TickDurationCollectorsTest {

    private MockedStatic<Bukkit> bukkit;
    private Server server;
    private Plugin plugin;

    @BeforeEach
    void setUp() {
        bukkit = mockStatic(Bukkit.class);
        server = mock(Server.class);
        bukkit.when(Bukkit::getServer).thenReturn(server);
        plugin = mock(Plugin.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("TickDurationCollectorsTest"));
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    void reportsNoValueWhileTheServerHasNotTickedYet() {
        when(server.getTickTimes()).thenReturn(new long[0]);

        assertTrue(Double.isNaN(new TickDurationMedianCollector(plugin).getTickDurationMedian()));
        assertTrue(Double.isNaN(new TickDurationAverageCollector(plugin).getTickDurationAverage()));
        assertTrue(Double.isNaN(new TickDurationMinCollector(plugin).getTickDurationMin()));
        assertTrue(Double.isNaN(new TickDurationMaxCollector(plugin).getTickDurationMax()));
    }

    @Test
    void collectingDoesNotThrowWhileTheServerHasNotTickedYet() {
        when(server.getTickTimes()).thenReturn(new long[0]);

        assertDoesNotThrow(() -> new TickDurationMedianCollector(plugin).doCollect());
        assertDoesNotThrow(() -> new TickDurationAverageCollector(plugin).doCollect());
        assertDoesNotThrow(() -> new TickDurationMinCollector(plugin).doCollect());
        assertDoesNotThrow(() -> new TickDurationMaxCollector(plugin).doCollect());
    }

    @Test
    void reportsTheSampleOnceTheServerHasTicked() {
        when(server.getTickTimes()).thenReturn(new long[] {30L, 10L, 50L, 20L, 40L});

        assertEquals(30d, new TickDurationMedianCollector(plugin).getTickDurationMedian());
        assertEquals(30d, new TickDurationAverageCollector(plugin).getTickDurationAverage());
        assertEquals(10d, new TickDurationMinCollector(plugin).getTickDurationMin());
        assertEquals(50d, new TickDurationMaxCollector(plugin).getTickDurationMax());
    }
}
