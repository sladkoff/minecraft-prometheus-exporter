package de.sldk.mc.metrics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.prometheus.client.CollectorRegistry;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import net.bytebuddy.utility.RandomString;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.*;

public class WorldSizeTest {

    private static final String METRIC_NAME = "mc_world_size";
    private static final String[] METRIC_LABELS = { "world" };

    private WorldSize worldSizeMetric;

    private Path path;
    private File file;
    private World world;

    @BeforeEach
    public void beforeEach() throws IOException{
        worldSizeMetric = new WorldSize(mock(Plugin.class));
        worldSizeMetric.enable();
    }

    @AfterEach
    public void afterEach() throws IOException {
        CollectorRegistry.defaultRegistry.clear();
        if (shouldDelete(path)) {
            FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            };
            Files.walkFileTree(path, visitor);
        }
    }

    @Test
    public void doesNotThrowIfFileDoesNotExist() {
        givenWorldFileDoesNotExist();
        assertDoesNotThrow(() -> worldSizeMetric.collect(world));
    }

    @Test
    public void doesNotPopulateMetricIfFileDoesNotExist() {
        givenWorldFileDoesNotExist();
        worldSizeMetric.collect(world);
        assertMetricIsEmpty();
    }

    @Test
    public void setsMetricWithCorrectNameAndLabel() throws IOException {
        String worldName = new RandomString(10).nextString();
        givenWorldFileExists(worldName);
        worldSizeMetric.collect(world);
        assertNotNull(getMetricValue(worldName));
    }

    @Test
    public void setsCorrectWorldSizeValue() throws IOException {
        String worldName = new RandomString(10).nextString();
        int worldSize = new Random().ints(128, 1024).findFirst().getAsInt();
        givenWorldFileExists(worldName, worldSize);
        worldSizeMetric.collect(world);
        Double value = getMetricValue(worldName);
        assertEquals(worldSize, value.longValue());
    }

    private Double getMetricValue(String worldName) {
        return CollectorRegistry.defaultRegistry.getSampleValue(
                METRIC_NAME,
                METRIC_LABELS,
                new String[] { worldName }
                );
    }

    private void assertMetricIsEmpty() {
        assertNull(CollectorRegistry.defaultRegistry.getSampleValue(METRIC_NAME));
    }

    private void givenWorldFileDoesNotExist() {
        String worldName = "some_file_that_surely_does_not_exist_"  + new RandomString(10).nextString();
        path = mock(Path.class);
        file = mock(File.class);
        world = mock(World.class);

        when(path.toFile()).thenReturn(file);
        when(file.toPath()).thenReturn(path);
        when(world.getWorldFolder()).thenReturn(file);
        when(world.getName()).thenReturn(worldName);
    }

    private void givenWorldFileExists(String worldName, int worldSize) throws IOException {
        givenWorldFileExists(worldName);
        String filename = new RandomString(10).nextString();
        File f = path.resolve(filename).toFile();
        try (FileWriter writer = new FileWriter(f)) {
            writer.write(new RandomString(worldSize).nextString());
        }
    }

    private void givenWorldFileExists(String worldName) throws IOException {
        path = Files.createTempDirectory("world");
        file = path.toFile();
        world = mock(World.class);

        when(world.getWorldFolder()).thenReturn(file);
        when(world.getName()).thenReturn(worldName);
    }

    private boolean shouldDelete(Path path) {
        return path != null
            && !mockingDetails(path).isMock()
            && !mockingDetails(path).isSpy();
    }
}
